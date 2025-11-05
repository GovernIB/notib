package es.caib.notib.logic.intf.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Informació d'una entitat a registrar a la base dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EntitatDataDto extends AuditoriaDto {

	private Long id;
	@EqualsAndHashCode.Include
	private String codi;
	private String nom;
	private EntitatTipusEnumDto tipus;
	@EqualsAndHashCode.Include
	private String dir3Codi;
	private String dir3CodiReg;
	private String apiKey;
	private boolean ambEntregaDeh;
	private String descripcio;

//	private boolean activa;
    private String logoCap;
    private String logoPeu;
	private byte[] logoCapBytes;
	private boolean eliminarLogoCap;
	private byte[] logoPeuBytes;
	private boolean eliminarLogoPeu;
	private String colorFons;
	private String colorLletra;
	private List<TipusDocumentDto> tipusDoc;
	private TipusDocumentDto tipusDocDefault;
//	private List<PermisDto> permisos;
//	private boolean usuariActualAdministradorEntitat;
//	private boolean usuariActualAdministradorOrgan;
//	private Long numAplicacions;

	private boolean entregaCieActiva;
	private Long operadorPostalId;
	private Long cieId;

	private boolean llibreEntitat;
	private String oficina;
	private String nomOficinaVirtual;

	private boolean oficinaEntitat;
	protected String llibre;
	protected String llibreNom;
}
