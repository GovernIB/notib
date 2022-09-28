/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TipusDocumentDto implements Serializable{

	private Long id;
	private Long entitat;
	private TipusDocumentEnumDto tipusDocEnum;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEntitat() {
		return entitat;
	}
	public void setEntitat(Long entitat) {
		this.entitat = entitat;
	}
	public TipusDocumentEnumDto getTipusDocEnum() {
		return tipusDocEnum;
	}
	public void setTipusDocEnum(TipusDocumentEnumDto tipusDocEnum) {
		this.tipusDocEnum = tipusDocEnum;
	}
	

	private static final long serialVersionUID = 5695764618684273126L;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
