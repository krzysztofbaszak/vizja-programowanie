package library.db;

import library.model.Librarian;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Klasa DAO (Data Access Object) do zarządzania danymi bibliotekarzy w bazie danych.
 * Zapewnia metody do dodawania, edytowania, usuwania oraz pobierania bibliotekarzy.
 */
public class LibrarianDao {

    /**
     * Dodaje nowego bibliotekarza do bazy danych.
     *
     * @param username unikalna nazwa użytkownika
     * @param fullname pełna nazwa bibliotekarza
     * @param password hasło użytkownika
     * @param salary wynagrodzenie bibliotekarza
     */
    public static void addLibrarian(String username, String fullname, String password, int salary) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
                "INSERT INTO users (username, fullname, password, role, salary) VALUES (?, ?, ?, 'librarian', ?)")) {
            ps.setString(1, username);
            ps.setString(2, fullname);
            ps.setString(3, password);
            ps.setInt(4, salary);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Błąd dodawania bibliotekarza: " + e.getMessage());
        }
    }

    /**
     * Edytuje dane istniejącego bibliotekarza.
     *
     * @param username unikalna nazwa użytkownika do edycji
     * @param fullname nowa pełna nazwa
     * @param password nowe hasło
     * @param salary nowe wynagrodzenie
     */
    public static void editLibrarian(String username, String fullname, String password, int salary) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
                "UPDATE users SET fullname=?, password=?, salary=? WHERE username=? AND role='librarian'")) {
            ps.setString(1, fullname);
            ps.setString(2, password);
            ps.setInt(3, salary);
            ps.setString(4, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Błąd edycji bibliotekarza: " + e.getMessage());
        }
    }

    /**
     * Usuwa bibliotekarza z bazy danych.
     *
     * @param username unikalna nazwa użytkownika do usunięcia
     */
    public static void removeLibrarian(String username) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
                "DELETE FROM users WHERE username=? AND role='librarian'")) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Błąd usuwania bibliotekarza: " + e.getMessage());
        }
    }

    /**
     * Pobiera listę wszystkich bibliotekarzy z bazy danych.
     *
     * @return lista obiektów Librarian
     */
    public static List<Librarian> getAllLibrarians() {
        List<Librarian> librarians = new ArrayList<>();
        try (Statement st = DatabaseManager.getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users WHERE role='librarian'")) {
            while (rs.next()) {
                librarians.add(new Librarian(
                        rs.getString("fullname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("salary")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Błąd pobierania bibliotekarzy: " + e.getMessage());
        }
        return librarians;
    }

    /**
     * Pobiera bibliotekarza o podanej nazwie użytkownika.
     *
     * @param username unikalna nazwa użytkownika
     * @return Optional zawierający bibliotekarza, jeśli znaleziono; pusty Optional w przeciwnym wypadku
     */
    public static Optional<Librarian> getByUsername(String username) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
                "SELECT * FROM users WHERE username=? AND role='librarian'"
        )) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new Librarian(
                    rs.getString("fullname"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("salary")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Błąd pobierania bibliotekarza: " + e.getMessage());
        }
        return Optional.empty();
    }
}
