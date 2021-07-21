ALTER TABLE NOT_NOTIFICACIO
    DROP CONSTRAINT NOT_NOTERREVENT_NOTIFICACIO_FK;

ALTER TABLE NOT_NOTIFICACIO
    DROP COLUMN NOT_ERROR_EVENT_ID;

-- Eliminam els events de les notificacions sense enviaments
delete from not_notificacio_event
where notificacio_id in (select n.id from
                            not_notificacio n left join not_notificacio_env env
                            ON env.notificacio_id = n.id
                         where
                            env.id is null);

-- Eliminam les notificacions sense enviaments
delete from not_notificacio
where id in (select n.id from
                not_notificacio n left join not_notificacio_env env
                ON env.notificacio_id = n.id
             where
                env.id is null);

-- Eliminam les notificacions eliminades de la taula de notificaicons
delete from not_notificacio_table
where id in (select nt.id from
                not_notificacio_table nt left join not_notificacio n
                on n.id = nt.id
             where n.id is null);