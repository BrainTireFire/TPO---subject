module com.example.tpo_5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires activemq.all;


    opens com.example.tpo_5 to javafx.fxml;
    exports com.example.tpo_5;
}