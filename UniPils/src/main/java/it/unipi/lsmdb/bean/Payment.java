package it.unipi.lsmdb.bean;

public class Payment {
    private String cardNumber;
    private int CVV;
    private String expDate;

    public Payment(String cardNumber,int CVV,String expDate){
        this.cardNumber=cardNumber;
        this.CVV=CVV;
        this.expDate=expDate;
    }
    public String getCardNumber(){
        return cardNumber;
    }

    public int getCVV(){
        return CVV;
    }

    public String getExpDate(){
        return expDate;
    }

    public void setCardNumber(String cardNumber){
        this.cardNumber=cardNumber;
    }

    public void setCVV(int CVV){
        this.CVV=CVV;
    }

    public void setExpDate(String expDate){
        this.expDate=expDate;
    }
}
