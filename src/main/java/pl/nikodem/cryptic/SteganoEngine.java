package pl.nikodem.cryptic;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SteganoEngine {

    private final BinaryConverter converter = new BinaryConverter();

    /**
     * Ukrywa wiadomość w obrazie.
     */
    public BufferedImage encode(BufferedImage original, String message) {
        // Tłumaczymy tekst na bity i dodajemy 16 zer (znacznik końca)
        String binaryMessage = converter.stringToBinary(message) + "0000000000000000";
        int bitIndex = 0;

        // Klonujemy obrazek
        BufferedImage encoded = copyImage(original);

        // Lecimy po pikselach
        for (int y = 0; y < encoded.getHeight(); y++) {
            for (int x = 0; x < encoded.getWidth(); x++) {

                // Jeśli wciąż mamy bity wiadomości do ukrycia
                if (bitIndex < binaryMessage.length()) {

                    // POBIERAMY obecny kolor
                    int pixel = encoded.getRGB(x, y);

                    // ROZBIJAMY na składowe
                    int alpha = (pixel >> 24) & 0xff;
                    int red   = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue  = (pixel) & 0xff;

                    // MODYFIKUJEMY składowe (każda dostaje 1 bit wiadomości)
                    if (bitIndex < binaryMessage.length()) {
                        red = (red & 0xFE) | (binaryMessage.charAt(bitIndex++) - '0');
                    }
                    if (bitIndex < binaryMessage.length()) {
                        green = (green & 0xFE) | (binaryMessage.charAt(bitIndex++) - '0');
                    }
                    if (bitIndex < binaryMessage.length()) {
                        blue = (blue & 0xFE) | (binaryMessage.charAt(bitIndex++) - '0');
                    }

                    // SKŁADAMY piksel w jedną całość
                    int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;

                    // ZAPISUJEMY nowy piksel do obrazka
                    encoded.setRGB(x, y, newPixel);
                }
            }
        }
        return encoded;
    }

    /**
     * Wyciąga wiadomość z obrazu.
     */
    public String decode(BufferedImage image) {
        StringBuilder binaryResult = new StringBuilder();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);

                // Pobieramy kanały
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                // Dodajemy bity JEDEN PO DRUGIM i od razu sprawdzamy znacznik
                if (addBitAndCheckMarker(binaryResult, red & 1)) return decodeResult(binaryResult);
                if (addBitAndCheckMarker(binaryResult, green & 1)) return decodeResult(binaryResult);
                if (addBitAndCheckMarker(binaryResult, blue & 1)) return decodeResult(binaryResult);
            }
        }

        return "Nie znaleziono ukrytej wiadomości.";
    }

    // Pomocnicza metoda, żeby kod był czystszy
    private boolean addBitAndCheckMarker(StringBuilder builder, int bit) {
        builder.append(bit);
        if (builder.length() >= 16) {
            String last16 = builder.substring(builder.length() - 16);
            return last16.equals("0000000000000000");
        }
        return false;
    }

    // Pomocnicza metoda do finalnego wycięcia tekstu
    private String decodeResult(StringBuilder builder) {
        String finalBinary = builder.substring(0, builder.length() - 16);
        return converter.binaryToString(finalBinary);
    }

    private BufferedImage copyImage(BufferedImage original) {
        BufferedImage copy = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
        copy.getGraphics().drawImage(original, 0, 0, null);
        return copy;
    }
}