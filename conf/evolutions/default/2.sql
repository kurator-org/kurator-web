# --- !Ups

insert into user (id, username, email, password, active) values(0, 'admin', 'lowery@gmail.com', '$2a$10$TXASsPiH2GhjqsGb8MMPiOkMLiZ5s6l7PO0xTTlXMweMz1PD5JoCa', TRUE);

insert into security_role (id, name) values(0, 'ADMIN');
insert into security_role (id, name) values(1, 'USER');

insert into user_security_role (user_id, security_role_id) values(0, 0);

# --- !Downs

delete from user where username = "admin";