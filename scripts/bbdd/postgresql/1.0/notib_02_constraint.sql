ALTER TABLE ONLY NOT_USUARI
  ADD CONSTRAINT NOT_USUARI_PKEY PRIMARY KEY (CODI);

ALTER TABLE ONLY NOT_ENTITAT
    ADD CONSTRAINT NOT_ENTITAT_PKEY PRIMARY KEY (ID);

ALTER TABLE ONLY NOT_ENTITAT
    ADD CONSTRAINT NOT_ENTITAT_CODI_KEY UNIQUE (CODI);

ALTER TABLE ONLY NOT_APLICACIO
    ADD CONSTRAINT NOT_APLICACIO_PKEY PRIMARY KEY (ID);


ALTER TABLE ONLY NOT_NOTIFICACIO
    ADD CONSTRAINT NOT_NOTIFICACIO_PKEY PRIMARY KEY (ID);


ALTER TABLE ONLY NOT_NOTIFICACIO_DEST
    ADD CONSTRAINT NOT_NOTIFICACIO_DEST_PKEY PRIMARY KEY (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO_EVENT
    ADD CONSTRAINT NOT_NOTIFICACIO_EVENT_PKEY PRIMARY KEY (ID);

ALTER TABLE ONLY NOT_ENTITAT
    ADD CONSTRAINT NOT_USUCRE_ENTITAT_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES NOT_USUARI (CODI);

ALTER TABLE ONLY NOT_ENTITAT
    ADD CONSTRAINT NOT_USUMOD_ENTITAT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES NOT_USUARI (CODI);

ALTER TABLE ONLY NOT_APLICACIO
    ADD CONSTRAINT NOT_USUCRE_APLICACIO_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES NOT_USUARI (CODI);

ALTER TABLE ONLY NOT_APLICACIO
    ADD CONSTRAINT NOT_USUMOD_APLICACIO_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES NOT_USUARI (CODI);

ALTER TABLE ONLY NOT_NOTIFICACIO
    ADD CONSTRAINT NOT_NOTEVE_NOTIFICACIO_FK FOREIGN KEY (ERROR_EVENT_ID) REFERENCES NOT_NOTIFICACIO_EVENT (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO
    ADD CONSTRAINT NOT_ENTITAT_NOTIFICACIO_FK FOREIGN KEY (ENTITAT_ID) REFERENCES NOT_ENTITAT (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO
    ADD CONSTRAINT NOT_USUCRE_NOTIFICACIO_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES NOT_USUARI (CODI);

ALTER TABLE ONLY NOT_NOTIFICACIO
    ADD CONSTRAINT NOT_USUMOD_NOTIFICACIO_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES NOT_USUARI (CODI);

ALTER TABLE ONLY NOT_NOTIFICACIO_DEST
    ADD CONSTRAINT NOT_NOTEVE_NOTERR_NOTDEST_FK FOREIGN KEY (NOTIFICA_ERROR_EVENT_ID) REFERENCES NOT_NOTIFICACIO_EVENT (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO_DEST
    ADD CONSTRAINT NOT_NOTEVE_SEUERR_NOTDEST_FK FOREIGN KEY (SEU_ERROR_EVENT_ID) REFERENCES NOT_NOTIFICACIO_EVENT (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO_DEST
    ADD CONSTRAINT NOT_NOTIFICACIO_NOTDEST_FK FOREIGN KEY (NOTIFICACIO_ID) REFERENCES NOT_NOTIFICACIO (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO_DEST
    ADD CONSTRAINT NOT_USUCRE_NOTDEST_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES NOT_USUARI (CODI);

ALTER TABLE ONLY NOT_NOTIFICACIO_DEST
    ADD CONSTRAINT NOT_USUMOD_NOTDEST_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES NOT_USUARI (CODI);

ALTER TABLE ONLY NOT_NOTIFICACIO_EVENT
    ADD CONSTRAINT NOT_NOTIFICACIO_NOTEVENT_FK FOREIGN KEY (NOTIFICACIO_ID) REFERENCES NOT_NOTIFICACIO (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO_EVENT
    ADD CONSTRAINT NOT_NOTDEST_NOTEVENT_FK FOREIGN KEY (NOTIFICACIO_DEST_ID) REFERENCES NOT_NOTIFICACIO_DEST (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO_EVENT
    ADD CONSTRAINT NOT_USUCRE_NOTEVENT_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES NOT_USUARI (CODI);

ALTER TABLE ONLY NOT_NOTIFICACIO_EVENT
    ADD CONSTRAINT NOT_USUMOD_NOTEVENT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES NOT_USUARI (CODI);

ALTER TABLE ONLY NOT_ACL_CLASS
    ADD CONSTRAINT NOT_ACL_CLASS_PKEY PRIMARY KEY (ID);

ALTER TABLE ONLY NOT_ACL_CLASS
    ADD CONSTRAINT NOT_ACL_CLASS_CLASS_HEY UNIQUE (CLASS);

ALTER TABLE ONLY NOT_ACL_ENTRY
    ADD CONSTRAINT NOT_ACL_ENTRY_PKEY PRIMARY KEY (ID);

ALTER TABLE ONLY NOT_ACL_ENTRY
    ADD CONSTRAINT NOT_ACL_ENTRY_IDENT_ORDER_KEY UNIQUE (ACL_OBJECT_IDENTITY, ACE_ORDER);

ALTER TABLE ONLY NOT_ACL_OBJECT_IDENTITY
    ADD CONSTRAINT NOT_ACL_OID_PKEY PRIMARY KEY (ID);

ALTER TABLE ONLY NOT_ACL_OBJECT_IDENTITY
    ADD CONSTRAINT NOT_ACL_IOD_CLASS_IDENTITY_KEY UNIQUE (OBJECT_ID_CLASS, OBJECT_ID_IDENTITY);

ALTER TABLE ONLY NOT_ACL_SID
    ADD CONSTRAINT NOT_ACL_SID_PK PRIMARY KEY (ID);

ALTER TABLE ONLY NOT_ACL_SID
    ADD CONSTRAINT NOT_ACL_SID_PRINCIPAL_SID_UK UNIQUE (SID, PRINCIPAL);

ALTER TABLE ONLY NOT_ACL_ENTRY
    ADD CONSTRAINT NOT_ACL_ENTRY_GRANTING_CK CHECK (GRANTING IN (true,false));

ALTER TABLE ONLY NOT_ACL_ENTRY
    ADD CONSTRAINT NOT_ACL_ENTRY_AUDIT_SUCCESS_CK CHECK (AUDIT_SUCCESS IN (true,false));

ALTER TABLE ONLY NOT_ACL_ENTRY
    ADD CONSTRAINT NOT_ACL_ENTRY_AUDIT_FAILURE_CK CHECK (AUDIT_FAILURE IN (true,false));

ALTER TABLE ONLY NOT_ACL_OBJECT_IDENTITY
    ADD CONSTRAINT NOT_ACL_OID_ENTRIES_CK CHECK (ENTRIES_INHERITING IN (true,false));

ALTER TABLE ONLY NOT_ACL_SID
    ADD CONSTRAINT NOT_ACL_SID_PRINCIPAL_CK CHECK (PRINCIPAL IN (true,false));

ALTER TABLE ONLY NOT_ACL_ENTRY
    ADD CONSTRAINT NOT_ACL_OID_ENTRY_FK FOREIGN KEY (ACL_OBJECT_IDENTITY) REFERENCES NOT_ACL_OBJECT_IDENTITY (ID);

ALTER TABLE ONLY NOT_ACL_ENTRY
    ADD CONSTRAINT NOT_ACL_SID_ENTRY_FK FOREIGN KEY (SID) REFERENCES NOT_ACL_SID (ID);

ALTER TABLE ONLY NOT_ACL_OBJECT_IDENTITY
    ADD CONSTRAINT NOT_ACL_CLASS_OID_FK FOREIGN KEY (OBJECT_ID_CLASS) REFERENCES NOT_ACL_CLASS (ID);

ALTER TABLE ONLY NOT_ACL_OBJECT_IDENTITY
    ADD CONSTRAINT NOT_ACL_PARENT_OID_FK FOREIGN KEY (PARENT_OBJECT) REFERENCES NOT_ACL_OBJECT_IDENTITY (ID);

ALTER TABLE ONLY NOT_ACL_OBJECT_IDENTITY
    ADD CONSTRAINT NOT_ACL_SID_OID_FK FOREIGN KEY (OWNER_SID) REFERENCES NOT_ACL_SID (ID);
    
-- NOTIB 1.0
ALTER TABLE ONLY NOT_PERSONA ADD (
  CONSTRAINT NOT_PERSONA_PK PRIMARY KEY (ID));
  
ALTER TABLE ONLY NOT_PROCEDIMENT ADD (
  CONSTRAINT NOT_PROCEDIMENT_PK PRIMARY KEY (ID));
  
ALTER TABLE ONLY NOT_GRUP ADD (
  CONSTRAINT NOT_GRUP_PK PRIMARY KEY (ID));
  
ALTER TABLE ONLY NOT_PAGADOR_POSTAL ADD (
  CONSTRAINT NOT_PAGADOR_POSTAL_PK PRIMARY KEY (ID));
  
ALTER TABLE NOT_PAGADOR_CIE ADD (
  CONSTRAINT NOT_PAGADOR_CIE_PK PRIMARY KEY (ID));

ALTER TABLE ONLY NOT_DOCUMENT ADD (
  CONSTRAINT NOT_DOCUMENT_PK PRIMARY KEY (ID));

  
ALTER TABLE ONLY NOT_PERSONA 
	ADD CONSTRAINT NOT_PERSONA_NOT_FK FOREIGN KEY (NOTIFICACIO_ENV_ID) REFERENCES NOT_NOTIFICACIO_ENV (ID);
    
ALTER TABLE ONLY NOT_NOTIFICACIO_ENV 
	ADD CONSTRAINT NOT_PERSONA_NOTIFICACIO_ENV_FK FOREIGN KEY (TITULAR_ID)REFERENCES NOT_PERSONA (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO 
	ADD CONSTRAINT NOT_DOCUMENT_NOTIFICACIO_FK FOREIGN KEY (DOCUMENT_ID) REFERENCES NOT_DOCUMENT (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO 
	ADD CONSTRAINT NOT_PAGADOR_POSTAL_NOTIFICACIO_FK FOREIGN KEY (PAGADOR_POSTAL_ID) REFERENCES NOT_PAGADOR_POSTAL (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO 
	ADD CONSTRAINT NOT_PAGADOR_CIE_NOTIFICACIO_FK FOREIGN KEY (PAGADOR_CIE_ID) REFERENCES NOT_PAGADOR_CIE (ID);

ALTER TABLE ONLY NOT_NOTIFICACIO 
	ADD CONSTRAINT NOT_PROCEDIMENT_NOT_FK FOREIGN KEY (PROCEDIMENT_ID) REFERENCES NOT_PROCEDIMENT (ID);
	
ALTER TABLE ONLY NOT_PROCEDIMENT ADD (
  CONSTRAINT NOT_GRUP_FK FOREIGN KEY (GRUP) REFERENCES NOT_GRUP (ID));
  
ALTER TABLE ONLY NOT_PROCEDIMENT ADD (
  CONSTRAINT NOT_PAGADOR_POSTAL_FK FOREIGN KEY (PAGADORPOSTAL) REFERENCES NOT_PAGADOR_POSTAL (ID));
  
ALTER TABLE ONLY NOT_PROCEDIMENT ADD (
  CONSTRAINT NOT_PAGADOR_CIE_FK FOREIGN KEY (PAGADORCIE) REFERENCES NOT_PAGADOR_CIE (ID));
    
ALTER TABLE ONLY NOT_PROCEDIMENT ADD (
  CONSTRAINT NOT_ENTITAT_FK FOREIGN KEY (ENTITAT) REFERENCES NOT_ENTITAT (ID));
      
ALTER TABLE ONLY NOT_GRUP ADD (
  CONSTRAINT NOT_ENTITAT_GRUP_FK FOREIGN KEY (ENTITAT) REFERENCES NOT_ENTITAT (ID));
    
ALTER TABLE ONLY NOT_COLUMNES ADD (
  CONSTRAINT NOT_COLUMNES_ENTITAT_FK FOREIGN KEY (ENTITAT_ID) REFERENCES NOT_ENTITAT (ID));

ALTER TABLE ONLY NOT_COLUMNES ADD (
  CONSTRAINT NOT_COLUMNES_USUARI_FK FOREIGN KEY (USUARI_CODI) REFERENCES NOT_USUARI (CODI));
  
ALTER TABLE ONLY NOT_PRO_GRUP ADD (
  CONSTRAINT NOT_GRUP_PRO_FK FOREIGN KEY (GRUP) REFERENCES NOT_GRUP (ID),
  CONSTRAINT NOT_PRO_GRUP_FK FOREIGN KEY (PROCEDIMENT) REFERENCES NOT_PROCEDIMENT (ID));

      