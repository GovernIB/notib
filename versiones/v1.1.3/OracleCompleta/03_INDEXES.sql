--------------------------------------------------------
--  DDL for Index SYS_C00188927
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_APLICACIO_PK" ON "NOT_APLICACIO" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188929
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_COLUMNES_PK" ON "NOT_COLUMNES" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188931
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_DOCUMENT_PK" ON "NOT_DOCUMENT" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188942
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_ENTITAT_PK" ON "NOT_ENTITAT" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188943
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_ENTITAT_UK" ON "NOT_ENTITAT" ("CODI") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188945
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_ENTITAT_TIPUS_DOC_PK" ON "NOT_ENTITAT_TIPUS_DOC" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188947
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_FORMATS_FULLA_PK" ON "NOT_FORMATS_FULLA" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188949
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_FORMATS_SOBRE_PK" ON "NOT_FORMATS_SOBRE" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188952
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_GRUP_PK" ON "NOT_GRUP" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188968
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_NOTIFICACIO_PK" ON "NOT_NOTIFICACIO" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188973
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_NOTIFICACIO_ENV_PK" ON "NOT_NOTIFICACIO_ENV" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188978
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_NOTIFICACIO_EVENT_PK" ON "NOT_NOTIFICACIO_EVENT" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188984
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_PAGADOR_CIE_PK" ON "NOT_PAGADOR_CIE" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188986
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_PAGADOR_POSTAL_PK" ON "NOT_PAGADOR_POSTAL" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188990
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_PERSONA_PK" ON "NOT_PERSONA" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188992
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_PRO_GRUP_PK" ON "NOT_PRO_GRUP" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00188997
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_PROCEDIMENT_PK" ON "NOT_PROCEDIMENT" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C00189000
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_USUARI_UK" ON "NOT_USUARI" ("CODI") 
  ;
  
--------------------------------------------------------
--  DDL for Index NOT_ORGAN_GESTOR_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_ORGAN_GESTOR_PK" ON "NOT_ORGAN_GESTOR" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index NOT_ORGAN_GESTOR_UK
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOT_ORGAN_GESTOR_UK" ON "NOT_ORGAN_GESTOR" ("CODI") 
  ;
--------------------------------------------------------
--  DDL for Index NOT_ORGAN_ENTITAT_FK_I
--------------------------------------------------------

  CREATE INDEX "NOT_ORGAN_ENTITAT_FK_I" ON "NOT_ORGAN_GESTOR" ("ENTITAT") 
  ;
