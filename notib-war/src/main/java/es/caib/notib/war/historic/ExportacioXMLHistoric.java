package es.caib.notib.war.historic;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

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


@Component
public class ExportacioXMLHistoric {

	public byte[] convertDadesOrgansGestors(
			Map<OrganGestorDto, List<HistoricAggregationOrganDto>> dades) throws JAXBException {
		RootOrganGestors root = DAOHistoric.mapRegistreOrganGestor(dades);
		JAXBContext context = JAXBContext.newInstance(RootOrganGestors.class);
		return this.getXMLBytes(context, root);
	}

	public byte[] convertDadesProcediment(
			Map<ProcedimentDto, List<HistoricAggregationProcedimentDto>> dades) throws JAXBException {
		RootProcediments root = DAOHistoric.mapRegistresProcediments(dades);
		JAXBContext context = JAXBContext.newInstance(RootProcediments.class);
		return this.getXMLBytes(context, root);
	}
	
	public byte[] convertDadesEstat(
			Map<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>> dades) throws JAXBException {
		RootEstat root = DAOHistoric.mapRegistresEstat(dades);
		JAXBContext context = JAXBContext.newInstance(RootEstat.class);
		return this.getXMLBytes(context, root);
	}
	
	public byte[] convertDadesGrup(
			Map<GrupDto, List<HistoricAggregationGrupDto>> dades) throws JAXBException {
		RootGrup root = DAOHistoric.mapRegistresGrup(dades);
		JAXBContext context = JAXBContext.newInstance(RootGrup.class);
		return this.getXMLBytes(context, root);
	}
	
	public byte[] convertDadesUsuari(
			Map<UsuariDto, List<HistoricAggregationUsuariDto>> dades) throws JAXBException {
		RootUsuari root = DAOHistoric.mapRegistresUsuari(dades);
		JAXBContext context = JAXBContext.newInstance(RootUsuari.class);
		return this.getXMLBytes(context, root);
	}

	private byte[] getXMLBytes(JAXBContext context, Object root) throws JAXBException {
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		StringWriter sw = new StringWriter();
		m.marshal(root, sw);
		return sw.toString().getBytes();
	}


}
