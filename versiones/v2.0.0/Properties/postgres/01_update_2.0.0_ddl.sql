CREATE TABLE not_sm_action (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, name VARCHAR(255), spel VARCHAR(255), CONSTRAINT not_sm_action_pkey PRIMARY KEY (id));

CREATE TABLE not_sm_deferred_events (jpa_repository_state_id BIGINT NOT NULL, deferred_events VARCHAR(255));

CREATE TABLE not_sm_guard (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, name VARCHAR(255), spel VARCHAR(255), CONSTRAINT not_sm_guard_pkey PRIMARY KEY (id));

CREATE TABLE not_sm_state (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, initial_state BOOLEAN, kind VARCHAR(255), machine_id VARCHAR(255), region VARCHAR(255), state VARCHAR(255), submachine_id VARCHAR(255), initial_action_id BIGINT, parent_state_id BIGINT, CONSTRAINT not_sm_state_pkey PRIMARY KEY (id));

CREATE TABLE not_sm_state_machine (machine_id VARCHAR(255) NOT NULL, state VARCHAR(255), state_machine_context OID, CONSTRAINT not_sm_state_machine_pkey PRIMARY KEY (machine_id));

CREATE TABLE not_sm_transition (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, event VARCHAR(255), kind VARCHAR(255), machine_id VARCHAR(255), guard_id BIGINT, source_id BIGINT, target_id BIGINT, CONSTRAINT not_sm_transition_pkey PRIMARY KEY (id));

CREATE TABLE not_sm_state_entry_actions (jpa_repository_state_id BIGINT, entry_actions_id BIGINT NOT NULL);

ALTER TABLE not_sm_state_entry_actions ADD CONSTRAINT pk_state_entry_actions PRIMARY KEY (jpa_repository_state_id, entry_actions_id);

CREATE TABLE not_sm_state_exit_actions (jpa_repository_state_id BIGINT, exit_actions_id BIGINT NOT NULL);

ALTER TABLE not_sm_state_exit_actions ADD CONSTRAINT pk_exit_entry_actions PRIMARY KEY (jpa_repository_state_id, exit_actions_id);

CREATE TABLE not_sm_state_state_actions (jpa_repository_state_id BIGINT, state_actions_id BIGINT NOT NULL);

ALTER TABLE not_sm_state_state_actions ADD CONSTRAINT pk_state_state_actions PRIMARY KEY (jpa_repository_state_id, state_actions_id);

CREATE TABLE not_sm_transition_actions (jpa_repository_transition_id BIGINT, actions_id BIGINT NOT NULL);

ALTER TABLE not_sm_transition_actions ADD CONSTRAINT pk_transition_actions PRIMARY KEY (jpa_repository_transition_id, actions_id);

ALTER TABLE not_sm_deferred_events ADD CONSTRAINT fk_state_deferred_events FOREIGN KEY (jpa_repository_state_id) REFERENCES not_sm_state (id);

ALTER TABLE not_sm_state ADD CONSTRAINT fk_state_initial_action FOREIGN KEY (initial_action_id) REFERENCES not_sm_action (id);

ALTER TABLE not_sm_state ADD CONSTRAINT fk_state_parent_state FOREIGN KEY (parent_state_id) REFERENCES not_sm_state (id);

ALTER TABLE not_sm_transition ADD CONSTRAINT fk_transition_guard FOREIGN KEY (guard_id) REFERENCES not_sm_guard (id);

ALTER TABLE not_sm_transition ADD CONSTRAINT fk_transition_source FOREIGN KEY (source_id) REFERENCES not_sm_state (id);

ALTER TABLE not_sm_transition ADD CONSTRAINT fk_transition_target FOREIGN KEY (target_id) REFERENCES not_sm_state (id);

ALTER TABLE not_sm_state_entry_actions ADD CONSTRAINT fk_state_entry_actions_a FOREIGN KEY (entry_actions_id) REFERENCES not_sm_action (id);

ALTER TABLE not_sm_state_entry_actions ADD CONSTRAINT fk_state_entry_actions_s FOREIGN KEY (jpa_repository_state_id) REFERENCES not_sm_state (id);

ALTER TABLE not_sm_state_exit_actions ADD CONSTRAINT fk_state_exit_actions_a FOREIGN KEY (exit_actions_id) REFERENCES not_sm_action (id);

ALTER TABLE not_sm_state_exit_actions ADD CONSTRAINT fk_state_exit_actions_s FOREIGN KEY (jpa_repository_state_id) REFERENCES not_sm_state (id);

ALTER TABLE not_sm_state_state_actions ADD CONSTRAINT fk_state_state_actions_a FOREIGN KEY (state_actions_id) REFERENCES not_sm_action (id);

ALTER TABLE not_sm_state_state_actions ADD CONSTRAINT fk_state_state_actions_s FOREIGN KEY (jpa_repository_state_id) REFERENCES not_sm_state (id);

ALTER TABLE not_sm_transition_actions ADD CONSTRAINT fk_transition_actions_a FOREIGN KEY (actions_id) REFERENCES not_sm_action (id);

ALTER TABLE not_sm_transition_actions ADD CONSTRAINT fk_transition_actions_t FOREIGN KEY (jpa_repository_transition_id) REFERENCES not_sm_transition (id);

ALTER TABLE not_notificacio_env ADD CONSTRAINT not_enviament_ref_uk UNIQUE (notifica_ref);

ALTER TABLE not_columnes RENAME COLUMN created_date TO data_enviament;


ALTER TABLE NOT_NOTIFICACIO_TABLE ADD ESTAT_STRING VARCHAR(2000);

ALTER TABLE NOT_NOTIFICACIO_TABLE ADD DOCUMENT_ID BIGINT;

ALTER TABLE NOT_NOTIFICACIO_TABLE ADD ENV_CER_DATA TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE NOT_NOTIFICACIO_TABLE ADD REG_ENV_PENDENTS BOOLEAN DEFAULT FALSE;

ALTER TABLE NOT_NOTIFICACIO_TABLE ADD PER_ACTUALITZAR BOOLEAN DEFAULT TRUE;


GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_GUARD TO WWW_NOTIB;

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_ACTION TO WWW_NOTIB;

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_DEFERRED_EVENTS TO WWW_NOTIB;

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_STATE TO WWW_NOTIB;

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_STATE_ENTRY_ACTIONS TO WWW_NOTIB;

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_STATE_EXIT_ACTIONS TO WWW_NOTIB;

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_SM_STATE_MACHINE TO WWW_NOTIB;

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_STATE_STATE_ACTIONS TO WWW_NOTIB;

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_TRANSITION TO WWW_NOTIB;

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_TRANSITION_ACTIONS TO WWW_NOTIB;

CREATE SYNONYM action FOR not_action;

CREATE SYNONYM guard FOR not_guard;

CREATE SYNONYM state FOR not_state;

CREATE SYNONYM state_machine FOR not_state_machine;

CREATE SYNONYM transition FOR not_transition;

CREATE SYNONYM deferred_events FOR not_deferred_events;

CREATE SYNONYM state_entry_actions FOR not_state_entry_actions;

CREATE SYNONYM state_exit_actions FOR not_state_exit_actions;

CREATE SYNONYM state_state_actions FOR not_state_state_actions;

CREATE SYNONYM transition_actions FOR not_transition_actions;