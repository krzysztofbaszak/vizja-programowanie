package library.db;

import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import library.model.*;

/**
 * Klasa DAO (Data Access Object) do zarządzania zasobami biblioteki (items).
 * Zapewnia operacje CRUD oraz funkcje specyficzne jak wypożyczanie i zwroty.
 */
public class ItemDao {

    /**
     * Zapisuje pozycję (książkę lub magazyn) w bazie danych.
     * Jeśli pozycja o danym UUID już istnieje, zostanie zastąpiona.
     * 
     * @param item obiekt Item (Book lub Magazine) do zapisania
     */
    public static void save(Item item) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
            "INSERT OR REPLACE INTO items (uuid,type,title,year,author,genre,issueNumber,available,loanedTo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        )) {
            ps.setString(1, item.getUuid().toString());
            if (item instanceof Book) {
                Book b = (Book) item;
                ps.setString(2, "book");
                ps.setString(3, b.getTitle());
                ps.setInt(4, b.getYear());
                ps.setString(5, b.getAuthor());
                ps.setString(6, b.getGenre());
                ps.setNull(7, java.sql.Types.INTEGER);
                ps.setBoolean(8, b.isAvailable());
                ps.setString(9, b.getLoanedTo());
            } else if (item instanceof Magazine) {
                Magazine m = (Magazine) item;
                ps.setString(2, "magazine");
                ps.setString(3, m.getTitle());
                ps.setInt(4, m.getYear());
                ps.setNull(5, java.sql.Types.VARCHAR);
                ps.setNull(6, java.sql.Types.VARCHAR);
                ps.setInt(7, m.getIssueNumber());
                ps.setBoolean(8, m.isAvailable());
                ps.setString(9, m.getLoanedTo());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Błąd zapisu pozycji: " + e.getMessage());
        }
    }

    /**
     * Dodaje nową książkę do bazy danych z podanymi parametrami.
     * Automatycznie generuje UUID.
     * 
     * @param title tytuł książki
     * @param author autor książki
     * @param year rok wydania
     * @param genre gatunek książki
     */
    public static void addBook(String title, String author, int year, String genre) {
        Book b = new Book(UUID.randomUUID(), title, year, author, genre, true, null);
        save(b);
    }

    /**
     * Pobiera listę wszystkich pozycji (książek i magazynów) z bazy danych.
     * 
     * @return lista obiektów Item (Book lub Magazine)
     */
    public static List<Item> getAll() {
        List<Item> result = new ArrayList<>();
        try (Statement st = DatabaseManager.getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM items")) {
            while (rs.next()) {
                String type = rs.getString("type");
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String title = rs.getString("title");
                int year = rs.getInt("year");
                boolean available = rs.getBoolean("available");
                String loanedTo = rs.getString("loanedTo");

                if ("book".equals(type)) {
                    String author = rs.getString("author");
                    String genre = rs.getString("genre");
                    result.add(new Book(uuid, title, year, author, genre, available, loanedTo));
                } else if ("magazine".equals(type)) {
                    int issueNumber = rs.getInt("issueNumber");
                    result.add(new Magazine(uuid, title, year, issueNumber, available, loanedTo));
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd pobierania pozycji: " + e.getMessage());
        }
        return result;
    }

    /**
     * Pobiera pozycję o podanym UUID.
     * 
     * @param uuid UUID pozycji do znalezienia
     * @return Optional zawierający obiekt Item jeśli znaleziono, pusty Optional w przeciwnym wypadku
     */
    public static Optional<Item> getByUuid(UUID uuid) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
                "SELECT * FROM items WHERE uuid=?"
        )) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type");
                String title = rs.getString("title");
                int year = rs.getInt("year");
                boolean available = rs.getBoolean("available");
                String loanedTo = rs.getString("loanedTo");

                if ("book".equals(type)) {
                    String author = rs.getString("author");
                    String genre = rs.getString("genre");
                    return Optional.of(new Book(uuid, title, year, author, genre, available, loanedTo));
                } else if ("magazine".equals(type)) {
                    int issueNumber = rs.getInt("issueNumber");
                    return Optional.of(new Magazine(uuid, title, year, issueNumber, available, loanedTo));
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd pobierania pozycji: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Usuwa pozycję o podanym UUID z bazy danych.
     * 
     * @param uuid UUID pozycji do usunięcia
     */
    public static void delete(UUID uuid) {
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
                "DELETE FROM items WHERE uuid=?"
        )) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Błąd usuwania pozycji: " + e.getMessage());
        }
    }

    /**
     * Próbuje wypożyczyć pozycję dla użytkownika.
     * Jeśli pozycja jest dostępna, zostaje oznaczona jako wypożyczona.
     * 
     * @param uuidStr UUID pozycji w formie tekstowej
     * @param username nazwa użytkownika wypożyczającego
     * @return true jeśli wypożyczenie się powiodło, false w przeciwnym razie
     */
    public static boolean borrowItem(String uuidStr, String username) {
        try {
            UUID uuid = UUID.fromString(uuidStr);
            Optional<Item> opt = getByUuid(uuid);
            if (opt.isPresent()) {
                Item item = opt.get();
                if (item.isAvailable()) {
                    item.setAvailable(false);
                    item.setLoanedTo(username);
                    save(item);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd wypożyczania: " + e.getMessage());
        }
        return false;
    }

    /**
     * Próbuje zwrócić pozycję do biblioteki (oznaczyć jako dostępna).
     * 
     * @param uuidStr UUID pozycji w formie tekstowej
     * @return true jeśli zwrot się powiódł, false w przeciwnym razie
     */
    public static boolean returnItem(String uuidStr) {
        try {
            UUID uuid = UUID.fromString(uuidStr);
            Optional<Item> opt = getByUuid(uuid);
            if (opt.isPresent()) {
                Item item = opt.get();
                if (!item.isAvailable()) {
                    item.setAvailable(true);
                    item.setLoanedTo(null);
                    save(item);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd zwrotu: " + e.getMessage());
        }
        return false;
    }

    /**
     * Próbuje zwrócić pozycję, jeśli aktualny użytkownik ją wypożyczył.
     * 
     * @param uuidStr UUID pozycji w formie tekstowej
     * @param username nazwa użytkownika zwracającego
     * @return true jeśli zwrot się powiódł, false w przeciwnym razie
     */
    public static boolean returnItemByUser(String uuidStr, String username) {
        try {
            UUID uuid = UUID.fromString(uuidStr);
            Optional<Item> opt = getByUuid(uuid);
            if (opt.isPresent()) {
                Item item = opt.get();
                if (!item.isAvailable() && username.equals(item.getLoanedTo())) {
                    item.setAvailable(true);
                    item.setLoanedTo(null);
                    save(item);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd zwrotu: " + e.getMessage());
        }
        return false;
    }

    /**
     * Pobiera listę wypożyczonych pozycji dla danego użytkownika.
     * 
     * @param username nazwa użytkownika
     * @return lista wypożyczonych obiektów Item
     */
    public static List<Item> getUserLoans(String username) {
        List<Item> result = new ArrayList<>();
        try (PreparedStatement ps = DatabaseManager.getConn().prepareStatement(
                "SELECT * FROM items WHERE loanedTo=?"
        )) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String title = rs.getString("title");
                int year = rs.getInt("year");
                boolean available = rs.getBoolean("available");
                String loanedTo = rs.getString("loanedTo");

                if ("book".equals(type)) {
                    String author = rs.getString("author");
                    String genre = rs.getString("genre");
                    result.add(new Book(uuid, title, year, author, genre, available, loanedTo));
                } else if ("magazine".equals(type)) {
                    int issueNumber = rs.getInt("issueNumber");
                    result.add(new Magazine(uuid, title, year, issueNumber, available, loanedTo));
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd pobierania wypożyczeń użytkownika: " + e.getMessage());
        }
        return result;
    }

    /**
     * Eksportuje listę wypożyczonych pozycji do pliku tekstowego.
     * Format: UUID;tytuł;użytkownik wypożyczający
     * 
     * @param path ścieżka do pliku wyjściowego
     */
    public static void exportLoansToTxt(String path) {
        try (PrintWriter out = new PrintWriter(path)) {
            List<Item> all = getAll();
            for (Item i : all) {
                if (!i.isAvailable() && i.getLoanedTo() != null) {
                    out.printf("%s;%s;%s\n", i.getUuid().toString(), i.getTitle(), i.getLoanedTo());
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd eksportu wypożyczeń: " + e.getMessage());
        }
    }
}
