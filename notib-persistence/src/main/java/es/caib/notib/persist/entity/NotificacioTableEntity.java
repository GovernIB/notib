/**
 * 
 */
package es.caib.notib.persist.entity;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.persist.audit.NotibAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;


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
@Table(name="NOT_NOTIFICACIO_TABLE")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioTableEntity extends NotibAuditable<Long> {

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	@MapsId
	private NotificacioEntity notificacio;

	/**
	 * INDEXS
	 */

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	private EntitatEntity entitat;

	@Column(name = "proc_codi_notib", length = 9)
	private String procedimentCodiNotib;

	/*Procediment*/
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "procediment_organ_id")
	private ProcSerOrganEntity procedimentOrgan; // TODO: useless

	@Column(name = "usuari_codi", length = 64, nullable = false)
	private String usuariCodi;
	@Column(name = "grup_codi", length = 64)
	private String grupCodi;

	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private Set<NotificacioEnviamentEntity> enviaments = new LinkedHashSet<NotificacioEnviamentEntity>();

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "NOTIFICACIO_MASSIVA_ID")
	@ForeignKey(name = "NOT_NOT_TABLE_NOT_MASSIVA_FK")
	protected NotificacioMassivaEntity notificacioMassiva;

	/**
	 * FIELDS
	 */
	@Column(name = "tipus_usuari")
	private TipusUsuariEnumDto tipusUsuari;
	@Column(name = "NOTIFICA_ERROR_DATE")
	private Date notificaErrorData;
	@Column(name = "NOTIFICA_ERROR_DESCRIPCIO")
	private String notificaErrorDescripcio;

	@Column(name = "env_tipus", nullable = false)
	private EnviamentTipus enviamentTipus;

	@Column(name = "registre_num_expedient", length = 80)
	private String numExpedient;
	@Column(name = "registre_env_intent")
	private int registreEnviamentIntent;

	@Column(name = "concepte", length = 255, nullable = false)
	private String concepte;

	@Column(name = "estat", nullable = false)
	private NotificacioEstatEnumDto estat;

	@Column(name = "estat_date")
	private Date estatDate;

	@Column(name = "ENTITAT_NOM")
	private String entitatNom;
	@Column(name = "PROCEDIMENT_CODI")
	private String procedimentCodi;
	@Column(name = "PROCEDIMENT_NOM")
	private String procedimentNom;
	@Column(name = "PROCEDIMENT_IS_COMU")
	private boolean procedimentIsComu;
	@Column(name = "PROCEDIMENT_REQUIRE_PERMISSION")
	private boolean procedimentRequirePermission;
	@Column(name = "PROCEDIMENT_TIPUS")
	@Enumerated(EnumType.STRING)
	private ProcSerTipusEnum procedimentTipus;
	@Column(name = "ORGAN_CODI")
	private String organCodi;
	@Column(name = "ORGAN_NOM")
	private String organNom;
	@Column(name = "ORGAN_ESTAT")
	private OrganGestorEstatEnum organEstat;
	@Column(name = "ERROR_LAST_EVENT")
	private boolean isErrorLastEvent;
	@Column(name = "ESTAT_PROCESSAT_DATE")
	private Date estatProcessatDate;
	@Column(name = "ENVIADA_DATE")
	private Date enviadaDate;
	@Column(name = "referencia", length = 36)
	protected String referencia;
	@Column(name = "TITULAR", length = 1024)
	private String titular;
	@Column(name = "NOTIFICA_IDS", length = 1024)
	private String notificaIds;
	@Column(name = "ESTAT_MASK")
	private Integer estatMask;

	@Column(name = "ESTAT_STRING", length = 512)
	private String estatString;
	@Column(name = "DOCUMENT_ID")
	private Long documentId;
	@Column(name = "ENV_CER_DATA")
	private Date envCerData;
	@Column(name = "REG_ENV_PENDENTS")
	private boolean hasEnviamentsPendentsRegistre;
	@Column(name = "PER_ACTUALITZAR")
	private boolean perActualitzar;

	@Setter
	@Transient
	private boolean permisProcessar;

	public boolean isComunicacioSir() { // Per al mapping al DTO

		if (EnviamentTipus.SIR.equals(getEnviamentTipus())) {
			return true;
		}

		if (!EnviamentTipus.COMUNICACIO.equals(this.getEnviamentTipus())) {
			return false;
		}

		for(NotificacioEnviamentEntity enviament : this.getEnviaments()) {
			if(!enviament.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
				return false;
			}
		}
		return true;
	}

	private static final long serialVersionUID = 458331024861203562L;
}
