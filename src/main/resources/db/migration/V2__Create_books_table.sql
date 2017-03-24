CREATE TABLE books (
  id               INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  title            VARCHAR(255) NOT NULL,
  release_date     DATE NOT NULL,
  category_id      INTEGER REFERENCES categories(id),
  quantity         INTEGER,
  author           VARCHAR(255) NOT NULL
);
ALTER TABLE books ADD CONSTRAINT books_unique_title UNIQUE (title);
