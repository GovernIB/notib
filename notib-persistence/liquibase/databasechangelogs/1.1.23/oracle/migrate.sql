-- Changeset db/changelog/changes/1.1.23/726.yaml::1665140886760-1::limit
UPDATE NOT_PROCESSOS_INICIALS SET INIT = 1 WHERE CODI = 'PROPIETATS_CONFIG_ENTITATS';

INSERT INTO NOT_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) VALUES ('CARPETA', 'PLUGINS', 10, 'Configuració API CARPETA - Notificacions mòvil');

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugin.carpeta.class', 'es.caib.notib.plugin.carpeta.CarpetaCaibImpl','Classe de la implementació a utilitzar', 'CARPETA', 0, 0, 'TEXT', 1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugin.carpeta.url', 'https://se.caib.es/carpetaapi/interna/secure/mobilenotification/sendnotificationtomobile','Url on es troba la API de la Carpeta', 'CARPETA', 1, 0, 'TEXT', 1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugin.carpeta.usuari', '$notib_carpeta','Usuari de connexió a la Carpeta', 'CARPETA', 2, 0, 'TEXT', 1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugin.carpeta.contrasenya', 'notib_carpeta','Contrasenya de l’usuari', 'CARPETA', 3, 0, 'TEXT', 1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugin.carpeta.missatge.codi.notificacio', 'NOTIFICACIO','Codi de la plantilla de missatge de la carpeta a utilitzar per les notificacions', 'CARPETA', 4, 0, 'TEXT', 1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugin.carpeta.missatge.codi.comunicacio', 'COMUNICACIO','Codi de la plantilla de missatge de la carpeta a utilitzar per les comunicacions', 'CARPETA', 4, 0, 'TEXT', 1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugin.carpeta.msg.actiu', '0','Indica si realitzar l’enviament de missatge mòbil al crear una nova notificació', 'CARPETA', 5, 0, 'BOOL', 1);

-- Changeset db/changelog/changes/1.1.23/788.yaml::1665140886761-1::limit
ALTER TABLE not_organ_gestor ADD nom_es VARCHAR2(1000 CHAR);

INSERT INTO NOT_PROCESSOS_INICIALS (codi, init, id) VALUES ('SINCRONITZAR_ORGANS_NOMS_MULTIDIOMA', 1, 4);

