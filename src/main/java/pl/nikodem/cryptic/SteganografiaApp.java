import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

// glowna klasa aplikacji, dziedziczy po JFrame zeby sama z siebie byc oknem
public class SteganografiaApp extends JFrame {

    // uzywam CardLayout zeby podmieniac panele (menu, kodowanie, dekodowanie) w jednym oknie, tak jest po prostu wygodniej
    private CardLayout cardLayout;
    private JPanel mainContainer;

    // referencje do elementow GUI z ekranu kodowania zeby moc je potem aktualizowac
    private JLabel encodeOriginalLabel;
    private JLabel encodeResultLabel;
    private JTextArea encodeTextArea;
    private BufferedImage imageToEncode;
    private BufferedImage encodedImage;

    // to samo dla ekranu dekodowania
    private JLabel decodeImageLabel;
    private JTextArea decodeTextArea;
    private BufferedImage imageToDecode;

    public SteganografiaApp() {
        setTitle("Steganografia Obrazowa LSB - Projekt Zaliczeniowy");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // to wrzuca okno na srodek ekranu przy odpaleniu

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // tworze osobne panele dla kazdego widoku
        JPanel menuPanel = createMenuPanel();
        JPanel encodePanel = createEncodePanel();
        JPanel decodePanel = createDecodePanel();

        // wrzucam je do glownego kontenera i daje im nazwy zeby moc sie do nich odwolac
        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(encodePanel, "ENCODE");
        mainContainer.add(decodePanel, "DECODE");

        add(mainContainer);

        // na start uzytkownik widzi tylko menu
        cardLayout.show(mainContainer, "MENU");
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Wybierz tryb pracy:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnGoToEncode = new JButton("1. Zakoduj nową wiadomość");
        btnGoToEncode.setFont(new Font("Arial", Font.PLAIN, 18));
        btnGoToEncode.setPreferredSize(new Dimension(300, 50));

        JButton btnGoToDecode = new JButton("2. Rozkoduj z pliku");
        btnGoToDecode.setFont(new Font("Arial", Font.PLAIN, 18));
        btnGoToDecode.setPreferredSize(new Dimension(300, 50));

        JButton btnExit = new JButton("3. Wyjście");
        btnExit.setFont(new Font("Arial", Font.PLAIN, 18));
        btnExit.setPreferredSize(new Dimension(300, 50));

        // akcje na przyciskach podmieniaja nam widoczne karty i przy okazji czyszcza smieci z poprzednich operacji
        btnGoToEncode.addActionListener(e -> {
            resetEncodePanel();
            cardLayout.show(mainContainer, "ENCODE");
        });

        btnGoToDecode.addActionListener(e -> {
            resetDecodePanel();
            cardLayout.show(mainContainer, "DECODE");
        });

        btnExit.addActionListener(e -> System.exit(0));

        panel.add(titleLabel, gbc);
        panel.add(btnGoToEncode, gbc);
        panel.add(btnGoToDecode, gbc);
        panel.add(btnExit, gbc);

        return panel;
    }

