# SOLID Audit - SRP and DIP Refactoring Candidates

This document records the initial SOLID audit for the refactoring task.
No refactoring has been applied yet. The purpose is to document the current
SRP and DIP violations before implementation.

## Project Overview

The backend is a Spring Boot application with a conventional layered structure:

- Controllers are in `hr.tvz.foodiehub.controllers`.
- Service interfaces are in `hr.tvz.foodiehub.services.interfaces`.
- Service implementations are in `hr.tvz.foodiehub.services.implementations`.
- Repositories are in `hr.tvz.foodiehub.repositories`.
- Security-related classes are in `hr.tvz.foodiehub.security`.
- Scheduled job logic is in `hr.tvz.foodiehub.scheduler.jobs`.

Main service implementations found:

- `AuthServiceImpl`
- `CommentServiceImpl`
- `IngredientServiceImpl`
- `RecipeServiceImpl`
- `StepServiceImpl`
- `UserServiceImpl`

Main controller classes found:

- `AdminController`
- `AuthController`
- `CommentController`
- `IngredientController`
- `RecipeController`
- `StepController`
- `UserCommentController`
- `UserController`

Repository classes found:

- `CommentRepository`
- `IngredientRepository`
- `RecipeRepository`
- `RecipeTagRepository`
- `RoleRepository`
- `StepRepository`
- `TagRepository`
- `UserRepository`

Existing tests include controller tests, service tests, repository tests,
security tests, validation tests, and Selenium E2E tests.

Production dependency injection observations:

- No production `@Autowired` field injection was found.
- `JwtService` uses `@Value` field injection for `jwt.secret`.
- Several services access static/global dependencies such as
  `SecurityContextHolder`.
- Some time-dependent code uses direct calls such as `LocalDateTime.now()`,
  `new Date()`, and `System.currentTimeMillis()`.

## Recommended SRP Violation

### SRP Candidate

`RecipeServiceImpl` mixes normal recipe application logic with scheduled
maintenance purge logic.

### SRP Location

File:

```text
src/main/java/hr/tvz/foodiehub/services/implementations/RecipeServiceImpl.java
```

Related interface:

```text
src/main/java/hr/tvz/foodiehub/services/interfaces/RecipeService.java
```

Scheduled job that currently depends on this responsibility:

```text
src/main/java/hr/tvz/foodiehub/scheduler/jobs/PurgeSoftDeletedRecipesJob.java
```

### SRP Evidence

`RecipeServiceImpl` currently handles multiple responsibilities:

- Listing active recipes.
- Loading a recipe by ID.
- Creating recipes.
- Updating recipes.
- Soft deleting recipes.
- Searching recipes with specifications.
- Checking recipe ownership for authorization.
- Mapping `Recipe` entities to `RecipeDTO`.
- Permanently purging old soft-deleted recipes for scheduled maintenance.

The clearest SRP issue is the purge method:

```text
RecipeServiceImpl.purgeSoftDeletedRecipesOlderThan(...)
```

This method belongs to background maintenance logic, not normal user-facing
recipe operations.

The `RecipeService` interface also exposes the maintenance method:

```text
int purgeSoftDeletedRecipesOlderThan(int retentionDays, int batchSize, boolean dryRun);
```

This means the general recipe service abstraction is responsible for both
normal recipe use cases and scheduled cleanup.

### SRP Explanation

The Single Responsibility Principle says that a class should have one reason
to change.

`RecipeServiceImpl` currently has several reasons to change:

- Recipe API requirements can change.
- Search/filtering behavior can change.
- Authorization ownership checks can change.
- DTO shape can change.
- Maintenance purge rules can change.

The purge behavior is especially separate from normal recipe behavior because
it is triggered by a scheduled job and deals with retention, batch size, and
hard deletion of already soft-deleted recipes.

### SRP Strength

Strong.

This is a clean lab example because the responsibility split is easy to
explain:

```text
RecipeServiceImpl manages recipes and purges old soft-deleted recipes.
```

The word "and" indicates two separate reasons to change.

### SRP Refactoring Plan

Create a focused maintenance service, for example:

```text
RecipeMaintenanceService
```

or:

