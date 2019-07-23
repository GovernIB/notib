package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.List;

public class ProcedimentDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private EntitatDto entitat;
	private PagadorPostalDto pagadorpostal;
	private PagadorCieDto pagadorcie;
	private boolean agrupar;
	private List<GrupDto> grups;
	private String llibre;
	private String llibreNom;
	private String oficina;
	private String oficinaNom;
	private String organGestor;
	private String organGestorNom;
	private String tipusAssumpte;
	private String tipusAssumpteNom;
	private String codiAssumpte;
	private String codiAssumpteNom;
	
	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private boolean usuariActualProcessar;
	private boolean usuariActualNotificacio;
	private boolean usuariActualAdministration;
	
	private int retard;
	private int caducitat;
	
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
	public String getOrganGestor() {
		return organGestor;
	}
	public void setOrganGestor(String organGestor) {
		this.organGestor = organGestor;
	}
	public String getTipusAssumpte() {
		return tipusAssumpte;
	}
	public String getCodiAssumpte() {
		return codiAssumpte;
	}
	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public void setTipusAssumpte(String tipusAssumpte) {
		this.tipusAssumpte = tipusAssumpte;
	}
	public void setCodiAssumpte(String codiAssumpte) {
		this.codiAssumpte = codiAssumpte;
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
	public String getLlibreNom() {
		return llibreNom;
	}
	public void setLlibreNom(String llibreNom) {
		this.llibreNom = llibreNom;
	}
	public String getOficinaNom() {
		return oficinaNom;
	}
	public void setOficinaNom(String oficinaNom) {
		this.oficinaNom = oficinaNom;
	}
	public String getOrganGestorNom() {
		return organGestorNom;
	}
	public void setOrganGestorNom(String organGestorNom) {
		this.organGestorNom = organGestorNom;
	}
	public String getTipusAssumpteNom() {
		return tipusAssumpteNom;
	}
	public void setTipusAssumpteNom(String tipusAssumpteNom) {
		this.tipusAssumpteNom = tipusAssumpteNom;
	}
	public String getCodiAssumpteNom() {
		return codiAssumpteNom;
	}
	public void setCodiAssumpteNom(String codiAssumpteNom) {
		this.codiAssumpteNom = codiAssumpteNom;
	}
	public int getCaducitat() {
		return caducitat;
	}
	public void setCaducitat(int caducitat) {
		this.caducitat = caducitat;
	}

	private static final long serialVersionUID = 6058789232924135932L;

}
