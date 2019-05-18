var UsBase = UsBase || {};

UsBase.MascaraCpfCnpj = (function() {

	function MascaraCpfCnpj(){
		this.radioTipoPessoa = $('.js-radio-tipo-pessoa');
		this.labelCpfCnpj = $('[for=cpfOuCnpj]');
		this.inputCpfCnpj = $('#cpfOuCnpj');
	}
	
	MascaraCpfCnpj.prototype.iniciar = function(){
		this.radioTipoPessoa.on('change', onTipoPessoaAlterado.bind(this));
		var tipoPessoaSelecionda = this.radioTipoPessoa.filter(':checked')[0]
		if(tipoPessoaSelecionda){
			aplicarMascara.call(this, $(tipoPessoaSelecionda));
		}
	}
	
	function onTipoPessoaAlterado(evento){
		var tipoPessoaSelecionada = $(evento.currentTarget);
		aplicarMascara.call(this, tipoPessoaSelecionada);
		this.inputCpfCnpj.val('');
	}
	
	function aplicarMascara(tipoPessoaSelecionada){
		this.labelCpfCnpj.text(tipoPessoaSelecionada.data('documento'));
		this.inputCpfCnpj.mask(tipoPessoaSelecionada.data('mascara'));
		this.inputCpfCnpj.removeAttr('disabled');
		
	}
	
	
	return MascaraCpfCnpj;
	
}());


$(function() {
     var mascaraCpfCnpj = new UsBase.MascaraCpfCnpj();
     mascaraCpfCnpj.iniciar();

});
