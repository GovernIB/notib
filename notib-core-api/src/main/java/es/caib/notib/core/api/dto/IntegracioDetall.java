package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IntegracioDetall {

	private Date data;
	private String descripcio;
	private IntegracioAccioTipusEnumDto tipus;
	private IntegracioAccioEstatEnumDto estat;
    private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;
	private List<AccioParam> parametres;
}
