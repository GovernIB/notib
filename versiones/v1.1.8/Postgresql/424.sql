-- #424 Permetre definir l'oficina de registre a nivell d'entitat o a nivell d'òrgan gestor
ALTER TABLE NOT_ORGAN_GESTOR ADD OFICINA character varying(255 );
ALTER TABLE NOT_ORGAN_GESTOR ADD OFICINA_NOM character varying(255 );
ALTER TABLE NOT_ENTITAT ADD OFICINA_ENTITAT BIGSERIAL(1) DEFAULT