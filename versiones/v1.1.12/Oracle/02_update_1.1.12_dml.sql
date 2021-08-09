-- #377 Implementar una nova secció de configuració general de l'aplicació
INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.emprar.sir', 'true', 'Activar SIR', 'BOOL', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 'es.caib.notib.adviser.actiu', 'true', 'Activar ADVISER', 'BOOL', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (2, 'es.caib.notib.comunicacio.tipus.defecte', 'ASINCRON',
                                                                                       'TIPUS DE COMUNICACIÓ DE LA PLATAFORMA (SINCRON/ASINCRON)',
                                                                                       'TIPUS_COMUNICACIO', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (3, 'es.caib.notib.default.user.language', 'ca',
                                                                                       'Llenguatge per defecte de l''aplicació',
                                                                                       'TEXT', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (4, 'es.caib.notib.metriques.generar', 'true', 'Generar mètriques', 'BOOL', 'GENERAL' )

    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (7, 'es.caib.notib.plugin.registre.generar.justificant', 'false',
                                                                                       'Indica si s''ha de generar el justificant del registre de totes les notificacions. ' ||
                                                                                       'Si es false només es generen per a comunicacions a administracions (enviaments SIR)',
                                                                                       'BOOL', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (9, 'es.caib.notib.notifica.dir3.entitat.permes', 'false',
                                                                                       'Tractar l''entitat com un organ gestor més',
                                                                                       'BOOL', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (9, 'es.caib.notib.plugin.codi.dir3.entitat', 'false',
                                                                                       'Si s''activa l''organisme emissor de les notificacions és la mateixa l''entitat',
                                                                                       'BOOL', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (10, 'es.caib.notib.document.consulta.id.csv.mida.min', '16',
                                                                                       'Nombre mínim de caràcters que pot tenir un codi CSV',
                                                                                       'INT', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (11, 'es.caib.notib.app.base.url', '',
                                                                                       'Especificar la URL base de l''aplicació',
                                                                                       'TEXT', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (11, 'es.caib.notib.enviament.massiu.prioritat', 'BAIXA',
                                                                                       'Indica si l''enviament massiu s''ha de prioritzar per a que s''enviin el més aviat possible, ' ||
                                                                                       'o si han de tenir una prioritat inferior que la resta de notificacions.',
                                                                                       'PRIORITAT_ENVIAMENT_MASSIU', 'GENERAL' )

    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (5, 'es.caib.notib.documents.metadades.from.arxiu', 'false',
                                                                                       'Indica si volem consultar les metadades dels documents directament de l''arxiu. ' ||
                                                                                       'Si no està marcat les metadades son consultades a ConCSV',
                                                                                       'BOOL', 'DOCUMENTS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (6, 'es.caib.notib.notificacio.document.size', '10485760',
                                                                                       'Mida máxima permesa per a un document d''una notificació  (bytes)',
                                                                                       'INT', 'DOCUMENTS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (6, 'es.caib.notib.notificacio.document.total.size', '15728640',
                                                                                       'Mida máxima del conjunt de documents d''una notificació (bytes)',
                                                                                       'INT', 'DOCUMENTS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (7, 'es.caib.notib.document.metadades.por.defecto', 'true',
                                                                                       'Indica si usar valors per defecte quan el document no té metadades ' ||
                                                                                       'o no s''envien metadades en la crida a l''API REST d''alta notificació',
                                                                                       'BOOL', 'DOCUMENTS' )
SELECT 1 FROM DUAL;