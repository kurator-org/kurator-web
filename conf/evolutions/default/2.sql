# --- !Ups

insert into user (id, username, password, role, active) values(0, 'admin', '$2a$10$XWZwG/VQ4TNXroeHV5mqfuORCA.5ULZ17zxatXxmVuFQp1NWYoXuu', 'ADMIN', TRUE);

# --- !Downs

delete from user where username = "admin";