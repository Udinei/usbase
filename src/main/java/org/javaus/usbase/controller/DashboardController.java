package org.javaus.usbase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import org.javaus.usbase.repository.Cervejas;
import org.javaus.usbase.repository.Clientes;
import org.javaus.usbase.repository.Vendas;

@Controller
public class DashboardController {

	@Autowired
	Vendas vendas;
	
	@Autowired
	Cervejas cervejas;
	
	@Autowired
	Clientes clientes;
	
	
	@GetMapping("/")
	public ModelAndView dashboard(){
		ModelAndView mv = new ModelAndView("Dashboard");
		mv.addObject("vendasNoAno", vendas.valorTotalNoAno());
		mv.addObject("vendasNoMes", vendas.valorTotalNoMes()); 
		mv.addObject("tickeMedio", vendas.valorTicketMedioNoAno());
		
		mv.addObject("valorItensEstoque",  cervejas.valorItensEstoque());
		mv.addObject("totalClientes", clientes.count());
		
		return mv;
	}
}
