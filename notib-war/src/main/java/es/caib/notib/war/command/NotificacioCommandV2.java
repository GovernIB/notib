/**
 * 
 */
package es.caib.notib.war.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.NotificaComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de notificacions manuals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioCommandV2 {

	private Long id;
	private String emisorDir3Codi;
	private NotificaComunicacioTipusEnumDto comunicacioTipus;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	@Size(max=50)
	private String concepte;
	private String descripcio;
	private Date enviamentDataProgramada;
	private int retard;
	private Date caducitat;
	private DocumentCommand document;
	private Long procedimentId;
	private Long grupId;
	private TipusDocumentEnumDto tipusDocument;
	private String documentArxiuUuidCsv;
	private MultipartFile arxiu;
	private String organ;
	private String llibre;
	private String oficina;
	
	// Enviament
	private ServeiTipusEnumDto serveiTipus;
	private PersonaCommand titular;
	private PersonaCommand destinatari = new PersonaCommand();
	private List<PersonaCommand> destinataris = new ArrayList<PersonaCommand>();

	private boolean entregaPostalActiva;
	private EntregapostalCommand entregaPostal;
	private EntregaDehCommand entregaDeh;
	
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
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public TipusDocumentEnumDto getTipusDocument() {
		return tipusDocument;
	}
	public void setTipusDocument(TipusDocumentEnumDto tipusDocument) {
		this.tipusDocument = tipusDocument;
	}
	public MultipartFile getArxiu() {
		return arxiu;
	}
	public void setArxiu(MultipartFile arxiu) {
		this.arxiu = arxiu;
	}
	public String getDocumentArxiuUuidCsv() {
		return documentArxiuUuidCsv;
	}
	public void setDocumentArxiuUuidCsv(String documentArxiuUuidCsv) {
		this.documentArxiuUuidCsv = documentArxiuUuidCsv;
	}
	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	public NotificaComunicacioTipusEnumDto getComunicacioTipus() {
		return comunicacioTipus;
	}
	public void setComunicacioTipus(NotificaComunicacioTipusEnumDto comunicacioTipus) {
		this.comunicacioTipus = comunicacioTipus;
	}
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
	public Long getGrupId() {
		return grupId;
	}
	public void setGrupId(Long grupId) {
		this.grupId = grupId;
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
	public PersonaCommand getDestinatari() {
		return destinatari;
	}
	public void setDestinatari(PersonaCommand destinatari) {
		this.destinatari = destinatari;
	}
	public List<PersonaCommand> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<PersonaCommand> destinataris) {
		this.destinataris = destinataris;
	}
	public boolean isEntregaPostalActiva() {
		return entregaPostalActiva;
	}
	public void setEntregaPostalActiva(boolean entregaPostalActiva) {
		this.entregaPostalActiva = entregaPostalActiva;
	}
	public EntregapostalCommand getEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(EntregapostalCommand entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public EntregaDehCommand getEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(EntregaDehCommand entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public DocumentCommand getDocument() {
		return document;
	}
	public void setDocument(DocumentCommand document) {
		this.document = document;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public String getLlibre() {
		return llibre;
	}
	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
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
		
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
