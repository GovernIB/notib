ALTER TABLE not_notificacio ADD seguent_remesa VARCHAR2(36 CHAR);
ALTER TABLE not_notificacio ADD num_registre_previ VARCHAR2(50 CHAR);
ALTER TABLE not_notificacio_env ADD registre_motiu VARCHAR2(255 CHAR);
CREATE TABLE not_explot_fet (id NUMBER(38, 0) NOT NULL, pendent NUMBER(38, 0) NOT NULL, reg_env_error NUMBER(38, 0) NOT NULL, registrada NUMBER(38, 0) NOT NULL, reg_acceptada NUMBER(38, 0) NOT NULL, reg_rebutjada NUMBER(38, 0) NOT NULL, not_env_error NUMBER(38, 0) NOT NULL, not_enviada NUMBER(38, 0) NOT NULL, not_notificada NUMBER(38, 0) NOT NULL, not_rebutjada NUMBER(38, 0) NOT NULL, not_expirada NUMBER(38, 0) NOT NULL, cie_env_error NUMBER(38, 0) NOT NULL, cie_enviada NUMBER(38, 0) NOT NULL, cie_notificada NUMBER(38, 0) NOT NULL, cie_rebutjada NUMBER(38, 0) NOT NULL, cie_error NUMBER(38, 0) NOT NULL, processada NUMBER(38, 0) NOT NULL, dimensio_id NUMBER(38, 0) NOT NULL, temps_id NUMBER(38, 0) NOT NULL, CONSTRAINT PK_NOT_EXPLOT_FET PRIMARY KEY (id));
CREATE TABLE not_explot_dim (id NUMBER(38, 0) NOT NULL, entitat_id NUMBER(38, 0) NOT NULL, procediment_id NUMBER(38, 0), organ_codi VARCHAR2(100 CHAR) NOT NULL, usuari_codi VARCHAR2(100 CHAR) NOT NULL, tipus VARCHAR2(16 CHAR), origen VARCHAR2(16 CHAR), CONSTRAINT PK_NOT_EXPLOT_DIM PRIMARY KEY (id));
CREATE TABLE not_explot_temps (id NUMBER(38, 0) NOT NULL, data date NOT NULL, anualitat INTEGER NOT NULL, mes INTEGER NOT NULL, trimestre INTEGER NOT NULL, setmana INTEGER NOT NULL, dia INTEGER NOT NULL, dia_setmana VARCHAR2(2 CHAR), CONSTRAINT PK_NOT_EXPLOT_TEMPS PRIMARY KEY (id));
ALTER TABLE not_explot_fet ADD CONSTRAINT fk_not_explot_fet_dim FOREIGN KEY (dimensio_id) REFERENCES not_explot_dim (id);
ALTER TABLE not_explot_fet ADD CONSTRAINT fk_not_explot_fet_temps FOREIGN KEY (temps_id) REFERENCES not_explot_temps (id);
ALTER TABLE not_explot_dim ADD CONSTRAINT not_explot_dim_uk UNIQUE (entitat_id, procediment_id, organ_codi, usuari_codi, tipus, origen);
ALTER TABLE not_notificacio ADD origen VARCHAR2(8 CHAR);

GRANT SELECT, UPDATE, INSERT, DELETE ON not_explot_temps TO www_notib;
GRANT SELECT, UPDATE, INSERT, DELETE ON not_explot_dim TO www_notib;
GRANT SELECT, UPDATE, INSERT, DELETE ON not_explot_fet TO www_notib;