-- #557
ALTER TABLE not_procediment ADD tipus VARCHAR2(32);
ALTER TABLE not_notificacio_table ADD procediment_tipus VARCHAR2(32);
ALTER TABLE not_notificacio_env_table ADD procediment_tipus VARCHAR2(32);

-- #630
ALTER TABLE not_notificacio ADD estat_processat_date TIMESTAMP;
ALTER TABLE not_notificacio_table ADD estat_processat_date TIMESTAMP;
ALTER TABLE not_notificacio_audit ADD estat_processat_date TIMESTAMP;