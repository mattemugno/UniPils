package dii.unipi.lsmdb.bean;

import java.time.LocalDate;
import java.util.Date;

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



}
