package es.caib.notib.core.entity.cie;

import es.caib.notib.client.domini.EntregaPostal;
import es.caib.notib.client.domini.EntregaPostalViaTipusEnum;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Classe del model de dades que representa els enviaments d'una
 * notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name="NOT_ENTREGA_POSTAl")
@EntityListeners(AuditingEntityListener.class)
public class EntregaPostalEntity extends NotibAuditable<Long> {

	/* Domicili */
	@Column(name = "dom_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliTipusEnumDto domiciliTipus;
	
	@Column(name = "dom_con_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliConcretTipusEnumDto domiciliConcretTipus;
	
	@Column(name = "dom_via_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliViaTipusEnumDto domiciliViaTipus;
	
	@Column(name = "dom_via_nom", length = 50)
	protected String domiciliViaNom;

	@Column(name = "dom_num_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus;
	
	@Column(name = "dom_num_num", length = 5)
	protected String domiciliNumeracioNumero;
	
	@Column(name = "dom_num_qualif", length = 3)
	protected String domiciliNumeracioQualificador;
	
	@Column(name = "dom_num_puntkm", length = 10)
	protected String domiciliNumeracioPuntKm;
	
	@Column(name = "dom_apartat", length = 10)
	protected String domiciliApartatCorreus;
	
	@Column(name = "dom_bloc", length = 50)
	protected String domiciliBloc;
	
	@Column(name = "dom_portal", length = 50)
	protected String domiciliPortal;
	
	@Column(name = "dom_escala", length = 50)
	protected String domiciliEscala;
	
	@Column(name = "dom_planta", length = 50)
	protected String domiciliPlanta;
	
	@Column(name = "dom_porta", length = 50)
	protected String domiciliPorta;
	
	@Column(name = "dom_complem", length = 250)
	protected String domiciliComplement;
	
	@Column(name = "dom_poblacio", length = 255)
	protected String domiciliPoblacio;
	
	@Column(name = "dom_mun_codine", length = 6)
	protected String domiciliMunicipiCodiIne;
	
	@Column(name = "dom_mun_nom", length = 64)
	protected String domiciliMunicipiNom;
	
	@Column(name = "dom_codi_postal", length = 10)
	protected String domiciliCodiPostal;
	
	@Column(name = "dom_prv_codi", length = 2)
	protected String domiciliProvinciaCodi;
	
	@Column(name = "dom_prv_nom", length = 64)
	protected String domiciliProvinciaNom;
	
	@Column(name = "dom_pai_codiso", length = 3)
	protected String domiciliPaisCodiIso; // ISO-3166
	
	@Column(name = "dom_pai_nom", length = 64)
	protected String domiciliPaisNom;
	
	@Column(name = "dom_linea1", length = 50)
	protected String domiciliLinea1;
	
	@Column(name = "dom_linea2", length = 50)
	protected String domiciliLinea2;
	
	@Column(name = "dom_cie")
	protected Integer domiciliCie;

	@Column(name = "format_sobre", length = 10)
	protected String formatSobre;

	@Column(name = "format_fulla", length = 10)
	protected String formatFulla;

	public void update(EntregaPostal entregaPostal) {

		domiciliViaTipus = toEnviamentViaTipusEnum(entregaPostal.getViaTipus());
		if (entregaPostal.getTipus() != null) {
			switch (entregaPostal.getTipus()) {
				case APARTAT_CORREUS:
					domiciliConcretTipus = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS;
					break;
				case ESTRANGER:
					domiciliConcretTipus = NotificaDomiciliConcretTipusEnumDto.ESTRANGER;
					break;
				case NACIONAL:
					domiciliConcretTipus = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
					break;
				case SENSE_NORMALITZAR:
					domiciliConcretTipus = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
					break;
			}
		}
		if (entregaPostal.getNumeroCasa() != null) {
			domiciliNumeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
		} else if (entregaPostal.getApartatCorreus() != null) {
			domiciliNumeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS;
		} else if (entregaPostal.getPuntKm() != null) {
			domiciliNumeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC;
		} else {
			domiciliNumeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO;
		}
		this.domiciliTipus = NotificaDomiciliTipusEnumDto.CONCRETO;
		if(! NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR.equals(entregaPostal.getTipus())) {
			this.domiciliViaNom = entregaPostal.getViaNom();
			this.domiciliNumeracioNumero = entregaPostal.getNumeroCasa();
			this.domiciliNumeracioQualificador = entregaPostal.getNumeroQualificador();
			this.domiciliNumeracioPuntKm = entregaPostal.getPuntKm();
			this.domiciliApartatCorreus = entregaPostal.getApartatCorreus();
			this.domiciliPortal = entregaPostal.getPortal();
			this.domiciliEscala = entregaPostal.getEscala();
			this.domiciliPlanta = entregaPostal.getPlanta();
			this.domiciliPorta = entregaPostal.getPorta();
			this.domiciliBloc = entregaPostal.getBloc();
			this.domiciliComplement = entregaPostal.getComplement();
			this.domiciliPoblacio = entregaPostal.getPoblacio();
			this.domiciliMunicipiCodiIne = entregaPostal.getMunicipiCodi();
			this.domiciliProvinciaCodi = entregaPostal.getProvincia();
			this.domiciliCie = entregaPostal.getCie();
			this.formatSobre = entregaPostal.getFormatSobre();
			this.formatFulla = entregaPostal.getFormatFulla();
		}

		this.domiciliPaisCodiIso = entregaPostal.getPaisCodi();
		this.domiciliCodiPostal = entregaPostal.getCodiPostal();
		this.domiciliLinea1 = entregaPostal.getLinea1();
		this.domiciliLinea2 = entregaPostal.getLinea2();
	}

	private NotificaDomiciliViaTipusEnumDto toEnviamentViaTipusEnum(
			EntregaPostalViaTipusEnum viaTipus) {
		if (viaTipus == null) {
			return null;
		}
		return NotificaDomiciliViaTipusEnumDto.valueOf(viaTipus.name());
	}

	public String provinciaCodiFormatted(){
		if (domiciliProvinciaCodi != null && !domiciliProvinciaCodi.isEmpty()) {
			return String.format("%02d", Integer.parseInt(domiciliProvinciaCodi));
		}
		return "";
	}

	public String municipiCodiFormatted(){
		if (domiciliMunicipiCodiIne != null && !domiciliMunicipiCodiIne.isEmpty()) {
			return provinciaCodiFormatted() + String.format("%04d", Integer.parseInt(domiciliMunicipiCodiIne));
		}
		return "";
	}
	@Override
	public String toString() {
		String domicili = "";
		String domiciliPoblacio ="";
		switch (domiciliConcretTipus) {
			case ESTRANGER:
				domicili = domiciliPaisCodiIso + "-";
				domicili += getDomiciliPoblacioString();
				domicili += getAdressa();
				break;
			case NACIONAL:
				domicili = provinciaCodiFormatted() + "-";
				domicili += municipiCodiFormatted() + "-";
				domicili += domiciliCodiPostal + "-";
				domicili += getDomiciliPoblacioString();

				domicili += getAdressa();
				break;
			case APARTAT_CORREUS:
				domicili = provinciaCodiFormatted() + "-";
				domicili += municipiCodiFormatted() + "-";
				domicili += domiciliCodiPostal + "-";
				domicili += domiciliApartatCorreus;
				domicili += getDomiciliPoblacioString();
				break;
			case SENSE_NORMALITZAR:
				domicili = domiciliLinea1 + " " + domiciliLinea2;
				break;
			default:
				return null;
		}
		return domicili;
	}
	private String getDomiciliPoblacioString() {
		String domiciliPoblacioCurt = "";
		if (domiciliPoblacio != null) {
			if (domiciliPoblacio.length() > 30) {
				domiciliPoblacioCurt = " (" + domiciliPoblacio.substring(0, 30) + ") - ";
			} else {
				domiciliPoblacioCurt += " (" + domiciliPoblacio + ") - ";
			}
		}
		return domiciliPoblacioCurt;
	}
	private String getAdressa() {
		String adressa = "";

		if (domiciliViaTipus != null)
			adressa += domiciliViaTipus + " ";
		if (domiciliViaNom != null && !domiciliViaNom.isEmpty())
			adressa += domiciliViaNom + " ";
		if(NotificaDomiciliNumeracioTipusEnumDto.NUMERO.equals(domiciliNumeracioTipus))
			adressa += domiciliNumeracioNumero + " ";
		else if(NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC.equals(domiciliNumeracioTipus))
			adressa += domiciliNumeracioPuntKm + " ";
		if (domiciliNumeracioQualificador != null && !domiciliNumeracioQualificador.isEmpty())
			adressa += domiciliNumeracioQualificador + " ";
		if (domiciliBloc != null && !domiciliBloc.isEmpty())
			adressa += "b. " + domiciliBloc + " ";
		if (domiciliPortal != null && !domiciliPortal.isEmpty())
			adressa += "pt. " + domiciliPortal + " ";
		if (domiciliEscala != null && !domiciliEscala.isEmpty())
			adressa += "e. " + domiciliEscala + " ";
		if (domiciliPlanta != null && !domiciliPlanta.isEmpty())
			adressa += "pl. " + domiciliPlanta + " ";
		if (domiciliPorta != null && !domiciliPorta.isEmpty())
			adressa += "p. " + domiciliPorta + " ";
		if (domiciliComplement != null && !domiciliComplement.isEmpty())
			adressa += "c. " + domiciliComplement + " ";
		return adressa;
	}

}
