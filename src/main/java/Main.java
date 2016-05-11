
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.halt;

public class Main {

    static HashMap<String, User> users = new HashMap<>();
    static ArrayList<Book> books = new ArrayList<>();

    // @Doug: sorry for using a global variable
    static int counter = 0;

    public static void main(String[] args){

        addTestUsers();
        addTestBooks();


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

                    }

                    try {
                        for (int i = 0; i < books.size(); i++) {
                            if (books.get(i).getUserName().equals(user.getUsername())) {
                                books.get(i).userNameMatches = true;
                            }
                        }
                    } catch (Exception e){
                        m.put("noBooks", e);
                    }

                    m.put("books", books);
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


                    // get the user from hashmap
                    User user = users.get(userName);

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

                    // create new user with user & password provided
                    User user = new User(request.queryParams("userName"), request.queryParams("password"));

                    // add new user to the Hashmap
                    users.put(user.getUsername(), user);


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
                    Book book = new Book(title, author, genre, user.getUsername(), counter++);
                    books.add(book);


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

                    for (int i = 0; i < books.size(); i++){
                        if (books.get(i).bookId == deleteId) {
                            books.remove(i);
                        }
                    }

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
                    Book book;

                    for (int i = 0; i < books.size(); i++){
                        if (books.get(i).bookId == editId) {
                            book = books.get(i);
                            if(!newTitle.equals("")){
                                book.setTitle(newTitle);
                            }

                            if(!newAuthor.equals("")){
                                book.setAuthor(newAuthor);
                            }

                            if(!newGenre.equals("")){
                                book.setGenre(newGenre);
                            }
                        }
                    }




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
                    Book book = books.get(editId);

                    m.put("book", book);


                    return new ModelAndView(m, "/editbook.mustache");
                },
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/logout",
                (request, response) -> {
                    // kill session, return to root for login
                    HashMap m = new HashMap();

                    try {
                        for (int i = 0; i < books.size(); i++) {
                                books.get(i).userNameMatches = false;
                        }
                    } catch (Exception e){
                        m.put("noBooks", e);
                    }


                    request.session().invalidate();
                    response.redirect("/");
                    halt();
                    return null;
                },
                new MustacheTemplateEngine()
        );


    }

    public static void addTestUsers(){
        users.put("franks", new User("franks", "password"));
        users.put("test", new User("test", "pw"));
        users.put("dvf", new User("dvf", "zmakqo"));
    }

    public static void addTestBooks(){
        books.add(new Book("Lord of the Rings", "J.R.R. Tolkien", "Fantasy", "franks", counter++));
        books.add(new Book("A Game of Thrones", "George R.R. Martin", "Fantasy", "franks", counter++));
        books.add(new Book("Ulysses", "James Joyce", "Literature", "dvf", counter++));
        books.add(new Book("Good to Great", "Jim Collins", "Non-Fiction", "dvf", counter++));
    }
}
