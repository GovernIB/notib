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

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import es.caib.notib.persist.audit.NotibAuditable;


/**
 * Classe del model de dades que representa la visibilitat de columnes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	
	public boolean isDataEnviament() {
		return dataEnviament;
	}
	public boolean isDataProgramada() {
		return dataProgramada;
	}
	public boolean isNotIdentificador() {
		return notIdentificador;
	}
	public boolean isProCodi() {
		return proCodi;
	}
	public boolean isGrupCodi() {
		return grupCodi;
	}
	public boolean isDir3Codi() {
		return dir3Codi;
	}
	public boolean isUsuari() {
		return usuari;
	}
	public boolean isEnviamentTipus() {
		return enviamentTipus;
	}
	public boolean isConcepte() {
		return concepte;
	}
	public boolean isDescripcio() {
		return descripcio;
	}
	public boolean isTitularNif() {
		return titularNif;
	}
	public boolean isTitularNomLlinatge() {
		return titularNomLlinatge;
	}
	public boolean isTitularEmail() {
		return titularEmail;
	}
	public boolean isDestinataris() {
		return destinataris;
	}
	public boolean isLlibreRegistre() {
		return llibreRegistre;
	}
	public boolean isNumeroRegistre() {
		return numeroRegistre;
	}
	public boolean isDataRegistre() {
		return dataRegistre;
	}
	public boolean isDataCaducitat() {
		return dataCaducitat;
	}
	public boolean isCodiNotibEnviament() {
		return codiNotibEnviament;
	}
	public boolean isNumCertificacio() {
		return numCertificacio;
	}
	public boolean isCsvUuid() {
		return csvUuid;
	}
	public boolean isEstat() {
		return estat;
	}
	public EntitatEntity getEntitat() {
		return entitat;
	}
	public UsuariEntity getUser() {
		return user;
	}
	public boolean isReferenciaNotificacio() {
		return referenciaNotificacio;
	}
	
	public void update(
			boolean dataEnviament,
			boolean dataProgramada,
			boolean notIdentificador,
			boolean proCodi,
			boolean grupCodi,
			boolean dir3Codi,
			boolean usuari,
			boolean enviamentTipus,
			boolean concepte,
			boolean descripcio,
			boolean titularNif,
			boolean titularNomLlinatge,
			boolean titularEmail,
			boolean destinataris,
			boolean llibreRegistre,
			boolean numeroRegistre,
			boolean dataRegistre,
			boolean dataCaducitat,
			boolean codiNotibEnviament,
			boolean numCertificacio,
			boolean csvUuid,
			boolean estat,
			boolean referenciaNotificacio) {
		this.dataEnviament = dataEnviament;
		this.dataProgramada = dataProgramada;
		this.notIdentificador = notIdentificador;
		this.proCodi = proCodi;
		this.grupCodi = grupCodi;
		this.dir3Codi = dir3Codi;
		this.usuari = usuari;
		this.enviamentTipus = enviamentTipus;
		this.concepte = concepte;
		this.descripcio = descripcio;
		this.titularNif = titularNif;
		this.titularNomLlinatge = titularNomLlinatge;
		this.titularEmail = titularEmail;
		this.destinataris = destinataris;
		this.llibreRegistre = llibreRegistre;
		this.numeroRegistre = numeroRegistre;
		this.dataRegistre = dataRegistre;
		this.dataCaducitat = dataCaducitat;
		this.codiNotibEnviament = codiNotibEnviament;
		this.numCertificacio = numCertificacio;
		this.csvUuid = csvUuid;
		this.estat = estat;
		this.referenciaNotificacio = referenciaNotificacio;
	}
	
	public static Builder getBuilder(
			boolean dataEnviament,
			boolean dataProgramada,
			boolean notIdentificador,
			boolean proCodi,
			boolean grupCodi,
			boolean dir3Codi,
			boolean usuari,
			boolean enviamentTipus,
			boolean concepte,
			boolean descripcio,
			boolean titularNif,
			boolean titularNomLlinatge,
			boolean titularEmail,
			boolean destinataris,
			boolean llibreRegistre,
			boolean numeroRegistre,
			boolean dataRegistre,
			boolean dataCaducitat,
			boolean codiNotibEnviament,
			boolean numCertificacio,
			boolean csvUuid,
			boolean estat,
			EntitatEntity entitat,
			UsuariEntity user) {
		return new Builder(
				dataEnviament, 
				dataProgramada, 
				notIdentificador, 
				proCodi, 
				grupCodi, 
				dir3Codi, 
				usuari, 
				enviamentTipus, 
				concepte, 
				descripcio, 
				titularNif, 
				titularNomLlinatge, 
				titularEmail,
				destinataris, 
				llibreRegistre, 
				numeroRegistre, 
				dataRegistre,
				dataCaducitat,
				codiNotibEnviament, 
				numCertificacio, 
				csvUuid, 
				estat,
				entitat,
				user);
	}

	public static class Builder {
		ColumnesEntity built;
		Builder(
					boolean dataEnviament,
					boolean dataProgramada,
					boolean notIdentificador,
					boolean proCodi,
					boolean grupCodi,
					boolean dir3Codi,
					boolean usuari,
					boolean enviamentTipus,
					boolean concepte,
					boolean descripcio,
					boolean titularNif,
					boolean titularNomLlinatge,
					boolean titularEmail,
					boolean destinataris,
					boolean llibreRegistre,
					boolean numeroRegistre,
					boolean dataRegistre,
					boolean dataCaducitat,
					boolean codiNotibEnviament,
					boolean numCertificacio,
					boolean csvUuid,
					boolean estat,
					EntitatEntity entitat,
					UsuariEntity user) {
			built = new ColumnesEntity();
			built.dataEnviament = dataEnviament;
			built.dataProgramada = dataProgramada;
			built.notIdentificador = notIdentificador;
			built.proCodi = proCodi;
			built.grupCodi = grupCodi;
			built.dir3Codi = dir3Codi;
			built.usuari = usuari;
			built.enviamentTipus = enviamentTipus;
			built.concepte = concepte;
			built.descripcio = descripcio;
			built.titularNif = titularNif;
			built.titularNomLlinatge = titularNomLlinatge;
			built.titularEmail = titularEmail;
			built.destinataris = destinataris;
			built.llibreRegistre = llibreRegistre;
			built.numeroRegistre = numeroRegistre;
			built.dataRegistre = dataRegistre;
			built.dataCaducitat = dataCaducitat;
			built.codiNotibEnviament = codiNotibEnviament;
			built.numCertificacio = numCertificacio;
			built.csvUuid = csvUuid;
			built.estat = estat;
			built.entitat = entitat;
			built.user = user;
		}
		
		public ColumnesEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
