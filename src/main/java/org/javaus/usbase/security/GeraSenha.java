package org.javaus.usbase.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Essa classe e um simples utilitario
 *  que gera uma criptografia de uma string, que pode ser utilizada com senha */
public class GeraSenha {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		/* antes de criptografar a senha a mesma deve ja estar codificado no sistema secreto de numeros */
		System.out.println(encoder.encode("a13i2")); 
	}

}
