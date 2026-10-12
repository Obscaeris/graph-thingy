module com.obscaeris {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.base;

    opens com.obscaeris to javafx.fxml;
    exports com.obscaeris;
}
