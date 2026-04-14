package pl.nikodem.cryptic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Klasa odpowiedzialna za wczytywanie i zapisywanie obrazów.
 */
public class ImageProcessor {

    /**
     * Wczytuje obraz z podanej ścieżki.
     * @param path Ścieżka do pliku (np. "C:/users/obrazek.png")
     * @return Obiekt BufferedImage reprezentujący siatkę pikseli
     * @throws IOException Gdy plik nie istnieje lub jest uszkodzony
     */
    public BufferedImage loadImage(String path) throws IOException {
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);
        if(image==null){
            throw new IOException("Wskazany plik nie jest obrazem lub formta nie jest obsługiwany");
        }

        return image;
    }

    /**
     * Zapisuje obraz na dysku w formacie PNG.
     * @param image Obraz do zapisania
     * @param path Ścieżka, gdzie ma zostać zapisany
     * @throws IOException Gdy wystąpi błąd zapisu
     */
    public void saveImage(BufferedImage image, String path) throws IOException {
        File outputFile = new File(path);
        boolean result = ImageIO.write(image, "png", outputFile);
        if (result) {
            System.out.println("Obraz został pomyślnie zapisany pod ścieżką: " + path);
        } else {
            throw new IOException("Nie udało się znaleźć odpowiedniego writera dla formatu PNG!");
        }
    }
}