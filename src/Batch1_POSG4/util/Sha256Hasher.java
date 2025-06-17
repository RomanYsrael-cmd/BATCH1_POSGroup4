package Batch1_POSG4.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Hasher {

    private String input;
    private String hashedOutput;

    public Sha256Hasher(String input) {
        this.input = input;
        this.hashedOutput = computeHash(input);
    }

    private String computeHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public String getInput() {
        return input;
    }

    public String getHashedOutput() {
        return hashedOutput;
    }
}