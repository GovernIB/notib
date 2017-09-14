package es.caib.notib.plugin.registre.sortida;

public interface RegistrePlugin {
	
	public RespostaAnotacioRegistre registrarSortida(
			RegistreAssentament registreSortida)
			throws RegistrePluginException;
	
}
