/**
 * 
 */
package es.caib.notib.back.command;

import es.caib.notib.back.helper.CaducitatHelper;
import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.back.validation.ValidNotificacio;
import es.caib.notib.back.validation.ValidPersonaValidator;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.Persona;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Command per al manteniment de notificacions manuals (V2).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */	
@Getter @Setter
@ValidNotificacio
public class NotificacioCommand {

	private Long id;
	@NotEmpty @Size(max=9)
	private String emisorDir3Codi;
	@NotEmpty
	private String organGestor;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private EnviamentTipus enviamentTipus;
	@Size(min=3, max=226)
	private String concepte;
	@Size(max=1000)
	private String descripcio;
	private Date enviamentDataProgramada;
	private int retard;
	private Date caducitat;
	private Integer caducitatDiesNaturals;
	private Long procedimentId;
	private String procedimentNom;
	private Long serveiId;
	private String tipusProcSer = "PROCEDIMENT";
	private String codiSia;
	private String grupCodi;
	@Size(max=64)
	private String usuariCodi;
	private String oficina;
	private String llibre;
	private String extracte;
	private String docFisica;
	private Idioma idioma;
	private String tipusAssumpte;
	@Size(max=80)
	private String numExpedient;
	private String refExterna;
	private String codiAssumpte;
	private String observacions;
	private boolean eliminarLogoPeu;
	private boolean eliminarLogoCap;
	private ServeiTipus serveiTipus;
//	protected NotificacioErrorTipusEnumDto notificaErrorTipus;
	@Valid @NotEmpty
	private List<EnviamentCommand> enviaments = new ArrayList<>();

	// Document 1
	private DocumentCommand[] documents = new DocumentCommand[5];
	private TipusDocumentEnumDto[] tipusDocument = new TipusDocumentEnumDto[5];
	private String[] tipusDocumentSelected = new String[5];
	private String[] tipusDocumentDefault = new String[5];
	private String[] documentArxiuCsv = new String[5];
	private String[] documentArxiuUuid = new String[5];
	private String[] documentArxiuUrl = new String[5];
	private MultipartFile[] arxiu = new MultipartFile[5];

	public void setTipusDocumentDefault(int i, String value) {
		this.tipusDocumentDefault[i] = value;
	}

	public byte[] getContingutArxiu(int i) {
		try {
			return arxiu[i].getBytes();
		} catch (IOException e) {
			logger.error("No s'ha pogut recuperar el contingut del fitxer per validar");
		}
		return null;
	}

