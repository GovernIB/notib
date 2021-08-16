-- #377
DELETE FROM NOT_CONFIG
WHERE KEY='es.caib.notib.notifica.apikey';
COMMIT;

-- #568
ALTER TABLE NOT_NOTIFICACIO_TABLE
    ADD PROCEDIMENT_REQUIRE_PERMISSION NUMBER(1) DEFAULT 0 NOT NULL;

ALTER TABLE NOT_NOTIFICACIO_ENV_TABLE
    ADD PROCEDIMENT_REQUIRE_PERMISSION NUMBER(1) DEFAULT 0 NOT NULL;


-- Les següents queries no es poden executar ja que a la base de dades tenim 44 procediments amb codi repetits
-- UPDATE NOT_NOTIFICACIO_TABLE
-- SET PROCEDIMENT_REQUIRE_PERMISSION = (SELECT pro.DIRECT_PERMISSION_REQUIRED FROM NOT_PROCEDIMENT pro WHERE pro.CODI = PROCEDIMENT_CODI)
-- WHERE PROCEDIMENT_CODI IS NOT NULL ;
--
--
-- UPDATE NOT_NOTIFICACIO_ENV_TABLE
-- SET PROCEDIMENT_REQUIRE_PERMISSION = (SELECT pro.DIRECT_PERMISSION_REQUIRED FROM NOT_PROCEDIMENT pro WHERE pro.CODI = PROCEDIMENT_CODI_NOTIB)
-- WHERE PROCEDIMENT_CODI_NOTIB IS NOT NULL ;

