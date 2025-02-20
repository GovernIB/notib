package es.caib.notib.logic.intf.dto.cie;

import es.caib.notib.client.domini.EntregaPostalVia;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregaPostalDto implements Serializable{

	private NotificaDomiciliConcretTipus domiciliConcretTipus;
	private EntregaPostalVia viaTipus;
	private String viaNom;
	private String numeroCasa;
	private String numeroQualificador;
	private String puntKm;
	private String apartatCorreus;
	private String portal;
	private String escala;
	private String planta;
	private String porta;
	private String bloc;
	private String complement;
	private String codiPostal;
	private String codiPostalNorm;
	private String poblacio;
	private String municipiCodi;
	private String provincia;
	private String paisCodi;
	private String linea1;
	private String linea2;
	private String formatSobre;
	private String formatFulla;
	private Integer cie;
	private String cieId;
	private boolean cieCancelat;
	private String cieEstat;
	private String cieErrorDesc;
	private String cieDatatReceptorNif;
	private String cieDatatReceptorNom;
	private Date cieCertificacioData;
	private String cieCertificacioArxiuId;
	private String cieCertificacioArxiuNom;
	private String cieCertificacioHash;
	private String cieCertificacioOrigen;
	private String cieCertificacioMetadades;
	private String cieCertificacioCsv;
	private String cieCertificacioMime;
	private Integer cieCertificacioTamany;
	private CieCertificacioTipus cieCertificacioTipus;
	private CieCertificacioArxiuTipus cieCertificacioArxiuTipus;
	private String cieCertificacioNumSeguiment;
	private Date cieEstatData;
	private String cieEstatDescripcio;
	private String cieDatatOrigen;
	private String cieDatatNumSeguiment;
	private String cieDatatErrorDescripcio;
	private Date cieEstatDataActualitzacio;
}
