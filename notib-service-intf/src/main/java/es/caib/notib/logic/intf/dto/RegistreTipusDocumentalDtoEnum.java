package es.caib.notib.logic.intf.dto;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RegistreTipusDocumentalDtoEnum implements Serializable{

	// DOCUMENTS DE DECISSIO
		RESSOLUCIO("TD01"),
		ACORD("TD02"),
		CONTRACTE("TD03"),
		CONVENI("TD04"),
		DECLARACIO("TD05"),
		// DOCUMENTS DE TRANSMISSIO
		COMINICACIO("TD06"),
		NOTIFICACIO("TD07"),
		PUBLICACIO("TD08"),
		ACUS_DE_REBUT("TD09"),
		// DOCUMENTS DE CONSTANCIA
		ACTA("TD10"),
		CERTIFICAT("TD11"),
		DILIGENCIA("TD12"),
		// DOCUMENTS DE JUDICI
		INFORME("TD13"),
		// DOCUMENTS DE CIUTADA
		SOLICITUD("TD14"),
		DENUNCIA("TD15"),
		ALEGACIO("TD16"),
		RECURSOS("TD17"),
		COMUNICACIO_CIUTADA("TD18"),
		FACTURA("TD19"),
		ALTRES_INCAUTATS("TD20"),
		// ALTRES
		ALTRES("TD99"),
		
		//NOUS TIPUS
		LLEI("TD51"),
		MOCIO("TD52"),
		INSTRUCCIO("TD53"),
		CONVOCATORIA("TD54"),
		ORDRE_DIA("TD55"),
		INFORME_PONENCIA("TD56"),
		DICTAMEN_COMISSIO("TD57"),
		INICIATIVA_LEGISLATIVA("TD58"),
		PREGUNTA("TD59"),
		INTERPELACIO("TD60"),
		RESPOSTA("TD61"),
		PROPOSICIO_NO_LLEI("TD62"),
		ESQUEMA("TD63"),
		PROPOSTA_RESOLUCIO("TD64"),
		COMPAREIXENSA("TD65"),
		SOLICITUD_INFORMACIO("TD66"),
		ESCRIT("TD67"),
		INICIATIVA_LEGISLATIVA2("TD68"),
		PETICIO("TD69");
		
		private final String valor;
		private RegistreTipusDocumentalDtoEnum(String valor) {
			this.valor = valor;
		}
		public String getValor() {
			return valor;
		}
		private static final Map<String, RegistreTipusDocumentalDtoEnum> lookup;
		static {
			lookup = new HashMap<String, RegistreTipusDocumentalDtoEnum>();
			for (RegistreTipusDocumentalDtoEnum s: EnumSet.allOf(RegistreTipusDocumentalDtoEnum.class))
				lookup.put(s.getValor(), s);
		}
		public static RegistreTipusDocumentalDtoEnum valorAsEnum(String valor) {
			if (valor == null)
				return null;
	        return lookup.get(valor); 
	    }

	private static final long serialVersionUID = 1488827482782270649L;
}
