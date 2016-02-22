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

CREATE TABLE workflow_run (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  workflow VARCHAR (255),
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  user_id bigint(20),
  output_text LONGTEXT,
  result_file VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES user(id)
);
-- username: admin, password: admin

INSERT INTO user (username, password) VALUES('admin', '$2a$10$8YF40nAp.y2iknDPi8.G1uqfv8u26ARgBvVncdr89BmXOim1nD6Qy');

# --- !Downs

DROP TABLE task;