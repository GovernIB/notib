package es.caib.notib.logic.intf.dto.cie;

import es.caib.notib.logic.intf.dto.AuditoriaDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class OperadorPostalDataDto extends AuditoriaDto implements Serializable {

	private Long id;
	@EqualsAndHashCode.Include
	private String nom;
	private String organismePagadorCodi;
	@EqualsAndHashCode.Include
	private String contracteNum;
	private Date contracteDataVig;
	private String facturacioClientCodi;
	private Long organGestorId;
}
