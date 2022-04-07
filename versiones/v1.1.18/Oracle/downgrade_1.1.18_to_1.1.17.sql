-- Tornar a la versió anterior (1.1.18 --> 1.1.17)
ALTER TABLE not_notificacio MODIFY registre_data DATE;
ALTER TABLE not_notificacio DROP CONSTRAINT not_notificacio_ref_uk;
ALTER TABLE not_notificacio_table DROP CONSTRAINT not_notificacio_table_ref_uk;