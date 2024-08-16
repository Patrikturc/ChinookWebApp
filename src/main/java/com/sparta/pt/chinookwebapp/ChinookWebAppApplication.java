package com.sparta.pt.chinookwebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootApplication
public class ChinookWebAppApplication {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		SpringApplication.run(ChinookWebAppApplication.class, args);
//		generateKey();
	}

	private static void generateKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
		SecretKey secretKey = keyGen.generateKey();
		String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		System.out.println("KEY- \n" + encodedKey);
	}
}
