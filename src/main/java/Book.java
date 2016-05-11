public class Book {

    private String title;
    private String author;
    private String genre;
    private String userName;
    boolean userNameMatches = false;
    int bookId;

    public Book(){

    }

    public Book(String title, String author, String genre, String userName, int bookId) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.userName = userName;
        this.bookId = bookId;
    }

    public Book(String title, String author, String genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String toString(){
        return "Title: " + this.title + "\n" +
                "Author: " + this.author + "\n" +
                "Genre: " + this.genre + "\n";
    }

}
