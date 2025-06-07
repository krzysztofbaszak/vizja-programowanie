package library.db;

import library.model.*;
import java.sql.*;
import java.util.*;

/**
 * Klasa DAO (Data Access Object) do zarządzania użytkownikami i bibliotekarzami w bazie danych.
 * Zapewnia metody do wyszukiwania, dodawania, edytowania, usuwania oraz pobierania użytkowników.
 */
public class UserDao {

    /**
     * Wyszukuje użytkownika o podanym username i roli (user lub librarian).
     *
     * @param username nazwa użytkownika do wyszukania
     * @param role rola użytkownika (np. "user" lub "librarian")
     * @return Optional zawierający obiekt Human (User lub Librarian) jeśli znaleziono, 
     *         pusty Optional w przeciwnym wypadku
     */
    public static Optional<Human> findUser(String username, String role) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
                "SELECT * FROM users WHERE username=? AND role=?"
        )) {
            ps.setString(1, username);
            ps.setString(2, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String fullname = rs.getString("fullname");
                String password = rs.getString("password");
                if ("librarian".equals(role)) {
                    int salary = rs.getInt("salary");
                    return Optional.of(new Librarian(fullname, username, password, salary));
                } else {
                    return Optional.of(new User(fullname, username, password));
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd logowania: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Pobiera użytkownika o podanym username, jeśli jest rolą "user".
     *
     * @param username nazwa użytkownika
     * @return Optional zawierający użytkownika lub pusty Optional jeśli nie znaleziono
     */
    public static Optional<User> getByUsername(String username) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
                "SELECT * FROM users WHERE username=? AND role='user'"
        )) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String fullname = rs.getString("fullname");
                String password = rs.getString("password");
                return Optional.of(new User(fullname, username, password));
            }
        } catch (SQLException e) {
            System.err.println("Błąd pobierania użytkownika: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Zapisuje użytkownika (User lub Librarian) w bazie danych.
     * Dla bibliotekarza zapisuje również wynagrodzenie.
     *
     * @param user obiekt użytkownika do zapisania
     * @param role rola użytkownika ("user" lub "librarian")
     */
    public static void saveUser(Human user, String role) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
            "INSERT OR REPLACE INTO users (username, fullname, password, role, salary) VALUES (?, ?, ?, ?, ?)"
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPassword());
            ps.setString(4, role);
            if (user instanceof Librarian) {
                ps.setInt(5, ((Librarian) user).getSalary());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Błąd zapisu użytkownika: " + e.getMessage());
        }
    }

    /**
     * Dodaje nowego użytkownika o roli "user".
     *
     * @param username nazwa użytkownika
     * @param fullname pełna nazwa użytkownika
     * @param password hasło użytkownika
     */
    public static void addUser(String username, String fullname, String password) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
            "INSERT INTO users (username, fullname, password, role) VALUES (?, ?, ?, 'user')"
        )) {
            ps.setString(1, username);
            ps.setString(2, fullname);
            ps.setString(3, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Błąd dodawania użytkownika: " + e.getMessage());
        }
    }

    /**
     * Edytuje dane użytkownika o roli "user".
     *
     * @param username nazwa użytkownika do edycji
     * @param fullname nowa pełna nazwa
     * @param password nowe hasło
     */
    public static void editUser(String username, String fullname, String password) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
            "UPDATE users SET fullname=?, password=? WHERE username=? AND role='user'"
        )) {
            ps.setString(1, fullname);
            ps.setString(2, password);
            ps.setString(3, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Błąd edycji użytkownika: " + e.getMessage());
        }
    }

    /**
     * Usuwa użytkownika o roli "user" z bazy danych.
     *
     * @param username nazwa użytkownika do usunięcia
     */
    public static void removeUser(String username) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
            "DELETE FROM users WHERE username=? AND role='user'"
        )) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Błąd usuwania użytkownika: " + e.getMessage());
        }
    }

    /**
     * Pobiera listę wszystkich użytkowników o roli "user".
     *
     * @return lista użytkowników
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Statement st = DatabaseManager.getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users WHERE role='user'")) {
            while (rs.next()) {
                users.add(new User(
                        rs.getString("fullname"),
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Błąd pobierania użytkowników: " + e.getMessage());
        }
        return users;
    }

    /**
     * Eksportuje listę użytkowników do pliku tekstowego.
     * Format: username;fullname;password (jeden użytkownik na linię).
     *
     * @param path ścieżka do pliku eksportu
     */
    public static void exportUsersToTxt(String path) {
        List<User> users = getAllUsers();
        try (java.io.PrintWriter out = new java.io.PrintWriter(path)) {
            for (User u : users) {
                out.println(u.getUsername() + ";" + u.getFullName() + ";" + u.getPassword());
            }
        } catch (Exception e) {
            System.err.println("Błąd eksportu użytkowników: " + e.getMessage());
        }
    }
}
