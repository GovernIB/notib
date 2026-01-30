package es.caib.notib.plugin.cie;

import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EnviamentCie extends NotificacioDtoV2 {
//
    private String identificador;
//    private TipusRemesa tipus;
//    private ProveidorCie proveidor;
//    private DestinatariCie destinatari;
//    private DocumentCie document;
    private boolean codiDir3Entitat;
    private CieDto entregaCie;
    private OperadorPostalDto operadorPostal;
    private byte[] contingutDocument;

	@Override
	public String toString() {
		return "[identificador: " + identificador + " - codiDir3Entitat:" + codiDir3Entitat
			+ " - entregaCie: " + entregaCie + " - operadorPostal: " + operadorPostal + " - mida del document " + contingutDocument.length+ "]";
	}
}
