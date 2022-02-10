package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationController {

    @FXML private TextField username;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private TextField fName;
    @FXML private TextField lName;
    @FXML private CheckBox male;
    @FXML private CheckBox female;
    @FXML private TextField cellular;
    @FXML private DatePicker dob;// = new DatePicker(LocalDate.now());

    @FXML
    private void register(ActionEvent actionEvent) {
        String uName = username.getText();
        String pwd = password.getText();
        String em = email.getText();
        String first = fName.getText();
        String last = lName.getText();
        LocalDate date = dob.getValue();
        boolean m = male.isSelected();
        boolean f = female.isSelected();
        String cell = cellular.getText();
        String gen;

        if (first.equals("") || last.equals("") || em.equals("") || uName.equals("") || pwd.equals("")||((!m)&&(!f))||(cell.equals(""))||(date == null)) {
            Utils.showErrorAlert("Fill in all fields");
            return;
        }

        if(m)
            gen = "male";
        else
            gen = "female";

        //email check
        Pattern p=Pattern.compile(".+@.+\\.[a-z]+");
        Matcher match = p.matcher(em);

        if(!match.matches()) {
            //show the error message
            Utils.showErrorAlert("incorrect email");
            return;
        }

        NeoDriver neo4j = NeoDriver.getInstance();
        /*if(!neo4j.getUsersFromUnique(uName)){
            //show the error message
            Utils.showErrorAlert("username or email already exist");
            return;
        }*/

        //int newId = MongoDriver.getMaxUserId() + 1;
        User user = new User(gen, first, last, em, uName, pwd, date, cell);

        if(!neo4j.addUser(user) || !MongoDriver.addUser(user)) {
            Utils.showErrorAlert("User not inserted");
            return;
        }

        Utils.showInfoAlert("User inserted successfully");
        DataSession.setUserLogged(user.getUsername());
        Utils.changeScene("menu-page.fxml", actionEvent);
    }

    @FXML
    public void goBack(ActionEvent actionEvent) {
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("menu-page.fxml", ae);
    }


}
