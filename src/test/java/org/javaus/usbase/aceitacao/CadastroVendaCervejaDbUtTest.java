package org.javaus.usbase.aceitacao;

import java.util.concurrent.TimeUnit;

import org.dbunit.operation.CompositeOperation;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;

import org.javaus.usbase.base.BaseTest;
import org.javaus.usbase.base.DbUnitHelper;
import org.javaus.usbase.repository.Cervejas;
import org.javaus.usbase.service.CadastroCervejaService;


public class CadastroVendaCervejaDbUtTest extends BaseTest { 

	private String querySQL = "";
	
	@Autowired
	private Cervejas cervejas;

	@Autowired
	CadastroCervejaService cadastroCervejaService;

	@BeforeClass
	public static void initClass() {
				
		// Valido para todo o testes, aguarda até trinta segundos por um determinado
		// elemento acessivel, legivel ou clicavel na DOM 
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
				
	}

	
	
	@Before
	public void init() {
		
		  // zera o autoincremento da tabela venda, para ser consistente com as msg exibidas na tela de venda  
		  querySQL = "ALTER TABLE venda AUTO_INCREMENT = 0";
		  
         // passa informações de conexao de banco para o DBUnit e pasta de acesso do .xml de controle do BD
		dbUnitHelper = new DbUnitHelper(setBaseBD(), "META-INF", querySQL);
		
		// executa a conexao
		dbUnitHelper.conectaBD();	

		// Controla os dados inseridos no banco, que serão removidos apos os testes 
		//dbUnitHelper.execute(DatabaseOperation.CLEAN_INSERT, "UsBaseXmlDBData.xml");
		dbUnitHelper.execute(DatabaseOperation.CLEAN_INSERT, "UsBaseXmlDBData.xml");
		
	}
	
	
	@Test
	public void fluxoPrincipal() throws Exception {
		
		/** A sequencia de chamada dos metodos abaixo, parte do principio de que todos os comandos serão executados 
		 a partir da  tela de cadastro */
		  // preparando cliente e itens que serão usados na venda
		  deveCadastrarUmClienteParaVenda();  
		  deveCadastrarUmaCervejaParaVenda();
		  deveCadastrarOutraCervejaSeraUsadaNaEdicao(); // cadastra uma nova cerveja que sera usada na edicao da venda
		  
		  // na venda
		  deveExibirMsgDePrenchimentoCamposObrigatorio();
		  deveCadastrarUmaNovaVenda();
		  devePesquisarRegistroCadastrado();
		  deveEditarRegistroCadastradoPesquisado();
		  deveExcluirUmItemDaVenda();
		 	  
		  deveSalvarHeEnviarVendaCadastradaOrcamentoPorEmail();
		  deveSalvarHeEmitirUmaVendaCadastradaOrcamento(); 
          //deveExibirMsgNenhumaEntidadePesquisadaEncontrada();
    	  //deveCancelarVendaEmitida();
		  devePermitirVendedorEnviarEmailDeVendaEmitida();
		  devePermitirVendedorAdminCancelarVendaEmitida();
         
	     
	}
	
	/*
	private void deveCancelarVendaEmitida() throws InterruptedException {
		clickButtonEditar();
		//ep(1000);
		clickButtonCancelar();
		//ep(6000);
		
	}*/



	public void deveExibirMsgDePrenchimentoCamposObrigatorio() throws InterruptedException{
		clickLinkMenu("Vendas");
		clickLinkItemMenuOrLinkAba("Cadastro de venda");
		clickButtonSalvarClassName("btn-primary");
		//ep(5000);
		validaMsgErrorInTextContains("Adicione pelo menos uma cerveja na venda");
		validaMsgErrorInTextContains("Selecione um cliente na pesquisa rápida");
		 
		clickButtonSairFormularioCadastro("btn-default");   
	}

	public void deveCadastrarUmaNovaVenda() throws InterruptedException {
		driver.get("http://localhost:8090");
		clickLinkMenu("Vendas");
		clickLinkItemMenuOrLinkAba("Cadastro de venda");
		preencheFormularioDeDados();
		clickButtonSalvar();
        // valida msg exibida usuario, apos salvar formulario de cadastro   
		validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Venda", "Venda salva com sucesso!");
		clickButtonPesquisa();
		  
	}


	private void deveSalvarHeEnviarVendaCadastradaOrcamentoPorEmail() throws InterruptedException {
		clickButtonEditar();
		clickLinkButtonSetaCaret("Salvar e enviar por e-mail");
		validaMsgSemChaveExibidaFoiHaMensagem("Venda nº 1 salva com sucesso e e-mail enviado");
		clickButtonPesquisa();
	
	}


	private void deveSalvarHeEmitirUmaVendaCadastradaOrcamento() throws InterruptedException {
		//ep(100);
		clickButtonEditar();
		//ep(500);
		clickLinkButtonSetaCaret("Salvar e emitir");
		validaMsgSemChaveExibidaFoiHaMensagem("Venda salva e emitida com sucesso");
		clickButtonPesquisa();
	}
	
