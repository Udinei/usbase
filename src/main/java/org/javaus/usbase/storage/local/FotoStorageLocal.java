package org.javaus.usbase.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;

import org.javaus.usbase.storage.FotoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

// Ao subir a aplicação essa classe cria o diretorio padrao de gravacao das foto, no sistema de
// arquivos local na home do usuario dentro da subpasta .usbasefotos

//@Profile("local")
@Component
public class FotoStorageLocal implements FotoStorage {
	
	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
	private static final String THUMBNAIL_PREFIX = "thumbnail.";
	
	@Value("${usbase.foto-storage-local.local}")
	private Path local; // armazena endereco de gravacao da foto

	@Value("${usbase.foto-storage-local.url-base}")
	private String urlBase;
	
	@Override
	public String salvar(MultipartFile[] files) {
		String novoNome = null;
		
		if(files != null && files.length > 0){
			MultipartFile arquivo = files[0];
			
			// aplicando um prefixo no nome do arquivo e retornando o novo nome
			novoNome =  renomearArquivo(arquivo.getOriginalFilename());
			
			try {
				// salva o arquivo com um novo nome, usando um prefixo gerado pelo UUID da google
				arquivo.transferTo(new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
			
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando a foto", e);
			}
		}
		
		try {
			// redimensionando a foto com biblioteca Thumbnails, a foto do local definitivo, de 253x432 px para 40x68 px 
			Thumbnails.of(this.local.resolve(novoNome).toString()).size(40,68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
			
		} catch (IOException e) {
             throw new RuntimeException("Erro gerando thumbnail", e);
		}
		
		return novoNome;
	}

	@Override
	public byte[] recuperar(String nome) {
		try {
			return Files.readAllBytes(this.local.resolve(nome));
			
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto", e);
		}
	}
	
	
	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperar(THUMBNAIL_PREFIX + fotoCerveja);
	}
	
	
	@Override
	public void excluir(String foto) {
		try {
			Files.deleteIfExists(this.local.resolve(foto));
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + foto));
			
		} catch (IOException e) {
			logger.warn(String.format("Error apagando foto '%s'. Mensagem: %s", foto, e.getMessage()));
		}
	}
	
	
	@Override
	public String getUrl(String foto) {
		System.out.println(">>>>  esta passando aqui");
		return urlBase + foto;
	}
	
	@PostConstruct
	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			
			if(logger.isDebugEnabled()){
				logger.debug("Pastas criadas para salvar fotos");
				logger.debug("Pastas default: "+ this.local.toAbsolutePath());

			}
		}catch (IOException e) {
			throw new RuntimeException("Erro criando para para salvar foto", e);
		}
	}
	
}
