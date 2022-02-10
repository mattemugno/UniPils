package it.unipi.lsmdb.bean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class User {

    private String gender;
    private String first;
    private String last;
    private String email;
    private String username;
    private String password;
    private LocalDate dob;
    private String cell;
    private ArrayList<Order> orders;
    private ArrayList<Payment> payments;
    private String address;

    public User(String gender, String first, String last, String email, String username, String password,
                LocalDate dob, String cell){
        this.first=first;
        this.last=last;
        this.username=username;
        this.password=password;
        this.email=email;
        this.gender=gender;
        this.dob=dob;
        this.cell=cell;
    }

    public String getGender(){
        return gender;
    }

    public LocalDate getDob() {
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

    public String getAddress() {
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

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public void setDob(LocalDate dob) {
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
}
