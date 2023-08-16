-- Changeset db/changelog/changes/1.1.23/788.yaml::1665140886761-1::limit
ALTER TABLE not_organ_gestor ADD nom_es VARCHAR(1000);
ALTER TABLE not_procediment ADD manual BOOLEAN DEFAULT false;

