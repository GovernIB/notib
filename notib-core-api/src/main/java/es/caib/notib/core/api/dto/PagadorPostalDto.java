package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class PagadorPostalDto extends AuditoriaDto implements Serializable {
	
	private Long id;
	@EqualsAndHashCode.Include
	private String dir3codi;
	@EqualsAndHashCode.Include
	private String contracteNum;
	private Date contracteDataVig;
	private String facturacioClientCodi;
	private Long entitatId;
	private Long organGestorId;
	private String organGestorCodi;
	private OrganGestorEstatEnum organGestorEstat;


	private static final long serialVersionUID = 6875716151909763392L;

}
