package org.javaus.usbase.service.event.venda;

import org.javaus.usbase.model.Venda;


public class VendaEvent  {

	private Venda venda;

	public VendaEvent(Venda venda) {
		this.venda = venda;
	}

	public VendaEvent() {
		
	}

	public Venda getVenda() {
		return venda;
	}

		

}
