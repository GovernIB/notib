INSERT ALL
    INTO NOT_CONFIG_TYPE (CODE) VALUES ('PASSWORD')
SELECT 1 FROM DUAL;


--
-- UPDATE NOT_CONFIG
-- SET TYPE_CODE = 'TEXT'
-- WHERE KEY IN (
--               'es.caib.notib.plugin.unitats.dir3.username',
--               'es.caib.notib.plugin.arxiu.caib.conversio.imprimible.usuari',
--               'es.caib.notib.plugin.arxiu.caib.usuari',
--               'es.caib.notib.plugin.gesconadm.username');

UPDATE NOT_CONFIG
SET POSITION=0
WHERE KEY='es.caib.notib.notifica.url';

UPDATE NOT_CONFIG
SET POSITION=1
WHERE KEY='es.caib.notib.notifica.sede.url';

UPDATE NOT_CONFIG
SET POSITION=2, DESCRIPTION='Nom de l''usuari de notific@', TYPE_CODE='TEXT'
WHERE KEY='es.caib.notib.notifica.username';

UPDATE NOT_CONFIG
SET POSITION=3, DESCRIPTION='Password de notific@', TYPE_CODE='PASSWORD'
WHERE KEY='es.caib.notib.notifica.password';

UPDATE NOT_CONFIG
SET POSITION=4
WHERE KEY='es.caib.notib.notifica.apikey';

UPDATE NOT_CONFIG
SET POSITION=5
WHERE KEY='es.caib.notib.notifica.clau.xifrat.ids';

UPDATE NOT_CONFIG
SET POSITION=6
WHERE KEY='es.caib.notib.notifica.versio';



-- --
--
-- --
UPDATE NOT_CONFIG
SET POSITION=0
WHERE KEY='es.caib.notib.plugin.unitats.dir3.url';

UPDATE NOT_CONFIG
SET POSITION=1, DESCRIPTION='Nom de l''usuari de DIR3', TYPE_CODE='TEXT'
WHERE KEY='es.caib.notib.plugin.unitats.dir3.username';

UPDATE NOT_CONFIG
SET POSITION=2, DESCRIPTION='Password de DIR3', TYPE_CODE='PASSWORD'
WHERE KEY='es.caib.notib.plugin.unitats.dir3.password';

UPDATE NOT_CONFIG
SET POSITION=3
WHERE KEY='es.caib.notib.plugin.unitats.dir3.protocol';

UPDATE NOT_CONFIG
SET POSITION=4
WHERE KEY='es.caib.notib.plugin.unitats.class';

UPDATE NOT_CONFIG
SET POSITION=5
WHERE KEY='es.caib.notib.plugin.unitats.fitxer';


-- --
--
-- --
UPDATE NOT_CONFIG
SET POSITION=0
WHERE KEY='es.caib.notib.plugin.arxiu.caib.base.url';

UPDATE NOT_CONFIG
SET POSITION=1
WHERE KEY='es.caib.notib.plugin.arxiu.verificacio.baseurl';

UPDATE NOT_CONFIG
SET POSITION=2, TYPE_CODE='TEXT'
WHERE KEY='es.caib.notib.plugin.arxiu.caib.usuari';

UPDATE NOT_CONFIG
SET POSITION=3, TYPE_CODE='PASSWORD'
WHERE KEY='es.caib.notib.plugin.arxiu.caib.contrasenya';

UPDATE NOT_CONFIG
SET POSITION=4, DESCRIPTION='URL CONCSV'
WHERE KEY='es.caib.notib.plugin.arxiu.csv.base.url';

UPDATE NOT_CONFIG
SET POSITION=5
WHERE KEY='es.caib.notib.plugin.arxiu.caib.conversio.imprimible.url.uuid';

UPDATE NOT_CONFIG
SET POSITION=6
WHERE KEY='es.caib.notib.plugin.arxiu.caib.conversio.imprimible.url.csv';

UPDATE NOT_CONFIG
SET POSITION=7, DESCRIPTION='Usuari CONCSV', TYPE_CODE='TEXT'
WHERE KEY='es.caib.notib.plugin.arxiu.caib.conversio.imprimible.usuari';

UPDATE NOT_CONFIG
SET POSITION=8, DESCRIPTION='Password CONCSV', TYPE_CODE='PASSWORD'
WHERE KEY='es.caib.notib.plugin.arxiu.caib.conversio.imprimible.contrasenya';

UPDATE NOT_CONFIG
SET POSITION=9
WHERE KEY='es.caib.notib.plugin.arxiu.class';

UPDATE NOT_CONFIG
SET POSITION=10
WHERE KEY='es.caib.notib.plugin.arxiu.gestionar.expedients';

UPDATE NOT_CONFIG
SET POSITION=11
WHERE KEY='es.caib.notib.plugin.arxiu.gestionar.documents';

UPDATE NOT_CONFIG
SET POSITION=12
WHERE KEY='es.caib.notib.plugin.arxiu.gestionar.carpetes';

UPDATE NOT_CONFIG
SET POSITION=13
WHERE KEY='es.caib.notib.plugin.arxiu.document.versionable';

UPDATE NOT_CONFIG
SET POSITION=14
WHERE KEY='es.caib.notib.plugin.arxiu.suporta.metadades';

UPDATE NOT_CONFIG
SET POSITION=15
WHERE KEY='es.caib.notib.plugin.arxiu.escriptori.classificacio';

UPDATE NOT_CONFIG
SET POSITION=16
WHERE KEY='es.caib.notib.plugin.arxiu.escriptori.serie.documental';

UPDATE NOT_CONFIG
SET POSITION=17
WHERE KEY='es.caib.notib.plugin.arxiu.caib.aplicacio.codi';

UPDATE NOT_CONFIG
SET POSITION=18
WHERE KEY='es.caib.notib.plugin.arxiu.caib.csv.definicio';

UPDATE NOT_CONFIG
SET POSITION=19
WHERE KEY='es.caib.notib.plugin.arxiu.caib.timeout.connect';

UPDATE NOT_CONFIG
SET POSITION=20
WHERE KEY='es.caib.notib.plugin.arxiu.caib.timeout.read';



-- --
--
-- --
UPDATE NOT_CONFIG
SET POSITION=0
WHERE KEY='es.caib.notib.plugin.gesconadm.base.url';

UPDATE NOT_CONFIG
SET POSITION=1, TYPE_CODE='TEXT'
WHERE KEY='es.caib.notib.plugin.gesconadm.username';

UPDATE NOT_CONFIG
SET POSITION=2, TYPE_CODE='PASSWORD'
WHERE KEY='es.caib.notib.plugin.gesconadm.password';

UPDATE NOT_CONFIG
SET POSITION=3
WHERE KEY='es.caib.notib.plugin.gesconadm.class';

UPDATE NOT_CONFIG
SET POSITION=4
WHERE KEY='es.caib.notib.plugin.gesconadm.basic.authentication';


DELETE FROM NOT_CONFIG_TYPE WHERE CODE = 'CREDENTIALS';
COMMIT ;