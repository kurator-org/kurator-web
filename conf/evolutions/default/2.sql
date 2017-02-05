# --- !Ups

insert into user (id, username, email, password, role, active) values(0, 'admin', 'lowery@gmail.com', '$2a$10$TXASsPiH2GhjqsGb8MMPiOkMLiZ5s6l7PO0xTTlXMweMz1PD5JoCa', 'ADMIN', TRUE);

# --- !Downs

delete from user where username = "admin";