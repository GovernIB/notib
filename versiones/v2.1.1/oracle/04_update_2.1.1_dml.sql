-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-17::limit
UPDATE not_notificacio_env_table t SET registre_numero = (SELECT registre_numero_formatat FROM not_NOTIFICACIO_ENV e WHERE t.id = e.id);
