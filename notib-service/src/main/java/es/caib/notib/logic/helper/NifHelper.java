package es.caib.notib.logic.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class NifHelper {

    private static final String LLETRES_NIF = "TRWAGMYFPDXBNJZSQVHLCKE";
    private static final String LLETRES_CIF = "ABCDEFGHJKLMNPQRSUVW";
    private static final String LLETRES_NIE = "XYZLM";
    private static final String DIGIT_CONTRTOL_CIF = "JABCDEFGHI";
    private static final String LLETRA_CIF = "KPQRSNW";

    public static boolean isvalid(String nif) {

        if (nif == null || nif.length() < 9) {
            return false;
        }
        nif = nif.toUpperCase();
        String primerCaracter = nif.substring(0, 1);
        boolean totNumeros = NumberUtils.isNumber(StringUtils.stripStart(nif.substring(1,nif.length()-1), "0"));
        boolean valid = false;
        if (LLETRES_CIF.contains(primerCaracter) && totNumeros) {
            valid = isCifValid(nif);
            if (valid) {
                return true;
            }
        }
        if (LLETRES_NIE.contains(primerCaracter) && totNumeros) {
            return isNieValid(nif);
        }
        return nif.substring(0,8).matches("-?\\d+") && isDniValid(nif);
    }

    public static boolean isValidNifNie(String nif) {

        if (nif == null || nif.length() < 9) {
            return false;
        }
        nif = nif.toUpperCase();
        String primerCaracter = nif.substring(0, 1);

//        if (LLETRES_CIF.contains(primerCaracter)) {
//            return false;
//        }
        if (LLETRES_NIE.contains(primerCaracter)) {
            return isNieValid(nif);
        }
        return nif.substring(0,8).matches("-?\\d+") && isDniValid(nif);
    }

    public static boolean isValidCif(String nif) {

        if (nif == null || nif.length() < 9) {
            return false;
        }
        nif = nif.toUpperCase();
        String primerCaracter = nif.substring(0, 1);
        return LLETRES_CIF.contains(primerCaracter) ? isCifValid(nif) : false;
    }

    private static boolean isCifValid(String cif) {

        String aux = cif.substring(0, 8);
        aux = calculaCif(aux);
        return cif.equals(aux);
    }

    private static boolean isNieValid(String nie) {

        String aux = nie.substring(0, 8);
        aux = calculaNie(aux);
        return nie.equals(aux);
    }

    private static boolean isDniValid(String dni) {

        String aux = dni.substring(0, 8);
        aux = calculaDni(aux);
        return dni.equals(aux);
    }

    private static String calculaCif(String cif) {

        return cif + calculaDigitControl(cif);
    }

    private static String calculaDigitControl(String cif) {

        String str = cif.substring(1, 8);
        String cabecera = cif.substring(0, 1);
        int sumaPar = 0;
        int sumaImpar = 0;
        int sumaTotal;

        for (int i = 1; i < str.length(); i += 2) {
            int aux = Integer.parseInt("" + str.charAt(i));
            sumaPar += aux;
        }

        for (int i = 0; i < str.length(); i += 2) {
            sumaImpar += posicioSenar("" + str.charAt(i));
        }

        sumaTotal = sumaPar + sumaImpar;
        sumaTotal = 10 - (sumaTotal % 10);
        if(sumaTotal==10){
            sumaTotal=0;
        }

        str = LLETRA_CIF.contains(cabecera) ? "" + DIGIT_CONTRTOL_CIF.charAt(sumaTotal) : "" + sumaTotal;
        return str;
    }

    private static int posicioSenar(String str) {

        int aux = Integer.parseInt(str);
        aux = aux * 2;
        aux = (aux / 10) + (aux % 10);
        return aux;
    }

    private static String calculaNie(String nie) {

        String str = null;
        if(nie.length()==9){
            nie=nie.substring(0, nie.length()-1);
        }
        if (nie.startsWith("X")) {
            str = nie.replace('X', '0');
        } else if (nie.startsWith("Y")) {
            str = nie.replace('Y', '1');
        } else if (nie.startsWith("Z")) {
            str = nie.replace('Z', '2');
        } else if (nie.startsWith("M")) {
            str = nie.replace('M', '0');
        } else if (nie.startsWith("L")) {
            str = nie.replace('L', '0');
        }
        return nie + calculaLletra(str);
    }

    private static char calculaLletra(String aux) {
        return LLETRES_NIF.charAt(Integer.parseInt(aux) % 23);
    }

    private static String calculaDni(String dni) {

        String str = completaZeros(dni, 8);
        if(str.length()==9){
            str=str.substring(0,dni.length()-1);
        }
        return str + calculaLletra(str);
    }

    private static String completaZeros(String str, int num) {

        while (str.length() < num) {
            str = "0" + str;
        }
        return str;
    }
}
