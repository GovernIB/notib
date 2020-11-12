/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto;

/**
 * Informació d'un enviament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class Enviament {

	private Long id;
	private String referencia;
	private Persona titular;
	private List<Persona> destinataris;
	private boolean entregaPostalActiva;
	private EntregaPostal entregaPostal;
	private boolean entregaDehActiva;
	private EntregaDeh entregaDeh;
	private NotificaServeiTipusEnumDto serveiTipus;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public Persona getTitular() {
		return titular;
	}
	public void setTitular(Persona titular) {
		this.titular = titular;
	}
	public List<Persona> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<Persona> destinataris) {
		this.destinataris = destinataris;
	}
	public boolean isEntregaPostalActiva() {
		return entregaPostalActiva;
	}
	public void setEntregaPostalActiva(boolean entregaPostalActiva) {
		this.entregaPostalActiva = entregaPostalActiva;
	}
	public boolean isEntregaDehActiva() {
		return entregaDehActiva;
	}
	public void setEntregaDehActiva(boolean entregaDehActiva) {
		this.entregaDehActiva = entregaDehActiva;
	}
	public EntregaPostal getEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(EntregaPostal entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public EntregaDeh getEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(EntregaDeh entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public NotificaServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(NotificaServeiTipusEnumDto serveiTipus) {
		this.serveiTipus = serveiTipus;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
