# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table result_file (
  id                            bigint auto_increment not null,
  workflow_result_id            bigint not null,
  label                         varchar(255),
  file_name                     varchar(255),
  description                   varchar(255),
  name                          varchar(255),
  constraint pk_result_file primary key (id)
);

create table security_role (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  constraint pk_security_role primary key (id)
);

create table user (
  id                            bigint auto_increment not null,
  firstname                     varchar(255),
  lastname                      varchar(255),
  email                         varchar(255) not null,
  username                      varchar(255),
  password                      varchar(255),
  affiliation                   varchar(255),
  created_on                    datetime(6),
  last_active                   datetime(6),
  active                        tinyint(1) default 0,
  constraint pk_user primary key (id)
);

create table user_user_group (
  user_id                       bigint not null,
  user_group_id                 bigint not null,
  constraint pk_user_user_group primary key (user_id,user_group_id)
);

create table user_security_role (
  user_id                       bigint not null,
  security_role_id              bigint not null,
  constraint pk_user_security_role primary key (user_id,security_role_id)
);

create table user_user_permission (
  user_id                       bigint not null,
  user_permission_id            bigint not null,
  constraint pk_user_user_permission primary key (user_id,user_permission_id)
);

create table user_workflow_run (
  user_id                       bigint not null,
  workflow_run_id               bigint not null,
  constraint pk_user_workflow_run primary key (user_id,workflow_run_id)
);

create table user_group (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  created_on                    datetime(6),
  constraint pk_user_group primary key (id)
);

create table user_group_workflow_run (
  user_group_id                 bigint not null,
  workflow_run_id               bigint not null,
  constraint pk_user_group_workflow_run primary key (user_group_id,workflow_run_id)
);

create table user_permission (
  id                            bigint auto_increment not null,
  permission_value              varchar(255),
  constraint pk_user_permission primary key (id)
);

create table user_upload (
  id                            bigint auto_increment not null,
  absolute_path                 varchar(255),
  file_name                     varchar(255),
  user_id                       bigint,
  constraint pk_user_upload primary key (id)
);

create table workflow (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  title                         varchar(255),
  yaml_file                     varchar(255),
  constraint pk_workflow primary key (id)
);

create table workflow_result (
  id                            bigint auto_increment not null,
  error_text                    longtext,
  output_text                   longtext,
  dq_report                     varchar(255),
  archive_path                  varchar(255),
  constraint pk_workflow_result primary key (id)
);

create table workflow_run (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  workflow_id                   bigint,
  pid                           bigint,
  workspace                     varchar(255),
  start_time                    datetime(6),
  end_time                      datetime(6),
  status                        varchar(7),
  result_id                     bigint,
  owner_id                      bigint,
  shared_on                     datetime(6),
  constraint ck_workflow_run_status check (status in ('SUCCESS','ERRORS','RUNNING')),
  constraint uq_workflow_run_result_id unique (result_id),
  constraint pk_workflow_run primary key (id)
);

alter table result_file add constraint fk_result_file_workflow_result_id foreign key (workflow_result_id) references workflow_result (id) on delete restrict on update restrict;
create index ix_result_file_workflow_result_id on result_file (workflow_result_id);

alter table user_user_group add constraint fk_user_user_group_user foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_user_group_user on user_user_group (user_id);

alter table user_user_group add constraint fk_user_user_group_user_group foreign key (user_group_id) references user_group (id) on delete restrict on update restrict;
create index ix_user_user_group_user_group on user_user_group (user_group_id);

alter table user_security_role add constraint fk_user_security_role_user foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_security_role_user on user_security_role (user_id);

alter table user_security_role add constraint fk_user_security_role_security_role foreign key (security_role_id) references security_role (id) on delete restrict on update restrict;
create index ix_user_security_role_security_role on user_security_role (security_role_id);

alter table user_user_permission add constraint fk_user_user_permission_user foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_user_permission_user on user_user_permission (user_id);

alter table user_user_permission add constraint fk_user_user_permission_user_permission foreign key (user_permission_id) references user_permission (id) on delete restrict on update restrict;
create index ix_user_user_permission_user_permission on user_user_permission (user_permission_id);

