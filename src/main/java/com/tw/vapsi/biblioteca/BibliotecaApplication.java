package com.tw.vapsi.biblioteca;

import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.repository.BooksRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BibliotecaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotecaApplication.class, args);
	}

	@Bean
	public CommandLineRunner setUp(BooksRepository repository) {
		return (args) -> {
			Book book1 = new Book("Northanger Abbey", "Austen, Jane","General",1, true,1814);
			Book book2 = new Book("War and Peace", "Tolstoy, Leo", "General",1, true,1865);
			repository.save(book1);
			repository.save(book2);

		};
	}
}
