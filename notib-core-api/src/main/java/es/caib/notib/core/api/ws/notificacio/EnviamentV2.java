/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació d'un enviament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class EnviamentV2 {

	private String referencia;

	private PersonaV2 titular;
	private List<PersonaV2> destinataris;
	private EntregaPostal entregaPostal;
	private EntregaDeh entregaDeh;
	private ServeiTipusEnum serveiTipus;

	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public PersonaV2 getTitular() {
		return titular;
	}
	public void setTitular(PersonaV2 titular) {
		this.titular = titular;
	}
	public List<PersonaV2> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<PersonaV2> destinataris) {
		this.destinataris = destinataris;
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
	public ServeiTipusEnum getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(ServeiTipusEnum serveiTipus) {
		this.serveiTipus = serveiTipus;
	}

}
