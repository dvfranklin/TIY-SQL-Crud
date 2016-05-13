import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class BookTrackerServiceTest {

    Connection connection;
    BookTrackerService service;

    @Before
    public void Before() throws SQLException {
        Server server = Server.createTcpServer("-baseDir", "./data").start();
        connection = DriverManager.getConnection("jdbc:h2:" + server.getURL() + "/test");
        service = new BookTrackerService(connection);
    }

    @Test
    public void whenDatabaseInitializedThenTablesExist() throws SQLException {
        //arrange
        service.initDatabase();

        //act
        ResultSet tables = connection.createStatement().executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'"); //  AND TABLE_NAME in ('User', 'GroceryItem')

        ArrayList<String> tableNames = new ArrayList<>();

        while(tables.next()){
            tableNames.add(tables.getString("TABLE_NAME"));
        }

        //assert
        assertThat(tableNames, hasItems("USER", "BOOK"));
    }

    @Test
    public void whenUsersEnteredThenExistInDatabase() throws SQLException {
        //arrange
        service.initDatabase();
        User user = new User("franks", "password");

        //act
        service.insertUser(user);

        //assert
        assertThat(user.getUserId(), not(0));

    }

    @Test
    public void whenUserSelectedThenObjectCreated() throws SQLException {
        //arrange
        service.initDatabase();

        //act
        User user = new User("franks", "password");
        service.insertUser(user);
        User user2 = service.selectUser("franks");


        //assert
        assertThat(user2.getUsername(), is("franks"));
    }

    @Test
    public void whenBookEnteredThenExistInDatabase() throws SQLException {
        //arrange
        service.initDatabase();
        User user = new User("franks", "password");
        Book book = new Book("Ulysses", "James Joyce", "Literature", user);

        //act
        service.insertBook(book);

        //assert
        assertThat(book.getBookId(), not(0));
    }

    @Test
    public void whenBookSelectedThenObjectCreated() throws SQLException {
        //arrange
        service.initDatabase();
        service.insertUser(new User("franks", "password"));
        User user = service.selectUser("franks");
        Book book = new Book("Ulysses", "James Joyce", "Literature", user);

        //act
        service.insertBook(book);
        Book book2 = service.selectBook(1);

        //assert
        assertThat(book2.getBookId(), is(1));
    }

    @Test
    public void whenBooksAddedThenArrayListReturned() throws SQLException {
        //arrange
        service.initDatabase();
        service.insertUser(new User("franks", "password"));
        User user = service.selectUser("franks");
        ArrayList<Book> books = new ArrayList<>();

        //act
        service.insertBook(new Book("Ulysses", "James Joyce", "Literature", user));
        service.insertBook(new Book("Hamlet", "Billy Shakes", "Drama", user));
        service.insertBook(new Book("The Odyssey", "Homer", "Epic", user));
        books = service.selectBooks(1);

        //assert
        assertThat(3, is(books.size()));
    }

    @Test
    public void whenBookIsUpdatedThenObjectIsUpdated() throws SQLException {
        //arrange
        service.initDatabase();
        service.insertUser(new User("franks", "password"));
        User user = service.selectUser("franks");
        service.insertBook(new Book("Ulysses", "James Joyce", "Literature", user));
        Book book = service.selectBook(1);


        //act
        book = new Book(book.getBookId(), "Game of Thrones", "GRRM", "Fantasy", user);
        service.updateBook(book);

        //assert
        assertThat("GRRM", is(service.selectBook(1).getAuthor()));
    }

    @Test
    public void whenBookIsDeletedThenNoLongerExists() throws SQLException {
        //arrange
        service.initDatabase();
        service.insertUser(new User("franks, password"));
        User user = service.selectUser("franks");
        Book book = new Book("Ulysses", "James Joyce", "Literature", user);
        Book book2 = new Book("Finnegans Wake", "James Joyce", "Literature", user);

        //act
        service.insertBook(book);
        service.insertBook(book2);
        service.deleteBook(book.getBookId());

        //assert
        assertThat(service.selectBooks(user.getUserId()).size(), is(1));
    }

    @After
    public void after() throws SQLException {
        connection.close();

        File dataFolder = new File("data");
        if(dataFolder.exists()){
            for(File file : dataFolder.listFiles()){
                if(file.getName().startsWith("test.h2.")){
                    file.delete();
                }
            }
        }
    }
}
