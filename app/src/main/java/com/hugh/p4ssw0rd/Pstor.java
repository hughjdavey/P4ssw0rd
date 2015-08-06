package com.hugh.p4ssw0rd;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Pstor {
    private static final String FILENAME = "db";
    private static final String LOGTAG = "Password Store";
    private Context context;
    private Map<String, Password> passwordMap;

    static byte[] secretKey = "p4ssw0rd12345678".getBytes();
    final static String ALGORITHM = "AES/ECB/PKCS5Padding";

    private Pstor(Context context) {
        this.context = context;
        this.passwordMap = load();
    }

    static Pstor pstor;
    public static synchronized Pstor getPstor(Context context) {
        if (pstor == null) {
            pstor = new Pstor(context);
        }
        return pstor;
    }

    private void addPassword(Password password) {
        String identifier = password.identifier.toLowerCase();
        passwordMap.put(identifier, password);
    }

    public void add(Password... passwords) {
        for (Password password : passwords) {
            addPassword(password);
        }
        save();
    }

    public Password getPassword(String identifier) {
        return passwordMap.get(identifier.toLowerCase());
    }

    public List<Password> getAllPasswords() {
        List<Password> passwords = new ArrayList<>(passwordMap.values());
        return passwords;
    }

    public void removePassword(Password password) {
        String identifier = password.identifier.toLowerCase();
        passwordMap.remove(identifier);
        save();
    }

    public void updatePassword(Password password) {
        removePassword(password);
        addPassword(password);
        save();
    }

    /** loads the map of password objects from disk */
    private Map<String, Password> load() {
        Map<String, Password> passMap;

        try(FileInputStream inputStream = context.openFileInput(FILENAME)) {
            //Cipher cipher = Cipher.getInstance(ALGORITHM);
            //cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, ALGORITHM));

            ObjectInputStream objectIn =
                    new ObjectInputStream(
                            new BufferedInputStream(
                                    new CipherInputStream(inputStream, getCipher(Cipher.DECRYPT_MODE))));

            passMap = (HashMap<String, Password>) objectIn.readObject();
            objectIn.close();
        }
        catch (Exception e) {
            passMap = new HashMap<>();
        }
        return passMap;
    }

    /** saves the map of password objects to disk */
    private boolean save() {
        boolean success = false;

        try(FileOutputStream outputStream = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            //Cipher cipher = Cipher.getInstance(ALGORITHM);
            //cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, ALGORITHM));

            ObjectOutputStream objectOut =
                    new ObjectOutputStream(
                            new BufferedOutputStream(
                                    new CipherOutputStream(outputStream, getCipher(Cipher.ENCRYPT_MODE))));

            objectOut.writeObject(this.passwordMap);
            success = true;
        }
        catch (Exception e) {
            success = false;
        }
        return success;
    }

    // TODO switch to CBC mode
    // TODO generate secretkey based on master password
    private Cipher getCipher(int mode) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, getSecretKey());
        }
        catch (GeneralSecurityException gse) {
            Log.e(LOGTAG, "exception getting cipher", gse);
        }
        return cipher;
    }

    private SecretKeySpec getSecretKey() {
        return new SecretKeySpec(secretKey, ALGORITHM);
    }

    static class SessionIv {
        private byte[] iv;
        private static SecureRandom random = new SecureRandom();

        public SessionIv() {
            this.iv = generateIv();
        }

        public IvParameterSpec getIv() {
            return new IvParameterSpec(this.iv);
        }

        private byte[] generateIv() {
            byte bytes[] = new byte[16];
            random.nextBytes(bytes);
            return bytes;
        }
    }
}
