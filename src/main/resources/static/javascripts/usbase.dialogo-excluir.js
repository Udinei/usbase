UsBase = UsBase || {};

UsBase.DialogoExcluir = (function(){
	
	function DialogoExcluir(){
		this.exclusaoBtn = $('.js-exclusao-btn');
		
		// search - perquisa dados na url
		// ao iniciar, se na url atual tiver o parametro "excluido" na querystring,
		// exibe msg de sucesso em um alert swal (sweetalert plugin JS de alertas)
		if(window.location.search.indexOf('excluido') > -1){ 
			swal('Pronto', 'Excluído com sucesso!', 'success') 
		}
	}
	
	DialogoExcluir.prototype.iniciar = function(){
		this.exclusaoBtn.on('click', onExcluirClicado.bind(this));
	}
	
	function onExcluirClicado(){
		event.preventDefault();
		var botaoClicado = $(event.currentTarget);
		var url = botaoClicado.data('url');  // obtem url data:url="@{/cervejas/{codigo}(codigo=${cerveja.codigo})}"
		var objeto = botaoClicado.data('objeto');
		
		// swall (sweetalert)- plugin de mensagem para java script
		swal({                              
			title: 'Tem certeza?',
			 text: 'Excluir "' + objeto + '"? Você não poderá recuperar depois.',
			showCancelButton: true,
			confirmButtonColor: '#DD6B55',
			confirmButtonText: 'Sim, exclua agora!',
			closeOnConfirm: false	// aguarda o usuario confirmar antes de fechar o alert
			}, onExcluirConfirmado.bind(this, url)); // confirmou a exclusao   
		}
	
	
	    // executa exclusao do objeto via ajax, cujo codigo foi esta sendo passado na url 
		function onExcluirConfirmado(url){
			$.ajax({
				url: url,
				method: 'DELETE',
				success: onExcluidoSucesso.bind(this),   // apos excluir e retornar do servidor sem erros
				error: onErrorExcluir.bind(this)         // retorna do servidor com erro
			});
		}
		
		
		function onExcluidoSucesso(){
			var urlAtual = window.location.href; // pegando a url principal
			var separador = urlAtual.indexOf('?') > -1 ? '&' : '?'; // caso ja tiver separador de parametro "?" entao coloca "&" senao coloca "?"
			
			// caso o parametro "excluido" nao esteja na url, cria uma nova url com a urlAtual + separador "? ou &" + o parametro "excluido"   
			var novaUrl = urlAtual.indexOf('excluido') > -1 ? urlAtual : urlAtual + separador + 'excluido';
			
			window.location = novaUrl;  // direciona o browser para a nova url
		}
		
		// a msg de erro capturada aqui, vira do metodo excluir do controller
		function onErrorExcluir (e){
			swal('Oops', e.responseText, 'error');
		}
		
	return DialogoExcluir;
	
}());

$(function(){
	var dialogo = new UsBase.DialogoExcluir();
	dialogo.iniciar();
	
	
});
