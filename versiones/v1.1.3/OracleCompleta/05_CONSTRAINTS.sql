--------------------------------------------------------
--  Constraints for Table NOT_ACL_CLASS
--------------------------------------------------------

  ALTER TABLE "NOT_ACL_CLASS" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_CLASS" MODIFY ("CLASS" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table NOT_ACL_ENTRY
--------------------------------------------------------

  ALTER TABLE "NOT_ACL_ENTRY" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_ENTRY" MODIFY ("ACL_OBJECT_IDENTITY" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_ENTRY" MODIFY ("ACE_ORDER" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_ENTRY" MODIFY ("SID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_ENTRY" MODIFY ("MASK" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_ENTRY" MODIFY ("GRANTING" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_ENTRY" MODIFY ("AUDIT_SUCCESS" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_ENTRY" MODIFY ("AUDIT_FAILURE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table NOT_ACL_OBJECT_IDENTITY
--------------------------------------------------------

  ALTER TABLE "NOT_ACL_OBJECT_IDENTITY" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_OBJECT_IDENTITY" MODIFY ("OBJECT_ID_CLASS" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_OBJECT_IDENTITY" MODIFY ("OBJECT_ID_IDENTITY" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_OBJECT_IDENTITY" MODIFY ("OWNER_SID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_OBJECT_IDENTITY" MODIFY ("ENTRIES_INHERITING" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table NOT_ACL_SID
--------------------------------------------------------

  ALTER TABLE "NOT_ACL_SID" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_SID" MODIFY ("PRINCIPAL" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ACL_SID" MODIFY ("SID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table NOT_APLICACIO
--------------------------------------------------------

  ALTER TABLE "NOT_APLICACIO" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_APLICACIO" MODIFY ("ENTITAT_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_APLICACIO" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_COLUMNES
--------------------------------------------------------

  ALTER TABLE "NOT_COLUMNES" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_COLUMNES" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_DOCUMENT
--------------------------------------------------------

  ALTER TABLE "NOT_DOCUMENT" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_DOCUMENT" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_ENTITAT
--------------------------------------------------------

  ALTER TABLE "NOT_ENTITAT" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT" MODIFY ("ACTIVA" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT" MODIFY ("AMB_ENTREGA_CIE" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT" MODIFY ("AMB_ENTREGA_DEH" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT" MODIFY ("API_KEY" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT" MODIFY ("CODI" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT" MODIFY ("DIR3_CODI" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT" MODIFY ("NOM" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT" MODIFY ("TIPUS" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT" MODIFY ("VERSION" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT" ADD PRIMARY KEY ("ID") ENABLE;
 
  ALTER TABLE "NOT_ENTITAT" ADD UNIQUE ("CODI") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_ENTITAT_TIPUS_DOC
--------------------------------------------------------

  ALTER TABLE "NOT_ENTITAT_TIPUS_DOC" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ENTITAT_TIPUS_DOC" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_FORMATS_FULLA
--------------------------------------------------------

  ALTER TABLE "NOT_FORMATS_FULLA" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_FORMATS_FULLA" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_FORMATS_SOBRE
--------------------------------------------------------

  ALTER TABLE "NOT_FORMATS_SOBRE" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_FORMATS_SOBRE" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_GRUP
--------------------------------------------------------

  ALTER TABLE "NOT_GRUP" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_GRUP" MODIFY ("CODI" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_GRUP" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_NOTIFICACIO
--------------------------------------------------------

  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("COM_TIPUS" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("CONCEPTE" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("EMISOR_DIR3CODI" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("ENV_TIPUS" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("ESTAT" NOT NULL ENABLE);
  
  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("PROC_CODI_NOTIB" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("USUARI_CODI" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("DOCUMENT_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("ENTITAT_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO" MODIFY ("PROCEDIMENT_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_NOTIFICACIO_ENV
--------------------------------------------------------

  ALTER TABLE "NOT_NOTIFICACIO_ENV" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO_ENV" MODIFY ("NOTIFICA_ERROR" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO_ENV" MODIFY ("NOTIFICA_ESTAT" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO_ENV" MODIFY ("TITULAR_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO_ENV" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_NOTIFICACIO_EVENT
--------------------------------------------------------

  ALTER TABLE "NOT_NOTIFICACIO_EVENT" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO_EVENT" MODIFY ("DATA" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO_EVENT" MODIFY ("ERROR" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO_EVENT" MODIFY ("TIPUS" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_NOTIFICACIO_EVENT" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_PAGADOR_CIE
--------------------------------------------------------

  ALTER TABLE "NOT_PAGADOR_CIE" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_PAGADOR_CIE" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_PAGADOR_POSTAL
--------------------------------------------------------

  ALTER TABLE "NOT_PAGADOR_POSTAL" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_PAGADOR_POSTAL" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_PERSONA
--------------------------------------------------------

  ALTER TABLE "NOT_PERSONA" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_PERSONA" MODIFY ("INTERESSATTIPUS" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_PERSONA" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_PROCEDIMENT
--------------------------------------------------------

  ALTER TABLE "NOT_PROCEDIMENT" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_PROCEDIMENT" MODIFY ("AGRUPAR" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_PROCEDIMENT" MODIFY ("CODI" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_PROCEDIMENT" MODIFY ("NOM" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_PROCEDIMENT" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_PRO_GRUP
--------------------------------------------------------

  ALTER TABLE "NOT_PRO_GRUP" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_PRO_GRUP" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table NOT_USUARI
--------------------------------------------------------

  ALTER TABLE "NOT_USUARI" MODIFY ("CODI" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_USUARI" MODIFY ("VERSION" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_USUARI" ADD PRIMARY KEY ("CODI") ENABLE;
  --------------------------------------------------------
--  Constraints for Table NOT_ORGAN_GESTOR
--------------------------------------------------------

  ALTER TABLE "NOT_ORGAN_GESTOR" ADD CONSTRAINT "NOT_ORGAN_GESTOR_PK" PRIMARY KEY ("ID") ENABLE;
 
  ALTER TABLE "NOT_ORGAN_GESTOR" ADD CONSTRAINT "NOT_ORGAN_GESTOR_UK" UNIQUE ("CODI") ENABLE;
 
  ALTER TABLE "NOT_ORGAN_GESTOR" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "NOT_ORGAN_GESTOR" MODIFY ("CODI" NOT NULL ENABLE);
