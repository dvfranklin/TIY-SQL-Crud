public class Book {

    private String title;
    private String author;
    private String genre;
    private int bookId;
    private User owner;


    public Book(int bookId, String title, String author, String genre, User owner){
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.owner = owner;
    }

    public Book(String title, String author, String genre, User owner){
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public User getUser() {
        return owner;
    }

    public void setUser(User owner) {
        this.owner = owner;
    }

    public String toString(){
        return "Title: " + this.title + "\n" +
                "Author: " + this.author + "\n" +
                "Genre: " + this.genre + "\n";
    }

}
