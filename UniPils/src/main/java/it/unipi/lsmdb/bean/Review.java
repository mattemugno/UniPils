package it.unipi.lsmdb.bean;


import java.util.Date;

public class Review {

    private String comment;
    private int rate;
    private Date timestamp;

    public Review(){}

    public Review(String comment, int rate, Date timestamp){
        this.comment = comment;
        this.rate = rate;
        this.timestamp = timestamp;
    }
}
