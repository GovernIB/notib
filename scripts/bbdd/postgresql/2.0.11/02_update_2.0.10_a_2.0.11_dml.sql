UPDATE NOT_CONFIG_GROUP SET description = 'Plugin de registre' WHERE code = 'REGISTRE';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin de Notifica' WHERE code = 'NOTIFICA';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin de Directori comú d''unitats organitzatives (DIR3)' WHERE code = 'DIR3';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin d''usuaris' WHERE code = 'USUARIS';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin de notificacions mòbils (API CARPETA)' WHERE code = 'CARPETA';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin d''inventari de procediments i serveis (ROLSAC)' WHERE code = 'GESCONADM';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin de centre d''impressió i ensobrat (CIE)' WHERE code = 'CIE';

INSERT INTO NOT_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) VALUES ('COMANDA', 'PLUGINS', 12, 'Plugin de Comanda');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.plugin.comanda.url', '', 'URL del plugin de Comanda', 'COMANDA', 0, 'TEXT', 0, '1');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.log.tipus.COMANDA', 'false', 'Mostrar logs del plugin de Comanda', 'LOGS', 23, 0, 'BOOL', 0);
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.plugin.comanda.entorn.codi', '', 'Codi entorn enviat a Comanda', 'COMANDA', 0, 'TEXT', 0, '2');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.plugin.comanda.actiu', 0, 'Acitvar enviaments a Comanda', 'COMANDA', 0, 'BOOL', 0, '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.plugin.comanda.usuari', '', 'Usuari de Comanda', 'COMANDA', 1, 'TEXT', 0, '3');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.plugin.comanda.password', '', 'Contrasenya de Comanda', 'COMANDA', 1, 'PASSWORD', 0, '4');

ALTER TABLE not_explot_fet DISABLE CONSTRAINT FK_NOT_EXPLOT_FET_TEMPS;
TRUNCATE TABLE NOT_EXPLOT_FET;
TRUNCATE TABLE NOT_EXPLOT_ENV_INFO;
TRUNCATE TABLE not_explot_temps;
ALTER TABLE not_explot_fet ENABLE CONSTRAINT FK_NOT_EXPLOT_FET_TEMPS;