module com.example.skrtdesk {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    
    opens com.example.skrtdesk to javafx.fxml;
    opens com.example.skrtdesk.controller to javafx.fxml;
    opens com.example.skrtdesk.model to com.google.gson;
    
    exports com.example.skrtdesk;
    exports com.example.skrtdesk.controller;
    exports com.example.skrtdesk.model;
    exports com.example.skrtdesk.service;
    exports com.example.skrtdesk.view;
    exports com.example.skrtdesk.util;
}