package es.caib.notib.war.historic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationEstatDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationGrupDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationOrganDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationProcedimentDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationUsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricFiltreDto;
import es.caib.notib.core.api.service.HistoricService;


@Component
public class ExportacioActionHistoric {

	@Autowired
	private HistoricService historicService;
	@Autowired
	private ExportacioXMLHistoric exportacioXMLHistoric;

	public FitxerDto exportarHistoricOrgansGestors(Long entitatId, HistoricFiltreDto filtre, String format) throws Exception {
		Map<OrganGestorDto, List<HistoricAggregationOrganDto>> dades = historicService.getHistoricsByOrganGestor(
				entitatId,
				filtre);

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		if (format.equals("json")) {
			fileContent = (new ExportacioJSONHistoric()).convertDadesOrgansGestors(dades);
			fitxer = new FitxerDto("historicOrgansGestors.json", "application/json", fileContent);

		} else if (format.equals("xlsx")) {
			fileContent = (new ExportacioExcelOrganGestorHistoric()).convertDadesOrgansGestors(dades);
			fitxer = new FitxerDto("historicOrgansGestors.xls", "application/vnd.ms-excel", fileContent);

		} else if (format.equals("odf")) {
			fileContent = (new ExportacioDocHistoric()).convertDadesOrgansGestors(dades);
			fitxer = new FitxerDto("historicOrgansGestors.ods", "application/vnd.oasis.opendocument.text", fileContent);

		} else if (format.equals("xml")) {
			fileContent = exportacioXMLHistoric.convertDadesOrgansGestors(dades);
			fitxer = new FitxerDto("historicOrgansGestors.xml", "application/xml", fileContent);

		} else {
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}
	

	public FitxerDto exportarHistoricProcediments(HistoricFiltreDto filtre, String format) throws Exception {
		Map<ProcedimentDto, List<HistoricAggregationProcedimentDto>> dades = historicService.getHistoricsByProcediment(
				filtre);
		
		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
		case "json":
			fileContent = (new ExportacioJSONHistoric()).convertDadesProcediment(dades);
			fitxer = new FitxerDto("historicProcediments.json", "application/json", fileContent);
			break;
		case "xlsx":
			fileContent = (new ExportacioExcelProcediment()).convertDadesProcediment(dades);
			fitxer = new FitxerDto("historicProcediments.xls", "application/vnd.ms-excel", fileContent);
			break;
		case "odf":
			fileContent = (new ExportacioDocHistoric()).convertDadesProcediment(dades);
			fitxer = new FitxerDto("historicProcediments.ods", "application/vnd.oasis.opendocument.text", fileContent);
			break;
		case "xml":
			fileContent = exportacioXMLHistoric.convertDadesProcediment(dades);
			fitxer = new FitxerDto("historicProcediments.xml", "application/xml", fileContent);
			break;
		default:
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}
	public FitxerDto exportarHistoricEstats(HistoricFiltreDto filtre, String format) throws Exception {
		Map<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>> dades = historicService.getHistoricsByEstat(
				filtre);
		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
		case "json":
			fileContent = (new ExportacioJSONHistoric()).convertDadesEstat(dades);
			fitxer = new FitxerDto("historicEstats.json", "application/json", fileContent);
			break;
		case "xlsx":
			fileContent = (new ExportacioExcelEstatNotificacio()).convertDadesEstat(dades);
			fitxer = new FitxerDto("historicEstats.xls", "application/vnd.ms-excel", fileContent);
			break;
		case "odf":
			fileContent = (new ExportacioDocHistoric()).convertDadesEstat(dades);
			fitxer = new FitxerDto("historicEstats.ods", "application/vnd.oasis.opendocument.text", fileContent);
			break;
		case "xml":
			fileContent = exportacioXMLHistoric.convertDadesEstat(dades);
			fitxer = new FitxerDto("historicEstats.xml", "application/xml", fileContent);
			break;
		default:
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}

	public FitxerDto exportarHistoricGrups(Long entitatId, HistoricFiltreDto filtre, String format) throws Exception {
		Map<GrupDto, List<HistoricAggregationGrupDto>> dades = historicService.getHistoricsByGrup(
				entitatId,
				filtre);
		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
		case "json":
			fileContent = (new ExportacioJSONHistoric()).convertDadesGrup(dades);
			fitxer = new FitxerDto("historicGrups.json", "application/json", fileContent);
			break;
		case "xlsx":
			fileContent = (new ExportacioExcelGrup()).convertDadesGrup(dades);
			fitxer = new FitxerDto("historicGrups.xls", "application/vnd.ms-excel", fileContent);
			break;
		case "odf":
			fileContent = (new ExportacioDocHistoric()).convertDadesGrup(dades);
			fitxer = new FitxerDto("historicGrups.ods", "application/vnd.oasis.opendocument.text", fileContent);
			break;
		case "xml":
			fileContent = exportacioXMLHistoric.convertDadesGrup(dades);
			fitxer = new FitxerDto("historicGrups.xml", "application/xml", fileContent);
			break;
		default:
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}

	public FitxerDto exportarHistoricUsuaris(
			String[] usuarisCodi,
			HistoricFiltreDto filtre,
			String format) throws Exception {
		Map<UsuariDto, List<HistoricAggregationUsuariDto>> dades = historicService.getHistoricsByUsuariAplicacio(
				filtre,
				Arrays.asList(usuarisCodi));

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
		case "json":
			fileContent = (new ExportacioJSONHistoric()).convertDadesUsuari(dades);
			fitxer = new FitxerDto("historicUsuaris.json", "application/json", fileContent);
			break;
		case "xlsx":
			fileContent = (new ExportacioExcelUsuariHistoric()).convertDadesUsuari(dades);
			fitxer = new FitxerDto("historicUsuaris.xls", "application/vnd.ms-excel", fileContent);
			break;
		case "odf":
			fileContent = (new ExportacioDocHistoric()).convertDadesUsuari(dades);
			fitxer = new FitxerDto("historicUsuaris.ods", "application/vnd.oasis.opendocument.text", fileContent);
			break;
		case "xml":
			fileContent = exportacioXMLHistoric.convertDadesUsuari(dades);
			fitxer = new FitxerDto("historicUsuaris.xml", "application/xml", fileContent);
			break;
		default:
			throw new Exception("Unsuported file format");
		}
		return fitxer;
	}
}
