alter table NOT_EXPLOT_FET modify TOT_PENDENT null;
alter table NOT_EXPLOT_FET modify TOT_REG_ERR null;
alter table NOT_EXPLOT_FET modify TOT_REGISTR null;
alter table NOT_EXPLOT_FET modify TOT_SIR_ACC null;
alter table NOT_EXPLOT_FET modify TOT_SIR_REB null;
alter table NOT_EXPLOT_FET modify TOT_NOT_ERR null;
alter table NOT_EXPLOT_FET modify TOT_NOT_ENV null;
alter table NOT_EXPLOT_FET modify TOT_NOT_NOT null;
alter table NOT_EXPLOT_FET modify TOT_NOT_REB null;
alter table NOT_EXPLOT_FET modify TOT_NOT_EXP null;
alter table NOT_EXPLOT_FET modify TOT_CIE_ERR null;
alter table NOT_EXPLOT_FET modify TOT_CIE_ENV null;
alter table NOT_EXPLOT_FET modify TOT_CIE_NOT null;
alter table NOT_EXPLOT_FET modify TOT_CIE_REB null;
alter table NOT_EXPLOT_FET modify TOT_CIE_FAL null;
alter table NOT_EXPLOT_FET modify TOT_PROCESS null;

ALTER TABLE NOT_NOTIFICACIO_TABLE ADD CREATED_DIA AS (TRUNC(CREATEDDATE));
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD ENVIADA_DIA AS (TRUNC(ENVIADA_DATE));
ALTER TABLE NOT_NOTIFICACIO_ENV_TABLE ADD REGISTRE_DIA AS (TRUNC(REGISTRE_DATA));
ALTER TABLE NOT_NOTIFICACIO_ENV ADD ESTAT_DIA AS (TRUNC(NOTIFICA_ESTAT_DATAACT));
ALTER TABLE NOT_NOTIFICACIO_ENV ADD ESTAT_NOM AS (CASE
    WHEN NOTIFICA_ESTAT = 0 THEN 'PENDENT'
    WHEN NOTIFICA_ESTAT = 10 THEN 'EXPIRADA'
    WHEN NOTIFICA_ESTAT IN (13, 14) THEN 'NOTIFICADA'
    WHEN NOTIFICA_ESTAT = 20 THEN 'REBUTJADA'
    WHEN NOTIFICA_ESTAT = 24 THEN 'PEND_ENV'
    WHEN NOTIFICA_ESTAT IN (2, 3, 9, 11, 12, 21) THEN 'NOT_ERR'
    ELSE 'ALTRES' END);
ALTER TABLE NOT_NOTIFICACIO_ENV ADD SIR_DIA AS (TRUNC(SIR_REC_DATA));
ALTER TABLE NOT_NOTIFICACIO_ENV ADD SIR_ESTAT AS (CASE
    WHEN ESTAT_REGISTRE = 9 THEN 'REBUTJADA'
    WHEN ESTAT_REGISTRE = 5 THEN 'NOTIFICADA'
    ELSE 'ALTRES' END);


CREATE INDEX NOT_CREATED_DIA_I ON NOT_NOTIFICACIO_TABLE (CREATED_DIA);
CREATE INDEX NOT_ENVIADA_DIA_I ON NOT_NOTIFICACIO_TABLE (ENVIADA_DIA);
CREATE INDEX NOT_REGISTRE_DIA_I ON NOT_NOTIFICACIO_ENV_TABLE (REGISTRE_DIA);
CREATE INDEX NOT_ESTAT_DIA_I ON NOT_NOTIFICACIO_ENV (ESTAT_DIA);
CREATE INDEX NOT_ESTAT_I ON NOT_NOTIFICACIO_ENV (ESTAT_NOM);
CREATE INDEX NOT_SIR_DIA_I ON NOT_NOTIFICACIO_ENV (SIR_DIA);
CREATE INDEX NOT_SIR_ESTAT_I ON NOT_NOTIFICACIO_ENV (SIR_ESTAT);

CREATE OR REPLACE VIEW NOT_CREADES_ENVIADES_VIEW AS
WITH notificacions AS (
    SELECT
        nt.entitat_id,
        n.procediment_id,
        o.codi as organ_codi,
        nt.usuari_codi,
        nt.env_tipus,
        n.origen,
        nt.CREATED_DIA,
        nt.ENVIADA_DIA
    FROM NOT_NOTIFICACIO_TABLE nt
    JOIN NOT_NOTIFICACIO n ON nt.id = n.id
    JOIN NOT_ORGAN_GESTOR o ON n.organ_gestor = o.id
    WHERE nt.CREATED_DIA IS NOT NULL OR nt.ENVIADA_DIA IS NOT NULL
)
SELECT entitat_id,
       procediment_id,
       organ_codi,
       usuari_codi,
       env_tipus,
       origen,
       'CREADA' AS tipus,
       CREATED_DIA AS dia,
       COUNT(*) AS total
FROM notificacions
WHERE CREATED_DIA IS NOT NULL
GROUP BY entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, CREATED_DIA

UNION ALL

