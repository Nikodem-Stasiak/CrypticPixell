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

                //Wyciągamy składowe
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                //Wyciągamy ostatnie bity (LSB) i dodajemy do wyniku
                binaryResult.append(red & 1);
                binaryResult.append(green & 1);
                binaryResult.append(blue & 1);

                // Sprawdzamy, czy mamy już "znacznik końca" (nasze 16 zer)
                // Robimy to co jakiś czas, żeby nie obciążać procesora,
                // albo po prostu na końcu każdego piksela.
                if (binaryResult.length() >= 16) {
                    String last16 = binaryResult.substring(binaryResult.length() - 16);
                    if (last16.equals("0000000000000000")) {
                        // Znaleźliśmy koniec! Wycinamy zera i dekodujemy resztę.
                        String finalBinary = binaryResult.substring(0, binaryResult.length() - 16);
                        return converter.binaryToString(finalBinary);
                    }
                }
            }
        }

        return "Nie znaleziono ukrytej wiadomości.";
    }

    private BufferedImage copyImage(BufferedImage original) {
        BufferedImage copy = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
        copy.getGraphics().drawImage(original, 0, 0, null);
        return copy;
    }
}