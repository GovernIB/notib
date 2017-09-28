
ALTER TABLE NOT_USUARI ADD (
  CONSTRAINT NOT_USUARI_PK PRIMARY KEY (CODI));

ALTER TABLE NOT_ENTITAT ADD (
  CONSTRAINT NOT_ENTITAT_PK PRIMARY KEY (ID),
  CONSTRAINT NOT_ENTITAT_CODI_UK UNIQUE (CODI));

ALTER TABLE NOT_APLICACIO ADD (
  CONSTRAINT NOT_APLICACIO_PK PRIMARY KEY (ID));

ALTER TABLE NOT_NOTIFICACIO ADD (
  CONSTRAINT NOT_NOTIFICACIO_PK PRIMARY KEY (ID));

ALTER TABLE NOT_NOTIFICACIO_ENV ADD (
  CONSTRAINT NOT_NOTIFICACIO_ENV_PK PRIMARY KEY (ID));

ALTER TABLE NOT_NOTIFICACIO_EVENT ADD (
  CONSTRAINT NOT_NOTIFICACIO_EVENT_PK PRIMARY KEY (ID));

ALTER TABLE NOT_ENTITAT ADD (
  CONSTRAINT NOT_USUCRE_ENTITAT_FK FOREIGN KEY (CREATEDBY_CODI)
    REFERENCES NOT_USUARI (CODI),
  CONSTRAINT NOT_USUMOD_ENTITAT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI)
    REFERENCES NOT_USUARI (CODI));

ALTER TABLE NOT_APLICACIO ADD (
  CONSTRAINT NOT_USUCRE_APLICACIO_FK FOREIGN KEY (CREATEDBY_CODI)
    REFERENCES NOT_USUARI (CODI),
  CONSTRAINT NOT_USUMOD_APLICACIO_FK FOREIGN KEY (LASTMODIFIEDBY_CODI)
    REFERENCES NOT_USUARI (CODI));

ALTER TABLE NOT_NOTIFICACIO ADD (
  CONSTRAINT NOT_NOTEVENOT_NOTIFICACIO_FK FOREIGN KEY (ERROR_NOT_EVENT_ID)
    REFERENCES NOT_NOTIFICACIO_EVENT (ID),
  CONSTRAINT NOT_ENTITAT_NOTIFICACIO_FK FOREIGN KEY (ENTITAT_ID)
    REFERENCES NOT_ENTITAT (ID),
  CONSTRAINT NOT_USUCRE_NOTIFICACIO_FK FOREIGN KEY (CREATEDBY_CODI)
    REFERENCES NOT_USUARI (CODI),
  CONSTRAINT NOT_USUMOD_NOTIFICACIO_FK FOREIGN KEY (LASTMODIFIEDBY_CODI)
    REFERENCES NOT_USUARI (CODI));

ALTER TABLE NOT_NOTIFICACIO_ENV ADD (
  CONSTRAINT NOT_NOTEVE_NOTERR_NOTENV_FK FOREIGN KEY (NOTIFICA_ERROR_EVENT_ID)
	  REFERENCES NOT_NOTIFICACIO_EVENT (ID),
  CONSTRAINT NOT_NOTEVE_SEUERR_NOTENV_FK FOREIGN KEY (SEU_ERROR_EVENT_ID)
	  REFERENCES NOT_NOTIFICACIO_EVENT (ID),
  CONSTRAINT NOT_NOTIFICACIO_NOTENV_FK FOREIGN KEY (NOTIFICACIO_ID)
	  REFERENCES NOT_NOTIFICACIO (ID),
  CONSTRAINT NOT_USUCRE_NOTENV_FK FOREIGN KEY (CREATEDBY_CODI)
    REFERENCES NOT_USUARI (CODI),
  CONSTRAINT NOT_USUMOD_NOTENV_FK FOREIGN KEY (LASTMODIFIEDBY_CODI)
    REFERENCES NOT_USUARI (CODI));

