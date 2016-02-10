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
  firstname varchar(255),
  lastname varchar(255),
  email VARCHAR(255),
  username VARCHAR (255) NOT NULL,
  password VARCHAR (255) NOT NULL,
  affiliation VARCHAR (255),
  PRIMARY KEY (id)
);

INSERT INTO user (username, password) VALUES('admin', 'admin');

# --- !Downs

DROP TABLE task;