package es.caib.notib.logic.intf.dto.adviser;

import lombok.Getter;

public enum ResultatEnviamentEnum {
        OK ("000", "000", "OK"),
        ERROR_ORGANISME("001", "001", "Organismo Desconocido"),
        ERROR_IDENTIFICADOR("002", "3002", "Identificador no encontrado"),
        ESTAT_DESCONEGUT("003", "003", "Estado inexistente"),
        ERROR_ACUSE("004", "3004", "Acuse no trobat"),
        ERROR_DESCONEGUT("666",  "666", "Error procesando peticion");

        @Getter
        private String codi;
        @Getter private String codiNexea;
        @Getter private String desc;

        ResultatEnviamentEnum(final String codi, final String codiNexea, final String desc) {

            this.codi = codi;
            this.codiNexea = codiNexea;
            this.desc = desc;
        }

        public static ResultatEnviamentEnum getByCodi(String codi) {

            for (var val : ResultatEnviamentEnum.values()) {
                if (val.getCodi().equals(codi)) {
                    return val;
                }
            }
            throw new IllegalArgumentException("ResultatEnviamentEnum no conte el codi " + codi);
        }
    }