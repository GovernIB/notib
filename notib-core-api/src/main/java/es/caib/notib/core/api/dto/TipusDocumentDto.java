/**
 * 
 */
package es.caib.notib.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TipusDocumentDto {

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
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