	public void deveCadastrarUmaCervejaParaVenda() throws InterruptedException {
		driver.get("http://localhost:8090/cervejas");
		CadastroCervejaDbUtTest cerveja = new CadastroCervejaDbUtTest();
		cerveja.deveCadastrarUmNovoRegistro();
	}	
	
	
	
	public void deveCadastrarUmClienteParaVenda() throws InterruptedException{
		driver.get("http://localhost:8090/clientes");
		CadastroClienteDbUtTest cliente = new CadastroClienteDbUtTest();
		cliente.deveCadastrarUmNovoCliente();
	}
	
	
	public void devePesquisarRegistroCadastrado() throws InterruptedException{
	   // recebe como parametro campo e valor retornado na pesquisa
		preencheCamposDePesquisa("Orçamento");
		//ep(300);
		clickButtonSalvarClassName("btn-primary");
		//ep(500);
		validaSeExibidaEmTabelaFoiHaMensagem("Juliana dos Santos");
		//ep(5000);
		
	}
	
	/** A alteração realizada por esse metodo sera desfeita pelo dbunit, dispensado a necessidade reeditar o registro
	 *  para o estado antes da edição (alteracao) 
	 * @throws InterruptedException 
	 *  
	 */
	public void deveEditarRegistroCadastradoPesquisado() throws InterruptedException{
		clickButtonEditar();
		preencheCamposDaEdicao();
		
    	clickButtonSalvarClassName("btn-primary");
    	validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Venda", "Venda salva com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");  
		
	}
	
	public void deveExcluirUmItemDaVenda() throws InterruptedException{
		driver.get("http://localhost:8090");
		clickLinkMenu("Vendas");
		//ep(100);
		clickLinkItemMenuOrLinkAba("Pesquisa venda", wait, action);
		clickButtonEditar();
		
 
		 WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//text()[contains(.,'Becks Long Neck')]/ancestor::div[1]")));
		 if(element.isDisplayed()){
				//Double click
				action.doubleClick(element).perform();
		 }
		 
		 WebElement excluir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Sim, excluir!']")));
		 if(element.isDisplayed()){
				//Double click
				action.doubleClick(excluir).perform();
		 }
		 
		//ep(5000);
    	clickButtonSalvarClassName("btn-primary");
		validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Venda", "Venda salva com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");
	}
	
	
	public void deveExibirMsgNenhumaEntidadePesquisadaEncontrada() throws InterruptedException{
		// recebe como parametro campo e valor retornado na pesquisa
		preencheCamposDePesquisaNenhumaEntidadeEncontrada();
		clickButtonSalvarClassName("btn-primary");
		validaSeExibidaEmTabelaFoiHaMensagem("Nenhuma venda encontrada");
		
	}
	
	public void devePermitirVendedorEnviarEmailDeVendaEmitida() throws InterruptedException{
		// desloga do sistema
		clickLogOut(); 
		// loga no sistema como usario vendedor
		driver.get("http://localhost:8090");
		login.loginFluxoPrincipal("usertest@usbase.com","u0e41e01");
		
		// Tenta cancelar venda feita pelo usuario admin
		//ep(5000);
		clickLinkMenu("Vendas");
		clickLinkItemMenuOrLinkAba("Pesquisa venda");
		//ep(1000);
		preencheCamposDePesquisa("Emitida");
		clickButtonPesquisar("btn-primary");
		clickButtonEditar();
		//ep(1000);
		//clickButtonCancelar();
     	clickButtonLabelButton("Enviar Email");
		//ep(6000);
		validaMsgSemChaveExibidaFoiHaMensagem("Venda nº 1 salva com sucesso e e-mail enviado");
		//ep(3000);
    	clickButtonSuperiorPesquisaFormularioNovoCadastro(); // volta para tela de pesquisa venda
    	
	}
	
	
	public void naoDevePermitirVendedorCancelarVendaEmitida() throws InterruptedException{
		// desloga do sistema
		clickLogOut(); 
		// loga no sistema como usario vendedor
		driver.get("http://localhost:8090");
		login.loginFluxoPrincipal("miguel@nn.com","miguel");
		
		// Tenta cancelar venda feita pelo usuario admin
		//ep(5000);
		clickLinkMenu("Vendas");
		clickLinkItemMenuOrLinkAba("Pesquisa venda");
		//ep(1000);
		preencheCamposDePesquisa("Emitida");
		clickButtonPesquisar("btn-primary");
		clickButtonEditar();
		//ep(1000);
		clickButtonCancelar();
		//ep(6000);
		validaMsgSemChaveH2ExibidaFoiHaMensagem("Acesso negado"); // exibem mensagem 403 acesso negado
		//ep(3000);
    	clickButtonClassName("btn-primary"); // volta para pagina de venda
    	
	}
	
