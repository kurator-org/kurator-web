# --- !Ups

insert into user (id, username, password, active) VALUES(0, 'admin', '$2a$10$ssyqTsb3dOUpaFDjGu4WVu3jwiotEI5iWz5RTIFnfAoxbo8ExZbGO', TRUE);

# --- !Downs

update table user delete where username = "admin";