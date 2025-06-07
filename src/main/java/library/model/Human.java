package library.model;

import java.io.Serializable;

/**
 * Klasa abstrakcyjna reprezentująca człowieka (użytkownika lub bibliotekarza) w systemie.
 * Zawiera podstawowe pola: pełną nazwę, nazwę użytkownika oraz hasło.
 * Implementuje interfejs {@link Serializable} do serializacji obiektów.
 */
public abstract class Human implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Pełna nazwa człowieka */
    protected String fullName;

    /** Nazwa użytkownika (login) */
    protected String username;

    /** Hasło użytkownika */
    protected String password;

    /**
     * Konstruktor klasy Human.
     *
     * @param fullName pełna nazwa
     * @param username nazwa użytkownika (login)
     * @param password hasło
     */
    public Human(String fullName, String username, String password) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
    }

    /** @return pełna nazwa użytkownika */
    public String getFullName() { return fullName; }

    /** @return nazwa użytkownika (login) */
    public String getUsername() { return username; }

    /** @return hasło użytkownika */
    public String getPassword() { return password; }
}
