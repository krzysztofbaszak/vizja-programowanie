package library.cli;

import java.util.Scanner;
import library.service.LibraryService;

/**
 * Klasa reprezentująca panel poleceń dla zwykłego użytkownika systemu bibliotecznego.
 * Umożliwia przeglądanie zasobów, wypożyczanie i zwracanie książek oraz przeglądanie własnych wypożyczeń.
 */
public class UserCommand {
    private final String username;

    /**
     * Konstruktor klasy UserCommand.
     * 
     * @param username Nazwa użytkownika wykonującego polecenia.
     */
    public UserCommand(String username) {
        this.username = username;
    }

    /**
     * Uruchamia interaktywny panel użytkownika, umożliwiający wybór opcji
     * związanych z korzystaniem z zasobów biblioteki.
     */
    public void run() {
        LibraryService service = new LibraryService();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n== PANEL UŻYTKOWNIKA ==");
            System.out.println("1. Przeglądaj pozycje");
            System.out.println("2. Wypożycz książkę");
            System.out.println("3. Moje wypożyczenia");
            System.out.println("4. Zwrot mojej książki");
            System.out.println("5. Wyloguj");
            System.out.print("Wybierz opcję: ");
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    service.printItemsTable();
                    break;
                case "2":
                    service.borrowItemInteractive(username, false);
                    break;
                case "3":
                    service.listUserLoans(username);
                    break;
                case "4":
                    service.returnUserLoanInteractive(username);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Nieznana opcja");
            }
        }
    }
}