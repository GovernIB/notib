ALTER TABLE not_organ_gestor ADD codi_pare VARCHAR2(32 CHAR);
ALTER TABLE not_notificacio_massiva ADD num_cancelades NUMBER(38, 0);
ALTER TABLE not_notificacio MODIFY registre_data TIMESTAMP;

ALTER TABLE not_notificacio ADD referencia VARCHAR2(36);
ALTER TABLE not_notificacio ADD CONSTRAINT not_notificacio_ref_uk UNIQUE (referencia);
ALTER TABLE not_notificacio_table ADD referencia VARCHAR2(36);
ALTER TABLE not_notificacio_table ADD CONSTRAINT not_notificacio_table_ref_uk UNIQUE (referencia);
ALTER TABLE not_notificacio_audit ADD referencia VARCHAR2(36);
ALTER TABLE not_notificacio_env MODIFY notifica_ref VARCHAR2(36);
ALTER TABLE not_notificacio_env_table MODIFY notifica_ref VARCHAR2(36);
ALTER TABLE not_notificacio_env_audit MODIFY notifica_ref VARCHAR2(36);
ALTER TABLE not_columnes ADD referencia_notificacio NUMBER(1, 0);

ALTER TABLE not_persona ADD document_tipus VARCHAR2(32 CHAR);
ALTER TABLE not_notificacio_audit MODIFY estat VARCHAR2(32 CHAR);
ALTER TABLE not_notificacio_env ADD per_email NUMBER(1);
ALTER TABLE not_notificacio ADD justificant_creat NUMBER(1);

CREATE TABLE NOT_PROCESSOS_INICIALS (ID NUMBER(19, 0) NOT NULL, CODI VARCHAR2(100 CHAR) NOT NULL, INIT NUMBER(1, 0) NOT NULL, CONSTRAINT NOT_PROCESSOS_INICIALS_PK PRIMARY KEY (ID));