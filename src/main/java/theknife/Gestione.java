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

import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Classe di utilità per cifratura AES e algoritmi di supporto.
 */
public class Gestione {

    public static abstract class Sort<T> {
        public static <T> void mergeSort(ArrayList<Ristorante> list, Comparator<Ristorante> comparator) {
            if (list.size() <= 1) return;
            int mid = list.size() / 2;
            ArrayList<Ristorante> left = new ArrayList<>(list.subList(0, mid));
            ArrayList<Ristorante> right = new ArrayList<>(list.subList(mid, list.size()));
            mergeSort(left, comparator);
            mergeSort(right, comparator);
            merge(list, left, right, comparator);
        }

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

    public static class CifraturaUtils {
        private static final String CHIAVE = "1234567890123456";

        public static String cripta(String testoChiaro) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec chiaveSpec = new SecretKeySpec(CHIAVE.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, chiaveSpec);
            byte[] encryptedBytes = cipher.doFinal(testoChiaro.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        public static String decripta(String testoCriptato) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec chiaveSpec = new SecretKeySpec(CHIAVE.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, chiaveSpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(testoCriptato));
            return new String(decryptedBytes);
        }
    }
}