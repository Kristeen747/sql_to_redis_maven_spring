package ru.riji.sql_to_influx.helpers;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class PasswordUtils {

    public static String key = "7!v1$QS7nhYbD]9";

    public static String decriptarString(final String texto) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        try {
            encryptor.setPassword(key);
            return encryptor.decrypt(texto);
        } catch (final Exception e) {
            return texto;
        }
    }
    
    public static String encriptarString(final String texto) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        try {
            encryptor.setPassword(key);
            return encryptor.encrypt(texto);
        } catch (final Exception e) {
            return texto;
        }
    }
}