SELECT entitat_id,
       procediment_id,
       organ_codi,
       usuari_codi,
       env_tipus,
       origen,
       'ENVIADA' AS tipus,
       ENVIADA_DIA AS dia,
       COUNT(*) AS total
FROM notificacions
WHERE ENVIADA_DIA IS NOT NULL
GROUP BY entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, ENVIADA_DIA;


CREATE OR REPLACE VIEW NOT_REGISTRADES_VIEW AS
WITH notificacions AS (
    SELECT
        et.entitat_id,
        n.procediment_id,
        o.codi as organ_codi,
        et.usuari_codi,
        et.tipus_enviament as env_tipus,
        n.origen,
        et.REGISTRE_DIA
    FROM NOT_NOTIFICACIO_ENV_TABLE et
    JOIN NOT_NOTIFICACIO n ON et.notificacio_id = n.id
    JOIN NOT_ORGAN_GESTOR o ON n.organ_gestor = o.id
    WHERE et.REGISTRE_DIA IS NOT NULL
)
SELECT entitat_id,
       procediment_id,
       organ_codi,
       usuari_codi,
       env_tipus,
       origen,
       'REGISTRADA' AS tipus,
       REGISTRE_DIA AS dia,
       COUNT(*) AS total
FROM notificacions
WHERE REGISTRE_DIA IS NOT NULL
GROUP BY entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, REGISTRE_DIA;


CREATE OR REPLACE VIEW NOT_ESTAT_VIEW AS
WITH notificacions AS (
    SELECT
        n.entitat_id,
        n.procediment_id,
        o.codi as organ_codi,
        n.usuari_codi,
        n.env_tipus,
        n.origen,
        e.ESTAT_NOM as estat,
        nt.error_last_event as error,
        e.ESTAT_DIA
    FROM NOT_NOTIFICACIO_ENV e
        JOIN NOT_NOTIFICACIO n ON e.notificacio_id = n.id
        JOIN NOT_NOTIFICACIO_TABLE nt ON nt.id = n.id
        JOIN NOT_ORGAN_GESTOR o ON n.organ_gestor = o.id
    WHERE e.ESTAT_DIA IS NOT NULL
)
SELECT entitat_id,
       procediment_id,
       organ_codi,
       usuari_codi,
       env_tipus,
       origen,
       CASE
           WHEN error = 1 AND estat = 'PENDENT' THEN 'REG_ERR'
           WHEN error = 1 AND estat = 'PEND_ENV' THEN 'ENV_ERR'
           ELSE estat
       END AS tipus,
       ESTAT_DIA AS dia,
       COUNT(*) AS total
FROM notificacions
WHERE ESTAT_DIA IS NOT NULL
GROUP BY entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, estat, error, ESTAT_DIA;


CREATE OR REPLACE VIEW NOT_SIR_VIEW AS
WITH notificacions AS (
    SELECT
        n.entitat_id,
        n.procediment_id,
        o.codi as organ_codi,
        n.usuari_codi,
        n.env_tipus,
        n.origen,
        e.SIR_ESTAT as estat,
        e.SIR_DIA
    FROM NOT_NOTIFICACIO_ENV e
        JOIN NOT_NOTIFICACIO_ENV_TABLE et on et.id = e.id
        JOIN NOT_NOTIFICACIO n ON e.notificacio_id = n.id
        JOIN NOT_ORGAN_GESTOR o ON n.organ_gestor = o.id
    WHERE e.SIR_DIA IS NOT NULL AND e.registre_estat_final = 1
)
SELECT entitat_id,
       procediment_id,
       organ_codi,
       usuari_codi,
       env_tipus,
       origen,
       estat AS tipus,
       SIR_DIA AS dia,
       COUNT(*) AS total
FROM notificacions
WHERE SIR_DIA IS NOT NULL
GROUP BY entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, estat, SIR_DIA;

create OR REPLACE view NOTIB.NOT_STATS_VIEW as
SELECT
    ROW_NUMBER() OVER (ORDER BY entitat_id, dia) AS id,
    entitat_id,
    procediment_id,
    organ_codi,
    usuari_codi,
    env_tipus,
    origen,
    tipus,
    dia,
    total
FROM (SELECT entitat_id,
             procediment_id,
             organ_codi,
             usuari_codi,
             env_tipus,
             origen,
             tipus,
             dia,
             total
      FROM NOT_CREADES_ENVIADES_VIEW

      UNION ALL

      SELECT entitat_id,
             procediment_id,
             organ_codi,
             usuari_codi,
             env_tipus,
             origen,
             tipus,
             dia,
             total
      FROM NOT_REGISTRADES_VIEW

      UNION ALL

      SELECT entitat_id,
             procediment_id,
             organ_codi,
             usuari_codi,
             env_tipus,
             origen,
             tipus,
             dia,
             total
      FROM NOT_ESTAT_VIEW

      UNION ALL

      SELECT entitat_id,
             procediment_id,
             organ_codi,
             usuari_codi,
             env_tipus,
             origen,
             tipus,
             dia,
             total
      FROM NOT_SIR_VIEW
     );