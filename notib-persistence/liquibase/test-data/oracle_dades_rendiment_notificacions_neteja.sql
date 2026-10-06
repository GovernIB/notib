-- =====================================================================================================
-- NOTIB - Eliminació de les dades de prova de rendiment (ORACLE)
-- =====================================================================================================
--
-- Esborra totes les notificacions creades per oracle_dades_rendiment_notificacions.sql (concepte que
-- comença per 'PERF ') i tot el que en depèn: enviaments, taules de llistat, state machine, events,
-- persones, documents, callbacks i correus agrupats.
-- =====================================================================================================

SET SERVEROUTPUT ON SIZE UNLIMITED
SET TIMING ON

DECLARE
    c_lot CONSTANT PLS_INTEGER := 1000;

    v_not_ids  SYS.ODCINUMBERLIST;
    v_env_ids  SYS.ODCINUMBERLIST;
    v_doc_ids  SYS.ODCINUMBERLIST;
    v_pers_ids SYS.ODCINUMBERLIST;
    v_total    PLS_INTEGER := 0;
BEGIN
    LOOP
        SELECT ID, DOCUMENT_ID BULK COLLECT INTO v_not_ids, v_doc_ids
          FROM NOT_NOTIFICACIO WHERE CONCEPTE LIKE 'PERF %' AND ROWNUM <= c_lot;
        EXIT WHEN v_not_ids.COUNT = 0;

        SELECT ID BULK COLLECT INTO v_env_ids
          FROM NOT_NOTIFICACIO_ENV WHERE NOTIFICACIO_ID IN (SELECT COLUMN_VALUE FROM TABLE(v_not_ids));

        SELECT ID BULK COLLECT INTO v_pers_ids FROM (
            SELECT ID FROM NOT_PERSONA WHERE NOTIFICACIO_ENV_ID IN (SELECT COLUMN_VALUE FROM TABLE(v_env_ids))
            UNION
            SELECT TITULAR_ID FROM NOT_NOTIFICACIO_ENV
             WHERE ID IN (SELECT COLUMN_VALUE FROM TABLE(v_env_ids)) AND TITULAR_ID IS NOT NULL);

        DELETE FROM STATE_MACHINE
         WHERE MACHINE_ID IN (SELECT NOTIFICA_REF FROM NOT_NOTIFICACIO_ENV WHERE ID IN (SELECT COLUMN_VALUE FROM TABLE(v_env_ids)));
        DELETE FROM NOT_CORREUS_AGRUPATS WHERE ENVIAMENT_ID IN (SELECT COLUMN_VALUE FROM TABLE(v_env_ids));
        DELETE FROM NOT_CALLBACK
         WHERE NOTIFICACIO_ID IN (SELECT COLUMN_VALUE FROM TABLE(v_not_ids))
            OR ENVIAMENT_ID IN (SELECT COLUMN_VALUE FROM TABLE(v_env_ids));
        DELETE FROM NOT_NOTIFICACIO_ENV_TABLE WHERE NOTIFICACIO_ID IN (SELECT COLUMN_VALUE FROM TABLE(v_not_ids));
        DELETE FROM NOT_NOTIFICACIO_TABLE WHERE ID IN (SELECT COLUMN_VALUE FROM TABLE(v_not_ids));
        DELETE FROM NOT_NOTIFICACIO_ENV WHERE ID IN (SELECT COLUMN_VALUE FROM TABLE(v_env_ids));
        DELETE FROM NOT_NOTIFICACIO_EVENT WHERE NOTIFICACIO_ID IN (SELECT COLUMN_VALUE FROM TABLE(v_not_ids));
        DELETE FROM NOT_PERSONA WHERE ID IN (SELECT COLUMN_VALUE FROM TABLE(v_pers_ids));
        DELETE FROM NOT_NOTIFICACIO WHERE ID IN (SELECT COLUMN_VALUE FROM TABLE(v_not_ids));
        DELETE FROM NOT_DOCUMENT WHERE ID IN (SELECT COLUMN_VALUE FROM TABLE(v_doc_ids));

        v_total := v_total + v_not_ids.COUNT;
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('  ' || v_total || ' notificacions eliminades');
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Fet: ' || v_total || ' notificacions eliminades.');
END;
/
