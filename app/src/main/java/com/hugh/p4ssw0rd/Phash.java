package com.hugh.p4ssw0rd;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

/*
 * PBKDF2 salted password hashing.
 * Author: havoc AT defuse.ca
 * www: http://crackstation.net/hashing-security.htm
 */
public class Phash {
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    // The following constants may be changed without breaking existing hashes.
    public static final int SALT_BYTE_SIZE = 24;
    public static final int HASH_BYTE_SIZE = 24;
    public static final int PBKDF2_ITERATIONS = 1000;

    public static final int ITERATION_INDEX = 0;
    public static final int SALT_INDEX = 1;
    public static final int PBKDF2_INDEX = 2;

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param password the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    private static String createHash(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return createHash(password.toCharArray());
    }

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param password the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    private static String createHash(char[] password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
        // format iterations:salt:hash
        return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" + toHex(hash);
    }

    /**
     * Validates a password using a hash.
     *
     * @param password    the password to check
     * @param correctHash the hash of the valid password
     * @return true if the password is correct, false if not
     */
    private static boolean validatePassword(String password, String correctHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return validatePassword(password.toCharArray(), correctHash);
    }

    /**
     * Validates a password using a hash.
     *
     * @param password    the password to check
     * @param correctHash the hash of the valid password
     * @return true if the password is correct, false if not
     */
    private static boolean validatePassword(char[] password, String correctHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Decode the hash into its parameters
        String[] params = correctHash.split(":");
        int iterations = Integer.parseInt(params[ITERATION_INDEX]);
        byte[] salt = fromHex(params[SALT_INDEX]);
        byte[] hash = fromHex(params[PBKDF2_INDEX]);
        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     *
     * @param a the first byte array
     * @param b the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    /**
     * Computes the PBKDF2 hash of a password.
     *
     * @param password   the password to hash.
     * @param salt       the salt
     * @param iterations the iteration count (slowness factor)
     * @param bytes      the length of the hash to compute in bytes
     * @return the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     *
     * @param hex the hex string
     * @return the hex string decoded into a byte array
     */
    private static byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return binary;
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param array the byte array to convert
     * @return a length*2 character string encoding the byte array
     */
    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }

    /** MAH CODE **/

    final static byte[] secretKey = "p4ssw0rd12345678".getBytes();
    final static String ALGORITHM = "AES/ECB/PKCS5Padding";
    final static String HASH_FILENAME = "hash";
    final static String LOGTAG = "Password Hashing";
    final static String FNF = "file not found";

    private static String loadHash(Context context) {
        String hash = "";

        try(FileInputStream inputStream = context.openFileInput(HASH_FILENAME)) {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, ALGORITHM));

            BufferedInputStream stream = new BufferedInputStream(
                    new CipherInputStream(inputStream, cipher)
            );

            java.util.Scanner scanner = new java.util.Scanner(stream).useDelimiter("\\A");
            hash = scanner.hasNext() ? scanner.next() : "";

            Log.d(LOGTAG, "hash is: " + hash);
        }
        catch (FileNotFoundException fnf) {
            Log.d(LOGTAG, "file not found - assuming first run");
            hash = FNF;
        }
        catch (Exception e) {
            Log.d(LOGTAG, "exception in loadHash", e);
        }
        return hash;
    }

    private static boolean saveHash(String hash, Context context) {
        boolean success;

        try(FileOutputStream outputStream = context.openFileOutput(HASH_FILENAME, Context.MODE_PRIVATE)) {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, ALGORITHM));

            BufferedOutputStream stream = new BufferedOutputStream(
                    new CipherOutputStream(outputStream, cipher)
            );

            stream.write(hash.getBytes());
            stream.close();
            success = true;
        }
        catch (Exception e) {
            success = false;
        }
        return success;
    }

    public static boolean checkPassword(Context context, String password) {
        try {
            return validatePassword(password, loadHash(context));
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            Log.e(LOGTAG, "crypto exception checking password", e);
            return false;
        }
    }

    public static boolean savePassword(Context context, String password) {
        try {
            return saveHash(createHash(password), context);
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            Log.e(LOGTAG, "crypto exception saving password", e);
            return false;
        }
    }

    public static boolean hasPasswordBeenSet(Context context) {
        return !loadHash(context).equals(FNF);
    }
}
