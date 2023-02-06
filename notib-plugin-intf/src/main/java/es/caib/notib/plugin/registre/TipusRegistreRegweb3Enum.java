package es.caib.notib.plugin.registre;

import lombok.Getter;

@Getter
public enum TipusRegistreRegweb3Enum {
	REGISTRE_ENTRADA(1L),
	REGISTRE_SORTIDA(2L);
	
	private final Long valor;
	TipusRegistreRegweb3Enum(Long valor) {
		this.valor = valor;
	}
}
