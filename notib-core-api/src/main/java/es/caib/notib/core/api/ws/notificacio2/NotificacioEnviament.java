/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio2;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class NotificacioEnviament {

	private String referencia;

	private String titularNom;
	private String titularLlinatges;
	private String titularNif;
	private String titularTelefon;
	private String titularEmail;

	private String destinatariNom;
	private String destinatariLlinatges;
	private String destinatariNif;
	private String destinatariTelefon;
	private String destinatariEmail;

	private DomiciliTipusEnum domiciliTipus;
	private DomiciliConcretTipusEnum domiciliConcretTipus;
	private String domiciliViaTipus;
	private String domiciliViaNom;
	private DomiciliNumeracioTipusEnum domiciliNumeracioTipus;
	private String domiciliNumeracioNumero;
	private String domiciliNumeracioPuntKm;
	private String domiciliApartatCorreus;
	private String domiciliBloc;
	private String domiciliPortal;
	private String domiciliEscala;
	private String domiciliPlanta;
	private String domiciliPorta;
	private String domiciliComplement;
	private String domiciliPoblacio;
	private String domiciliMunicipiCodiIne;
	private String domiciliMunicipiNom;
	private String domiciliCodiPostal;
	private String domiciliProvinciaCodi;
	private String domiciliProvinciaNom;
	private String domiciliPaisCodiIso;
	private String domiciliPaisNom;
	private String domiciliLinea1;
	private String domiciliLinea2;
	private Integer domiciliCie;

	private boolean dehObligat;
	private String dehNif;
	private String dehProcedimentCodi;

	private ServeiTipusEnum serveiTipus;
	private int retardPostal;
	private Date caducitat;

	private NotificacioEnviamentEstatEnum estat;

	private String datatData;
	private String datatOrigen;
	private String datatEstatCodi;
	private Date certificacioData;
	private String certificacioOrigen;
	private NotificacioEnviamentEstatEnum notificaEstat;

	private Date seuDataPublicacio;
	private Date seuRegistreNumero;
	private Date seuRegistreData;
	private Date seuDataFi;
	private NotificacioEnviamentEstatEnum seuEstat;

	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getTitularNom() {
		return titularNom;
	}
	public void setTitularNom(String titularNom) {
		this.titularNom = titularNom;
	}
	public String getTitularLlinatges() {
		return titularLlinatges;
	}
	public void setTitularLlinatges(String titularLlinatges) {
		this.titularLlinatges = titularLlinatges;
	}
	public String getTitularNif() {
		return titularNif;
	}
	public void setTitularNif(String titularNif) {
		this.titularNif = titularNif;
	}
	public String getTitularTelefon() {
		return titularTelefon;
	}
	public void setTitularTelefon(String titularTelefon) {
		this.titularTelefon = titularTelefon;
	}
	public String getTitularEmail() {
		return titularEmail;
	}
	public void setTitularEmail(String titularEmail) {
		this.titularEmail = titularEmail;
	}
	public String getDestinatariNom() {
		return destinatariNom;
	}
	public void setDestinatariNom(String destinatariNom) {
		this.destinatariNom = destinatariNom;
	}
	public String getDestinatariLlinatges() {
		return destinatariLlinatges;
	}
	public void setDestinatariLlinatges(String destinatariLlinatges) {
		this.destinatariLlinatges = destinatariLlinatges;
	}
	public String getDestinatariNif() {
		return destinatariNif;
	}
	public void setDestinatariNif(String destinatariNif) {
		this.destinatariNif = destinatariNif;
	}
	public String getDestinatariTelefon() {
		return destinatariTelefon;
	}
	public void setDestinatariTelefon(String destinatariTelefon) {
		this.destinatariTelefon = destinatariTelefon;
	}
	public String getDestinatariEmail() {
		return destinatariEmail;
	}
	public void setDestinatariEmail(String destinatariEmail) {
		this.destinatariEmail = destinatariEmail;
	}
	public DomiciliTipusEnum getDomiciliTipus() {
		return domiciliTipus;
	}
	public void setDomiciliTipus(DomiciliTipusEnum domiciliTipus) {
		this.domiciliTipus = domiciliTipus;
	}
	public DomiciliConcretTipusEnum getDomiciliConcretTipus() {
		return domiciliConcretTipus;
	}
	public void setDomiciliConcretTipus(DomiciliConcretTipusEnum domiciliConcretTipus) {
		this.domiciliConcretTipus = domiciliConcretTipus;
	}
	public String getDomiciliViaTipus() {
		return domiciliViaTipus;
	}
	public void setDomiciliViaTipus(String domiciliViaTipus) {
		this.domiciliViaTipus = domiciliViaTipus;
	}
	public String getDomiciliViaNom() {
		return domiciliViaNom;
	}
	public void setDomiciliViaNom(String domiciliViaNom) {
		this.domiciliViaNom = domiciliViaNom;
	}
	public DomiciliNumeracioTipusEnum getDomiciliNumeracioTipus() {
		return domiciliNumeracioTipus;
	}
	public void setDomiciliNumeracioTipus(DomiciliNumeracioTipusEnum domiciliNumeracioTipus) {
		this.domiciliNumeracioTipus = domiciliNumeracioTipus;
	}
	public String getDomiciliNumeracioNumero() {
		return domiciliNumeracioNumero;
	}
	public void setDomiciliNumeracioNumero(String domiciliNumeracioNumero) {
		this.domiciliNumeracioNumero = domiciliNumeracioNumero;
	}
	public String getDomiciliNumeracioPuntKm() {
		return domiciliNumeracioPuntKm;
	}
	public void setDomiciliNumeracioPuntKm(String domiciliNumeracioPuntKm) {
		this.domiciliNumeracioPuntKm = domiciliNumeracioPuntKm;
	}
	public String getDomiciliApartatCorreus() {
		return domiciliApartatCorreus;
	}
	public void setDomiciliApartatCorreus(String domiciliApartatCorreus) {
		this.domiciliApartatCorreus = domiciliApartatCorreus;
	}
	public String getDomiciliBloc() {
		return domiciliBloc;
	}
	public void setDomiciliBloc(String domiciliBloc) {
		this.domiciliBloc = domiciliBloc;
	}
	public String getDomiciliPortal() {
		return domiciliPortal;
	}
	public void setDomiciliPortal(String domiciliPortal) {
		this.domiciliPortal = domiciliPortal;
	}
	public String getDomiciliEscala() {
		return domiciliEscala;
	}
	public void setDomiciliEscala(String domiciliEscala) {
		this.domiciliEscala = domiciliEscala;
	}
	public String getDomiciliPlanta() {
		return domiciliPlanta;
	}
	public void setDomiciliPlanta(String domiciliPlanta) {
		this.domiciliPlanta = domiciliPlanta;
	}
	public String getDomiciliPorta() {
		return domiciliPorta;
	}
	public void setDomiciliPorta(String domiciliPorta) {
		this.domiciliPorta = domiciliPorta;
	}
	public String getDomiciliComplement() {
		return domiciliComplement;
	}
	public void setDomiciliComplement(String domiciliComplement) {
		this.domiciliComplement = domiciliComplement;
	}
	public String getDomiciliPoblacio() {
		return domiciliPoblacio;
	}
	public void setDomiciliPoblacio(String domiciliPoblacio) {
		this.domiciliPoblacio = domiciliPoblacio;
	}
	public String getDomiciliMunicipiCodiIne() {
		return domiciliMunicipiCodiIne;
	}
	public void setDomiciliMunicipiCodiIne(String domiciliMunicipiCodiIne) {
		this.domiciliMunicipiCodiIne = domiciliMunicipiCodiIne;
	}
	public String getDomiciliMunicipiNom() {
		return domiciliMunicipiNom;
	}
	public void setDomiciliMunicipiNom(String domiciliMunicipiNom) {
		this.domiciliMunicipiNom = domiciliMunicipiNom;
	}
	public String getDomiciliCodiPostal() {
		return domiciliCodiPostal;
	}
	public void setDomiciliCodiPostal(String domiciliCodiPostal) {
		this.domiciliCodiPostal = domiciliCodiPostal;
	}
	public String getDomiciliProvinciaCodi() {
		return domiciliProvinciaCodi;
	}
	public void setDomiciliProvinciaCodi(String domiciliProvinciaCodi) {
		this.domiciliProvinciaCodi = domiciliProvinciaCodi;
	}
	public String getDomiciliProvinciaNom() {
		return domiciliProvinciaNom;
	}
	public void setDomiciliProvinciaNom(String domiciliProvinciaNom) {
		this.domiciliProvinciaNom = domiciliProvinciaNom;
	}
	public String getDomiciliPaisCodiIso() {
		return domiciliPaisCodiIso;
	}
	public void setDomiciliPaisCodiIso(String domiciliPaisCodiIso) {
		this.domiciliPaisCodiIso = domiciliPaisCodiIso;
	}
	public String getDomiciliPaisNom() {
		return domiciliPaisNom;
	}
	public void setDomiciliPaisNom(String domiciliPaisNom) {
		this.domiciliPaisNom = domiciliPaisNom;
	}
	public String getDomiciliLinea1() {
		return domiciliLinea1;
	}
	public void setDomiciliLinea1(String domiciliLinea1) {
		this.domiciliLinea1 = domiciliLinea1;
	}
	public String getDomiciliLinea2() {
		return domiciliLinea2;
	}
	public void setDomiciliLinea2(String domiciliLinea2) {
		this.domiciliLinea2 = domiciliLinea2;
	}
	public Integer getDomiciliCie() {
		return domiciliCie;
	}
	public void setDomiciliCie(Integer domiciliCie) {
		this.domiciliCie = domiciliCie;
	}
	public boolean isDehObligat() {
		return dehObligat;
	}
	public void setDehObligat(boolean dehObligat) {
		this.dehObligat = dehObligat;
	}
	public String getDehNif() {
		return dehNif;
	}
	public void setDehNif(String dehNif) {
		this.dehNif = dehNif;
	}
	public String getDehProcedimentCodi() {
		return dehProcedimentCodi;
	}
	public void setDehProcedimentCodi(String dehProcedimentCodi) {
		this.dehProcedimentCodi = dehProcedimentCodi;
	}
	public ServeiTipusEnum getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(ServeiTipusEnum serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public int getRetardPostal() {
		return retardPostal;
	}
	public void setRetardPostal(int retardPostal) {
		this.retardPostal = retardPostal;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public void setCaducitat(Date caducitat) {
		this.caducitat = caducitat;
	}
	public NotificacioEnviamentEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEnviamentEstatEnum estat) {
		this.estat = estat;
	}
	public String getDatatData() {
		return datatData;
	}
	public void setDatatData(String datatData) {
		this.datatData = datatData;
	}
	public String getDatatOrigen() {
		return datatOrigen;
	}
	public void setDatatOrigen(String datatOrigen) {
		this.datatOrigen = datatOrigen;
	}
	public String getDatatEstatCodi() {
		return datatEstatCodi;
	}
	public void setDatatEstatCodi(String datatEstatCodi) {
		this.datatEstatCodi = datatEstatCodi;
	}
	public Date getCertificacioData() {
		return certificacioData;
	}
	public void setCertificacioData(Date certificacioData) {
		this.certificacioData = certificacioData;
	}
	public String getCertificacioOrigen() {
		return certificacioOrigen;
	}
	public void setCertificacioOrigen(String certificacioOrigen) {
		this.certificacioOrigen = certificacioOrigen;
	}
	public NotificacioEnviamentEstatEnum getNotificaEstat() {
		return notificaEstat;
	}
	public void setNotificaEstat(NotificacioEnviamentEstatEnum notificaEstat) {
		this.notificaEstat = notificaEstat;
	}
	public Date getSeuDataPublicacio() {
		return seuDataPublicacio;
	}
	public void setSeuDataPublicacio(Date seuDataPublicacio) {
		this.seuDataPublicacio = seuDataPublicacio;
	}
	public Date getSeuRegistreNumero() {
		return seuRegistreNumero;
	}
	public void setSeuRegistreNumero(Date seuRegistreNumero) {
		this.seuRegistreNumero = seuRegistreNumero;
	}
	public Date getSeuRegistreData() {
		return seuRegistreData;
	}
	public void setSeuRegistreData(Date seuRegistreData) {
		this.seuRegistreData = seuRegistreData;
	}
	public Date getSeuDataFi() {
		return seuDataFi;
	}
	public void setSeuDataFi(Date seuDataFi) {
		this.seuDataFi = seuDataFi;
	}
	public NotificacioEnviamentEstatEnum getSeuEstat() {
		return seuEstat;
	}
	public void setSeuEstat(NotificacioEnviamentEstatEnum seuEstat) {
		this.seuEstat = seuEstat;
	}

}
