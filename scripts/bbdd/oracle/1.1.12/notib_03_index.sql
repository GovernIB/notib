  CREATE INDEX NOT_APLICACIO_AUDIT_EK ON NOT_APLICACIO_AUDIT (APLICACIO_ID);


  CREATE UNIQUE INDEX NOT_APLICACIO_AUDIT_PK ON NOT_APLICACIO_AUDIT (ID);


  CREATE UNIQUE INDEX NOT_APLICACIO_PK ON NOT_APLICACIO (ID);


  CREATE INDEX NOT_AVIS_DATA_FINAL_I ON NOT_AVIS (DATA_FINAL);


  CREATE INDEX NOT_AVIS_DATA_INICI_I ON NOT_AVIS (DATA_INICI);


  CREATE UNIQUE INDEX NOT_AVIS_PK ON NOT_AVIS (ID);


  CREATE UNIQUE INDEX NOT_COLUMNES_PK ON NOT_COLUMNES (ID);


  CREATE UNIQUE INDEX NOT_CONFIG_GROUP_PK ON NOT_CONFIG_GROUP (CODE);


  CREATE UNIQUE INDEX NOT_CONFIG_PK ON NOT_CONFIG (KEY);


  CREATE UNIQUE INDEX NOT_CONFIG_TYPE_PK ON NOT_CONFIG_TYPE (CODE);


  CREATE UNIQUE INDEX NOT_DOCUMENT_PK ON NOT_DOCUMENT (ID);


  CREATE INDEX NOT_ENTITAT_AUDIT_EK ON NOT_ENTITAT_AUDIT (ENTITAT_ID);


  CREATE UNIQUE INDEX NOT_ENTITAT_AUDIT_PK ON NOT_ENTITAT_AUDIT (ID);


  CREATE INDEX NOT_ENTITAT_EK ON NOT_NOTIFICACIO_MASSIVA (ENTITAT_ID);


  CREATE INDEX NOT_ENTITAT_ENTREGA_CIE_EK ON NOT_ENTITAT (ENTREGA_CIE_ID);


  CREATE UNIQUE INDEX NOT_ENTITAT_PK ON NOT_ENTITAT (ID);


  CREATE UNIQUE INDEX NOT_ENTITAT_TIPUS_DOC_PK ON NOT_ENTITAT_TIPUS_DOC (ID);


  CREATE UNIQUE INDEX NOT_ENTITAT_UK ON NOT_ENTITAT (CODI);


  CREATE INDEX NOT_ENTREGA_CIE_CIE_EK ON NOT_ENTREGA_CIE (CIE_ID);


  CREATE INDEX NOT_ENTREGA_CIE_OPERADOR_EK ON NOT_ENTREGA_CIE (OPERADOR_POSTAL_ID);


  CREATE UNIQUE INDEX NOT_ENTREGA_CIE_PK ON NOT_ENTREGA_CIE (ID);


  CREATE UNIQUE INDEX NOT_ENTREGA_POSTAL_PK ON NOT_ENTREGA_POSTAL (ID);


  CREATE INDEX NOT_ENV_ENTREGA_POSTAL_EK ON NOT_NOTIFICACIO_ENV (ENTREGA_POSTAL_ID);


  CREATE UNIQUE INDEX NOT_FORMATS_FULLA_PK ON NOT_FORMATS_FULLA (ID);


  CREATE UNIQUE INDEX NOT_FORMATS_SOBRE_PK ON NOT_FORMATS_SOBRE (ID);


  CREATE INDEX NOT_GRUP_AUDIT_EK ON NOT_GRUP_AUDIT (GRUP_ID);


  CREATE UNIQUE INDEX NOT_GRUP_AUDIT_PK ON NOT_GRUP_AUDIT (ID);


  CREATE UNIQUE INDEX NOT_GRUP_PK ON NOT_GRUP (ID);


  CREATE UNIQUE INDEX NOT_HIST_ENVI_OID_PK ON NOT_HIST_ENVIAMENTS (ID);


  CREATE UNIQUE INDEX NOT_HIST_NOTIF_OID_PK ON NOT_HIST_NOTIF (ID);


  CREATE UNIQUE INDEX NOT_HIST_ORGAN_OID_PK ON NOT_HIST_ORGAN (ID);


  CREATE UNIQUE INDEX NOT_HIST_PROC_OID_PK ON NOT_HIST_PROCEDIMENT (ID);


  CREATE INDEX NOT_NOTIF_ESTAT_I ON NOT_NOTIFICACIO (ESTAT);


  CREATE INDEX NOT_NOTIFICACIO_AUDIT_EK ON NOT_NOTIFICACIO_AUDIT (NOTIFICACIO_ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_AUDIT_PK ON NOT_NOTIFICACIO_AUDIT (ID);


  CREATE INDEX NOT_NOTIFICACIO_ENV_AUDIT_EK ON NOT_NOTIFICACIO_ENV_AUDIT (ENVIAMENT_ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_ENV_AUDIT_PK ON NOT_NOTIFICACIO_ENV_AUDIT (ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_ENV_PK ON NOT_NOTIFICACIO_ENV (ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_EVENT_PK ON NOT_NOTIFICACIO_EVENT (ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_MASSIVA_PK ON NOT_NOTIFICACIO_MASSIVA (ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_PK ON NOT_NOTIFICACIO (ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_TABLE_PK ON NOT_NOTIFICACIO_TABLE (ID);


  CREATE INDEX NOT_NOTIF_ORGAN_FK_I ON NOT_NOTIFICACIO (ORGAN_GESTOR);


  CREATE INDEX NOT_NOTIF_PROCEDIMENT_FK_I ON NOT_NOTIFICACIO (PROCEDIMENT_ID);


  CREATE INDEX NOT_NOTIF_PROCOD_I ON NOT_NOTIFICACIO (PROC_CODI_NOTIB);


  CREATE INDEX NOT_NOTIF_PROORGAN_FK_I ON NOT_NOTIFICACIO (PROCEDIMENT_ORGAN_ID);


  CREATE INDEX NOT_NOTIF_USUARI_I ON NOT_NOTIFICACIO (USUARI_CODI);


  CREATE INDEX NOT_NOT_TABLE_NOT_MASSIVA_EK ON NOT_NOTIFICACIO_TABLE (NOTIFICACIO_MASSIVA_ID);


  CREATE INDEX NOT_ORGAN_EK ON NOT_PRO_ORGAN (ORGANGESTOR_ID);


  CREATE INDEX NOT_ORGAN_ENTITAT_FK_I ON NOT_ORGAN_GESTOR (ENTITAT);


  CREATE INDEX NOT_ORGAN_ENTREGA_CIE_EK ON NOT_ORGAN_GESTOR (ENTREGA_CIE_ID);


  CREATE UNIQUE INDEX NOT_ORGAN_GESTOR_PK ON NOT_ORGAN_GESTOR (ID);


  CREATE UNIQUE INDEX NOT_ORGAN_GESTOR_UK ON NOT_ORGAN_GESTOR (CODI);


  CREATE UNIQUE INDEX NOT_PAGADOR_CIE_PK ON NOT_PAGADOR_CIE (ID);


  CREATE INDEX NOT_PAGADOR_EK ON NOT_NOTIFICACIO_MASSIVA (PAGADOR_POSTAL_ID);


  CREATE UNIQUE INDEX NOT_PAGADOR_POSTAL_PK ON NOT_PAGADOR_POSTAL (ID);


  CREATE UNIQUE INDEX NOT_PERSONA_PK ON NOT_PERSONA (ID);


  CREATE INDEX NOT_PROCEDIMENT_AUDIT_EK ON NOT_PROCEDIMENT_AUDIT (PROCEDIMENT_ID);


  CREATE UNIQUE INDEX NOT_PROCEDIMENT_AUDIT_PK ON NOT_PROCEDIMENT_AUDIT (ID);


  CREATE INDEX NOT_PROCEDIMENT_EK ON NOT_PRO_ORGAN (PROCEDIMENT_ID);


  CREATE INDEX NOT_PROCEDIMENT_ENTREGA_CIE_EK ON NOT_PROCEDIMENT (ENTREGA_CIE_ID);


  CREATE UNIQUE INDEX NOT_PROCEDIMENT_PK ON NOT_PROCEDIMENT (ID);


  CREATE INDEX NOT_PRO_GRUP_AUDIT_EK ON NOT_PRO_GRUP_AUDIT (PROGRUP_ID);


  CREATE UNIQUE INDEX NOT_PRO_GRUP_AUDIT_PK ON NOT_PRO_GRUP_AUDIT (ID);


  CREATE UNIQUE INDEX NOT_PRO_GRUP_PK ON NOT_PRO_GRUP (ID);


  CREATE UNIQUE INDEX NOT_PRO_ORGAN_PK ON NOT_PRO_ORGAN (ID);


  CREATE UNIQUE INDEX NOT_USUARI_UK ON NOT_USUARI (CODI);


  CREATE UNIQUE INDEX NOT_APLICACIO_PK ON NOT_APLICACIO (ID);


  CREATE UNIQUE INDEX NOT_APLICACIO_AUDIT_PK ON NOT_APLICACIO_AUDIT (ID);


  CREATE INDEX NOT_APLICACIO_AUDIT_EK ON NOT_APLICACIO_AUDIT (APLICACIO_ID);


  CREATE UNIQUE INDEX NOT_AVIS_PK ON NOT_AVIS (ID);


  CREATE INDEX NOT_AVIS_DATA_INICI_I ON NOT_AVIS (DATA_INICI);


  CREATE INDEX NOT_AVIS_DATA_FINAL_I ON NOT_AVIS (DATA_FINAL);


  CREATE UNIQUE INDEX NOT_COLUMNES_PK ON NOT_COLUMNES (ID);


  CREATE UNIQUE INDEX NOT_CONFIG_PK ON NOT_CONFIG (KEY);


  CREATE UNIQUE INDEX NOT_CONFIG_GROUP_PK ON NOT_CONFIG_GROUP (CODE);


  CREATE UNIQUE INDEX NOT_CONFIG_TYPE_PK ON NOT_CONFIG_TYPE (CODE);


  CREATE UNIQUE INDEX NOT_DOCUMENT_PK ON NOT_DOCUMENT (ID);


  CREATE UNIQUE INDEX NOT_ENTITAT_PK ON NOT_ENTITAT (ID);


  CREATE UNIQUE INDEX NOT_ENTITAT_UK ON NOT_ENTITAT (CODI);


  CREATE INDEX NOT_ENTITAT_ENTREGA_CIE_EK ON NOT_ENTITAT (ENTREGA_CIE_ID);


  CREATE UNIQUE INDEX NOT_ENTITAT_AUDIT_PK ON NOT_ENTITAT_AUDIT (ID);


  CREATE INDEX NOT_ENTITAT_AUDIT_EK ON NOT_ENTITAT_AUDIT (ENTITAT_ID);


  CREATE UNIQUE INDEX NOT_ENTITAT_TIPUS_DOC_PK ON NOT_ENTITAT_TIPUS_DOC (ID);


  CREATE UNIQUE INDEX NOT_ENTREGA_CIE_PK ON NOT_ENTREGA_CIE (ID);


  CREATE INDEX NOT_ENTREGA_CIE_CIE_EK ON NOT_ENTREGA_CIE (CIE_ID);


  CREATE INDEX NOT_ENTREGA_CIE_OPERADOR_EK ON NOT_ENTREGA_CIE (OPERADOR_POSTAL_ID);


  CREATE UNIQUE INDEX NOT_ENTREGA_POSTAL_PK ON NOT_ENTREGA_POSTAL (ID);


  CREATE UNIQUE INDEX NOT_FORMATS_FULLA_PK ON NOT_FORMATS_FULLA (ID);


  CREATE UNIQUE INDEX NOT_FORMATS_SOBRE_PK ON NOT_FORMATS_SOBRE (ID);


  CREATE UNIQUE INDEX NOT_GRUP_PK ON NOT_GRUP (ID);


  CREATE UNIQUE INDEX NOT_GRUP_AUDIT_PK ON NOT_GRUP_AUDIT (ID);


  CREATE INDEX NOT_GRUP_AUDIT_EK ON NOT_GRUP_AUDIT (GRUP_ID);


  CREATE UNIQUE INDEX NOT_HIST_ENVI_OID_PK ON NOT_HIST_ENVIAMENTS (ID);


  CREATE UNIQUE INDEX NOT_HIST_NOTIF_OID_PK ON NOT_HIST_NOTIF (ID);


  CREATE UNIQUE INDEX NOT_HIST_ORGAN_OID_PK ON NOT_HIST_ORGAN (ID);


  CREATE UNIQUE INDEX NOT_HIST_PROC_OID_PK ON NOT_HIST_PROCEDIMENT (ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_PK ON NOT_NOTIFICACIO (ID);


  CREATE INDEX NOT_NOTIF_ORGAN_FK_I ON NOT_NOTIFICACIO (ORGAN_GESTOR);


  CREATE INDEX NOT_NOTIF_PROORGAN_FK_I ON NOT_NOTIFICACIO (PROCEDIMENT_ORGAN_ID);


  CREATE INDEX NOT_NOTIF_PROCEDIMENT_FK_I ON NOT_NOTIFICACIO (PROCEDIMENT_ID);


  CREATE INDEX NOT_NOTIF_ESTAT_I ON NOT_NOTIFICACIO (ESTAT);


  CREATE INDEX NOT_NOTIF_USUARI_I ON NOT_NOTIFICACIO (USUARI_CODI);


  CREATE INDEX NOT_NOTIF_PROCOD_I ON NOT_NOTIFICACIO (PROC_CODI_NOTIB);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_AUDIT_PK ON NOT_NOTIFICACIO_AUDIT (ID);


  CREATE INDEX NOT_NOTIFICACIO_AUDIT_EK ON NOT_NOTIFICACIO_AUDIT (NOTIFICACIO_ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_ENV_PK ON NOT_NOTIFICACIO_ENV (ID);


  CREATE INDEX NOT_ENV_ENTREGA_POSTAL_EK ON NOT_NOTIFICACIO_ENV (ENTREGA_POSTAL_ID);


  CREATE INDEX NOT_NOTIFICACIO_ENV_AUDIT_EK ON NOT_NOTIFICACIO_ENV_AUDIT (ENVIAMENT_ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_ENV_AUDIT_PK ON NOT_NOTIFICACIO_ENV_AUDIT (ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_EVENT_PK ON NOT_NOTIFICACIO_EVENT (ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_MASSIVA_PK ON NOT_NOTIFICACIO_MASSIVA (ID);


  CREATE INDEX NOT_PAGADOR_EK ON NOT_NOTIFICACIO_MASSIVA (PAGADOR_POSTAL_ID);


  CREATE INDEX NOT_ENTITAT_EK ON NOT_NOTIFICACIO_MASSIVA (ENTITAT_ID);


  CREATE INDEX NOT_NOT_TABLE_NOT_MASSIVA_EK ON NOT_NOTIFICACIO_TABLE (NOTIFICACIO_MASSIVA_ID);


  CREATE UNIQUE INDEX NOT_NOTIFICACIO_TABLE_PK ON NOT_NOTIFICACIO_TABLE (ID);


  CREATE UNIQUE INDEX NOT_ORGAN_GESTOR_PK ON NOT_ORGAN_GESTOR (ID);


  CREATE UNIQUE INDEX NOT_ORGAN_GESTOR_UK ON NOT_ORGAN_GESTOR (CODI);


  CREATE INDEX NOT_ORGAN_ENTITAT_FK_I ON NOT_ORGAN_GESTOR (ENTITAT);


  CREATE INDEX NOT_ORGAN_ENTREGA_CIE_EK ON NOT_ORGAN_GESTOR (ENTREGA_CIE_ID);


  CREATE UNIQUE INDEX NOT_PAGADOR_CIE_PK ON NOT_PAGADOR_CIE (ID);


  CREATE UNIQUE INDEX NOT_PAGADOR_POSTAL_PK ON NOT_PAGADOR_POSTAL (ID);


  CREATE UNIQUE INDEX NOT_PERSONA_PK ON NOT_PERSONA (ID);


  CREATE UNIQUE INDEX NOT_PROCEDIMENT_PK ON NOT_PROCEDIMENT (ID);


  CREATE INDEX NOT_PROCEDIMENT_ENTREGA_CIE_EK ON NOT_PROCEDIMENT (ENTREGA_CIE_ID);


  CREATE UNIQUE INDEX NOT_PROCEDIMENT_AUDIT_PK ON NOT_PROCEDIMENT_AUDIT (ID);


  CREATE INDEX NOT_PROCEDIMENT_AUDIT_EK ON NOT_PROCEDIMENT_AUDIT (PROCEDIMENT_ID);


  CREATE UNIQUE INDEX NOT_PRO_GRUP_PK ON NOT_PRO_GRUP (ID);


  CREATE UNIQUE INDEX NOT_PRO_GRUP_AUDIT_PK ON NOT_PRO_GRUP_AUDIT (ID);


  CREATE INDEX NOT_PRO_GRUP_AUDIT_EK ON NOT_PRO_GRUP_AUDIT (PROGRUP_ID);


  CREATE UNIQUE INDEX NOT_PRO_ORGAN_PK ON NOT_PRO_ORGAN (ID);


  CREATE INDEX NOT_ORGAN_EK ON NOT_PRO_ORGAN (ORGANGESTOR_ID);


  CREATE INDEX NOT_PROCEDIMENT_EK ON NOT_PRO_ORGAN (PROCEDIMENT_ID);


  CREATE UNIQUE INDEX NOT_USUARI_UK ON NOT_USUARI (CODI);