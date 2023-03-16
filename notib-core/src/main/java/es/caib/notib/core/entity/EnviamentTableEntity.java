package es.caib.notib.core.entity;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.ProcSerTipusEnum;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


/**
 * Classe del model de dades que representa una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="NOT_NOTIFICACIO_ENV_TABLE")
@EntityListeners(AuditingEntityListener.class)
public class EnviamentTableEntity extends NotibAuditable<Long> {
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	@MapsId
	private NotificacioEnviamentEntity enviament;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NOTIFICACIO_ID")
	private NotificacioEntity notificacio;

	@Column(name = "TIPUS_ENVIAMENT", nullable = false)
	protected NotificaEnviamentTipusEnumDto tipusEnviament;

	@Column(name = "DESTINATARIS", length = 4000, nullable = false)
	protected String destinataris;

	/**
	 * INDEXS
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	private EntitatEntity entitat;

	@Column(name = "PROCEDIMENT_CODI_NOTIB", length = 9)
	private String procedimentCodiNotib;

	@Column(name = "usuari_codi", length = 64, nullable = false)
	private String usuariCodi;

	@Column(name = "grup_codi", length = 64)
	private String grupCodi;


	/**
	 * FIELDS
	 */
	@Column(name = "TITULAR_NIF", length = 9)
	private String titularNif;
	@Column(name = "TITULAR_NOM", length = 255)
	private String titularNom;
	@Column(name = "TITULAR_EMAIL", length = 160)
	private String titularEmail;
	@Column(name = "TITULAR_LLINATGE1", length = 40)
	private String titularLlinatge1;
	@Column(name = "TITULAR_LLINATGE2", length = 40)
	private String titularLlinatge2;
	@Column(name = "TITULAR_RAOSOCIAL", length = 100)
	private String titularRaoSocial;

	////
	// INFO NOTIFICACIO
	////

	@Column(name = "DATA_PROGRAMADA")
	@Temporal(TemporalType.DATE)
	protected Date enviamentDataProgramada;

	@Column(name = "EMISOR_DIR3", length = 9, nullable = false)
	protected String emisorDir3Codi;

	@Column(name = "concepte", length = 255, nullable = false)
	protected String concepte;

	@Column(name = "descripcio", length = 1000)
	protected String descripcio;

	@Column(name = "LLIBRE")
	private String registreLlibreNom;

	@Column(name = "NOT_ORGAN_CODI")
	private String organCodi;
	
	@Column(name = "ORGAN_ESTAT")
	private OrganGestorEstatEnum organEstat;

	@Column(name = "NOT_ESTAT", nullable = false)
	protected NotificacioEstatEnumDto estat;

	@Column(name = "CSV_UUID", length = 256)
	protected String csv_uuid;

	@Column(name = "HAS_ERRORS")
	protected Boolean hasErrors;


	// //
	// PROCEDIMENT
	// //
	@Column(name = "PROCEDIMENT_IS_COMU")
	private Boolean procedimentIsComu;

	@Column(name = "PROCEDIMENT_PROCORGAN_ID")
	protected Long procedimentOrganId; // TODO: useless

	@Column(name = "PROCEDIMENT_REQUIRE_PERMISSION")
	private boolean procedimentRequirePermission;

	@Column(name = "PROCEDIMENT_TIPUS")
	@Enumerated(EnumType.STRING)
	private ProcSerTipusEnum procedimentTipus;


	// //
	// REGISTRE
	// //
	@Column(name = "registre_numero", length = 19)
	protected Integer registreNumero;

	@Column(name = "registre_data")
	@Temporal(TemporalType.DATE)
	protected Date registreData;

	@Column(name = "REGISTRE_ENVIAMENT_INTENT")
	protected Integer registreEnviamentIntent;


	// //
	// CAMPS DE NOTIFICA
	// //

	@Column(name = "NOTIFICA_DATA_CADUCITAT")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaDataCaducitat;

	@Column(name = "NOTIFICA_IDENTIFICADOR", length = 20)
	protected String notificaIdentificador;

	@Column(name = "NOTIFICA_CERT_NUM_SEGUIMENT", length = 50)
	protected String notificaCertificacioNumSeguiment;

	@Column(name = "NOTIFICA_ESTAT", length = 50)
	protected EnviamentEstat notificaEstat;

	@Column(name = "NOTIFICA_REF", length = 36)
	protected String notificaReferencia;

	@Column(name = "callback_error")
	protected boolean errorLastCallback;
}
