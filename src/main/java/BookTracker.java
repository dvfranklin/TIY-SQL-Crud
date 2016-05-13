
import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.halt;

public class BookTracker {


      public static void main(String[] args) throws SQLException{

        // establish jdbc connection and initialize db
        Server server = Server.createTcpServer("-baseDir", "./data").start();
        Connection connection = DriverManager.getConnection("jdbc:h2:" + server.getURL() + "/main");
        BookTrackerService service = new BookTrackerService(connection);
        service.initDatabase();

        server.createWebServer().start();

        Spark.get(
                "/",
                (request,response) -> {
                    HashMap m = new HashMap();
                    User user = new User();

                    if(!request.session().attributes().contains("user")){

                        // if not logged in, or gave bad login info
                        m.put("missingInfo", request.queryParams("missingInfo"));
                        m.put("badInfo", request.queryParams("badInfo"));
                    } else {

                        // new user in session
                        user = request.session().attribute("user");
                        m.put("user", user);
                        m.put("userName", user.getUsername());
                        ArrayList<Book> books = service.selectBooks(user.getUserId());
                        m.put("books", books);


                    }

                    return new ModelAndView(m, "booktracker.mustache");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    // get username & password from login form
                    String userName = request.queryParams("userName");
                    String password = request.queryParams("password");

                    // if either field is null, return an error
                    if(userName.equals("") || password.equals("")){
                        response.redirect("/?missingInfo=true");
                        halt();
                    }

                    User user = service.selectUser(userName);

                    // if user doesn't exist, or password is incorrect, return error
                    if((user == null) || !user.getPassword().equals(password)){
                        response.redirect("/?badInfo=true");
                        halt();
                    }

                    // add user to the session and redirect to webroot
                    request.session().attribute("user", user);
                    response.redirect("/");
                    halt();

                    return "";
                }
        );

        // send user to signup page
        Spark.get(
                "/signup",
                (request, response) -> {
                    // displays the signup page
                    return new ModelAndView(null, "signup.mustache");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/create-user",
                (request, response) -> {

                    // create new user with user & password provided, add to jdb
                    User user = new User(request.queryParams("userName"), request.queryParams("password"));
                    service.insertUser(user);

                    // add user to session and redirect to webroot
                    request.session().attribute("user", user);
                    response.redirect("/");
                    halt();
                    return "";
                }
        );

        Spark.post(
                "/create-book",
                (request, response) -> {


                    String title = request.queryParams("title");
                    String author = request.queryParams("author");
                    String genre = request.queryParams("genre");
                    User user = request.session().attribute("user");
                    Book book = new Book(title, author, genre, user);
                    service.insertBook(book);


                    // redirect to webroot
                    response.redirect("/");
                    halt();


                    return "";
                }
        );

        Spark.post(
                "/delete",
                (request, response) -> {

                    // get the requested message from User's ArrayList of messages
                    // delete the requested message
                    int deleteId = Integer.valueOf(request.queryParams("bookId"));

                    service.deleteBook(deleteId);

                    // redirect to webroot
                    response.redirect("/");
                    halt();

                    return "";
                }
        );

        Spark.post(
                "/edit",
                (request, response) -> {
                    String newTitle = request.queryParams("title");
                    String newAuthor = request.queryParams("author");
                    String newGenre = request.queryParams("genre");
                    int editId = Integer.valueOf(request.queryParams("bookId"));
                    User user = request.session().attribute("user");


                    Book book = new Book(editId, newTitle, newAuthor, newGenre, user);
                    service.updateBook(book);

                    response.redirect("/");
                    halt();

                    return "";
                }
        );

        Spark.get(
                "/edit",
                (request, response) -> {

                    HashMap m = new HashMap();
                    int editId = Integer.valueOf(request.queryParams("bookId"));
                    Book book = service.selectBook(editId);

                    m.put("book", book);


                    return new ModelAndView(m, "/editbook.mustache");
                },
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/logout",
                (request, response) -> {
                    // kill session, return to root for login
                    request.session().invalidate();
                    response.redirect("/");
                    halt();
                    return null;
                },
                new MustacheTemplateEngine()
        );
    }
}
