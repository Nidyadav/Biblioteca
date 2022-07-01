CREATE TABLE books (
	id bigserial PRIMARY KEY,
	name VARCHAR ( 50 ) NOT NULL,
	author VARCHAR ( 50 ) NOT NULL,
	genere VARCHAR ( 50 ) NOT NULL,
	quantity integer DEFAULT 1,
	isAvailable boolean DEFAULT true,
	yearOfPublish integer
);

CREATE TABLE users (
	id serial PRIMARY KEY,
	firstName VARCHAR ( 50 ) NOT NULL,
	lastName VARCHAR ( 50 ) NOT NULL,
	email VARCHAR ( 100 ) NOT NULL,
	password VARCHAR ( 100 ) NOT NULL,
	usertype VARCHAR ( 100 ) DEFAULT 'ROLE_USER',
	isloggedin boolean DEFAULT false
);

CREATE TABLE borrowed_books (
        user_id integer NOT NULL,
        book_id integer NOT NULL
        );

