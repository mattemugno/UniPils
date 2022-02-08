package it.unipi.lsmdb.bean;

import java.time.LocalDate;

public class User {

    private String username;
    private String password;
    private String first;
    private String last;
    private String gender;
    private String email;
    private LocalDate dob;
    private String cell;

    public User(String username, String first, String last, String email, String gender, LocalDate dob, String cell ){
        this.username=username;
        this.first=first;
        this.last=last;
        this.email=email;
        this.gender=gender;
        this.dob=dob;
        this.cell=cell;
    }

    public User(String username, String password){
        this.username=username;
        this.password=password;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail(){
        return email;
    }

    public String getFirst(){
        return first;
    }

    public String getLast(){
        return last;
    }
    public String getCellular(){
        return cell;
    }

    public String getGender(){
        return gender;
    }

    public LocalDate getDob(){
        return dob;
    }




}
