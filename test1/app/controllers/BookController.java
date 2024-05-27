package controllers;

import models.Book;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import services.MongoBookService;
import services.MongoService;
import views.html.books.*;
import javax.inject.Inject;
import java.util.Set;

import static models.Book.books;

public class BookController extends Controller {
    @Inject
    FormFactory formFactory;

    private final MongoBookService mongoService;
    public Set<Book> books;

    @Inject
    public BookController(MongoBookService mongoService) {
        this.mongoService = mongoService;
        books = mongoService.getAllBooks();
    }
    public Result index(){
        return ok(views.html.books.index.render(books));
    }
    public Result show(Integer id){
        Book showBook=mongoService.findById(id);
        if(showBook==null){
            return notFound("Book not Found");
        }
        return ok(views.html.books.show.render(showBook));

    }
    public Result destroy(Integer id){
        mongoService.remove(id);
        return redirect(routes.BookController.index());
    }
    public Result update(){
        Book updateBook=formFactory.form(Book.class).bindFromRequest().get();
        Book oldBook=mongoService.findById(updateBook.id);
        if(oldBook==null){
            return notFound("Book not found");
        }
        mongoService.updateBook(oldBook.id,updateBook);
        return ok(views.html.books.index.render(books));
    }
    public Result create(){
        Form<Book> bf= formFactory.form(Book.class);
        return ok(create.render(bf));    }
    public Result save(){
        Form<Book> newForm=formFactory.form(Book.class).bindFromRequest();
        Book newBook=newForm.get();
        books.add(newBook);
        mongoService.updateCol();
        return redirect(routes.BookController.index());
    }
    public Result edit(Integer id){
        Book bk= mongoService.findById(id);
        if(bk==null){
            return notFound("Book not found");
        }
        Form<Book> editBook=formFactory.form(Book.class).fill(bk);
        return ok(views.html.books.edit.render(editBook));



    }



}
