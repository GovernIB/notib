/**
 * 
 */
package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * Command per al manteniment de notificacions V1.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioCommand {

	private Long id;
	private String codiEmisor;
	private String descripcio;
	private int retard;
	private Date caducitat;
	private ProcedimentCommand procediment;
	private GrupCommand grup;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private Date enviamentDataProgramada;
	@Size(max=50)
	private String concepte;
	private TipusDocumentEnumDto tipusDocument;
	private MultipartFile arxiu;
	private String documentArxiuId;
	private List<String> metadades;
	private boolean documentNormalitzat;
	@Size(max=20)
	private String documentSha1;	
	private boolean documentGenerarCsv;
	private NotificacioEstatEnumDto estat;
	private NotificacioEnviamentEstatEnumDto estatNotifica;
	private boolean entregaPostalActiva;
	private EntregapostalCommand entregaPostal;
	private boolean entregaDeh;

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
	public String getDocumentArxiuId() {
		return documentArxiuId;
	}
	public void setDocumentArxiuId(String documentArxiuId) {
		this.documentArxiuId = documentArxiuId;
	}
	public List<String> getMetadades() {
		return metadades;
	}
	public void setMetadades(List<String> metadades) {
		this.metadades = metadades;
	}
	public String getDocumentSha1() {
		return documentSha1;
	}
	public void setDocumentSha1(String documentSha1) {
		this.documentSha1 = documentSha1;
	}
	public boolean isDocumentNormalitzat() {
		return documentNormalitzat;
	}
	public void setDocumentNormalitzat(boolean documentNormalitzat) {
		this.documentNormalitzat = documentNormalitzat;
	}
	public boolean isDocumentGenerarCsv() {
		return documentGenerarCsv;
	}
	public void setDocumentGenerarCsv(boolean documentGenerarCsv) {
		this.documentGenerarCsv = documentGenerarCsv;
	}
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public NotificacioEnviamentEstatEnumDto getEstatNotifica() {
		return estatNotifica;
	}
	public void setEstatNotifica(NotificacioEnviamentEstatEnumDto estatNotifica) {
		this.estatNotifica = estatNotifica;
	}
	public String getCodiEmisor() {
		return codiEmisor;
	}
	public void setCodiEmisor(String codiEmisor) {
		this.codiEmisor = codiEmisor;
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
	public ProcedimentCommand getProcediment() {
		return procediment;
	}
	public void setProcediment(ProcedimentCommand procediment) {
		this.procediment = procediment;
	}
	public GrupCommand getGrup() {
		return grup;
	}
	public void setGrup(GrupCommand grup) {
		this.grup = grup;
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
	public boolean isEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	
	public static NotificacioCommand asCommand(NotificacioDto dto) {
		if (dto == null) {
			return null;
		}
		NotificacioCommand command = ConversioTipusHelper.convertir(
				dto,
				NotificacioCommand.class );
		return command;
	}
	public static NotificacioDto asDto(NotificacioCommand command) {
		if (command == null) {
			return null;
		}
		NotificacioDto dto = ConversioTipusHelper.convertir(
				command,
				NotificacioDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
