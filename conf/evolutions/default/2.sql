# --- !Ups

insert into user (id, username, email, password, active) values(0, 'admin', 'lowery@gmail.com', '$2a$10$C6PJ9ED16fpZHSPpapJOxOk7AbjLl9w/wWAC6c4mRURUz4pLCbWTi', TRUE);

insert into security_role (id, name) values(0, 'ADMIN');
insert into security_role (id, name) values(1, 'USER');

insert into user_security_role (user_id, security_role_id) values(0, 0);

# --- !Downs

delete from user where username = "admin";