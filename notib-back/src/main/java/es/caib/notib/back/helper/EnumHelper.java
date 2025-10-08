/**
 * 
 */
package es.caib.notib.back.helper;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;



/**
 * Utilitat per a facilitar la generació de options dels camps
 * select procedents d'enumeracions a les pàgines JSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumHelper {

	private EnumHelper() {
		throw new IllegalStateException("EnumHelper no es pot instanciar");
	}

	public static List<HtmlOption> getOptionsForEnum(Class<?> enumeracio) {
		return getOptionsForEnum(enumeracio, null);
	}

	public static List<HtmlOption> getOptionsForEnum(Class<?> enumeracio, String textKeyPrefix) {
		return getOptionsForEnum(enumeracio, textKeyPrefix, null);
	}

	public static List<HtmlOption> getOptionsForEnum(Class<?> enumeracio, String textKeyPrefix, Enum<?>[] ignores) {

		if (!enumeracio.isEnum()) {
			return new ArrayList<>();
		}
		boolean incloure;
		List<HtmlOption> resposta = new ArrayList<>();
		for (var e: enumeracio.getEnumConstants()) {
			incloure = true;
			if (ignores != null) {
				for (var ignore: ignores) {
					if (e.equals(ignore)) {
						incloure = false;
						break;
					}
				}
			}
			if (incloure) {
				resposta.add(new HtmlOption(((Enum<?>)e).name(), (textKeyPrefix != null) ? textKeyPrefix + ((Enum<?>)e).name() : ((Enum<?>)e).name()));
			}
		}
		return resposta;
	}
	
	public static List<HtmlOption> getOrderedOptionsForEnum(Class<?> enumeracio, String textKeyPrefix, Enum<?>[] ordre) {

		List<HtmlOption> resposta = new ArrayList<>();
		if (!enumeracio.isEnum()) {
			return resposta;
		}
		for (var e: ordre) {
			resposta.add(new HtmlOption(e.name(), (textKeyPrefix != null) ? textKeyPrefix + e.name() : e.name()));
		}
		return resposta;
	}
	
	public static HtmlOption getOneOptionForEnum(Class<?> enumeracio, String textKeyPrefix) {

		if (!enumeracio.isEnum()) {
			return null;
		}
		HtmlOption resposta = null;
		for (var e : enumeracio.getEnumConstants()) {
			if (textKeyPrefix.contains(((Enum<?>) e).name())) {
				resposta = new HtmlOption(((Enum<?>) e).name(), textKeyPrefix);
			}
		}
		return resposta;
	}
	
	public static List<HtmlOption> getOptionsForArray(String[] values, String[] texts) {

		List<HtmlOption> resposta = new ArrayList<>();
		for (int i = 0; i < values.length; i++) {
			resposta.add(new HtmlOption(values[i], texts[i]));
		}
		return resposta;
	}

	@Data
	public static class HtmlOption implements Comparable<HtmlOption> {
		private String value;
		private String text;

		public HtmlOption(String value, String text) {
			this.value = value;
			this.text = text;
		}

		@Override
		public int compareTo(HtmlOption o) {
			return this.value.compareTo(o.getValue());
		}
	}

}
