module it.unipi.lsmdb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    opens it.unipi.lsmdb to javafx.fxml;
    exports it.unipi.lsmdb;
    exports it.unipi.lsmdb.controller;
    opens it.unipi.lsmdb.controller to javafx.fxml;

}