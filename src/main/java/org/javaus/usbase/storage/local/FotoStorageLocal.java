package org.javaus.usbase.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import org.javaus.usbase.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;


//@Profile("local")
@Component
public class FotoStorageLocal implements FotoStorage {
	
	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
	private static final String THUMBNAIL_PREFIX = "thumbnail.";

	
	private Path local; // armazena endereco de gravacao da foto
	
	// Diretorio local de gravacao da foto no sistema de arquivos na home do usuario na subpasta .usbasefotos
	public FotoStorageLocal() {
		this(getDefault().getPath(System.getProperty("user.home"),".usbasefotos"));
		
	}

	
	public FotoStorageLocal(Path path) {
		this.local = path;
		criarPastas();
	}
	

	@Override
	public String salvar(MultipartFile[] files) {
		String novoNome = null;
		
		if(files != null && files.length > 0){
			MultipartFile arquivo = files[0];
			novoNome =  renomearArquivo(arquivo.getOriginalFilename());
			
			try {
				// salva o arquivo com novo nome
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
		return "http://localhost:8080/fotos/" + foto;
	}
	
	
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
