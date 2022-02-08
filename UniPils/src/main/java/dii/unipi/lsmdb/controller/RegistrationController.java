package dii.unipi.lsmdb.controller;

import dii.unipi.lsmdb.utils.Utils;
import dii.unipi.lsmdb.persistence.MongoDriver;
import dii.unipi.lsmdb.bean.User;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
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
        String m = male.getText();
        String f = female.getText();
        String cell = cellular.getText();
        String gen;

        if (first.equals("") || last.equals("") || em.equals("") || uName.equals("") || pwd.equals("")||((m.equals(""))&&(f.equals("")))||(cell.equals(""))) {
            Utils.showErrorAlert("Fill in all fields");
            return;
        }

        if(f.equals(""))
            gen = m;
        else
            gen = f;

        //email check
        Pattern p=Pattern.compile(".+@.+\\.[a-z]+");
        Matcher match = p.matcher(em);

        if(!match.matches()) {
            //show the error message
            Utils.showErrorAlert("incorrect email");
            return;
        }

        //int newId = MongoDriver.getMaxUserId() + 1;
        //User user = new User(uName, first, last, em, gen, date, cell);
        //MongoDriver.setMaxUserId(user.getUserId());
        //MongoDriver.addUser(user);
        //Utils.showInfoAlert("User succesfully added");
        //SessionUtils.setUserLogged(user);

        Utils.changeScene("hello-view.fxml", actionEvent);
    }

    @FXML
    public void goBack(ActionEvent actionEvent) {
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("hello-view.fxml", ae);
    }


}
