package org.javaus.usbase.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.javaus.usbase.controller.page.PageWrapper;
import org.javaus.usbase.dto.CervejaDTO;
import org.javaus.usbase.model.Cerveja;
import org.javaus.usbase.model.Origem;
import org.javaus.usbase.model.Sabor;
import org.javaus.usbase.repository.Cervejas;
import org.javaus.usbase.repository.Estilos;
import org.javaus.usbase.repository.filter.CervejaFilter;
import org.javaus.usbase.service.CadastroCervejaService;
import org.javaus.usbase.service.exception.ImpossivelExcluirEntidadeException;
import org.javaus.usbase.service.exception.SkuCervejaJaCadastradaException;
import org.javaus.usbase.util.MakeUrl;
import org.javaus.usbase.util.MessagesUtil;

@Controller
@RequestMapping("/cervejas")
public class CervejasController {
	
	@Autowired
	MakeUrl url;
	
	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	private Estilos estilos;
	
	@Autowired
	private CadastroCervejaService cadastroCervejaService;
	
	@Autowired
	private Cervejas cervejas; 

	private String urlFinalFoto = "";
		
	@RequestMapping("/nova")
	public ModelAndView nova(Cerveja cerveja){
		ModelAndView mv = new ModelAndView("cerveja/CadastroCerveja");
		
		mv.addObject("sabores", Sabor.values());
		mv.addObject("estilos", estilos.findAll());
		mv.addObject("origens", Origem.values());
		
		return mv;
	}
	
	/** Esse metodo trata requisicao de POST para mais de uma url.
	 *  a expressao regular "{\\d+}" permite qualquer digito na url ex: "cervejas/3"
	 *  alem da url "/nova"
	*/
	@PostMapping(value = { "/nova", "{\\d+}" })
	public ModelAndView salvar(@Valid Cerveja cerveja, BindingResult result, RedirectAttributes attributes){
		
		if(result.hasErrors()){ // caso tenha algum erro na pagina 
			return nova(cerveja); //coloca o objeto vindo da view na requisicao de resposta exibindo as msg de erro
		}
		
		try{	
		    cadastroCervejaService.salvar(cerveja); 
		    
		} catch (SkuCervejaJaCadastradaException e){ //captura a exception
	        // nome e atributo que deu problema na regra de negocio
			result.rejectValue("sku", e.getMessage(), e.getMessage()); // injeta mensagem na view do objeto
			return nova(cerveja);  // retorna objeto na view e exibe mensagem de dados duplicados
		}
	
		//attributes.addFlashAttribute("mensagem", "Cerveja salva com sucesso!"); // injeta msg, que permanece na view mesmo apos o redirect
		attributes.addFlashAttribute("mensagem", messagesUtil.getMessage("msg.salva.sucesso", "Cerveja"));
	
		return new ModelAndView("redirect:/cervejas/nova"); // carrega uma nova pagina com uma nova requisição
	}
	

	/** @PageableDefault(size=2) - Controla a quantidade de registro por pagina
	/ BindingResult é necessario ao usar GetMapping. Os valores ficam salvos na url
	  Pageable - Objeto que controla a paginação(numero da pagina) e ordenação da pesquisa 
	  HttpServletRequest - usado montar e a manter a url com o filtro atual */
	@GetMapping
	public ModelAndView pesquisar(CervejaFilter cervejaFilter, BindingResult result,
			@PageableDefault(size=2) Pageable pageable, HttpServletRequest httpServletRequest){
					
        // monta url final da foto da tabela de intens
   		urlFinalFoto = url.urlBrowser() + "/fotos/"; 

		ModelAndView mv = new ModelAndView("cerveja/PesquisaCervejas");
		mv.addObject("sabores", Sabor.values());
		mv.addObject("estilos", estilos.findAll());
		mv.addObject("origens", Origem.values());
		mv.addObject("urlFinalFoto", urlFinalFoto);		
		
	    PageWrapper<Cerveja> paginaWrapper = new PageWrapper<>(cervejas.filtrar(cervejaFilter, pageable)
	    		, httpServletRequest);	
			    
	    mv.addObject("pagina", paginaWrapper);
				
		return mv;
	}

	/** Recupera a cerveja ao digitar o sku ou nome */ 
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CervejaDTO> pesquisar(String skuOuNome){
		return cervejas.porSkuOuNome(skuOuNome);

	}
	
	
	// @ResponseBody permite retornar objetos no formato Json, mensagens de erro ou de sucesso ao JS
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cerveja cerveja){

		try {
			cadastroCervejaService.excluir(cerveja);
			
		} catch (ImpossivelExcluirEntidadeException e) {
			// retorna msg para browser que sera tratada no js
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		return ResponseEntity.ok().build(); //retorna 200 ao cliente browser - de tudo ocorreu como esperado 
	}
	
	
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Cerveja cerveja){
		ModelAndView mv = nova(cerveja);
		mv.addObject(cerveja);
		
		return mv;
	}
	
}
