package es.caib.notib.logic.statemachine;

import liquibase.pro.packaged.L;

public class SmConstants {

    public SmConstants() {
        throw new RuntimeException("Classe no instanciable");
    }

    public static final String JMS_FACTORY_ACK = "jmsFactory";

    // HEADER
    public static final String ENVIAMENT_UUID_HEADER = "h_enviament_uuid";

    // EXTENDED
    public static final String ENVIAMENT_TIPUS = "ex_tipus";
    public static final String ENVIAMENT_SENSE_NIF = "ex_sense_nif";
    public static final String ENVIAMENT_ESTAT_FINAL = "ex_final";
//    public static final String ADVISER_ACTIU = "ex_adviser";
//    public static final String CALLBACK_SIR_ACTIU = "ex_callback_sir";
    public static final String CONSULTA_POOLING = "ex_pooling";
    public static final String ENVIAMENT_REINTENTS = "ex_reintents";
    // Número màxim de reintents per acció
    public static final String RG_MAX_REINTENTS = "ex_rg_max_reintents";
    public static final String NT_MAX_REINTENTS = "ex_nt_max_reintents";
    public static final String SR_MAX_REINTENTS = "ex_sr_max_reintents";
    public static final String EM_MAX_REINTENTS = "ex_em_max_reintents";
    public static final String CN_MAX_REINTENTS = "ex_cn_max_reintents";

    // QUEUE
    public static final String CUA_REGISTRE = "qu_registre";
    public static final String CUA_NOTIFICA = "qu_notifica";
    public static final String CUA_EMAIL = "qu_email";
    public static final String CUA_CONSULTA_ESTAT = "qu_estat";
    public static final String CUA_POOLING_ESTAT = "qu_pool_estat";
    public static final String CUA_CONSULTA_SIR = "qu_sir";
    public static final String CUA_POOLING_SIR = "qu_pool_sir";

    // Delay
    // 1er reintent 30min, 2on reintet 8h, successius 24h
    public static Long delay(int reintent) {
        switch (reintent) {
            case 0: return 0L;
            case 1: return 500L;
            case 2: return 10000L;
            default: return 86400000L;
        }
//        switch (reintent) {
//            case 0: return 0L;          // Inmediat
//            case 1: return 1800000L;    // Delay de 30min
//            case 2: return 28800000L;   // Delay de 8h
//            default: return 86400000L;  // Delay de 24h
//        }
    }

}
