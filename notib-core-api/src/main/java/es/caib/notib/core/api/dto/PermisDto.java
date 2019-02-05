/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisDto implements Serializable {

	private Long id;
	private String principal;
	private TipusEnumDto tipus;
	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;
	
	private boolean usuari;
	private boolean administrador;
	private boolean administradorEntitat;
	private boolean aplicacio;
	
	private boolean consulta;
	private boolean processar;
	private boolean notificacio;
	private boolean gestio;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public TipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(TipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public boolean isWrite() {
		return write;
	}
	public void setWrite(boolean write) {
		this.write = write;
	}
	public boolean isCreate() {
		return create;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public boolean isAdministration() {
		return administration;
	}
	public void setAdministration(boolean administration) {
		this.administration = administration;
	}
	
	public boolean isUsuari() {
		return usuari;
	}
	public boolean isAdministrador() {
		return administrador;
	}
	public boolean isAdministradorEntitat() {
		return administradorEntitat;
	}
	public void setUsuari(boolean usuari) {
		this.usuari = usuari;
	}
	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}
	public void setAdministradorEntitat(boolean administradorEntitat) {
		this.administradorEntitat = administradorEntitat;
	}
	public boolean isAplicacio() {
		return aplicacio;
	}
	public void setAplicacio(boolean aplicacio) {
		this.aplicacio = aplicacio;
	}
	
	public boolean isConsulta() {
		return consulta;
	}
	public boolean isProcessar() {
		return processar;
	}
	public boolean isNotificacio() {
		return notificacio;
	}
	public boolean isGestio() {
		return gestio;
	}
	public void setConsulta(boolean consulta) {
		this.consulta = consulta;
	}
	public void setProcessar(boolean processar) {
		this.processar = processar;
	}
	public void setNotificacio(boolean notificacio) {
		this.notificacio = notificacio;
	}
	public void setGestio(boolean gestio) {
		this.gestio = gestio;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
