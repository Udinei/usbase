package org.javaus.usbase.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/** Essa classe e utilizada para carregar o contexto da aplicação para os testes, alem de integrar dbunit e o junit. 
 * disponibilizando diversas funcionalidades do spring boot, bem como injecao de dependencia de
 *  qualquer classe da aplicacao nos testes, com uso da anotation  @Autowire por exemplo.
 * */

@WebAppConfiguration
/*@EnableTransactionManagement
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class})*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class UsBaseApplicationTest {
	
	
	@Test
	public void contextLoads() {
		
	}

}
