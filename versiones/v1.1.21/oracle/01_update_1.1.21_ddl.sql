ALTER TABLE not_organ_gestor DROP COLUMN estat;
ALTER TABLE not_organ_gestor ADD estat VARCHAR2(1 CHAR) DEFAULT 'V';
ALTER TABLE not_organ_gestor ADD sir NUMBER(1) DEFAULT '0';
ALTER TABLE not_organ_gestor ADD tipus_transicio VARCHAR2(12 CHAR);

ALTER TABLE not_procediment ADD organ_no_sinc NUMBER(1) DEFAULT '0';
ALTER TABLE not_procediment ADD actiu NUMBER(1) DEFAULT '1';

ALTER TABLE not_entitat ADD data_sincronitzacio TIMESTAMP;
ALTER TABLE not_entitat ADD data_actualitzacio TIMESTAMP;

ALTER TABLE not_avis ADD avis_admin NUMBER(1) DEFAULT '0';
ALTER TABLE not_avis ADD entitat_id NUMBER(38, 0);

CREATE TABLE not_og_sinc_rel (antic_og NUMBER(38, 0) NOT NULL, nou_og NUMBER(38, 0) NOT NULL);
ALTER TABLE not_og_sinc_rel ADD CONSTRAINT not_organ_antic_fk FOREIGN KEY (antic_og) REFERENCES not_organ_gestor (id);
ALTER TABLE not_og_sinc_rel ADD CONSTRAINT not_organ_nou_fk FOREIGN KEY (nou_og) REFERENCES not_organ_gestor (id);
ALTER TABLE not_og_sinc_rel ADD CONSTRAINT not_uo_sinc_rel_mult_uk UNIQUE (antic_og, nou_og);

grant select, update, insert, delete on not_og_sinc_rel to www_notib;