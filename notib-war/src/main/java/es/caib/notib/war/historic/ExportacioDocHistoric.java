package es.caib.notib.war.historic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.tools.generic.DateTool;

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
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.ITemplateEngine;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.velocity.internal.VelocityTemplateEngine;

public class ExportacioDocHistoric {

	public byte[] convertDadesOrgansGestors(
			Map<OrganGestorDto, List<HistoricAggregationOrganDto>> dades) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the
		// registry
		IXDocReport report = getReportInstance("/es/caib/notib/war/templates/template_historic_organ_ca.odt");

		// 2) Create Java model context
		IContext context = report.createContext();
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());

		// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		report.process(context, bos);

		return bos.toByteArray();
	}

	public byte[] convertDadesProcediment(
			Map<ProcedimentDto, List<HistoricAggregationProcedimentDto>> dades) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the
		// registry
		IXDocReport report = getReportInstance("/es/caib/notib/war/templates/template_historic_procediment_ca.odt");

		// 2) Create Java model context
		IContext context = report.createContext();
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());

		// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		report.process(context, bos);

		return bos.toByteArray();
	}
	
	public byte[] convertDadesEstat(
			Map<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>> dades) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the
		// registry
		IXDocReport report = getReportInstance("/es/caib/notib/war/templates/template_historic_estat_ca.odt");

		// 2) Create Java model context
		IContext context = report.createContext();
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());

		// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		report.process(context, bos);

		return bos.toByteArray();
	}
	
	public byte[] convertDadesGrup(
			Map<GrupDto, List<HistoricAggregationGrupDto>> dades) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the
		// registry
		IXDocReport report = getReportInstance("/es/caib/notib/war/templates/template_historic_grup_ca.odt");

		// 2) Create Java model context
		IContext context = report.createContext();
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());

		// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		report.process(context, bos);

		return bos.toByteArray();
	}
	
	public byte[] convertDadesUsuari(
			Map<UsuariDto, List<HistoricAggregationUsuariDto>> dades) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the
		// registry
		IXDocReport report = getReportInstance("/es/caib/notib/war/templates/template_historic_usuaris_ca.odt");

		// 2) Create Java model context
		IContext context = report.createContext();
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());

		// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		report.process(context, bos);

		return bos.toByteArray();
	}

	private IXDocReport getReportInstance(String filename) throws IOException, XDocReportException {
		InputStream in = this.getClass().getResourceAsStream(filename);
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
	
		Properties properties = new Properties();
		properties.setProperty("resource.loader", "class");
		properties.setProperty(
				"class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		properties.setProperty("runtime.log.logsystem.class", 
				"org.apache.velocity.runtime.log.NullLogChute");
		
		ITemplateEngine templateEngine = new VelocityTemplateEngine(properties);
				
		report.setTemplateEngine(templateEngine);
		return report;
	}
    
}
