package org.javaus.usbase.config.format;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Locale;

import org.springframework.format.Formatter;



public abstract class TemporalFormatter<T extends Temporal> implements Formatter<T>{

	@Override
	public String print(T temporal, Locale locale) {
		DateTimeFormatter formatter = getDataTimeFormatter(locale);
		return formatter.format(temporal);
	}

	@Override
	public T parse(String text, Locale locale) throws ParseException {
		DateTimeFormatter formatter = getDataTimeFormatter(locale);
		return parse(text, formatter);
	}

	private DateTimeFormatter getDataTimeFormatter(Locale locale){
		return DateTimeFormatter.ofPattern(pattern(locale));
	}
	public abstract T parse(String text, DateTimeFormatter formatter );
	public abstract String pattern(Locale locale);
	
}
