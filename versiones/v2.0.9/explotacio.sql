CREATE TABLE not_explot_temps (
                                  id NUMBER(38, 0) NOT NULL,
                                  data date NOT NULL,
                                  anualitat INTEGER NOT NULL,
                                  mes INTEGER NOT NULL,
                                  trimestre INTEGER NOT NULL,
                                  setmana INTEGER NOT NULL,
                                  dia INTEGER NOT NULL,
                                  dia_setmana VARCHAR2(2 CHAR),
                                  CONSTRAINT NOT_EXPLOT_TEMPS_PK PRIMARY KEY (id));

CREATE TABLE not_explot_dim (
                                id NUMBER(38, 0) NOT NULL,
                                entitat_id NUMBER(38, 0) NOT NULL,
                                procediment_id NUMBER(38, 0),
                                organ_codi VARCHAR2(100 CHAR) NOT NULL,
                                usuari_codi VARCHAR2(100 CHAR) NOT NULL,
                                tipus VARCHAR2(16 CHAR),
                                origen VARCHAR2(16 CHAR),
                                CONSTRAINT NOT_EXPLOT_DIM_PK PRIMARY KEY (id));

ALTER TABLE not_explot_dim ADD CONSTRAINT not_explot_dim_uk UNIQUE (entitat_id, procediment_id, organ_codi, usuari_codi, tipus, origen);

CREATE TABLE not_explot_fet (
                                id NUMBER(38, 0) NOT NULL,
                                pendent NUMBER(38, 0) NOT NULL,
                                reg_env_error NUMBER(38, 0) NOT NULL,
                                registrada NUMBER(38, 0) NOT NULL,
                                reg_acceptada NUMBER(38, 0) NOT NULL,
                                reg_rebutjada NUMBER(38, 0) NOT NULL,
                                not_env_error NUMBER(38, 0) NOT NULL,
                                not_enviada NUMBER(38, 0) NOT NULL,
                                not_notificada NUMBER(38, 0) NOT NULL,
                                not_rebutjada NUMBER(38, 0) NOT NULL,
                                not_expirada NUMBER(38, 0) NOT NULL,
                                cie_env_error NUMBER(38, 0) NOT NULL,
                                cie_enviada NUMBER(38, 0) NOT NULL,
                                cie_notificada NUMBER(38, 0) NOT NULL,
                                cie_rebutjada NUMBER(38, 0) NOT NULL,
                                cie_error NUMBER(38, 0) NOT NULL,
                                processada NUMBER(38, 0) NOT NULL,
                                dimensio_id NUMBER(38, 0) NOT NULL,
                                temps_id NUMBER(38, 0) NOT NULL,
                                CONSTRAINT NOT_EXPLOT_FET_PK PRIMARY KEY (id));
ALTER TABLE not_explot_fet ADD CONSTRAINT not_explot_fet_dim_fk FOREIGN KEY (dimensio_id) REFERENCES not_explot_dim (id);
ALTER TABLE not_explot_fet ADD CONSTRAINT not_explot_fet_temps_fk FOREIGN KEY (temps_id) REFERENCES not_explot_temps (id);

ALTER TABLE not_notificacio ADD origen VARCHAR2(8 CHAR);

UPDATE NOT_NOTIFICACIO SET origen = (CASE
    WHEN NOTIFICACIO_MASSIVA_ID IS NOT NULL THEN 'MASSIVA'
    WHEN TIPUS_USUARI = 0 THEN 'REST'
    ELSE 'WEB'
END);

INSERT INTO NOT_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_EXPLOTACIO','SCHEDULLED',10,'Explotació de dades estadístiques');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.generar.dades.explotacio.actiu', 'true', 'Activar la generació de dades estadístiques', 'SCHEDULLED_EXPLOTACIO', 0, 0, 'BOOL', 0);
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.generar.dades.explotacio.cron', '0 30 0 * * *', 'Especificar l''expressió ''cron'' indicant la freqüencia en que s''han generar les dades estadístiques', 'SCHEDULLED_EXPLOTACIO', 0, 0, 'CRON', 0);

GRANT SELECT, UPDATE, INSERT, DELETE ON not_explot_temps TO www_notib;
GRANT SELECT, UPDATE, INSERT, DELETE ON not_explot_dim TO www_notib;
GRANT SELECT, UPDATE, INSERT, DELETE ON not_explot_fet TO www_notib;