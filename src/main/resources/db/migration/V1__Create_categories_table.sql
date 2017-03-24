CREATE TABLE categories (
  id          INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(255) NOT NULL
);
ALTER TABLE categories ADD CONSTRAINT categories_unique_title UNIQUE (title);
