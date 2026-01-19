create or replace TRIGGER ${db_prefix}ACLCLA_IDGEN_TRG BEFORE INSERT ON ${db_prefix}ACL_CLASS FOR EACH ROW
BEGIN
    SELECT ${db_prefix}ACL_CLASS_SEQ.NEXTVAL INTO :new.id FROM dual;
END;
/

create or replace TRIGGER ${db_prefix}ACLENT_IDGEN_TRG BEFORE INSERT ON ${db_prefix}ACL_ENTRY FOR EACH ROW
BEGIN
    SELECT ${db_prefix}ACL_ENTRY_SEQ.NEXTVAL INTO :new.id FROM dual;
END;
/

create or replace TRIGGER ${db_prefix}ACLOID_IDGEN_TRG BEFORE INSERT ON ${db_prefix}ACL_OBJECT_IDENTITY FOR EACH ROW
BEGIN
    SELECT ${db_prefix}ACL_OBJECT_IDENTITY_SEQ.NEXTVAL INTO :new.id FROM dual;
END;
/

create or replace TRIGGER ${db_prefix}ACLSID_IDGEN_TRG BEFORE INSERT ON ${db_prefix}ACL_SID FOR EACH ROW
BEGIN
    SELECT ${db_prefix}ACL_SID_SEQ.NEXTVAL INTO :new.id FROM dual;
END;
/