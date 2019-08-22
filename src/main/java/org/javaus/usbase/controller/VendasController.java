package org.javaus.usbase.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.javaus.usbase.controller.page.PageWrapper;
import org.javaus.usbase.controller.validator.VendaValidator;
import org.javaus.usbase.dto.VendaMes;
import org.javaus.usbase.dto.VendaOrigem;
import org.javaus.usbase.mail.Mailer;
import org.javaus.usbase.model.Cerveja;
import org.javaus.usbase.model.ItemVenda;
import org.javaus.usbase.model.StatusVenda;
import org.javaus.usbase.model.TipoPessoa;
import org.javaus.usbase.model.Venda;
import org.javaus.usbase.repository.Cervejas;
import org.javaus.usbase.repository.Vendas;
import org.javaus.usbase.repository.filter.VendaFilter;
import org.javaus.usbase.security.UsuarioSistema;
import org.javaus.usbase.service.CadastroVendaService;
import org.javaus.usbase.session.TabelasItensSession;
import org.javaus.usbase.util.MakeUrl;
import org.javaus.usbase.util.MessagesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/vendas")
public class VendasController {
			
	@Autowired
	MakeUrl url;
	
	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private CadastroVendaService cadastroVendaService; 
	
	@Autowired
	private TabelasItensSession tabelaItens;
	
	// componente de validacao para a venda
	@Autowired
	private VendaValidator vendaValidator;
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private Mailer mailer;
	
	private String urlFinalFoto = "";
	
	/** Adiciona validade ao controller.
	 *  Ao encontrar um @Valid em qualquer metodo dessa classe, usa esse validador para validar os atributos
	 *  o parametro passado ao @InitBinder "venda" informa que esse validador é somente para classe Venda, caso
	 *  esse parametro nao seja passado o Spring tentara Validar a classe VendaFilter também provocando um erro */
//	@InitBinder("venda")
//	public void inicializarValidador(WebDataBinder binder){
//	   binder.setValidator(vendaValidator);	
//	}
	
		
	
	@GetMapping("/nova")
	public ModelAndView nova(Venda venda){
		 ModelAndView  mv = new  ModelAndView("venda/CadastroVenda");
		 
		 
		 // cria uuid de controle da sessão de visao do usuario
		 setUuid(venda);
		 
		 mv.addObject("itens", venda.getItens());
		 mv.addObject("valorFrete", venda.getValorFrete());
		 mv.addObject("valorDesconto", venda.getValorDesconto());
		 mv.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
				 
		return mv;
	}


	/** Executa esse metodo se tiver na url o paramento salvar */
	@PostMapping(value ="/nova", params="salvar")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		
		// adiciona primeiro os itens na venda para depois validar a venda
		validarVenda(venda);
		
		// A anotation @Valid foi removido dos parametros do metodo, para facilitar a validação dos itens da venda
		// passando diretamente o resulta para ser validado na classe VendaValidator
		vendaValidator.validate(venda, result);
		if(result.hasErrors()){
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.salvar(venda);
		//attributes.addFlashAttribute("mensagem", "Venda salva com sucesso" );
		attributes.addFlashAttribute("mensagem", messagesUtil.getMessage("msg.salva.sucesso", "Venda"));
		return new ModelAndView("redirect:/vendas/nova");
	}


	/** Executa esse metodo se tiver na url o paramento emitir */
	@PostMapping(value = "/nova", params = "emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		validarVenda(venda);
        
		// aplicando o validador "@Valid" apos inserir os itens 
		vendaValidator.validate(venda, result);
		if(result.hasErrors()){
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva e emitida com sucesso" );
		return new ModelAndView("redirect:/vendas/nova");
	}
	
	/** Executa esse metodo se tiver na url o paramento enviarEmail */
	@PostMapping(value="/nova", params="enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes
			, @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		validarVenda(venda);
        		
		// aplicando o validador "@Valid" apos inserir os itens 
		vendaValidator.validate(venda, result);
		if(result.hasErrors()){
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		venda = cadastroVendaService.salvar(venda);
			
		mailer.enviar(venda); // envia email de forma assincrona
				
		attributes.addFlashAttribute("mensagem", String.format("Venda nº %d salva com sucesso e e-mail enviado", venda.getCodigo()));
		return new ModelAndView("redirect:/vendas/nova");
	}
	
	
	// envio de email da venda emitida
	@PostMapping(value="/nova", params="enviarEmailVendaEmitida")
	public ModelAndView enviarEmailVendaEmitida(Venda venda, BindingResult result, RedirectAttributes attributes
			, @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		validarVenda(venda);
        		
		// aplicando o validador "@Valid" apos inserir os itens 
		vendaValidator.validate(venda, result);
		if(result.hasErrors()){
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		venda = cadastroVendaService.salvar(venda);
		mailer.enviar(venda); // envia email de forma assincrona
				
		attributes.addFlashAttribute("mensagem", String.format("Venda nº %d salva com sucesso e e-mail enviado", venda.getCodigo()));
		return new ModelAndView("redirect:/vendas/nova");
	}
	
	
	/**
	 * Esse metodo faz a busca do item selecionado no autocomplete
	 * */
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid){
		Cerveja cerveja = cervejas.getOne(codigoCerveja);
		
		// monta url final da foto da tabela de intens
		urlFinalFoto = url.urlBrowser() + "/fotos/" + cerveja.getUrlThumbnailFoto();
        cerveja.setUrlThumbnailFoto(urlFinalFoto);
		tabelaItens.adicionarItem(uuid, cerveja, 1);
		
		return mvTabelaItensVenda(uuid, null);
	}
	

	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("codigoCerveja") Cerveja cerveja, @RequestParam Venda codigoVenda, @RequestParam Integer quantidade, @RequestParam String uuid){
		Venda venda = null;
		
		// recupera a venda da lista de itens, a venda sera usada para controle da exibição do bloco de exclusao de itens
		if(codigoVenda != null){	
			venda = vendas.getOne(codigoVenda.getCodigo());
		}
		
		if((cerveja.getQuantidadeEstoque() - quantidade) < 0){
			System.out.println("ops quantidade acima do estoque");
		};
		
		tabelaItens.alterarQuantidadeItens(uuid, cerveja, quantidade);
		
		return mvTabelaItensVenda(uuid, venda);
	}

	
	/**  Esse metodo demonstra uma das formas de integração do sprig-data com o spring-jpa no acesso a dados
	  *  @PathVariable("codigoCerveja") Cerveja cerveja - faz uma pesquisa findOne direta, isso é possivel porque esta 
	  *  sendo usado na interface da classe "Cerveja" o jpaRepository, usa a string "codigoCerveja"
	  *  passada como parametro para pesquisar e retornar objeto Cerveja.
	  *  Nota: Para tanto deve-se primeiro configurar "criar" no Webconfig o Bean DomainClassConverter
	  */
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable String uuid){
		tabelaItens.excluirItem(uuid, cerveja);
		
		return mvTabelaItensVenda(uuid, null);
		
	}
	
