INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 'es.caib.notib.adviser.sir.actiu', 'true', 'Activar adviser SIR', 'BOOL', 'GENERAL' );

UPDATE NOT_CONFIG_TYPE SET VALUE = 'es.caib.notib.plugin.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin,es.caib.notib.plugin.valsig.ValidacioFirmesPluginMock' WHERE CODE = 'VALSIG_CLASS';

INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 'es.caib.notib.notifica.apostrof.permes', 'true', 'Permetre el caràcter apòstrof en els enviaments a Notific@', 'BOOL', 'NOTIFICA');