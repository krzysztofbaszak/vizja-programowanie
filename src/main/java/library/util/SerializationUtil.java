package library.util;

import java.io.*;
import java.util.List;
import java.util.UUID;
import library.db.ItemDao;
import library.model.Book;
import library.model.Item;

/**
 * Klasa narzędziowa do eksportu i importu danych bibliotecznych w formacie binarnym oraz tekstowym (CSV).
 */
public class SerializationUtil {

    /**
     * Eksportuje wszystkie pozycje biblioteczne do pliku binarnego.
     *
     * @param filePath ścieżka do pliku, do którego zostaną zapisane dane.
     */
    public static void exportToBin(String filePath) {
        List<Item> items = ItemDao.getAll();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(items);
            System.out.println("Wyeksportowano do pliku binarnego: " + filePath);
        } catch (IOException e) {
            System.err.println("Błąd eksportu do pliku binarnego: " + e.getMessage());
        }
    }

    /**
     * Importuje pozycje biblioteczne z pliku binarnego.
     * Nowe pozycje są dodawane, a istniejące pomijane.
     *
     * @param filePath ścieżka do pliku binarnego z danymi.
     */
    @SuppressWarnings("unchecked")
    public static void importFromBin(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            List<Item> items = (List<Item>) ois.readObject();
            int added = 0, skipped = 0;
            for (Item item : items) {
                if (ItemDao.getByUuid(item.getUuid()).isEmpty()) {
                    ItemDao.save(item);
                    added++;
                } else {
                    skipped++;
                }
            }
            System.out.printf("Zaimportowano %d nowych książek (pominięto %d istniejących) z pliku binarnego: %s\n",
                    added, skipped, filePath);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Błąd importu z pliku binarnego: " + e.getMessage());
        }
    }

    /**
     * Eksportuje wszystkie pozycje biblioteczne (tylko książki) do pliku tekstowego CSV.
     *
     * @param filePath ścieżka do pliku tekstowego.
     */
    public static void exportToTxt(String filePath) {
        List<Item> items = ItemDao.getAll();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Typ;UUID;Tytuł;Autor;Rok;Gatunek;Status;Wypożyczający");
            for (Item item : items) {
                if (item instanceof Book) {
                    Book b = (Book) item;
                    writer.printf("Book;%s;%s;%s;%d;%s;%s;%s\n",
                            b.getUuid(), b.getTitle(), b.getAuthor(), b.getYear(),
                            b.getGenre(), b.isAvailable() ? "DOSTĘPNA" : "WYPOŻYCZONA",
                            b.getLoanedTo() == null ? "-" : b.getLoanedTo());
                }
            }
            System.out.println("Wyeksportowano do pliku tekstowego: " + filePath);
        } catch (IOException e) {
            System.err.println("Błąd eksportu do pliku tekstowego: " + e.getMessage());
        }
    }

    /**
     * Importuje pozycje biblioteczne z pliku tekstowego CSV.
     * Nowe pozycje są dodawane, a istniejące pomijane.
     *
     * @param filePath ścieżka do pliku tekstowego.
     */
    public static void importFromTxt(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // pominięcie nagłówka
            int added = 0, skipped = 0;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 8) {
                    String type = data[0];
                    String uuidStr = data[1];
                    String title = data[2];
                    String author = data[3];
                    int year = Integer.parseInt(data[4]);
                    String genre = data[5];
                    if ("Book".equals(type)) {
                        UUID uuid = UUID.fromString(uuidStr);
                        if (ItemDao.getByUuid(uuid).isEmpty()) {
                            Book b = new Book(uuid, title, year, author, genre, true, null);
                            ItemDao.save(b);
                            added++;
                        } else {
                            skipped++;
                        }
                    }
                }
            }
            System.out.printf("Zaimportowano %d nowych książek (pominięto %d istniejących) z pliku tekstowego: %s\n",
                    added, skipped, filePath);
        } catch (IOException e) {
            System.err.println("Błąd importu z pliku tekstowego: " + e.getMessage());
        }
    }
}