ALTER TABLE NOT_NOTIFICACIO_EVENT ADD (
  CONSTRAINT NOT_NOTIFICACIO_NOTEVENT_FK FOREIGN KEY (NOTIFICACIO_ID)
	  REFERENCES NOT_NOTIFICACIO (ID),
  CONSTRAINT NOT_NOTENV_NOTEVENT_FK FOREIGN KEY (NOTIFICACIO_ENV_ID)
	  REFERENCES NOT_NOTIFICACIO_ENV (ID),
  CONSTRAINT NOT_USUCRE_NOTEVENT_FK FOREIGN KEY (CREATEDBY_CODI)
    REFERENCES NOT_USUARI (CODI),
  CONSTRAINT NOT_USUMOD_NOTEVENT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI)
    REFERENCES NOT_USUARI (CODI));

ALTER TABLE NOT_ACL_CLASS ADD (
  CONSTRAINT NOT_ACL_CLASS_PK PRIMARY KEY (ID),
  CONSTRAINT NOT_ACL_CLASS_CLASS_UK UNIQUE (CLASS));

ALTER TABLE NOT_ACL_ENTRY ADD (
  CONSTRAINT NOT_ACL_ENTRY_PK PRIMARY KEY (ID),
  CONSTRAINT NOT_ACL_ENTRY_IDENT_ORDER_UK UNIQUE (ACL_OBJECT_IDENTITY, ACE_ORDER));

ALTER TABLE NOT_ACL_OBJECT_IDENTITY ADD (
  CONSTRAINT NOT_ACL_OID_PK PRIMARY KEY (ID),
  CONSTRAINT NOT_ACL_IOD_CLASS_IDENTITY_UK UNIQUE (OBJECT_ID_CLASS, OBJECT_ID_IDENTITY));

ALTER TABLE NOT_ACL_SID ADD (
  CONSTRAINT NOT_ACL_SID_PK PRIMARY KEY (ID),
  CONSTRAINT NOT_ACL_SID_PRINCIPAL_SID_UK UNIQUE (SID, PRINCIPAL));

ALTER TABLE NOT_ACL_ENTRY ADD CONSTRAINT NOT_ACL_ENTRY_GRANTING_CK
  CHECK (GRANTING IN (1,0));

ALTER TABLE NOT_ACL_ENTRY ADD CONSTRAINT NOT_ACL_ENTRY_AUDIT_SUCCESS_CK
  CHECK (AUDIT_SUCCESS IN (1,0));

ALTER TABLE NOT_ACL_ENTRY ADD CONSTRAINT NOT_ACL_ENTRY_AUDIT_FAILURE_CK
  CHECK (AUDIT_FAILURE IN (1,0));

ALTER TABLE NOT_ACL_OBJECT_IDENTITY ADD CONSTRAINT NOT_ACL_OID_ENTRIES_CK
  CHECK (ENTRIES_INHERITING IN (1,0));

ALTER TABLE NOT_ACL_SID ADD CONSTRAINT NOT_ACL_SID_PRINCIPAL_CK
  CHECK (PRINCIPAL IN (1,0));

ALTER TABLE NOT_ACL_ENTRY ADD CONSTRAINT NOT_ACL_OID_ENTRY_FK
  FOREIGN KEY (ACL_OBJECT_IDENTITY)
  REFERENCES NOT_ACL_OBJECT_IDENTITY (ID);

ALTER TABLE NOT_ACL_ENTRY ADD CONSTRAINT NOT_ACL_SID_ENTRY_FK
  FOREIGN KEY (SID)
  REFERENCES NOT_ACL_SID (ID);

ALTER TABLE NOT_ACL_OBJECT_IDENTITY ADD CONSTRAINT NOT_ACL_CLASS_OID_FK
  FOREIGN KEY (OBJECT_ID_CLASS)
  REFERENCES NOT_ACL_CLASS (ID);

ALTER TABLE NOT_ACL_OBJECT_IDENTITY ADD CONSTRAINT NOT_ACL_PARENT_OID_FK
  FOREIGN KEY (PARENT_OBJECT)
  REFERENCES NOT_ACL_OBJECT_IDENTITY (ID);

ALTER TABLE NOT_ACL_OBJECT_IDENTITY ADD CONSTRAINT NOT_ACL_SID_OID_FK
  FOREIGN KEY (OWNER_SID)
  REFERENCES NOT_ACL_SID (ID);
