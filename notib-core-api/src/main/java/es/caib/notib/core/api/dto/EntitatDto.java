/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatDto extends AuditoriaDto {

	private Long id;
	private String codi;
	private String nom;
	private EntitatTipusEnumDto tipus;
	private String dir3Codi;
	private String dir3CodiReg;
	private String apiKey;
	private boolean ambEntregaDeh;
	private String descripcio;
	private boolean activa;
	private byte[] logoCapBytes;
	private boolean eliminarLogoCap;
	private byte[] logoPeuBytes;
	private boolean eliminarLogoPeu;
	private String colorFons;
	private String colorLletra;
	private List<TipusDocumentDto> tipusDoc;
	private TipusDocumentDto tipusDocDefault;
	private List<PermisDto> permisos;
	private boolean usuariActualAdministradorEntitat;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public EntitatTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(EntitatTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public String getDir3CodiReg() {
		return dir3CodiReg;
	}
	public void setDir3CodiReg(String dir3CodiReg) {
		this.dir3CodiReg = dir3CodiReg;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public boolean isAmbEntregaDeh() {
		return ambEntregaDeh;
	}
	public void setAmbEntregaDeh(boolean ambEntregaDeh) {
		this.ambEntregaDeh = ambEntregaDeh;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	public byte[] getLogoCapBytes() {
		return logoCapBytes;
	}
	public void setLogoCapBytes(byte[] logoCapBytes) {
		this.logoCapBytes = logoCapBytes;
	}
	public byte[] getLogoPeuBytes() {
		return logoPeuBytes;
	}
	public void setLogoPeuBytes(byte[] logoPeuBytes) {
		this.logoPeuBytes = logoPeuBytes;
	}
	public boolean isEliminarLogoCap() {
		return eliminarLogoCap;
	}
	public void setEliminarLogoCap(boolean eliminarLogoCap) {
		this.eliminarLogoCap = eliminarLogoCap;
	}
	public boolean isEliminarLogoPeu() {
		return eliminarLogoPeu;
	}
	public void setEliminarLogoPeu(boolean eliminarLogoPeu) {
		this.eliminarLogoPeu = eliminarLogoPeu;
	}
	public String getColorFons() {
		return colorFons;
	}
	public void setColorFons(String colorFons) {
		this.colorFons = colorFons;
	}
	public String getColorLletra() {
		return colorLletra;
	}
	public void setColorLletra(String colorLletra) {
		this.colorLletra = colorLletra;
	}
	public List<TipusDocumentDto> getTipusDoc() {
		return tipusDoc;
	}
	public void setTipusDoc(List<TipusDocumentDto> tipusDoc) {
		this.tipusDoc = tipusDoc;
	}
	public TipusDocumentDto getTipusDocDefault() {
		return tipusDocDefault;
	}
	public void setTipusDocDefault(TipusDocumentDto tipusDocDefault) {
		this.tipusDocDefault = tipusDocDefault;
	}
	public List<PermisDto> getPermisos() {
		return permisos;
	}
	public void setPermisos(List<PermisDto> permisos) {
		this.permisos = permisos;
	}
	public boolean isUsuariActualAdministradorEntitat() {
		return usuariActualAdministradorEntitat;
	}
	public void setUsuariActualAdministradorEntitat(boolean usuariActualAdministradorEntitat) {
		this.usuariActualAdministradorEntitat = usuariActualAdministradorEntitat;
	}

	public boolean getActiva() {
		return activa;
	}
	public void setActiva(Boolean activa) {
		this.activa = activa;
	}
	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
