package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PagadorPostalDto extends AuditoriaDto implements Serializable {
	
	private Long id;
	private String dir3codi;
	private String contracteNum;
	private Date contracteDataVig;
	private String facturacioClientCodi;
	private EntitatDto entitat;


	private static final long serialVersionUID = 6875716151909763392L;

}
