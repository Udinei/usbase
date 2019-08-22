package org.javaus.usbase.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.javaus.usbase.aceitacao.LoginDeTest;
import org.javaus.usbase.util.MessagesUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.thoughtworks.selenium.webdriven.commands.IsVisible;

public class BaseTest  extends UsBaseApplicationTest{
	

	protected static DbUnitHelper dbUnitHelper;
	protected static WebDriver driver;
	protected MessagesUtil messagesUtil = new MessagesUtil();
	public static LoginDeTest login = new LoginDeTest();
	protected List<String> infoDB = null;
	public static WebDriverWait wait;
	public static Actions action;
	
	@Autowired
	protected Environment environment;
	
	
	public BaseTest() {
		super();
		
	}
	
	/** Esse metodo inicia os testes a partir da tela de pesquisa */
	@BeforeClass
	public static void setUp() throws Exception {
		
		login.setUp();
		login.loginFluxoPrincipal("","");
		
		driver = login.getWebdriver(); 
		wait = new WebDriverWait(driver, 10);
		action = new Actions(driver);
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
	
	}
	
	 
    @AfterClass
    public static void cleanUp() throws InterruptedException{
        if (driver != null) {
    	//ep(5000);
    		driver.close();
    		driver.quit();
        }
        
   
    }

    /** Esse metodo recupera as informações de banco do profile ativo no arquivo application-{profile}.properties
     *  essas informação serao usadas na conexao de banco do dbunit 
     * */
    public List<String> setBaseBD(){
    	String driver = environment.getProperty("spring.datasource.driver-class-name").toString();
    	String url = environment.getProperty("spring.datasource.url").toString();
		String username = environment.getProperty("spring.datasource.username").toString();
		String password = environment.getProperty("spring.datasource.password").toString();
	    return infoDB = Arrays.asList(driver, url, username, password); 
    }


	public boolean isElementPresent(By by) {
	        try {
	          driver.findElement(by);
	          return true;
	        } catch (NoSuchElementException e) {
	          return false;
	        }
	      }
	
	
	/**
	 * Esse metodo preenche um campo com mascara, contornando a situacao de que o selenium 
	 * nao controla a posição do cursor, então cada caracter do campo com mascara é inserido 
	 * um apos o outro
	 */
	public void preencheCampoComMascara(String nameCampo, String valueText) {
		String c = "";
		// clica no campo
		driver.findElement(By.name(nameCampo)).click();
		
		// insere no campo caracter por caracter
		for (int i = 0; i < valueText.length(); i++) {
			c = valueText.substring(i, i+1); // retorna o caracter da posicao
			driver.findElement(By.name(nameCampo)).sendKeys(c);	//preenche o campo
		}
	}
	
	
	/**
	 * Esse metodo preenche um campo com mascara, contornando a situacao de que o selenium 
	 * nao controla a posição do cursor, então cada caracter do campo com mascara é inserido 
	 * um apos o outro
	 */
	public void preencheCampoComMascaraSemClick(String nameCampo, String valueText) {
		String c = "";
			
		// insere no campo caracter por caracter
		for (int i = 0; i < valueText.length(); i++) {
			c = valueText.substring(i, i+1); // retorna o caracter da posicao
			driver.findElement(By.name(nameCampo)).sendKeys(c);	//preenche o campo
		}
	}
	
	
	/**
	 * Esse metodo seleciona radioButton do campo informado 
	 * @param nameCampo - nome do campo a ser selecionado
	 * @param valueOption - valor do atribute "value" (opcao) a ser selecionada, caso enum sera o nome do campo enum e não a descrição
	 */
	public void selectRadioButtonValue(String nameCampo, String valueOption) {
		List<WebElement> radiobutton = driver.findElements(By.name(nameCampo));

		for (int i = 0; i < radiobutton.size(); i++) {
			 String sValue = radiobutton.get(i).getAttribute("value");
		        if (sValue.equalsIgnoreCase(valueOption)) {
		        	radiobutton.get(i).click();
		        }
		}
	}
	
