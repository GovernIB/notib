/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioEnviamentDto extends AuditoriaDto {

	private Long id;
	private String titularNif;
	private String titularNom;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularTelefon;
	private String titularEmail;
	private String destinatariNif;
	private String destinatariNom;
	private String destinatariLlinatge1;
	private String destinatariLlinatge2;
	private String destinatariTelefon;
	private String destinatariEmail;
	private NotificaDomiciliTipusEnumDto domiciliTipus;
	private NotificaDomiciliConcretTipusEnumDto domiciliConcretTipus;
	private NotificaDomiciliViaTipusEnumDto domiciliViaTipus;
	private String domiciliViaNom;
	private NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus;
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
	protected String domiciliPaisCodiIso;
	protected String domiciliPaisNom;
	protected String domiciliLinea1;
	protected String domiciliLinea2;
	private Integer domiciliCie;
	private boolean dehObligat;
	private String dehNif;
	private String dehProcedimentCodi;
	private NotificaServeiTipusEnumDto serveiTipus;
	private String formatSobre;
	private String formatFulla;
	private String notificaReferencia;
	private String notificaIdentificador;
	private Date notificaDataCreacio;
	private Date notificaDataDisposicio;
	private Date notificaDataCaducitat;
	private String notificaEmisorDir3;
	private String notificaEmisorDescripcio;
	private String notificaEmisorNif;
	private String notificaArrelDir3;
	private String notificaArrelDescripcio;
	private String notificaArrelNif;
	private NotificacioEnviamentEstatEnumDto notificaEstat;
	private Date notificaEstatData;
	private String notificaEstatDescripcio;
	private String notificaDatatOrigen;
	private String notificaDatatReceptorNif;
	private String notificaDatatReceptorNom;
	private String notificaDatatNumSeguiment;
	private String notificaDatatErrorDescripcio;
	private Date notificaCertificacioData;
	private String notificaCertificacioArxiuId;
	private String notificaCertificacioHash;
	private String notificaCertificacioOrigen;
	private String notificaCertificacioMetadades;
	private String notificaCertificacioCsv;
	private String notificaCertificacioMime;
	private Integer notificaCertificacioTamany;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	private String notificaCertificacioNumSeguiment;
	private boolean notificaError;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private String seuRegistreNumero;
	private Date seuRegistreData;
	private Date seuDataFi;
	private NotificacioEnviamentEstatEnumDto seuEstat;
	private boolean seuError;
	private Date seuErrorData;
	private String seuErrorDescripcio;
	private Date seuDataEnviament;
	private int seuReintentsEnviament;
	private Date seuDataEstat;
	private Date seuDataNotificaInformat;
	private Date seuDataNotificaDarreraPeticio;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitularNif() {
		return titularNif;
	}
	public void setTitularNif(String titularNif) {
		this.titularNif = titularNif;
	}
	public String getTitularNom() {
		return titularNom;
	}
	public void setTitularNom(String titularNom) {
		this.titularNom = titularNom;
	}
	public String getTitularLlinatge1() {
		return titularLlinatge1;
	}
	public void setTitularLlinatge1(String titularLlinatge1) {
		this.titularLlinatge1 = titularLlinatge1;
	}
	public String getTitularLlinatge2() {
		return titularLlinatge2;
	}
	public void setTitularLlinatge2(String titularLlinatge2) {
		this.titularLlinatge2 = titularLlinatge2;
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
	public String getDestinatariNif() {
		return destinatariNif;
	}
	public void setDestinatariNif(String destinatariNif) {
		this.destinatariNif = destinatariNif;
	}
	public String getDestinatariNom() {
		return destinatariNom;
	}
	public void setDestinatariNom(String destinatariNom) {
		this.destinatariNom = destinatariNom;
	}
	public String getDestinatariLlinatge1() {
		return destinatariLlinatge1;
	}
	public void setDestinatariLlinatge1(String destinatariLlinatge1) {
		this.destinatariLlinatge1 = destinatariLlinatge1;
	}
	public String getDestinatariLlinatge2() {
		return destinatariLlinatge2;
	}
	public void setDestinatariLlinatge2(String destinatariLlinatge2) {
		this.destinatariLlinatge2 = destinatariLlinatge2;
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
	public NotificaDomiciliTipusEnumDto getDomiciliTipus() {
		return domiciliTipus;
	}
	public void setDomiciliTipus(NotificaDomiciliTipusEnumDto domiciliTipus) {
		this.domiciliTipus = domiciliTipus;
	}
	public NotificaDomiciliConcretTipusEnumDto getDomiciliConcretTipus() {
		return domiciliConcretTipus;
	}
	public void setDomiciliConcretTipus(NotificaDomiciliConcretTipusEnumDto domiciliConcretTipus) {
		this.domiciliConcretTipus = domiciliConcretTipus;
	}
	public NotificaDomiciliViaTipusEnumDto getDomiciliViaTipus() {
		return domiciliViaTipus;
	}
	public void setDomiciliViaTipus(NotificaDomiciliViaTipusEnumDto domiciliViaTipus) {
		this.domiciliViaTipus = domiciliViaTipus;
	}
	public String getDomiciliViaNom() {
		return domiciliViaNom;
	}
	public void setDomiciliViaNom(String domiciliViaNom) {
		this.domiciliViaNom = domiciliViaNom;
	}
	public NotificaDomiciliNumeracioTipusEnumDto getDomiciliNumeracioTipus() {
		return domiciliNumeracioTipus;
	}
	public void setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus) {
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
	public NotificaServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(NotificaServeiTipusEnumDto serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public String getFormatSobre() {
		return formatSobre;
	}
	public void setFormatSobre(String formatSobre) {
		this.formatSobre = formatSobre;
	}
	public String getFormatFulla() {
		return formatFulla;
	}
	public void setFormatFulla(String formatFulla) {
		this.formatFulla = formatFulla;
	}
	public String getNotificaReferencia() {
		return notificaReferencia;
	}
	public void setNotificaReferencia(String notificaReferencia) {
		this.notificaReferencia = notificaReferencia;
	}
	public String getNotificaIdentificador() {
		return notificaIdentificador;
	}
	public void setNotificaIdentificador(String notificaIdentificador) {
		this.notificaIdentificador = notificaIdentificador;
	}
	public Date getNotificaDataCreacio() {
		return notificaDataCreacio;
	}
	public void setNotificaDataCreacio(Date notificaDataCreacio) {
		this.notificaDataCreacio = notificaDataCreacio;
	}
	public Date getNotificaDataDisposicio() {
		return notificaDataDisposicio;
	}
	public void setNotificaDataDisposicio(Date notificaDataDisposicio) {
		this.notificaDataDisposicio = notificaDataDisposicio;
	}
	public Date getNotificaDataCaducitat() {
		return notificaDataCaducitat;
	}
	public void setNotificaDataCaducitat(Date notificaDataCaducitat) {
		this.notificaDataCaducitat = notificaDataCaducitat;
	}
	public String getNotificaEmisorDir3() {
		return notificaEmisorDir3;
	}
	public void setNotificaEmisorDir3(String notificaEmisorDir3) {
		this.notificaEmisorDir3 = notificaEmisorDir3;
	}
	public String getNotificaEmisorDescripcio() {
		return notificaEmisorDescripcio;
	}
	public void setNotificaEmisorDescripcio(String notificaEmisorDescripcio) {
		this.notificaEmisorDescripcio = notificaEmisorDescripcio;
	}
	public String getNotificaEmisorNif() {
		return notificaEmisorNif;
	}
	public void setNotificaEmisorNif(String notificaEmisorNif) {
		this.notificaEmisorNif = notificaEmisorNif;
	}
	public String getNotificaArrelDir3() {
		return notificaArrelDir3;
	}
	public void setNotificaArrelDir3(String notificaArrelDir3) {
		this.notificaArrelDir3 = notificaArrelDir3;
	}
	public String getNotificaArrelDescripcio() {
		return notificaArrelDescripcio;
	}
	public void setNotificaArrelDescripcio(String notificaArrelDescripcio) {
		this.notificaArrelDescripcio = notificaArrelDescripcio;
	}
	public String getNotificaArrelNif() {
		return notificaArrelNif;
	}
	public void setNotificaArrelNif(String notificaArrelNif) {
		this.notificaArrelNif = notificaArrelNif;
	}
	public NotificacioEnviamentEstatEnumDto getNotificaEstat() {
		return notificaEstat;
	}
	public void setNotificaEstat(NotificacioEnviamentEstatEnumDto notificaEstat) {
		this.notificaEstat = notificaEstat;
	}
	public Date getNotificaEstatData() {
		return notificaEstatData;
	}
	public void setNotificaEstatData(Date notificaEstatData) {
		this.notificaEstatData = notificaEstatData;
	}
	public String getNotificaEstatDescripcio() {
		return notificaEstatDescripcio;
	}
	public void setNotificaEstatDescripcio(String notificaEstatDescripcio) {
		this.notificaEstatDescripcio = notificaEstatDescripcio;
	}
	public String getNotificaDatatOrigen() {
		return notificaDatatOrigen;
	}
	public void setNotificaDatatOrigen(String notificaDatatOrigen) {
		this.notificaDatatOrigen = notificaDatatOrigen;
	}
	public String getNotificaDatatReceptorNif() {
		return notificaDatatReceptorNif;
	}
	public void setNotificaDatatReceptorNif(String notificaDatatReceptorNif) {
		this.notificaDatatReceptorNif = notificaDatatReceptorNif;
	}
	public String getNotificaDatatReceptorNom() {
		return notificaDatatReceptorNom;
	}
	public void setNotificaDatatReceptorNom(String notificaDatatReceptorNom) {
		this.notificaDatatReceptorNom = notificaDatatReceptorNom;
	}
	public String getNotificaDatatNumSeguiment() {
		return notificaDatatNumSeguiment;
	}
	public void setNotificaDatatNumSeguiment(String notificaDatatNumSeguiment) {
		this.notificaDatatNumSeguiment = notificaDatatNumSeguiment;
	}
	public String getNotificaDatatErrorDescripcio() {
		return notificaDatatErrorDescripcio;
	}
	public void setNotificaDatatErrorDescripcio(String notificaDatatErrorDescripcio) {
		this.notificaDatatErrorDescripcio = notificaDatatErrorDescripcio;
	}
	public Date getNotificaCertificacioData() {
		return notificaCertificacioData;
	}
	public void setNotificaCertificacioData(Date notificaCertificacioData) {
		this.notificaCertificacioData = notificaCertificacioData;
	}
	public String getNotificaCertificacioArxiuId() {
		return notificaCertificacioArxiuId;
	}
	public void setNotificaCertificacioArxiuId(String notificaCertificacioArxiuId) {
		this.notificaCertificacioArxiuId = notificaCertificacioArxiuId;
	}
	public String getNotificaCertificacioHash() {
		return notificaCertificacioHash;
	}
	public void setNotificaCertificacioHash(String notificaCertificacioHash) {
		this.notificaCertificacioHash = notificaCertificacioHash;
	}
	public String getNotificaCertificacioOrigen() {
		return notificaCertificacioOrigen;
	}
	public void setNotificaCertificacioOrigen(String notificaCertificacioOrigen) {
		this.notificaCertificacioOrigen = notificaCertificacioOrigen;
	}
	public String getNotificaCertificacioMetadades() {
		return notificaCertificacioMetadades;
	}
	public void setNotificaCertificacioMetadades(String notificaCertificacioMetadades) {
		this.notificaCertificacioMetadades = notificaCertificacioMetadades;
	}
	public String getNotificaCertificacioCsv() {
		return notificaCertificacioCsv;
	}
	public void setNotificaCertificacioCsv(String notificaCertificacioCsv) {
		this.notificaCertificacioCsv = notificaCertificacioCsv;
	}
	public String getNotificaCertificacioMime() {
		return notificaCertificacioMime;
	}
	public void setNotificaCertificacioMime(String notificaCertificacioMime) {
		this.notificaCertificacioMime = notificaCertificacioMime;
	}
	public Integer getNotificaCertificacioTamany() {
		return notificaCertificacioTamany;
	}
	public void setNotificaCertificacioTamany(Integer notificaCertificacioTamany) {
		this.notificaCertificacioTamany = notificaCertificacioTamany;
	}
	public NotificaCertificacioTipusEnumDto getNotificaCertificacioTipus() {
		return notificaCertificacioTipus;
	}
	public void setNotificaCertificacioTipus(NotificaCertificacioTipusEnumDto notificaCertificacioTipus) {
		this.notificaCertificacioTipus = notificaCertificacioTipus;
	}
	public NotificaCertificacioArxiuTipusEnumDto getNotificaCertificacioArxiuTipus() {
		return notificaCertificacioArxiuTipus;
	}
	public void setNotificaCertificacioArxiuTipus(NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus) {
		this.notificaCertificacioArxiuTipus = notificaCertificacioArxiuTipus;
	}
	public String getNotificaCertificacioNumSeguiment() {
		return notificaCertificacioNumSeguiment;
	}
	public void setNotificaCertificacioNumSeguiment(String notificaCertificacioNumSeguiment) {
		this.notificaCertificacioNumSeguiment = notificaCertificacioNumSeguiment;
	}
	public boolean isNotificaError() {
		return notificaError;
	}
	public void setNotificaError(boolean notificaError) {
		this.notificaError = notificaError;
	}
	public Date getNotificaErrorData() {
		return notificaErrorData;
	}
	public void setNotificaErrorData(Date notificaErrorData) {
		this.notificaErrorData = notificaErrorData;
	}
	public String getNotificaErrorDescripcio() {
		return notificaErrorDescripcio;
	}
	public void setNotificaErrorDescripcio(String notificaErrorDescripcio) {
		this.notificaErrorDescripcio = notificaErrorDescripcio;
	}
	public String getSeuRegistreNumero() {
		return seuRegistreNumero;
	}
	public void setSeuRegistreNumero(String seuRegistreNumero) {
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
	public NotificacioEnviamentEstatEnumDto getSeuEstat() {
		return seuEstat;
	}
	public void setSeuEstat(NotificacioEnviamentEstatEnumDto seuEstat) {
		this.seuEstat = seuEstat;
	}
	public boolean isSeuError() {
		return seuError;
	}
	public void setSeuError(boolean seuError) {
		this.seuError = seuError;
	}
	public Date getSeuErrorData() {
		return seuErrorData;
	}
	public void setSeuErrorData(Date seuErrorData) {
		this.seuErrorData = seuErrorData;
	}
	public String getSeuErrorDescripcio() {
		return seuErrorDescripcio;
	}
	public void setSeuErrorDescripcio(String seuErrorDescripcio) {
		this.seuErrorDescripcio = seuErrorDescripcio;
	}
	public Date getSeuDataEnviament() {
		return seuDataEnviament;
	}
	public void setSeuDataEnviament(Date seuDataEnviament) {
		this.seuDataEnviament = seuDataEnviament;
	}
	public int getSeuReintentsEnviament() {
		return seuReintentsEnviament;
	}
	public void setSeuReintentsEnviament(int seuReintentsEnviament) {
		this.seuReintentsEnviament = seuReintentsEnviament;
	}
	public Date getSeuDataEstat() {
		return seuDataEstat;
	}
	public void setSeuDataEstat(Date seuDataEstat) {
		this.seuDataEstat = seuDataEstat;
	}
	public Date getSeuDataNotificaInformat() {
		return seuDataNotificaInformat;
	}
	public void setSeuDataNotificaInformat(Date seuDataNotificaInformat) {
		this.seuDataNotificaInformat = seuDataNotificaInformat;
	}
	public Date getSeuDataNotificaDarreraPeticio() {
		return seuDataNotificaDarreraPeticio;
	}
	public void setSeuDataNotificaDarreraPeticio(Date seuDataNotificaDarreraPeticio) {
		this.seuDataNotificaDarreraPeticio = seuDataNotificaDarreraPeticio;
	}

	public String getTitularLlinatges() {
		return concatenarLlinatges(
				titularLlinatge1,
				titularLlinatge2);
	}
	public String getTitular() {
		StringBuilder sb = new StringBuilder();
		sb.append(titularNom);
		String llinatges = getTitularLlinatges();
		if (llinatges != null && !llinatges.isEmpty()) {
			sb.append(" ");
			sb.append(llinatges);
		}
		sb.append(" (");
		sb.append(destinatariNif);
		sb.append(")");
		return sb.toString();
	}

	public String getDestinatariLlinatges() {
		return concatenarLlinatges(
				destinatariLlinatge1,
				destinatariLlinatge2);
	}
	public String getDestinatari() {
		StringBuilder sb = new StringBuilder();
		sb.append(destinatariNom);
		String llinatges = getDestinatariLlinatges();
		if (llinatges != null && !llinatges.isEmpty()) {
			sb.append(" ");
			sb.append(llinatges);
		}
		sb.append(" (");
		sb.append(destinatariNif);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}



	private String concatenarLlinatges(
			String llinatge1,
			String llinatge2) {
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

	private static final long serialVersionUID = -139254994389509932L;

}
