/**
 *
 *  @author Kawęcki Maciej S20202
 *
 */

package zad2;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Service {
    private String countryName;
    private final String openWeatherKey = "254de91d9c78e4a0fb8e86996f7a1779";
    private String openWeatherAPI = "https://api.openweathermap.org/data/2.5/weather?q=";
    private String exchangeRatesAPI = "https://api.exchangerate.host/convert?";
    private String nbpRateTable = "http://api.nbp.pl/api/exchangerates/tables/";

    public Service(String countryName) {
        this.countryName = countryName;
    }

    public String getWeather(String cityName){
        String countryCode = getCountryCode(this.countryName);

        if (countryCode == null) {
            return null;
        }

        openWeatherAPI += cityName +"," +countryCode+ "&appid="+ openWeatherKey;

        String result = makeHTTPRequest(this.openWeatherAPI);

        return result;
    }

    public String getWeatherDetails(String cityName){
        String jsonWeather = getWeather(cityName);
        JSONObject content = new JSONObject(jsonWeather);
        JSONArray weatherDescription = content.getJSONArray("weather");
        JSONObject descriptionObject = weatherDescription.getJSONObject(0);
        String description = descriptionObject.getString("description");

        JSONObject windSpeed = content.getJSONObject("wind");
        Double speed = windSpeed.getDouble("speed");

        JSONObject temperature = content.getJSONObject("main");
        Double tempCelsus = temperature.getDouble("temp") - 273.15;


        String info = "Wind speed: " + speed.toString() + "\n"
                      + "Weather description: " + description + "\n"
                        + "Temperature celsius: " + tempCelsus.toString();
        return info;
    }

    public Double getRateFor(String currencyCode){
        String currencyCodeInCountry = getCurrencyCode(this.countryName);
        exchangeRatesAPI += "from=" + currencyCode + "&to=" +currencyCodeInCountry;

        String response = makeHTTPRequest(exchangeRatesAPI);

        JSONObject content = new JSONObject(response);
        JSONObject info = content.getJSONObject("info");
        Double currencyRate = info.getDouble("rate");

        if (currencyRate == null) {
            return null;
        }

        return currencyRate;
    }

    public Double getNBPRate(){
        Double tableA = this.helperToNBP("A");
        if (tableA != null)
        {
            return tableA;
        }

        Double tableB = this.helperToNBP("B");
        if (tableB != null)
        {
            return tableB;
        }

        return this.helperToNBP("C");
    }

    private Double helperToNBP(String tableName){
        String currencyCodeInCountry = getCurrencyCode(this.countryName);
        if (currencyCodeInCountry == null) {
            return null;
        }else if (currencyCodeInCountry.equals("PLN")){
            return 1.0;
        }

        nbpRateTable += tableName + "/";

        String response = makeHTTPRequest(nbpRateTable);

        if (response == null){
            return null;
        }

        JSONArray content = new JSONArray(response);
        JSONObject tableObject = content.getJSONObject(0);
        JSONArray rates = tableObject.getJSONArray("rates");

        List<String> accumulatedValues =  IntStream.range(0, rates.length())
                .filter(index -> ((JSONObject)rates.get(index)).optString("code").equals(currencyCodeInCountry))
                .mapToObj(index -> ((JSONObject)rates.get(index)).optString("mid"))
                .collect(Collectors.toList());

        Double numberReturn = Double.parseDouble(accumulatedValues.get(0));

        return numberReturn;
    }

    private String getCountryCode(String country){
        String[] isoCountries = Locale.getISOCountries();

        for (String isoCode : isoCountries) {
            Locale locale = new Locale("",isoCode);

            if (locale.getDisplayCountry().equals(country)){
                return isoCode;
            }
        }


        return null;
    }

    private String getCurrencyCode(String country){
        Optional<Locale> optional = Arrays.stream(Locale.getAvailableLocales()).filter(local ->
                local.getDisplayCountry().equals(country)
        ).findFirst();

        if(optional.isPresent()) {
            Currency currency = Currency.getInstance(optional.get());
            return currency.getCurrencyCode();
        }
        return null;
    }

    private String makeHTTPRequest(String urlPage) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL url = new URL(urlPage);
            URLConnection URLConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(URLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            return null;
        }

        return stringBuffer.toString();
    }
}
