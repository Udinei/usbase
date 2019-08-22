var UsBase = UsBase || {};  // use o namespace UsBase ou  caso não exista crie 

UsBase.EstiloCadastroRapido = (function(){
	
	function EstiloCadastroRapido(){

		this.modal = $('#modalCadastroRapidoEstilo'); // acessa o html modalCadastroRapidoEstilo pelo id (#)
		this.botaoSalvar = this.modal.find('.js-modal-cadastro-estilo-salvar-btn'); // acessa botaoSalvar pela classe (.) usando o metodo find
		this.form = this.modal.find('form');      // acessa o form do modal usando metodo find
		this.url = this.form.attr('action');      // acessa a urlexpression (@{/estilos}) contida no atributo action do form 
		this.inputNomeEstilo = $('#nomeEstilo');  // acessa pelo id o input nomeEstilo
		this.containerMensagemErro = $('.js-mensagem-cadastro-rapido-estilo'); // acessa a div de mensagem de erro do Cadastro rapido de estilo
	}
	
	EstiloCadastroRapido.prototype.iniciar = function (){
	
		this.form.on('submit', function(event){ event.preventDefault() }); // cancela o evento padrao on.submit do form ou seja (não sera executado quando o usuario precionar enter no formulario rapido)
		this.modal.on('shown.bs.modal', onModalShow.bind(this));      // no evento 'shown.bs.modal'(quando o formulario for chamado) executa a funcao onModalShow - .bind(this) para a function ser executada no contexto  
		this.modal.on('hide.bs.modal', onModalClose.bind(this));      // no evento 'hide.bs.modal' (quando o formulario for fechado, clicado no botao cancelar) executa a funcao  onModalClose
		this.botaoSalvar.on('click', onBotaoSalvarClick.bind(this));  // no evento 'click' executa a funcao  onBotaoSalvarClick
	}
	
	// foca o curso no campo nomeEstilo do modal
	function onModalShow(){
		this.inputNomeEstilo.focus();  // this. para utilizar a variavel do contexto
	}
	
	// ao fechar o modal remove classe de erro da div
	function onModalClose(){
		this.inputNomeEstilo.val(''); // limpa campo nome estilo do modal
		this.containerMensagemErro.addClass('hidden');          // esconde a div de msg de erros
		this.form.find('.form-group').removeClass('has-error'); // remove a classe de erro do form  
	}
	
	// ao clicar no botao salvar, envia o formulario para a url em( th:action=@{/estilos}) no controller
	function onBotaoSalvarClick(){
	 var nomeEstilo = this.inputNomeEstilo.val().trim();  // remove os espacos em branco do campo
	    $.ajax({
		   url: this.url,
		   method:'POST',
		   contentType: 'application/json',
		   data: JSON.stringify({ nome: nomeEstilo }), //envia o objeto em Json com JSON.stringify({}) 
		   error: onErroSalvandoEstilo.bind(this),     // se houver um evento de erro no servidor chama a funcao onErroSalvandoEstilo(passandoContexto  this)
		   success: onEstiloSalvo.bind(this)           // se ocorrer um evento de sucesso no servidor chama a funcao onEstiloSalvo(passandoContexto this)
		   
	   	});
	}

	// exibir mensagem de erro
	function onErroSalvandoEstilo(obj){
		var mensagemErro = obj.responseText; // pegando msg de erro enviada pelo servidor
		this.containerMensagemErro.removeClass('hidden'); // exibe a div de erro do modal
		this.containerMensagemErro.html('<span>' + mensagemErro + '</span>'); // insere no modal um span com a msg de erro 
		this.form.find('.form-group').addClass('has-error');  // inserindo classe de erro para destacar o texto do campo nome em vermelho
	}

 
	function onEstiloSalvo(estilo){
		var comboEstilo = $('#estilo'); // acessa a comboBox estilo do cadastro da cerveja pelo id #
		comboEstilo.append('<option value=' + estilo.codigo +'>' + estilo.nome + '</option>'); 	// adiciona na comboBox o nome do estilo e o codigo do novo estilo informado no modal
		comboEstilo.val(estilo.codigo); // seta o codigo do estilo salvo, como atual 
		this.modal.modal('hide'); // esconde o modal
		
	}
	
	return EstiloCadastroRapido;
	
}());


$(function(){

	var estiloCadastroRapido = new UsBase.EstiloCadastroRapido();
	estiloCadastroRapido.iniciar();
	
// Abaixo o mesmo codigo javaScript antes da modularização para uso de objeto javaScript (EstiloCadastroRapido)
// para servir de exmplo
//	form.on('submit', function(event){ event.preventDefault() });
//	var modal = $('#modalCadastroRapidoEstilo');
//	var botaoSalvar = modal.find('.js-modal-cadastro-estilo-salvar-btn');
//	var form = modal.find('form');
//	form.on('submit', function(event){ event.preventDefault() });
//	var url = form.attr('action');
//	var inputNomeEstilo = $('#nomeEstilo');
//	var containerMensagemErro = $('.js-mensagem-cadastro-rapido-estilo');
//	
//	modal.on('shown.bs.modal', onModalShow);
//	modal.on('hide.bs.modal', onModalClose)
//	botaoSalvar.on('click', onBotaoSalvarClick)
//	
//	function onModalShow(){
//		inputNomeEstilo.focus();
//	}
	
	
//	function onModalClose(){
//		inputNomeEstilo.val('');
//		containerMensagemErro.addClass('hidden');
//		form.find('.form-group').removeClass('has-error');
//	}
//	
//	function onBotaoSalvarClick(){
//		var nomeEstilo = inputNomeEstilo.val().trim();
//	   $.ajax({
//		   url: url,
//		   method:'POST',
//		   contentType: 'application/json',
//		   data: JSON.stringify({ nome: nomeEstilo }),
//		   error: onErroSalvandoEstilo,
//		   success: onEstiloSalvo
//		   
//	   });
//	}
//	
//	function onErroSalvandoEstilo(obj){
//		var mensagemErro = obj.responseText;
//		containerMensagemErro.removeClass('hidden');
//		containerMensagemErro.html('<span>' + mensagemErro + '</span>');
//		form.find('.form-group').addClass('has-error');
//	}
//	
//	function onEstiloSalvo(estilo){
//		var comboEstilo = $('#estilo');
//		comboEstilo.append('<option value=' + estilo.codigo +'>' + estilo.nome + '</option>');
//		comboEstilo.val(estilo.codigo);
//		modal.modal('hide');
//		
//	}
	
});

