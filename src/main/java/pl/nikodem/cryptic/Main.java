package pl.nikodem.cryptic;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //Inicjalizacja narzędzi
        ImageProcessor processor = new ImageProcessor();
        SteganoEngine engine = new SteganoEngine();

        // Ścieżki do plików (Zmień na swoje!)
        String inputPath = "C:\\Users\\nikod\\Desktop\\Döner_kebab.png";
        String outputPath = "C:\\Users\\nikod\\Desktop\\secret_kebab.png";
        String secretMessage = "Tajny projekt CrypticPixell - misja zaliczenie!";

        try {
            System.out.println("--- ROZPOCZYNAMY KODOWANIE ---");

            // Wczytujemy oryginał
            BufferedImage originalImage = processor.loadImage(inputPath);
            System.out.println("Pomyślnie wczytano obrazek: " + originalImage.getWidth() + "x" + originalImage.getHeight());

            // Kodujemy wiadomość
            System.out.println("Koduję wiadomość: " + secretMessage);
            BufferedImage encodedImage = engine.encode(originalImage, secretMessage);

            // Zapisujemy wynik
            processor.saveImage(encodedImage, outputPath);
            System.out.println("--- KODOWANIE ZAKOŃCZONE ---");

            System.out.println("\n--- ROZPOCZYNAMY DEKODOWANIE ---");

            // Wczytujemy ZAPISANY obrazek (to ważne, by sprawdzić czy zapis nie uszkodził bitów)
            BufferedImage toDecode = processor.loadImage(outputPath);

            // Odczytujemy tajemnicę
            String decodedMessage = engine.decode(toDecode);
            System.out.println("Odczytana wiadomość: " + decodedMessage);

            // Weryfikacja
            if (secretMessage.equals(decodedMessage)) {
                System.out.println("\n✅ SUKCES! Wiadomość została odzyskana w 100%.");
            } else {
                System.out.println("\n❌ BŁĄD! Coś poszło nie tak z bitami.");
            }

        } catch (IOException e) {
            System.err.println("Problem z plikiem: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Nieoczekiwany błąd: " + e.getMessage());
            e.printStackTrace();
        }
    }
}