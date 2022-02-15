package it.unipi.lsmdb.bean;

public class Payment {
    private int cardNumber;
    private int CVV;
    private String expDate;

    public Payment(int cardNumber, int CVV, String expDate) {
        this.cardNumber = cardNumber;
        this.CVV = CVV;
        this.expDate = expDate;
    }

    public Payment() {
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getCVV() {
        return CVV;
    }

    public void setCVV(int CVV) {
        this.CVV = CVV;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "cardNumber='" + cardNumber + '\'' +
                ", CVV=" + CVV +
                ", expDate='" + expDate + '\'' +
                '}';
    }
}
