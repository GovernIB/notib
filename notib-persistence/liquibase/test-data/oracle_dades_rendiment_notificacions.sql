-- =====================================================================================================
-- NOTIB - Generació de dades de prova per a test de rendiment (ORACLE)
-- =====================================================================================================
--
-- Crea N notificacions completes, amb tot el que en depèn:
--   NOT_DOCUMENT, NOT_NOTIFICACIO, NOT_PERSONA (titular + destinatari opcional), NOT_NOTIFICACIO_EVENT,
--   NOT_NOTIFICACIO_ENV, STATE_MACHINE, NOT_NOTIFICACIO_ENV_TABLE i NOT_NOTIFICACIO_TABLE.
--
-- Les notificacions es reparteixen entre diferents escenaris (estat de la notificació + estat dels
-- enviaments + estat de la state machine + events coherents). Totes queden amb PER_ACTUALITZAR = 1 i
-- ESTAT_STRING buit, de manera que l'aplicació recalcula la columna d'estat en llegir-les.
--
-- Totes les notificacions generades tenen el concepte començant per 'PERF ' per poder-les identificar
-- i eliminar amb l'script oracle_dades_rendiment_notificacions_neteja.sql.
--
-- Ús (sqlplus / SQLcl / SQL Developer com a "Run Script"):
--   1. Ajustar els paràmetres del bloc "Paràmetres".
--   2. Executar l'script amb l'usuari propietari de l'esquema NOTIB. El fitxer és UTF-8: amb sqlplus
--      cal tenir NLS_LANG=.AL32UTF8 perquè els accents es desin correctament, p.ex.:
--        NLS_LANG=.AL32UTF8 sqlplus notib/notib@//localhost:1521/FREEPDB1 @oracle_dades_rendiment_notificacions.sql
--
-- Escenaris (cada notificació té 1, 2 o 3 enviaments; les comunicacions SIR sempre 1):
--   A  PENDENT                 enviament NOTIB_PENDENT            SM REGISTRE_PENDENT
--   B  PENDENT (error reg.)    enviament NOTIB_PENDENT            SM REGISTRE_ERROR    event registre amb error
--   C  REGISTRADA              enviament REGISTRADA               SM NOTIFICA_PENDENT
--   D  ENVIADA                 enviament PENDENT_SEU/ENVIADA_DEH  SM NOTIFICA_SENT
--   E  ENVIADA_AMB_ERRORS      enviament REGISTRADA               SM NOTIFICA_ERROR    event notifica amb error
--   F  ENVIADA (error cons.)   enviament PENDENT_SEU              SM CONSULTA_ERROR    event consulta amb error
--   G  FINALITZADA             enviament NOTIFICADA               SM FI                datat + certificació
--   H  FINALITZADA             enviament REBUTJADA                SM FI
--   I  FINALITZADA             enviament EXPIRADA                 SM FI
--   J  FINALITZADA_AMB_ERRORS  enviament ABSENT/ADRESA_INC./...   SM FI
--   K  PROCESSADA              enviament NOTIFICADA               SM FI
--   L  FINALITZADA (comunic.)  enviament LLEGIDA                  SM FI
--   M  ENVIAT_SIR              enviament ENVIAT_SIR               SM SIR_PENDENT
--   N  FINALITZADA (SIR acc.)  enviament ENVIAT_SIR / OFICI_ACC.  SM FI
--   O  ANULADA                 enviament ANULADA                  SM FI
--   P  PENDENT (programada)    enviament ENVIAMENT_PROGRAMAT      SM NOU
--
-- ATENCIÓ: els enviaments en estats no finals (A, B, C, D, F, M, P) poden ser agafats pels processos
-- periòdics de l'aplicació (registre, enviament a Notifica, consulta d'estat, SIR...). En un entorn
-- connectat a serveis reals, convé desactivar aquests processos abans d'arrencar.
-- =====================================================================================================

SET SERVEROUTPUT ON SIZE UNLIMITED
SET TIMING ON

