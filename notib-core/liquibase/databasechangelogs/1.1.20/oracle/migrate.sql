-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 26/08/22 15:45
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.20/693.yaml::1634114082437-9::limit
ALTER TABLE not_config ADD entitat_codi VARCHAR2(64 CHAR);

ALTER TABLE not_config ADD CONFIGURABLE NUMBER(1, 0) DEFAULT '0';

INSERT INTO NOT_PROCESSOS_INICIALS (codi, init, id) VALUES ('PROPIETATS_CONFIG_ENTITATS', 1, 2);

UPDATE NOT_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE '%columna.entitat' OR KEY LIKE '%columna.num.expedient' OR KEY LIKE '%email.footer' OR KEY LIKE '%justificant.capsalera.codi' OR KEY LIKE '%justificant.capsalera.direccio' OR KEY LIKE '%justificant.capsalera.email' OR KEY LIKE '%justificant.capsalera.logo' OR KEY LIKE '%justificant.capsalera.nif' OR KEY LIKE '%justificant.peu.logo' OR KEY LIKE '%justificant.peu.titol' OR KEY LIKE '%plugin.arxiu.caib.aplicacio.codi' OR KEY LIKE '%plugin.arxiu.caib.base.url' OR KEY LIKE '%plugin.arxiu.caib.contrasenya' OR KEY LIKE '%plugin.arxiu.caib.conversio.imprimible.contrasenya' OR KEY LIKE '%plugin.arxiu.caib.conversio.imprimible.url.csv' OR KEY LIKE '%plugin.arxiu.caib.conversio.imprimible.url.uuid' OR KEY LIKE '%plugin.arxiu.caib.conversio.imprimible.usuari' OR KEY LIKE '%plugin.arxiu.caib.csv.definicio' OR KEY LIKE '%plugin.arxiu.caib.timeout.connect' OR KEY LIKE '%plugin.arxiu.caib.timeout.read' OR KEY LIKE '%plugin.arxiu.caib.usuari' OR KEY LIKE '%plugin.arxiu.class' OR KEY LIKE '%plugin.arxiu.csv.base.url' OR KEY LIKE '%plugin.arxiu.document.versionable' OR KEY LIKE '%plugin.arxiu.escriptori.classificacio' OR KEY LIKE '%plugin.arxiu.escriptori.serie.documental' OR KEY LIKE '%plugin.arxiu.gestionar.carpetes' OR KEY LIKE '%plugin.arxiu.gestionar.documents' OR KEY LIKE '%plugin.arxiu.gestionar.expedients' OR KEY LIKE '%plugin.arxiu.suporta.metadades' OR KEY LIKE '%plugin.arxiu.verificacio.baseurl' OR KEY LIKE '%plugin.firmaservidor.class' OR KEY LIKE '%plugin.firmaservidor.portafib.auth.password' OR KEY LIKE '%plugin.firmaservidor.portafib.auth.username' OR KEY LIKE '%plugin.firmaservidor.portafib.endpoint' OR KEY LIKE '%plugin.firmaservidor.portafib.location' OR KEY LIKE '%plugin.firmaservidor.portafib.perfil' OR KEY LIKE '%plugin.firmaservidor.portafib.signerEmail' OR KEY LIKE '%plugin.firmaservidor.portafib.username' OR KEY LIKE '%plugin.gesconadm.base.url' OR KEY LIKE '%plugin.gesconadm.basic.authentication' OR KEY LIKE '%plugin.gesconadm.class' OR KEY LIKE '%plugin.gesconadm.password' OR KEY LIKE '%plugin.gesconadm.username' OR KEY LIKE '%plugin.gesdoc.class' OR KEY LIKE '%plugin.registre.class' OR KEY LIKE '%plugin.registre.documents.enviar' OR KEY LIKE '%plugin.registre.enviamentSir.tipusDocumentEnviar' OR KEY LIKE '%plugin.registre.generar.justificant' OR KEY LIKE '%plugin.registre.namespaceuri' OR KEY LIKE '%plugin.registre.port.name' OR KEY LIKE '%plugin.registre.segons.entre.peticions' OR KEY LIKE '%plugin.registre.service.name' OR KEY LIKE '%plugin.regweb.mock.justificant' OR KEY LIKE '%plugin.regweb.mock.sequencia' OR KEY LIKE '%plugin.unitats.class' OR KEY LIKE '%plugin.unitats.dir3.password' OR KEY LIKE '%plugin.unitats.dir3.protocol' OR KEY LIKE '%plugin.unitats.dir3.url' OR KEY LIKE '%plugin.unitats.dir3.username' OR KEY LIKE '%plugin.unitats.fitxer' OR KEY LIKE '%destinatari.multiple' OR KEY LIKE '%document.consulta.id.csv.mida.min' OR KEY LIKE '%document.metadades.por.defecto' OR KEY LIKE '%documents.metadades.from.arxiu' OR KEY LIKE '%plugin.codi.dir3.entitat' OR KEY LIKE '%procediment.alta.auto.caducitat' OR KEY LIKE '%procediment.alta.auto.retard' OR KEY LIKE '%titular.incapacitat' OR KEY LIKE '%emprar.sir' OR KEY LIKE '%adviser.actiu' OR KEY LIKE '%enviament.massiu.prioritat' OR KEY LIKE '%notifica.dir3.entitat.permes';

-- Changeset db/changelog/changes/1.1.20/718.yaml::1653224812020-1::limit
INSERT INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (8, 'SCHEDULLED', 'SCHEDULLED_SERVEIS', 'Tasca periòdica d''actualització dels serveis' );

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.actualitzacio.serveis.actiu', 'true', 'Indica si la tasca periòdica està activa (true/false)', 'SCHEDULLED_SERVEIS', 0, 0, 'BOOL', 0);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.actualitzacio.serveis.cron', '0 0 0 * * ?', 'Especificar l''expressió ''cron'' indicant la freqüencia en que s''han d''actualitzar els serveis', 'SCHEDULLED_SERVEIS', 1, 0, 'CRON', 0);

