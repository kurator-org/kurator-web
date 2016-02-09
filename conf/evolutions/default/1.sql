# Users schema

# --- !Ups

CREATE TABLE task (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  done bit NOT NULL,
  due_date DATE NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE user (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  firstname varchar(255) NOT NULL,
  lastname varchar(255) NOT NULL,
  username VARCHAR (255) NOT NULL,
  password VARCHAR (255) NOT NULL,
  PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE task;