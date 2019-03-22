package es.caib.notib.plugin.registre.sortida;

import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.exception.RegistrePluginException;
import es.caib.notib.core.api.ws.registre.RegistreAssentament;
import es.caib.notib.core.api.ws.registre.RespostaConsultaRegistre;
import es.caib.notib.core.api.ws.registre.RespostaJustificantRecepcio;

public interface RegistrePlugin {
	
	public String registrarSortida(
			RegistreAssentament registreSortida,
			String aplicacioNom,
			String aplicacioVersio,
			String entitat) throws RegistrePluginException;
	
	public RespostaConsultaRegistre comunicarAsientoRegistral(String codiDir3Entitat, AsientoRegistralBeanDto arb, Long tipusOperacio);
	
	public RespostaConsultaRegistre salidaAsientoRegistral(String codiDir3Entitat, AsientoRegistralBeanDto arb, Long tipusOperacio);
	
	public RespostaJustificantRecepcio obtenerJustificante(String codiDir3Entitat, String numeroRegistreFormatat, String llibre, Long tipusRegistre);
	
	public RespostaJustificantRecepcio obtenerOficioExterno(String codiDir3Entitat, String numeroRegistreFormatat, String llibre);
}
