package org.javaus.usbase.aceitacao;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.dbunit.operation.DatabaseOperation;
import org.javaus.usbase.base.BaseTest;
import org.javaus.usbase.base.DbUnitHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CadastroEstiloDbutTest extends BaseTest {

	private Map<String, String> mapAtributosNovoCadastro = new HashMap<String, String>();
	private Map<String, String> mapAtributosPesquisa = new HashMap<String, String>();
	private Map<String, String> mapAtributosEditado = new HashMap<String, String>();
	private Map<String, String> mapAtributosPesquisaEditado = new HashMap<String, String>();
	
	@Before
	public void init() {
		// passa informações de conexao de banco para o DBUnit e pasta de acesso
		// do .xml de controle do BD
		dbUnitHelper = new DbUnitHelper(setBaseBD(), "META-INF", "");

		// executa a conexao
		dbUnitHelper.conectaBD();

		// Controla os dados inseridos no banco, que serão removidos apos os
		// testes
		dbUnitHelper.execute(DatabaseOperation.CLEAN_INSERT, "UsBaseXmlDBData.xml");

	}

	@BeforeClass
	public static void initClass() {
		//driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		// entra na tela de pesquisa
		driver.get("http://localhost:8090/estilos");
	}

	@Test
	public void fluxoPrincipal() throws Exception {
		// na tela de cadastro
		deveCadastrarNovoEstilo();
		deveValidarExecaoEstiloJaCadastrado();

		// na tela de pesquisa
		devePesquisarEstiloCadastrado();
		deveEditarEstiloPesquisado();
		devePesquisarEstiloCadastradoEditado();	
		deveCancelarExclusaoEstiloCadastradoPesquisadoEditado();
		deveExcluirEstiloCadastradoPesquisadoEditado();

	}

	private void deveEditarEstiloPesquisado() throws InterruptedException {
		clickButtonEditar();
		devePreencherCamposDaTelaNovoCadastroEditar();
		clickButtonLabelButton("Salvar");
		validaMsgSucessWithKeyInSpanText("msg.salvo.sucesso",  "Estilo", "Estilo salvo com sucesso!");
		clickButtonSuperiorPesquisaFormularioNovoCadastro();
	}

	private void deveCadastrarNovoEstilo() throws InterruptedException {
		acessaFormularioNovoCadastraHePreencheCampos();
		clickButtonLabelButton("Salvar");
		validaMsgSucessWithKeyInSpanText("msg.salvo.sucesso",  "Estilo", "Estilo salvo com sucesso!");
		clickButtonSuperiorPesquisaFormularioNovoCadastro();

	}

	private void acessaFormularioNovoCadastraHePreencheCampos() throws InterruptedException {
		clickButtonSendToFormularioNovoCadastro("Novo Estilo");
		devePreencherCamposDaTelaNovoCadastro();
		
	}
	

	public void deveValidarExecaoEstiloJaCadastrado() throws InterruptedException {
		acessaFormularioNovoCadastraHePreencheCampos();
		clickButtonLabelButton("Salvar");
		validaMsgErrorWithKeyInTextContains("msg.error.atrib.ent.ja.cadastrado", "Nome", "estilo",	"Nome do estilo já cadastrado!");
		clickButtonSuperiorPesquisaFormularioNovoCadastro();
	}

	public void devePesquisarEstiloCadastrado() throws InterruptedException {
		devePreencherCamposDaTelaPesquisar();
		clickButtonPesquisarFormularioPesquisa();
	}
	
	public void devePesquisarEstiloCadastradoEditado() throws InterruptedException {
		devePreencherCamposDaTelaPesquisarCadastroEditado();
		clickButtonPesquisarFormularioPesquisa();
	}
	
	
	public void deveCancelarExclusaoEstiloCadastradoPesquisadoEditado() throws InterruptedException {
		// click no icone "X" (link) para excluir regristro de teste
		clickButtonXTelaPesquisarParaExcluirRegistroPesquisado();
		// alert da msg de exclusao do registro foi exibida
		// click no botao cancel
		 clickButtonLabelButton("Cancel");
		 ElementoEstaPresenteNaTela("CadastroEditado");

	}

	public void deveExcluirEstiloCadastradoPesquisadoEditado() throws InterruptedException {
		// click no icone "X" (link) para excluir regristro de teste
		clickButtonXTelaPesquisarParaExcluirRegistroPesquisado();
		// alert da msg de exclusao do registro foi exibida
		clickButtonLabel("Sim, exclua agora!");
		 clickButtonLabel("OK");
	}
	// preenche campos na pesquisa
	private void devePreencherCamposDaTelaPesquisar() throws InterruptedException{
		setNomeValorAtributosPesquisar();;
		preencheCampos(mapAtributosPesquisa); 
	}
	
	// preenche campos na edição
	private void devePreencherCamposDaTelaNovoCadastroEditar() throws InterruptedException{
		setNomeValorAtributosEditado();;
		preencheCampos(mapAtributosEditado); 
	}
	
	// preenche campos na pesquisa
	private void devePreencherCamposDaTelaPesquisarCadastroEditado() throws InterruptedException{
		setNomeValorAtributosPesquisarEditado();;
		preencheCampos(mapAtributosPesquisaEditado); 
	}
	
	// preenche campos de novo cadastro
	private void devePreencherCamposDaTelaNovoCadastro() throws InterruptedException{
		setNomeValorAtributosNovoCadastro();
		preencheCampos(mapAtributosNovoCadastro); 
	}
	
	/**
	 * O locator pode ser um dos valores abaixo, separados do nome do campo por hifen.
	 * Ex: nome-id ou nome-xpath
	 * id, Name, Class Name, Tag Name, Link Text, Partial Link Text, xpath
	 */
	public void setNomeValorAtributosNovoCadastro() {
    	mapAtributosNovoCadastro.put("nome-id", "CadastroEstiloTeste");
		
	}
	
	public void setNomeValorAtributosPesquisar() {
		mapAtributosPesquisa.put("estilo-id", "CadastroEstiloTeste");
	
	}

	public void setNomeValorAtributosPesquisarEditado() {
		mapAtributosPesquisaEditado.put("estilo-id", "CadastroEditado");
	
	}
	
	public void setNomeValorAtributosEditado() {
    	mapAtributosEditado.put("nome-id", "CadastroEditado");
		
	}
	
	public void preencheCampos(Map<String, String> mapAtributos) throws InterruptedException {
		String conteudo = "";
		 
		for (String campo : mapAtributos.keySet()) {
			
			// se o tipo do locator do campo for id
			int index_hifen = campo.indexOf("-");
			if(campo.substring(index_hifen).equals("-id")){
				conteudo = mapAtributos.get(campo);
				campo = campo.substring(0,index_hifen);
				isVisibleTheanCleanAndFillField(campo, conteudo);
			}
		}
	}


}
