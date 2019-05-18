package org.javaus.usbase.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.javaus.usbase.controller.page.PageWrapper;
import org.javaus.usbase.model.Usuario;
import org.javaus.usbase.repository.Grupos;
import org.javaus.usbase.repository.Usuarios;
import org.javaus.usbase.repository.filter.UsuarioFilter;
import org.javaus.usbase.service.CadastroUsuarioService;
import org.javaus.usbase.service.StatusUsuario;
import org.javaus.usbase.service.exception.EmailJaCadastradoException;
import org.javaus.usbase.service.exception.ImpossivelExcluirEntidadeException;
import org.javaus.usbase.service.exception.SenhaObrigatoriaUsuarioException;
import org.javaus.usbase.util.MessagesUtil;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {
	
	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	private Grupos grupos;
	
	@Autowired
	private CadastroUsuarioService cadastroUsuarioService;
	
	@Autowired
	private Usuarios usuarios; 
	
	@RequestMapping("/novo")
	public ModelAndView novo(Usuario usuario){
		 ModelAndView mv = new ModelAndView("usuario/CadastroUsuario");
		 mv.addObject("grupos", grupos.findAll());
		 return mv;
	}
	
	@PostMapping({ "/novo", "{\\+d}" })
	public ModelAndView salvar(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes){
		if(result.hasErrors()){
			return novo(usuario);
		}
	
		try {
			cadastroUsuarioService.salvar(usuario);	
			
		} catch (EmailJaCadastradoException e) {
			result.rejectValue("email", e.getMessage(), e.getMessage());
			return novo(usuario);
		}catch (SenhaObrigatoriaUsuarioException e){
			result.rejectValue("senha", e.getMessage(), e.getMessage());
			return novo(usuario);
		}
		
						
		//attributes.addFlashAttribute("mensagem", "Usuário salvo com sucesso");
		attributes.addFlashAttribute("mensagem", messagesUtil.getMessage("msg.salvo.sucesso", "Usuário"));
		return new ModelAndView("redirect:/usuarios/novo");
		
	}
	
	
	@GetMapping
	public ModelAndView pesquisar(UsuarioFilter usuarioFilter, @PageableDefault(size=3) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("usuario/PesquisaUsuarios");
		mv.addObject("grupos", grupos.findAll());

		PageWrapper<Usuario> paginaWrapper = new PageWrapper<>(usuarios.filtrar(usuarioFilter, pageable)
	    		                                  , httpServletRequest);	
		
	    mv.addObject("pagina", paginaWrapper);
				
		return mv;
	}
	
	/** @PutMapping - Atualização de objetos. Aguarda retorno de uma view. Como o metodo não retornara uma 
	    view. Apos a execução @ResponseStatus(HttpStatus.OK) e utilizado para retornar codigo http para browser
	    "codigo 200". A string status sera convertida automanticamento pelo spring para o Enum StatusUsuario */
	@PutMapping("/status") 	
	@ResponseStatus(HttpStatus.OK)  
	public void atualizarStatus(@RequestParam("codigos[]") Long[] codigos, @RequestParam("status") StatusUsuario statusUsuario){
		//Arrays.asList(codigos).forEach(System.out::println ); // print de array com java 8
		
		cadastroUsuarioService.alterarStatus(codigos, statusUsuario);
		
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo){
		Usuario usuario = usuarios.buscarComGrupos(codigo); // Busca usuarios no BD com grupos
		
		ModelAndView mv = novo(usuario); // chama tela de cadastro do usuario, preenchida com os dados do usuario, selecionado na pesquisa para edicao 
		mv.addObject(usuario);
		return mv;
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Usuario usuario){

		try {
			cadastroUsuarioService.excluir(usuario);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		return ResponseEntity.ok().build(); //retorna 200 ao cliente browser - de tudo ocorreu como esperado 
	}
}
