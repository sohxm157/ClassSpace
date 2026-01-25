package com.ClassSpace.FeedbackFeature.utils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GenerateHash {
	

	public static String generateHash(String input) {
	    try {
	        // 1. Initialize the MessageDigest with the SHA-256 algorithm
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");

	        // 2. Perform the hashing (returns a byte array)
	        byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

	        // 3. Convert the byte array into a readable Base64 string
	        return Base64.getEncoder().encodeToString(encodedHash);

	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Hashing algorithm not found", e);
	    }
	}
}
