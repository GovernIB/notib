CREATE INDEX NOT_PAGADOR_POSTAL_FK_I ON NOT_PROCEDIMENT(PAGADORPOSTAL);
CREATE INDEX NOT_PAGADOR_CIE_FK_I ON NOT_PROCEDIMENT(PAGADORCIE);
CREATE INDEX NOT_ENTITAT_FK_I ON NOT_PROCEDIMENT(entitat);

CREATE INDEX NOT_USUCRE_ENTITAT_FK_I ON NOT_ENTITAT(CREATEDBY_CODI);
CREATE INDEX NOT_USUMOD_ENTITAT_FK_I ON NOT_ENTITAT(LASTMODIFIEDBY_CODI);

CREATE INDEX NOT_NOTEVERRNOT_NOT_FK_I ON NOT_NOTIFICACIO(NOT_ERROR_EVENT_ID);
CREATE INDEX NOT_ENTITAT_NOTIFICACIO_FK_I ON NOT_NOTIFICACIO(ENTITAT_ID);
CREATE INDEX NOT_USUCRE_NOTIFICACIO_FK_I ON NOT_NOTIFICACIO(CREATEDBY_CODI);
CREATE INDEX NOT_USUMOD_NOTIFICACIO_FK_I ON NOT_NOTIFICACIO(LASTMODIFIEDBY_CODI);

CREATE INDEX NOT_NOTEVE_NOTERR_NOTDEST_FK_I ON NOT_NOTIFICACIO_ENV(NOTIFICA_ERROR_EVENT_ID);
CREATE INDEX NOT_NOTIFICACIO_NOTDEST_FK_I ON NOT_NOTIFICACIO_ENV(NOTIFICACIO_ID);
CREATE INDEX NOT_USUCRE_NOTDEST_FK_I ON NOT_NOTIFICACIO_ENV(CREATEDBY_CODI);
CREATE INDEX NOT_USUMOD_NOTDEST_FK_I ON NOT_NOTIFICACIO_ENV(LASTMODIFIEDBY_CODI);

CREATE INDEX NOT_NOTIFICACIO_NOTEVENT_FK_I ON NOT_NOTIFICACIO_EVENT(NOTIFICACIO_ID);
CREATE INDEX NOT_NOTDEST_NOTEVENT_FK_I ON NOT_NOTIFICACIO_EVENT(NOTIFICACIO_ENV_ID);
CREATE INDEX NOT_USUCRE_NOTEVENT_FK_I ON NOT_NOTIFICACIO_EVENT(CREATEDBY_CODI);
CREATE INDEX NOT_USUMOD_NOTEVENT_FK_I ON NOT_NOTIFICACIO_EVENT(LASTMODIFIEDBY_CODI);

CREATE INDEX NOT_ACL_OID_ENTRY_FK_I ON NOT_ACL_ENTRY(ACL_OBJECT_IDENTITY);
CREATE INDEX NOT_ACL_SID_ENTRY_FK_I ON NOT_ACL_ENTRY(SID);

CREATE INDEX NOT_ACL_CLASS_OID_FK_I ON NOT_ACL_OBJECT_IDENTITY(OBJECT_ID_CLASS);
CREATE INDEX NOT_ACL_PARENT_OID_FK_I ON NOT_ACL_OBJECT_IDENTITY(PARENT_OBJECT);

CREATE INDEX NOT_ACL_SID_OID_FK_I ON NOT_ACL_OBJECT_IDENTITY(OWNER_SID);

CREATE INDEX NOT_ENTITAT_TIPUS_DOC_FK_I ON NOT_ENTITAT_TIPUS_DOC(ENTITAT_ID);
