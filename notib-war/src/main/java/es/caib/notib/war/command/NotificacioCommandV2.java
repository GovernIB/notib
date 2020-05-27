/**
 * 
 */
package es.caib.notib.war.command;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import es.caib.notib.war.validation.ValidNotificacio;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment de notificacions manuals (V2).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */	
@Getter @Setter
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
	@Valid @NotEmpty
	private List<EnviamentCommand> enviaments = new ArrayList<EnviamentCommand>();
	

	public byte[]  getContingutArxiu() {
		try {
			return arxiu.getBytes();
		} catch (IOException e) {
			logger.error("No s'ha pogut recuperar el contingut del fitxer per validar");
		}
		return null;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public interface NotificacioCaducitat {}
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioCommandV2.class);

}
