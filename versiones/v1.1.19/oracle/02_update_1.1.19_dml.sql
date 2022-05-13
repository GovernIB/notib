DECLARE
  v_ordre NUMBER(19,0);
  CURSOR v_entries IS SELECT ID, ACL_OBJECT_IDENTITY, SID, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE FROM NOT_ACL_ENTRY WHERE MASK = 1024;
BEGIN
FOR r_entry IN v_entries
  LOOP
    SELECT MAX(ACE_ORDER) + 1 INTO v_ordre FROM NOT_ACL_ENTRY WHERE ACL_OBJECT_IDENTITY = r_entry.ACL_OBJECT_IDENTITY;
    INSERT INTO NOT_ACL_ENTRY(ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE)
    VALUES (NOT_ACL_ENTRY_SEQ.nextval, r_entry.ACL_OBJECT_IDENTITY, v_ordre, r_entry.SID, 4096, r_entry.GRANTING, r_entry.AUDIT_SUCCESS, r_entry.AUDIT_FAILURE);
  END LOOP;
END;
/
