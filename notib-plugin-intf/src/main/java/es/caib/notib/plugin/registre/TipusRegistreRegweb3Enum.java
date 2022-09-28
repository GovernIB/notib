package es.caib.notib.plugin.registre;

public enum TipusRegistreRegweb3Enum {
	REGISTRE_ENTRADA(1L),
	REGISTRE_SORTIDA(2L);
	
	private final Long valor;
	private TipusRegistreRegweb3Enum(Long valor) {
		this.valor = valor;
	}
	public Long getValor() {
		return valor;
	}
}
