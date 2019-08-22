package org.javaus.usbase.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.javaus.usbase.model.StatusVenda;
import org.javaus.usbase.model.Venda;
import org.javaus.usbase.repository.Vendas;
import org.javaus.usbase.service.event.venda.VendaCanceladaListener;
import org.javaus.usbase.service.event.venda.VendaEmitidaListener;

@Service
public class CadastroVendaService {

	@Autowired
	private Vendas vendas;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	// A ausencia da anotação abaixo provoca o erro: No transactional Entitmanager available
	@Transactional
	public Venda salvar(Venda venda){
		
		if(venda.isSalvarProibido()){
			//throw new RuntimeException("Usuário tentando salvar uma venda proibida");
		}
		
		// se for uma nova venda
		if(venda.isNova()){
			// seta data e hora atual da venda
			venda.setDataCriacao(LocalDateTime.now());
		
		// se estiver editanto usa a data de criacao da venda recuperada do banco
		}else{
			Venda vendaExistente = vendas.getOne(venda.getCodigo());
			venda.setDataCriacao(vendaExistente.getDataCriacao());
		}
				
		
		if(venda.getDataEntrega() != null){
			// formando a DataHoraEntrega, caso não tenha horario entrega, passe o horario do sistema 12:00  
			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega()
					, venda.getHorarioEntrega() != null ? venda.getHorarioEntrega() : LocalTime.NOON));
			}
		
		 return vendas.saveAndFlush(venda);
				
	}
	
	

	@Transactional
	public void emitir(Venda venda) {
		venda.setStatus(StatusVenda.EMITIDA);
		salvar(venda);
		
		
		
		// Dispara o evento para as classes listener que escutam esse evento, 
		// atualiza estoque apos uma venda uma venda emitida
		publisher.publishEvent(new VendaEmitidaListener(venda)); 
				
	}
	
	/** "@PreAuthorize" - define quem pode chamar esse metodo  
	 *   "#venda.usuario == principal.usuario" - somente o usuario adminstrador, quem criou a venda 
	    ou quem tem o papel - "or hasRole('CANCELAR_VENDA')" 
	    Para tanto a anottation @EnableGlobalMethodSecurity(prePostEnabled = true), que faz parte 
	    do controle de acesso do spring security, deve ter sido declara no arquivo SecurityConfig do spring,  */
	@PreAuthorize("#venda.usuario == principal.usuario or hasRole('CANCELAR_VENDA')") 
	@Transactional
	public void cancelar(Venda venda) {
		Venda vendaExistente = vendas.getOne(venda.getCodigo());
				
		// retorna produto para estoque, essa tecnica permite o desacomplemento do software 
		publisher.publishEvent(new VendaCanceladaListener(vendaExistente));
		
		vendaExistente.setStatus(StatusVenda.CANCELADA);
		vendas.save(vendaExistente);
	}



	
}
