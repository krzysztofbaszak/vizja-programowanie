
# Vizja Library

## Opis projektu
Projekt **Vizja Library** to system biblioteczny stworzony jako zadanie zaliczeniowe z przedmiotu **Podstawy Programowania**.  
Autor: **Krzysztof Baszak**

Aplikacja umożliwia zarządzanie zasobami biblioteki, użytkownikami oraz bibliotekarzami.  
System posiada dwa oddzielne panele:  
- **Panel bibliotekarza** — pełna administracja, zarządzanie zasobami, użytkownikami i bibliotekarzami, import/eksport danych  
- **Panel użytkownika** — możliwość przeglądania katalogu, wypożyczania i zwrotu książek

---

## Funkcjonalności
- Zarządzanie książkami i magazynami  
- Zarządzanie użytkownikami i bibliotekarzami  
- Logowanie z rozróżnieniem roli (bibliotekarz, użytkownik)  
- Import i eksport danych w formatach binarnych i tekstowych (CSV)  
- Obsługa wypożyczeń i zwrotów  
- Interaktywny interfejs tekstowy w konsoli  

---

## Informacje o użytkownikach
- Domyślnie przy pierwszym uruchomieniu tworzy się użytkownik **admin** z hasłem **admin** i rolą bibliotekarza.  
- Nie jest automatycznie tworzony użytkownik z rolą zwykłego użytkownika — można go dodać ręcznie z poziomu panelu bibliotekarza.  
- Użytkownik i bibliotekarz mają oddzielne panele oraz różne uprawnienia i funkcje.  

---

## Dokumentacja
Dokumentację projektu można wygenerować za pomocą narzędzia `javadoc`.  
Instrukcja generowania dokumentacji:

```bash
./gradlew javadoc
```

Dokumentacja zostanie wygenerowana w katalogu:  
`build/docs/javadoc/index.html`

---

## Kompilacja i uruchamianie

### Wymagania
- Java 17 (OpenJDK 17 lub kompatybilne)  
- Gradle (projekt korzysta z wrappera Gradle)

### Kompilacja i uruchomienie

1. Skompiluj projekt i utwórz plik JAR ze wszystkimi zależnościami:

```bash
./gradlew shadowJar
```

2. Uruchom aplikację:

```bash
java -jar build/libs/vizja-app-1.0-all.jar
```

---

## Struktura projektu

- `src/main/java/library` — kod źródłowy aplikacji  
- `library.db` — plik bazy danych SQLite (tworzony automatycznie)  
- `build` — katalog z wynikami kompilacji i dokumentacją  
- `bin` — (opcjonalnie) katalog z plikami klas  
- `gradlew`, `gradlew.bat` — wrapper Gradle do uruchamiania builda

---

## Autor
Krzysztof Baszak  
Projekt realizowany jako zadanie zaliczeniowe przedmiotu **Podstawy Programowania**

---

## Uwagi
- Przed uruchomieniem aplikacji zaleca się zapoznać z kodem źródłowym i strukturą bazy danych.  
- Baza danych SQLite jest tworzona automatycznie przy pierwszym uruchomieniu, zawiera domyślnego użytkownika admin z hasłem admin.  

---

## Licencja
Projekt do użytku edukacyjnego.
