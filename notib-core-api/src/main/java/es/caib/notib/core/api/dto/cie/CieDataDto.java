package es.caib.notib.core.api.dto.cie;

import es.caib.notib.core.api.dto.AuditoriaDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class CieDataDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String nom;
	@EqualsAndHashCode.Include
	private String organismePagadorCodi;
	@EqualsAndHashCode.Include
	private Date contracteDataVig;
	private Long organGestorId;

	private static final long serialVersionUID = 1305599728317046741L;

}