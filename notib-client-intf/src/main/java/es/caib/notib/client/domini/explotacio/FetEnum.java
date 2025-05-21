package es.caib.notib.client.domini.explotacio;

public enum FetEnum {
    PND ("Pendent", "La comunicació/notificació està pendent de ser registrada"),
    REG_ERR ("Error enviant a registre", "S'ha produït un error al intentar registrar la comunicació/notificació"),
    REG ("Registrada", "La comunicació/notificació ha estat registrada i està pendent de ser enviada al destinatari"),
    SIR_ACC ("Registre SIR acceptat", "La comunicació SIR ha estat acceptada per l'administració destinatària"),
    SIR_REB ("Registre SIR rebutjat", "La comunicació SIR ha estat rebutjada per l'administració destinatària"),
    NOT_ERR ("Error enviant a Notific@", "S'ha produït un error al intentar enviar la comunicació/notificació al destinatari mitjançant Notific@"),
    NOT_ENV ("Enviada", "La comunicació/notificació ha estat enviada a Notific@"),
    NOT_ACC ("Acceptada", "La comunicació/notificació ha estat acceptada pel destinatari al DEHú"),
    NOT_REB ("Rebutjada", "La comunicació/notificació ha estat rebutjada pel destinatari al DEHú"),
    NOT_EXP ("Expirada", "La comunicació/notificació ha expirat sense ser acceptada al DEHú"),
//    NOT_FAL ("Error a Notific@", "S'ha produït un error en el procés de notificació a Notific@"),
    CIE_ERR ("Error enviant a CIE", "S'ha produït un error al intentar enviar la comunicació/notificació a CIE"),
    CIE_ENV ("Enviada a CIE", "La comunicació/notificació ha estat enviada a CIE"),
    CIE_ACC ("Acceptada CIE", "La comunicació/notificació ha estat acceptada pel destinatari a CIE"),
    CIE_REB ("Rebutjada CIE", "La comunicació/notificació ha estat rebutjada pel destinatari a CIE"),
    CIE_FAL ("Error CIE", "S'ha produït un error en el procés de notificació a CIE"),
    PRC ("Processada", "La comunicació/notificació ha estat processada completament"),

    // Transicions
    TR_CRE("Crear", "Transició a 'Pendent'"),
    TR_REG_ERR("Error registrar", "Transició a 'Error enviant a registre'"),
    TR_REG("Registrar", "Transició a 'Registrada'"),
    TR_SIR_ACC("SIR Acceptar", "Transició a 'Registre SIR acceptat'"),
    TR_SIR_REB("SIR Rebutjar", "Transició a 'Registre SIR rebutjat"),
    TR_NOT_ERR("Error enviar", "Transició a 'Error enviant a Notific@'"),
    TR_NOT_ENV("Enviar", "Transició a 'Enviada'"),
    TR_NOT_ACC("Acceptar", "Transició a 'Acceptada'"),
    TR_NOT_REB("Rebutjar", "Transició a 'Rebutjada'"),
    TR_NOT_EXP("Expirar", "Transició a 'Expirada'"),
    TR_NOT_FAL("Fallar", "Transició a 'Error a Notific@'"),
    TR_CIE_ERR("Error enviar CIE", "Transició a 'Error enviant a CIE'"),
    TR_CIE_ENV("Enviar CIE", "Transició a 'Enviada a CIE'"),
    TR_CIE_ACC("Acceptar CIE", "Transició a 'Acceptada CIE'"),
    TR_CIE_REB("Rebutjar CIE", "Transició a 'Rebutjada CIE'"),
    TR_CIE_CAN("Cancel·lar CIE", "Transició a 'Cancel·lada CIE'"),
    TR_CIE_FAL("Fallar CIE", "Transició a 'Error CIE'"),
    TR_EML_ERR("Error eviar email", "Transició a 'Error enviant per email'"),
    TR_EML_ENV("Enviar email", "Transició a 'Enviada per email'"),

    // Temps mig en estat
    TMP_PND ("Temps Pendent", "Temps mig entre pendent i registre"),
    TMP_REG ("Temps Registre", "Temps mig entre registre i enviat"),
    TMP_NOT ("Temps Notific@", "Temps mig entre enviat i finalitzat a Notific@"),
    TMP_CIE ("Temps Cie", "Temps mig entre enviat i finalitzat a CIE"),
    TMP_TOT ("Temps Total", "Temps mig desde la creació a la finalització"),
    TMP_REG_SAC("Temps Registre-SIR Acceptat", "Temps mig entre registre i acceptació SIR"),
    TMP_REG_SRB("Temps Registre-SIR Rebutjat", "Temps mig entre registre i rebuig SIR"),
    TMP_REG_NOT("Temps Registre-Notific@", "Temps mig entre registre i enviament Notific@"),
    TMP_REG_EML("Temps Registre-Email", "Temps mig entre registre i enviament email"),
    TMP_NOT_NOT("Temps Notific@-Notificat", "Temps mig entre enviament i notificació Notific@"),
    TMP_NOT_REB("Temps Notific@-Rebutjat", "Temps mig entre enviament i rebuig Notific@"),
    TMP_NOT_EXP("Temps Notific@-Expirat", "Temps mig entre enviament i expiració Notific@"),
    TMP_NOT_FAL("Temps Notific@-Fallada", "Temps mig entre enviament i fallada Notific@"),
    TMP_CIE_NOT("Temps CIE-Notificat", "Temps mig entre enviament i notificació CIE"),
    TMP_CIE_REB("Temps CIE-Rebutjat", "Temps mig entre enviament i rebuig CIE"),
    TMP_CIE_CAN("Temps CIE-Cancel·lat", "Temps mig entre enviament i cancel·lació CIE"),
    TMP_CIE_FAL("Temps CIE-Fallada", "Temps mig entre enviament i fallada CIE"),
    TMP_TOT_NAC("Temps Total-Notific@ Acceptat", "Temps mig des de la creació fins acceptació Notific@"),
    TMP_TOT_NRB("Temps Total-Notific@ Rebutjat", "Temps mig des de la creació fins rebuig Notific@"),
    TMP_TOT_NEX("Temps Total-Notific@ Expirat", "Temps mig des de la creació fins expiració Notific@"),
    TMP_TOT_NFL("Temps Total-Notific@ Fallada", "Temps mig des de la creació fins fallada Notific@"),
    TMP_TOT_CAC("Temps Total-CIE Acceptat", "Temps mig des de la creació fins acceptació CIE"),

    // Intents
    INT_REG("Intents Registre", "Nombre mig d'intents de registre"),
    INT_SIR("Intents SIR", "Nombre mig d'intents d'enviament SIR"),
    INT_NOT("Intents Notific@", "Nombre mig d'intents d'enviament Notific@"),
    INT_CIE("Intents CIE", "Nombre mig d'intents d'enviament CIE"),
    INT_EML("Intents Email", "Nombre mig d'intents d'enviament per email");

    private String nom;
    private String descripcio;
    
    FetEnum(String nom, String descripcio) {
        this.nom = nom;
        this.descripcio = descripcio;
    }
    
    public String getNom() {
        return nom;
    }
    public String getDescripcio() {
        return descripcio;
    }

}
