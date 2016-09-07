# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table result_file (
  id                            bigint not null,
  workflow_result_id            bigint not null,
  label                         varchar(255),
  file_name                     varchar(255),
  constraint pk_result_file primary key (id)
);
create sequence result_file_seq;

create table user (
  id                            bigint not null,
  firstname                     varchar(255),
  lastname                      varchar(255),
  email                         varchar(255),
  username                      varchar(255),
  password                      varchar(255),
  affiliation                   varchar(255),
  active                        boolean,
  constraint pk_user primary key (id)
);
create sequence user_seq;

create table user_upload (
  id                            bigint not null,
  absolute_path                 varchar(255),
  file_name                     varchar(255),
  user_id                       bigint,
  constraint pk_user_upload primary key (id)
);
create sequence user_upload_seq;

create table workflow (
  id                            bigint not null,
  name                          varchar(255),
  title                         varchar(255),
  yaml_file                     varchar(255),
  constraint pk_workflow primary key (id)
);
create sequence workflow_seq;

create table workflow_result (
  id                            bigint not null,
  error_text                    clob,
  output_text                   clob,
  archive_path                  varchar(255),
  constraint pk_workflow_result primary key (id)
);
create sequence workflow_result_seq;

create table workflow_run (
  id                            bigint not null,
  workflow_id                   bigint,
  start_time                    timestamp,
  end_time                      timestamp,
  result_id                     bigint,
  user_id                       bigint,
  constraint uq_workflow_run_result_id unique (result_id),
  constraint pk_workflow_run primary key (id)
);
create sequence workflow_run_seq;

alter table result_file add constraint fk_result_file_workflow_result_id foreign key (workflow_result_id) references workflow_result (id) on delete restrict on update restrict;
create index ix_result_file_workflow_result_id on result_file (workflow_result_id);

alter table user_upload add constraint fk_user_upload_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_upload_user_id on user_upload (user_id);

alter table workflow_run add constraint fk_workflow_run_workflow_id foreign key (workflow_id) references workflow (id) on delete restrict on update restrict;
create index ix_workflow_run_workflow_id on workflow_run (workflow_id);

alter table workflow_run add constraint fk_workflow_run_result_id foreign key (result_id) references workflow_result (id) on delete restrict on update restrict;

alter table workflow_run add constraint fk_workflow_run_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_workflow_run_user_id on workflow_run (user_id);


# --- !Downs

alter table result_file drop constraint if exists fk_result_file_workflow_result_id;
drop index if exists ix_result_file_workflow_result_id;

alter table user_upload drop constraint if exists fk_user_upload_user_id;
drop index if exists ix_user_upload_user_id;

alter table workflow_run drop constraint if exists fk_workflow_run_workflow_id;
drop index if exists ix_workflow_run_workflow_id;

alter table workflow_run drop constraint if exists fk_workflow_run_result_id;

alter table workflow_run drop constraint if exists fk_workflow_run_user_id;
drop index if exists ix_workflow_run_user_id;

drop table if exists result_file;
drop sequence if exists result_file_seq;

drop table if exists user;
drop sequence if exists user_seq;

drop table if exists user_upload;
drop sequence if exists user_upload_seq;

drop table if exists workflow;
drop sequence if exists workflow_seq;

drop table if exists workflow_result;
drop sequence if exists workflow_result_seq;

drop table if exists workflow_run;
drop sequence if exists workflow_run_seq;

