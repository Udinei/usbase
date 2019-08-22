package org.javaus.usbase.aceitacao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.dbunit.operation.DatabaseOperation;
import org.javaus.usbase.base.BaseTest;
import org.javaus.usbase.base.DbUnitHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**Esse teste cria um registro de teste no BD para o cadastro da cidade de teste, que sera apagado apos os 
 * testes*/
public class CadastroCidadeDbutTestTmp extends BaseTest  {

	
	
	@Before
	public void init() {
         // passa informações de conexao de banco para o DBUnit e pasta de acesso do .xml de controle do BD
		dbUnitHelper = new DbUnitHelper(setBaseBD(), "META-INF", "");
		
		// executa a conexao
		dbUnitHelper.conectaBD();	
		
		// Controla os dados inseridos no banco, que serão removidos apos os testes 
		dbUnitHelper.execute(DatabaseOperation.CLEAN_INSERT, "UsBaseXmlDBData.xml");
		
	}	
	
	@BeforeClass
	public static void initClass() {
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		//  entra na tela de pesquisa
		driver.get("http://localhost:8090/cidades");
	}
	
	
	@Test
	public void fluxoPrincipal() throws Exception {
		// na tela de cadastro
		deveCadastrarNovaCidade();
		deveValidarExecaoCidadeJaCadastrada();
		
		//na tela de pesquisa
        devePesquisarCidadeJaCadastrada();
        devePesquisarEstadoHeCidadeJaCadastrada();
        deveCancelarExclusaoCidadeCadastradoPesquisada();
       
		deveExcluirCidadeCadastradaPesquisada();
				
	}

	//@Test
	public void deveCadastrarNovaCidade() {
		// a partir da tela de pesquisa click no  botao Novo
		driver.findElement(By.xpath("//span[text()='Nova cidade']")).click();
		// seleciona estado Goias
		selectComboxVisibleValue("estado", "Goias");
		// na tela de cadastro informa registro de teste 
		driver.findElement(By.name("nome")).sendKeys("CadastroCidadeTeste");
		// click no botao salvar
		driver.findElement(By.className("btn-primary")).click();
		// valida se msg de sucesso ao salvar foi exibida		
		assertEquals(messagesUtil.getMessage("msg.salva.sucesso", "Cidade"), driver.findElement(By.xpath("//span[text()='Cidade salva com sucesso!']")).getText());
	}
	
	
	public void deveValidarExecaoCidadeJaCadastrada() throws InterruptedException {
		// seleciona estado Goias
		selectComboxVisibleValue("estado", "Goias");
		// na tela de cadastro limpa campo nome
		//Thread.sleep(2000);
		driver.findElement(By.name("nome")).sendKeys("");
		// informa registro de teste
		driver.findElement(By.name("nome")).sendKeys("CadastroCidadeTeste");
		// click para salvar
		driver.findElement(By.className("btn-primary")).click();
		validaMsgErrorWithKeyInTextContains("msg.error.atrib.ent.ja.cadastrada", "Nome", "cidade", "Nome da cidade já cadastrada!");
		clickButtonSairFormularioCadastro("btn-default");  
	}
	
	
	public void devePesquisarCidadeJaCadastrada() throws InterruptedException {
		// na tela de pesquisa, limpa campo estilo
		driver.findElement(By.id("nome")).sendKeys("");
		// entra com registro a ser pesquisado 
		driver.findElement(By.id("nome")).sendKeys("CadastroCidadeTeste");
		clickButtonSalvarClassName("btn-primary");
		validaSeExibidaEmTabelaFoiHaMensagem("CadastroCidadeTeste");
		driver.findElement(By.id("nome")).sendKeys("");

	}
		
	public void devePesquisarEstadoHeCidadeJaCadastrada() throws InterruptedException {
		// seleciona estado Goias
		selectComboxVisibleValue("estado", "Goias");
		// na tela de pesquisa, limpa campo cidade
		driver.findElement(By.id("nome")).sendKeys("");
		clickButtonSalvarClassName("btn-primary");
		validaSeExibidaEmTabelaFoiHaMensagem("CadastroCidadeTeste");
		driver.findElement(By.id("nome")).sendKeys("");

	}
	

	public void deveCancelarExclusaoCidadeCadastradoPesquisada() throws InterruptedException{
		// na tela de pesquisa, limpa campo estilo
		driver.findElement(By.id("nome")).sendKeys("");
		// entra com registro a ser pesquisado 
		//driver.findElement(By.id("nome")).sendKeys("CadastroCidadeTeste");
		clickButtonPesquisar("btn-primary");
		clickButtonExcluirRegistroPesquisadoComSubString("CadastroCidadeTeste");
		clickButtomCancelAlertExcluirRegistro(); 
		validaExclusaoRegistroFoiCancelada("CadastroCidadeTeste");
	}

	public void deveExcluirCidadeCadastradaPesquisada() throws InterruptedException{
		//Thread.sleep(1000);
		clickButtonExcluirRegistroPesquisadoComSubString("CadastroCidadeTeste");
		clickButtonOkAlertExcluirRegistro();
     }
		
}
