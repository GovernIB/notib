-- CREATE TABLE NOT_OFICINA (codi VARCHAR2(64 CHAR) NOT NULL, nom VARCHAR2(1024 CHAR), sir NUMBER(1) NOT NULL, actiu NUMBER(1) NOT NULL, organ_codi VARCHAR2(16 CHAR), entitat_id NUMBER(38, 0), CONSTRAINT NOT_OFI_PK PRIMARY KEY (codi));
--
-- ALTER TABLE NOT_OFICINA ADD CONSTRAINT NOT_OFI_ORGAN_FK FOREIGN KEY (organ_codi) REFERENCES NOT_ORGAN_GESTOR(CODI);
-- ALTER TABLE NOT_OFICINA ADD CONSTRAINT NOT_OFI_ENTITAT_FK FOREIGN KEY (entitat_id) REFERENCES NOT_ENTITAT(ID);
--
-- GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_OFICINA TO WWW_NOTIB;
--
-- ALTER TABLE not_notificacio_table ADD titular VARCHAR2(1024 CHAR);
-- ALTER TABLE not_notificacio_table ADD notifica_ids VARCHAR2(1024 CHAR);
-- ALTER TABLE not_notificacio_table ADD estat_mask INTEGER;
--
-- -- INDEXOS
-- -- ----------------
--
-- -- NOT_NOTIFICACIO
-- CREATE INDEX not_not_estatrint_idx on NOT_NOTIFICACIO (ESTAT, REGISTRE_ENV_INTENT);
--
-- -- NOT_NOTIFICACIO_ENV
-- CREATE INDEX not_notenv_estat_idx on NOT_NOTIFICACIO_ENV (NOTIFICA_ESTAT);
-- CREATE INDEX not_notenv_regpen_idx on NOT_NOTIFICACIO_ENV (REGISTRE_ESTAT_FINAL, NOTIFICA_ESTAT, SIR_CON_INTENT);
-- CREATE INDEX not_notenv_estatsrint_idx on NOT_NOTIFICACIO_ENV (NOTIFICA_ESTAT_FINAL, NOTIFICA_ESTAT, NOTIFICA_INTENT_NUM);
--
-- -- NOT_NOTIFICACIO_TABLE
-- CREATE INDEX not_nottbl_entitat_idx ON NOT_NOTIFICACIO_TABLE(ENTITAT_ID);
-- CREATE INDEX not_nottbl_entgrup_idx on NOT_NOTIFICACIO_TABLE (ENTITAT_ID, GRUP_CODI);
--
-- -- NOT_NOTIFICACIO_ENV_TABLE
-- CREATE INDEX not_envtbl_entitat_idx ON NOT_NOTIFICACIO_ENV_TABLE(ENTITAT_ID);