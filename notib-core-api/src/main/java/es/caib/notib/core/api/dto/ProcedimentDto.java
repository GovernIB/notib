package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ProcedimentDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private String codisia;
	private EntitatDto entitat;
	private PagadorPostalDto pagadorpostal;
	private PagadorCieDto pagadorcie;
	private boolean agrupar;
	private List<GrupDto> grups;
	private String llibre;
	private String oficina;
	private TipusAssumpteEnumDto tipusAssumpte;
	
	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private boolean usuariActualProcessar;
	private boolean usuariActualNotificacio;
	private boolean usuariActualAdministration;
	
	private Date enviamentDataProgramada;
	private int retard;
	
	
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
	public String getCodisia() {
		return codisia;
	}
	public void setCodisia(String codisia) {
		this.codisia = codisia;
	}
	public boolean isAgrupar() {
		return agrupar;
	}
	public void setAgrupar(boolean agrupar) {
		this.agrupar = agrupar;
	}
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	public PagadorPostalDto getPagadorpostal() {
		return pagadorpostal;
	}
	public void setPagadorpostal(PagadorPostalDto pagadorpostal) {
		this.pagadorpostal = pagadorpostal;
	}
	public PagadorCieDto getPagadorcie() {
		return pagadorcie;
	}
	public void setPagadorcie(PagadorCieDto pagadorcie) {
		this.pagadorcie = pagadorcie;
	}
	public List<GrupDto> getGrups() {
		return grups;
	}
	public void setGrups(List<GrupDto> grupsDto) {
		this.grups = grupsDto;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	public int getRetard() {
		return retard;
	}
	public void setRetard(int retard) {
		this.retard = retard;
	}
	public String getLlibre() {
		return llibre;
	}
	public String getOficina() {
		return oficina;
	}
	public TipusAssumpteEnumDto getTipusAssumpte() {
		return tipusAssumpte;
	}
	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public void setTipusAssumpte(TipusAssumpteEnumDto tipusAssumpte) {
		this.tipusAssumpte = tipusAssumpte;
	}
	public List<PermisDto> getPermisos() {
		return permisos;
	}
	public boolean isUsuariActualRead() {
		return usuariActualRead;
	}
	public boolean isUsuariActualProcessar() {
		return usuariActualProcessar;
	}
	public boolean isUsuariActualNotificacio() {
		return usuariActualNotificacio;
	}
	public boolean isUsuariActualAdministration() {
		return usuariActualAdministration;
	}
	public void setPermisos(List<PermisDto> permisos) {
		this.permisos = permisos;
	}
	public void setUsuariActualRead(boolean usuariActualRead) {
		this.usuariActualRead = usuariActualRead;
	}
	public void setUsuariActualProcessar(boolean usuariActualProcessar) {
		this.usuariActualProcessar = usuariActualProcessar;
	}
	public void setUsuariActualNotificacio(boolean usuariActualNotificacio) {
		this.usuariActualNotificacio = usuariActualNotificacio;
	}
	public void setUsuariActualAdministration(boolean usuariActualAdministration) {
		this.usuariActualAdministration = usuariActualAdministration;
	}



	private static final long serialVersionUID = 6058789232924135932L;

}
