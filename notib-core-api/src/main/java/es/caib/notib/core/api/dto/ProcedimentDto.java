package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
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
	
	private static final long serialVersionUID = 6058789232924135932L;





}