alter table user_workflow_run add constraint fk_user_workflow_run_user foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_workflow_run_user on user_workflow_run (user_id);

alter table user_workflow_run add constraint fk_user_workflow_run_workflow_run foreign key (workflow_run_id) references workflow_run (id) on delete restrict on update restrict;
create index ix_user_workflow_run_workflow_run on user_workflow_run (workflow_run_id);

alter table user_group_workflow_run add constraint fk_user_group_workflow_run_user_group foreign key (user_group_id) references user_group (id) on delete restrict on update restrict;
create index ix_user_group_workflow_run_user_group on user_group_workflow_run (user_group_id);

alter table user_group_workflow_run add constraint fk_user_group_workflow_run_workflow_run foreign key (workflow_run_id) references workflow_run (id) on delete restrict on update restrict;
create index ix_user_group_workflow_run_workflow_run on user_group_workflow_run (workflow_run_id);

alter table user_upload add constraint fk_user_upload_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_upload_user_id on user_upload (user_id);

alter table workflow_run add constraint fk_workflow_run_workflow_id foreign key (workflow_id) references workflow (id) on delete restrict on update restrict;
create index ix_workflow_run_workflow_id on workflow_run (workflow_id);

alter table workflow_run add constraint fk_workflow_run_result_id foreign key (result_id) references workflow_result (id) on delete restrict on update restrict;

alter table workflow_run add constraint fk_workflow_run_owner_id foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_workflow_run_owner_id on workflow_run (owner_id);


# --- !Downs

alter table result_file drop foreign key fk_result_file_workflow_result_id;
drop index ix_result_file_workflow_result_id on result_file;

alter table user_user_group drop foreign key fk_user_user_group_user;
drop index ix_user_user_group_user on user_user_group;

alter table user_user_group drop foreign key fk_user_user_group_user_group;
drop index ix_user_user_group_user_group on user_user_group;

alter table user_security_role drop foreign key fk_user_security_role_user;
drop index ix_user_security_role_user on user_security_role;

alter table user_security_role drop foreign key fk_user_security_role_security_role;
drop index ix_user_security_role_security_role on user_security_role;

alter table user_user_permission drop foreign key fk_user_user_permission_user;
drop index ix_user_user_permission_user on user_user_permission;

alter table user_user_permission drop foreign key fk_user_user_permission_user_permission;
drop index ix_user_user_permission_user_permission on user_user_permission;

alter table user_workflow_run drop foreign key fk_user_workflow_run_user;
drop index ix_user_workflow_run_user on user_workflow_run;

alter table user_workflow_run drop foreign key fk_user_workflow_run_workflow_run;
drop index ix_user_workflow_run_workflow_run on user_workflow_run;

alter table user_group_workflow_run drop foreign key fk_user_group_workflow_run_user_group;
drop index ix_user_group_workflow_run_user_group on user_group_workflow_run;

alter table user_group_workflow_run drop foreign key fk_user_group_workflow_run_workflow_run;
drop index ix_user_group_workflow_run_workflow_run on user_group_workflow_run;

alter table user_upload drop foreign key fk_user_upload_user_id;
drop index ix_user_upload_user_id on user_upload;

alter table workflow_run drop foreign key fk_workflow_run_workflow_id;
drop index ix_workflow_run_workflow_id on workflow_run;

alter table workflow_run drop foreign key fk_workflow_run_result_id;

alter table workflow_run drop foreign key fk_workflow_run_owner_id;
drop index ix_workflow_run_owner_id on workflow_run;

drop table if exists result_file;

drop table if exists security_role;

drop table if exists user;

drop table if exists user_user_group;

drop table if exists user_security_role;

drop table if exists user_user_permission;

drop table if exists user_workflow_run;

drop table if exists user_group;

drop table if exists user_group_workflow_run;

drop table if exists user_permission;

drop table if exists user_upload;

drop table if exists workflow;

drop table if exists workflow_result;

drop table if exists workflow_run;