	public boolean isComunicacioSIR() {
		for (EnviamentCommand enviament : enviaments) {
			if (InteressatTipus.ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus())) {
				return true;
			}
		}
		return false;
	}

	public static NotificacioCommand asCommand(NotificacioDtoV2 dto) {
		if (dto == null) {
			return null;
		}
		NotificacioCommand command = ConversioTipusHelper.convertir(dto, NotificacioCommand.class);
		
		if (dto.getProcediment() != null) {
			if (ProcSerTipusEnum.SERVEI.equals(dto.getProcediment().getTipus())) {
				command.setServeiId(dto.getProcediment().getId());
			} else {
				command.setProcedimentId(dto.getProcediment().getId());
			}
		}

//		if (NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(dto.getEnviamentTipus())) {
//			boolean aAdministracio = false;
//			for (NotificacioEnviamentDtoV2 enviament : dto.getEnviaments()) {
//				if (InteressatTipus.ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus())) {
//					aAdministracio = true;
//					break;
//				}
//			}
//			if (aAdministracio) {
//				command.setEnviamentTipus(TipusEnviamentEnumDto.COMUNICACIO_SIR);
//
//			} else {
//				command.setEnviamentTipus(TipusEnviamentEnumDto.COMUNICACIO);
//
//			}
//		} else {
//			command.setEnviamentTipus(TipusEnviamentEnumDto.NOTIFICACIO);
//		}

		if (dto.getCaducitat() != null) {
			command.setCaducitatDiesNaturals(CaducitatHelper.getDiesEntreDates(dto.getCaducitat()));
		}
		return command;
	}
	public Notificacio asNotificacioV2() {

		var dto = ConversioTipusHelper.convertir(this, Notificacio.class);
//		ProcSerDto procedimentDto = new ProcSerDto();
//		if ("PROCEDIMENT".equals(tipusProcSer)) {
////		if (procedimentId != null) {
//			procedimentDto.setId(this.getProcedimentId());
//		} else {
//			procedimentDto.setId(this.getServeiId());
//		}
		dto.setProcedimentId("PROCEDIMENT".equals(tipusProcSer) ? procedimentId : serveiId);
//		dto.setProcediment(procedimentDto);

//		GrupDto grupDto = new GrupDto();
//		grupDto.setId(this.getGrupId());
		dto.setGrupCodi(grupCodi);

		// Format de municipi i província
		if (dto.getEnviaments() != null) {
			for (var enviament: dto.getEnviaments()) {
				if (enviament.getTitular().getEmail() != null && !enviament.getTitular().getEmail().isEmpty()) {
					enviament.getTitular().setEmail(enviament.getTitular().getEmail().replaceAll("\\s+", ""));
				}

				establecerCamposPersona(enviament.getTitular());

				if (enviament.getDestinataris() != null) {
					for (var destinatari : enviament.getDestinataris()) {
						if (destinatari.getEmail() != null && !destinatari.getEmail().isEmpty()) {
							destinatari.setEmail(destinatari.getEmail().replaceAll("\\s+", ""));
						}
						establecerCamposPersona(destinatari);
						destinatari.setIncapacitat(Boolean.FALSE);
					}
				}
			}
		}
		return dto;
	}
	
	private void establecerCamposPersona(Persona persona) {
		if (persona != null) {
			if (InteressatTipus.FISICA.equals(persona.getInteressatTipus())) {
				persona.setDir3Codi(null);
				persona.setDocumentTipus(null);
			} else if (InteressatTipus.FISICA_SENSE_NIF.equals(persona.getInteressatTipus())) {
				persona.setDir3Codi(null);
			} else if (InteressatTipus.JURIDICA.equals(persona.getInteressatTipus())) {
				persona.setDocumentTipus(null);
				persona.setLlinatge1(null);
				persona.setLlinatge2(null);
			} else if (InteressatTipus.ADMINISTRACIO.equals(persona.getInteressatTipus())) {
				persona.setDocumentTipus(null);
				persona.setIncapacitat(Boolean.FALSE);
				persona.setLlinatge1(null);
				persona.setLlinatge2(null);	
			}
		}
	}
	
	public int getConcepteDefaultSize() {
		int concepteSize = 0;
		try {
			Field concepte = this.getClass().getDeclaredField("concepte");
			concepteSize = concepte.getAnnotation(Size.class).max();
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la longitud del concepte: " + ex.getMessage());
		}
		return concepteSize;
	}
	
	public int getDescripcioDefaultSize() {
		int descripcioSize = 0;
		try {
			Field descripcio = this.getClass().getDeclaredField("descripcio");
			descripcioSize = descripcio.getAnnotation(Size.class).max();
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la longitud de descripció: " + ex.getMessage());
		}
		return descripcioSize;
	}
	
	public int getNomDefaultSize() {
//		int concepteSize = 0;
//		try {
//			Field concepte = PersonaCommand.class.getDeclaredField("nom");
//			concepteSize = concepte.getAnnotation(Size.class).max();
//		} catch (Exception ex) {
//			logger.error("No s'ha pogut recuperar la longitud del nom: " + ex.getMessage());
//		}
		return ValidPersonaValidator.MAX_SIZE_NOM;
	}

	public int getRaoSocialDefaultsize() {
		return ValidPersonaValidator.MAX_SIZE_RAO_SOCIAL;
	}
	
	public int getLlinatge1DefaultSize() {
		int concepteSize = 0;
		try {
			Field concepte = PersonaCommand.class.getDeclaredField("llinatge1");
			concepteSize = concepte.getAnnotation(Size.class).max();
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la longitud del llinatge 1: " + ex.getMessage());
		}
		return concepteSize;
	}
	
	public int getLlinatge2DefaultSize() {
		int concepteSize = 0;
		try {
			Field concepte = PersonaCommand.class.getDeclaredField("llinatge2");
			concepteSize = concepte.getAnnotation(Size.class).max();
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la longitud del llinatge 2: " + ex.getMessage());
		}
		return concepteSize;
	}
	
	public int getEmailDefaultSize() {
		int concepteSize = 0;
		try {
			Field concepte = PersonaCommand.class.getDeclaredField("email");
			concepteSize = concepte.getAnnotation(Size.class).max();
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la longitud de l'email: " + ex.getMessage());
		}
		return concepteSize;
	}
	
	public int getTelefonDefaultSize() {
		int concepteSize = 0;
		try {
			Field concepte = PersonaCommand.class.getDeclaredField("telefon");
			concepteSize = concepte.getAnnotation(Size.class).max();
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la longitud del telèfon: " + ex.getMessage());
		}
		return concepteSize;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public interface NotificacioCaducitat {}
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioCommand.class);

}
