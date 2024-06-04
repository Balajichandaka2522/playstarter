package models;


import org.bson.Document;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Book {

    @JsonProperty("price")
    public Integer price;
    @JsonProperty("name")
    public String name;
    @JsonProperty("author")
    public String author;
    @JsonProperty("status")
    public String publisher;
    @JsonProperty("publisher")
    public String status;

    public String getStatus() {
        return status;
    }


    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }




    public Book(){

    }
    public Book(Integer p,String n,String aut,String s,String pub){
        this.price=p;
        this.author=aut;
        this.name=n;
        this.status=s;
        this.publisher=pub;

    }
    public Document toDocument(){
        Document d=new Document();
        d.append("price",price);
        d.append("author",author);
        d.append("name",name);
        d.append("status",status);
        d.append("publisher",publisher);

        return d;
    }

    public void setName(String name) {
        this.name=name;
    }

    public void setPrice(Integer price) {
        this.price=price;
    }

    public void setAuthor(String author) {
        this.author=author;
    }

    public void setStatus(String status) {
        this.status=status;
    }
}
