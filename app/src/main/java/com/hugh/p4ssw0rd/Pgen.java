package com.hugh.p4ssw0rd;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pgen {

    private static SecureRandom random = new SecureRandom();
    private static String LOGTAG = "Password Generator";

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
