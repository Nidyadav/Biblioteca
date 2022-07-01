DROP TABLE IF EXISTS borrowed_books;
DROP TABLE IF EXISTS users;


CREATE TABLE borrowed_books (
        user_id integer NOT NULL,
        book_id bigint NOT NULL
        );

CREATE TABLE users (
	id serial PRIMARY KEY,
	firstName VARCHAR ( 50 ) NOT NULL,
	lastName VARCHAR ( 50 ) NOT NULL,
	email VARCHAR ( 100 ) NOT NULL,
	password VARCHAR ( 100 ) NOT NULL,
	role VARCHAR ( 100 ) DEFAULT 'USER'
);