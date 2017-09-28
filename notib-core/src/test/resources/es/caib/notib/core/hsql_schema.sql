create table public.not_acl_sid (id bigint generated by default as identity (start with 1), principal bit not null, sid varchar(100) not null, primary key (id))
create table public.not_acl_class (id bigint generated by default as identity(start with 100) not null primary key,class varchar_ignorecase(100) not null,constraint public.not_class_uk unique(class))
create table public.not_acl_object_identity (id bigint generated by default as identity(start with 100) not null primary key,object_id_class bigint not null,object_id_identity bigint not null,parent_object bigint,owner_sid bigint not null,entries_inheriting boolean not null,constraint public.not_oid_uk unique(object_id_class,object_id_identity),constraint public.not_oid_oid_fk foreign key(parent_object)references public.not_acl_object_identity(id),constraint public.not_class_oid_fk foreign key(object_id_class)references public.not_acl_class(id),constraint public.not_sid_oid_fk foreign key(owner_sid)references public.not_acl_sid(id))
create table public.not_acl_entry (id bigint generated by default as identity(start with 100) not null primary key,acl_object_identity bigint not null,ace_order int not null,sid bigint not null,mask integer not null,granting boolean not null,audit_success boolean not null,audit_failure boolean not null,constraint public.not_entry_uk unique(acl_object_identity,ace_order),constraint public.not_oid_entry_fk foreign key(acl_object_identity) references public.not_acl_object_identity(id),constraint public.not_sid_entry_fk foreign key(sid) references public.not_acl_sid(id))
create table not_aplicacio (id bigint generated by default as identity (start with 1), createdDate timestamp, lastModifiedDate timestamp, callback_url varchar(256), tipus_autenticacio integer, usuari_codi varchar(64), createdBy_codi varchar(64), lastModifiedBy_codi varchar(64), primary key (id))
create table not_entitat (id bigint generated by default as identity (start with 1), createdDate timestamp, lastModifiedDate timestamp, activa bit not null, codi varchar(64) not null, descripcio varchar(1024), dir3_codi varchar(9) not null, nom varchar(256) not null, tipus varchar(32) not null, version bigint not null, createdBy_codi varchar(64), lastModifiedBy_codi varchar(64), primary key (id), unique (codi))
create table not_notificacio (id bigint generated by default as identity (start with 1), createdDate timestamp, lastModifiedDate timestamp, concepte varchar(50) not null, descripcio varchar(100), doc_arxiu_id varchar(64) not null, doc_arxiu_nom varchar(256) not null, doc_gen_csv bit not null, doc_hash varchar(40) not null, doc_normalitzat bit not null, emisor_dir3codi varchar(9) not null, env_data_prog date, env_tipus integer not null, error_not bit not null, estat integer not null, pagcie_dir3 varchar(9), pagcie_data_vig date, pagcor_codi_client varchar(20), pagcor_dir3 varchar(9), pagcor_numcont varchar(20), pagcor_data_vig date, proc_codi_sia varchar(6) not null, proc_desc_sia varchar(256), seu_avis_text varchar(256) not null, seu_avis_mobil varchar(256), seu_avis_titol varchar(256) not null, seu_exp_ideni varchar(52) not null, seu_exp_serdoc varchar(10) not null, seu_exp_titol varchar(256) not null, seu_exp_uniorg varchar(10) not null, seu_idioma varchar(256) not null, seu_ofici_text varchar(256) not null, seu_ofici_titol varchar(256) not null, seu_reg_llibre varchar(256) not null, seu_reg_oficina varchar(256) not null, createdBy_codi varchar(64), lastModifiedBy_codi varchar(64), entitat_id bigint not null, error_not_event_id bigint, primary key (id))
create table not_notificacio_env (id bigint generated by default as identity (start with 1), createdDate timestamp, lastModifiedDate timestamp, caducitat date, deh_nif varchar(9), deh_obligat bit, deh_proc_codi varchar(6), destinatari_email varchar(100), destinatari_llinatge1 varchar(125), destinatari_llinatge2 varchar(125), destinatari_nif varchar(9), destinatari_nom varchar(125), destinatari_telefon varchar(16), dom_apartat varchar(10), dom_bloc varchar(50), dom_cie integer, dom_codi_postal varchar(10), dom_complem varchar(250), dom_con_tipus integer, dom_escala varchar(50), dom_linea1 varchar(50), dom_linea2 varchar(50), dom_mun_codine varchar(5), dom_mun_nom varchar(64), dom_num_num varchar(10), dom_num_puntkm varchar(10), dom_num_tipus integer, dom_pai_codiso varchar(3), dom_pai_nom varchar(64), dom_planta varchar(50), dom_poblacio varchar(30), dom_porta varchar(50), dom_portal varchar(50), dom_prv_codi varchar(2), dom_prv_nom varchar(64), dom_tipus integer, dom_via_nom varchar(100), dom_via_tipus integer, notifica_arr_dir3desc varchar(100), notifica_arr_dir3codi varchar(9), notifica_cer_arxiuid varchar(50), notifica_cer_arxtip integer, notifica_cer_csv varchar(50), notifica_cer_data timestamp, notifica_cer_hash varchar(50), notifica_cer_metas varchar(255), notifica_cer_numseg varchar(50), notifica_cer_origen varchar(20), notifica_cer_tamany integer, notifica_cer_tipus integer, notifica_cer_mime varchar(20), notifica_datcre timestamp, notifica_datdisp timestamp, notifica_des_dir3desc varchar(100), notifica_des_dir3codi varchar(9), notifica_emi_dir3desc varchar(100), notifica_emi_dir3codi varchar(9), notifica_error bit not null, notifica_estat integer not null, notifica_estat_data timestamp, notifica_estat_desc varchar(255), notifica_estat_numseg varchar(50), notifica_estat_origen varchar(20), notifica_estat_recnif varchar(9), notifica_estat_recnom varchar(100), notifica_id varchar(20), notifica_ref varchar(20), retard_postal integer, servei_tipus integer, seu_data_enviam timestamp, seu_data_estat timestamp, seu_data_fi timestamp, seu_data_notidp timestamp, seu_data_notinf timestamp, seu_error bit not null, seu_estat integer not null, seu_reg_data timestamp, seu_reg_numero varchar(50), titular_email varchar(100), titular_llinatge1 varchar(125), titular_llinatge2 varchar(125), titular_nif varchar(9) not null, titular_nom varchar(125), titular_telefon varchar(16), createdBy_codi varchar(64), lastModifiedBy_codi varchar(64), notifica_error_event_id bigint, notificacio_id bigint not null, seu_error_event_id bigint, primary key (id))
create table not_notificacio_event (id bigint generated by default as identity (start with 1), createdDate timestamp, lastModifiedDate timestamp, data timestamp not null, descripcio varchar(256), error bit not null, error_desc varchar(2048), tipus integer not null, createdBy_codi varchar(64), lastModifiedBy_codi varchar(64), notificacio_env_id bigint, notificacio_id bigint not null, primary key (id))
create table not_usuari (codi varchar(64) not null, email varchar(200), llinatges varchar(100), nom varchar(100), nom_sencer varchar(200), version bigint not null, primary key (codi))
alter table not_aplicacio add constraint FK9B95B2D3909872D3 foreign key (lastModifiedBy_codi) references not_usuari
alter table not_aplicacio add constraint FK9B95B2D3894BB72A foreign key (createdBy_codi) references not_usuari
alter table not_entitat add constraint FK42CD649D909872D3 foreign key (lastModifiedBy_codi) references not_usuari
alter table not_entitat add constraint FK42CD649D894BB72A foreign key (createdBy_codi) references not_usuari
alter table not_notificacio add constraint FKF6C118A6909872D3 foreign key (lastModifiedBy_codi) references not_usuari
alter table not_notificacio add constraint not_notevenot_notificacio_fk foreign key (error_not_event_id) references not_notificacio_event
alter table not_notificacio add constraint FKF6C118A6894BB72A foreign key (createdBy_codi) references not_usuari
alter table not_notificacio add constraint not_entitat_notificacio_fk foreign key (entitat_id) references not_entitat
alter table not_notificacio_env add constraint not_noteve_noterr_notdest_fk foreign key (notifica_error_event_id) references not_notificacio_event
alter table not_notificacio_env add constraint FK9C50DD4909872D3 foreign key (lastModifiedBy_codi) references not_usuari
alter table not_notificacio_env add constraint not_notificacio_notdest_fk foreign key (notificacio_id) references not_notificacio
alter table not_notificacio_env add constraint FK9C50DD4894BB72A foreign key (createdBy_codi) references not_usuari
alter table not_notificacio_env add constraint not_noteve_seuerr_notdest_fk foreign key (seu_error_event_id) references not_notificacio_event
alter table not_notificacio_event add constraint FKACBC59C1909872D3 foreign key (lastModifiedBy_codi) references not_usuari
alter table not_notificacio_event add constraint not_notifi_noteve_fk foreign key (notificacio_id) references not_notificacio
alter table not_notificacio_event add constraint FKACBC59C1894BB72A foreign key (createdBy_codi) references not_usuari
alter table not_notificacio_event add constraint not_notenv_noteve_fk foreign key (notificacio_env_id) references not_notificacio_env
