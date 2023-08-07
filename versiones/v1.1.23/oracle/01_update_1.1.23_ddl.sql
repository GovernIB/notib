-- Changeset db/changelog/changes/1.1.23/788.yaml::1665140886761-1::limit
ALTER TABLE not_organ_gestor ADD nom_es VARCHAR2(1000 CHAR);

ALTER TABLE not_procediment ADD manual NUMBER(1, 0) DEFAULT '0';