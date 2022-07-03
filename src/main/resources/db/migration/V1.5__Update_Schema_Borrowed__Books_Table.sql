DROP TABLE IF EXISTS borrowed_books;

CREATE TABLE borrowed_books (
        user_id integer NOT NULL,
        book_id bigint NOT NULL,
        CONSTRAINT fk_user
              FOREIGN KEY(user_id)
        	        REFERENCES users(id),
        CONSTRAINT fk_book
              FOREIGN KEY(book_id)
                	 REFERENCES books(id)
        );