--------------------------------------------------------
--  DDL for Trigger NOT_ACLCLA_IDGEN_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NOT_ACLCLA_IDGEN_TRG" 
BEFORE INSERT ON NOT_ACL_CLASS
FOR EACH ROW
  BEGIN
    SELECT NOT_ACL_CLASS_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;

/
ALTER TRIGGER "NOT_ACLCLA_IDGEN_TRG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger NOT_ACLENT_IDGEN_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NOT_ACLENT_IDGEN_TRG" 
BEFORE INSERT ON NOT_ACL_ENTRY
FOR EACH ROW
  BEGIN
    SELECT NOT_ACL_ENTRY_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;

/
ALTER TRIGGER "NOT_ACLENT_IDGEN_TRG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger NOT_ACLOID_IDGEN_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NOT_ACLOID_IDGEN_TRG" 
BEFORE INSERT ON NOT_ACL_OBJECT_IDENTITY
FOR EACH ROW
  BEGIN
    SELECT NOT_ACL_OBJECT_IDENTITY_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;

/
ALTER TRIGGER "NOT_ACLOID_IDGEN_TRG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger NOT_ACLSID_IDGEN_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NOT_ACLSID_IDGEN_TRG" 
BEFORE INSERT ON NOT_ACL_SID
FOR EACH ROW
  BEGIN
    SELECT NOT_ACL_SID_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;

/
ALTER TRIGGER "NOT_ACLSID_IDGEN_TRG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger NOT_ACLCLA_IDGEN_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NOT_ACLCLA_IDGEN_TRG" 
BEFORE INSERT ON NOT_ACL_CLASS
FOR EACH ROW
  BEGIN
    SELECT NOT_ACL_CLASS_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;

/
ALTER TRIGGER "NOT_ACLCLA_IDGEN_TRG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger NOT_ACLENT_IDGEN_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NOT_ACLENT_IDGEN_TRG" 
BEFORE INSERT ON NOT_ACL_ENTRY
FOR EACH ROW
  BEGIN
    SELECT NOT_ACL_ENTRY_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;

/
ALTER TRIGGER "NOT_ACLENT_IDGEN_TRG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger NOT_ACLOID_IDGEN_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NOT_ACLOID_IDGEN_TRG" 
BEFORE INSERT ON NOT_ACL_OBJECT_IDENTITY
FOR EACH ROW
  BEGIN
    SELECT NOT_ACL_OBJECT_IDENTITY_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;

/
ALTER TRIGGER "NOT_ACLOID_IDGEN_TRG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger NOT_ACLSID_IDGEN_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NOT_ACLSID_IDGEN_TRG" 
BEFORE INSERT ON NOT_ACL_SID
FOR EACH ROW
  BEGIN
    SELECT NOT_ACL_SID_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;

/
ALTER TRIGGER "NOT_ACLSID_IDGEN_TRG" ENABLE;
