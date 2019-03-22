package es.caib.notib.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

/**
 * Classe de model de dades que conté la informació dels procediments i pagadors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Subselect("select 	pro.id as id,"
		+ "			pro.codi as codi, "
		+ "			pro.nom as nom, "
		+ "			ent.nom as entitatNom, "
		+ "			pro.codiassumpte as codiassumpte, "
		+ " 		pro.tipusassumpte as tipusassumpte, "		
		+ "			pro.llibre as llibre, "
		+ "			pro.oficina as oficina, "
		+ "         pro.agrupar as agrupar, "
		+ "			cie.dir3_codi as pagadorcie, "
		+ "			postal.dir3_codi as pagadorpostal, "
		+ "			pro.retard as retard, "
		+ "         pro.entitat as entitat_id"
		+ " from	not_procediment pro "
		+ " left outer join not_pagador_cie cie on cie.id = pro.pagadorcie "
		+ " left outer join not_entitat ent on ent.id = pro.entitat "
		+ " left outer join not_pagador_postal postal on postal.id = pro.pagadorpostal")
@Immutable
public class ProcedimentFormEntity {
	
	@Id
	@Column(name = "id")
	private Long id;
	
	@Column(name = "codi")
	protected String codi;
	
	@Column(name = "nom")
	protected String nom;

	@Column(name = "entitatNom")
	protected String entitatNom;
	
	@Column(name = "codiassumpte")
	protected String codiAssumpte;
	
	@Column(name = "tipusassumpte")
	protected String tipusAssumpte;
	
	@Column(name = "llibre")
	protected String llibre;
	
	@Column(name = "oficina")
	protected String oficina;

	@Column(name = "pagadorcie")
	protected String pagadorcie;
	
	@Column(name = "agrupar")
	protected boolean agrupar;
	
	@Column(name = "pagadorpostal")
	protected String pagadorpostal;
	
	@Column(name = "retard")
	protected Integer retard;
	
	@Column(name = "entitat_id")
	protected Long entitat_id;

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

	public String getEntitatNom() {
		return entitatNom;
	}

	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}

	public boolean isAgrupar() {
		return agrupar;
	}

	public void setAgrupar(boolean agrupar) {
		this.agrupar = agrupar;
	}

	public String getCodiAssumpte() {
		return codiAssumpte;
	}

	public void setCodiAssumpte(String codiAssumpte) {
		this.codiAssumpte = codiAssumpte;
	}

	public String getTipusAssumpte() {
		return tipusAssumpte;
	}

	public void setTipusAssumpte(String tipusAssumpte) {
		this.tipusAssumpte = tipusAssumpte;
	}

	public String getLlibre() {
		return llibre;
	}

	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}

	public String getOficina() {
		return oficina;
	}

	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	
	public String getPagadorcie() {
		return pagadorcie;
	}

	public void setPagadorcie(String pagadorcie) {
		this.pagadorcie = pagadorcie;
	}

	public String getPagadorpostal() {
		return pagadorpostal;
	}

	public void setPagadorpostal(String pagadorpostal) {
		this.pagadorpostal = pagadorpostal;
	}

	public Integer getRetard() {
		return retard;
	}

	public void setRetard(Integer retard) {
		this.retard = retard;
	}

	public Long getEntitat_id() {
		return entitat_id;
	}

	public void setEntitat_id(Long entitat_id) {
		this.entitat_id = entitat_id;
	}

}
