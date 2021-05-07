-- 492
INSERT INTO NOT_NOTIFICACIO_TABLE(
                                  ID,
                                  ENTITAT_ID,
                                  ENTITAT_NOM,
                                  PROC_CODI_NOTIB,
                                  PROCEDIMENT_ORGAN_ID,
                                  GRUP_CODI,
                                  USUARI_CODI,
                                  TIPUS_USUARI,
                                  ERROR_LAST_CALLBACK,
                                  ERROR_LAST_EVENT,
                                  REGISTRE_ENV_INTENT,
                                  REGISTRE_NUM_EXPEDIENT,
                                  NOTIFICA_ERROR_DATE,
                                  NOTIFICA_ERROR_DESCRIPCIO,
                                  ENV_TIPUS,
                                  CONCEPTE,
                                  ESTAT,
                                  ESTAT_DATE,
                                  PROCEDIMENT_CODI,
                                  PROCEDIMENT_NOM,
                                  PROCEDIMENT_IS_COMU,
                                  ORGAN_CODI,
                                  ORGAN_NOM,
                                  CREATEDDATE,
                                  CREATEDBY_CODI,
                                  LASTMODIFIEDBY_CODI,
                                  LASTMODIFIEDDATE)
SELECT n.ID,
       n.ENTITAT_ID,
       entitat.NOM,
       n.PROC_CODI_NOTIB,
       n.PROCEDIMENT_ORGAN_ID,
       n.GRUP_CODI,
       n.USUARI_CODI,
       n.TIPUS_USUARI,
       n.CALLBACK_ERROR,
       CASE WHEN n.IS_ERROR_LAST_EVENT is null THEN 0 ELSE n.IS_ERROR_LAST_EVENT END,
       n.REGISTRE_ENV_INTENT,
       n.REGISTRE_NUM_EXPEDIENT,
       event.DATA,
       event.ERROR_DESC,
       n.ENV_TIPUS,
       n.CONCEPTE,
       n.ESTAT,
       n.ESTAT_DATE,
       pro.CODI,
       pro.NOM,
       CASE WHEN pro.COMU is null then 0 else pro.COMU end,
       organ.CODI,
       organ.NOM,
       n.CREATEDDATE,
       n.CREATEDBY_CODI,
       n.LASTMODIFIEDBY_CODI,
       n.LASTMODIFIEDDATE
FROM
    NOT_NOTIFICACIO n
        LEFT JOIN NOT_NOTIFICACIO_EVENT event on n.NOT_ERROR_EVENT_ID = event.ID
        LEFT JOIN NOT_PROCEDIMENT pro on n.PROCEDIMENT_ID = pro.ID
        LEFT JOIN NOT_ORGAN_GESTOR organ on n.ORGAN_GESTOR = organ.CODI
        LEFT JOIN NOT_ENTITAT entitat on n.ENTITAT_ID = entitat.ID;

