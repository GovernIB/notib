-- Modificar les columnes de la taula NOT_EXPLOT_FET per permetre valors NULL (PostgreSQL ja permet valors NULL per defecte)
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_PENDENT DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_REG_ERR DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_REGISTR DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_SIR_ACC DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_SIR_REB DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_NOT_ERR DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_NOT_ENV DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_NOT_NOT DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_NOT_REB DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_NOT_EXP DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_CIE_ERR DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_CIE_ENV DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_CIE_NOT DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_CIE_REB DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_CIE_FAL DROP NOT NULL;
ALTER TABLE NOT_EXPLOT_FET ALTER COLUMN TOT_PROCESS DROP NOT NULL;

-- Afegir columnes calculades amb expressions GENERATED sempre basades en altres camps
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD COLUMN CREATED_DIA DATE GENERATED ALWAYS AS (DATE_TRUNC('day', CREATEDDATE)) STORED;
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD COLUMN ENVIADA_DIA DATE GENERATED ALWAYS AS (DATE_TRUNC('day', ENVIADA_DATE)) STORED;
ALTER TABLE NOT_NOTIFICACIO_ENV_TABLE ADD COLUMN REGISTRE_DIA DATE GENERATED ALWAYS AS (DATE_TRUNC('day', REGISTRE_DATA)) STORED;
ALTER TABLE NOT_NOTIFICACIO_ENV ADD COLUMN ESTAT_DIA DATE GENERATED ALWAYS AS (DATE_TRUNC('day', NOTIFICA_ESTAT_DATAACT)) STORED;

-- Afegir columnes generades amb condicions CASE
ALTER TABLE NOT_NOTIFICACIO_ENV ADD COLUMN ESTAT_NOM TEXT GENERATED ALWAYS AS (
    CASE
        WHEN NOTIFICA_ESTAT = 0 THEN 'PENDENT'
        WHEN NOTIFICA_ESTAT = 10 THEN 'EXPIRADA'
        WHEN NOTIFICA_ESTAT IN (13, 14) THEN 'NOTIFICADA'
        WHEN NOTIFICA_ESTAT = 20 THEN 'REBUTJADA'
        WHEN NOTIFICA_ESTAT = 24 THEN 'PEND_ENV'
        WHEN NOTIFICA_ESTAT IN (2, 3, 9, 11, 12, 21) THEN 'NOT_ERR'
        ELSE 'ALTRES'
    END
) STORED;

ALTER TABLE NOT_NOTIFICACIO_ENV ADD COLUMN SIR_DIA DATE GENERATED ALWAYS AS (DATE_TRUNC('day', SIR_REC_DATA)) STORED;
ALTER TABLE NOT_NOTIFICACIO_ENV ADD COLUMN SIR_ESTAT TEXT GENERATED ALWAYS AS (
    CASE
        WHEN ESTAT_REGISTRE = 9 THEN 'REBUTJADA'
        WHEN ESTAT_REGISTRE = 5 THEN 'NOTIFICADA'
        ELSE 'ALTRES'
    END
) STORED;

-- Crear índexs sobre les columnes calculades
CREATE INDEX NOT_CREATED_DIA_I ON NOT_NOTIFICACIO_TABLE (CREATED_DIA);
CREATE INDEX NOT_ENVIADA_DIA_I ON NOT_NOTIFICACIO_TABLE (ENVIADA_DIA);
CREATE INDEX NOT_REGISTRE_DIA_I ON NOT_NOTIFICACIO_ENV_TABLE (REGISTRE_DIA);
CREATE INDEX NOT_ESTAT_DIA_I ON NOT_NOTIFICACIO_ENV (ESTAT_DIA);
CREATE INDEX NOT_ESTAT_I ON NOT_NOTIFICACIO_ENV (ESTAT_NOM);
CREATE INDEX NOT_SIR_DIA_I ON NOT_NOTIFICACIO_ENV (SIR_DIA);
CREATE INDEX NOT_SIR_ESTAT_I ON NOT_NOTIFICACIO_ENV (SIR_ESTAT);

-- Crear les vistes
CREATE OR REPLACE VIEW NOT_CREADES_ENVIADES_VIEW AS
WITH notificacions AS (
    SELECT
        nt.entitat_id,
        n.procediment_id,
        o.codi AS organ_codi,
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
        o.codi AS organ_codi,
        et.usuari_codi,
        et.tipus_enviament AS env_tipus,
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
        o.codi AS organ_codi,
        n.usuari_codi,
        n.env_tipus,
        n.origen,
        e.ESTAT_NOM AS estat,
        nt.error_last_event AS error,
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
        o.codi AS organ_codi,
        n.usuari_codi,
        n.env_tipus,
        n.origen,
        e.SIR_ESTAT AS estat,
        e.SIR_DIA
    FROM NOT_NOTIFICACIO_ENV e
        JOIN NOT_NOTIFICACIO_ENV_TABLE et ON et.id = e.id
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

CREATE OR REPLACE VIEW NOT_STATS_VIEW AS
SELECT
    entitat_id,
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
SELECT
    entitat_id,
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
SELECT
    entitat_id,
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
SELECT
    entitat_id,
    procediment_id,
    organ_codi,
    usuari_codi,
    env_tipus,
    origen,
    tipus,
    dia,
    total
FROM NOT_SIR_VIEW;
