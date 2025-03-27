package es.caib.notib.api.interna.controller;

import es.caib.notib.api.interna.openapi.interficies.ConsultaApiRestV2Intf;
import es.caib.notib.api.interna.util.EnhancedBooleanEditor;
import es.caib.notib.api.interna.util.EnhancedDateEditor;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.consulta.Arxiu;
import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import es.caib.notib.logic.intf.dto.ApiConsulta;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.util.CaseInsensitiveEnumEditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/consulta/v3")
public class ConsultaApiRestV3Controller extends NotificacioApiRestBaseController implements ConsultaApiRestV2Intf {

	@Autowired
	private EnviamentService enviamentService;
	@Autowired
	private NotificacioService notificacioService;

	private static final String PATH = "/consulta/v2";

	@GetMapping(value="/enviaments/{dniTitular}", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaV2 enviamnetsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).estatFinal(null).basePath(basePath)
				.pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).idioma(lang != null ? lang : Idioma.CA)
				.visibleCarpeta(visibleCarpeta == null || visibleCarpeta).build();
		return enviamentService.findEnviamentsV2(consulta);
	}


	@GetMapping(value="/comunicacions/{dniTitular}", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaV2 comunicacionsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(EnviamentTipus.COMUNICACIO).estatFinal(null).basePath(basePath)
								.pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).idioma(lang != null ? lang : Idioma.CA)
								.visibleCarpeta(visibleCarpeta == null || visibleCarpeta).build();
		return enviamentService.findEnviamentsV2(consulta);
	}

	@GetMapping(value="/notificacions/{dniTitular}", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaV2 notificacionsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(EnviamentTipus.NOTIFICACIO).estatFinal(null).basePath(basePath)
								.pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).idioma(lang != null ? lang : Idioma.CA)
								.visibleCarpeta(visibleCarpeta == null || visibleCarpeta).build();
		try {
			return enviamentService.findEnviamentsV2(consulta);
		} catch (Exception e) {
			return RespostaConsultaV2.builder().error(true).errorDescripcio(e.getMessage()).errorData(new Date()).build();
		}
	}

	@GetMapping(value="/enviaments/{dniTitular}/pendents", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaV2 enviamentsPendentsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).estatFinal(false).basePath(basePath)
				.pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).idioma(lang != null ? lang : Idioma.CA)
				.visibleCarpeta(visibleCarpeta == null || visibleCarpeta).build();
		try {
			return enviamentService.findEnviamentsV2(consulta);
		} catch (Exception e) {
			return RespostaConsultaV2.builder().error(true).errorDescripcio(e.getMessage()).errorData(new Date()).build();
		}
	}

	@GetMapping(value="/comunicacions/{dniTitular}/pendents", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaV2 comunicacionsPendentsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(EnviamentTipus.COMUNICACIO).estatFinal(false).basePath(basePath)
								.pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).idioma(lang != null ? lang : Idioma.CA)
								.visibleCarpeta(visibleCarpeta == null || visibleCarpeta).build();
		try {
			return enviamentService.findEnviamentsV2(consulta);
		} catch (Exception e) {
			return RespostaConsultaV2.builder().error(true).errorDescripcio(e.getMessage()).errorData(new Date()).build();
		}
	}

	@GetMapping(value="/notificacions/{dniTitular}/pendents", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaV2 notificacionsPendentsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(EnviamentTipus.NOTIFICACIO).estatFinal(false).basePath(basePath)
								.pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).idioma(lang != null ? lang : Idioma.CA)
								.visibleCarpeta(visibleCarpeta == null || visibleCarpeta).build();
		try {
			return enviamentService.findEnviamentsV2(consulta);
		} catch (Exception e) {
			return RespostaConsultaV2.builder().error(true).errorDescripcio(e.getMessage()).errorData(new Date()).build();
		}
	}

	@GetMapping(value="/enviaments/{dniTitular}/llegides", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaV2 enviamentsLlegitsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(EnviamentTipus.COMUNICACIO).estatFinal(true).basePath(basePath)
				.pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).idioma(lang != null ? lang : Idioma.CA)
				.visibleCarpeta(visibleCarpeta == null || visibleCarpeta).build();
		try {
			return enviamentService.findEnviamentsV2(consulta);
		} catch (Exception e) {
			return RespostaConsultaV2.builder().error(true).errorDescripcio(e.getMessage()).errorData(new Date()).build();
		}
	}

	@GetMapping(value="/comunicacions/{dniTitular}/llegides", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaV2 comunicacionsLlegidesByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(EnviamentTipus.COMUNICACIO).estatFinal(true).basePath(basePath)
								.pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).idioma(lang != null ? lang : Idioma.CA)
								.visibleCarpeta(visibleCarpeta == null || visibleCarpeta).build();
		try {
			return enviamentService.findEnviamentsV2(consulta);
		} catch (Exception e) {
			return RespostaConsultaV2.builder().error(true).errorDescripcio(e.getMessage()).errorData(new Date()).build();
		}
	}

	@GetMapping(value="/notificacions/{dniTitular}/llegides", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaV2 notificacionsLlegidesByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(EnviamentTipus.NOTIFICACIO).estatFinal(true).basePath(basePath)
								.pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).idioma(lang != null ? lang : Idioma.CA)
								.visibleCarpeta(visibleCarpeta == null || visibleCarpeta).build();
		try {
			return enviamentService.findEnviamentsV2(consulta);
		} catch (Exception e) {
			return RespostaConsultaV2.builder().error(true).errorDescripcio(e.getMessage()).errorData(new Date()).build();
		}
	}

	@GetMapping(value="/document/{notificacioId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Arxiu> getDocument(HttpServletRequest request, @PathVariable Long notificacioId) {

		Arxiu document;
		ArxiuDto arxiu = null;
		var status = HttpStatus.OK;
		try {
			arxiu = notificacioService.getDocumentArxiu(notificacioId);
		} catch (Exception e) {
			log.debug("No s'ha trobat el document per a la notificació amb identificador " + notificacioId);
		}
		if (arxiu != null && arxiu.getContingut() != null) {
			if (arxiu.getContentType() == null && arxiu.getNom() != null) {
				var type = arxiu.getNom().endsWith(".pdf") ? "application/pdf" : (arxiu.getNom().endsWith(".pdf") ? "application/zip" : null);
				arxiu.setContentType(type);
			}
			var contingutDocumentBasse64 = Base64.getEncoder().encodeToString(arxiu.getContingut());
			document = Arxiu.builder().nom(arxiu.getNom()).mediaType(arxiu.getContentType()).contingut(contingutDocumentBasse64).build();
			return new ResponseEntity<>(document, status);
		}
		document = Arxiu.builder().error(true).missatgeError("No s'ha trobat el document.").build();
		status = HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(document, status);
	}

	@GetMapping(value="/certificacio/{enviamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Arxiu> getCertificacio(HttpServletRequest request, @PathVariable Long enviamentId) {

		Arxiu certificacio;
		ArxiuDto arxiu = null;
		var status = HttpStatus.OK;
		try {
			arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
		} catch (Exception e) {
			log.debug("No s'ha trobat la certificació per a l'enviament amb identificador " + enviamentId);
		}
		if (arxiu != null && arxiu.getContingut() != null) {
			var contingutCertificacioBasse64 = Base64.getEncoder().encodeToString(arxiu.getContingut());
			certificacio = Arxiu.builder().nom(arxiu.getNom()).mediaType(arxiu.getContentType()).contingut(contingutCertificacioBasse64).build();
			return new ResponseEntity<>(certificacio, status);
		}
		certificacio = Arxiu.builder().error(true).missatgeError("No s'ha trobat la certificació.").build();
		status = HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(certificacio, status);
	}

	@GetMapping(value="/justificant/{enviamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Arxiu> getJustificant(HttpServletRequest request, @PathVariable Long enviamentId) {

		Arxiu justificant = null;
		byte[] contingutJustificant = null;
		var status = HttpStatus.OK;
		try {
			contingutJustificant = enviamentService.getDocumentJustificant(enviamentId);
		} catch (Exception e) {
			log.debug("No s'ha trobat el justificant per a l'enviament amb identificador " + enviamentId);
		}
		if (contingutJustificant != null) {
			var contingutJustificantBasse64 = Base64.getEncoder().encodeToString(contingutJustificant);
			justificant = Arxiu.builder().nom("Justificant").mediaType(com.google.common.net.MediaType.PDF.toString()).contingut(contingutJustificantBasse64).build();
			return new ResponseEntity<>(justificant, status);
		}
		justificant = Arxiu.builder().error(true).missatgeError("No s'ha trobat el justificant.").build();
		status = HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(justificant, status);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Idioma.class, new CaseInsensitiveEnumEditor(Idioma.class));
		binder.registerCustomEditor(Boolean.class, new EnhancedBooleanEditor("si", "no", true));
		binder.registerCustomEditor(Date.class, new EnhancedDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
}
