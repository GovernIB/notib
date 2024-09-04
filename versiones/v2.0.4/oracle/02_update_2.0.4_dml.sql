-- #883
UPDATE NOT_CONFIG SET DESCRIPTION = 'Nombre màxim de vegades que una mateixa comunicació SIR s''intentarà enviar a registre abans d''obtenir una resposta satisfactòria' WHERE KEY = 'es.caib.notib.tasca.enviament.actualitzacio.estat.registre.reintents.maxim';
UPDATE NOT_CONFIG SET DESCRIPTION = 'Dies que el pooling seguirà fent la consulta si aquesta no retorna un estat final' WHERE KEY = 'es.caib.notib.consulta.sir.dies.intents';
UPDATE NOT_CONFIG SET DESCRIPTION = 'Mostrar logs del plugin de validació de signatura' WHERE KEY = 'es.caib.notib.log.tipus.VALIDATE_SIGNATURE';
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.log.tipus.EFICIENCIA_TAULA_REMESES', 'false', 'Mostrar logs de la eficiencia dels metodes', 'LOGS', 17, 0, 'BOOL', 0);
