-- #377
INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (1, 'es.caib.notib.plugin.registre.url',1,
                                                                                     'Url de registre', 'REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (2, 'es.caib.notib.plugin.registre.usuari', 2,
                                                                                     'Usuari', 'REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (3, 'es.caib.notib.plugin.registre.password', 3,
                                                                                                'Contrasenya',
                                                                                                'PASSWORD', 'REGISTRE' )
SELECT 1 FROM DUAL;

UPDATE NOT_CONFIG
SET POSITION=4
WHERE KEY='es.caib.notib.plugin.registre.namespaceuri';

UPDATE NOT_CONFIG
SET POSITION=5
WHERE KEY='es.caib.notib.plugin.registre.service.name';
UPDATE NOT_CONFIG
SET POSITION=6
WHERE KEY='es.caib.notib.plugin.registre.port.name';
UPDATE NOT_CONFIG
SET POSITION=7
WHERE KEY='es.caib.notib.plugin.registre.segons.entre.peticions';
UPDATE NOT_CONFIG
SET POSITION=8
WHERE KEY='es.caib.notib.plugin.registre.documents.enviar';
UPDATE NOT_CONFIG
SET POSITION=9
WHERE KEY='es.caib.notib.plugin.regweb.mock.sequencia';
UPDATE NOT_CONFIG
SET POSITION=10
WHERE KEY='es.caib.notib.plugin.regweb.mock.justificant';
UPDATE NOT_CONFIG
SET POSITION=11
WHERE KEY='es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar';

UPDATE NOT_CONFIG
SET VALUE='REST', DESCRIPTION='API protocol'
WHERE KEY='es.caib.notib.plugin.unitats.dir3.protocol';

commit;