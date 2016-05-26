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
  active VARCHAR (255) NOT NULL,
  affiliation VARCHAR (255),
  PRIMARY KEY (id)
);

CREATE TABLE workflow_result (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  output_text LONGTEXT,
  error_text LONGTEXT,
  archive_path VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE result_file (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  label VARCHAR(255),
  file_name VARCHAR(255),
  workflow_result_id BIGINT(20),
  PRIMARY KEY (id),
  FOREIGN KEY (workflow_result_id) REFERENCES workflow_result(id)
);

CREATE TABLE workflow (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name VARCHAR (255),
  title VARCHAR (255),
  output_format VARCHAR (255),
  PRIMARY KEY (id)
);

CREATE TABLE workflow_run (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  workflow_id BIGINT(20),
  user_id BIGINT(20),
  result_id BIGINT(20),
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES user(id),
  FOREIGN KEY (result_id) REFERENCES workflow_result(id)
);

CREATE TABLE user_upload (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    absolute_path VARCHAR(255),
    file_name VARCHAR(255),
    user_id BIGINT(20),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- username: admin, password: admin

INSERT INTO user (username, password, active) VALUES('admin', '$2a$10$8YF40nAp.y2iknDPi8.G1uqfv8u26ARgBvVncdr89BmXOim1nD6Qy', TRUE);

# --- !Downs

DROP TABLE task;