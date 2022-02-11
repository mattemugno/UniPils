package it.unipi.lsmdb.bean;


import org.neo4j.driver.Value;

import java.time.LocalDateTime;
import java.util.Date;

public class Review {

    private String comment;
    private int score;
    private int scoreRev;
    private LocalDateTime timestamp;
    private Value sc;
    public Value ts;


    public Review(String comment, int score){
        this.comment = comment;
        this.score = score;
    }

    /*public Review(String comment, int scoreRev, LocalDateTime timestamp){
        this.comment = comment;
        this.scoreRev = scoreRev;
        this.timestamp = timestamp;
    }*/

    public Review(String comment, Value sc, Value ts) {
        this.comment = comment;
        this.sc = sc;
        this.ts = ts;
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

    public String getSc() {
        return String.valueOf(sc);
    }

    public String getTs() {
        return String.valueOf(ts);
    }
}
