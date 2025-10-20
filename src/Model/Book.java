package Model;

public class Book {
    String bookName;
    String bookAuthor;
    String bookPublisher;
    String bookDescription;
    String bookCategory;
    String bookStatus = "在库";
    int totalBooks = 1;       // 该图书的总副本数（实例变量，非static）
    int availableBooks = 1;  // 可借的副本数（动态变化）
    public Book(String name, String author, String publisher, String description, String category) {
        this.bookName = name;
        this.bookAuthor = author;
        this.bookPublisher = publisher;
        this.bookDescription = description;
        this.bookCategory = category;
    }
    public Book(String name, String author, String publisher) {
        this.bookName = name;
        this.bookAuthor = author;
        this.bookPublisher = publisher;
    }
    public Book() {}
    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }


    public String getBookStatus() {
        if(availableBooks > 0)
            return "有余量";
        else
            return "全外借";
    }
    public int getAvailableBooks() {
        return availableBooks;
    }
    public void setAvailableBooks(int availableBooks) {
        this.availableBooks = availableBooks;
    }
    public int getTotalBooks() {
        return totalBooks;
    }
    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }
}
