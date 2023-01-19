-- Changeset db/changelog/changes/1.1.22/747.yaml::1665140886759-2::limit
CREATE TABLE NOT_MON_INT (ID NUMBER(19, 0) NOT NULL, CODI VARCHAR2(100 CHAR) NOT NULL, data TIMESTAMP(6), descripcio VARCHAR2(1024 CHAR), tipus VARCHAR2(10 CHAR), aplicacio VARCHAR2(64 CHAR), temps_resposta NUMBER(19, 0), estat VARCHAR2(5 CHAR), codi_usuari VARCHAR2(64 CHAR), codi_entitat VARCHAR2(64 CHAR), error_descripcio VARCHAR2(1024 CHAR), excepcio_msg VARCHAR2(1024 CHAR), excepcio_stacktrace VARCHAR2(2048 CHAR), CONSTRAINT NOT_MON_INT_PK PRIMARY KEY (ID));
CREATE TABLE NOT_MON_INT_PARAM (ID NUMBER(19, 0) NOT NULL, MON_INT_ID NUMBER(19, 0) NOT NULL, codi VARCHAR2(64 CHAR) NOT NULL, valor VARCHAR2(1024 CHAR), CONSTRAINT NOT_MON_INT_PARAM_PK PRIMARY KEY (ID));

ALTER TABLE NOT_MON_INT_PARAM ADD CONSTRAINT NOT_MONINTPARAM_MONINT_FK FOREIGN KEY (MON_INT_ID) REFERENCES NOT_MON_INT(ID);

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_MON_INT TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_MON_INT_PARAM TO WWW_NOTIB;