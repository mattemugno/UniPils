package it.unipi.lsmdb.bean;

public class OrderList {

    private int beerId;
    private String beerName;
    private int beerPrice;
    private int quantity;

    public OrderList(int beerId,String beerName,int beerPrice,int quantity){
        this.beerId=beerId;
        this.beerName=beerName;
        this.beerPrice=beerPrice;
        this.quantity=quantity;
    }

    public OrderList(){
    }

    public int getBeerId(){
        return beerId;
    }

    public String getBeerName(){
        return beerName;
    }

    public int getBeerPrice(){
        return beerPrice;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setBeerId(int beerId){
        this.beerId=beerId;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public void setBeerPrice(int beerPrice) {
        this.beerPrice = beerPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderList{" +
                "beerId=" + beerId +
                ", beerName='" + beerName + '\'' +
                ", beerPrice=" + beerPrice +
                ", quantity=" + quantity +
                '}';
    }
}
