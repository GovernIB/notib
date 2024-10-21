ALTER TABLE not_organ_gestor ADD permetre_sir NUMBER(1) DEFAULT '0' NOT NULL;
ALTER TABLE not_pagador_cie ADD cie_extern NUMBER(1) DEFAULT '1' NOT NULL;
ALTER TABLE not_pagador_cie ADD api_key VARCHAR2(128 CHAR);
ALTER TABLE not_pagador_cie ADD salt VARCHAR2(88 CHAR);
ALTER TABLE not_pagador_cie ADD organ_emisor NUMBER(19, 0);
ALTER TABLE not_entrega_postal ADD cie_id VARCHAR2(100 CHAR);
ALTER TABLE not_entrega_postal ADD cie_cancelat NUMBER(1) DEFAULT '1' NOT NULL;
ALTER TABLE not_entrega_postal ADD cie_estat VARCHAR2(50 CHAR);
ALTER TABLE NOT_PAGADOR_CIE ADD CONSTRAINT NOT_EMISORCIE_ORGAN_FK FOREIGN KEY (ORGAN_EMISOR) REFERENCES NOT_ORGAN_GESTOR (ID);
ALTER TABLE not_notificacio ADD caducitat_original date;
