package library.db;

import java.sql.*;

/**
 * Klasa zarządzająca połączeniem z bazą danych SQLite,
 * tworzeniem schematu bazy oraz inicjalizacją domyślnego użytkownika admin.
 */
public class DatabaseManager {
    private static Connection conn = null;

    /**
     * Nawiązuje połączenie z bazą danych SQLite.
     * Jeśli połączenie jest już otwarte, metoda nie wykonuje żadnej operacji.
     * Inicjalizuje schemat bazy oraz zapewnia istnienie użytkownika admin.
     */
    public static void connect() {
        if (conn != null) return;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:library.db");
            initSchema();
            ensureAdminUser();
        } catch (Exception e) {
            throw new RuntimeException("Błąd połączenia z bazą: " + e.getMessage());
        }
    }

    /**
     * Tworzy tabele w bazie danych, jeśli nie istnieją.
     * Tabele to: items (zasoby biblioteki) oraz users (użytkownicy i bibliotekarze).
     * 
     * @throws SQLException jeśli wystąpi błąd SQL podczas tworzenia tabel
     */
    private static void initSchema() throws SQLException {
        Statement st = conn.createStatement();

        st.executeUpdate("CREATE TABLE IF NOT EXISTS items (" +
                "uuid TEXT PRIMARY KEY, " +
                "type TEXT, " + 
                "title TEXT, " +
                "year INT, " +
                "author TEXT, " +     
                "genre TEXT, " +      
                "issueNumber INT, " + 
                "available BOOLEAN, " +
                "loanedTo TEXT" +
                ");");

        st.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "fullname TEXT, " +
                "password TEXT, " +
                "role TEXT, " +      
                "salary INT " +      
                ");");
    }

    /**
     * Sprawdza, czy w tabeli users jest jakikolwiek użytkownik.
     * Jeśli tabela jest pusta, tworzy domyślnego użytkownika admin (rolę bibliotekarza).
     * 
     * @throws SQLException jeśli wystąpi błąd SQL podczas operacji
     */
    private static void ensureAdminUser() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) AS count FROM users")) {
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count == 0) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO users (username, fullname, password, role) VALUES (?, ?, ?, ?)")) {
                        ps.setString(1, "admin");
                        ps.setString(2, "Administrator");
                        ps.setString(3, "admin"); 
                        ps.setString(4, "librarian");
                        ps.executeUpdate();
                        System.out.println("Dodano domyślnego użytkownika admin.");
                    }
                }
            }
        }
    }

    /**
     * Zwraca aktywne połączenie do bazy danych SQLite,
     * nawiązując połączenie, jeśli jeszcze nie istnieje.
     * 
     * @return Obiekt Connection reprezentujący połączenie z bazą
     */
    public static Connection getConn() {
        connect();
        return conn;
    }
}
