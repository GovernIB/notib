ALTER TABLE not_notificacio_table ADD caducitat TIMESTAMP;
UPDATE not_notificacio_table t SET CADUCITAT = (SELECT caducitat FROM not_notificacio n WHERE t.id = n.id);