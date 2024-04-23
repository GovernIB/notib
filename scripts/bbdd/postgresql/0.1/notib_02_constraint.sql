
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
