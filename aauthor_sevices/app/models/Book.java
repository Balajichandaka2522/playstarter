package models;


import org.bson.Document;
import play.mvc.WebSocket;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Book {

    @JsonProperty("price")
    public Integer price;
    @JsonProperty("name")
    public String name;
    @JsonProperty("status")
    public String status;
    public Book(){

    }
    public Book(Integer p,String n,String s){
        this.price=p;
        this.name=n;
        this.status=s;
    }
    public Document toDocument(){
        Document d=new Document();
        d.append("price",price);
        d.append("name",name);
        d.append("status",status);
        return d;
    }

    public void setName(String name) {
        this.name=name;
    }

    public void setPrice(Integer price) {
        this.price=price;
    }

    public void setStatus(String status) {
        this.status=status;
    }
}
