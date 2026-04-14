package pl.nikodem.cryptic;

/**
 * Klasa odpowiedzialna za tłumaczenie tekstu na bity i odwrotnie.
 * Każdy znak (char) musi być reprezentowany przez dokładnie 8 bitów.
 */

public class BinaryConverter {

    /**
     * Zamienia czytelny tekst na ciąg zer i jedynek.
     * @param text np. "ABC"
     * @return np. "010000010100001001000011"
     */

    public String stringToBinary(String text) {
            StringBuilder binary = new StringBuilder();

            for(char c : text.toCharArray()){
                String binaryChar = Integer.toBinaryString(c);
                String padded = String.format("%8s",binaryChar).replace(' ', '0');
                binary.append(padded);
            }

        return binary.toString();
    }

    /**
     * Zamienia ciąg zer i jedynek z powrotem na tekst.
     * @param binary Ciąg zer i jedynek (długość musi być podzielna przez 8)
     * @return Czytelny tekst
     */
    public String binaryToString(String binary) {
        StringBuilder text = new StringBuilder();
        for(int i = 0; i< binary.length(); i+=8){
            String chunk = binary.substring(i, i+8);
            int charCode = Integer.parseInt(chunk,2);
            text.append((char) charCode);
        }

        return text.toString();
    }
}