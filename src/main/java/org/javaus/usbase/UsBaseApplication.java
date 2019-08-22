package org.javaus.usbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/** Essa classe deve ser executada para subir aplicacao com spring boot
 *  Utiliza o tomcat embeded 
 *  comando para executar a aplicacao apos o empacotamento:
 *  java -jar nomeApp args - args opcional para passar parametros em arquivos externos ser for o caso */

@SpringBootApplication
public class UsBaseApplication {
	public static void main(String[] args) {
		SpringApplication.run(UsBaseApplication.class, args);
	}
		
}
