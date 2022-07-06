package com.tw.vapsi.biblioteca.model;

import com.tw.vapsi.biblioteca.model.validator.After1800AndBeforeNextYear;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Invalid Book name")
    private String name;

    @NotBlank(message = "Invalid Author name")
    private String author;

    @NotBlank(message = "Invalid Genre name")
    @Column(name="genere")
    private String genre;

    private int quantity = 1;

    @Column(name="isavailable")
    private boolean isAvailable = true;


    @After1800AndBeforeNextYear(message = "Year should be greater than 1800 and till current year")
    @Column(name="yearofpublish")
    private Integer yearOfPublish;

    @ManyToOne
    @JoinTable(name = "borrowed_books",
            joinColumns = {@JoinColumn(name = "book_id", insertable = false,
                    updatable = false, referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", insertable = false,
                    updatable = false, referencedColumnName = "id")}
    )
    private User user;

    public Book() {
    }

    public Book(String name, String author, String genere, int quantity, boolean isAvailable, int yearOfPublish) {
        this.name = name;
        this.author = author;
        this.genre = genere;
        this.quantity = quantity;
        this.isAvailable = isAvailable;
        this.yearOfPublish = yearOfPublish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id && quantity == book.quantity && isAvailable == book.isAvailable && Objects.equals(yearOfPublish, book.yearOfPublish) && name.equals(book.name) && author.equals(book.author) && Objects.equals(genre, book.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, author, genre, quantity, isAvailable, yearOfPublish);
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public Integer getYearOfPublish() {
        return yearOfPublish;
    }

    public void setYearOfPublish(Integer yearOfPublish) {
        this.yearOfPublish = yearOfPublish;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
