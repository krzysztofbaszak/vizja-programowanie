package library.model;

import java.io.Serializable;

/**
 * Klasa reprezentująca użytkownika biblioteki.
 * Dziedziczy po klasie Human.
 */
public class User extends Human implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Konstruktor tworzący użytkownika z podanymi danymi.
     * 
     * @param fullName pełna nazwa użytkownika
     * @param username unikalna nazwa użytkownika (login)
     * @param password hasło użytkownika
     */
    public User(String fullName, String username, String password) {
        super(fullName, username, password);
    }
}
