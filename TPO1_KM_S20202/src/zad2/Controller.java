package zad2;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private WebView webView;
    @FXML
    private TextField countryField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField currencyField;
    @FXML
    private TextArea weatherTextArea;
    @FXML
    private TextArea currencyTextArea;
    @FXML
    private TextArea nbpTextArea;
    private WebEngine engine;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        engine = webView.getEngine();
        countryField.setText("Poland");
        cityField.setText("Warsaw");
        currencyField.setText("USD");
        handleNewData();
    }

    public void handleNewData(){
        String country = countryField.getText();
        String city = cityField.getText();
        String currency = currencyField.getText();

        Service service = new Service(country);
        String weatherJson = service.getWeatherDetails(city);
        Double rate1 = service.getRateFor(currency);
        Double rate2 = service.getNBPRate();

        String[] arr = {weatherJson,rate1.toString(), rate2.toString() };
        setTextAreas(arr);

        loadPage(city);
    }

    public void loadPage(String city){
        engine.load("https://en.wikipedia.org/wiki/" + city);
    }

    public void setTextAreas(String[] data){
        weatherTextArea.setEditable(false);
        currencyTextArea.setEditable(false);
        nbpTextArea.setEditable(false);
        weatherTextArea.setText(data[0]);
        currencyTextArea.setText(data[1]);
        nbpTextArea.setText(data[2]);
    }

}
