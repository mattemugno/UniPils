package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FollowerController implements Initializable {

    NeoDriver neo4j = NeoDriver.getInstance();

    @FXML private VBox followerInfoPane;
    @FXML private Label totalFollower;
    @FXML private VBox suggestedFriends;
    private int skip=0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        printFollowerUser();
        printSuggestedFriends();
       // totalFollower.setText(String.valueOf(count));
    }

    @FXML public void printFollowerUser(){
        int count=0;
        String usernameLogged;
        if(DataSession.getUserView()==null)
            usernameLogged=DataSession.getUserLogged();
        else
            usernameLogged=DataSession.getUserView();

        ArrayList<String> followers = neo4j.getFollowers(usernameLogged,skip,10);
        Font font = Font.font("Comic Sans", FontWeight.BOLD, 18);
        if(!followers.isEmpty()){
            for (String follow : followers) {
                HBox hb = new HBox();

                Button userFollow = new Button();
                userFollow.setText(follow);
                userFollow.setFont(font);
                userFollow.setOnAction(actionEvent -> {
                    DataSession.setUserView(follow);
                    Utils.changeScene("profile-user.fxml", actionEvent);
                });

                Button btnDelete = new Button();
                btnDelete.setText("delete follow");
                btnDelete.setPadding(new Insets(5, 5, 5, 5));
                btnDelete.setFont(font);
                btnDelete.setVisible(false);

                if (DataSession.getUserView() == null) {
                    btnDelete.setVisible(true);
                    btnDelete.setOnAction(actionEvent -> {
                        if (neo4j.deleteFollows(usernameLogged, follow))
                            Utils.changeScene("follower-page.fxml", actionEvent);
                    });
                }

                hb.getChildren().addAll(userFollow, btnDelete);
                followerInfoPane.getChildren().add(hb);
                count++;
            }

            skip=skip+10;
            createShowMore();
        }

    }


    private void showMore(){
        followerInfoPane.getChildren().remove(followerInfoPane.getChildren().size() - 1);
        printFollowerUser();
    }

    private void createShowMore(){
        Button showMore = new Button("Show more");
        showMore.setOnAction(actionEvent -> showMore());
        followerInfoPane.getChildren().add(showMore);
    }

    @FXML public void printSuggestedFriends(){
        ArrayList<String> suggFriends=neo4j.SuggestedUsers(DataSession.getUserLogged());
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        Label labTitle=new Label("Suggested follower: ");
        labTitle.setFont(font);
        suggestedFriends.getChildren().add(labTitle);
        if(!(suggFriends==null)){
            for(String sugg:suggFriends) {
                Button btn = new Button();
                btn.setText(sugg);
                btn.setOnAction(actionEvent -> {
                    DataSession.setUserView(sugg);
                    Utils.changeScene("profile-user.fxml", actionEvent);
                });
                suggestedFriends.getChildren().add(btn);
            }
        }else{
            Label label=new Label("No suggested follower");
            suggestedFriends.getChildren().add(label);
        }

    }
}
