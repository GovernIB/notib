package es.caib.notib.core.api.dto.notenviament;

import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Informació d'un destinatari d'una anotació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotEnviamentTableItemDto implements Serializable {

	private Long id;
	private Date createdDate;

	private List<DestinatariDto> destinataris;
	private Date notificaDataCaducitat;

	// Titular
	private DestinatariDto titular;

	//Info notificació
	private Date enviamentDataProgramada;
	private String procedimentCodiNotib;
	private String grupCodi;
	private String emisorDir3Codi;
	private String usuariCodi;
	private String concepte;
	private String descripcio;
	private String llibre;
	private NotificacioEstatEnumDto estat;
	private Long notificacioId;
	private String csvUuid;

	//Registre
	private Integer registreNumero;
	private Date registreData;

	//Notific@

	private String notificaIdentificador;
	private String notificaCertificacioNumSeguiment;

	private boolean isEnviant;

	public NotEnviamentTableItemDto() {
	}

	public NotEnviamentTableItemDto(Object id,
									Object createdDate,
									Object titularNif,
									Object titularNom,
									Object titularEmail,
									Object titularLlinatge1,
									Object titularLlinatge2,
									Object titularRaoSocial,
									Object enviamentDataProgramada,
									Object procedimentCodiNotib,
									Object grupCodi,
									Object emisorDir3Codi,
									Object usuariCodi,
									Object concepte,
									Object descripcio,
									Object llibre,
									Object estat,
									Object notificacioId,
									Object csvUuid,
									Object registreNumero,
									Object registreData,
									Object notificaDataCaducitat,
									Object notificaIdentificador,
									Object notificaCertificacioNumSeguiment
	) {
		this.id = (Long) id;
		this.createdDate = (Date) createdDate;
		this.titular = new DestinatariDto(
				(String) titularNif,
				(String)  titularNom,
				(String) titularEmail,
				(String) titularLlinatge1,
				(String) titularLlinatge2,
				(String) titularRaoSocial);

		this.enviamentDataProgramada = (Date) enviamentDataProgramada;
		this.procedimentCodiNotib = (String) procedimentCodiNotib;
		this.grupCodi = (String) grupCodi;
		this.emisorDir3Codi = (String) emisorDir3Codi;
		this.usuariCodi = (String) usuariCodi;
		this.concepte = (String) concepte;
		this.descripcio = (String) descripcio;
		this.llibre = (String) llibre;
		this.estat = (NotificacioEstatEnumDto) estat;
		this.notificacioId = (Long) notificacioId;
		this.csvUuid = (String) csvUuid;
		this.registreNumero = (Integer) registreNumero;
		this.registreData = (Date) registreData;
		this.notificaDataCaducitat = (Date) notificaDataCaducitat;
		this.notificaIdentificador = (String) notificaIdentificador;
		this.notificaCertificacioNumSeguiment = (String) notificaCertificacioNumSeguiment;
	}

	public NotificacioEstatEnumDto getEstat() {
		if (isEnviant){
			return NotificacioEstatEnumDto.ENVIANT;
		}
		return this.estat;
	}

	public String getDestinatarisNomLlinatges() {
		StringBuilder destinatarisNomLlinatges = new StringBuilder();
		for(DestinatariDto destinatari: destinataris) {
			destinatarisNomLlinatges.append(destinatari.concatenarNifNomLlinatges()).append("</br>");
		}
		return destinatarisNomLlinatges.toString();
	}

	public String getTitularNifNomLlinatge() {
		return titular.concatenarNifNomLlinatges();
	}
	public String getTitularNomLlinatge() {
		return titular.concatenarNomLlinatges();
	}
	public String getTitularLlinatges() {
		return titular.concatenarLlinatges();
	}
	public String getTitularNif() { return titular.nif; }
	public String getTitularEmail() { return titular.email; }

	@AllArgsConstructor
	public static class DestinatariDto {
		private final String nif;
		private final String nom;
		private final String email;
		private final String llinatge1;
		private final String llinatge2;
		private final String raoSocial;


		private String concatenarLlinatges() {
			if (llinatge1 == null && llinatge2 == null) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(llinatge1);
			if (llinatge2 != null && !llinatge2.isEmpty()) {
				sb.append(" ");
				sb.append(llinatge2);
			}
			return sb.toString();
		}

		private String concatenarNomLlinatges() {
			StringBuilder sb = new StringBuilder();
			String llinatges = concatenarLlinatges();
			if (llinatges != null && !llinatges.isEmpty()) {
				sb.append(llinatges);
			}

			if (nom != null && !nom.isEmpty()) {
				if (llinatges != null && !llinatges.isEmpty())
					sb.append(", ");
				sb.append(nom);
			} else if (raoSocial != null && !raoSocial.isEmpty()) {
				sb.append(raoSocial);
			}
			return sb.toString();
		}

		private String concatenarNifNomLlinatges() {
			StringBuilder sb = new StringBuilder();
			String llinatges = concatenarLlinatges();
			if (nif != null) {
				sb.append(nif);
				sb.append(" - ");
			}
			if (llinatges != null && !llinatges.isEmpty()) {
				sb.append("[");
				sb.append(llinatges);
			}

			if (nom != null && !nom.isEmpty()) {
				sb.append(", ");
				sb.append(nom);

				if (raoSocial == null) {
					sb.append("]");
				}
			}
			if (raoSocial != null && !raoSocial.isEmpty()) {
				sb.append(" | ");
				sb.append(raoSocial);
				sb.append("]");
			}
			return sb.toString();
		}
	}
	private static final long serialVersionUID = -139254994389509932L;

}