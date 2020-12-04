package es.caib.notib.war.historic;


import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
import es.caib.notib.war.historic.DAOHistoric.RootEstat;
import es.caib.notib.war.historic.DAOHistoric.RootGrup;
import es.caib.notib.war.historic.DAOHistoric.RootOrganGestors;
import es.caib.notib.war.historic.DAOHistoric.RootProcediments;
import es.caib.notib.war.historic.DAOHistoric.RootUsuari;


public class ExportacioJSONHistoric {
	
	
	public byte[] convertDadesOrgansGestors(
			Map<OrganGestorDto, List<HistoricAggregationOrganDto>> dades) throws JAXBException {
		RootOrganGestors root = DAOHistoric.mapRegistreOrganGestor(dades);
		return convertJSON(root);
	}
	
	public byte[] convertDadesProcediment(
			Map<ProcedimentDto, List<HistoricAggregationProcedimentDto>> dades) throws JAXBException {
		RootProcediments root = DAOHistoric.mapRegistresProcediments(dades);
		return convertJSON(root);
	}
	
	public byte[] convertDadesEstat(
			Map<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>> dades) throws JAXBException {
		RootEstat root = DAOHistoric.mapRegistresEstat(dades);
		return convertJSON(root);
	}
	
	public byte[] convertDadesGrup(
			Map<GrupDto, List<HistoricAggregationGrupDto>> dades) throws JAXBException {
		RootGrup root = DAOHistoric.mapRegistresGrup(dades);
		return convertJSON(root);
	}
	
	public byte[] convertDadesUsuari(
			Map<UsuariDto, List<HistoricAggregationUsuariDto>> dades) throws JAXBException {
		RootUsuari root = DAOHistoric.mapRegistresUsuari(dades);
		return convertJSON(root);
	}
	

	private  byte[]convertJSON(Object dades) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.writeValueAsBytes(dades);
		} catch (JsonProcessingException e) {
			return new byte[0];
		}
	}

//
//	public byte[] rawJacksonConversion(Object dades) {
//		// https://www.baeldung.com/jackson-xml-serialization-and-deserialization
//		XmlMapper xmlMapper = new XmlMapper();
//		try {
//			return xmlMapper.writeValueAsBytes(dades);
//		} catch (JsonProcessingException e) {
//			return new byte[0];
//		}
//	}
}
