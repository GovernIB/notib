package es.caib.notib.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class ProcedimentCommand {
	
	private Long id;
	@NotEmpty @Size(max=9)
	private String codi;
	@NotEmpty @Size(max=100)
	private String nom;
	private Long entitatId;
	private String entitatNom;
	private Long pagadorPostalId;
	private Long pagadorCieId;	
	private boolean agrupar;
	private boolean consulta;
	private boolean processar;
	private boolean notificacio;
	private boolean gestio;
	private int retard;
	private String oficina;
	private String oficinaNom;
	private String llibre;
	private String llibreNom;
	private String organGestor;
	private String organGestorNom;
	private String tipusAssumpte;
	private String tipusAssumpteNom;
	private String codiAssumpte;
	private String codiAssumpteNom;
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
	public boolean isConsulta() {
		return consulta;
	}
	public void setConsulta(boolean consulta) {
		this.consulta = consulta;
	}
	public boolean isProcessar() {
		return processar;
	}
	public void setProcessar(boolean processar) {
		this.processar = processar;
	}
	public boolean isNotificacio() {
		return notificacio;
	}
	public void setNotificacio(boolean notificacio) {
		this.notificacio = notificacio;
	}
	public boolean isGestio() {
		return gestio;
	}
	public void setGestio(boolean gestio) {
		this.gestio = gestio;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public Long getPagadorPostalId() {
		return pagadorPostalId;
	}
	public void setPagadorPostalId(Long pagadorPostalId) {
		this.pagadorPostalId = pagadorPostalId;
	}
	public Long getPagadorCieId() {
		return pagadorCieId;
	}
	public void setPagadorCieId(Long pagadorCieId) {
		this.pagadorCieId = pagadorCieId;
	}
	public String getEntitatNom() {
		return entitatNom;
	}
	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}
	public int getRetard() {
		return retard;
	}
	public void setRetard(int retard) {
		this.retard = retard;
	}
	public String getOficina() {
		return oficina;
	}
	public String getLlibre() {
		return llibre;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public void setLlibre(String llibre) {
		this.llibre = llibre;
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
	public void setTipusAssumpte(String tipusAssumpte) {
		this.tipusAssumpte = tipusAssumpte;
	}
	public String getCodiAssumpte() {
		return codiAssumpte;
	}
	public void setCodiAssumpte(String codiAssumpte) {
		this.codiAssumpte = codiAssumpte;
	}
	public String getOficinaNom() {
		return oficinaNom;
	}
	public void setOficinaNom(String oficinaNom) {
		this.oficinaNom = oficinaNom;
	}
	public String getLlibreNom() {
		return llibreNom;
	}
	public void setLlibreNom(String llibreNom) {
		this.llibreNom = llibreNom;
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
	public static ProcedimentCommand asCommand(ProcedimentDto dto) {
		if (dto == null) {
			return null;
		}
		ProcedimentCommand command = ConversioTipusHelper.convertir(
				dto,
				ProcedimentCommand.class );
		return command;
	}
	public static ProcedimentDto asDto(ProcedimentCommand command) {
		if (command == null) {
			return null;
		}
		ProcedimentDto dto = ConversioTipusHelper.convertir(
				command,
				ProcedimentDto.class);
		
		EntitatDto entitatDto = new EntitatDto();
		entitatDto.setId(command.getEntitatId());
		dto.setEntitat(entitatDto);

		PagadorPostalDto pagadoPostalDto = null;
		if (command.getPagadorPostalId() != null) {
			pagadoPostalDto = new PagadorPostalDto();
			pagadoPostalDto.setId(command.getPagadorPostalId());
		}
		dto.setPagadorpostal(pagadoPostalDto);

		PagadorCieDto pagadorCieDto = null;
		if (command.getPagadorCieId() != null) {
			pagadorCieDto = new PagadorCieDto();
			pagadorCieDto.setId(command.getPagadorCieId());
		}
		dto.setPagadorcie(pagadorCieDto);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
