package com.example.barcode;


import java.io.Serializable;

public class Books implements Serializable {
    private  String  BookName;
    private  String Authors;
    private  String Bsbn;

    public Books() {
    }
    public Books(String BookName,String Authors, String Isbn){
        this.BookName=BookName;
        this.Authors= Authors;
        this.Bsbn=Isbn;
    }
    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getAuthors() {
        return Authors;
    }

    public void setAuthors(String authors) {
        Authors = authors;
    }

    public String getBsbn() {
        return Bsbn;
    }

    public void setBsbn(String bsbn) {
        Bsbn = bsbn;
    }
}
