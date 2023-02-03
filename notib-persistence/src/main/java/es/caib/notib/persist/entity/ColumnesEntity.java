/**
 * 
 */
package es.caib.notib.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.caib.notib.logic.intf.dto.notenviament.ColumnesDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import es.caib.notib.persist.audit.NotibAuditable;


/**
 * Classe del model de dades que representa la visibilitat de columnes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name="not_columnes")
@EntityListeners(AuditingEntityListener.class)
public class ColumnesEntity extends NotibAuditable<Long> {

	@Column(name="data_enviament")
	private boolean dataEnviament;
	@Column(name="data_programada")
	private boolean dataProgramada;
	@Column(name="not_identificador")
	private boolean notIdentificador;
	@Column(name="pro_codi")
	private boolean proCodi; 
	@Column(name="grup_codi")
	private boolean grupCodi;
	@Column(name="dir3_codi")
	private boolean dir3Codi; 
	@Column(name="usuari")
	private boolean usuari; 
	@Column(name="enviament_tipus")
	private boolean enviamentTipus; 
	@Column(name="concepte")
	private boolean concepte; 
	@Column(name="descripcio")
	private boolean descripcio; 
	@Column(name="titular_nif")
	private boolean titularNif; 
	@Column(name="titular_nom_llinatge")
	private boolean titularNomLlinatge; 
	@Column(name="titular_email")
	private boolean titularEmail;
	@Column(name="destinataris")
	private boolean destinataris; 
	@Column(name="llibre_registre")
	private boolean llibreRegistre; 
	@Column(name="numero_registre")
	private boolean numeroRegistre; 
	@Column(name="data_registre")
	private boolean dataRegistre; 
	@Column(name="data_caducitat")
	private boolean dataCaducitat; 
	@Column(name="codi_notib_env")
	private boolean codiNotibEnviament; 
	@Column(name="num_certificacio")
	private boolean numCertificacio; 
	@Column(name="csv_uuid")
	private boolean csvUuid; 
	@Column(name="estat")
	private boolean estat;
	@Column(name="referencia_notificacio")
	private boolean referenciaNotificacio;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "not_columnes_entitat_fk")
	private EntitatEntity entitat;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "usuari_codi")
	@ForeignKey(name = "not_columnes_usuari_fk")
	private UsuariEntity user;

	public ColumnesEntity(ColumnesDto col,  EntitatEntity entitat, UsuariEntity user) {

		update(col);
		this.entitat = entitat;
		this.user = user;
	}

	public void update(ColumnesDto col) {

		dataEnviament = col.isDataEnviament();
		dataProgramada = col.isDataProgramada();
		notIdentificador = col.isNotIdentificador();
		proCodi = col.isProCodi();
		grupCodi = col.isGrupCodi();
		usuari = col.isUsuari();
		dir3Codi = col.isDir3Codi();
		enviamentTipus = col.isEnviamentTipus();
		concepte = col.isConcepte();
		descripcio = col.isDescripcio();
		titularNif = col.isTitularNif();
		titularNomLlinatge = col.isTitularNomLlinatge();
		titularEmail = col.isTitularEmail();
		destinataris = col.isDestinataris();
		llibreRegistre = col.isLlibreRegistre();
		numeroRegistre = col.isNumeroRegistre();
		dataRegistre = col.isDataRegistre();
		dataCaducitat = col.isDataCaducitat();
		codiNotibEnviament = col.isCodiNotibEnviament();
		numCertificacio = col.isNumCertificacio();
		csvUuid = col.isCsvUuid();
		estat = col.isEstat();
		referenciaNotificacio = col.isReferenciaNotificacio();
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
