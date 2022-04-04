-- Tornar a aquesta versió (1.1.17 --> 1.1.18) després d'haver fet un downgrade (1.1.18-->1.1.17)
ALTER TABLE not_notificacio MODIFY registre_data TIMESTAMP;
update not_notificacio n set registre_data = (select min(registre_data) from not_notificacio_env e where e.notificacio_id = n.id);
ALTER TABLE not_notificacio ADD CONSTRAINT not_notificacio_ref_uk UNIQUE (referencia);
ALTER TABLE not_notificacio_table ADD CONSTRAINT not_notificacio_table_ref_uk UNIQUE (referencia);
UPDATE not_notificacio_env SET per_email = '0' where per_email is null;
UPDATE not_notificacio SET justificant_creat = '0' where justificant_creat is null;