package es.caib.notib.logic.intf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IntegracioInfo {

	IntegracioCodi codi;
	String codiEntitat;
	String descripcio;
	String aplicacio;
	Long notificacioId;
	IntegracioAccioTipusEnumDto tipus;
	Long tempsInici;
	List<AccioParam> params = new ArrayList<>();

	public static final String INTERFICIE_WEB = "Interficie web";
	
	public IntegracioInfo(IntegracioCodi codi, String descripcio, IntegracioAccioTipusEnumDto tipus, AccioParam... params) {

		super();
		this.tempsInici = System.currentTimeMillis();
		this.codi = codi;
		this.descripcio = descripcio;
		this.tipus = tipus;
		for (AccioParam param: params) {
			this.params.add(param);
		}
//		if ("CALLBACK".equals(this.codi)) {
//			this.aplicacio = params.length == 4 ? params[2].getValor() : params.length == 3 ? params[2].getValor() : null;
//		}
	}

	public void addParam(String key, String value) {
		this.params.add(new AccioParam(key, value));
	}

	public Long getTempsResposta() {
		return System.currentTimeMillis() - tempsInici;
	}

	public void setAplicacio(String aplicacio) {
		this.aplicacio = aplicacio;
	}

	public void setAplicacio(TipusUsuariEnumDto tipus, String usuariCodi) {

		this.aplicacio = TipusUsuariEnumDto.APLICACIO.equals(tipus)  ? usuariCodi : INTERFICIE_WEB;
	}
}
