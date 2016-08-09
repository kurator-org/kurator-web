# --- !Ups

insert into user (id, username, password, active) values(0, 'admin', '$2a$10$XWZwG/VQ4TNXroeHV5mqfuORCA.5ULZ17zxatXxmVuFQp1NWYoXuu', TRUE);

# --- !Downs

delete from user where username = "admin";