	public void devePermitirVendedorAdminCancelarVendaEmitida() throws InterruptedException{
		// desloga do sistema
		clickLogOut(); 
		// loga no sistema como usario vendedor
		driver.get("http://localhost:8090");
		login.loginFluxoPrincipal("admin@usbase.com","a13i2");
		
		// Tenta cancelar venda feita pelo usuario admin
		clickLinkMenu("Vendas");
		clickLinkItemMenuOrLinkAba("Pesquisa venda");
		//ep(1000);
		preencheCamposDePesquisa("Emitida");
		clickButtonPesquisar("btn-primary");
		clickButtonEditar();

		//ep(1000);
		// o usuario admin deve ter a permissao ROLE_CANCELAR VENDA (colocar essa permissao no arquivo do flyway)
		// e incluir no grupo a permissao
		clickButtonCancelar();
		//ep(2000);
		validaMsgSemChaveExibidaFoiHaMensagem("Venda cancelada com sucesso!");
    	clickLogOut(); // sai do sistema
	}

	
	public void deveExcluirRegistroPesquisadoEncontradoNaoUsadoEmOutroCadastro() throws InterruptedException{
 			//ep(1000);
			clickButtonExcluirRegistroPesquisadoComSubString("Cerveja");
			clickButtonOkAlertExcluirRegistro();
	}

		
	public void preencheFormularioDeDados() throws InterruptedException {
		// na tela de cadastro informa registro de teste 
		// obtem o identificador da janela mestre antes de clicar no link de chamada da janela modal/popup
		String janelaMaster = driver.getWindowHandle();
		
		driver.findElement(By.xpath("//*[@class='glyphicon  glyphicon-search']")).click();
		
		driver.findElement(By.id("nomeClienteModal")).sendKeys("Juliana");
		clickButtonPesquisar("js-pesquisa-rapida-clientes-btn");
		
		//ep(100);
		driver.findElement(By.xpath("//td[text()='Juliana dos Santos']")).click();
		
		//ep(200);
		preencheCampoComMascaraSemClick("valorFrete", "200");
		preencheCampoComMascaraSemClick("valorDesconto", "100");
		clickLinkItemMenuOrLinkAba("Entrega");
		
		//ep(100);
		driver.findElement(By.xpath("//input[@type='text'][@name='dataEntrega']")).sendKeys("25/10/2020");
		preencheCampoComMascara("horarioEntrega", "1945");
		driver.findElement(By.xpath("//textarea[@name='observacao']")).sendKeys("Entregar somente após as 09:00");
		driver.findElement(By.cssSelector("a[href='#cervejas']")).click();
		driver.findElement(By.id("cerveja")).sendKeys("AA1234");
		//ep(100);
		driver.findElement(By.xpath("//text()[contains(.,'Becks Long Neck')]/ancestor::div[1]")).click();
		
		//ep(100);
		driver.findElement(By.name("valorUnitario")).clear();
		driver.findElement(By.name("valorUnitario")).sendKeys("2");
	
		driver.findElement(By.id("cerveja")).click();
		
		mantemJanelaMestreAguardaReturnModalOrPopup(janelaMaster);
		    
	}
	
	
	public void preencheCamposDePesquisa(String status){
		String[] dataCriacao = retornaDataHojeHeAmanha();
			
		// na tela de cadastro informa registro de pesquisa 
		driver.findElement(By.name("codigo")).sendKeys("1");
		selectComboxVisibleValue("status", status);
		preencheCampoComMascara("desde", dataCriacao[0]);
		preencheCampoComMascara("ate", dataCriacao[1]);
		driver.findElement(By.name("valorMinimo")).sendKeys("1");
		driver.findElement(By.name("valorMaximo")).sendKeys("2000");
		driver.findElement(By.name("nomeCliente")).sendKeys("Juliana dos Santos");
		preencheCampoComMascara("cpfOuCnpjCliente", "54305209187");
	}

	public void preencheCamposDePesquisaNenhumaEntidadeEncontrada(){
		driver.findElement(By.name("codigo")).sendKeys("10");
		selectComboxVisibleValue("status","Emitida");
	}
	
	public void preencheCamposDaEdicao() throws InterruptedException{
		
		driver.findElement(By.cssSelector("a[href='#cervejas']")).click();
		driver.findElement(By.id("cerveja")).sendKeys("BB1234");
		driver.findElement(By.xpath("//text()[contains(.,'Cerva Negra')]/ancestor::div[1]")).click();
		
		//ep(100);
		driver.findElement(By.name("valorUnitario")).clear();
		driver.findElement(By.name("valorUnitario")).sendKeys("3");
	
	}
	
	private void deveCadastrarOutraCervejaSeraUsadaNaEdicao() throws InterruptedException{
		driver.get("http://localhost:8090/cervejas");
		CadastroCervejaDbUtTest cerveja = new CadastroCervejaDbUtTest();
		
		cerveja.deveCadastrarUmNovoRegistroNovaCerveja();
	}

	// sera utilizado o registro anteriormente editado
	public void preencheCamposDePesquisaParaExclusao(){
			driver.findElement(By.name("nome")).sendKeys("Cerveja");
			selectComboxVisibleValue("sabor","Suave");
			selectRadioButtonValue("origem", "Nacional");		
		}
	
	
	
	@After
	public void end() {
		

	}
}
