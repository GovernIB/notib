package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioPerfil;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioResultat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioTransaccioResposta;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface DigitalitzacioService {

    /**
     * Recupera els perfils disponibles per un usuari d'aplicació.
     *
     * @return La llista dels perfils.
     */
    @PreAuthorize("isAuthenticated()")
    List<DigitalitzacioPerfil> getPerfilsDisponibles();

    /**
     * Inicia el procés de digitalització mostrant el fomulari per escanejar documents.
     *
     * @param codiPerfil El codi del perfil que s'ha seleccionat per iniciar l'escaneig.
     * @param urlReturn Url on es retornarà la cridada de Portafib.
     * @return Resposta de DigutalIB amb el id de la transacció.
     */
    @PreAuthorize("isAuthenticated()")
    DigitalitzacioTransaccioResposta iniciarDigitalitzacio(String codiPerfil, String urlReturn);

    /**
     * Recupera el resultat d'un escaneig.
     *
     * @param idTransaccio Id de la transacció de la qual es vol recuperar el resultat.
     * @param returnScannedFile Indica si s'ha escanejat un document sense firma.
     * @param returnSignedFile Indica si s'ha escanejat un document amb firma.
     * @return L'estat i el document escanejat.
     */
    @PreAuthorize("isAuthenticated()")
    DigitalitzacioResultat recuperarResultat(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile);

    /**
     * Tanca un transacció.
     *
     * @param idTransaccio Id de la transacció.
     */
    @PreAuthorize("isAuthenticated()")
    void tancarTransaccio(String idTransaccio);
}