	/**
	 * Esse metodo seleciona um campo checkbox informado 
	 * @param className - nome da classe ser selecionada
	 */
	public void selectInputBootstrapSwitchLabel(String className) {
		// seleciona status do usuario
		driver.findElement(By.className(className)).click();
	}
	
	/**
	 * Esse metodo aguarda o span aparecer na tela e
	 * realiza o click em um texto contido dentro da tag span,
	 *  cujo span pode estar sendo usado em um link
	 *  Ex:
	 *  <a class="btn  btn-default" href="/estilos/novo">
	 *     <i class="glyphicon  glyphicon-plus-sign"></i>
	 *      <span class="hidden-xs hidden-sm ">Wello Word</span>
     *   </a>
	 * @param spanContainsText - Texto contido no span  
	 * @throws InterruptedException 
	 */
	public void clickInXpathSpanContainsText(String spanContainsText) throws InterruptedException {
		 WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'" + spanContainsText + "')]")));
		 if(element.isDisplayed()){
				//click
				action.click(element).perform();
		 }
	
	}
	
	public void clickInXpathSpanText(String spanText) throws InterruptedException {
		 WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='"+ spanText +"']")));
		 if(element.isDisplayed()){
				//click
				action.click(element).perform();
		 }
	
	}
	
	/**
	 * Esse metodo aguarda a visibilidade de um campo do tipo By.id, limpa e preenche
	 * o mesmo com um conteudo passado
	 * @throws InterruptedException 
	 */
	public void isVisibleTheanCleanAndFillField(String campo, String conteudo) throws InterruptedException{
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.id(campo)));
		
		 if(element.isDisplayed()){
				// na tela de pesquisa, limpa campo estilo
				//driver.findElement(By.id(campo)).sendKeys("");
			    element.clear();
				//action.sendKeys("");
				// entra com registro a ser pesquisado
				driver.findElement(By.id(campo)).sendKeys(conteudo);
		 }
	}
	
	
	
	public void clickButtonSendToFormularioNovoCadastro(String titleButton) throws InterruptedException{
		clickInXpathSpanContainsText(titleButton);
	}
	
	public void clickButtonSendToFormularioNovoCadastroSpanText(String titleButton) throws InterruptedException{
		clickInXpathSpanText(titleButton);
	}
	
	/**
	 * Esse metodo realiza o click no botaõ de acesso a tela de cadastros
	 * @param btnContainsText - Nome contido no texto do botão a ser clicado  
	 * @throws InterruptedException 
	 */
	public void clickButtonAcessaFormularioDeCadastro(String btnContainsText) throws InterruptedException {
		//Thread.sleep(800);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnContainsText")));
		// wait.until(ExpectedConditions.visibilityOf(btnContainsText));
		// entra na tela de cadastro 
		driver.findElement(By.xpath("//span[contains(text(),'" + btnContainsText + "')]")).click();
	}
	
	
	/**
	 * Esse metodo executo o click no botao da classe informada
	 * @param classNameButton - nome da classe do botao 
	 * @throws InterruptedException 
	 */
	public void clickButtonSalvarClassName(String classNameButton) throws InterruptedException {
		Thread.sleep(200);
		// click no botao salvar
		driver.findElement(By.className(classNameButton)).click();
	}
	
	
	public void clickButtonClassName(String classNameButton){
		driver.findElement(By.className(classNameButton)).click();
	}
	
	public WebElement xpathButtonTextIsVisible(String buttonText) throws InterruptedException {
		 WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='"+ buttonText +"']")));
		 Thread.sleep(5000);
		 if(element.isDisplayed()){
			 return element;
		 }
	 return null;
	}
	
	public WebElement xpathClicavelIsVisible(String xpath) throws InterruptedException {
		 WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		
		 if(element.isDisplayed()){
			 return element;
		 }
	 return null;
	}
	
	public void clickButtonLabelButton(String textButton) throws InterruptedException{
		WebElement element = xpathButtonTextIsVisible(textButton);
				
		if(element.isDisplayed()){
			assertEquals(textButton,element.getText());
			action.click(element).perform();
		}
	}

	public void clickButtonLabel(String textButton) throws InterruptedException{
		WebElement element = xpathButtonTextIsVisible(textButton);
		//wait.until(ExpectedConditions.alertIsPresent());			
		if(element.isDisplayed()){
			//wait.until(ExpectedConditions.textToBePresentInElement(element,textButton));
			Thread.sleep(1000);
			assertEquals(textButton,element.getText());
			action.click(element).perform();
		}
	}
	
	
	
	public boolean ElementoEstaPresenteNaTela(String texto){
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-objeto='"+ texto +"']")));
		
		 if(element.isDisplayed()){
			 return true;
		 }
		 return false;
	   
	}
		
	public void clickButtonSalvar(){
		driver.findElement(By.xpath("//button[text()='Salvar']")).click();
	}
	
	public void clickButtonCancelar(){
		driver.findElement(By.xpath("//button[text()='Cancelar']")).click();
	}
	
	public void clickButtonPesquisarFormularioPesquisa(){
		 WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Pesquisar']")));
		 if(element.isDisplayed()){
				//click
				action.click(element).perform();
		 }
		 
		//driver.findElement(By.xpath("//button[text()='Pesquisar']")).click();
	}

	
	
	public void clickButtonSuperiorPesquisaFormularioNovoCadastro(){
		 WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Pesquisa']")));
		 if(element.isDisplayed()){
				//click
				action.click(element).perform();
		 }
	}
	

	/**
	 * Esse metodo executa o click no botao da classe informada
	 * @param classNameButton - nome da classe do botao 
	 */
	public void clickButtonPesquisar(String classNameButton) {
		driver.findElement(By.className(classNameButton)).click();
	}
	
	/**
	 * Esse metodo executa o click no botao da classe informada
	 * @param classNameButton - nome da classe do botao 
	 */
	public void clickButtonPesquisa() {
		driver.findElement(By.xpath("//span[text()='Pesquisa']")).click();
	}
	
	
	/**
	 * Esse metodo executa o click no link da classe informada
	 * @param classNameButton - nome da classe do botao 
	 */
	public void clickLinkItemMenuOrLinkAba(String textLink) {
		// click no botao salvar
		driver.findElement(By.xpath("//a[text()='"+ textLink + "']")).click();
	}
	
	public void clickLinkItemMenuOrLinkAba(String textLink, WebDriverWait wait, Actions action) {
		// click no botao salvar
		//driver.findElement(By.xpath("//a[text()='"+ textLink + "']")).click();
		 WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text()='"+ textLink + "']")));
		 if(element.isDisplayed()){
				//click
				action.click(element).perform();
		 }
	}
	
	public void clickLinkMenu(String nomeMenu){
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='"+ nomeMenu +"']")));
		 if(element.isDisplayed()){
				//click
				action.click(element).perform();
		 }
   }

	public void clickLink(String nomeText){
		driver.findElement(By.xpath("//a[text()='"+ nomeText +"']")).click();
   }


	public void clickLinkButtonSetaCaret(String nomeText){
		driver.findElement(By.cssSelector("span[class='caret']")).click();
		driver.findElement(By.xpath("//a[text()='"+ nomeText +"']")).click();

   }
	
	
	/**
	 * Esse metodo valida a mensagem exibida ao usuario, apos salvar a entidade 
	 * @param messageKey - chave ndo arquivo properties da mensagem
	 * @param entity - nome da entidade que esta sendo salva
	 * @param msgText - mensagem de texto esperada pelo assertEquals do metodo 
	 * @throws InterruptedException 
	 */
	public void validaMsgSucessWithKeyInSpanText(String messagesKey, String entity, String msgText) throws InterruptedException {
		Thread.sleep(5000);
		validaMsgKeyInElementSpanText(messagesKey, entity, msgText);
		
	}

	
	/**
	 * Esse metodo valida a mensagem exibida ao usuario, quando a entidade já existe 
	 * @param messageKey - chave ndo arquivo properties da mensagem
	 * @param atributo - chave ndo arquivo properties da mensagem
	 * @param entity - nome da entidade que esta sendo salva
	 * @param msgText - mensagem de texto esperada pelo assertEquals do metodo 
	 */
	public void validaMsgErrorWithKeyInTextContains(String messagesKey, String atributo, String entity, String msgText) {
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//text()[contains(.,'" + msgText +"')]/ancestor::div[1]")));

		if(element.isDisplayed()){
			// valida se msg de erro foi exibida		
			assertEquals(messagesUtil.getMessage(messagesKey, atributo, entity), element.getText());
		 }
	}

	/**
	 * Esse metodo valida a mensagem exibida ao usuario, quando a entidade já existe 
	 * @param messageKey - chave ndo arquivo properties da mensagem
	 * @param atributo - chave ndo arquivo properties da mensagem
	 * @param entity - nome da entidade que esta sendo salva
	 * @param msgText - mensagem de texto esperada pelo assertEquals do metodo 
	 * @throws InterruptedException 
	 */
	public void validaMsgErrorInTextContains(String msgText) throws InterruptedException {
		
		Thread.sleep(500);
		// valida se msg de erro foi exibida		
		assertEquals(msgText, driver.findElement(By.xpath("//text()[contains(.,'" + msgText +"')]/ancestor::div[1]")).getText());

		
	}

	
	/**
	 * Esse metodo verifica se foi exibido um elemento text de uma mensagem em um span
	 * @param msgText - mensagem a ser verificada se foi exibida
	 * */
	public void validaMsgSemChaveExibidaFoiHaMensagem(String msgText){

		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'"+ msgText +"')]")));
		 
		 if(element.getText().equals(msgText)){
			 assertTrue(true);
		 }

	}
	
	/**
	 * Esse metodo verifica se foi exibido um elemento text H2 em uma mensagem
	 * @param msgText - mensagem a ser verificada se foi exibida 
	 * */
	public void validaMsgSemChaveH2ExibidaFoiHaMensagem(String msgText){
		
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2[class='aw-error-panel__title']")));
	 
		 if(element.getText().equals(msgText)){
			 assertTrue(true);
		 }
				
	}
	
	
	public void clickLogOut(){
		driver.findElement(By.xpath("//*[@class='fa  fa-sign-out']")).click();
		
	}
	
	
	/**
	 * Esse metodo seleciona um comboBox e seu item caso esteja visivel
	 * @param nameCampo - nome do campo combobox
	 * @param textItemValue - Texto do item a ser selecionado
	 */
	public void selectComboxVisibleValue(String nameCampo, String textItemValue) {
		// selecina na combobox o estado
		driver.findElement(By.name(nameCampo)).click();
    	WebElement cbx = driver.findElement(By.name(nameCampo));
    	new Select(cbx).selectByVisibleText(textItemValue);
	}
	
	/**
	 * Esse metodo seleciona um item de um checkbox na tela ou todos os item
	 * @param itemValueText a ser selecionado
	*/
	public void selectCheckBoxItemValue(String itemValueText) {
	   List<WebElement> allCheckbox = driver.findElements(By.xpath("//input[@type='checkbox']"));

	   // seleciona somente o item cujo valor foi passado como parametro
	   if(itemValueText != "todos"){
		    for (WebElement ele : allCheckbox) {
		    	WebElement tmp = ele.findElement(By.xpath("//label[text()='"+ itemValueText +"']"));
		    	
		        if (tmp.getText().equals(itemValueText)) {
		            tmp.click();
		        }
		    }
		// seleciona todos os itens do checkbox    
	   }else{
		   List<WebElement> els = driver.findElements(By.xpath("//input[@type='checkbox']"));
		   for ( WebElement el : els ) {
		       if ( !el.isSelected() ) {
		           el.click();
		       }
		   }
	   }
	}
	
	
	/**
	 * Esse metodo valida as mensagem de campos obrigatorios
	 * @param nomeCampoForm - lista com os nomes dos campos na tela a serem validados, a lista deve conter dois campos
	 * separados por virgula sendo: "nomeDoCampo, genero" 
	 * */
	public void validaCamposObrigatorios(List nomeCampoForm){
		String tmp = "";
		String genero = "";
		
		for (int i = 0; i < nomeCampoForm.size(); i++) {
				tmp = (String) nomeCampoForm.get(i);
				genero = tmp.split(",")[0];
				
				if(genero != null && genero.equals("M")){
					assertTrue(isElementPresent(By.xpath("//span[text()='" + nomeCampoForm.get(i) + " é obrigatório']")));
					
				}else if(genero != null && genero.equals("F")){
					assertTrue(isElementPresent(By.xpath("//span[text()='" + nomeCampoForm.get(i) + " é obrigatória']")));
				}
		}
		
	}
	
	/**
	 * Esse metodo executa açao de click em um button da classe passada como parametro
	 * @param classTextBtn - nome da classe do buttom
	 */
	public void clickButtonSairFormularioCadastro(String classTextBtn) {
		// sai da tela de cadastro
		driver.findElement(By.className(classTextBtn)).click();
	}
	
	/**
	 * Esse metodo valida se a mensagem esperada foi exibida para o usuario
	 * @param msgText - mensagem a ser exibida
	 * @throws InterruptedException 
	 */
	public void validaSeExibidaEmTabelaFoiHaMensagem(String msgText) throws InterruptedException {
		Thread.sleep(5000);
		boolean element = wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//td[text()='"+ msgText +"']"), msgText));
		 
		if(element){
			assertTrue(true);
		}
		//assertTrue(isElementPresent(By.xpath("//td[text()='"+ msgText +"']")));
				
	}
	
	/**
	 * Esse metodo valida a mensagem de execao exibida ao usuario, ato tentar salvar uma entidade ja cadastrada 
	 * @param messageKey - chave ndo arquivo properties da mensagem
	 * @param entity - nome da entidade que esta sendo salva
	 * @param msgText - mensagem de texto esperada pelo assertEquals do metodo 
	 */
	public void validaMsgDeExecaoExibidaHaoUsuario(String valueText) {
		// valida se msg de sucesso ao salvar foi exibida		
		assertTrue(isElementPresent(By.xpath("//span[text()='"+ valueText +"']")));
		
	}
	
	
	/**
	 * Esse metodo executa o click no botao editar da lista de pesquisa de registros
	 * @throws InterruptedException 
	 */
	public void clickButtonEditar() throws InterruptedException {
		WebElement element = xpathClicavelIsVisible("//*[@class='glyphicon glyphicon-pencil']");
		action.click(element).perform();
	}
	
	/**
	 * Esse metodo executa o click no botao "X"(Excluir) da tela de pesquisa de registros
	 * @throws InterruptedException 
	 */
	public void clickButtonXTelaPesquisarParaExcluirRegistroPesquisado() throws InterruptedException {
		WebElement element = xpathClicavelIsVisible("//*[@class='glyphicon glyphicon-remove']");
		action.click(element).perform();
	}
	
	
	/**
	 * Esse metodo verifica se o registro não foi excluido, verificando se ainda se encontra na listagem de pesquisa
	 * @param valueTextRegistro - substring contida no registro com cancelamento de excluao
	 */
	public void validaExclusaoRegistroFoiCancelada(String valueTextRegistro) {
		// verifica se o elemento ainda esta na listagem
		assertTrue(login.isElementPresent(By.xpath("//*[@data-objeto='"+ valueTextRegistro +"']")));
	}


	/**
	 * Esse metodo realiza o cancelamento da exclusao do registro pesquisado selecionado 
	 */
	public void clickButtomCancelAlertExcluirRegistro() {
		// alert da msg de exclusao do registro foi exibida
		assertTrue(login.isElementPresent(By.xpath("//button[text()='Cancel']"))); 
		
        // click no botao cancel do alert
		driver.findElement(By.xpath("//button[text()='Cancel']")).click();
	}


	/**
	 * Esse metodo exclui um registro com o texto passado como parametro
	 * @param valueTextRegistro - texto do registro a ser excluido
	 */
	public void clickButtonExcluirRegistroPesquisadoComSubString(String valueTextRegistro) {
		// click no icone "X" (link) para excluir o registro pesquisado   		
		driver.findElement(By.xpath("//*[@data-objeto='"+ valueTextRegistro + "']")).click();
	}
	
	/**
	 * Esse metodo realiza o click no botao excluir do alert exibido ao usuario
	 * @throws InterruptedException 
	 */
	public void clickButtonOkAlertExcluirRegistro() throws InterruptedException {
		// alert da msg de exclusao do registro foi exibida
		//ep(1000);
		//assertTrue(login.isElementPresent(By.xpath("//button[text()='Sim, exclua agora!']"))); 
		// click no botao sim 
		//driver.findElement(By.xpath("//button[text()='Sim, exclua agora!']")).click(); 
		// alert da msg de confirma a exclusao
		//assertTrue(login.isElementPresent(By.xpath("//button[text()='OK']")));
		// alert de exclusao com sucesso e botao ok
		//ep(1000);
		//driver.findElement(By.xpath("//button[text()='OK']")).click();
		// alert da msg de exclusao do registro foi exibida
		 clickButtonLabelButton("Sim, exclua agora!");
		 clickButtonLabelButton("OK");
	}
	

	/**
	 * Esse metodo faz upload de arquivos para o servidor
	 * @throws InterruptedException 
	 * @path idInput - id do link a ser clicado para abrir a caixa de dialogo do windows
	 * @path path - caminho no disco onde esta o arquivo a ser carregado
	 * 
	*/
    public void uploadFileDiskFromWebserve(String idInput, String path) throws InterruptedException{	
		WebElement upload =  driver.findElement(By.xpath("//input[@type='file'][@id='" + idInput + "']"));
		//ep(5000);
		upload.sendKeys(path);
		
    }
    
    /** 
     * Esse metodo implementa a logica para a janela mestre aguarde a execução do modal e seu retorno
     * para somente depois seguir com o preenchimento da janela mestre
     * @param master - identificar da nome da janela mestre que deve ser obtido da seguinte forma:
     *                 	String master = driver.getWindowHandle(); 
     * */
	public void mantemJanelaMestreAguardaReturnModalOrPopup(String master) throws InterruptedException {
		// lógica para aguardar o popup, algum tempo e evitar o loop infinito.
		// verificando se o tamanho se torna maior que 1 ou quebra depois
		int timeCount = 1;

		do {
			// obtem modal atual
			driver.getWindowHandles();
			
			//ep(100);
			timeCount++;
			   
			if(timeCount > 50){
			       break;
			 }
			// enquanto o modal altual nao for fechado ou timeCount > 50
		 } while (driver.getWindowHandles().size() == 1 );
			
		// atribui o modal atual ao conjunto
		Set<String> handles = driver.getWindowHandles();
			
		// Mudando para a janela modal/popup.
		for(String handle : handles){
		    if(!handle.equals(master)){
		         driver.switchTo().window(handle);
		    }
		}
	}
	
	/**
	 * Esse metodo retorna um array de string com duas posicoes contendo a data de hoje e amanha
	 * */
	public String[] retornaDataHojeHeAmanha() {
	
		LocalDate hoje = LocalDate.now();
		LocalDate amanha = hoje.plusDays(1);
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(new Locale("pt", "BR"));
			
		String [] dataCriacao = new String[2];
		dataCriacao[0] = hoje.format(formatador); 
		dataCriacao[1] = amanha.format(formatador);
		return dataCriacao;
	}
	
	public String retornaTextContains(String text){
		return driver.findElement(By.xpath("//text()[contains(.,'" + text +"')]/ancestor::div[1]")).getText();
	 
	}
	
	private void validaMsgKeyInElementSpanText(String messagesKey, String entity, String msgText) throws InterruptedException {
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='"+ msgText + "']")));
		//boolean element = wait.until(ExpectedConditions.invisibilityOfElementWithText(By.xpath("//span[text()='"+ msgText + "']"), msgText));
		
		if(element.isDisplayed()){
		     // valida se msg de sucesso foi exibida	
			 if(element.getText().equals(msgText) && messagesUtil.getMessage(messagesKey, entity).equals(element.getText())){
				 assertTrue(true);
		      }
		}
      }
	}
