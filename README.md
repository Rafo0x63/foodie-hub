# FoodieHub

FoodieHub je web aplikacija za dijeljenje recepata, planiranje obroka i organizaciju kuhanja na jednom mjestu. Cilj projekta je ponuditi modernu platformu na kojoj korisnici mogu pregledavati i objavljivati recepte, pratiti korake pripreme, organizirati tjedne obroke i jednostavnije planirati kupovinu namirnica.

Projekt je zamišljen kao centralno mjesto za digitalno kuhanje — od inspiracije i pretrage recepata do planiranja obroka i upravljanja sastojcima.

## Sadržaj

- [O projektu](#o-projektu)
- [Glavne funkcionalnosti](#glavne-funkcionalnosti)
- [Tehnologije](#tehnologije)
- [Arhitektura projekta](#arhitektura-projekta)
- [Planirani moduli](#planirani-moduli)
- [Pokretanje projekta](#pokretanje-projekta)
- [Struktura projekta](#struktura-projekta)
- [Baza podataka](#baza-podataka)
- [Plan razvoja](#plan-razvoja)
- [Autor](#autor)

## O projektu

Danas su recepti, savjeti za kuhanje i ideje za obroke raspršeni po različitim platformama. FoodieHub nastaje kao rješenje za taj problem — aplikacija koja objedinjuje recepte, korisničke profile, planiranje obroka i buduće funkcionalnosti vezane uz personalizirano iskustvo kuhanja.

Aplikacija će omogućiti korisnicima da:

- objavljuju vlastite recepte
- pregledavaju tuđe recepte
- dodaju sastojke i korake pripreme
- kategoriziraju i lakše pretražuju sadržaj
- planiraju obroke za više dana unaprijed
- generiraju pregled potrebnih namirnica

## Glavne funkcionalnosti

Planirane funkcionalnosti projekta uključuju:

### Upravljanje receptima
- kreiranje novih recepata
- uređivanje postojećih recepata
- brisanje recepata
- prikaz detalja recepta
- dodavanje sastojaka s količinama
- dodavanje koraka pripreme

### Organizacija sadržaja
- kategorije recepata
- tagovi
- filtriranje i pretraga
- pregled recepata po nazivu, kategoriji ili oznakama

### Korisničke funkcionalnosti
- registracija i prijava korisnika
- korisnički profil
- pregled vlastitih recepata
- spremanje omiljenih recepata

### Planiranje obroka
- tjedni planer obroka
- dodavanje recepata u plan obroka
- generiranje liste potrebnih namirnica

### Dodatne mogućnosti
- upload slika za recepte
- komentari i ocjene recepata
- rangiranje i personalizacija sadržaja
- administracija kategorija i sadržaja

## Tehnologije

Projekt se razvija korištenjem sljedećih tehnologija:

### Backend
- **Spring Boot 4**
- **Spring Data JPA 25**
- **Spring Web**
- **MySQL**
- **Maven**

### Frontend
- **Angular 21**
- **Tailwind CSS**
- **TypeScript**
- **HTML / CSS**

### Ostalo
- REST API komunikacija između frontenda i backenda
- multipart upload datoteka za slike recepata
- verzioniranje izvornog koda putem Git-a i GitHub-a

## Arhitektura projekta

Projekt je organiziran kao aplikacija s odvojenim frontend i backend dijelom:

- **backend** služi za poslovnu logiku, rad s bazom podataka i izlaganje REST API-ja
- **frontend** služi za prikaz korisničkog sučelja i komunikaciju s backendom

Takav pristup omogućuje jasnu podjelu odgovornosti, lakše održavanje sustava i jednostavnije proširenje projekta u budućnosti.

## Planirani moduli

### 1. Modul za korisnike
- registracija korisnika
- prijava korisnika
- upravljanje profilom

### 2. Modul za recepte
- CRUD operacije nad receptima
- upravljanje sastojcima i koracima
- dodavanje slika

### 3. Modul za pretragu i filtriranje
- pretraga po nazivu
- filtriranje po kategoriji
- filtriranje po tagovima
- filtriranje po vremenu pripreme i tipu prehrane

### 4. Modul za planer obroka
- dodavanje recepata u dane u tjednu
- pregled planiranih obroka
- povezivanje s listom za kupovinu

### 5. Modul za listu za kupovinu
- generiranje liste na temelju plana obroka
- grupiranje namirnica
- označavanje kupljenih stavki

## Pokretanje projekta

### Backend
1. Klonirati repozitorij:
   ```bash
   git clone https://github.com/Rafo0x63/foodie-hub.git
