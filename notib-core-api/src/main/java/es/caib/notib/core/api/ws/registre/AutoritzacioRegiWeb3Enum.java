package es.caib.notib.core.api.ws.registre;

public enum AutoritzacioRegiWeb3Enum {

	REGISTRE_ENTRADA(1L),
	REGISTRE_SORTIDA(2L),
	CONSULTA_REGISTRE_ENTRADA(3L),
	CONSULTA_REGISTRE_SORTIDA(4L);

	private final Long valor;
	private AutoritzacioRegiWeb3Enum(Long valor) {
		this.valor = valor;
	}
	public Long getValor() {
		return valor;
	}
//	private static final Map<Long, AutoritzacioRegiWeb3> lookup;
//	static {
//		lookup = new HashMap<Long, AutoritzacioRegiWeb3>();
//		for (AutoritzacioRegiWeb3 s: EnumSet.allOf(AutoritzacioRegiWeb3.class))
//			lookup.put(s.getValor(), s);
//	}
//	public static AutoritzacioRegiWeb3 valorAsEnum(Long valor) {
//		if (valor == null)
//			return null;
//        return lookup.get(valor); 
//    }
	
}
