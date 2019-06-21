package org.javaus.usbase.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.javaus.usbase.controller.page.PageWrapper;
import org.javaus.usbase.model.Cidade;
import org.javaus.usbase.repository.Cidades;
import org.javaus.usbase.repository.Estados;
import org.javaus.usbase.repository.filter.CidadeFilter;
import org.javaus.usbase.service.CadastroCidadeService;
import org.javaus.usbase.service.exception.ImpossivelExcluirEntidadeException;
import org.javaus.usbase.service.exception.NomeCidadeJaCadastradoException;
import org.javaus.usbase.util.MessagesUtil;

@Controller
@RequestMapping("/cidades")
public class CidadesController {
	
	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	private Cidades cidades;
	
	@Autowired
	private Estados estados;
	
	@Autowired
	private CadastroCidadeService cadastroCidadeService;
	
	 
	
	@RequestMapping("/nova")
	public ModelAndView nova(Cidade cidade){
		ModelAndView mv = new ModelAndView("cidade/CadastroCidade");
		mv.addObject("estados", estados.findAll());
	
		return mv;
	}

	// metodo executado pelo ajax chamado via javascript - vira a url /usbase/cidades?estado=2
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@Cacheable(value = "cidades", key="#codigoEstado") // fazendo cache de cidades, tendo como chave codigo do estado
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado(
			@RequestParam(name = "estado", defaultValue = "-1") Long codigoEstado){
		
		try {
			Thread.sleep(1000); // teste - aguarda 1 segundo antes de pesquisar
		} catch (Exception e) {
			// TODO: handle exception
		}
		return cidades.findByEstadoCodigo(codigoEstado);
		
	}
	
	@PostMapping({ "/novo", "{\\+d}" }) 
	@CacheEvict(value="cidades", key = "#cidade.estado.codigo", condition = "#cidade.temEstado()") // limpando de cache
	public ModelAndView salvar(@Valid Cidade cidade, BindingResult result, RedirectAttributes attributes){
		if(result.hasErrors()){
			return nova(cidade);
		}
	
	
		try {
			cadastroCidadeService.salvar(cidade);
			
		} catch (NomeCidadeJaCadastradoException e) {
			//utilizar rejectValue para destacar o campo a ser revisto
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return nova(cidade);
			
		}
		
		//attributes.addFlashAttribute("mensagem", "Cidade salva com sucesso!");
		// envia msg para a view, da entidade "Estilo" salva com sucesso 
		attributes.addFlashAttribute("mensagem", messagesUtil.getMessage("msg.salva.sucesso", "Cidade"));
				
		return new ModelAndView("redirect:/cidades/nova");
		
	}
	
	@GetMapping
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult result, @PageableDefault(size = 2) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("cidade/PesquisaCidades");
		mv.addObject("estados", estados.findAll());
		
	    PageWrapper<Cidade> paginaWrapper = new PageWrapper<>(cidades.filtrar(cidadeFilter, pageable), httpServletRequest);	
		
	    mv.addObject("pagina", paginaWrapper);
	    
		return mv;
		
	}
	
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo){
		Cidade cidade = cidades.buscarComEstados(codigo);
				
		ModelAndView mv = nova(cidade);
		
		mv.addObject(cidade);
		return mv;
		
	}
	

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cidade cidade){

		try {
			cadastroCidadeService.excluir(cidade);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		return ResponseEntity.ok().build(); //retorna 200 ao cliente browser - de tudo ocorreu como esperado 
	}
	
}
