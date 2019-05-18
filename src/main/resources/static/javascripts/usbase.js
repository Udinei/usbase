var UsBase = UsBase || {}; // use o namespace UsBase ou  caso não exista crie (objeto global)


UsBase.MaskMoney = (function(){ // model patterns 
	
	function MaskMoney(){    // criando uma função construtora, colocar as  inicializacoes
		this.decimal =$('.js-decimal');
		this.plain =$('.js-plain');
	}
	
	MaskMoney.prototype.enable = function(){ // comportamento da funcao
		//this.decimal.maskMoney({ decimal: ',' , thousands: '.' });
    	//this.plain.maskMoney({ precision:0, thousands: '.' });
		this.decimal.maskNumber({ decimal: ',' , thousands: '.' });
    	this.plain.maskNumber({ integer: true, thousands: '.' });
	}
	
	return MaskMoney;
	
}()); 

UsBase.MaskPhoneNumber = (function() {
	
	function MaskPhoneNumber(){
		this.inputPhoneNumber = $('.js-phone-number');
		
	}
	
	MaskPhoneNumber.prototype.enable = function(){
		
		var maskBehavior = function (val) {
			  return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
			};
			
		var	options = {
			  onKeyPress: function(val, e, field, options) {
			      field.mask(maskBehavior.apply({}, arguments), options);
			    }
		    };
		
		this.inputPhoneNumber.mask(maskBehavior, options);
	}
	
	return MaskPhoneNumber;
	
}());


UsBase.MaskCep = (function() {
	
	function MaskCep() {
		this.inputCep = $('.js-cep');
	}
	
	MaskCep.prototype.enable = function() {
		this.inputCep.mask('00.000-000');
	}
	
	return MaskCep;
	
}());

// Essa function trata a digitacao de horas e minutos invalidos em campos do tipo hh:mm
// Ex: 24:59, 30:01, 00:60, 05:122:
UsBase.MaskHoraMinuto= (function(){
	
	function MaskHoraMinuto(){
		this.inputHoraMinuto = $('.js-hora-minuto');
	}
	
	MaskHoraMinuto.prototype.enable = function(){
		
		var mask = function (val) {
		    val = val.split(":");
		    return (parseInt(val[0]) > 19) ? "HZ:M0" : "H0:M0";
		}

		pattern = {
		    onKeyPress: function(val, e, field, options) {
		        field.mask(mask.apply({}, arguments), options);
		    },
		    translation: {
		        'H': { pattern: /[0-2]/, optional: false },
		        'Z': { pattern: /[0-3]/, optional: false },
		        'M': { pattern: /[0-5]/, optional: false }
		    },
		    placeholder: 'hh:mm'
		};
		
		this.inputHoraMinuto.mask(mask, pattern);
	}
	
	   return MaskHoraMinuto;
}());

UsBase.MaskDate = (function(){
	
	function MaskDate(){
		this.inputDate = $('.js-date');
		
	}
	
	MaskDate.prototype.enable = function(){
		this.inputDate.mask('00/00/0000');
		this.inputDate.datepicker({
		 	orientation : 'bottom', 
			language : 'pt-BR',
			highlight : false,
			autoclose : true
		});
	}
	
	return MaskDate;
	
}());

// envia o token csrf em toda requisicao Ajax
UsBase.Security = (function(){
	
	function Security(){
		this.token = $('input[name=_csrf]').val();
		this.header = $('input[name=_csrf_header').val();
	}
	
	Security.prototype.enable = function() {
		// toda requisição ajax que for feita pelo JQuery, deve executar codigo abaixo
		$(document).ajaxSend(function(event, jqxhr, settings){
			jqxhr.setRequestHeader(this.header, this.token);
		}.bind(this));
	}
	
	return Security;
	
}());


// funcao estatica
UsBase.formatarMoeda = function(valor){
	numeral.locale('pt-br');
	return numeral(valor).format('0,0.00');
}

UsBase.recuperarValor = function(valorFormatado){
	 return numeral(valorFormatado).value();
	
}

UsBase.formataInteiroComPonto = function(valor){
	// retorna inteiro > 1000 com ponto ex: 1.000 
	return numeral(valor).format('0,000'); 
}


$(function(){
	var maskMoney = new UsBase.MaskMoney();
	maskMoney.enable();
	
	var maskPhoneNumber = new UsBase.MaskPhoneNumber();
	maskPhoneNumber.enable();
	
	var maskCep = new UsBase.MaskCep();
	maskCep.enable();
	
	var maskHoraMinuto = new UsBase.MaskHoraMinuto();
	maskHoraMinuto.enable();
	
	var maskDate = new UsBase.MaskDate();
	maskDate.enable();
	
	var security = new UsBase.Security();
	security.enable();
	
	
});



/* Antes da implementação acima
$(function(){
	var decimal =$('.js-decimal');
	decimal.maskMoney({ decimal: ',' , thousands: '.' });
	
	// numeros inteiros
	var plain =$('.js-plain');
	plain.maskMoney({ precision:0, thousands: '.' });
});
*/
