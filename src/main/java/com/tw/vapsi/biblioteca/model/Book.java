package com.tw.vapsi.biblioteca.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;

    private String author;

    private String genere;

    private int quantity;

    private boolean isAvailable;

    private long yearOfPublish;

    public Book() {
    }

    public Book(String name, String author, String genere, int quantity, boolean isAvailable, long yearOfPublish) {
        this.name = name;
        this.author = author;
        this.genere = genere;
        this.quantity = quantity;
        this.isAvailable = isAvailable;
        this.yearOfPublish = yearOfPublish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id && quantity == book.quantity && isAvailable == book.isAvailable && yearOfPublish == book.yearOfPublish && name.equals(book.name) && author.equals(book.author) && Objects.equals(genere, book.genere);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, author, genere, quantity, isAvailable, yearOfPublish);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public long getYearOfPublish() {
        return yearOfPublish;
    }

    public void setYearOfPublish(long yearOfPublish) {
        this.yearOfPublish = yearOfPublish;
    }
}
