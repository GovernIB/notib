package es.caib.notib.logic.intf.dto.cie;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
public class CieFiltreDto implements Serializable{

	private String organismePagadorCodi;
	private Date contracteDataVig;
	private Long organGestorId;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1305599728317046741L;

}
