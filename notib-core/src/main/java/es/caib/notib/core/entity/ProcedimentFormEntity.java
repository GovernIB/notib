package es.caib.notib.core.entity;

import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.*;

/**
 * Classe de model de dades que conté la informació dels procediments i pagadors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@Entity
@Subselect("select 	pro.id as id,"
		+ "			pro.codi as codi, "
		+ "			pro.nom as nom, "
		+ "			ent.nom as entitatNom, "
		+ "			pro.organ_gestor as organGestor, "
		+ "			og.nom as organGestorNom, "	
		+ "			og.estat as organGestorEstat, "
		+ "			pro.codiassumpte as codiassumpte, "
		+ " 		pro.tipusassumpte as tipusassumpte, "		
		+ "			og.llibre as llibre, "
		+ "			ent.oficina as oficina, "
		+ "         pro.agrupar as agrupar, "
		+ "         pro.caducitat as caducitat, "
		+ "         CASE "
		+ "				WHEN pro.ENTREGA_CIE_ID is not null then 1 "
		+ "				WHEN og.ENTREGA_CIE_ID is not null then 2 "
		+ "				WHEN ent.ENTREGA_CIE_ID is not null then 3 "
		+ "				else 0 "
		+ "			end as entregaCieActiva, "
//		+ "			cie.dir3_codi as pagadorcie, "
//		+ "			postal.dir3_codi as pagadorpostal, "
		+ "			pro.retard as retard, "
		+ "			pro.comu as comu, "
		+ "         pro.DIRECT_PERMISSION_REQUIRED as requireDirectPermission, "
		+ "         pro.entitat as entitat_id"
		+ " from	not_procediment pro "
		+ " left outer join not_entitat ent on ent.id = pro.entitat "
//		+ " left outer join not_pagador_postal postal on postal.id = pro.pagadorpostal "
		+ " left outer join not_organ_gestor og on pro.organ_gestor = og.codi "
		+ " where pro.tipus = 'PROCEDIMENT'")
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
	
	@Column(name = "organGestor")
	protected String organGestor;

	@Column(name = "organGestorNom")
	protected String organGestorNom;
	
	@Column(name = "organGestorEstat")
	@Enumerated(EnumType.ORDINAL)
	protected OrganGestorEstatEnum organGestorEstat;
	
	@Column(name = "tipusassumpte")
	protected String tipusAssumpte;
	
	@Column(name = "llibre")
	protected String llibre;
	
	@Column(name = "oficina")
	protected String oficina;

	@Column(name = "agrupar")
	protected boolean agrupar;

	@Column(name = "retard")
	protected Integer retard;
	
	@Column(name = "entitat_id")
	protected Long entitat_id;
	
	@Column(name = "comu")
	protected boolean comu;

	@Column(name = "caducitat")
	protected Integer caducitat;

	// 0 (Inactiva) / 1 (Activa per procediment) / 2 (Activa per òrgan gestor) / 3 (Activa per entitat)
	@Column(name = "entregaCieActiva")
	protected int entregaCieActiva;

	@Column(name = "requireDirectPermission")
	protected boolean requireDirectPermission;
}
