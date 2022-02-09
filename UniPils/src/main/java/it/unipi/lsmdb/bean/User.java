package it.unipi.lsmdb.bean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class User {

    private String gender;
    private ArrayList<String> name;
    private String email;
    private ArrayList<String> login;
    private LocalDate dob;
    private String cell;
    private ArrayList<Order> orders;
    private ArrayList<Payment> payments;
    private String address;



    public User(String gender, ArrayList<String> name, String email, ArrayList<String> login,
                LocalDate dob, String cell,ArrayList<Order> orders,ArrayList<Payment> payments,String address){
        this.name=name;
        this.login=login;
        this.orders=orders;
        this.payments=payments;
        this.address=address;
        this.email=email;
        this.gender=gender;
        this.dob=dob;
        this.cell=cell;
    }

    public String getGender(){
        return gender;
    }

    public ArrayList<String> getName() {
        return name;
    }

    public ArrayList<String> getLogin() {
        return login;
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

    public void setName(ArrayList<String> name) {
        this.name = name;
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

    public void setLogin(ArrayList<String> login) {
        this.login = login;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }


}
