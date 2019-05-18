package org.javaus.usbase.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import org.javaus.usbase.model.Usuario;
import org.javaus.usbase.repository.Usuarios;
import org.javaus.usbase.service.exception.EmailJaCadastradoException;
import org.javaus.usbase.service.exception.ImpossivelExcluirEntidadeException;
import org.javaus.usbase.service.exception.SenhaObrigatoriaUsuarioException;
import org.javaus.usbase.util.MessagesUtil;

@Service
public class CadastroUsuarioService {

	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	private Usuarios usuarios;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public void salvar(Usuario usuario){
		
		Optional<Usuario> usuarioExistente = usuarios.findByEmail(usuario.getEmail());
		
		// O email do usuario ja existe cadastrado e o usuario e diferente, Então envia msg com exception  
		if(usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)){
		    throw new EmailJaCadastradoException("E-mail já cadastrado");

		}
		
		
		if(usuario.isNovo() && StringUtils.isEmpty(usuario.getSenha())){
			throw new SenhaObrigatoriaUsuarioException("Senha é obrigatória para novo usuário");
		}
		
		
		// Nota: O "campo senha" na edição sempre vira em branco na tela (Seguranca do Spring Security),
		// Se novo Usuario ou ja existente e que tenha alterado a senha, informado uma nova senha 
		// entao criptografa a nova senha informada e seta no cadastro do usuario   
		if(usuario.isNovo() || !StringUtils.isEmpty(usuario.getSenha())){
			// criptogrando nova senha para novos usuarios ou ja cadastrados que alteraram a senha
			usuario.setSenha(this.passwordEncoder.encode(usuario.getSenha()));

			
		// 	caso contrario se Usuario já cadastrado, e não alterou a senha
		//	seta a mesma senha do usuario recuperada do banco, ja criptografada
		}else if(StringUtils.isEmpty(usuario.getSenha())) {
			Usuario tmp = new Usuario();
			if(usuario.getCodigo() != null){
				 // recupera usuario do bd
				 tmp = usuarios.findOne(usuario.getCodigo());
				 usuario.setSenha(tmp.getSenha());
				 usuario.setConfirmacaoSenha(tmp.getSenha());
			}
						
		}
		
		
		usuario.setConfirmacaoSenha(usuario.getSenha());
		
		
        /** Se o usuario não e novo e esta sendo editado, ele nao podera alterar seu proprio status 
		    (nesse caso o status não e exibido na tela). Para evitar erro de nullPointer, ao salvar, 
		    seta  o campo status com o status do usuario retornado do banco */
		if(!usuario.isNovo() && usuario.getAtivo() == null){
			usuario.setAtivo(usuarioExistente.get().getAtivo());
			
		}
		
		usuarios.save(usuario);
	}

	/** Metodo chamado por outro metodo PUT do controller, que executara um UPDATE
	   automaticamente no banco de dados, sem ser solicitado explicitamente um Update em JPQL ou SQL Nativa*/
	@Transactional
	public void alterarStatus(Long[] codigos, StatusUsuario statusUsuario) {
		statusUsuario.executar(codigos, usuarios);
	}

	
	@Transactional
	public void excluir(Usuario usuario) {
		try{
			usuarios.delete(usuario);
			usuarios.flush();
						
		}catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível apagar usuario. Esta sendo usado em algum cadastro.");
			
		}
		
	}
}
