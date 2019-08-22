package org.javaus.usbase.suite;

import org.javaus.usbase.aceitacao.CadastroCervejaDbUtTest;
import org.javaus.usbase.aceitacao.CadastroClienteDbUtTest;
import org.javaus.usbase.aceitacao.CadastroCidadeDbUtTest;
import org.javaus.usbase.aceitacao.CadastroEstiloDbutTest;
import org.javaus.usbase.aceitacao.CadastroUsuarioDbUtTest;
import org.javaus.usbase.aceitacao.CadastroVendaCervejaDbUtTest;
import org.javaus.usbase.aceitacao.LoginItTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   CadastroCidadeDbUtTest.class,
   CadastroEstiloDbutTest.class,
   CadastroClienteDbUtTest.class,
   CadastroUsuarioDbUtTest.class,
   CadastroCervejaDbUtTest.class,
   CadastroVendaCervejaDbUtTest.class,
   LoginItTest.class   
})

public class JunitTestSuite {   
	
} 
