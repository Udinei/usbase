package org.javaus.usbase.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.javaus.usbase.aceitacao.CadastroCervejaDbUtTest;
import org.javaus.usbase.aceitacao.CadastroClienteDbUtTest;
import org.javaus.usbase.aceitacao.CadastroEstadoDbUtTest;
import org.javaus.usbase.aceitacao.CadastroEstiloItTest;
import org.javaus.usbase.aceitacao.CadastroUsuarioDbUtTest;
import org.javaus.usbase.aceitacao.CadastroVendaCervejaDbUtTest;
import org.javaus.usbase.aceitacao.LoginItTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   CadastroEstadoDbUtTest.class,
   CadastroEstiloItTest.class,
   CadastroClienteDbUtTest.class,
   CadastroUsuarioDbUtTest.class,
   CadastroCervejaDbUtTest.class,
   CadastroVendaCervejaDbUtTest.class,
   LoginItTest.class   
})

public class JunitTestSuite {   
	
} 
