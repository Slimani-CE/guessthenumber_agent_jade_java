module com.sma.tp2_withjavafx_sma {
    requires javafx.controls;
    requires javafx.fxml;
    requires jade;
    opens ma.enset.tp2_sma.agent;
    exports ma.enset.tp2_sma.containers;
    exports ma.enset.tp2_sma.agent;
}