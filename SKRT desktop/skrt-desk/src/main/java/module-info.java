module com.example.skrtdesk {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.skrtdesk to javafx.fxml;
    exports com.example.skrtdesk;
}