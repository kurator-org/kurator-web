CREATE TABLE task (
  id bigint(20) NOT NULL AUTO_INCREMENT
  name varchar(255) NOT NULL,
  done bit NOT NULL,
  dueDate DATE NOT NULL,
  PRIMARY KEY (id)
)