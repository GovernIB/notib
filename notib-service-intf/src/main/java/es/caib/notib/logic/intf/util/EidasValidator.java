package es.caib.notib.logic.intf.util;

import com.google.common.base.Strings;

public class EidasValidator {

    public static boolean validateEidas(String eidas) {

        var split = eidas.split("/");
        return split.length == 3 && validarPais(split[0]) && validarPais(split[1]);
    }

    private static boolean validarPais(String pais) {
    //https://administracionelectronica.gob.es/pae_Home/pae_Estrategias/pae_Identidad_y_firmaelectronica/Nodo-eIDAS/Sistemas-de-identificacion-electronica-notificados.html
        return !Strings.isNullOrEmpty(pais) && pais.length() == 2;
    }

    public static boolean isFormatEidas(String eidas) {
        return eidas.split("/").length == 3;
    }
}
