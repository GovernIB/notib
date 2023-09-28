CREATE UNIQUE INDEX "NOTIB"."NOT_NOTIFICACIO_PK" ON "NOTIB"."NOT_NOTIFICACIO" ("ID";

CREATE INDEX "NOTIB"."NOT_PAGADOR_EK" ON "NOTIB"."NOT_NOTIFICACIO_MASSIVA" ("PAGADOR_POSTAL_ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_ENTITAT_AUDIT_PK" ON "NOTIB"."NOT_ENTITAT_AUDIT" ("ID");

CREATE INDEX "NOTIB"."NOT_ENVTBL_ENTITAT_IDX" ON "NOTIB"."NOT_NOTIFICACIO_ENV_TABLE" ("ENTITAT_ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_PRO_ORGAN_PK" ON "NOTIB"."NOT_PRO_ORGAN" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_ENTITAT_PK" ON "NOTIB"."NOT_ENTITAT" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_GRUP_AUDIT_PK" ON "NOTIB"."NOT_GRUP_AUDIT" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_PERSONA_PK" ON "NOTIB"."NOT_PERSONA" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_HIST_NOTIF_OID_PK" ON "NOTIB"."NOT_HIST_NOTIF" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_ORGAN_GESTOR_UK" ON "NOTIB"."NOT_ORGAN_GESTOR" ("CODI");

CREATE UNIQUE INDEX "NOTIB"."NOT_FORMATS_FULLA_PK" ON "NOTIB"."NOT_FORMATS_FULLA" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_UO_SINC_REL_MULT_UK" ON "NOTIB"."NOT_OG_SINC_REL" ("ANTIC_OG", "NOU_OG");

CREATE UNIQUE INDEX "NOTIB"."NOT_PRO_GRUP_AUDIT_PK" ON "NOTIB"."NOT_PRO_GRUP_AUDIT" ("ID");

CREATE INDEX "NOTIB"."NOT_ENTREGA_CIE_OPERADOR_EK" ON "NOTIB"."NOT_ENTREGA_CIE" ("OPERADOR_POSTAL_ID");

CREATE INDEX "NOTIB"."NOT_NOTIF_ESTAT_I" ON "NOTIB"."NOT_NOTIFICACIO" ("ESTAT");

CREATE UNIQUE INDEX "NOTIB"."NOT_NOTIFICACIO_AUDIT_PK" ON "NOTIB"."NOT_NOTIFICACIO_AUDIT" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_NOTIFICACIO_ENV_PK" ON "NOTIB"."NOT_NOTIFICACIO_ENV" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_ENTITAT_TIPUS_DOC_PK" ON "NOTIB"."NOT_ENTITAT_TIPUS_DOC" ("ID");

CREATE INDEX "NOTIB"."NOT_ENTITAT_ENTREGA_CIE_EK" ON "NOTIB"."NOT_ENTITAT" ("ENTREGA_CIE_ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_ORGAN_GESTOR_PK" ON "NOTIB"."NOT_ORGAN_GESTOR" ("ID");

CREATE INDEX "NOTIB"."NOT_APLICACIO_AUDIT_EK" ON "NOTIB"."NOT_APLICACIO_AUDIT" ("APLICACIO_ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_APLICACIO_AUDIT_PK" ON "NOTIB"."NOT_APLICACIO_AUDIT" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_CONFIG_TYPE_PK" ON "NOTIB"."NOT_CONFIG_TYPE" ("CODE");

CREATE UNIQUE INDEX "NOTIB"."NOT_PAGADOR_POSTAL_PK" ON "NOTIB"."NOT_PAGADOR_POSTAL" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_CONFIG_PK" ON "NOTIB"."NOT_CONFIG" ("KEY");

CREATE UNIQUE INDEX "NOTIB"."NOT_NOTIFICACIO_TABLE_PK" ON "NOTIB"."NOT_NOTIFICACIO_TABLE" ("ID");

CREATE INDEX "NOTIB"."NOT_NOTIFICACIO_AUDIT_EK" ON "NOTIB"."NOT_NOTIFICACIO_AUDIT" ("NOTIFICACIO_ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_ENTREGA_CIE_PK" ON "NOTIB"."NOT_ENTREGA_CIE" ("ID");

CREATE INDEX "NOTIB"."NOT_NOTIF_PROORGAN_FK_I" ON "NOTIB"."NOT_NOTIFICACIO" ("PROCEDIMENT_ORGAN_ID");

CREATE INDEX "NOTIB"."NOT_NOTIF_ORGAN_FK_I" ON "NOTIB"."NOT_NOTIFICACIO" ("ORGAN_GESTOR");

CREATE UNIQUE INDEX "NOTIB"."NOT_DOCUMENT_PK" ON "NOTIB"."NOT_DOCUMENT" ("ID");

CREATE INDEX "NOTIB"."NOT_NOTIF_PROCEDIMENT_FK_I" ON "NOTIB"."NOT_NOTIFICACIO" ("PROCEDIMENT_ID");

CREATE INDEX "NOTIB"."NOT_PROCEDIMENT_EK" ON "NOTIB"."NOT_PRO_ORGAN" ("PROCEDIMENT_ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_PAGADOR_CIE_PK" ON "NOTIB"."NOT_PAGADOR_CIE" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_ENTREGA_POSTAL_PK" ON "NOTIB"."NOT_ENTREGA_POSTAL" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_PROCEDIMENT_AUDIT_PK" ON "NOTIB"."NOT_PROCEDIMENT_AUDIT" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_GRUP_PK" ON "NOTIB"."NOT_GRUP" ("ID");

CREATE INDEX "NOTIB"."NOT_ORGAN_ENTITAT_FK_I" ON "NOTIB"."NOT_ORGAN_GESTOR" ("ENTITAT");

CREATE INDEX "NOTIB"."NOT_ENTREGA_CIE_CIE_EK" ON "NOTIB"."NOT_ENTREGA_CIE" ("CIE_ID");

CREATE INDEX "NOTIB"."NOT_TABLE_ENTITAT_INDEX" ON "NOTIB"."NOT_NOTIFICACIO_TABLE" ("ENTITAT_ID");

CREATE INDEX "NOTIB"."NOT_MONINT_CODI_IDX" ON "NOTIB"."NOT_MON_INT" ("CODI");

CREATE UNIQUE INDEX "NOTIB"."NOT_APLICACIO_PK" ON "NOTIB"."NOT_APLICACIO" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_COLUMNES_PK" ON "NOTIB"."NOT_COLUMNES" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_CALLBACK_PK" ON "NOTIB"."NOT_CALLBACK" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_MON_INT_PARAM_PK" ON "NOTIB"."NOT_MON_INT_PARAM" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_HIST_PROC_OID_PK" ON "NOTIB"."NOT_HIST_PROCEDIMENT" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_OFI_PK" ON "NOTIB"."NOT_OFICINA" ("CODI");

CREATE UNIQUE INDEX "NOTIB"."NOT_PRO_GRUP_PK" ON "NOTIB"."NOT_PRO_GRUP" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_NOTIFICACIO_UN" ON "NOTIB"."NOT_NOTIFICACIO" ("REFERENCIA");

CREATE UNIQUE INDEX "NOTIB"."NOT_FORMATS_SOBRE_PK" ON "NOTIB"."NOT_FORMATS_SOBRE" ("ID");

CREATE INDEX "NOTIB"."NOT_PROCEDIMENT_ENTREGA_CIE_EK" ON "NOTIB"."NOT_PROCEDIMENT" ("ENTREGA_CIE_ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_PROCESSOS_INICIALS_PK" ON "NOTIB"."NOT_PROCESSOS_INICIALS" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_NOTIFICACIO_EVENT_PK" ON "NOTIB"."NOT_NOTIFICACIO_EVENT" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_NOTIFICACIO_MASSIVA_PK" ON "NOTIB"."NOT_NOTIFICACIO_MASSIVA" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_CONFIG_GROUP_PK" ON "NOTIB"."NOT_CONFIG_GROUP" ("CODE");

CREATE UNIQUE INDEX "NOTIB"."NOT_NOTIFICACIO_TABLE_UN" ON "NOTIB"."NOT_NOTIFICACIO_TABLE" ("REFERENCIA");

CREATE INDEX "NOTIB"."NOT_ENV_ENTREGA_POSTAL_EK" ON "NOTIB"."NOT_NOTIFICACIO_ENV" ("ENTREGA_POSTAL_ID");

CREATE INDEX "NOTIB"."NOT_PRO_GRUP_AUDIT_EK" ON "NOTIB"."NOT_PRO_GRUP_AUDIT" ("PROGRUP_ID");

CREATE INDEX "NOTIB"."NOT_AVIS_DATA_FINAL_I" ON "NOTIB"."NOT_AVIS" ("DATA_FINAL");

CREATE INDEX "NOTIB"."NOT_NOTIFICACIO_ENV_AUDIT_EK" ON "NOTIB"."NOT_NOTIFICACIO_ENV_AUDIT" ("ENVIAMENT_ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_MON_INT_PK" ON "NOTIB"."NOT_MON_INT" ("ID");

CREATE INDEX "NOTIB"."NOT_AVIS_DATA_INICI_I" ON "NOTIB"."NOT_AVIS" ("DATA_INICI");

CREATE UNIQUE INDEX "NOTIB"."NOT_HIST_ENVI_OID_PK" ON "NOTIB"."NOT_HIST_ENVIAMENTS" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_USUARI_UK" ON "NOTIB"."NOT_USUARI" ("CODI");

CREATE UNIQUE INDEX "NOTIB"."NOT_NOTIFICACIO_ENV_AUDIT_PK" ON "NOTIB"."NOT_NOTIFICACIO_ENV_AUDIT" ("ID");

CREATE INDEX "NOTIB"."NOT_ENTITAT_EK" ON "NOTIB"."NOT_NOTIFICACIO_MASSIVA" ("ENTITAT_ID");

CREATE INDEX "NOTIB"."NOT_MONINT_DATA_IDX" ON "NOTIB"."NOT_MON_INT" ("DATA");

CREATE UNIQUE INDEX "NOTIB"."NOT_HIST_ORGAN_OID_PK" ON "NOTIB"."NOT_HIST_ORGAN" ("ID");

CREATE INDEX "NOTIB"."NOT_NOTIFICACIO_EVENT" ON "NOTIB"."NOT_NOTIFICACIO_EVENT" ("NOTIFICACIO_ID");

CREATE INDEX "NOTIB"."NOT_PROCEDIMENT_AUDIT_EK" ON "NOTIB"."NOT_PROCEDIMENT_AUDIT" ("PROCEDIMENT_ID");

CREATE INDEX "NOTIB"."NOT_NOTIF_PROCOD_I" ON "NOTIB"."NOT_NOTIFICACIO" ("PROC_CODI_NOTIB");

CREATE INDEX "NOTIB"."NOT_TABLE_ENV_INDEX" ON "NOTIB"."NOT_NOTIFICACIO_ENV_TABLE" ("NOTIFICACIO_ID");

CREATE INDEX "NOTIB"."NOT_GRUP_AUDIT_EK" ON "NOTIB"."NOT_GRUP_AUDIT" ("GRUP_ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_PROCEDIMENT_PK" ON "NOTIB"."NOT_PROCEDIMENT" ("ID");

CREATE UNIQUE INDEX "NOTIB"."NOT_ENTITAT_UK" ON "NOTIB"."NOT_ENTITAT" ("CODI");

CREATE UNIQUE INDEX "NOTIB"."NOT_AVIS_PK" ON "NOTIB"."NOT_AVIS" ("ID");

CREATE INDEX "NOTIB"."NOT_NOT_TABLE_NOT_MASSIVA_EK" ON "NOTIB"."NOT_NOTIFICACIO_TABLE" ("NOTIFICACIO_MASSIVA_ID");

CREATE INDEX "NOTIB"."NOT_ORGAN_EK" ON "NOTIB"."NOT_PRO_ORGAN" ("ORGANGESTOR_ID");

CREATE INDEX "NOTIB"."NOT_ENTITAT_AUDIT_EK" ON "NOTIB"."NOT_ENTITAT_AUDIT" ("ENTITAT_ID");

CREATE INDEX "NOTIB"."NOT_NOTIF_USUARI_I" ON "NOTIB"."NOT_NOTIFICACIO" ("USUARI_CODI");

CREATE INDEX "NOTIB"."NOT_ORGAN_ENTREGA_CIE_EK" ON "NOTIB"."NOT_ORGAN_GESTOR" ("ENTREGA_CIE_ID");