```text
RecipePurgeService
```

Move `purgeSoftDeletedRecipesOlderThan(...)` out of `RecipeServiceImpl` and
into the new service.

After refactoring:

- `RecipeServiceImpl` should keep recipe CRUD/search behavior.
- The new purge service should handle retention, batch size, dry-run behavior,
  and hard deletion of old soft-deleted recipes.
- `PurgeSoftDeletedRecipesJob` should depend on the new purge service instead
  of the general `RecipeService`.
- `RecipeService` should no longer expose the purge method.

### SRP Estimated Effort

Medium.

### SRP Risk Level

Medium.

The refactor affects the scheduled job and service interface, but the behavior
itself is isolated.

### SRP Tests Needed After Refactoring

- Existing recipe CRUD/search service tests should still pass.
- Add or update purge service tests for retention cutoff calculation.
- Add or update purge service tests for batch size limiting.
- Add or update purge service tests for dry-run behavior.
- Add or update purge service tests for actual deletion behavior.
- Add or update scheduled job tests to verify that the job calls the new purge
  service with configured values.

## Recommended DIP Violation

### DIP Candidate

`JwtService` hides configuration and time dependencies.

### DIP Location

File:

```text
src/main/java/hr/tvz/foodiehub/security/JwtService.java
```

Related test:

```text
src/test/java/hr/tvz/foodiehub/security/JwtServiceTest.java
```

### DIP Evidence

`JwtService` uses field injection for the JWT secret:

```text
@Value("${jwt.secret}")
private String secret;
```

It also directly depends on system time:

```text
new Date()
System.currentTimeMillis()
```

The current unit test uses reflection to set the private `secret` field:

```text
ReflectionTestUtils.setField(jwtService, "secret", SECRET);
```

That is a testability smell because the dependency is hidden inside the class
instead of being provided through construction.

### DIP Explanation

The Dependency Inversion Principle says that high-level logic should not
depend directly on low-level concrete details. Dependencies should be explicit
and injectable.

`JwtService` currently depends directly on:

- Spring field injection through `@Value`
- the system clock through `new Date()`
- the system clock through `System.currentTimeMillis()`

These dependencies are hidden implementation details. They make tests less
deterministic and require reflection-based setup.

### DIP Strength

Strong.

This is a good DIP example because the before/after change is small and easy
to demonstrate.

### DIP Refactoring Plan

Use constructor injection for the JWT secret.

Use Java's existing `Clock` abstraction for time-dependent behavior. A custom
interface is not needed.

After refactoring, `JwtService` should receive its dependencies explicitly:

```text
JwtService(String secret, Clock clock)
```

Expected result:

- Tests no longer need `ReflectionTestUtils`.
- Token expiration can be tested deterministically with a fixed clock.
- The service no longer hides important configuration/time dependencies.

### DIP Estimated Effort

Easy.

### DIP Risk Level

Low.

The change is localized to `JwtService`, its configuration, and its unit tests.

### DIP Tests Needed After Refactoring

- Token generation creates a valid token.
- Valid token returns `true`.
- Malformed token returns `false`.
- Token signed with a different secret returns `false`.
- Expired token returns `false` using a fixed `Clock`.
- Roles and username extraction still work.

## Additional Candidates

These candidates are valid but are not the first recommendation for the lab
task.

### DIP: Services Depend on SecurityContextHolder

Files:

```text
src/main/java/hr/tvz/foodiehub/services/implementations/AuthServiceImpl.java
src/main/java/hr/tvz/foodiehub/services/implementations/CommentServiceImpl.java
src/main/java/hr/tvz/foodiehub/services/implementations/RecipeServiceImpl.java
```

Evidence:

- `AuthServiceImpl.getCurrentUser()` reads from `SecurityContextHolder`.
- `CommentServiceImpl.createNewComment()` reads from `SecurityContextHolder`.
- `RecipeServiceImpl.createNewRecipe()` reads from `SecurityContextHolder`.

This is a DIP smell because business services depend on Spring Security global
static state. A possible refactor would introduce a small `CurrentUserProvider`
that wraps `SecurityContextHolder`.

