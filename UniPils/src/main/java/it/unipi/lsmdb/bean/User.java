package it.unipi.lsmdb.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class User {

    private String gender;
    private String first;
    private String last;
    private String email;
    private String username;
    private String password;
    private LocalDateTime dob;
    private String cell;
    private ArrayList<Order> orders;
    private ArrayList<Payment> payments;
    private ArrayList<String> address;
    private int interactions;

    public User(String gender, String first, String last, String email, String username, String password,
                LocalDateTime dob, String cell){
        this.first=first;
        this.last=last;
        this.username=username;
        this.password=password;
        this.email=email;
        this.gender=gender;
        this.dob=dob;
        this.cell=cell;
        this.orders = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.address = new ArrayList<>();
    }

    public User(){};

    public User(String uname, String pass, String f, String l) {
        this.first=f;
        this.last=l;
        this.username=uname;
        this.password=pass;
    }

    public User (String username, int interactions){
        this.username = username;
        this.interactions = interactions;
    }

    public String getGender(){
        return gender;
    }

    public LocalDateTime getDob() {
        return dob;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public ArrayList<String> getAddress() {
        return address;
    }

    public String getCell() {
        return cell;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(ArrayList<String> address) {
        this.address = address;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public void setDob(LocalDateTime dob) {
        this.dob = dob;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public int getInteractions() {
        return interactions;
    }

    public void setInteractions(int interactions) {
        this.interactions = interactions;
    }

    @Override
    public String toString() {
        return "User{" +
                "gender='" + gender + '\'' +
                ", first='" + first + '\'' +
                ", last='" + last + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", dob=" + dob +
                ", cell='" + cell + '\'' +
                ", orders=" + orders +
                ", payments=" + payments +
                ", address='" + address + '\'' +
                '}';
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("name")
    private void unpackNestedName(Map<String, Object> name) {
        this.first = (String) name.get("first");
        this.last = (String) name.get("last");
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("login")
    private void unpackNestedLogin(Map<String, Object> login) {
        this.username = (String) login.get("username");
        this.password = (String) login.get("password");
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("dob")
    private void unpackNestedDob(Map<String, Object> dob) {
        //Map<String, Object> date = (Map<String, Object>) dob.get("date");
        String dobString = (String) dob.get("date");
        String date = dobString.replace("T", " ").replace("Z", "");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        this.dob = LocalDateTime.parse(date, format);
    }
}
