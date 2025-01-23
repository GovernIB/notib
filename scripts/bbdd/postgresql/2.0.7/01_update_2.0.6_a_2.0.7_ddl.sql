ALTER TABLE not_notificacio_table ADD caducitat TIMESTAMP;
ALTER TABLE not_notificacio_env ADD plazo_ampliado NUMBER(1, 0) DEFAULT '0';
ALTER TABLE not_pagador_cie ADD CONSTRAINT entitat_organ_constraint UNIQUE (entitat, organ_gestor);