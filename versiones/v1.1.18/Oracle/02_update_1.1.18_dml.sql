update not_notificacio n set registre_data = (select min(registre_data) from not_notificacio_env e where e.notificacio_id = n.id);

UPDATE not_notificacio_env SET per_email = '0';
UPDATE not_notificacio SET justificant_creat = '0';

INSERT INTO NOT_PROCESSOS_INICIALS (id, codi, init) VALUES (1, 'ACTUALITZAR_REFERENCIES', 1);