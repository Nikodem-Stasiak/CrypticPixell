# CrypticPixell 🕵️‍♂️🖼️

**CrypticPixell** to narzędzie do steganografii napisane w języku Java, które pozwala na ukrywanie tajnych wiadomości tekstowych wewnątrz obrazów w formacie PNG bez widocznej zmiany ich wyglądu.

## 🚀 Funkcje
* **Kodowanie wiadomości:** Ukrywanie tekstu w pikselach obrazu.
* **Dekodowanie wiadomości:** Odczytywanie ukrytych danych z "zatrutych" obrazów.
* **Bezstratność:** Wykorzystanie formatu PNG, aby zapewnić integralność danych.
* **Interaktywne Menu:** Łatwa obsługa z poziomu konsoli (CLI).
* **Bezpieczeństwo bitowe:** Implementacja autorskiego algorytmu LSB.

## 🛠️ Technologia i Algorytm
Projekt wykorzystuje technikę **LSB (Least Significant Bit)**. 

### Jak to działa?
1. Program zamienia tekst na ciąg binarny (zera i jedynki).
2. Każdy piksel obrazu (składający się z kanałów Red, Green, Blue) jest modyfikowany.
3. Najmniej znaczący bit (ostatni) każdego kanału koloru jest podmieniany na bit wiadomości.
4. Różnica w kolorze wynosi zaledwie 1/255, co jest **niezauważalne dla ludzkiego oka**.

## 📋 Wymagania
* Java JDK 17 lub nowsza.
* Maven (do zarządzania projektem).

## 💻 Jak uruchomić?
1. Sklonuj repozytorium:
   ```bash
   git clone https://github.com/Nikodem-Stasiak/CrypticPixell.git

2. Przejdź do folderu projektu i zbuduj go:
   ```bash
   mvn clean install

3. Uruchom klasę Main w swoim IDE lub poprzez terminal.

## 📖 Przykład użycia
Po uruchomieniu programu zobaczysz menu:
1. Wybierz opcję 1, aby zakodować wiadomość.
2. Podaj ścieżkę do pliku .png, wpisz tekst i wskaż miejsce zapisu.
3. Wyślij obrazek znajomemu – tylko on (używając opcji 2) będzie mógł odczytać Twoją tajemnicę!

Projekt stworzony w celach edukacyjnych na studia informatyczne.
