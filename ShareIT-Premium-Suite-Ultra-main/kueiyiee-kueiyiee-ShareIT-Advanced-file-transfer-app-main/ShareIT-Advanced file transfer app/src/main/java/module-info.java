module com.shareit.shareit {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    
    opens com.shareit.shareit to javafx.fxml;
    exports com.shareit.shareit;
}