package com.bulbasauro.utils;

/**
 * Created on 09/02/2016.
 */
public class NumberSelector {

    public static String numeroUsuario(String urlPerfil) {
        String userNumber = "";
        for (int i = urlPerfil.length(); i > 0; i--) {
            String caracter = String.valueOf(urlPerfil.charAt(i - 1));
            if ("_".equals(caracter)) {
                userNumber = new StringBuilder(userNumber).reverse().toString();
                break;
            } else {
                userNumber = userNumber + caracter;
            }
        }
        return userNumber;
    }

    public static String numeroPost(String numeroPost) {
        String postNumero = "";
        for (int i = numeroPost.length(); i > 0; i--) {
            String caracter = String.valueOf(numeroPost.charAt(i - 1));
            if ("#".equals(caracter)) {
                postNumero = new StringBuilder(postNumero).reverse().toString();
                break;
            } else {
                postNumero = postNumero + caracter;
            }
        }
        return postNumero;
    }
}