INSERT INTO NOT_NOTIFICACIO_ENV_TABLE (
    ID,
    NOTIFICACIO_ID,
    ENTITAT_ID,
    DESTINATARIS,
    TIPUS_ENVIAMENT,

    -- TITULAR
    TITULAR_NIF,
    TITULAR_NOM,
    TITULAR_EMAIL,
    TITULAR_LLINATGE1,
    TITULAR_LLINATGE2,
    TITULAR_RAOSOCIAL,

    -- INFO NOTIFICACIO
    DATA_PROGRAMADA,
    PROCEDIMENT_CODI_NOTIB,
    GRUP_CODI,
    EMISOR_DIR3,
    USUARI_CODI,
    NOT_ORGAN_CODI,
    CONCEPTE,
    DESCRIPCIO,
    LLIBRE,
    NOT_ESTAT,
    CSV_UUID,
    HAS_ERRORS,

    -- INFO PROCEDIMENT
    PROCEDIMENT_IS_COMU,
    PROCEDIMENT_PROCORGAN_ID,

    -- REGISTRE
    REGISTRE_NUMERO,
    REGISTRE_DATA,
    REGISTRE_ENVIAMENT_INTENT,

    -- NOTIFICA
    NOTIFICA_DATA_CADUCITAT,
    NOTIFICA_IDENTIFICADOR,
    NOTIFICA_CERT_NUM_SEGUIMENT,
    NOTIFICA_ESTAT,
    NOTIFICA_REF,

    CREATEDDATE,
    CREATEDBY_CODI,
    LASTMODIFIEDBY_CODI,
    LASTMODIFIEDDATE
)
SELECT env.ID,
       n.id,
       n.ENTITAT_ID,
       (select
            listagg(
                        CASE WHEN dest.nif is null THEN '' ELSE concat(dest.nif, ' - ') END ||
                        CASE WHEN dest.llinatge1 is null AND dest.llinatge2 is null THEN
                                     '[' || dest.nom || ']'
                             ELSE
                                     '[' || dest.llinatge1 || CASE WHEN dest.llinatge2 is null THEN '' ELSE concat(' ', dest.llinatge2) END || ', ' || dest.nom || ']'
                            END
                , '<br> ')
                        within group ( order by dest.nom )
        from
            not_persona dest
        WHERE dest.notificacio_env_id = env.id),
       N.ENV_TIPUS,

       titular.NIF,
       titular.nom,
       titular.EMAIL,
       titular.LLINATGE1,
       titular.LLINATGE2,
       titular.RAO_SOCIAL,

       n.ENV_DATA_PROG,
       n.PROC_CODI_NOTIB,
       n.GRUP_CODI,
       n.EMISOR_DIR3CODI,
       n.USUARI_CODI,
       n.ORGAN_GESTOR,
       n.CONCEPTE,
       n.DESCRIPCIO,
       n.REGISTRE_LLIBRE_NOM,
       n.ESTAT,
       concat(doc.CSV, doc.UUID),
       CASE WHEN n.NOT_ERROR_EVENT_ID is null THEN 0 ELSE 1 END,
       pro.COMU,
       n.PROCEDIMENT_ORGAN_ID,
       n.REGISTRE_NUMERO,
       n.REGISTRE_DATA,
       n.REGISTRE_ENV_INTENT,

       env.NOTIFICA_DATCAD,
       env.NOTIFICACIO_ID,
       env.NOTIFICA_CER_NUMSEG,
       env.NOTIFICA_ESTAT,
       env.NOTIFICA_REF,

       env.CREATEDDATE,
       env.CREATEDBY_CODI,
       env.LASTMODIFIEDBY_CODI,
       env.LASTMODIFIEDDATE
FROM
    NOT_NOTIFICACIO_ENV env
        INNER JOIN NOT_NOTIFICACIO n on env.NOTIFICACIO_ID = n.ID
        LEFT JOIN NOT_PERSONA titular on env.TITULAR_ID = titular.id
        LEFT JOIN NOT_PROCEDIMENT pro on n.PROCEDIMENT_ID = pro.ID
        LEFT JOIN NOT_DOCUMENT doc on n.DOCUMENT_ID = doc.ID
        LEFT JOIN NOT_ENTITAT entitat on n.ENTITAT_ID = entitat.ID;

-- 504
UPDATE NOT_NOTIFICACIO_TABLE
SET
    NOTIFICA_ERROR_DATE = null,
    NOTIFICA_ERROR_DESCRIPCIO = null
WHERE ESTAT = 1;

-- 489
-- Actualitzar els estats DESCONEGUT i SENSE_INFORMACIO a estats finals
UPDATE NOT_NOTIFICACIO_ENV
SET NOTIFICA_ESTAT_FINAL = 1
WHERE NOTIFICA_ESTAT IN (4, 21);

COMMIT;

-- Actualitzar els estats de les notificacions
UPDATE NOT_NOTIFICACIO
SET ESTAT = 3
WHERE ID IN (
    SELECT nn.ID
    FROM NOT_NOTIFICACIO nn
             INNER JOIN NOT_NOTIFICACIO_ENV nne ON nn.ID = nne.NOTIFICACIO_ID
    WHERE nne.NOTIFICACIO_ID NOT IN (
        SELECT NOTIFICACIO_ID
        FROM NOT_NOTIFICACIO_ENV
        WHERE NOTIFICA_ESTAT_FINAL = 0)
      AND nn.ESTAT NOT IN (3, 4)
);

--