This is architecturally strong, but it is broader than the `JwtService`
refactor.

### DIP: PreAuthorize References Concrete Bean Name

File:

```text
src/main/java/hr/tvz/foodiehub/services/implementations/RecipeServiceImpl.java
```

Evidence:

```text
@recipeServiceImpl.isOwner(#id, authentication.name)
```

This security expression depends on the concrete Spring bean name
`recipeServiceImpl`. A cleaner approach would move ownership checks into a
dedicated authorization component such as `RecipeAuthorizationService`.

### SRP: AuthServiceImpl Mixes Authentication and HTTP Cookie Handling

File:

```text
src/main/java/hr/tvz/foodiehub/services/implementations/AuthServiceImpl.java
```

Evidence:

- Validates login credentials.
- Generates JWT tokens.
- Writes access-token cookies.
- Clears cookies on logout.
- Registers users.
- Reads current user from security context.
- Maps users to DTOs.

This is a strong SRP smell, but the `RecipeServiceImpl` purge example is
cleaner and easier to present as a lab refactor.

## Final Recommendation

Use these two examples for the Jira task:

1. SRP: Extract recipe purge logic from `RecipeServiceImpl` into a dedicated
   maintenance/purge service.
2. DIP: Refactor `JwtService` to use constructor injection for the JWT secret
   and inject `Clock` instead of using direct system time.

These choices are low-overengineering, easy to explain, and suitable for a
before/after SOLID lab demonstration.

## After Refactoring

The selected SRP and DIP refactors were implemented.

### SRP After State

Before:

- `RecipeServiceImpl` handled normal recipe behavior and purge maintenance.
- `RecipeService` exposed `purgeSoftDeletedRecipesOlderThan(...)`.
- `PurgeSoftDeletedRecipesJob` depended on the general `RecipeService`.

After:

- `RecipeServiceImpl` no longer contains purge maintenance logic.
- `RecipeService` only exposes normal recipe use cases.
- A new `RecipeMaintenanceService` abstraction owns the purge use case.
- `RecipeMaintenanceServiceImpl` implements the purge logic.
- `PurgeSoftDeletedRecipesJob` depends on `RecipeMaintenanceService`.

Files changed:

```text
src/main/java/hr/tvz/foodiehub/services/interfaces/RecipeService.java
src/main/java/hr/tvz/foodiehub/services/interfaces/RecipeMaintenanceService.java
src/main/java/hr/tvz/foodiehub/services/implementations/RecipeServiceImpl.java
src/main/java/hr/tvz/foodiehub/services/implementations/RecipeMaintenanceServiceImpl.java
src/main/java/hr/tvz/foodiehub/scheduler/jobs/PurgeSoftDeletedRecipesJob.java
```

Why this improves SRP:

- Recipe CRUD/search logic and scheduled purge maintenance now have separate
  classes and separate reasons to change.
- The scheduled job no longer depends on a broad recipe service interface.

Tests added:

```text
src/test/java/hr/tvz/foodiehub/services/RecipeMaintenanceServiceTest.java
```

The new tests cover:

- retention cutoff calculation
- batch size limiting
- dry-run behavior
- actual deletion behavior

### DIP After State

Before:

- `JwtService` used `@Value` field injection for `jwt.secret`.
- `JwtService` directly used `new Date()` and `System.currentTimeMillis()`.
- `JwtServiceTest` used `ReflectionTestUtils` to inject the secret.

After:

- `JwtService` receives the JWT secret through constructor injection.
- `JwtService` receives a `Clock` through constructor injection.
- A shared `Clock` bean is provided by `TimeConfig`.
- `JwtServiceTest` creates the service with explicit constructor arguments.
- Expiration behavior is tested with a fixed clock.

Files changed:

```text
src/main/java/hr/tvz/foodiehub/security/JwtService.java
src/main/java/hr/tvz/foodiehub/config/TimeConfig.java
src/test/java/hr/tvz/foodiehub/security/JwtServiceTest.java
```

Why this improves DIP:

- `JwtService` no longer hides important dependencies inside fields and system
  calls.
- The class is easier to unit test because configuration and time are explicit.
- Tests no longer need reflection to mutate private fields.
