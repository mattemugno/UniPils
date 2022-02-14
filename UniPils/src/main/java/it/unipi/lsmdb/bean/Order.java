package it.unipi.lsmdb.bean;

import java.util.ArrayList;

public class Order {
    private int id_order;
    private ArrayList<OrderList> orderList = new ArrayList<>();
    private String delivery_date;
    private double feedback;
    private int total_cost;
    private String confirmation_date;

    public Order(){
        this.total_cost = 0;
    }

    public Order(int idOrder,ArrayList<OrderList> orderList,String deliveryDate,double feedback,int totalCost,String confirmationDate){
        this.id_order=idOrder;
        this.orderList=orderList;
        this.delivery_date=deliveryDate;
        this.feedback=feedback;
        this.total_cost=totalCost;
        this.confirmation_date=confirmationDate;
    }

    public int getIdOrder(){
        return id_order;
    }

    public ArrayList<OrderList> getOrderList(){
        return orderList;
    }

    public String getDeliveryDate(){
        return delivery_date;
    }

    public double getFeedback() {
        return feedback;
    }

    public int getTotalCost() {
        return total_cost;
    }

    public String getConfirmationDate() {
        return confirmation_date;
    }

    public void setIdOrder(int idOrder) {
        this.id_order = idOrder;
    }

    public void setOrderList(OrderList orderList) {
        this.orderList.add(orderList);
    }

    public void setDeliveryDate(String deliveryDate) {
        this.delivery_date = deliveryDate;
    }

    public void setFeedback(double feedback) {
        this.feedback = feedback;
    }

    public void setTotalCost(int totalCost) {
        this.total_cost += totalCost;
    }

    public void setConfirmationDate(String confirmationDate) {
        this.confirmation_date = confirmationDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "idOrder=" + id_order +
                ", orderList=" + orderList +
                ", deliveryDate=" + delivery_date +
                ", feedback=" + feedback +
                ", totalCost=" + total_cost +
                ", confirmationDate=" + confirmation_date +
                '}';
    }
}
