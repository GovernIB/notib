-- #557
ALTER TABLE not_procediment ADD tipus VARCHAR(32);
ALTER TABLE not_notificacio_table ADD procediment_tipus VARCHAR(32);
ALTER TABLE not_notificacio_env_table ADD procediment_tipus VARCHAR(32);

-- #630
ALTER TABLE not_notificacio ADD estat_processat_date TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE not_notificacio_table ADD estat_processat_date TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE not_notificacio_audit ADD estat_processat_date TIMESTAMP WITHOUT TIME ZONE;