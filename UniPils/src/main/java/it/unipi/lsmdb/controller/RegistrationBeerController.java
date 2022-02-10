package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationBeerController {

    @FXML private TextField name;
    @FXML private TextField state;
    @FXML private TextField country;
    @FXML private TextField style;
    @FXML private TextField availability;
    @FXML private TextField abv;
    @FXML private TextField volume;
    @FXML private TextField price;
  //  @FXML private TextField brewery_id;
    @FXML private TextField brewery_name;
    @FXML private TextField brewery_city;
    @FXML private TextField brewery_types;

    @FXML private void registerBeer(ActionEvent actionEvent){
        String uName = name.getText();
        String st=state.getText();
        String co=country.getText();
        String sty=style.getText();
        String av=availability.getText();
        String ab=abv.getText();
        String vol=volume.getText();
        String pr=price.getText();
        String brew_name=brewery_name.getText();
        String brew_city=brewery_city.getText();
        String brew_types=brewery_types.getText();

        if (uName.equals("") || st.equals("") || co.equals("") || sty.equals("") || av.equals("")||(ab.equals(""))||(vol.equals(""))
                ||(pr.equals(""))||(brew_name.equals(""))||(brew_city.equals(""))||(brew_types.equals(""))) {
            Utils.showErrorAlert("Fill in all fields");
            return;
        }
        int availa= Integer.parseInt(av);
        int a=Integer.parseInt(ab);
        int volu=Integer.parseInt(vol);
        int pri=Integer.parseInt(pr);

        //NeoDriver neo4j = NeoDriver.getInstance();

        int newId = MongoDriver.getMaxBeerId() + 1;
        int brewId = MongoDriver.getBreweryId(brew_name);

        Beer beer = new Beer(newId, uName, st, co, sty, availa, a, volu,pri,brewId,brew_name,brew_city,brew_types,0);
        Utils.addBeer(beer);
        Utils.changeScene("homepage.fxml", actionEvent);
    }


}
