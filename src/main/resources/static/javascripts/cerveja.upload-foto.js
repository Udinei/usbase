var UsBase = UsBase || {};

UsBase.UploadFoto = (function(){
		
	// funcao construtora
	function UploadFoto(){   
		
		//inicializacao de atributos(variaveis)
		// acessando elementos hidden da foto contidos no html do cadastro de cerveja
		this.inputNomeFoto = $('input[name=foto]'); 
		this.inputContentType = $('input[name=contentType]');
		this.novaFoto = $('input[name=novaFoto]');
		this.inputUrlFoto = $('input[name=urlFoto]');
		
		
		// recuperando html template html com codigo do handlebars, inserido no cadastro de cerveja
		// por <th:block th:replace="hbs/FotoCerveja"></th:block>
		this.htmlFotoCervejaTemplate = $('#foto-cerveja').html(); 		
		this.template = Handlebars.compile(this.htmlFotoCervejaTemplate); // compilando template handlebar
		
		// div onde sera exido a foto do cerveja
		this.containerFotoCerveja = $('.js-container-foto-cerveja');
		
		this.uploadDrop = $('#upload-drop');
		this.imgLoading = $('.js-img-loading');
		
	}
	
	
	// Esse metodo é executado ao voltar do servidor, apos clicar em salvar
	// funcao de execucao de comportamento ao selecionar a foto para dentro do componente do UIkit
	UploadFoto.prototype.iniciar = function (){  
	        
		    // Ao clicar em salvar seta os parametros do upload da foto em settings (objeto usado pelo UIkit) que sera enviado via Ajax 
			var settings = {
				type: 'json',    // formato do envio JSON
				filelimit: 1,   // quantidade de arquivos que sera enviados de uma vez
				allow: '*.(jpg|jpeg|png)',   // tipos de arquivos permitidos enviar no contentType
				action: this.containerFotoCerveja.data('url-fotos'),  // mapeamento do metodo a ser executado no controler, para tanto foi adicionando na div o atributo (th:attr="data-url-fotos=@{/fotos}) 
				complete: onUploadCompleto.bind(this), // ao completar o envio da foto executa o metodo 
				beforeSend: adicionarCsrfToken,    // evitando ataque de um clique CSRF
				loadstart: onLoadStart.bind(this)
	        }
			
			// Executa quando a foto foi selecionada, enviando-a para o servidor via Ajax
			UIkit.uploadSelect($('#upload-select'), settings);
			
            // Executa quando a foto foi arrastada e enviando-a para o servidor via Ajax 						
			UIkit.uploadDrop(this.uploadDrop, settings);
			
			// Apos a validacao dos campos da tela, se usuario já selecionou uma foto, o campo urlFoto (hidden) foi preenchido 
			// entao carrega a foto novamente usando a url contida no campo urlFoto 
			if(this.inputNomeFoto.val()){
				// call força acesso a todo contexto da classe 
				renderizarFoto.call(
						this, {  // call e this, forca para que funcao seja executada no context da funcao
								nome: this.inputNomeFoto.val(),
								contentType: this.inputContentType.val(),
								url: this.inputUrlFoto.val() // obtem a url da foto, carregada anteriormente
						       }
				);  
			}
			
	}
	 
	function onLoadStart(){
		this.imgLoading.removeClass('hidden'); // exibe icone de animação carregando imagem
		
	}
	
	
	// faz upload de uma nova foto
	function onUploadCompleto(resposta){  
		this.novaFoto.val('true');
		this.inputUrlFoto.val(resposta.url); // obtem a url da foto
		this.imgLoading.addClass('hidden'); // esconde animação de carregando imagem
		renderizarFoto.call(this,resposta);  
		
	}
	
	// carrega a foto para tela de cadastro de cerveja
	function renderizarFoto(resposta){
		this.inputNomeFoto.val(resposta.nome)
		this.inputContentType.val(resposta.contentType);
			
		this.uploadDrop.addClass('hidden');  // esconde a div upload-drop que contem o link de selecao da foto

		// monta url da foto, com a url atual para exibição correta da foto
		var newURL =  window.location.protocol + "//" + 
			          window.location.host + 
			          "/fotos/" +
			          resposta.nome;

		var htmlFotoCerveja = this.template({url: newURL});
		
		
		//var htmlFotoCerveja = this.template({url: resposta.url}); // seta a url da foto na variavel {{url}} do handlebar, que esta no hmtl fotoCerveja
		this.containerFotoCerveja.append(htmlFotoCerveja);        // insere o html fotoCerveja no lugar da div upload-drop que esta hidden, que fica
		                                                          // dentro da div com a classe .js-container-foto-cerveja 
				
		$('.js-remove-foto').on('click', onRemoverFoto.bind(this));
	}
	
	function onRemoverFoto(){
		$('.js-foto-cerveja').remove(); // remove trecho html inserido pelo handerbar
		this.uploadDrop.removeClass('hidden') // exibe novamente a div upload-drop que contem o link de selecao da foto
		this.inputNomeFoto.val('');  
		this.inputContentType.val('');
		this.novaFoto.val('false');
	}
	
	// adicionando a proteção de um click  na requisicao (token do csrf)
	function adicionarCsrfToken(xhr){
		var token = $('input[name=_csrf]').val();  // pegando o input pelo atributo name
		var header = $('input[name=_csrf_header').val();
		xhr.setRequestHeader(header, token);
	}
	
	return UploadFoto;
	
})();


 $(function(){  // apos carregar pagina executa o metodo iniciar da classe UsBase
	var uploadFoto = new UsBase.UploadFoto();
	uploadFoto.iniciar();
 });
