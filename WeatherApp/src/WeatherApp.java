import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//retrieve weather data from API - this backend logic will fetch the latest weather
//data from the external API and return it. The GUI will display this data to the user
public class WeatherApp {
    //fetch weather data for given location
    public static JSONObject getWeatherData(String locationName){
        //get location coordinates using the geolocation API
        JSONArray locationData = getLocationData(locationName);

        //extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (Double) location.get("latitude");
        double longitude = (Double) location.get("longitude");

        //build API request URL with the location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";

        try {
            //call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check for response status
            //200 - means that it was successful
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to the API");
                return null;
            }else{
                //store resulting json data
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());
                while(scanner.hasNext()){
                    //read and store in the string builder
                    resultJson.append(scanner.nextLine());
                }

                //close scanner and url connection
                scanner.close();
                conn.disconnect();

                //parse through our data
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

                //return hourly data
                JSONObject hourly = (JSONObject) jsonObject.get("hourly");

                //we want to get the current hour's data
                //so we need to get the index of our current hour
                JSONArray time = (JSONArray) hourly.get("time");
                int index = findIndexOfCurrentTime(time);

                //get temperature
                JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                double temperature = (double) temperatureData.get(index);

                //get weather code
                JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
                String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

                //get humidity
                JSONArray relativeHumidityData = (JSONArray) hourly.get("relative_humidity_2m");
                long humidity = (long) relativeHumidityData.get(index);

                //get windspeed
                JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
                double windspeed = (double) windspeedData.get(index);

                //build the weather json data object that we are going to access in our front end
                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather_condition", weatherCondition);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed", windspeed);

                return weatherData;
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }

    //retrieves geographic coordinates for given location name
    public static JSONArray getLocationData(String locationName){
        //replace any whitespace in the location name to + to adhere to API's request format
        locationName = locationName.replaceAll(" ", "+");

        //build API url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json&latitude=33.767&longitude=-118.1892";
        try{
            //call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check response status
            //200 means successful connection
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else {
                //store the API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and store the resulting json data into our string builder
                while(scanner.hasNextLine()){
                    resultJson.append(scanner.nextLine());
                }

                //close scanner
                scanner.close();

                //close url connection
                conn.disconnect();

                //parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get the list of location data the AOI generated from the location name
                JSONArray locationData = (JSONArray) jsonObject.get("results");
                return locationData;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try {
            //attempt to create a conection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            //connect to our API
            conn.connect();
            return conn;
        }catch (Exception e){
            e.printStackTrace();
        }

        //could not make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //iterate through the time list and see which one matches our current time
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime(){
        //get current data and time
        LocalDateTime currentDataTime = LocalDateTime.now();

        //format data to be 2025-08-29-02T00:00 (this is how it's read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format and print the current date and time
        String formattedDateTime = currentDataTime.format(formatter);

        return formattedDateTime;
    }

    //convert the weather code to something more readable
    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = "";
        if(weatherCode == 0L){
            weatherCondition = "Clear";
        }else if (weatherCode <= 3L && weatherCode > 0L){
            weatherCondition = "Cloudy";
        }else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)){
            weatherCondition = "Rain";
        }else if(weatherCode >= 71L && weatherCode <= 77L){
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}