	private ModelAndView mvTabelaItensVenda(String uuid, Venda venda) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		List<ItemVenda> lista = tabelaItens.ordenaListaItensPorSku(tabelaItens.getItens(uuid));
		
		mv.addObject("itens", lista);
    	mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		
		if(venda != null){
			mv.addObject("venda", venda);	
		}
						
		return mv;
	}
	


	private void validarVenda(Venda venda) {
			// recupera os itens da venda da tabela de intens, e seta a venda nos itens, ao salvar a venda 
			venda.adicionarItens(tabelaItens.ordenaListaItensPorSku(tabelaItens.getItens(venda.getUuid())));
			
			// calcula o valor total da venda
			venda.calcularValorTotal();
			
					
	}
	
		
	@GetMapping
	private ModelAndView pesquisar(VendaFilter vendaFilter, BindingResult result, @PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("venda/PesquisaVendas");
		mv.addObject("todosStatus", StatusVenda.values());
		mv.addObject("tiposPessoa", TipoPessoa.values());

		
	    PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendas.filtrar(vendaFilter, pageable)
	    		, httpServletRequest);	
		
	    mv.addObject("pagina", paginaWrapper);
	    
		return mv;
	}	
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo){
		// recupera a venda com os itens
		Venda venda = vendas.buscarComItens(codigo);

		// gera um uuid de sessao do usuario, para a venda recuperada caso nao exista
		setUuid(venda);
		
        
		// para cada item da venda, adiciona na Tabelas Itens da Session, o uuid, o item e a quantidade
		for(ItemVenda item : venda.getItens()){
			
			// monta url final da foto da cerveja
			urlFinalFoto = url.urlBrowser() + "/fotos/" + item.getCerveja().getUrlThumbnailFoto();
			item.getCerveja().setUrlThumbnailFoto(urlFinalFoto);
			
			tabelaItens.adicionarItem(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}
			
		ModelAndView mv = nova(venda);
		mv.addObject(venda);
		return mv;
		
	}


	@PostMapping(value = "/nova", params = "cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result
			,RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		
		try {
			cadastroVendaService.cancelar(venda);
			
		// acesso negado ao tentar cancelar uma venda	
		} catch (AccessDeniedException e) {
			ModelAndView mv = new ModelAndView("error");
			mv.addObject("status", 403);
			return mv;
		}
		
		//cadastroVendaService.cancelar(venda);
		attributes.addFlashAttribute("mensagem", "Venda cancelada com sucesso!");
		
		// apos cancelar retorna para a pagina de vendas com os campos desabilitados
		return new ModelAndView("redirect:/vendas/" + venda.getCodigo());
				
	}
	
	/** @ResponseBody Habilita o retorno, de uma lista de objetos no formato Json*/
	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalPorMes(){
		return vendas.totalPorMes();
	}
	
	@GetMapping("/porOrigem")
	public @ResponseBody List<VendaOrigem> vendasPorNacionalidade() {
		return this.vendas.totalPorOrigem();
	}
	
	/**Gera um uuid de controle de sessão de visao do usuario, ou seja para cada nova aba aberta o usuario tera
	   um identificador de sessao */
	private void setUuid(Venda venda) {
		// se um uuid de sessao do usuario não foi criado quando da criacao ou edicao de uma venda   
		if(StringUtils.isEmpty(venda.getUuid())){
			 // entao gera aleatoreamente um uuid usando API UUID do javaUtil e seta na venda  
			 venda.setUuid(UUID.randomUUID().toString()); 
		
		 }
	}
			
}
