package pl.nikodem.cryptic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

/**
 * Klasa Main - punkt wejściowy aplikacji CrypticPixell.
 * Odpowiada za interakcję z użytkownikiem i sterowanie procesami steganograficznymi.
 */
public class Main {
    public static void main(String[] args) {
        // Inicjalizacja skanera do czytania danych z konsoli oraz obiektów procesora i silnika
        Scanner scanner = new Scanner(System.in);
        ImageProcessor processor = new ImageProcessor();
        SteganoEngine engine = new SteganoEngine();

        System.out.println("--- WITAJ W CRYPTIC PIXELL ---");

        // Pętla while(true) pozwala na korzystanie z programu wielokrotnie bez konieczności jego restartu
        while (true) {
            System.out.println("\nWYBIERZ OPCJĘ:");
            System.out.println("1. Zakoduj wiadomość w obrazie");
            System.out.println("2. Odczytaj wiadomość z obrazu");
            System.out.println("0. Wyjdź");
            System.out.print("Wybór: ");

            // Czytamy wybór jako String, co jest bezpieczniejsze przy obsłudze enterów w konsoli
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                handleEncoding(scanner, processor, engine);
            } else if (choice.equals("2")) {
                handleDecoding(scanner, processor, engine);
            } else if (choice.equals("0")) {
                System.out.println("Zamykanie programu. Do zobaczenia!");
                break; // Wychodzi z pętli, co kończy działanie metody main
            } else {
                System.out.println("Nieprawidłowy wybór, spróbuj ponownie.");
            }
        }
        scanner.close(); // Zamykanie zasobów na koniec pracy programu
    }

    /**
     * Obsługuje proces pobierania danych od użytkownika i wywoływania kodowania.
     */
    private static void handleEncoding(Scanner scanner, ImageProcessor processor, SteganoEngine engine) {
        try {
            System.out.print("Podaj ścieżkę do oryginalnego obrazu (PNG): ");
            // replace("\"", "") to "bezpiecznik" - usuwa cudzysłowy, jeśli użytkownik przeciągnął plik do konsoli
            String inputPath = scanner.nextLine().replace("\"", "");

            System.out.print("Podaj wiadomość do ukrycia: ");
            String message = scanner.nextLine();

            System.out.print("Podaj ścieżkę zapisu (np. C:/Users/Desktop/output.png): ");
            String outputPath = scanner.nextLine().replace("\"", "");

            // Logika: Wczytaj -> Zakoduj w pamięci -> Zapisz na dysk
            BufferedImage original = processor.loadImage(inputPath);
            BufferedImage encoded = engine.encode(original, message);
            processor.saveImage(encoded, outputPath);

            System.out.println("✅ Sukces! Twoja wiadomość została bezpiecznie ukryta.");
        } catch (IOException e) {
            // Jeśli plik nie istnieje lub ścieżka jest błędna, wyświetlamy czytelny błąd zamiast "wywalać" program
            System.err.println("❌ Błąd operacji na pliku: " + e.getMessage());
        }
    }

    /**
     * Obsługuje proces pobierania ścieżki i wywoływania dekodowania wiadomości.
     */
    private static void handleDecoding(Scanner scanner, ImageProcessor processor, SteganoEngine engine) {
        try {
            System.out.print("Podaj ścieżkę do obrazu z ukrytą wiadomością (PNG): ");
            String path = scanner.nextLine().replace("\"", "");

            // Logika: Wczytaj -> Wyciągnij bity i zamień na tekst
            BufferedImage image = processor.loadImage(path);
            String message = engine.decode(image);

            System.out.println("\n----------------------------------");
            System.out.println("ODCZYTANA WIADOMOŚĆ: " + message);
            System.out.println("----------------------------------");
        } catch (IOException e) {
            System.err.println("❌ Błąd podczas odczytu: " + e.getMessage());
        }
    }
}