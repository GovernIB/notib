/**
 * 
 */
package es.caib.notib.war.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.IdiomaEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.ValidArxiuDocument;
import es.caib.notib.war.validation.ValidConcepte;
import es.caib.notib.war.validation.ValidCsvDocument;
import es.caib.notib.war.validation.ValidDescripcio;
import es.caib.notib.war.validation.ValidNotificacio;
import es.caib.notib.war.validation.ValidUrlDocument;
import es.caib.notib.war.validation.ValidUuidDocument;

/**
 * Command per al manteniment de notificacions manuals (V2).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */	
@ValidCsvDocument (
		fieldName 	= "tipusDocument",
		dependFieldName = "documentArxiuCsv"
)

@ValidUuidDocument (
		fieldName 	= "tipusDocument",
		dependFieldName = "documentArxiuUuid"
)

@ValidUrlDocument (
		fieldName 	= "tipusDocument",
		dependFieldName = "documentArxiuUrl"
)

@ValidArxiuDocument (
		fieldName 	= "tipusDocument",
		dependFieldName = "contingutArxiu"
)
@ValidConcepte (
		fieldName = "concepte"
)
@ValidDescripcio (
		fieldName = "descripcio"
)
@ValidNotificacio
public class NotificacioCommandV2 {

	private Long id;
	@NotEmpty @Size(max=9)
	private String emisorDir3Codi;
	private String organGestor;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	@NotEmpty @Size(max=255)
	private String concepte;
	@Size(max=1000)
	private String descripcio;
	private Date enviamentDataProgramada;
	private int retard;
	@NotNull
	private Date caducitat;
	private DocumentCommand document;
	private Long procedimentId;
	private String procedimentNom;
	private String codiSia;
	private Long grupId;
	@Size(max=64)
	private String usuariCodi;
	private TipusDocumentEnumDto tipusDocument;
	private String tipusDocumentSelected;
	private String tipusDocumentDefault;
	private String documentArxiuCsv;
	private String documentArxiuUuid;
	private String documentArxiuUrl;
	private MultipartFile arxiu;
	private String oficina;
	private String llibre;
	private String extracte;
	private String docFisica;
	private IdiomaEnumDto idioma;
	private String tipusAssumpte;
	@Size(max=256)
	private String numExpedient;
	private String refExterna;
	private String codiAssumpte;
	private String observacions;
	private boolean eliminarLogoPeu;
	private boolean eliminarLogoCap;
	private ServeiTipusEnumDto serveiTipus;
	@Valid
	private PersonaCommand titular;
//	private PersonaCommand destinatari = new PersonaCommand();
	@Valid
	private List<PersonaCommand> destinataris = new ArrayList<PersonaCommand>();
	@Valid
	private List<EnviamentCommand> enviaments = new ArrayList<EnviamentCommand>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public NotificaEnviamentTipusEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(NotificaEnviamentTipusEnumDto enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	@Size(max=255)
	public String getConcepte() {
		return concepte;
	}
	public TipusDocumentEnumDto getTipusDocument() {
		return tipusDocument;
	}
	public void setTipusDocument(TipusDocumentEnumDto tipusDocument) {
		this.tipusDocument = tipusDocument;
	}
	public String getTipusDocumentDefault() {
		return tipusDocumentDefault;
	}
	public void setTipusDocumentDefault(String tipusDocumentDefault) {
		this.tipusDocumentDefault = tipusDocumentDefault;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public String getDocumentArxiuCsv() {
		return documentArxiuCsv;
	}
	public void setDocumentArxiuCsv(String documentArxiuCsv) {
		this.documentArxiuCsv = documentArxiuCsv;
	}
	public String getDocumentArxiuUuid() {
		return documentArxiuUuid;
	}
	public void setDocumentArxiuUuid(String documentArxiuUuid) {
		this.documentArxiuUuid = documentArxiuUuid;
	}
	public String getDocumentArxiuUrl() {
		return documentArxiuUrl;
	}
	public void setDocumentArxiuUrl(String documentArxiuUrl) {
		this.documentArxiuUrl = documentArxiuUrl;
	}
	public MultipartFile getArxiu() {
		return arxiu;
	}
	public void setArxiu(MultipartFile arxiu) {
		this.arxiu = arxiu;
	}
	public byte[]  getContingutArxiu() {
		try {
			return arxiu.getBytes();
		} catch (IOException e) {
			logger.error("No s'ha pogut recuperar el contingut del fitxer per validar");
		}
		return null;
	}
	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	public String getOrganGestor() {
		return organGestor;
	}
	public void setOrganGestor(String organGestor) {
		this.organGestor = organGestor;
	}
	public NotificacioComunicacioTipusEnumDto getComunicacioTipus() {
		return comunicacioTipus;
	}
	public void setComunicacioTipus(NotificacioComunicacioTipusEnumDto comunicacioTipus) {
		this.comunicacioTipus = comunicacioTipus;
	}
	@Size(max=1000)
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public int getRetard() {
		return retard;
	}
	public void setRetard(int retard) {
		this.retard = retard;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public void setCaducitat(Date caducitat) {
		this.caducitat = caducitat;
	}
	public Long getProcedimentId() {
		return procedimentId;
	}
	public void setProcedimentId(Long procedimentId) {
		this.procedimentId = procedimentId;
	}
	public String getProcedimentNom() {
		return procedimentNom;
	}
	public void setProcedimentNom(String procedimentNom) {
		this.procedimentNom = procedimentNom;
	}
	public String getCodiSia() {
		return codiSia;
	}
	public void setCodiSia(String codiSia) {
		this.codiSia = codiSia;
	}
	public Long getGrupId() {
		return grupId;
	}
	public void setGrupId(Long grupId) {
		this.grupId = grupId;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public ServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(ServeiTipusEnumDto serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public PersonaCommand getTitular() {
		return titular;
	}
	public void setTitular(PersonaCommand titular) {
		this.titular = titular;
	}
//	public PersonaCommand getDestinatari() {
//		return destinatari;
//	}
//	public void setDestinatari(PersonaCommand destinatari) {
//		this.destinatari = destinatari;
//	}
	public List<PersonaCommand> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<PersonaCommand> destinataris) {
		this.destinataris = destinataris;
	}
	public List<EnviamentCommand> getEnviaments() {
		return enviaments;
	}
	public void setEnviaments(List<EnviamentCommand> enviaments) {
		this.enviaments = enviaments;
	}
	public DocumentCommand getDocument() {
		return document;
	}
	public void setDocument(DocumentCommand document) {
		this.document = document;
	}
	public String getOficina() {
		return oficina;
	}
	public String getLlibre() {
		return llibre;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}
	public String getExtracte() {
		return extracte;
	}
	public String getDocFisica() {
		return docFisica;
	}
	public IdiomaEnumDto getIdioma() {
		return idioma;
	}
	public String getTipusAssumpte() {
		return tipusAssumpte;
	}
	public String getNumExpedient() {
		return numExpedient;
	}
	public String getRefExterna() {
		return refExterna;
	}
	public String getCodiAssumpte() {
		return codiAssumpte;
	}
	public String getObservacions() {
		return observacions;
	}
	public boolean isEliminarLogoPeu() {
		return eliminarLogoPeu;
	}
	public void setEliminarLogoPeu(boolean eliminarLogoPeu) {
		this.eliminarLogoPeu = eliminarLogoPeu;
	}
	public boolean isEliminarLogoCap() {
		return eliminarLogoCap;
	}
	public void setEliminarLogoCap(boolean eliminarLogoCap) {
		this.eliminarLogoCap = eliminarLogoCap;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	public void setDocFisica(String docFisica) {
		this.docFisica = docFisica;
	}
	public void setIdioma(IdiomaEnumDto idioma) {
		this.idioma = idioma;
	}
	public void setTipusAssumpte(String tipusAssumpte) {
		this.tipusAssumpte = tipusAssumpte;
	}
	public void setNumExpedient(String numeroExpedient) {
		this.numExpedient = numeroExpedient;
	}
	public void setRefExterna(String refExterna) {
		this.refExterna = refExterna;
	}
	public void setCodiAssumpte(String codiAssumpte) {
		this.codiAssumpte = codiAssumpte;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getTipusDocumentSelected() {
		return tipusDocumentSelected;
	}
	public void setTipusDocumentSelected(String tipusDocumentSelected) {
		this.tipusDocumentSelected = tipusDocumentSelected;
	}
	public static NotificacioCommandV2 asCommand(NotificacioDtoV2 dto) {
		if (dto == null) {
			return null;
		}
		NotificacioCommandV2 command = ConversioTipusHelper.convertir(
				dto,
				NotificacioCommandV2.class );
		return command;
	}
	public static NotificacioDtoV2 asDto(NotificacioCommandV2 command) {
		if (command == null) {
			return null;
		}
		NotificacioDtoV2 dto = ConversioTipusHelper.convertir(
				command,
				NotificacioDtoV2.class);
		
		ProcedimentDto procedimentDto = new ProcedimentDto();
		procedimentDto.setId(command.getProcedimentId());
		dto.setProcediment(procedimentDto);
		
		GrupDto grupDto = new GrupDto();
		grupDto.setId(command.getGrupId());
		dto.setGrup(grupDto);
		

		DocumentDto document = new DocumentDto();
		document.setArxiuGestdocId(dto.getDocument().getArxiuGestdocId());
		document.setArxiuNom(dto.getDocument().getArxiuNom());
		document.setContingutBase64(dto.getDocument().getContingutBase64());
		document.setCsv(dto.getDocument().getCsv());
		document.setHash(dto.getDocument().getHash());
		document.setId(dto.getDocument().getId());
		document.setUuid(dto.getDocument().getUuid());
		document.setUrl(dto.getDocument().getUrl());
		document.setNormalitzat(dto.getDocument().isNormalitzat());
		document.setGenerarCsv(dto.getDocument().isGenerarCsv());
		for (int i = 0; i < command.getDocument().getMetadadesKeys().size(); i++) {
			document.getMetadades().put(command.getDocument().getMetadadesKeys().get(i), command.getDocument().getMetadadesValues().get(i));
		}
		dto.setDocument(document);
		
		// Format de municipi i província
		if (dto.getEnviaments() != null) {
			for (NotificacioEnviamentDtoV2 enviament: dto.getEnviaments()) {
				if (enviament.getEntregaPostal() != null) {
					String codiProvincia = enviament.getEntregaPostal().getProvincia();
					if (codiProvincia != null && !codiProvincia.isEmpty()) {
						try {
							codiProvincia = String.format("%02d", Integer.parseInt(codiProvincia));
							enviament.getEntregaPostal().setProvincia(codiProvincia);
							String codiMunicipi = enviament.getEntregaPostal().getMunicipiCodi();
							if (codiMunicipi != null && !codiMunicipi.isEmpty()) {
								codiMunicipi = codiProvincia + String.format("%04d", Integer.parseInt(codiMunicipi));
								enviament.getEntregaPostal().setMunicipiCodi(codiMunicipi);
							}
						} catch (Exception e) {
							logger.error("Error al donar format a la provincia: '" + enviament.getEntregaPostal().getProvincia() + 
									"' i al municipi '" + enviament.getEntregaPostal().getMunicipiCodi() + "'");
						}
					}
				}
			}
		}
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public interface NotificacioCaducitat {}
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioCommandV2.class);

}
