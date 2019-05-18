package org.javaus.usbase.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.javaus.usbase.controller.page.PageWrapper;
import org.javaus.usbase.model.Cliente;
import org.javaus.usbase.model.TipoPessoa;
import org.javaus.usbase.repository.Clientes;
import org.javaus.usbase.repository.Estados;
import org.javaus.usbase.repository.filter.ClienteFilter;
import org.javaus.usbase.service.CadastroClienteService;
import org.javaus.usbase.service.exception.CpfCnpjClienteJaCadastradoException;
import org.javaus.usbase.service.exception.ImpossivelExcluirEntidadeException;
import org.javaus.usbase.util.MessagesUtil;

@Controller
@RequestMapping("/clientes")
public class ClientesController {
	
	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	private Estados estados;
	
	@Autowired
	private CadastroClienteService cadastroClienteService;
	
	@Autowired
	private Clientes clientes;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Cliente cliente){
		ModelAndView mv = new ModelAndView("cliente/CadastroCliente");
		mv.addObject("estados", estados.findAll());
		mv.addObject("tiposPessoa", TipoPessoa.values());
		
		return mv;
	}

	

	@PostMapping({ "/novo", "{\\+d}" }) 
	public ModelAndView salvar(@Valid Cliente cliente, BindingResult result, RedirectAttributes attributes){
		if(result.hasErrors()){
			return novo(cliente);
		}
		
		try {
	
			cadastroClienteService.salvar(cliente);	
		} catch (CpfCnpjClienteJaCadastradoException e) {
			result.rejectValue("cpfOuCnpj", e.getMessage(), e.getMessage());
			return novo(cliente);
			
		}
		
		//attributes.addFlashAttribute("mensagem", "Cliente salvo com sucesso!");
		attributes.addFlashAttribute("mensagem", messagesUtil.getMessage("msg.salvo.sucesso", "Cliente"));
		return new ModelAndView("redirect:/clientes/novo");
		
	}
	
	
	@GetMapping
	public ModelAndView pesquisar(ClienteFilter clienteFilter, BindingResult result, @PageableDefault(size = 2) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("cliente/PesquisaClientes");
		mv.addObject("tipoPessoa", TipoPessoa.values());

	    PageWrapper<Cliente> paginaWrapper = new PageWrapper<>(clientes.filtrar(clienteFilter, pageable)
	    		, httpServletRequest);	
		
	    mv.addObject("pagina", paginaWrapper);
	    
		return mv;
		
	}
	
	/**Esse metodo sera um GET acessado via Ajax, contentType application/json, por isso deve retornar @ResponseBody para o javascript 
	   interpretar objetos json no browser */
	@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody List<Cliente> pesquisar(String nome){
		validarTamanhoNome(nome);
		return clientes.findByNomeStartingWithIgnoreCase(nome);
	}


	/** Tratamento da quantidade minima de caracter que o usuario deve digitar no campo de pesquisa rapida de cliente */ 
	private void validarTamanhoNome(String nome) {
		if(StringUtils.isEmpty(nome) || nome.length() < 3){
			throw new IllegalArgumentException();
		}
		
	}
	

	/** Trata a Exception IllegalArgumentException  para esse controller quando a execao Exception for lancada
	 evitando a exibição da pilha da execcao no log da aplicação, retorna badRequest Erro 400 (erro provocado pelo usuario) */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> tratarIllegalArgumentException(IllegalArgumentException e){
		return ResponseEntity.badRequest().build();
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo){
		Cliente cliente = clientes.buscaComEndereco(codigo);
		
		ModelAndView mv = novo(cliente);
		mv.addObject(cliente);
		return mv;
		
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cliente cliente){

		try {
			cadastroClienteService.excluir(cliente);
		 	
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		return ResponseEntity.ok().build(); //retorna 200 ao cliente browser - de tudo ocorreu como esperado 
	}
	

	
}
