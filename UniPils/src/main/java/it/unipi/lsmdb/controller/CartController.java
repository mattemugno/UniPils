package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Order;
import it.unipi.lsmdb.bean.OrderList;
import it.unipi.lsmdb.persistence.LevelDbDriver;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @FXML Button submit;

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
        ArrayList<Beer> beer_cart = new ArrayList<>();

        keyList = levelDbDriver.findKeysByPrefix(username);

        for (String key: keyList){
            int beer_id = levelDbDriver.splitKeys(key);
            Beer beer = MongoDriver.getBeerById(beer_id);
            beer_cart.add(beer);
        }

        for (Beer item : beer_cart) {
            double space = 5;
            VBox beer = new VBox(space);
            beer.setMaxWidth(491);
            beer.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            Label title = new Label();
            title.setText("Beer ID:  " + item.get_id() + "     " + "Beer Name:  " + item.getName());
            title.setFont(font);

            Label details = new Label();
            details.setText("Brewery Name:  " + item.getBrewery_name() + "     " + "Beer Style:  " + item.getStyle());
            details.setFont(font);

            beer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setIdBeerToShow(item.get_id());
                Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", event);
                event.consume();
            });

            Button button = new Button();
            button.setText("REMOVE BEER");
            button.setOnAction(e -> {
                if(deleteItemFromCart(username, item.get_id(), e))
                    Utils.showInfoAlert("Beer removed successfully");
                else Utils.showErrorAlert("Beer not removed");
            }); //remove from wishlist

            Label quantity = new Label();
            quantity.setText("Quantity: ");

            TextField q = new TextField();

            beer.getChildren().addAll(quantity,q,button, title, details);
            cartInfoPane.getChildren().add(beer);
        }
    }

    private boolean deleteItemFromCart(String username, int beer_id, ActionEvent actionEvent){

        try {
            LevelDbDriver levelDbDriver = new LevelDbDriver();

            String key = username + ":" + beer_id + ":" + "quantity";
            levelDbDriver.deleteValue(key);
            Utils.changeScene("cart-page.fxml", actionEvent);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private boolean confirmOrder(ActionEvent actionEvent){

        String username = DataSession.getUserLogged();

        LevelDbDriver levelDbDriver = new LevelDbDriver();
        List<String> keys = levelDbDriver.findKeysByPrefix(username);

        Order order = new Order();
        order.setIdOrder(MongoDriver.getMaxIdOrder(username) + 1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        String confDate = (ZonedDateTime.now().format(dtf));

        order.setConfirmationDate(confDate);
        order.setFeedback(3);

        try {
            for (String key: keys){

                int beer_id = levelDbDriver.splitKeys(key);
                key = username + ":" + beer_id + ":" + "quantity";
                Beer beer = MongoDriver.getBeerById(beer_id);

                int quantity = Integer.parseInt(levelDbDriver.getString(key));
                String beer_name = beer.getName();
                int price = beer.getPrice();

                order.setTotalCost(price*quantity);

                OrderList orderList = new OrderList(beer_id, beer_name, price, quantity);

                order.setOrderList(orderList);

            }

            MongoDriver.addOrder(username, order);

            Utils.showInfoAlert("Order confirmed");
            Utils.changeScene("homepage.fxml", actionEvent);

            return true;

        } catch (Exception e){
            Utils.showErrorAlert("Unable to confirm order");
            e.printStackTrace();
            return false;
        }
    }
}
