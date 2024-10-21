-- Changeset db/changelog/changes/2.0.6/808.yaml::1634114082437-1::limit
ALTER TABLE not_organ_gestor ADD permetre_sir NUMBER(1) DEFAULT '0' NOT NULL;

-- Changeset db/changelog/changes/2.0.6/930.yaml::1634114082437-1::limit
ALTER TABLE not_pagador_cie ADD cie_extern NUMBER(1) DEFAULT '1' NOT NULL;

ALTER TABLE not_pagador_cie ADD api_key VARCHAR2(128 CHAR);

ALTER TABLE not_pagador_cie ADD salt VARCHAR2(88 CHAR);

ALTER TABLE not_pagador_cie ADD organ_emisor NUMBER(19, 0);

ALTER TABLE not_entrega_postal ADD cie_id VARCHAR2(100 CHAR);

ALTER TABLE not_entrega_postal ADD cie_cancelat NUMBER(1) DEFAULT '1' NOT NULL;

ALTER TABLE not_entrega_postal ADD cie_estat VARCHAR2(50 CHAR);

UPDATE NOT_PAGADOR_CIE SET CIE_EXTERN = 0;

INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.cie.class', 'es.caib.notib.plugin.cie.CieNexeaPluginImpl', 'Classe plugin CIE', 'CIE_CLASS', 'CIE' );

INSERT INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (11, 'PLUGINS', 'CIE', 'Plugin CIE' );

INSERT INTO NOT_CONFIG_TYPE (CODE, VALUE) VALUES ('CIE_CLASS', 'es.caib.notib.plugin.cie.CieNexeaPluginImpl');

INSERT INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (1, 'es.caib.notib.plugin.cie.url', 0, 'Url del plugin CIE', 'CIE');

INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (2, 'es.caib.notib.plugin.cie.max.reintents', '3', 'Número màxim de reintets dels enviaments CIE', 'INT', 'CIE' );

INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (3, 'es.caib.notib.plugin.cie.reintents.delay', '10000', 'Temps en millisegons entre cada reintent', 'INT', 'CIE' );

ALTER TABLE NOT_PAGADOR_CIE ADD CONSTRAINT NOT_EMISORCIE_ORGAN_FK FOREIGN KEY (ORGAN_EMISOR) REFERENCES NOT_ORGAN_GESTOR (ID);

INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.notifica.sincronizar.envioOE.reintents.maxim', '3', 'Màxim nombre de reintents del mètode sincronizarEnvioOE per entregues postals', 'INT', 'NOTIFICA' );

-- Changeset db/changelog/changes/2.0.6/934.yaml::1634114082437-1::limit
ALTER TABLE not_notificacio ADD caducitat_original date;

-- Changeset db/changelog/changes/2.0.6/935.yaml::1634114082437-1::limit
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.llindar.dies.enviament.remeses', '5', 'Llindar en dies passats els quals la remesa no serà envaida i es té que tornar a crear', 'INT', 'GENERAL' );

