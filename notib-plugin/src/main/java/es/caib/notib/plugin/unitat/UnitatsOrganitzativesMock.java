/**
 * 
 */
package es.caib.notib.plugin.unitat;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class UnitatsOrganitzativesMock extends AbstractSalutPlugin implements UnitatsOrganitzativesPlugin {
	
	private static final String SERVEI_CERCA = "/rest/busqueda/";
	private static final String SERVEI_CATALEG = "/rest/catalogo/";
	private static final String SERVEI_UNITAT = "/rest/unidad/";
	private static final String SERVEI_ORGANIGRAMA = "/rest/organigrama/";
	private static final String WS_CATALEG = "ws/Dir3CaibObtenerCatalogos";
	private static final String WS_UNITATS = "ws/Dir3CaibObtenerUnidades";
	private static final String WS_OFICINA = "ws/Dir3CaibObtenerOficinas";

	private final Properties properties;

    private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

	public UnitatsOrganitzativesMock(Properties properties) {
		this.properties = properties;
	}

    public UnitatsOrganitzativesMock(Properties properties, boolean configuracioEspecifica) {
        this.properties = properties;
        this.configuracioEspecifica = configuracioEspecifica;
        urlPlugin = properties.getProperty("es.caib.notib.plugin.unitats.dir3.url");
        logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.UNITATS")));
    }

	@Override
	public Map<String, NodeDir3> organigramaPerEntitat(String codiEntitat) throws SistemaExternException {
		Map<String, NodeDir3>  organigrama = new HashMap<>();
		organigrama.put("E04975701", new NodeDir3());

		return organigrama;
	}

	@Override
	public Map<String, NodeDir3> organigramaPerEntitat(
			String pareCodi,
			Date fechaActualizacion,
			Date fechaSincronizacion) throws SistemaExternException {
		Map<String, NodeDir3> organigrama = new HashMap<String, NodeDir3>();
		return organigrama;
	}

	@Override
	public byte[] findAmbPareJson(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {
		return new byte[0];
	}

	@Override
	public List<NodeDir3> findAmbPare(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {
		return null;
	}

	@Override
	public NodeDir3 findAmbCodi(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {
		return null;
	}

	@Override
	public List<ObjetoDirectorio> unitatsPerEntitat(String codiEntitat, boolean inclourePare) throws SistemaExternException {
		List<ObjetoDirectorio> unitats = new ArrayList<ObjetoDirectorio>();
		return unitats;
	}
	
	@Override
	public String unitatDenominacio(String codiDir3) throws SistemaExternException {
		return "";
	}
	
	@Override
	public List<NodeDir3> cercaUnitats(
			String codi, 
			String denominacio,
			Long nivellAdministracio, 
			Long comunitatAutonoma, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long provincia, 
			String municipi) throws SistemaExternException {
		return null;
	}
	
	@Override
	public List<ObjetoDirectorio> unitatsPerDenominacio(String denominacio) throws SistemaExternException {
		return null;
	}
	
	@Override
	public List<NodeDir3> cercaOficines(
			String codi,
			String denominacio,
			Long nivellAdministracio,
			Long comunitatAutonoma,
			Long provincia,
			String municipi) throws SistemaExternException {
		return null;
	}
	
	@Override
	public List<CodiValor> nivellsAdministracio() throws SistemaExternException {
		return null;
	}
	
	public List<CodiValorPais> paisos() throws SistemaExternException {
		return null;
	}
	
	@Override
	public List<CodiValor> comunitatsAutonomes() throws SistemaExternException {
		return null;
	}
	
	@Override
	public List<CodiValor> provincies() throws SistemaExternException {
		return null;
	}
	
	@Override
	public List<CodiValor> provincies(String codiCA) throws SistemaExternException {
		return null;
	}


	@Override
	public List<CodiValor> localitats(String codiProvincia) throws SistemaExternException {
		return null;
	}
	
	@Override
	public List<OficinaSir> oficinesSIRUnitat(
			String unitat,
			Map<String, OrganismeDto> arbreUnitats) throws SistemaExternException {
		return null;
	}
	
	@Override
	public List<OficinaSir> getOficinesEntitat(String entitat) throws SistemaExternException {
		return null;
	}

}
