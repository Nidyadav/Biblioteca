package com.tw.vapsi.biblioteca.repository;

import com.tw.vapsi.biblioteca.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface BooksRepository extends JpaRepository<Book,Long> {
    @Modifying
    @Query(value="insert into borrowed_books (user_id,book_id) values(?1,?2)",nativeQuery = true)
    public void checkOutBook(long userId, long bookId);
}
