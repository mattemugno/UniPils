package it.unipi.lsmdb.bean;


import java.util.Date;

public class Review {

    private String comment;
    private int score;


    public Review(String comment, int rate, Date timestamp){
        this.comment = comment;
        this.score = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int rate) {
        this.score = rate;
    }
}
