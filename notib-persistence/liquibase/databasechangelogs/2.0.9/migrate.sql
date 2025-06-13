-- Changeset db/changelog/changes/2.0.9/555.yaml::1634114082437-1::limit
ALTER TABLE not_notificacio ADD seguent_remesa VARCHAR2(36 CHAR);

ALTER TABLE not_notificacio ADD num_registre_previ VARCHAR2(50 CHAR);

-- Changeset db/changelog/changes/2.0.9/981.yaml::1634114082437-1::limit
UPDATE NOT_NOTIFICACIO_TABLE SET estat_mask = estat_mask + 4096 WHERE id IN (SELECT t.id FROM not_notificacio_table t JOIN NOT_NOTIFICACIO_ENV nne ON t.id = nne.NOTIFICACIO_ID WHERE t.ENV_TIPUS  = 2 AND nne.ESTAT_REGISTRE = 5);

UPDATE NOT_NOTIFICACIO_TABLE SET estat_mask = estat_mask + 8192 WHERE id IN (SELECT t.id FROM not_notificacio_table t JOIN NOT_NOTIFICACIO_ENV nne ON t.id = nne.NOTIFICACIO_ID WHERE t.ENV_TIPUS  = 2 AND nne.ESTAT_REGISTRE = 9);

-- Changeset db/changelog/changes/2.0.9/984.yaml::1634114082437-1::limit
ALTER TABLE not_notificacio_env ADD registre_motiu VARCHAR2(255 CHAR);

-- Changeset db/changelog/changes/2.0.9/explotacio.yaml::17458513344725::limit
CREATE TABLE not_explot_fet (id NUMBER(38, 0) NOT NULL, pendent NUMBER(38, 0) NOT NULL, reg_env_error NUMBER(38, 0) NOT NULL, registrada NUMBER(38, 0) NOT NULL, reg_acceptada NUMBER(38, 0) NOT NULL, reg_rebutjada NUMBER(38, 0) NOT NULL, not_env_error NUMBER(38, 0) NOT NULL, not_enviada NUMBER(38, 0) NOT NULL, not_notificada NUMBER(38, 0) NOT NULL, not_rebutjada NUMBER(38, 0) NOT NULL, not_expirada NUMBER(38, 0) NOT NULL, cie_env_error NUMBER(38, 0) NOT NULL, cie_enviada NUMBER(38, 0) NOT NULL, cie_notificada NUMBER(38, 0) NOT NULL, cie_rebutjada NUMBER(38, 0) NOT NULL, cie_error NUMBER(38, 0) NOT NULL, processada NUMBER(38, 0) NOT NULL, dimensio_id NUMBER(38, 0) NOT NULL, temps_id NUMBER(38, 0) NOT NULL, CONSTRAINT PK_NOT_EXPLOT_FET PRIMARY KEY (id));
CREATE TABLE not_explot_dim (id NUMBER(38, 0) NOT NULL, entitat_id NUMBER(38, 0) NOT NULL, procediment_id NUMBER(38, 0), organ_codi VARCHAR2(100 CHAR) NOT NULL, usuari_codi VARCHAR2(100 CHAR) NOT NULL, tipus VARCHAR2(16 CHAR), origen VARCHAR2(16 CHAR), CONSTRAINT PK_NOT_EXPLOT_DIM PRIMARY KEY (id));
CREATE TABLE not_explot_temps (id NUMBER(38, 0) NOT NULL, data date NOT NULL, anualitat INTEGER NOT NULL, mes INTEGER NOT NULL, trimestre INTEGER NOT NULL, setmana INTEGER NOT NULL, dia INTEGER NOT NULL, dia_setmana VARCHAR2(2 CHAR), CONSTRAINT PK_NOT_EXPLOT_TEMPS PRIMARY KEY (id));
ALTER TABLE not_explot_fet ADD CONSTRAINT fk_not_explot_fet_dim FOREIGN KEY (dimensio_id) REFERENCES not_explot_dim (id);
ALTER TABLE not_explot_fet ADD CONSTRAINT fk_not_explot_fet_temps FOREIGN KEY (temps_id) REFERENCES not_explot_temps (id);
ALTER TABLE not_explot_dim ADD CONSTRAINT not_explot_dim_uk UNIQUE (entitat_id, procediment_id, organ_codi, usuari_codi, tipus, origen);
ALTER TABLE not_notificacio ADD origen VARCHAR2(8 CHAR);
INSERT INTO NOT_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_EXPLOTACIO','SCHEDULLED',10,'Explotació de dades estadístiques');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.generar.dades.explotacio.actiu', 'true', 'Activar la generació de dades estadístiques', 'SCHEDULLED_EXPLOTACIO', 0, 0, 'BOOL', 0);
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.generar.dades.explotacio.cron', '0 30 0 * * *', 'Especificar l''expressió ''cron'' indicant la freqüencia en que s''han generar les dades estadístiques', 'SCHEDULLED_EXPLOTACIO', 1, 0, 'CRON', 0);
UPDATE NOT_NOTIFICACIO SET origen = (CASE WHEN NOTIFICACIO_MASSIVA_ID IS NOT NULL THEN 'MASSIVA' WHEN TIPUS_USUARI = 0 THEN 'REST' ELSE 'WEB' END);

UPDATE NOT_NOTIFICACIO n SET n.organ_gestor = (SELECT o.id FROM NOT_ORGAN_GESTOR o JOIN NOT_PROCEDIMENT p ON p.ORGAN_GESTOR = o.id WHERE p.id = n.procediment_id) WHERE n.ORGAN_GESTOR IS NULL;
UPDATE NOT_NOTIFICACIO n SET n.organ_gestor = (SELECT o.id FROM NOT_ORGAN_GESTOR o WHERE o.CODI = n.emisor_dir3codi) WHERE n.ORGAN_GESTOR IS NULL;
UPDATE NOT_NOTIFICACIO n SET n.organ_gestor = (SELECT DISTINCT o.id FROM NOT_ENTITAT e JOIN NOT_ORGAN_GESTOR o ON o.codi = e.DIR3_CODI AND o.entitat = e.id WHERE n.ENTITAT_ID = e.id) WHERE n.ORGAN_GESTOR IS NULL;
ALTER TABLE not_notificacio MODIFY organ_gestor NOT NULL;