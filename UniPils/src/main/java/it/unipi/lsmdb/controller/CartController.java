package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Order;
import it.unipi.lsmdb.bean.OrderList;
import it.unipi.lsmdb.bean.Payment;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.LevelDbDriver;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    @FXML
    Label title;
    @FXML
    VBox cartInfoPane;
    @FXML
    Button submit;
    @FXML
    Label totalPrice;
    @FXML
    Label defaultDA;
    @FXML
    TextField insertDA;
    @FXML
    Label defaultPM;
    @FXML
    TextField insertCVV;
    @FXML
    TextField insertcardNumber;
    @FXML
    TextField insertExpDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String username = DataSession.getUserLogged();
        title.setText(username + " 's Cart");
        printBeersCart(username);
    }

    private void printBeersCart(String username) {

        Font font = Font.font("Comic Sans", FontWeight.BOLD, 18);

        LevelDbDriver levelDbDriver = new LevelDbDriver();
        List<String> keyList;

        keyList = levelDbDriver.findKeysByPrefix(username);

        int totalPricePrinted = 0;


        for (String key : keyList) {

            if (key.split(":")[2].contains("name")) {

                int beer_id = levelDbDriver.splitKeys(key);
                key = username + ":" + beer_id + ":" + "name";

                String beer_name = levelDbDriver.getString(key);

                double space = 5;
                VBox beer = new VBox(space);
                beer.setMinWidth(600);
                beer.setStyle("-fx-border-style: solid inside;"
                        + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                        + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

                Label titleId = new Label();
                titleId.setText("Beer ID:  " + beer_id + "     " + "Beer Name:  " + beer_name);
                titleId.setFont(font);

                Label titlePrice = new Label();
                int price = Integer.parseInt(levelDbDriver.getString(username + ":" + beer_id + ":" + "price"));
                titlePrice.setText("Price:  " + price + " USD");
                titlePrice.setFont(font);

                beer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    DataSession.setIdBeerToShow(beer_id);
                    Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", event);
                    event.consume();
                });

                Button buttonRemove = new Button();
                buttonRemove.setText("Remove beer");
                buttonRemove.setOnAction(e -> {
                    if (deleteItemFromCart(username, beer_id, e))
                        Utils.showInfoAlert("Beer removed successfully");
                    else Utils.showErrorAlert("Beer not removed");
                });

                Label quantityLabel = new Label();
                quantityLabel.setText("Quantity: ");

                int quantity = Integer.parseInt(levelDbDriver.getString(username + ":" + beer_id + ":" + "quantity"));
                TextField textField = new TextField();
                textField.setText(String.valueOf(quantity));
                textField.setMaxWidth(100);

                totalPricePrinted += price * quantity;

                Button buttonQuantity = new Button();
                buttonQuantity.setText("Update quantity");
                buttonQuantity.setOnAction(e -> {
                    if (updateQuantity(beer_id, e, textField.getText()))
                        Utils.showInfoAlert("Quantity updated");
                    else Utils.showErrorAlert("Quantity not updated");
                });

                HBox hBox = new HBox(buttonRemove, buttonQuantity);//Add choiceBox and textField to hBox

                beer.getChildren().addAll(quantityLabel, textField, hBox, titleId, titlePrice);
                cartInfoPane.getChildren().add(beer);
            }
        }

        String deliveryAddress = MongoDriver.getAddresses(username).get(0);
        defaultDA.setText(deliveryAddress);
        totalPrice.setText(totalPrice.getText() + " " + totalPricePrinted + " USD");

        ArrayList<Payment> payment = MongoDriver.getPaymentsFromUsername(username);
        defaultPM.setText("CVV: " + payment.get(0).getCVV() + ", Card Number: " + payment.get(0).getCVV() + ", Exp Date: " + payment.get(0).getExpDate());
    }

    private boolean deleteItemFromCart(String username, int beer_id, ActionEvent actionEvent) {

        try {
            LevelDbDriver levelDbDriver = new LevelDbDriver();

            String keyName = username + ":" + beer_id + ":" + "name";
            String keyPrice = username + ":" + beer_id + ":" + "price";
            String keyQuantity = username + ":" + beer_id + ":" + "quantity";

            levelDbDriver.deleteValue(keyName);
            levelDbDriver.deleteValue(keyPrice);
            levelDbDriver.deleteValue(keyQuantity);

            Utils.changeScene("cart-page.fxml", actionEvent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateQuantity(int beer_id, ActionEvent actionEvent, String quantity){

        String username = DataSession.getUserLogged();
        LevelDbDriver levelDbDriver = new LevelDbDriver();
        String key = username + ":" + beer_id + ":" + "quantity";

        try {
            levelDbDriver.put(key, quantity);
            Utils.changeScene("cart-page.fxml", actionEvent);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    @FXML
    private boolean confirmOrder(ActionEvent actionEvent) {

        String username = DataSession.getUserLogged();

        LevelDbDriver levelDbDriver = new LevelDbDriver();
        List<String> keys = levelDbDriver.findKeysByPrefix(username);

        Order order = new Order();

        order.setIdOrder(MongoDriver.getMaxIdOrder(username) + 1);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        String confDate = (ZonedDateTime.now().format(dtf));
        order.setConfirmationDate(confDate);
        order.setFeedback(3);

        if (!insertDA.getText().isBlank()) {
            MongoDriver.addDeliveryAd(insertDA.getText(), username);
        }

        if (!insertCVV.getText().isBlank() || !insertcardNumber.getText().isBlank() || !insertExpDate.getText().isBlank()){
            MongoDriver.addPayment(username, Integer.parseInt(insertCVV.getText()) , Integer.parseInt(insertcardNumber.getText()), insertExpDate.getText());
        }


        try {

            for (String keyName : keys) {

                if (keyName.split(":")[2].contains("name")){

                    int beer_id = levelDbDriver.splitKeys(keyName);

                    keyName = username + ":" + beer_id + ":" + "name";
                    String keyPrice = username + ":" + beer_id + ":" + "price";
                    String keyQuantity = username + ":" + beer_id + ":" + "quantity";

                    String beer_name = levelDbDriver.getString(keyName);

                    int quantity = Integer.parseInt(levelDbDriver.getString(keyQuantity));
                    int price = Integer.parseInt(levelDbDriver.getString(keyPrice));

                    order.setTotalCost(price * quantity);

                    OrderList orderList = new OrderList(beer_id, beer_name, price, quantity);

                    order.setOrderList(orderList);

                    levelDbDriver.deleteValue(keyName);
                    levelDbDriver.deleteValue(keyPrice);
                    levelDbDriver.deleteValue(keyQuantity);
                }

            }

            Utils.addOrder(username, order);
            Utils.changeScene("profile-user.fxml", actionEvent);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
