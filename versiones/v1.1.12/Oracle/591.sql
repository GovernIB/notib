ALTER TABLE NOT_NOTIFICACIO_ENV ADD DEH_CERT_INTENT_NUM NUMBER (10,0) DEFAULT 0;
ALTER TABLE NOT_NOTIFICACIO_ENV ADD DEH_CERT_INTENT_DATA TIMESTAMP(6);
ALTER TABLE NOT_NOTIFICACIO_ENV ADD CIE_CERT_INTENT_NUM NUMBER (10,0) DEFAULT 0;
ALTER TABLE NOT_NOTIFICACIO_ENV ADD CIE_CERT_INTENT_DATA TIMESTAMP(6);

--PROPIETATS APLICACIÓ
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (
																						0, 
																						'es.caib.notib.tasca.enviament.actualitzacio.certificacio.finalitzades.actiu', 
																						'true',
                                                                                        'Indica si la tasca periòdica de consulta de certificació per DEH i CIE està activa (true/false)',
                                                                                        'BOOL',
                                                                                        'SCHEDULLED_NOTIFICA' )
INSERT INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (
																		7, 
																		'SCHEDULLED', 
																		'SCHEDULLED_UPDATE_CERT_NOTIFICA',
																		'Tasca periòdica d''actualització de la certificació dels enviaments finalitzats (DEH/CIE) sense certificació' )
INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 
    																				   'es.caib.notib.tasca.enviament.actualitzacio.estat.deh.reintents.maxim',
    																				   '10',
                                                                                       'Nombre màxim d''enviaments DEH que es processaran cada cop que s''executi la tasca',
                                                                                       'INT',
                                                                                       'SCHEDULLED_UPDATE_CERT_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 
       																				   'es.caib.notib.tasca.enviament.actualitzacio.estat.cie.reintents.maxim',
                                                                                       '10',
                                                                                       'Nombre màxim d''enviaments CIE que es processaran cada cop que s''executi la tasca',
                                                                                       'INT',
                                                                                       'SCHEDULLED_UPDATE_CERT_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (
    																							2, 
    																							'es.caib.notib.tasca.enviament.deh.actualitzacio.certificacio.periode', 
    																							'300000', 
    																							0,
                                                                                                'Iterval de temps entre les execucions de la tasca (ms) d''enviaments DEH',
                                                                                                'INT',
                                                                                                'SCHEDULLED_UPDATE_CERT_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (
    																							3, 
    																							'es.caib.notib.tasca.enviament.deh.actualitzacio.certificacio.retard.inicial', 
    																							'300000', 
    																							0,
                                                                                                'Temps a esperar per a executar la tasca per primera vegada un cop arrancat el servidor (ms) dels enviaments DEH',
                                                                                                'INT',
                                                                                                'SCHEDULLED_UPDATE_CERT_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (
    																							4, 
    																							'es.caib.notib.tasca.enviament.cie.actualitzacio.certificacio.periode', 
    																							'432000000', 
    																							0,
                                                                                                'Iterval de temps entre les execucions de la tasca (ms) d''enviaments CIE',
                                                                                                'INT',
                                                                                                'SCHEDULLED_UPDATE_CERT_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (
    																							5, 
    																							'es.caib.notib.tasca.enviament.cie.actualitzacio.certificacio.retard.inicial', 
    																							'300000', 
    																							0,
                                                                                                'Temps a esperar per a executar la tasca per primera vegada un cop arrancat el servidor (ms) dels enviaments CIE',
                                                                                                'INT',
                                                                                                'SCHEDULLED_UPDATE_CERT_NOTIFICA' )                                                                                               
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (6, 
       																				   'es.caib.notib.tasca.enviament.deh.actualitzacio.certificacio.processar.max',
                                                                                       '10',
                                                                                       'Nombre màxim d''enviaments DEH sense certificació que es processaran cada cop que s''executi la tasca',
                                                                                       'INT',
                                                                                       'SCHEDULLED_UPDATE_CERT_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (7, 
       																				   'es.caib.notib.tasca.enviament.cie.actualitzacio.certificacio.processar.max',
                                                                                       '10',
                                                                                       'Nombre màxim d''enviaments CIE sense certificació que es processaran cada cop que s''executi la tasca',
                                                                                       'INT',
                                                                                       'SCHEDULLED_UPDATE_CERT_NOTIFICA' )
SELECT 1 FROM DUAL;