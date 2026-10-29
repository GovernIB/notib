-- Notib 2.1.1 - Propietats de configuració: subgrups per a les propietats que penjaven directament dels grups
-- GENERAL, SCHEDULLED, FIRMA i ASPECTE (que a més tenen subgrups). El menú de propietats de la interfície React
-- només ha de tenir pares que s'expandeixen i fills amb les propietats. S'executa després de 07_update_2.1.1_ddl.sql.
-- Es mouen també les còpies per entitat (comparteixen el grup). A partir d'ara les propietats noves d'aquests
-- apartats s'han d'afegir als subgrups (GENERAL_COMUNES, SCHEDULLED_COMUNES, FIRMA_SERVIDOR, ASPECTE_COMUNES), no als pares.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-58::limit
INSERT INTO not_CONFIG_GROUP (ID, CODE, PARENT_CODE, PARENT_ID, POSITION, DESCRIPTION) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 'GENERAL_COMUNES', 'GENERAL', (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'GENERAL'), 0, 'Configuracions comunes');
INSERT INTO not_CONFIG_GROUP (ID, CODE, PARENT_CODE, PARENT_ID, POSITION, DESCRIPTION) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 'SCHEDULLED_COMUNES', 'SCHEDULLED', (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'SCHEDULLED'), 0, 'Configuració comuna de les tasques periòdiques');
INSERT INTO not_CONFIG_GROUP (ID, CODE, PARENT_CODE, PARENT_ID, POSITION, DESCRIPTION) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 'FIRMA_SERVIDOR', 'FIRMA', (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'FIRMA'), 0, 'Configuració de la firma en servidor');
INSERT INTO not_CONFIG_GROUP (ID, CODE, PARENT_CODE, PARENT_ID, POSITION, DESCRIPTION) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 'ASPECTE_COMUNES', 'ASPECTE', (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'ASPECTE'), 0, 'Aspecte comú de l''aplicació');

-- Mou les propietats (les globals i les còpies per entitat) als subgrups nous: GROUP_CODE (el que fa servir el codi
-- antic) i CONFIG_GROUP_ID (el que fa servir la interfície React).
UPDATE not_CONFIG SET GROUP_CODE = 'GENERAL_COMUNES', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'GENERAL_COMUNES') WHERE GROUP_CODE = 'GENERAL';
UPDATE not_CONFIG SET GROUP_CODE = 'SCHEDULLED_COMUNES', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'SCHEDULLED_COMUNES') WHERE GROUP_CODE = 'SCHEDULLED';
UPDATE not_CONFIG SET GROUP_CODE = 'FIRMA_SERVIDOR', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'FIRMA_SERVIDOR') WHERE GROUP_CODE = 'FIRMA';
UPDATE not_CONFIG SET GROUP_CODE = 'ASPECTE_COMUNES', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'ASPECTE_COMUNES') WHERE GROUP_CODE = 'ASPECTE';
