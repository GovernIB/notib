package es.caib.notib.logic.intf.dto.cie;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
public class CieTableItemDto implements Serializable{
	private Long id;
	private String nom;
	private String organismePagador;
	private Date contracteDataVig;
	private String organismeEmisor;
}
