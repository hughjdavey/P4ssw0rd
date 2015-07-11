package com.hugh.p4ssw0rd;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pgen {

    private static SecureRandom random = new SecureRandom();
    private static String LOGTAG = "Password Generator";

    /**
     * Enumeration of the three supported character types
     */
    public enum Chartype {
        LETTERS("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),
        NUMBERS("0123456789"),
        SYMBOLS("!£$%^&*()_-+={}[];'#:@~<>?,./|¬`\"");
        Set<Character> charset;

        Chartype(String chars) {
            charset = new HashSet<>();
            for (char c : chars.toCharArray()) {
                charset.add(c);
            }
        }
    }

    /**
     * Generate a random password
     *
     * There are more letters than anything else so to avoid a letter-heavy password we randomly
     * select a chartype for each character rather than combining all chartypes into one set,
     * which can and did produce 'alphanumeric' passwords consisting solely of letters
     *
     * @param size user selected length
     * @param types user selected character type(s) (from alphabetic, numeric and symbolic)
     * @return random password of specified length using all specified character types
     */
    public static String generatePassword(int size, Set<Chartype> types) {
        StringBuilder password = new StringBuilder();
        while (size > 0) {
            Chartype randomType = (Chartype) types.toArray()[random.nextInt(types.size())];
            List<Character> randomCharset = new ArrayList<>(randomType.charset);
            int randomIndex = random.nextInt(randomCharset.size());
            password.append(randomCharset.get(randomIndex));
            size--;
        }

        return password.toString();
    }
}
