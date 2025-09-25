package es.caib.notib.plugin.digitalitzacio;

import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioPerfil;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioResultat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioTransaccioResposta;
import es.caib.notib.plugin.SalutPlugin;
import es.caib.notib.plugin.SistemaExternException;

import java.util.List;

public interface DigitalitzacioPlugin extends SalutPlugin {

    List<DigitalitzacioPerfil> recuperarPerfilsDisponibles(String idioma) throws SistemaExternException;

    DigitalitzacioTransaccioResposta iniciarProces(String codiPerfil, String idioma, UsuariDto funcionari, String urlReturn) throws SistemaExternException;

    DigitalitzacioResultat recuperarResultat(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) throws SistemaExternException;

    void tancarTransaccio(String idTransaccio) throws SistemaExternException;
}
