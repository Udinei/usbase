package org.javaus.usbase.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.javaus.usbase.controller.page.PageWrapper;
import org.javaus.usbase.model.Estilo;
import org.javaus.usbase.repository.Estilos;
import org.javaus.usbase.repository.filter.EstiloFilter;
import org.javaus.usbase.service.CadastroEstiloService;
import org.javaus.usbase.service.exception.ImpossivelExcluirEntidadeException;
import org.javaus.usbase.service.exception.NomeEstiloJaCadastradoException;
import org.javaus.usbase.util.MessagesUtil;

@Controller
@RequestMapping("/estilos")  // mapeamento do controller para evitar ter que colocar em cada metodo "/estilos"
public class EstilosController {
	
	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	CadastroEstiloService cadastroEstiloService;
	
	
	@Autowired
	private Estilos estilos;
	
	
	@RequestMapping("/novo")
	public ModelAndView novo(Estilo estilo){
		// ModelAndView serve para especificar a view que será renderizada e quais os dados ela utilizará para isso.
		return new ModelAndView("estilo/CadastroEstilo");
	}
	

	@PostMapping({ "/novo", "{\\+d}" }) 
	public ModelAndView salvar(@Valid Estilo estilo, BindingResult result,  RedirectAttributes attributes){
		// validacao do bean validation
		if(result.hasErrors()){
			return novo(estilo);
		}
		
		try{
			cadastroEstiloService.salvar(estilo);	
			
		} catch (NomeEstiloJaCadastradoException e){ //captura a exception
            // nome é atributo que deu problema na regra de negocio
			result.rejectValue("nome", e.getMessage(), e.getMessage()); // injeta mensagem na view do objeto
			return novo(estilo);  // retorna objeto na view e exibe mensagem de dados duplicados
		}
	
		attributes.addFlashAttribute("mensagem", messagesUtil.getMessage("msg.salvo.sucesso", "Estilo"));
		return new ModelAndView("redirect:/estilos/novo");
	}
	
	/** Para usar Json colocar no maven a API do Jackson para converter objetos Json em objetos Java 
	 *  @RequestBody -  serializa o objeto retornado para JSON, chega no metodo em JSON e ja é convertido em objeto java:   
	 *  @ResponseEntity<?> - envia uma resposta completa, com status, com cabeçalho e corpo, muita utilizada me microservice */
	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid Estilo estilo, BindingResult result){
		// em result vira os erro de validação dos campos em tela definidos nas entidades model
		if(result.hasErrors()){
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());	// envia msg de erros para a view
		}
		
		// Uma forma elegante e generica de tratar um exception seria o uso de um Handler
		// como nesse caso ao salvar um estilo e retornar um ResponseEntity caso haja um erro 
		// o mesmo sera tratado pela classe ControllerAdviceExceptionHandler em vez de usar um try/catch
		// - Caso o estilo ja exista cadastrado 
		estilo = cadastroEstiloService.salvar(estilo);
          
		// retorna codigo 200 ao browser ResponseEntity.ok
		return ResponseEntity.ok(estilo);
		
	}
	
	@GetMapping
	public ModelAndView pesquisar(EstiloFilter estiloFilter, BindingResult result, @PageableDefault(size = 2) Pageable pageable, HttpServletRequest httpServletRequest	){
		ModelAndView mv = new ModelAndView("estilo/PesquisaEstilos");
		 
		PageWrapper<Estilo> paginaWrapper = new PageWrapper<>(estilos.filtrar(estiloFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
		
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo){
		Estilo estilo = estilos.getOne(codigo);
		
		ModelAndView mv = novo(estilo);
		mv.addObject(estilo);
		return mv;
		
	}
	
	// Esse metodo demonstra a forma de integração do sprig-data com o spring-jpa no acesso a dados
	// esta sendo  usado na interface da classe "Estilos" o jpaRepository.
	// Para converter o "codigo" passado no PathVariable para o objeto Estilo,
	// para tanto deve primeiro ser configurado "criar" no Webconfig o Bean DomainClassConverter
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Estilo estilo){

		try {
			cadastroEstiloService.excluir(estilo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		return ResponseEntity.ok().build(); //retorna 200 ao cliente browser - de tudo ocorreu como esperado 
	}
	
}
