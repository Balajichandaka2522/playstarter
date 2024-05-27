package controllers;
import models.Book;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.books.*;
import javax.inject.Inject;
import java.util.Set;



public class BookController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result index(){
        Set<Book> books = Book.allBooks();
        return ok(index.render(books));
    }

    public Result create(){
        Form<Book> bookForm = formFactory.form(Book.class);
        return ok(create.render(bookForm));
    }
    public Result save(Http.Request request){
        Form<Book> bookForm = formFactory.form(Book.class).bindFromRequest(request);
        Book book = bookForm.get();
        Book.add(book);
        return redirect(routes.BookController.index());
    }
    public Result edit(Integer id){
        Book b = Book.findById(id);
        if(b==null){
            return notFound("Book not found");
        }
        Form<Book> bookForm = formFactory.form(Book.class).fill(b);
        return ok(edit.render(bookForm));
    }
    public Result update(Http.Request request){
        Book book = formFactory.form(Book.class).bindFromRequest(request).get();
        Book oldbook = Book.findById(book.id);
        if(oldbook==null){
            return notFound("Book not found");
        }
        oldbook.title= book.title;
        oldbook.price=book.price;
        oldbook.author= book.author;
        return redirect(routes.BookController.index());
    }
    public Result delete(Integer id){
        Book b = Book.findById(id);
        if(b==null){
            return notFound("Book not found");
        }
        Book.remove(b);
        return redirect(routes.BookController.index());
    }
    public Result show(Integer id){
        Book b = Book.findById(id);
        if(b==null){
            return notFound("Book not found");
        }
        return ok(show.render(b));
    }
}