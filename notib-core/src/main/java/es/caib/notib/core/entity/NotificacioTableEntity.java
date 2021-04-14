/**
 * 
 */
package es.caib.notib.core.entity;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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
	private ProcedimentOrganEntity procedimentOrgan;
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

	/**
	 * FIELDS
	 */
	@Column(name = "tipus_usuari")
	private TipusUsuariEnumDto tipusUsuari;
//	@Column(name = "callback_error")
//	private boolean errorLastCallback;
	@Column(name = "NOTIFICA_ERROR_DATE")
	private Date notificaErrorData;
	@Column(name = "NOTIFICA_ERROR_DESCRIPCIO")
	private String notificaErrorDescripcio;

	@Column(name = "env_tipus", nullable = false)
	private NotificaEnviamentTipusEnumDto enviamentTipus;

	@Column(name = "registre_num_expedient", length = 80)
	private String numExpedient;
	@Column(name = "registre_env_intent")
	private int registreEnviamentIntent;
//	@Column(name = "REGISTRE_HAS_ENVS_PENDENTS")
//	private boolean hasEnviamentsPendentsRegistre;

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
	@Column(name = "ORGAN_CODI")
	private String organCodi;
	@Column(name = "ORGAN_NOM")
	private String organNom;
	@Column(name = "ERROR_LAST_EVENT")
	private boolean isErrorLastEvent;

	@Setter
	@Transient
	private boolean permisProcessar;
	@Setter
	@Transient
	private boolean hasEnviamentsPendentsRegistre;

	private static final long serialVersionUID = 458331024861203562L;
}
