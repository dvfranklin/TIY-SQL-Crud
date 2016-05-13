import java.sql.*;
import java.util.ArrayList;

public class BookTrackerService {

    private final Connection connection;

    public BookTrackerService(Connection connection){
        this.connection = connection;
    }

    public void initDatabase() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS user (id IDENTITY, username VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS book (id IDENTITY, title VARCHAR, author VARCHAR, genre VARCHAR, userId INT)");
    }

    public void insertUser(User user) throws SQLException {
        PreparedStatement prepStmt = connection.prepareStatement("INSERT INTO user VALUES (NULL, ?, ?)");

        prepStmt.setString(1, user.getUsername());
        prepStmt.setString(2, user.getPassword());
        prepStmt.execute();

        ResultSet results = prepStmt.getGeneratedKeys();
        results.next();
        user.setUserId(results.getInt(1));
    }

    public User selectUser(String username) throws SQLException {
        User user = null;

        PreparedStatement prepStmt = connection.prepareStatement("SELECT * FROM user WHERE username = ?");
        prepStmt.setString(1, username);

        ResultSet results = prepStmt.executeQuery();
        if(results.next()) {
            user = new User(results.getInt("id"), results.getString("username"), results.getString("password"));
        }

        return user;
    }

    public void insertBook(Book book) throws SQLException {
        PreparedStatement prepStmt = connection.prepareStatement("INSERT INTO book VALUES(NULL, ?, ?, ?, ?)");
        prepStmt.setString(1, book.getTitle());
        prepStmt.setString(2, book.getAuthor());
        prepStmt.setString(3, book.getGenre());
        prepStmt.setInt(4, book.getUser().getUserId());
        prepStmt.execute();

        ResultSet results = prepStmt.getGeneratedKeys();
        results.next();
        book.setBookId(results.getInt(1));
    }

    public Book selectBook(int bookId) throws SQLException {
        Book book = null;

        PreparedStatement prepStmt = connection.prepareStatement("SELECT * FROM book INNER JOIN user ON book.userId = user.id WHERE book.id = ?");
        prepStmt.setInt(1, bookId);

        ResultSet results = prepStmt.executeQuery();
        if(results.next()){
            User user = new User(results.getString("username"));
            book = new Book(results.getInt("id"), results.getString("title"), results.getString("author"), results.getString("genre"), user);
        }

        return book;
    }

    public ArrayList<Book> selectBooks(int userId) throws SQLException {
        ArrayList<Book> books = new ArrayList<>();

        PreparedStatement prepStmt = connection.prepareStatement("SELECT * FROM book INNER JOIN user on book.userId = user.id WHERE user.id = ?");
        prepStmt.setInt(1, userId);

        ResultSet results = prepStmt.executeQuery();
        while (results.next()) {
            User user = new User(results.getString("username"));
            Book book = new Book(results.getInt("id"), results.getString("title"), results.getString("author"), results.getString("genre"), user);
            books.add(book);
        }

        return books;
    }

    public void updateBook(Book book) throws SQLException {
        PreparedStatement prepStmt = connection.prepareStatement("UPDATE book SET title = ?, author = ?, genre = ? WHERE id = ?");

        prepStmt.setString(1, book.getTitle());
        prepStmt.setString(2, book.getAuthor());
        prepStmt.setString(3, book.getGenre());
        prepStmt.setInt(4, book.getBookId());

        prepStmt.execute();
    }

    public void deleteBook(int bookId) throws SQLException {
        PreparedStatement prepStmt = connection.prepareStatement("DELETE FROM book WHERE id = ?");

        prepStmt.setInt(1, bookId);

        prepStmt.execute();
    }

}
