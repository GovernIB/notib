package es.caib.notib.logic.statemachine;

public class SmConstants {

    public SmConstants() {
        throw new RuntimeException("Classe no instanciable");
    }

    public static final String JMS_FACTORY_ACK = "jmsFactory";

    // HEADER
    public static final String ENVIAMENT_UUID_HEADER = "h_enviament_uuid";

    // EXTENDED
    public static final String ENVIAMENT_TIPUS = "ex_tipus";
    public static final String ENVIAMENT_DELAY = "enviament_delay";
    public static final Long MASSIU_DELAY = 5000L;
    public static final String ENVIAMENT_SENSE_NIF = "ex_sense_nif";
    public static final String ENVIAMENT_ESTAT_FINAL = "ex_final";
//    public static final String ADVISER_ACTIU = "ex_adviser";
//    public static final String CALLBACK_SIR_ACTIU = "ex_callback_sir";
    public static final String CONSULTA_POOLING = "ex_pooling";
    public static final String ENVIAMENT_REINTENTS = "ex_reintents";
    public static final String CONSULTA_SIR_POOLING_DATA_INICI = "ex_sir_pooling_data_inici";
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

    public static final String RG_RETRY = "rg_retry";
    public static final String NT_RETRY = "nt_retry";

    public static Long INTENT2;
    public static Long INTENT3;
    public static Long INTENT4;

    public static Long delay(int reintent, Long delay) {
        return Math.max(delay(reintent), delay);
    }

    public static Long delay(int reintent) {

        switch (reintent) {
            case 0: return 0L;// Inmediat
            case 1: return INTENT2 != null ? INTENT2 : 1800000L;    // Per defecte delay de 30min
            case 2: return INTENT3 != null ? INTENT3 : 28800000L;   // Per defecte delay de 8h
            default: return INTENT4 != null ? INTENT4 : 86400000L;  // Per defecte delay de 24h
        }
    }

}