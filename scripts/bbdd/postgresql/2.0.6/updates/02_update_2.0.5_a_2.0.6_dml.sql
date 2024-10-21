UPDATE NOT_PAGADOR_CIE SET CIE_EXTERN = 0;
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.cie.class', 'es.caib.notib.plugin.cie.CieNexeaPluginImpl', 'Classe plugin CIE', 'CIE_CLASS', 'CIE' );
INSERT INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (11, 'PLUGINS', 'CIE', 'Plugin CIE' );
INSERT INTO NOT_CONFIG_TYPE (CODE, VALUE) VALUES ('CIE_CLASS', 'es.caib.notib.plugin.cie.CieNexeaPluginImpl');
INSERT INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (1, 'es.caib.notib.plugin.cie.url', 0, 'Url del plugin CIE', 'CIE');
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (2, 'es.caib.notib.plugin.cie.max.reintents', '3', 'Número màxim de reintets dels enviaments CIE', 'INT', 'CIE' );
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.notifica.sincronizar.envioOE.reintents.maxim', '3', 'Màxim nombre de reintents del mètode sincronizarEnvioOE per entregues postals', 'INT', 'NOTIFICA' );
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (3, 'es.caib.notib.plugin.cie.reintents.delay', '10000', 'Temps en millisegons entre cada reintent', 'INT', 'CIE' );
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.llindar.dies.enviament.remeses', '5', 'Llindar en dies passats els quals la remesa no serà envaida i es té que tornar a crear', 'INT', 'GENERAL' );

