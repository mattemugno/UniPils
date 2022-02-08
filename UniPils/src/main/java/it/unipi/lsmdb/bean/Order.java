package it.unipi.lsmdb.bean;

import java.time.LocalDate;
import java.util.ArrayList;

public class Order {
    private int idOrder;
    private ArrayList<OrderList> orderList;
    private LocalDate deliveryDate;
    private Float feedback;
    private Float totalCost;
    private LocalDate confirmationDate;

    public Order(){

    }

    public Order(int idOrder,ArrayList<OrderList> orderList,LocalDate deliveryDate,Float feedback,Float totalCost,LocalDate confirmationDate){
        this.idOrder=idOrder;
        this.orderList=orderList;
        this.deliveryDate=deliveryDate;
        this.feedback=feedback;
        this.totalCost=totalCost;
        this.confirmationDate=confirmationDate;
    }

    public int getIdOrder(){
        return idOrder;
    }

    public ArrayList<OrderList> getOrderList(){
        return orderList;
    }

    public LocalDate getDeliveryDate(){
        return deliveryDate;
    }

    public Float getFeedback() {
        return feedback;
    }

    public Float getTotalCost() {
        return totalCost;
    }

    public LocalDate getConfirmationDate() {
        return confirmationDate;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public void setOrderList(ArrayList<OrderList> orderList) {
        this.orderList = orderList;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setFeedback(Float feedback) {
        this.feedback = feedback;
    }

    public void setTotalCost(Float totalCost) {
        this.totalCost = totalCost;
    }

    public void setConfirmationDate(LocalDate confirmationDate) {
        this.confirmationDate = confirmationDate;
    }
}
