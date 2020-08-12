package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PagadorCieDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String dir3codi;
	private Date contracteDataVig;
	private Long entitatId;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1305599728317046741L;

}