DECLARE
    -- ---------------------------------------------------------------------------------------------
    -- Paràmetres
    -- ---------------------------------------------------------------------------------------------
    c_num_notificacions CONSTANT PLS_INTEGER   := 10000;       -- Nombre de notificacions a crear
    c_entitat_codi      CONSTANT VARCHAR2(64)  := 'CAIB';      -- Codi de l'entitat (NOT_ENTITAT.CODI)
    c_usuari            CONSTANT VARCHAR2(64)  := 'not_user';  -- Ha d'existir a NOT_USUARI
    c_dies_enrere       CONSTANT PLS_INTEGER   := 365;         -- Rang de dates de creació (dies enrere)
    c_commit_cada       CONSTANT PLS_INTEGER   := 500;         -- Commit cada N notificacions

    -- ---------------------------------------------------------------------------------------------
    -- Ordinals dels enums (JPA @Enumerated ORDINAL)
    -- ---------------------------------------------------------------------------------------------
    -- NotificacioEstatEnumDto
    NE_PENDENT                CONSTANT PLS_INTEGER := 0;
    NE_ENVIADA                CONSTANT PLS_INTEGER := 1;
    NE_REGISTRADA             CONSTANT PLS_INTEGER := 2;
    NE_FINALITZADA            CONSTANT PLS_INTEGER := 3;
    NE_PROCESSADA             CONSTANT PLS_INTEGER := 4;
    NE_ENVIAT_SIR             CONSTANT PLS_INTEGER := 8;
    NE_ENVIADA_AMB_ERRORS     CONSTANT PLS_INTEGER := 9;
    NE_FINALITZADA_AMB_ERRORS CONSTANT PLS_INTEGER := 10;
    NE_OFICI_ACCEPTAT         CONSTANT PLS_INTEGER := 12;
    NE_ANULADA                CONSTANT PLS_INTEGER := 14;
    -- EnviamentEstat
    EE_NOTIB_PENDENT       CONSTANT PLS_INTEGER := 0;
    EE_ABSENT              CONSTANT PLS_INTEGER := 2;
    EE_ADRESA_INCORRECTA   CONSTANT PLS_INTEGER := 3;
    EE_DESCONEGUT          CONSTANT PLS_INTEGER := 4;
    EE_ENVIADA_DEH         CONSTANT PLS_INTEGER := 6;
    EE_ENVIAMENT_PROGRAMAT CONSTANT PLS_INTEGER := 7;
    EE_EXPIRADA            CONSTANT PLS_INTEGER := 10;
    EE_MORT                CONSTANT PLS_INTEGER := 12;
    EE_LLEGIDA             CONSTANT PLS_INTEGER := 13;
    EE_NOTIFICADA          CONSTANT PLS_INTEGER := 14;
    EE_PENDENT_SEU         CONSTANT PLS_INTEGER := 17;
    EE_REBUTJADA           CONSTANT PLS_INTEGER := 20;
    EE_REGISTRADA          CONSTANT PLS_INTEGER := 24;
    EE_ANULADA             CONSTANT PLS_INTEGER := 26;
    EE_ENVIAT_SIR          CONSTANT PLS_INTEGER := 27;
    -- EnviamentTipus
    ET_NOTIFICACIO CONSTANT PLS_INTEGER := 0;
    ET_COMUNICACIO CONSTANT PLS_INTEGER := 1;
    ET_SIR         CONSTANT PLS_INTEGER := 2;
    -- NotificacioEventTipusEnumDto
    EV_REGISTRE_ENVIAMENT   CONSTANT PLS_INTEGER := 0;
    EV_SIR_CONSULTA         CONSTANT PLS_INTEGER := 2;
    EV_NOTIFICA_ENVIAMENT   CONSTANT PLS_INTEGER := 3;
    EV_NOTIFICA_CONSULTA    CONSTANT PLS_INTEGER := 4;
    EV_ADVISER_CERTIFICACIO CONSTANT PLS_INTEGER := 5;
    EV_ADVISER_DATAT        CONSTANT PLS_INTEGER := 6;
    EV_NOTIFICA_ANULAR      CONSTANT PLS_INTEGER := 20;
    -- NotificacioRegistreEstatEnumDto
    RE_VALID          CONSTANT PLS_INTEGER := 0;
    RE_OFICI_ACCEPTAT CONSTANT PLS_INTEGER := 5;
    RE_OFICI_SIR      CONSTANT PLS_INTEGER := 12;
    -- EnviamentSmEstat
    SM_NOU              CONSTANT PLS_INTEGER := 0;
    SM_REGISTRE_PENDENT CONSTANT PLS_INTEGER := 1;
    SM_REGISTRE_ERROR   CONSTANT PLS_INTEGER := 3;
    SM_NOTIFICA_PENDENT CONSTANT PLS_INTEGER := 8;
    SM_NOTIFICA_ERROR   CONSTANT PLS_INTEGER := 10;
    SM_NOTIFICA_SENT    CONSTANT PLS_INTEGER := 11;
    SM_CONSULTA_ERROR   CONSTANT PLS_INTEGER := 13;
    SM_SIR_PENDENT      CONSTANT PLS_INTEGER := 15;
    SM_FI               CONSTANT PLS_INTEGER := 19;
    -- EnviamentSmEvent
    SE_RG_ENVIAR  CONSTANT PLS_INTEGER := 0;
    SE_RG_SUCCESS CONSTANT PLS_INTEGER := 1;
    SE_RG_ERROR   CONSTANT PLS_INTEGER := 4;
    SE_NT_SUCCESS CONSTANT PLS_INTEGER := 13;
    SE_NT_ERROR   CONSTANT PLS_INTEGER := 16;
    SE_CN_SUCCESS CONSTANT PLS_INTEGER := 19;
    SE_CN_ERROR   CONSTANT PLS_INTEGER := 21;
    SE_SR_SUCCESS CONSTANT PLS_INTEGER := 25;

    TYPE t_sm_noms IS TABLE OF VARCHAR2(30) INDEX BY PLS_INTEGER;
    v_sm_noms t_sm_noms;

    -- ---------------------------------------------------------------------------------------------
    -- Plantilla Kryo del context de la state machine (STATE_MACHINE.STATE_MACHINE_CONTEXT).
    -- Obtinguda d'un context real; s'hi substitueixen l'event, l'estat, el uuid de l'enviament i
    -- el tipus d'enviament. Kryo serialitza els strings ASCII amb el bit alt activat a l'últim byte.
    -- ---------------------------------------------------------------------------------------------
    c_kryo_1 CONSTANT VARCHAR2(400) := '01010065732E636169622E6E6F7469622E6C6F6769632E696E74662E73746174656D616368696E652E456E7669616D656E74536D4576656EF401';
    c_kryo_2 CONSTANT VARCHAR2(400) := '010165732E636169622E6E6F7469622E6C6F6769632E696E74662E73746174656D616368696E652E456E7669616D656E74536D45737461F401';
    c_kryo_3 CONSTANT VARCHAR2(400) := '01026F72672E737072696E676672616D65776F726B2E6D6573736167696E672E4D657373616765486561646572F30101036A6176612E7574696C2E486173684D61F001030301685F656E7669616D656E745F757569E40301';
    c_kryo_4 CONSTANT VARCHAR2(800) := '030169E401046A6176612E7574696C2E555549C4019008AAD721D0C9EB03C653A3B71F5D7F030174696D657374616DF009C2D7AEF0BB6701056F72672E737072696E676672616D65776F726B2E73746174656D616368696E652E737570706F72742E4F627365727661626C654D61F00105030172675F72657472F90500030165785F73656E73655F6E69E60500030165785F7265696E74656E74F302040301656E7669616D656E745F64656C61F90900030165785F74697075F30301';
    c_kryo_5 CONSTANT VARCHAR2(400) := '01066A6176612E7574696C2E41727261794C6973F40100010301000301';
    c_kryo_6 CONSTANT VARCHAR2(400) := '01060100';

    -- ---------------------------------------------------------------------------------------------
    -- Dades de referència
    -- ---------------------------------------------------------------------------------------------
    TYPE t_proc IS RECORD (
        id          NUMBER,
        codi        VARCHAR2(64),
        nom         VARCHAR2(1000),
        comu        NUMBER(1),
        req_perm    NUMBER(1),
        tipus       VARCHAR2(32),
        organ_id    NUMBER,
        organ_codi  VARCHAR2(64),
        organ_nom   VARCHAR2(1000),
        organ_estat NUMBER
    );
    TYPE t_procs IS TABLE OF t_proc;
    v_procs t_procs;

    TYPE t_strs IS TABLE OF VARCHAR2(100);
    v_noms      t_strs := t_strs('Maria', 'Joan', 'Antoni', 'Francesca', 'Miquel', 'Catalina', 'Josep',
                                 'Margalida', 'Bartomeu', 'Aina', 'Pere', 'Joana', 'Jaume', 'Antònia');
    v_llinatges t_strs := t_strs('Garcia', 'Ferrer', 'Pons', 'Mas', 'Vidal', 'Roig', 'Serra', 'Bauzà',
                                 'Coll', 'Oliver', 'Riera', 'Amengual', 'Sastre', 'Mir', 'Ramis', 'Font');
    v_estats_lletra CONSTANT VARCHAR2(23) := 'TRWAGMYFPDXBNJZSQVHLCKE';

    v_entitat_id   NUMBER;
    v_entitat_nom  VARCHAR2(256);
    v_entitat_dir3 VARCHAR2(9);
    v_gesdoc_id    VARCHAR2(64);
    v_sir_desti    VARCHAR2(9);
    v_sir_desti_nom VARCHAR2(255);

    -- ---------------------------------------------------------------------------------------------
    -- Escenari actual
    -- ---------------------------------------------------------------------------------------------
    TYPE t_esc IS RECORD (
        codi          VARCHAR2(1),
        env_tipus     PLS_INTEGER,
        not_estat     PLS_INTEGER,
        env_estat     PLS_INTEGER,
        env_final     PLS_INTEGER,
        sm_estat      PLS_INTEGER,
        sm_event      PLS_INTEGER,
        reg_estat     PLS_INTEGER,   -- null si no registrat
        reg_error     PLS_INTEGER,
        notifica_env  PLS_INTEGER,       -- enviat a Notifica
        notifica_err  PLS_INTEGER,       -- error en enviar a Notifica
        consulta_err  PLS_INTEGER,       -- error en consultar l'estat a Notifica
        datat         PLS_INTEGER,
        certificat    PLS_INTEGER,
        sir_acceptat  PLS_INTEGER,
        anulat        PLS_INTEGER,
        processat     PLS_INTEGER,
        programat     PLS_INTEGER
    );
    e t_esc;

    -- Variables de treball
    v_not_id      NUMBER;
    v_doc_id      NUMBER;
    v_env_id      NUMBER;
    v_tit_id      NUMBER;
    v_dest_id     NUMBER;
    v_ev_id       NUMBER;
    v_ultim_ev_id NUMBER;
    v_ultim_ev_dt DATE;
    v_err_ev_id   NUMBER;
    v_err_desc    VARCHAR2(2048);
    v_p           t_proc;
    v_num_envs    PLS_INTEGER;
    v_created     DATE;
    v_t           DATE;
    v_reg_data    DATE;
    v_env_data    DATE;
    v_datat_data  DATE;
    v_cad         DATE;
    v_prog        DATE;
    v_ref         VARCHAR2(36);
    v_env_ref     VARCHAR2(36);
    v_concepte    VARCHAR2(240);
    v_reg_num     NUMBER;
    v_reg_formatat VARCHAR2(50);
    v_notifica_id VARCHAR2(20);
    v_nom         VARCHAR2(100);
    v_ll1         VARCHAR2(40);
    v_ll2         VARCHAR2(40);
    v_nif         VARCHAR2(9);
    v_tipus_int   VARCHAR2(30);
    v_rao_social  VARCHAR2(255);
    v_cod_desti   VARCHAR2(9);
    v_tit_fmt     VARCHAR2(500);
    v_destinataris VARCHAR2(4000);
    v_env_estat   PLS_INTEGER;
    v_env_error   PLS_INTEGER;
    v_motiu_anul  VARCHAR2(250);
    v_sm_estat_nom VARCHAR2(30);
    v_dest_nom    VARCHAR2(100);
    v_dest_ll1    VARCHAR2(40);
    v_dest_ll2    VARCHAR2(40);
    v_sm_context  BLOB;
    -- Agregats per NOT_NOTIFICACIO_TABLE
    v_titulars    VARCHAR2(4000);
    v_reg_nums    VARCHAR2(4000);
    v_estat_mask  NUMBER;
    v_enviada_dt  DATE;
    v_cer_dt      DATE;
    v_tab_err_dt  DATE;
    v_tab_err_desc VARCHAR2(2048);
    v_tab_err_fi  PLS_INTEGER;
    v_anulable    PLS_INTEGER;
    v_num_errors  PLS_INTEGER;
    v_ev_count    PLS_INTEGER := 0;
    v_env_count   PLS_INTEGER := 0;

    -- ---------------------------------------------------------------------------------------------
    -- Funcions auxiliars
    -- ---------------------------------------------------------------------------------------------
    FUNCTION nou_id RETURN NUMBER IS
        v NUMBER;
    BEGIN
        SELECT NOT_HIBERNATE_SEQ.NEXTVAL INTO v FROM dual;
        RETURN v;
    END;

    FUNCTION nou_uuid RETURN VARCHAR2 IS
        v VARCHAR2(32) := LOWER(RAWTOHEX(SYS_GUID()));
    BEGIN
        RETURN SUBSTR(v, 1, 8) || '-' || SUBSTR(v, 9, 4) || '-' || SUBSTR(v, 13, 4) || '-' || SUBSTR(v, 17, 4) || '-' || SUBSTR(v, 21, 12);
    END;

    FUNCTION kryo_ascii(p VARCHAR2) RETURN VARCHAR2 IS
    BEGIN
        RETURN RAWTOHEX(UTL_RAW.CAST_TO_RAW(SUBSTR(p, 1, LENGTH(p) - 1)))
            || TO_CHAR(ASCII(SUBSTR(p, -1)) + 128, 'FM0X');
    END;

    FUNCTION kryo_context(p_uuid VARCHAR2, p_estat PLS_INTEGER, p_event PLS_INTEGER, p_tipus VARCHAR2) RETURN BLOB IS
        v_u VARCHAR2(100) := kryo_ascii(p_uuid);
    BEGIN
        RETURN TO_BLOB(HEXTORAW(
            c_kryo_1 || TO_CHAR(p_event + 1, 'FM0X') ||
            c_kryo_2 || TO_CHAR(p_estat + 1, 'FM0X') ||
            c_kryo_3 || v_u ||
            c_kryo_4 || kryo_ascii(p_tipus) ||
            c_kryo_5 || v_u ||
            c_kryo_6));
    END;

    -- Màscara de l'estat de la notificació (NotificacioEstatEnumDto.getMask() = 1 << ordinal)
    FUNCTION mask_not(p_ord PLS_INTEGER) RETURN NUMBER IS
    BEGIN
        RETURN POWER(2, p_ord);
    END;

    -- Ordinal de NotificacioEstatEnumDto amb el mateix nom que l'EnviamentEstat (o null)
    FUNCTION env_estat_a_not_estat(p_env PLS_INTEGER) RETURN PLS_INTEGER IS
    BEGIN
        RETURN CASE p_env
            WHEN 15 THEN 0    -- PENDENT
            WHEN 23 THEN 1    -- ENVIADA
            WHEN 24 THEN 2    -- REGISTRADA
            WHEN 22 THEN 3    -- FINALITZADA
            WHEN 25 THEN 4    -- PROCESSADA
            WHEN 10 THEN 5    -- EXPIRADA
            WHEN 14 THEN 6    -- NOTIFICADA
            WHEN 20 THEN 7    -- REBUTJADA
            WHEN 27 THEN 8    -- ENVIAT_SIR
            WHEN 28 THEN 9    -- ENVIADA_AMB_ERRORS
            WHEN 29 THEN 10   -- FINALITZADA_AMB_ERRORS
            WHEN 26 THEN 14   -- ANULADA
            ELSE NULL END;
    END;

    FUNCTION afegir_mask(p_mask NUMBER, p_bit NUMBER) RETURN NUMBER IS
    BEGIN
        RETURN CASE WHEN BITAND(p_mask, p_bit) = 0 THEN p_mask + p_bit ELSE p_mask END;
    END;

    PROCEDURE set_escenari(i PLS_INTEGER) IS
        v_slot PLS_INTEGER := MOD(i, 24);
    BEGIN
        e := NULL;
        e.env_tipus := ET_NOTIFICACIO;
        e.env_final := 0;
        e.reg_error := 0; e.notifica_env := 0; e.notifica_err := 0; e.consulta_err := 0;
        e.datat := 0; e.certificat := 0; e.sir_acceptat := 0; e.anulat := 0;
        e.processat := 0; e.programat := 0;
        IF v_slot IN (0, 1) THEN
            e.codi := 'A'; e.not_estat := NE_PENDENT; e.env_estat := EE_NOTIB_PENDENT;
            e.sm_estat := SM_REGISTRE_PENDENT; e.sm_event := SE_RG_ENVIAR;
        ELSIF v_slot IN (2, 3) THEN
            e.codi := 'B'; e.not_estat := NE_PENDENT; e.env_estat := EE_NOTIB_PENDENT;
            e.sm_estat := SM_REGISTRE_ERROR; e.sm_event := SE_RG_ERROR; e.reg_error := 1;
        ELSIF v_slot IN (4, 5) THEN
            e.codi := 'C'; e.not_estat := NE_REGISTRADA; e.env_estat := EE_REGISTRADA;
            e.sm_estat := SM_NOTIFICA_PENDENT; e.sm_event := SE_RG_SUCCESS; e.reg_estat := RE_VALID;
        ELSIF v_slot IN (6, 7, 8) THEN
            e.codi := 'D'; e.not_estat := NE_ENVIADA;
            e.env_estat := CASE WHEN MOD(i, 2) = 0 THEN EE_PENDENT_SEU ELSE EE_ENVIADA_DEH END;
            e.sm_estat := SM_NOTIFICA_SENT; e.sm_event := SE_NT_SUCCESS; e.reg_estat := RE_VALID;
            e.notifica_env := 1;
        ELSIF v_slot = 9 THEN
            e.codi := 'E'; e.not_estat := NE_ENVIADA_AMB_ERRORS; e.env_estat := EE_REGISTRADA;
            e.sm_estat := SM_NOTIFICA_ERROR; e.sm_event := SE_NT_ERROR; e.reg_estat := RE_VALID;
            e.notifica_err := 1;
        ELSIF v_slot = 10 THEN
            e.codi := 'F'; e.not_estat := NE_ENVIADA; e.env_estat := EE_PENDENT_SEU;
            e.sm_estat := SM_CONSULTA_ERROR; e.sm_event := SE_CN_ERROR; e.reg_estat := RE_VALID;
            e.notifica_env := 1; e.consulta_err := 1;
        ELSIF v_slot IN (11, 12) THEN
            e.codi := 'G'; e.not_estat := NE_FINALITZADA; e.env_estat := EE_NOTIFICADA; e.env_final := 1;
            e.sm_estat := SM_FI; e.sm_event := SE_CN_SUCCESS; e.reg_estat := RE_VALID;
            e.notifica_env := 1; e.datat := 1; e.certificat := 1;
        ELSIF v_slot = 13 THEN
            e.codi := 'H'; e.not_estat := NE_FINALITZADA; e.env_estat := EE_REBUTJADA; e.env_final := 1;
            e.sm_estat := SM_FI; e.sm_event := SE_CN_SUCCESS; e.reg_estat := RE_VALID;
            e.notifica_env := 1; e.datat := 1; e.certificat := 1;
        ELSIF v_slot = 14 THEN
            e.codi := 'I'; e.not_estat := NE_FINALITZADA; e.env_estat := EE_EXPIRADA; e.env_final := 1;
            e.sm_estat := SM_FI; e.sm_event := SE_CN_SUCCESS; e.reg_estat := RE_VALID;
            e.notifica_env := 1; e.datat := 1;
        ELSIF v_slot = 15 THEN
            e.codi := 'J'; e.not_estat := NE_FINALITZADA_AMB_ERRORS; e.env_final := 1;
            e.env_estat := CASE MOD(TRUNC(i / 24), 4) WHEN 0 THEN EE_ABSENT WHEN 1 THEN EE_ADRESA_INCORRECTA
                                               WHEN 2 THEN EE_DESCONEGUT ELSE EE_MORT END;
            e.sm_estat := SM_FI; e.sm_event := SE_CN_SUCCESS; e.reg_estat := RE_VALID;
            e.notifica_env := 1; e.datat := 1;
        ELSIF v_slot = 16 THEN
            e.codi := 'K'; e.not_estat := NE_PROCESSADA; e.env_estat := EE_NOTIFICADA; e.env_final := 1;
            e.sm_estat := SM_FI; e.sm_event := SE_CN_SUCCESS; e.reg_estat := RE_VALID;
            e.notifica_env := 1; e.datat := 1; e.certificat := 1; e.processat := 1;
        ELSIF v_slot IN (17, 18) THEN
            e.codi := 'L'; e.env_tipus := ET_COMUNICACIO; e.not_estat := NE_FINALITZADA;
            e.env_estat := EE_LLEGIDA; e.env_final := 1;
            e.sm_estat := SM_FI; e.sm_event := SE_CN_SUCCESS; e.reg_estat := RE_VALID;
            e.notifica_env := 1; e.datat := 1;
        ELSIF v_slot = 19 THEN
            e.codi := 'M'; e.env_tipus := ET_SIR; e.not_estat := NE_ENVIAT_SIR; e.env_estat := EE_ENVIAT_SIR;
            e.sm_estat := SM_SIR_PENDENT; e.sm_event := SE_RG_SUCCESS; e.reg_estat := RE_OFICI_SIR;
        ELSIF v_slot = 20 THEN
            e.codi := 'N'; e.env_tipus := ET_SIR; e.not_estat := NE_FINALITZADA; e.env_estat := EE_ENVIAT_SIR;
            e.env_final := 1; e.sm_estat := SM_FI; e.sm_event := SE_SR_SUCCESS; e.reg_estat := RE_OFICI_ACCEPTAT;
            e.sir_acceptat := 1;
        ELSIF v_slot IN (21, 22) THEN
            e.codi := 'O'; e.not_estat := NE_ANULADA; e.env_estat := EE_ANULADA; e.env_final := 1;
            e.sm_estat := SM_FI; e.sm_event := SE_NT_SUCCESS; e.reg_estat := RE_VALID;
            e.notifica_env := 1; e.anulat := 1;
        ELSE
            e.codi := 'P'; e.not_estat := NE_PENDENT; e.env_estat := EE_ENVIAMENT_PROGRAMAT;
            e.sm_estat := SM_NOU; e.sm_event := SE_RG_ENVIAR; e.programat := 1;
        END IF;
    END;

    PROCEDURE inserir_event(p_tipus PLS_INTEGER, p_data DATE, p_error PLS_INTEGER, p_desc VARCHAR2, p_error_desc VARCHAR2) IS
    BEGIN
        v_ev_id := nou_id;
        INSERT INTO NOT_NOTIFICACIO_EVENT (
            ID, CREATEDDATE, LASTMODIFIEDDATE, CREATEDBY_CODI, LASTMODIFIEDBY_CODI,
            DATA, DESCRIPCIO, ERROR, ERROR_DESC, NOTIFICACIO_ID, NOTIFICACIO_ENV_ID, TIPUS,
            FI_REINTENTS, INTENTS)
        VALUES (
            v_ev_id, p_data, p_data, c_usuari, c_usuari,
            p_data, p_desc, p_error, p_error_desc, v_not_id, v_env_id, p_tipus,
            p_error, CASE WHEN p_error = 1 THEN 3 ELSE 1 END);
        v_ultim_ev_id := v_ev_id;
        v_ultim_ev_dt := p_data;
        IF p_error = 1 THEN
            v_err_ev_id := v_ev_id;
            v_err_desc := p_error_desc;
        END IF;
        v_ev_count := v_ev_count + 1;
    END;

