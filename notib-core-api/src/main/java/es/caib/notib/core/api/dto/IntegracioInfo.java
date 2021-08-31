package es.caib.notib.core.api.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class IntegracioInfo {

	String codi;
	String descripcio;
	IntegracioAccioTipusEnumDto tipus;
	Long tempsInici;
	List<AccioParam> params = new ArrayList<AccioParam>();
	
	public IntegracioInfo(
			String codi,
			String descripcio, 
			IntegracioAccioTipusEnumDto tipus,
			AccioParam... params) {
		super();
		this.tempsInici = System.currentTimeMillis();
		this.codi = codi;
		this.descripcio = descripcio;
		this.tipus = tipus;
		for (AccioParam param: params)
			this.params.add(param);
	}

	public void addParam(String key, String value) {
		this.params.add(new AccioParam(key, value));
	}

	public Long getTempsResposta() {
		return System.currentTimeMillis() - tempsInici;
	}
}
