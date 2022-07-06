package com.tw.vapsi.biblioteca.repository;

import com.tw.vapsi.biblioteca.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface BooksRepository extends JpaRepository<Book,Long> {
    List<Book> findByNameAndYearOfPublish(String name, Integer yearOfPublish);
}
