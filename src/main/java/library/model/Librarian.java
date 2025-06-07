package library.model;

import java.io.Serializable;

/**
 * Klasa reprezentująca bibliotekarza, rozszerzająca klasę {@link Human}.
 * Zawiera dodatkowo informacje o wynagrodzeniu bibliotekarza.
 * Implementuje interfejs {@link Serializable}.
 */
public class Librarian extends Human implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Wynagrodzenie bibliotekarza */
    private int salary;

    /**
     * Konstruktor tworzący bibliotekarza z pełnymi danymi.
     *
     * @param fullName pełna nazwa (imię i nazwisko)
     * @param username unikalna nazwa użytkownika
     * @param password hasło
     * @param salary wynagrodzenie
     */
    public Librarian(String fullName, String username, String password, int salary) {
        super(fullName, username, password);
        this.salary = salary;
    }

    /**
     * Pobiera wynagrodzenie bibliotekarza.
     *
     * @return wynagrodzenie
     */
    public int getSalary() { return salary; }

    /**
     * Ustawia wynagrodzenie bibliotekarza.
     *
     * @param salary nowe wynagrodzenie
     */
    public void setSalary(int salary) { this.salary = salary; }
}
