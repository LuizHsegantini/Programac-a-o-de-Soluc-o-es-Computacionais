package com.gestao.projetos.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Classe utilitária para operações de criptografia
 */
public class CryptUtils {

    /**
     * Gera hash SHA-256 da senha
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    /**
     * Verifica se a senha informada confere com o hash
     */
    public static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}
