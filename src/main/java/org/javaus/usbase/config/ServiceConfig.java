package org.javaus.usbase.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.javaus.usbase.service.CadastroCervejaService;
import org.javaus.usbase.storage.FotoStorage;

@Configuration
@ComponentScan(basePackageClasses = {CadastroCervejaService.class, FotoStorage.class})
public class ServiceConfig {
	



}
