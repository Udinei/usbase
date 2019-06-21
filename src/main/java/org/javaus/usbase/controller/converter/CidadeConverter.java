package org.javaus.usbase.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import org.javaus.usbase.model.Cidade;

public class CidadeConverter implements Converter<String, Cidade> {

		@Override
		public Cidade convert(String codigo) {
			if(!StringUtils.isEmpty(codigo)){
				Cidade cidade = new Cidade();
				cidade.setCodigo(Long.valueOf(codigo));
				return cidade;
			}
			
			return null;
		}
		
	}