    private JPanel createEncodePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton btnBack = new JButton("<< Powrót do menu");
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));
        topPanel.add(btnBack, BorderLayout.WEST);
        topPanel.add(new JLabel("TRYB KODOWANIA (Ukrywanie tekstu)", SwingConstants.CENTER), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // grid zeby miec dwa obrazki ladnie obok siebie
        JPanel imagesPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Obraz oryginalny", SwingConstants.CENTER), BorderLayout.NORTH);
        encodeOriginalLabel = new JLabel("Brak obrazu", SwingConstants.CENTER);
        encodeOriginalLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        leftPanel.add(encodeOriginalLabel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Obraz zakodowany (wynik)", SwingConstants.CENTER), BorderLayout.NORTH);
        encodeResultLabel = new JLabel("Brak obrazu", SwingConstants.CENTER);
        encodeResultLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        rightPanel.add(encodeResultLabel, BorderLayout.CENTER);

        imagesPanel.add(leftPanel);
        imagesPanel.add(rightPanel);
        panel.add(imagesPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        encodeTextArea = new JTextArea(3, 50);
        encodeTextArea.setLineWrap(true);
        encodeTextArea.setBorder(BorderFactory.createTitledBorder("Tekst do ukrycia:"));
        bottomPanel.add(new JScrollPane(encodeTextArea), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnLoad = new JButton("Wczytaj obraz (PNG)");
        JButton btnEncode = new JButton("ZAKODUJ");
        btnEncode.setBackground(new Color(150, 255, 150));
        JButton btnSave = new JButton("Zapisz wynik (PNG)");

        // delegowanie zdarzen do osobnych metod zeby tu nie zrobic spaghetti kodu
        btnLoad.addActionListener(e -> loadImageForEncoding());
        btnEncode.addActionListener(e -> performEncoding());
        btnSave.addActionListener(e -> saveEncodedImage());

        btnPanel.add(btnLoad);
        btnPanel.add(btnEncode);
        btnPanel.add(btnSave);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDecodePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton btnBack = new JButton("<< Powrót do menu");
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));
        topPanel.add(btnBack, BorderLayout.WEST);
        topPanel.add(new JLabel("TRYB DEKODOWANIA (Odczytywanie z pliku)", SwingConstants.CENTER), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        decodeImageLabel = new JLabel("Brak wczytanego obrazu", SwingConstants.CENTER);
        decodeImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        centerPanel.add(decodeImageLabel, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        decodeTextArea = new JTextArea(4, 50);
        decodeTextArea.setLineWrap(true);
        // blokuje pole zeby uzytkownik nie pisal glupot w miejscu na odczyt
        decodeTextArea.setEditable(false);
        decodeTextArea.setBorder(BorderFactory.createTitledBorder("Odczytana wiadomość:"));
        bottomPanel.add(new JScrollPane(decodeTextArea), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnLoad = new JButton("Wczytaj zakodowany obraz (PNG)");
        JButton btnDecode = new JButton("ROZKODUJ");
        btnDecode.setBackground(new Color(255, 200, 150));

        btnLoad.addActionListener(e -> loadImageForDecoding());
        btnDecode.addActionListener(e -> performDecoding());

        btnPanel.add(btnLoad);
        btnPanel.add(btnDecode);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // metody do zerowania interfejsu przy skakaniu pomiedzy oknami
    private void resetEncodePanel() {
        imageToEncode = null;
        encodedImage = null;
        encodeOriginalLabel.setIcon(null);
        encodeOriginalLabel.setText("Brak obrazu");
        encodeResultLabel.setIcon(null);
        encodeResultLabel.setText("Brak obrazu");
        encodeTextArea.setText("");
    }

    private void resetDecodePanel() {
        imageToDecode = null;
        decodeImageLabel.setIcon(null);
        decodeImageLabel.setText("Brak wczytanego obrazu");
        decodeTextArea.setText("");
    }

    private void loadImageForEncoding() {
        JFileChooser chooser = new JFileChooser();
        // zmuszamy uzytkownika do png bo kompresja w jpg zniszczy modyfikacje LSB
        chooser.setFileFilter(new FileNameExtensionFilter("Obrazy PNG", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                imageToEncode = ImageIO.read(chooser.getSelectedFile());
                // skaluje to tylko wizualnie zeby nie wywalilo mi layoutu okna przy zdjeciach 4k
                Image scaled = imageToEncode.getScaledInstance(350, 250, Image.SCALE_SMOOTH);
                encodeOriginalLabel.setIcon(new ImageIcon(scaled));
                encodeOriginalLabel.setText("");

                // kasuje prawy podglad na wszelki wypadek
                encodedImage = null;
                encodeResultLabel.setIcon(null);
                encodeResultLabel.setText("Brak obrazu");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd odczytu pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performEncoding() {
        if (imageToEncode == null) {
            JOptionPane.showMessageDialog(this, "Wczytaj najpierw plik!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String text = encodeTextArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wpisz wiadomość!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // doklejam znak null (\0) na koniec zeby przy dekodowaniu wiedziec kiedy skonczyc czytac piksele
        String msg = text + "\0";
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        int totalBits = msgBytes.length * 8;

        // metoda lsb to 1 bit na piksel wiec pojemnosc obrazka to po prostu szerokosc * wysokosc
        if (totalBits > imageToEncode.getWidth() * imageToEncode.getHeight()) {
            JOptionPane.showMessageDialog(this, "Tekst jest za dlugi jak na taki maly obrazek!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // kopiuje oryginalny obraz na nowy zeby na nim robic modyfikacje bitowe
        encodedImage = new BufferedImage(imageToEncode.getWidth(), imageToEncode.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = encodedImage.getGraphics();
        g.drawImage(imageToEncode, 0, 0, null);
        g.dispose();

        int bitIndex = 0;
        outer: // etykieta do petli zebym mogl z niej uciec w calosci jak juz zaszyfruje caly tekst
        for (int y = 0; y < encodedImage.getHeight(); y++) {
            for (int x = 0; x < encodedImage.getWidth(); x++) {
                if (bitIndex >= totalBits) break outer;

                // obliczanie z ktorego bajtu we wiadomosci wyciagamy aktualnie bit
                int byteIndex = bitIndex / 8;
                int bitPos = bitIndex % 8;

                // to przesuniecie w prawo i maska & 1 po prostu daje mi wartosc tego konkretnego bitu (0 lub 1)
                int bit = (msgBytes[byteIndex] >> (7 - bitPos)) & 1;

                int rgb = encodedImage.getRGB(x, y);
                // tu sie dzieje cala magia LSB
                // maska 0xFFFFFFFE zostawia wszystko z inta poza absolutnie najmniej znaczacym bitem, ktory ustawia na zero
                // potem OR (|) wstawia w to miejsce nasz bit z tekstu
                int newRgb = (rgb & 0xFFFFFFFE) | bit;
                encodedImage.setRGB(x, y, newRgb);

                bitIndex++;
            }
        }

        Image scaled = encodedImage.getScaledInstance(350, 250, Image.SCALE_SMOOTH);
        encodeResultLabel.setIcon(new ImageIcon(scaled));
        encodeResultLabel.setText("");
        JOptionPane.showMessageDialog(this, "Zakodowano pomyślnie. Pamiętaj by zapisać plik!");
    }

    private void saveEncodedImage() {
        if (encodedImage == null) {
            JOptionPane.showMessageDialog(this, "Nie ma nic do zapisania.", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Obrazy PNG", "png"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            // poprawianie rozszerzenia jak uzytkownik zapomni dopisac
            if (!f.getName().toLowerCase().endsWith(".png")) {
                f = new File(f.getParentFile(), f.getName() + ".png");
            }
            try {
                ImageIO.write(encodedImage, "png", f);
                JOptionPane.showMessageDialog(this, "Plik zapisany!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Wywaliło błąd przy zapisie.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadImageForDecoding() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Obrazy PNG", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                imageToDecode = ImageIO.read(chooser.getSelectedFile());
                Image scaled = imageToDecode.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                decodeImageLabel.setIcon(new ImageIcon(scaled));
                decodeImageLabel.setText("");
                decodeTextArea.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd odczytu pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performDecoding() {
        if (imageToDecode == null) {
            JOptionPane.showMessageDialog(this, "Wczytaj najpierw plik z ukrytym tekstem!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Byte> decodedBytes = new ArrayList<>();
        int currentByte = 0;
        int bitCount = 0;

        outer:
        for (int y = 0; y < imageToDecode.getHeight(); y++) {
            for (int x = 0; x < imageToDecode.getWidth(); x++) {

                int rgb = imageToDecode.getRGB(x, y);
                // wyciagam ten ostatni bit z piksela
                int lsb = rgb & 1;

                // doklejam go do aktualnie skladanego bajtu
                currentByte = (currentByte << 1) | lsb;
                bitCount++;

                // jak zlozylem 8 bitow to znaczy ze mam gotowy caly bajt (znak)
                if (bitCount == 8) {
                    // jezeli udalo sie zczytac cale zera to znaczy ze to byl ten nasz terminator dodany na etapie kodowania
                    // czyli przerywamy bo nie ma juz wiecej sensownego tekstu w pikselach
                    if (currentByte == 0) break outer;

                    decodedBytes.add((byte) currentByte);
                    // resetuje liczniki na kolejny znak
                    currentByte = 0;
                    bitCount = 0;
                }
            }
        }

        if (decodedBytes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nie znaleziono ukrytego tekstu (albo próbujesz na czystym pliku).", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // przepisywanie tablicy bo List<Byte> nie wchodzi tak latwo do stringa
        byte[] result = new byte[decodedBytes.size()];
        for (int i = 0; i < decodedBytes.size(); i++) {
            result[i] = decodedBytes.get(i);
        }

        decodeTextArea.setText(new String(result, StandardCharsets.UTF_8));
        JOptionPane.showMessageDialog(this, "Tekst odczytany!");
    }

    public static void main(String[] args) {
        // standardowa zagrywka dla okienek w javie zeby nie zablokowac watku glownego
        SwingUtilities.invokeLater(() -> {
            new SteganografiaApp().setVisible(true);
        });
    }
}