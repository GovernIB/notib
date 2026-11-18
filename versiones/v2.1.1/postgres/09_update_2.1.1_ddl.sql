CREATE TABLE not_excepcio_log (
    id BIGINT NOT NULL,
    data TIMESTAMP NOT NULL,
    tipus VARCHAR(512),
    object_id VARCHAR(512),
    object_class VARCHAR(512),
    uri VARCHAR(1024),
    param1 VARCHAR(512),
    param2 VARCHAR(512),
    message VARCHAR(4000),
    stacktrace TEXT,
    entitat_id BIGINT,
    createddate TIMESTAMP,
    createdby_codi VARCHAR(64),
    lastmodifiedby_codi VARCHAR(64),
    lastmodifieddate TIMESTAMP,
    signatura VARCHAR(64),
    num_ocurrencies INTEGER,
    data_darrera TIMESTAMP,
    CONSTRAINT not_excepcio_log_pk PRIMARY KEY (id)
);
CREATE INDEX not_excepcio_log_sign_idx ON not_excepcio_log (signatura, data_darrera);
ALTER TABLE not_mon_int ADD COLUMN excepcio_id BIGINT;
ALTER TABLE not_mon_int ADD CONSTRAINT not_mon_int_excepcio_fk FOREIGN KEY (excepcio_id) REFERENCES not_excepcio_log (id);
