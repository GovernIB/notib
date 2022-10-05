ALTER TABLE not_organ_gestor DROP COLUMN estat;

ALTER TABLE not_organ_gestor ADD estat VARCHAR(1) DEFAULT 'V';

ALTER TABLE not_organ_gestor ADD sir BOOLEAN DEFAULT FALSE;

ALTER TABLE not_organ_gestor ADD tipus_transicio VARCHAR(12);

ALTER TABLE not_procediment ADD organ_no_sinc BOOLEAN DEFAULT FALSE;

ALTER TABLE not_entitat ADD data_sincronitzacio TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE not_entitat ADD data_actualitzacio TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE not_avis ADD avis_admin BOOLEAN DEFAULT FALSE;

ALTER TABLE not_avis ADD entitat_id BIGINT;

CREATE TABLE not_og_sinc_rel (antic_og BIGINT NOT NULL, nou_og BIGINT NOT NULL);

ALTER TABLE not_og_sinc_rel ADD CONSTRAINT not_organ_antic_fk FOREIGN KEY (antic_og) REFERENCES not_organ_gestor (id);

ALTER TABLE not_og_sinc_rel ADD CONSTRAINT not_organ_nou_fk FOREIGN KEY (nou_og) REFERENCES not_organ_gestor (id);

ALTER TABLE not_og_sinc_rel ADD CONSTRAINT not_uo_sinc_rel_mult_uk UNIQUE (antic_og, nou_og);