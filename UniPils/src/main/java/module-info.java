module dii.unipi.lsmdb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    opens dii.unipi.lsmdb to javafx.fxml;
    exports dii.unipi.lsmdb;
    exports dii.unipi.lsmdb.controller;
    opens dii.unipi.lsmdb.controller to javafx.fxml;

}