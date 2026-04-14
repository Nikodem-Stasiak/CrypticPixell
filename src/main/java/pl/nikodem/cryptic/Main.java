package pl.nikodem.cryptic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ImageProcessor processor = new ImageProcessor();
        SteganoEngine engine = new SteganoEngine();

        System.out.println("--- WITAJ W CRYPTIC PIXELL ---");

        while (true) {
            System.out.println("\nWYBIERZ OPCJĘ:");
            System.out.println("1. Zakoduj wiadomość w obrazie");
            System.out.println("2. Odczytaj wiadomość z obrazu");
            System.out.println("0. Wyjdź");
            System.out.print("Wybór: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                handleEncoding(scanner, processor, engine);
            } else if (choice.equals("2")) {
                handleDecoding(scanner, processor, engine);
            } else if (choice.equals("0")) {
                System.out.println("Zamykanie programu. Do zobaczenia!");
                break;
            } else {
                System.out.println("Nieprawidłowy wybór, spróbuj ponownie.");
            }
        }
        scanner.close();
    }

    private static void handleEncoding(Scanner scanner, ImageProcessor processor, SteganoEngine engine) {
        try {
            System.out.print("Podaj ścieżkę do oryginalnego obrazu (PNG): ");
            String inputPath = scanner.nextLine().replace("\"", ""); // Usuwa cudzysłowy, jeśli ktoś przeciągnął plik

            System.out.print("Podaj wiadomość do ukrycia: ");
            String message = scanner.nextLine();

            System.out.print("Podaj ścieżkę zapisu (np. C:/Users/Desktop/output.png): ");
            String outputPath = scanner.nextLine().replace("\"", "");

            BufferedImage original = processor.loadImage(inputPath);
            BufferedImage encoded = engine.encode(original, message);
            processor.saveImage(encoded, outputPath);

            System.out.println("✅ Sukces! Wiadomość została ukryta.");
        } catch (IOException e) {
            System.err.println("❌ Błąd: " + e.getMessage());
        }
    }

    private static void handleDecoding(Scanner scanner, ImageProcessor processor, SteganoEngine engine) {
        try {
            System.out.print("Podaj ścieżkę do obrazu z ukrytą wiadomością (PNG): ");
            String path = scanner.nextLine().replace("\"", "");

            BufferedImage image = processor.loadImage(path);
            String message = engine.decode(image);

            System.out.println("\n----------------------------------");
            System.out.println("ODCZYTANA WIADOMOŚĆ: " + message);
            System.out.println("----------------------------------");
        } catch (IOException e) {
            System.err.println("❌ Błąd: " + e.getMessage());
        }
    }
}