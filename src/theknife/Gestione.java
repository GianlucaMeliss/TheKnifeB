/*
 * Nome: Alessandro
 * Cognome: Melnyk
 * Matricola:761001
 * Sede: VA
 *
 * Nome: Gianluca
 * Cognome: Melis
 * Matricola:761289
 * Sede: VA
 *
 * Nome: Simone
 * Cognome: Zamberletti
 * Matricola:761355
 * Sede: VA
 *
 * Nome: Davide
 * Cognome: Redemagni
 * Matricola:760043
 * Sede: VA
 */
package theknife;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.FileReader;
import java.lang.reflect.Type;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Classe contenitore per diverse classi di utilità usate nell'applicazione.
 */
public class Gestione {

    // --- CLASSE AGGIUNTA PER GESTIRE LE DATE ---
    /**
     * Adattatore per {@link LocalDate}, usato da Gson per serializzare e
     * deserializzare le date nel formato ISO.
     */
    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

        /**
         * Serializza un oggetto {@link LocalDate} in JSON.
         *
         * @param out   writer JSON
         * @param value valore da scrivere
         * @throws IOException se si verifica un errore di scrittura
         */
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(FORMATTER.format(value));
            }
        }

        /**
         * Deserializza un valore JSON in un oggetto {@link LocalDate}.
         *
         * @param in reader JSON
         * @return oggetto {@link LocalDate} o {@code null} se il valore era nullo
         * @throws IOException se si verifica un errore di lettura
         */
        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else {
                return LocalDate.parse(in.nextString(), FORMATTER);
            }
        }
    }

    //<editor-fold desc="Codice non modificato - Sort e CifraturaUtils">
    /**
     * Classe astratta che fornisce un'implementazione generica del merge sort
     * per ordinare liste di {@link Ristorante}.
     */
    public static abstract class Sort<T> {
        /**
         * Ordina una lista di ristoranti usando il merge sort.
         *
         * @param list       lista da ordinare
         * @param comparator comparatore da utilizzare
         */
        public static <T> void mergeSort(ArrayList<Ristorante> list, Comparator<Ristorante> comparator) {
            if (list.size() <= 1) return;
            int mid = list.size() / 2;
            ArrayList<Ristorante> left = new ArrayList<>(list.subList(0, mid));
            ArrayList<Ristorante> right = new ArrayList<>(list.subList(mid, list.size()));
            mergeSort(left, comparator);
            mergeSort(right, comparator);
            merge(list, left, right, comparator);
        }

        /**
         * Unisce due sottoliste ordinate in una lista finale ordinata.
         *
         * @param destination lista di destinazione
         * @param left        sottolista sinistra ordinata
         * @param right       sottolista destra ordinata
         * @param comparator  comparatore per l'ordinamento
         */
        private static <T> void merge(ArrayList<Ristorante> destination, ArrayList<Ristorante> left, ArrayList<Ristorante> right, Comparator<Ristorante> comparator) {
            destination.clear();
            int i = 0, j = 0;
            while (i < left.size() && j < right.size()) {
                Ristorante l = left.get(i);
                Ristorante r = right.get(j);
                if (comparator.compare(l, r) <= 0) {
                    destination.add(l);
                    i++;
                } else {
                    destination.add(r);
                    j++;
                }
            }
            while (i < left.size()) destination.add(left.get(i++));
            while (j < right.size()) destination.add(right.get(j++));
        }
    }

    /**
     * Classe di utilità per cifrare e decifrare testi usando l'algoritmo AES.
     */
    public static class CifraturaUtils {
        private static final String CHIAVE = "1234567890123456";

        /**
         * Cripta una stringa in chiaro con AES.
         *
         * @param testoChiaro stringa da criptare
         * @return testo criptato in Base64
         * @throws Exception se si verifica un errore di cifratura
         */
        public static String cripta(String testoChiaro) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec chiaveSpec = new SecretKeySpec(CHIAVE.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, chiaveSpec);
            byte[] encryptedBytes = cipher.doFinal(testoChiaro.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        /**
         * Decripta una stringa cifrata con AES.
         *
         * @param testoCriptato stringa criptata in Base64
         * @return testo originale in chiaro
         * @throws Exception se si verifica un errore di decifratura
         */
        public static String decripta(String testoCriptato) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec chiaveSpec = new SecretKeySpec(CHIAVE.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, chiaveSpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(testoCriptato));
            return new String(decryptedBytes);
        }
    }
    //</editor-fold>

    /**
     * Classe di utilità per leggere oggetti da file JSON.
     */
    public static class Deserializer {
        /**
         * Deserializza una lista di oggetti da un file JSON.
         *
         * @param filePath percorso del file JSON
         * @param clazz    classe degli oggetti da deserializzare
         * @param adapter  adattatore personalizzato per la deserializzazione
         * @param <T>      tipo dell'oggetto
         * @return lista di oggetti oppure lista vuota in caso di errore
         */
        public static <T> ArrayList<T> fromJsonFile(String filePath, Class<T> clazz, JsonDeserializer<T> adapter) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(clazz, adapter)
                    .create();
            try (FileReader reader = new FileReader(filePath)) {
                Type listType = TypeToken.getParameterized(ArrayList.class, clazz).getType();
                ArrayList<T> result = gson.fromJson(reader, listType);
                return result == null ? new ArrayList<>() : result;
            } catch (Exception e) {
                System.err.println("Errore durante la deserializzazione: " + e.getMessage());
                return new ArrayList<>();
            }
        }

        /**
         * Deserializza un oggetto generico da un file JSON.
         *
         * @param filePath percorso del file JSON
         * @param typeOfT  tipo dell'oggetto
         * @param <T>      tipo dell'oggetto
         * @return oggetto deserializzato o {@code null} in caso di errore
         */
        public static <T> T fromJsonFile(String filePath, Type typeOfT) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            try (FileReader reader = new FileReader(filePath)) {
                return gson.fromJson(reader, typeOfT);
            } catch (Exception e) {
                System.err.println("Errore durante la deserializzazione: " + e.getMessage());
                return null;
            }
        }
    }

    /**
     * Classe di utilità per scrivere oggetti in file JSON.
     */
    public static class Serializer {
        /**
         * Serializza un oggetto in un file JSON.
         *
         * @param filePath  percorso del file JSON
         * @param obj       oggetto da serializzare
         * @param typeOfObj tipo dell'oggetto
         * @param <T>       tipo dell'oggetto
         */
        public static <T> void toJsonFile(String filePath, T obj, Type typeOfObj) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .setPrettyPrinting()
                    .create();
            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(obj, typeOfObj, writer);
            } catch (Exception e) {
                System.err.println("Errore durante la serializzazione: " + e.getMessage());
            }
        }
    }
}
