# --- !Ups

insert into user (id, username, email, password, active) values(1, 'admin', 'lowery@gmail.com', '$2a$10$C6PJ9ED16fpZHSPpapJOxOk7AbjLl9w/wWAC6c4mRURUz4pLCbWTi', TRUE);

insert into security_role (id, name) values(1, 'ADMIN');
insert into security_role (id, name) values(2, 'USER');

insert into user_security_role (user_id, security_role_id) values(1, 1);

# --- !Downs

delete from user where username = "admin";