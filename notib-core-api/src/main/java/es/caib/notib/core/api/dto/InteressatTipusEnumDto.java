package es.caib.notib.core.api.dto;

/**
 * Enumerat que indica el tipus de comunicació de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum InteressatTipusEnumDto {
	
	ADMINISTRACIO(1L),
	FISICA(2L),
	JURIDICA(3L);

	private final Long val;

	InteressatTipusEnumDto(Long val) {
		this.val = val;
	}
	public Long getLongVal() {
		return val;
	}

	public static InteressatTipusEnumDto toEnum(Long text) {
		if (text == null)
			return null;
		for (InteressatTipusEnumDto valor : InteressatTipusEnumDto.values()) {
			if (text.equals(valor.getLongVal())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + InteressatTipusEnumDto.class.getName() + " per al text " + text);
	}

}
