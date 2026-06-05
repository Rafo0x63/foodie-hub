# SOLID refaktoriranje — OCP, LSP, ISP

Dokument popisuje stvarne SOLID prekršaje pronađene u `src/main/java` i refaktoriranje
kojim se ispravljaju. Pregledan je cijeli `main` izvorni kod (kontroleri, servisi,
sučelja, entiteti, security, scheduler).

Sažetak nalaza:

| Princip | Status | Lokacija |
|--------|--------|----------|
| **ISP** | prekršaj (2x) | `UserCommentController` → `CommentService`; `AdminController` → `UserService` |
| **OCP** | prekršaj (1x) | `NewRecipesReportJob` (izlaz izvještaja hardkodiran na logiranje) |
| **LSP** | nema prekršaja | nema domenskog nasljeđivanja koje krši ugovor (vidi zadnju sekciju) |

---

## ISP — Interface Segregation Principle

> Klijent ne smije biti prisiljen ovisiti o metodama sučelja koje ne koristi.

### Prekršaj 1: `UserCommentController` ovisi o cijelom `CommentService`

`CommentService` ima 7 metoda (recept-scoped CRUD + `getAllComments` + `getAllUserComments`).
`UserCommentController` koristi **samo jednu** — `getAllUserComments(userId)`:

```java
// UserCommentController.java
private final CommentService commentService;          // 7 metoda
...
return ResponseEntity.ok(commentService.getAllUserComments(userId));  // koristi 1
```

Kontroler je vezan na recept-scoped operacije (create/update/delete komentara) koje
ga se ne tiču. Promjena potpisa tih metoda nepotrebno utječe na ovaj kontroler.

### Prekršaj 2: `AdminController` ovisi o cijelom `UserService`

`UserService` ima 5 metoda (get/create/update/delete). `AdminController` koristi
**samo `getAllUsers()`**:

```java
// AdminController.java
private final UserService userService;   // 5 metoda
...
return userService.getAllUsers();        // koristi 1 (read-only)
```

Read-only klijent je prisiljen ovisiti o mutirajućim metodama.

### Rješenje: segregacija sučelja + kompozitno sučelje

Veliko sučelje se razbije na uska, kohezivna sučelja. Implementacija i dalje
implementira sve (preko kompozitnog sučelja koje nasljeđuje uska), a **svaki klijent
ovisi samo o uskom sučelju koje stvarno koristi**.

```java
public interface RecipeCommentService {   // recept-scoped CRUD
    List<CommentDTO> getAllRecipeComments(Long recipeId);
    CommentDTO getCommentById(Long recipeId, Long commentId);
    void deleteCommentById(Long recipeId, Long commentId);
    CommentDTO createNewComment(Long recipeId, CreateCommentRequest req);
    CommentDTO updateComment(Long recipeId, Long commentId, CreateCommentRequest req);
}

public interface UserCommentService {      // user-scoped čitanje
    List<CommentDTO> getAllUserComments(Long userId);
}

// kompozit — implementacija i postojeći širi klijenti ostaju netaknuti
public interface CommentService extends RecipeCommentService, UserCommentService {
    List<CommentDTO> getAllComments();
}
```

- `UserCommentController` → ovisi o `UserCommentService` (1 metoda).
- `CommentController` → ovisi o `RecipeCommentService` (recept-scoped CRUD).
- `CommentServiceImpl implements CommentService` — nepromijenjena, i dalje nudi sve.

Analogno za korisnike:

```java
public interface UserQueryService {        // čitanje
    List<UserDTO> getAllUsers();
    UserDTO getUserByID(Long id);
}
public interface UserCommandService {      // izmjene
    UserDTO createNewUser(CreateUserRequest req);
    UserDTO updateUser(Long id, CreateUserRequest req);
    void deleteUserById(Long id);
}
public interface UserService extends UserQueryService, UserCommandService {}
```

- `AdminController` → ovisi o `UserQueryService`.
- `UserController` → ovisi o `UserService` (koristi sve).

Kako kompozit nasljeđuje uska sučelja, postojeći mockovi (`CommentService`,
`UserService`) i dalje zadovoljavaju uže ovisnosti (podtip), pa testovi ostaju zeleni.

---

## OCP — Open/Closed Principle

> Modul treba biti otvoren za proširenje, a zatvoren za izmjenu.

### Prekršaj: izlaz izvještaja je hardkodiran u `NewRecipesReportJob`

Job sam formatira i logira izvještaj:

```java
// NewRecipesReportJob.java
NewRecipesReportDTO report = recipeReportService.generateNewRecipesReport(lookbackHours);
log.info("New-recipes report [{} - {}]: {} new recipe(s)...", ...);   // jedini izlaz
```

Dodavanje novog odredišta izvještaja (baza, e-mail, Slack) **zahtijeva izmjenu samog
joba** — što je upravo ono što OCP zabranjuje.

### Rješenje: apstrakcija `RecipeReportPublisher` (strategija)

```java
public interface RecipeReportPublisher {
    void publish(NewRecipesReportDTO report);
}

@Component
public class LoggingRecipeReportPublisher implements RecipeReportPublisher {
    public void publish(NewRecipesReportDTO report) { log.info("New-recipes report ...", ...); }
}
```

Job ovisi o **listi** publishera i ne zna za konkretne izlaze:

```java
private final List<RecipeReportPublisher> publishers;
...
publishers.forEach(p -> p.publish(report));
```

Novi izlaz = nova `@Component` klasa koja implementira `RecipeReportPublisher`.
**Job se ne dira.** Spring autowira sve implementacije u listu.

> Napomena: redak `New-recipes report [...]: N new recipe(s)` i dalje se ispisuje
> (sad iz `LoggingRecipeReportPublisher`), pa demo u 08:55 radi jednako.

---

## LSP — Liskov Substitution Principle

> Podtip mora biti zamjenjiv za svoj nadtip bez narušavanja ispravnosti programa.

**Nalaz: u trenutnom kodu nema LSP prekršaja.** Razlozi:

- Domenske klase (`Recipe`, `User`, `Comment`, …) **ne dijele zajedničku baznu
  klasu** i nemaju podtipove — nema hijerarhije u kojoj bi se ugovor mogao prekršiti.
- Jedina nasljeđivanja su okvirna i poštuju ugovore nadtipa:
  - `NotFoundException extends RuntimeException` — čista specijalizacija.
  - `JwtAuthenticationFilter extends OncePerRequestFilter` — ispravno implementira
    `doFilterInternal` po ugovoru.
- Nigdje nema `UnsupportedOperationException`, `instanceof`/`getClass()` grananja ni
  override-a koji sužava ugovor (jači preduvjet / slabiji postuvjet).

Tipičan LSP prekršaj izgledao bi ovako (NE postoji u ovom projektu):

```java
class ReadOnlyRecipeRepo extends RecipeRepo {
    @Override public Recipe save(Recipe r) { throw new UnsupportedOperationException(); }
}
```

Budući da prekršaja nema, ispravak nije potreban. Ako zadatak izričito traži
demonstraciju LSP-a (before/after), može se namjerno uvesti reprezentativan primjer
pa ispraviti — to je zaseban korak i traži se posebno odobrenje.
