package library.cli;

import java.util.Scanner;
import library.service.LibraryService;

/**
 * Klasa reprezentująca panel poleceń dla bibliotekarza w systemie bibliotecznym.
 * Umożliwia zarządzanie zasobami biblioteki, użytkownikami, bibliotekarzami
 * oraz import/eksport danych.
 */
public class LibrarianCommand {
    private final String username;

    /**
     * Konstruktor klasy LibrarianCommand.
     * 
     * @param username Nazwa użytkownika bibliotekarza wykonującego polecenia.
     */
    public LibrarianCommand(String username) {
        this.username = username;
    }

    /**
     * Metoda uruchamia interaktywny panel bibliotekarza,
     * który umożliwia wybór różnych opcji zarządzania biblioteką.
     */
    public void run() {
        LibraryService service = new LibraryService();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n== PANEL BIBLIOTEKARZA ==");
            System.out.println("1. Zarządzanie zasobami biblioteki");
            System.out.println("2. Zarządzanie użytkownikami");
            System.out.println("3. Zarządzanie bibliotekarzami");
            System.out.println("9. Import/Eksport");
            System.out.println("0. Wyloguj");
            System.out.print("Wybierz opcję: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    library.util.ScreenUtil.clearScreen();
                    manageLibraryItemsMenu(service, scanner);
                    break;
                case "2":
                    library.util.ScreenUtil.clearScreen();
                    manageUsersMenu(service, scanner);
                    break;
                case "3":
                    library.util.ScreenUtil.clearScreen();
                    manageLibrariansMenu(service, scanner);
                    break;
                case "9":
                    library.util.ScreenUtil.clearScreen();
                    importExportMenu(service, scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Nieznana opcja!");
            }
        }
    }

    /**
     * Wyświetla i obsługuje menu zarządzania zasobami biblioteki (np. książkami).
     * 
     * @param service Serwis biblioteczny obsługujący logikę biznesową.
     * @param scanner Scanner do pobierania danych od użytkownika.
     */
    private void manageLibraryItemsMenu(LibraryService service, Scanner scanner) {
        while (true) {
            System.out.println("\n-- Zarządzanie zasobami biblioteki --");
            System.out.println("1. Przeglądaj pozycje");
            System.out.println("2. Dodaj książkę");
            System.out.println("3. Wypożycz książkę");
            System.out.println("4. Przyjmij zwrot");
            System.out.println("5. Usuń książkę");
            System.out.println("0. Powrót");
            System.out.print("Wybierz opcję: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    library.util.ScreenUtil.clearScreen();
                    service.printItemsTable();
                    break;
                case "2":
                    library.util.ScreenUtil.clearScreen();
                    service.addBookInteractive();
                    break;
                case "3":
                    library.util.ScreenUtil.clearScreen();
                    service.borrowItemInteractive(username, true);
                    break;
                case "4":
                    library.util.ScreenUtil.clearScreen();
                    service.returnItemInteractive();
                    break;
                case "5":
                    library.util.ScreenUtil.clearScreen();
                    service.removeItemInteractive();
                    break;
                case "0": return;
                default: System.out.println("Nieznana opcja!");
            }
        }
    }

    /**
     * Wyświetla i obsługuje menu zarządzania użytkownikami systemu.
     * 
     * @param service Serwis biblioteczny obsługujący logikę biznesową.
     * @param scanner Scanner do pobierania danych od użytkownika.
     */
    private void manageUsersMenu(LibraryService service, Scanner scanner) {
        while (true) {
            library.util.ScreenUtil.clearScreen();
            System.out.println("\n-- Zarządzanie użytkownikami --");
            System.out.println("1. Dodaj użytkownika");
            System.out.println("2. Edytuj użytkownika");
            System.out.println("3. Usuń użytkownika");
            System.out.println("0. Powrót");
            System.out.print("Wybierz opcję: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1": library.util.ScreenUtil.clearScreen(); service.addUserInteractive(); break;
                case "2": library.util.ScreenUtil.clearScreen(); service.editUserInteractive(); break;
                case "3": library.util.ScreenUtil.clearScreen(); service.removeUserInteractive(); break;
                case "0": return;
                default: System.out.println("Nieznana opcja!");
            }
        }
    }

    /**
     * Wyświetla i obsługuje menu zarządzania bibliotekarzami.
     * 
     * @param service Serwis biblioteczny obsługujący logikę biznesową.
     * @param scanner Scanner do pobierania danych od użytkownika.
     */
    private void manageLibrariansMenu(LibraryService service, Scanner scanner) {
        while (true) {
            library.util.ScreenUtil.clearScreen();
            System.out.println("\n-- Zarządzanie bibliotekarzami --");
            System.out.println("1. Dodaj bibliotekarza");
            System.out.println("2. Edytuj bibliotekarza");
            System.out.println("3. Usuń bibliotekarza");
            System.out.println("4. Wyświetl bibliotekarzy");
            System.out.println("0. Powrót");
            System.out.print("Wybierz opcję: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1": service.addLibrarianInteractive(); break;
                case "2": service.editLibrarianInteractive(); break;
                case "3": service.removeLibrarianInteractive(); break;
                case "4":
                    service.printLibrarianTable(library.db.LibrarianDao.getAllLibrarians());
                    System.out.print("Wciśnij Enter by wrócić...");
                    scanner.nextLine();
                    break;
                case "0": return;
                default: System.out.println("Nieznana opcja!");
            }
        }
    }

    /**
     * Wyświetla i obsługuje menu importu i eksportu danych (książki, użytkownicy, wypożyczenia).
     * 
     * @param service Serwis biblioteczny obsługujący logikę biznesową.
     * @param scanner Scanner do pobierania danych od użytkownika.
     */
    private void importExportMenu(LibraryService service, Scanner scanner) {
        while (true) {
            library.util.ScreenUtil.clearScreen();
            System.out.println("\n-- Import/Eksport --");
            System.out.println("1. Import/eksport książek (ze statusami)");
            System.out.println("2. Import/Eksport użytkowników");
            System.out.println("3. Eksport wypożyczonych książek wraz z użytkownikami");
            System.out.println("0. Powrót");
            System.out.print("Wybierz opcję: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    service.importExportBooksInteractive();
                    break;
                case "2":
                    // Podmenu wyboru import/eksport użytkowników
                    while (true) {
                        System.out.println("\n1. Importuj użytkowników");
                        System.out.println("2. Eksportuj użytkowników");
                        System.out.println("0. Powrót");
                        System.out.print("Wybierz opcję: ");
                        String userOpt = scanner.nextLine().trim();
                        if (userOpt.equals("1")) {
                            service.importUsersInteractive();
                        } else if (userOpt.equals("2")) {
                            service.exportUsersInteractive();
                        } else if (userOpt.equals("0")) {
                            break;
                        } else {
                            System.out.println("Nieznana opcja!");
                        }
                    }
                    break;
                case "3":
                    service.exportLoansInteractive();
                    break;
                case "0": return;
                default: System.out.println("Nieznana opcja!");
            }
        }
    }
}
