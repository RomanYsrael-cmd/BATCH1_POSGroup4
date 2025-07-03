package Batch1_POSG4.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Provides SHA-256 hashing functionality for a given input string.
public class Sha256Hasher {

    // Instance fields (public)

    // Instance fields (private)
    private String input;
    private String hashedOutput;

    // Constructs a Sha256Hasher and computes the hash for the input.
    public Sha256Hasher(String input) {
        this.input = input;
        this.hashedOutput = computeHash(input);
    }

    // Returns the original input string.
    public String getInput() {
        return input;
    }

    // Returns the SHA-256 hash of the input string.
    public String getHashedOutput() {
        return hashedOutput;
    }

    // Computes the SHA-256 hash for the given input string.
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
}