BEGIN
    v_sm_noms(SM_NOU) := 'NOU';
    v_sm_noms(SM_REGISTRE_PENDENT) := 'REGISTRE_PENDENT';
    v_sm_noms(SM_REGISTRE_ERROR) := 'REGISTRE_ERROR';
    v_sm_noms(SM_NOTIFICA_PENDENT) := 'NOTIFICA_PENDENT';
    v_sm_noms(SM_NOTIFICA_ERROR) := 'NOTIFICA_ERROR';
    v_sm_noms(SM_NOTIFICA_SENT) := 'NOTIFICA_SENT';
    v_sm_noms(SM_CONSULTA_ERROR) := 'CONSULTA_ERROR';
    v_sm_noms(SM_SIR_PENDENT) := 'SIR_PENDENT';
    v_sm_noms(SM_FI) := 'FI';

    SELECT ID, NOM, DIR3_CODI INTO v_entitat_id, v_entitat_nom, v_entitat_dir3
      FROM NOT_ENTITAT WHERE CODI = c_entitat_codi;

    SELECT p.ID, p.CODI, p.NOM, NVL(p.COMU, 0), NVL(p.DIRECT_PERMISSION_REQUIRED, 0), p.TIPUS,
           o.ID, o.CODI, o.NOM, DECODE(o.ESTAT, 'V', 0, 'E', 1, 'A', 2, 'T', 3, 0)
      BULK COLLECT INTO v_procs
      FROM NOT_PROCEDIMENT p
      JOIN NOT_ORGAN_GESTOR o ON o.ID = p.ORGAN_GESTOR
     WHERE p.ENTITAT = v_entitat_id
     ORDER BY p.ID;
    IF v_procs.COUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'L''entitat ' || c_entitat_codi || ' no té cap procediment/servei amb òrgan gestor');
    END IF;

    -- Es reutilitza un document real de gesdoc perquè les descàrregues funcionin (si n'hi ha cap)
    BEGIN
        SELECT ARXIU_GEST_DOC_ID INTO v_gesdoc_id FROM (
            SELECT ARXIU_GEST_DOC_ID FROM NOT_DOCUMENT WHERE ARXIU_GEST_DOC_ID IS NOT NULL ORDER BY ID DESC
        ) WHERE ROWNUM = 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        v_gesdoc_id := NULL;
    END;

    -- Òrgan destí per a les comunicacions SIR: un òrgan vigent diferent de l'emissor
    BEGIN
        SELECT CODI, NOM INTO v_sir_desti, v_sir_desti_nom FROM (
            SELECT CODI, NOM FROM NOT_ORGAN_GESTOR
             WHERE ESTAT = 'V' AND ENTITAT = v_entitat_id AND CODI <> v_entitat_dir3 AND LENGTH(CODI) <= 9
             ORDER BY ID
        ) WHERE ROWNUM = 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        v_sir_desti := v_entitat_dir3;
        v_sir_desti_nom := v_entitat_nom;
    END;

    DBMS_OUTPUT.PUT_LINE('Entitat ' || c_entitat_codi || ' (id ' || v_entitat_id || '), ' || v_procs.COUNT
        || ' procediments. Creant ' || c_num_notificacions || ' notificacions...');

    FOR i IN 1 .. c_num_notificacions LOOP
        set_escenari(i);
        v_p := v_procs(MOD(i, v_procs.COUNT) + 1);
        v_num_envs := CASE WHEN e.env_tipus = ET_SIR THEN 1
                           WHEN MOD(i, 7) = 0 THEN 3
                           WHEN MOD(i, 3) = 0 THEN 2
                           ELSE 1 END;

        -- Data de creació repartida al llarg del període, amb hora variable
        v_created := TRUNC(SYSDATE) - MOD(i * 37, c_dies_enrere) + MOD(TO_NUMBER(i) * 7919, 36000) / 86400 + 8 / 24;
        IF v_created > SYSDATE THEN
            v_created := v_created - 1;
        END IF;
        v_reg_data   := v_created + 5 / 1440;
        v_env_data   := v_created + 15 / 1440;
        v_datat_data := v_created + 1 + MOD(i, 9);
        IF v_datat_data > SYSDATE THEN
            v_datat_data := SYSDATE - 1 / 24;
        END IF;
        v_cad  := TRUNC(v_created) + 10;
        v_prog := CASE WHEN e.programat = 1 THEN TRUNC(SYSDATE) + 1 + MOD(i, 30) ELSE NULL END;
        v_ref  := nou_uuid;
        v_concepte := 'PERF ' || LPAD(i, 7, '0') || ' - '
            || CASE e.env_tipus WHEN ET_NOTIFICACIO THEN 'Notificació' WHEN ET_COMUNICACIO THEN 'Comunicació' ELSE 'Comunicació SIR' END
            || ' de prova de rendiment (' || e.codi || ')';

        -- Document
        v_doc_id := nou_id;
        INSERT INTO NOT_DOCUMENT (
            ID, CREATEDDATE, CREATEDBY_CODI, ARXIU_GEST_DOC_ID, ARXIU_NOM, NORMALITZAT, MEDIA, MIDA,
            ORIGEN, VALIDESA, TIPUS_DOCUMENTAL, FIRMAT)
        VALUES (
            v_doc_id, v_created, c_usuari, v_gesdoc_id, 'document_perf_' || i || '.pdf', 0, 'application/pdf', 102400 + MOD(i, 50000),
            'ADMINISTRACIO', 'ORIGINAL', 'NOTIFICACIO', 0);

        -- Notificació
        v_not_id := nou_id;
        -- Cada enviament té el seu propi número de registre; a la notificació hi queda el del primer
        v_reg_num := CASE WHEN e.reg_estat IS NOT NULL THEN 100000 + i * 3 ELSE NULL END;
        v_reg_formatat := CASE WHEN e.reg_estat IS NOT NULL THEN 'GOIBS' || v_reg_num || '/' || TO_CHAR(v_reg_data, 'YYYY') ELSE NULL END;
        INSERT INTO NOT_NOTIFICACIO (
            ID, CREATEDDATE, LASTMODIFIEDDATE, CREATEDBY_CODI, LASTMODIFIEDBY_CODI,
            CADUCITAT, CADUCITAT_ORIGINAL, COM_TIPUS, CONCEPTE, DESCRIPCIO, EMISOR_DIR3CODI, ENV_DATA_PROG, ENV_TIPUS,
            CALLBACK_ERROR, ESTAT, ESTAT_DATE, ESTAT_PROCESSAT_DATE, MOTIU, GRUP_CODI,
            NOT_ENV_DATA, NOT_ENV_DATA_NOTIFICA, NOT_ENV_INTENT, PROC_CODI_NOTIB,
            REGISTRE_DATA, REGISTRE_ENV_INTENT, REGISTRE_NUMERO, REGISTRE_NUMERO_FORMATAT,
            REGISTRE_OFICINA_NOM, REGISTRE_LLIBRE_NOM, RETARD_POSTAL, TIPUS_USUARI, USUARI_CODI,
            DOCUMENT_ID, ENTITAT_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, IDIOMA,
            IS_ERROR_LAST_EVENT, REFERENCIA, JUSTIFICANT_CREAT, DELETED, ORGAN_GESTOR, ORIGEN, ANULABLE)
        VALUES (
            v_not_id, v_created, v_created, c_usuari, c_usuari,
            CASE WHEN e.env_tipus = ET_SIR THEN NULL ELSE v_cad END,
            CASE WHEN e.env_tipus = ET_SIR THEN NULL ELSE v_cad END,
            1, v_concepte, 'Descripció de la notificació ' || i || ' generada per a proves de rendiment',
            v_entitat_dir3, v_prog, e.env_tipus,
            0, e.not_estat, v_created + 1 / 24,
            CASE WHEN e.processat = 1 THEN v_datat_data + 1 / 24 ELSE NULL END,
            CASE WHEN e.processat = 1 THEN 'Processada manualment (proves rendiment)' ELSE NULL END,
            NULL,
            CASE WHEN e.notifica_env = 1 THEN v_env_data ELSE NULL END,
            CASE WHEN e.notifica_env = 1 THEN v_env_data ELSE NULL END,
            CASE WHEN e.notifica_err = 1 THEN 3 WHEN e.notifica_env = 1 THEN 1 ELSE 0 END,
            v_p.codi,
            CASE WHEN e.reg_estat IS NOT NULL THEN v_reg_data ELSE NULL END,
            CASE WHEN e.reg_error = 1 THEN 3 WHEN e.reg_estat IS NOT NULL THEN 1 ELSE 0 END,
            v_reg_num, v_reg_formatat,
            CASE WHEN e.reg_estat IS NOT NULL THEN 'Oficina de proves' ELSE NULL END,
            CASE WHEN e.reg_estat IS NOT NULL THEN 'Llibre de proves' ELSE NULL END,
            0, 1, c_usuari,
            v_doc_id, v_entitat_id, v_p.id, NULL, MOD(i, 2),
            GREATEST(e.reg_error, e.notifica_err, e.consulta_err), v_ref, 0, 0, v_p.organ_id, 'WEB', 0);

        v_titulars := NULL;
        v_reg_nums := NULL;
        v_estat_mask := 0;
        v_enviada_dt := NULL;
        v_cer_dt := NULL;
        v_tab_err_dt := NULL;
        v_tab_err_desc := NULL;
        v_tab_err_fi := 0;
        v_anulable := 0;
        v_num_errors := 0;

        FOR k IN 1 .. v_num_envs LOOP
            v_env_id := nou_id;
            v_env_ref := nou_uuid;
            v_ultim_ev_id := NULL;
            v_ultim_ev_dt := NULL;
            v_err_ev_id := NULL;
            v_err_desc := NULL;
            v_env_estat := e.env_estat;

            -- Titular
            v_tit_id := nou_id;
            IF e.env_tipus = ET_SIR THEN
                v_tipus_int := 'ADMINISTRACIO';
                v_nom := NULL; v_ll1 := NULL; v_ll2 := NULL; v_nif := NULL;
                v_rao_social := v_sir_desti_nom;
                v_cod_desti := v_sir_desti;
            ELSIF MOD(i + k, 10) = 0 THEN
                v_tipus_int := 'JURIDICA';
                v_nom := NULL; v_ll1 := NULL; v_ll2 := NULL;
                v_nif := 'B' || LPAD(MOD(i * 131 + k, 10000000), 7, '0') || MOD(i + k, 10);
                v_rao_social := 'Empresa de proves ' || i || '-' || k || ' SL';
                v_cod_desti := NULL;
            ELSE
                v_tipus_int := 'FISICA';
                v_nom := v_noms(MOD(i + k, v_noms.COUNT) + 1);
                v_ll1 := v_llinatges(MOD(i * 3 + k, v_llinatges.COUNT) + 1);
                v_ll2 := v_llinatges(MOD(i * 7 + k * 5, v_llinatges.COUNT) + 1);
                v_nif := LPAD(MOD(TO_NUMBER(i) * 9973 + k * 31, 100000000), 8, '0');
                v_nif := v_nif || SUBSTR(v_estats_lletra, MOD(TO_NUMBER(v_nif), 23) + 1, 1);
                v_rao_social := NULL;
                v_cod_desti := NULL;
            END IF;
            INSERT INTO NOT_PERSONA (
                ID, CREATEDDATE, CREATEDBY_CODI, COD_ENTITAT_DESTI, EMAIL, INCAPACITAT, INTERESSATTIPUS,
                LLINATGE1, LLINATGE2, NIF, NOM, RAO_SOCIAL, TELEFON, NOTIFICACIO_ENV_ID)
            VALUES (
                v_tit_id, v_created, c_usuari, v_cod_desti,
                CASE WHEN v_tipus_int = 'FISICA' THEN 'perf' || i || '_' || k || '@limit.es' ELSE NULL END,
                0, v_tipus_int, v_ll1, v_ll2, v_nif, v_nom, v_rao_social, NULL, v_env_id);
            v_tit_fmt := CASE WHEN v_rao_social IS NOT NULL THEN v_rao_social ELSE v_nom || ' ' || v_ll1 || ' ' || v_ll2 END
                || CASE WHEN v_nif IS NOT NULL THEN ' (' || v_nif || ')' ELSE NULL END;
            v_destinataris := v_tit_fmt;

            -- Destinatari addicional (alguns enviaments de notificació)
            IF e.env_tipus = ET_NOTIFICACIO AND MOD(i + k, 5) = 0 THEN
                v_dest_id := nou_id;
                v_dest_nom := v_noms(MOD(i + 3, v_noms.COUNT) + 1);
                v_dest_ll1 := v_llinatges(MOD(i + 2, v_llinatges.COUNT) + 1);
                v_dest_ll2 := v_llinatges(MOD(i + 5, v_llinatges.COUNT) + 1);
                INSERT INTO NOT_PERSONA (
                    ID, CREATEDDATE, CREATEDBY_CODI, INCAPACITAT, INTERESSATTIPUS,
                    LLINATGE1, LLINATGE2, NIF, NOM, NOTIFICACIO_ENV_ID)
                VALUES (
                    v_dest_id, v_created, c_usuari, 0, 'FISICA',
                    v_dest_ll1, v_dest_ll2, '00000000T', v_dest_nom, v_env_id);
                v_destinataris := v_destinataris || '<br>' || v_dest_nom || ' ' || v_dest_ll1 || ' ' || v_dest_ll2 || ' (00000000T)';
            END IF;

            -- Events (en ordre cronològic; l'últim queda com a ULTIM_EVENT de l'enviament)
            IF e.reg_error = 1 THEN
                inserir_event(EV_REGISTRE_ENVIAMENT, v_reg_data, 1, NULL,
                    'Codi error: ERROR' || CHR(10) || 'No s''ha pogut registrar l''enviament (error simulat per proves de rendiment)');
            ELSIF e.reg_estat IS NOT NULL THEN
                inserir_event(EV_REGISTRE_ENVIAMENT, v_reg_data, 0, NULL, NULL);
            END IF;
            IF e.env_tipus = ET_SIR AND e.sir_acceptat = 1 THEN
                inserir_event(EV_SIR_CONSULTA, v_datat_data, 0, NULL, NULL);
            END IF;
            IF e.notifica_err = 1 THEN
                inserir_event(EV_NOTIFICA_ENVIAMENT, v_env_data, 1, NULL,
                    'Error en l''enviament a Notific@: servei no disponible (error simulat per proves de rendiment)');
            ELSIF e.notifica_env = 1 THEN
                inserir_event(EV_NOTIFICA_ENVIAMENT, v_env_data, 0, NULL, NULL);
            END IF;
            IF e.consulta_err = 1 THEN
                inserir_event(EV_NOTIFICA_CONSULTA, v_env_data + 1, 1, NULL,
                    'Error consultant l''estat a Notific@ (error simulat per proves de rendiment)');
            END IF;
            IF e.datat = 1 THEN
                inserir_event(EV_ADVISER_DATAT, v_datat_data, 0, NULL, NULL);
            END IF;
            IF e.certificat = 1 THEN
                inserir_event(EV_ADVISER_CERTIFICACIO, v_datat_data + 1 / 24, 0, NULL, NULL);
            END IF;
            IF e.anulat = 1 THEN
                inserir_event(EV_NOTIFICA_ANULAR, v_env_data + 2, 0, NULL, NULL);
            END IF;

            v_reg_formatat := CASE WHEN e.reg_estat IS NOT NULL THEN 'GOIBS' || (v_reg_num + k - 1) || '/' || TO_CHAR(v_reg_data, 'YYYY') ELSE NULL END;
            v_env_error := CASE WHEN v_err_ev_id IS NOT NULL THEN 1 ELSE 0 END;
            v_notifica_id := CASE WHEN e.notifica_env = 1 THEN 'PERF' || LPAD(v_env_id, 16, '0') ELSE NULL END;
            v_motiu_anul := CASE WHEN e.anulat = 1 THEN 'Anul·lada per proves de rendiment' ELSE NULL END;

            -- Enviament
            INSERT INTO NOT_NOTIFICACIO_ENV (
                ID, CREATEDDATE, LASTMODIFIEDDATE, CREATEDBY_CODI, LASTMODIFIEDBY_CODI,
                NOTIFICACIO_ID, TITULAR_ID, ULTIM_EVENT, NOTIFICA_REF, SERVEI_TIPUS, DEH_OBLIGAT,
                NOTIFICA_ID, NOTIFICA_DATCRE, NOTIFICA_DATDISP, NOTIFICA_DATCAD,
                NOTIFICA_EMI_DIR3CODI, NOTIFICA_EMI_DIR3DESC, NOTIFICA_EMI_DIR3NIF,
                NOTIFICA_ARR_DIR3CODI, NOTIFICA_ARR_DIR3DESC, NOTIFICA_ARR_DIR3NIF,
                NOTIFICA_ESTAT, NOTIFICA_ESTAT_DATA, NOTIFICA_ESTAT_DATAACT, NOTIFICA_ESTAT_FINAL, NOTIFICA_ESTAT_DESC,
                NOTIFICA_DATAT_ORIGEN, NOTIFICA_DATAT_RECNIF, NOTIFICA_DATAT_RECNOM, NOTIFICA_DATAT_NUMSEG,
                NOTIFICA_CER_DATA, NOTIFICA_CER_ARXIUID, NOTIFICA_CER_HASH, NOTIFICA_CER_ORIGEN, NOTIFICA_CER_CSV,
                NOTIFICA_CER_MIME, NOTIFICA_CER_TAMANY, NOTIFICA_CER_TIPUS, NOTIFICA_CER_ARXTIP, NOTIFICA_CER_NUMSEG,
                NOTIFICA_ERROR, NOTIFICA_ERROR_EVENT_ID, NOTIFICA_INTENT_DATA, NOTIFICA_INTENT_NUM,
                REGISTRE_NUMERO_FORMATAT, REGISTRE_DATA, ESTAT_REGISTRE, REGISTRE_ESTAT_FINAL,
                SIR_CON_DATA, SIR_CON_INTENT, SIR_REC_DATA, SIR_REG_DESTI_DATA, SIR_FI_POOLING,
                DEH_CERT_INTENT_NUM, CIE_CERT_INTENT_NUM, PER_EMAIL, CALLBACK_ERROR, PLAZO_AMPLIADO,
                ENTREGA_POSTAL, ANULAT, MOTIU_ANULACIO)
            VALUES (
                v_env_id, v_created, NVL(v_ultim_ev_dt, v_created), c_usuari, c_usuari,
                v_not_id, v_tit_id, v_ultim_ev_id, v_env_ref, 0, 0,
                v_notifica_id,
                CASE WHEN e.notifica_env = 1 THEN v_env_data ELSE NULL END,
                CASE WHEN e.notifica_env = 1 THEN v_env_data ELSE NULL END,
                CASE WHEN e.notifica_env = 1 THEN v_cad ELSE NULL END,
                CASE WHEN e.notifica_env = 1 THEN v_p.organ_codi ELSE NULL END,
                CASE WHEN e.notifica_env = 1 THEN SUBSTR(v_p.organ_nom, 1, 100) ELSE NULL END,
                NULL,
                CASE WHEN e.notifica_env = 1 THEN v_entitat_dir3 ELSE NULL END,
                CASE WHEN e.notifica_env = 1 THEN SUBSTR(v_entitat_nom, 1, 100) ELSE NULL END,
                NULL,
                v_env_estat,
                CASE WHEN e.datat = 1 THEN v_datat_data WHEN e.notifica_env = 1 THEN v_env_data ELSE NULL END,
                NVL(v_ultim_ev_dt, v_created),
                e.env_final, NULL,
                CASE WHEN e.datat = 1 THEN 'electronico' ELSE NULL END,
                CASE WHEN e.datat = 1 AND v_env_estat IN (EE_NOTIFICADA, EE_LLEGIDA) THEN v_nif ELSE NULL END,
                CASE WHEN e.datat = 1 AND v_env_estat IN (EE_NOTIFICADA, EE_LLEGIDA) THEN SUBSTR(v_tit_fmt, 1, 400) ELSE NULL END,
                NULL,
                CASE WHEN e.certificat = 1 THEN v_datat_data + 1 / 24 ELSE NULL END,
                CASE WHEN e.certificat = 1 THEN 'perf-cert-' || v_env_id ELSE NULL END,
                CASE WHEN e.certificat = 1 THEN STANDARD_HASH(TO_CHAR(v_env_id), 'SHA1') ELSE NULL END,
                CASE WHEN e.certificat = 1 THEN 'electronico' ELSE NULL END,
                NULL,
                CASE WHEN e.certificat = 1 THEN 'application/pdf' ELSE NULL END,
                CASE WHEN e.certificat = 1 THEN 54321 ELSE NULL END,
                CASE WHEN e.certificat = 1 THEN 0 ELSE NULL END,
                CASE WHEN e.certificat = 1 THEN 0 ELSE NULL END,
                NULL,
                v_env_error,
                v_err_ev_id,
                CASE WHEN e.notifica_env = 1 OR e.notifica_err = 1 THEN v_env_data ELSE NULL END,
                CASE WHEN e.notifica_err = 1 THEN 3 WHEN e.notifica_env = 1 THEN 1 ELSE 0 END,
                v_reg_formatat,
                CASE WHEN e.reg_estat IS NOT NULL THEN v_reg_data ELSE NULL END,
                e.reg_estat,
                CASE WHEN e.reg_estat IS NULL THEN 0 WHEN e.env_tipus = ET_SIR AND e.sir_acceptat = 0 THEN 0 ELSE 1 END,
                CASE WHEN e.env_tipus = ET_SIR THEN v_reg_data + 1 / 24 ELSE NULL END,
                CASE WHEN e.env_tipus = ET_SIR THEN 1 ELSE 0 END,
                CASE WHEN e.sir_acceptat = 1 THEN v_datat_data ELSE NULL END,
                CASE WHEN e.sir_acceptat = 1 THEN v_datat_data ELSE NULL END,
                e.sir_acceptat,
                0, 0, 0, 0, 0,
                0, e.anulat, v_motiu_anul);
            v_env_count := v_env_count + 1;

            -- State machine
            v_sm_estat_nom := v_sm_noms(e.sm_estat);
            v_sm_context := kryo_context(v_env_ref, e.sm_estat, e.sm_event,
                CASE e.env_tipus WHEN ET_NOTIFICACIO THEN 'NOTIFICACIO' WHEN ET_COMUNICACIO THEN 'COMUNICACIO' ELSE 'SIR' END);
            INSERT INTO STATE_MACHINE (MACHINE_ID, STATE, STATE_MACHINE_CONTEXT)
            VALUES (v_env_ref, v_sm_estat_nom, v_sm_context);

            -- Taula de llistat d'enviaments
            INSERT INTO NOT_NOTIFICACIO_ENV_TABLE (
                ID, CREATEDDATE, CREATEDBY_CODI, LASTMODIFIEDDATE, LASTMODIFIEDBY_CODI,
                NOTIFICACIO_ID, NOT_ID, ENTITAT_ID, DESTINATARIS, TIPUS_ENVIAMENT,
                TITULAR_NIF, TITULAR_NOM, TITULAR_EMAIL, TITULAR_LLINATGE1, TITULAR_LLINATGE2, TITULAR_RAOSOCIAL,
                DATA_PROGRAMADA, PROCEDIMENT_CODI_NOTIB, PROCEDIMENT_ID, PROCEDIMENT_NOM, GRUP_CODI, EMISOR_DIR3,
                USUARI_CODI, ORGAN_ID, NOT_ORGAN_CODI, ORGAN_NOM, ORGAN_ESTAT, CONCEPTE, DESCRIPCIO, LLIBRE,
                NOT_ESTAT, CSV_UUID, HAS_ERRORS, PROCEDIMENT_IS_COMU, PROCEDIMENT_PROCORGAN_ID,
                PROCEDIMENT_REQUIRE_PERMISSION, PROCEDIMENT_TIPUS, REGISTRE_NUMERO, REGISTRE_DATA,
                REGISTRE_ENVIAMENT_INTENT, NOTIFICA_DATA_CADUCITAT, NOTIFICA_IDENTIFICADOR,
                NOTIFICA_CERT_NUM_SEGUIMENT, NOTIFICA_ESTAT, NOTIFICA_REF, CALLBACK_ERROR, ENTREGA_POSTAL,
                ANULAT, MOTIU_ANULACIO, ANULABLE, REFERENCIA_NOTIFICACIO, ULTIM_EVENT_DATA)
            VALUES (
                v_env_id, v_created, c_usuari, NVL(v_ultim_ev_dt, v_created), c_usuari,
                v_not_id, v_not_id, v_entitat_id, v_destinataris, e.env_tipus,
                v_nif, v_nom, CASE WHEN v_tipus_int = 'FISICA' THEN 'perf' || i || '_' || k || '@limit.es' ELSE NULL END,
                v_ll1, v_ll2, v_rao_social,
                v_prog, v_p.codi, v_p.id, v_p.nom, NULL, v_entitat_dir3,
                c_usuari, v_p.organ_id, v_p.organ_codi, v_p.organ_nom, v_p.organ_estat, v_concepte,
                'Descripció de la notificació ' || i || ' generada per a proves de rendiment',
                CASE WHEN e.reg_estat IS NOT NULL THEN 'Llibre de proves' ELSE NULL END,
                e.not_estat, ' ', v_env_error, v_p.comu, NULL,
                v_p.req_perm, v_p.tipus, v_reg_formatat,
                CASE WHEN e.reg_estat IS NOT NULL THEN TRUNC(v_reg_data) ELSE NULL END,
                CASE WHEN e.reg_error = 1 THEN 3 WHEN e.reg_estat IS NOT NULL THEN 1 ELSE 0 END,
                CASE WHEN e.notifica_env = 1 THEN v_cad ELSE NULL END,
                v_notifica_id, NULL, v_env_estat, v_env_ref, 0, 0,
                e.anulat, v_motiu_anul,
                CASE WHEN v_notifica_id IS NOT NULL AND e.env_final = 0 AND e.anulat = 0 THEN 1 ELSE 0 END,
                v_ref, TRUNC(v_ultim_ev_dt));

            -- Agregats de la notificació (mateix càlcul que NotificacioTableHelper.actualitzarRegistre)
            v_titulars := CASE WHEN v_titulars IS NULL THEN v_tit_fmt ELSE SUBSTR(v_titulars || ', ' || v_tit_fmt, 1, 1024) END;
            IF v_reg_formatat IS NOT NULL THEN
                v_reg_nums := CASE WHEN v_reg_nums IS NULL THEN v_reg_formatat ELSE SUBSTR(v_reg_nums || ', ' || v_reg_formatat, 1, 2000) END;
            END IF;
            v_estat_mask := afegir_mask(v_estat_mask, mask_not(e.not_estat));
            IF env_estat_a_not_estat(v_env_estat) IS NOT NULL AND v_estat_mask NOT IN (8, 16) THEN
                v_estat_mask := afegir_mask(v_estat_mask, mask_not(env_estat_a_not_estat(v_env_estat)));
            END IF;
            IF e.sir_acceptat = 1 THEN
                v_estat_mask := v_estat_mask + mask_not(NE_OFICI_ACCEPTAT);
            END IF;
            IF v_ultim_ev_dt IS NOT NULL AND (v_enviada_dt IS NULL OR v_ultim_ev_dt > v_enviada_dt) THEN
                v_enviada_dt := v_ultim_ev_dt;
            END IF;
            IF e.certificat = 1 AND v_cer_dt IS NULL THEN
                v_cer_dt := v_datat_data + 1 / 24;
            END IF;
            IF v_err_ev_id IS NOT NULL AND v_ultim_ev_id = v_err_ev_id THEN
                v_num_errors := v_num_errors + 1;
                IF v_tab_err_dt IS NULL THEN
                    v_tab_err_dt := v_ultim_ev_dt;
                    v_tab_err_desc := v_err_desc;
                END IF;
                v_tab_err_fi := 1;
            END IF;
            IF v_notifica_id IS NOT NULL AND e.env_final = 0 AND e.anulat = 0 THEN
                v_anulable := 1;
            END IF;
        END LOOP;

        IF v_num_errors > 1 THEN
            v_tab_err_desc := 'S''ha produït algun error en els enviaments. Els errors es poden consultar en cada un dels enviaments.';
        END IF;

        UPDATE NOT_NOTIFICACIO SET ANULABLE = v_anulable WHERE ID = v_not_id;

        -- Taula de llistat de notificacions (PER_ACTUALITZAR = 1 i ESTAT_STRING buit: es recalcula en llegir)
        INSERT INTO NOT_NOTIFICACIO_TABLE (
            ID, CREATEDDATE, CREATEDBY_CODI, LASTMODIFIEDDATE, LASTMODIFIEDBY_CODI,
            ENTITAT_ID, ENTITAT_NOM, PROC_CODI_NOTIB, PROCEDIMENT_ORGAN_ID, GRUP_CODI, USUARI_CODI, TIPUS_USUARI,
            ERROR_LAST_CALLBACK, ERROR_LAST_EVENT, LAST_EVENT_FI_REINTENTS, REGISTRE_ENV_INTENT, REGISTRE_NUM_EXPEDIENT,
            NOTIFICA_ERROR_DATE, NOTIFICA_ERROR_DESCRIPCIO, ENV_TIPUS, CONCEPTE, ESTAT, ESTAT_DATE,
            PROCEDIMENT_CODI, PROCEDIMENT_NOM, PROCEDIMENT_IS_COMU, PROCEDIMENT_REQUIRE_PERMISSION, PROCEDIMENT_TIPUS,
            ORGAN_ID, ORGAN_CODI, ORGAN_NOM, ORGAN_ESTAT, NOTIFICACIO_MASSIVA_ID, ESTAT_PROCESSAT_DATE,
            ENVIADA_DATE, REFERENCIA, TITULAR, NOTIFICA_IDS, REGISTRE_NUMS, ESTAT_MASK, ESTAT_STRING,
            DOCUMENT_ID, ENV_CER_DATA, CADUCITAT, PER_ACTUALITZAR, REG_ENV_PENDENTS, DELETED,
            ENTREGA_POSTAL, ENTREGA_POSTAL_ERROR, ANULABLE)
        VALUES (
            v_not_id, v_created, c_usuari, NVL(v_enviada_dt, v_created), c_usuari,
            v_entitat_id, v_entitat_nom, v_p.codi, NULL, NULL, c_usuari, 1,
            0, CASE WHEN v_tab_err_dt IS NOT NULL THEN 1 ELSE 0 END, v_tab_err_fi,
            CASE WHEN e.reg_error = 1 THEN 3 WHEN e.reg_estat IS NOT NULL THEN 1 ELSE 0 END, NULL,
            v_tab_err_dt, v_tab_err_desc, e.env_tipus, v_concepte, e.not_estat, v_created + 1 / 24,
            v_p.codi, v_p.nom, v_p.comu, v_p.req_perm, v_p.tipus,
            v_p.organ_id, v_p.organ_codi, v_p.organ_nom, v_p.organ_estat, NULL,
            CASE WHEN e.processat = 1 THEN v_datat_data + 1 / 24 ELSE NULL END,
            v_enviada_dt, v_ref, v_titulars, NULL, v_reg_nums, v_estat_mask, NULL,
            v_doc_id, v_cer_dt, CASE WHEN e.env_tipus = ET_SIR THEN NULL ELSE v_cad END,
            1, CASE WHEN e.not_estat = NE_PENDENT THEN 1 ELSE 0 END, 0,
            0, 0, v_anulable);

        IF MOD(i, c_commit_cada) = 0 THEN
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('  ' || i || ' notificacions creades');
        END IF;
    END LOOP;
    COMMIT;

    DBMS_OUTPUT.PUT_LINE('Fet: ' || c_num_notificacions || ' notificacions, ' || v_env_count
        || ' enviaments, ' || v_ev_count || ' events.');
END;
/

-- Resum per estat de les notificacions generades
SELECT t.ESTAT,
       DECODE(t.ESTAT, 0, 'PENDENT', 1, 'ENVIADA', 2, 'REGISTRADA', 3, 'FINALITZADA', 4, 'PROCESSADA',
                       8, 'ENVIAT_SIR', 9, 'ENVIADA_AMB_ERRORS', 10, 'FINALITZADA_AMB_ERRORS', 14, 'ANULADA',
                       TO_CHAR(t.ESTAT)) AS NOM_ESTAT,
       COUNT(*) AS NOTIFICACIONS
  FROM NOT_NOTIFICACIO_TABLE t
 WHERE t.CONCEPTE LIKE 'PERF %'
 GROUP BY t.ESTAT
 ORDER BY t.ESTAT;
