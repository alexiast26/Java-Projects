import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
   private JSONObject weatherData;

   public WeatherAppGUI() {
       super("Weather App");

       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setSize(450, 650);
       setLocationRelativeTo(null);
       setLayout(null);
       setResizable(false);

       addGUIComponents();
   }

   private void addGUIComponents() {
       //search field
       JTextField searchTextField = new JTextField();

       //set location and size of the component
       searchTextField.setBounds(15, 15, 351, 45);

       //style and font
       searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

       add(searchTextField);

       //weather image
       JLabel weatherConditionImage = new JLabel(new ImageIcon("src/assets/cloudy.png"));
       weatherConditionImage.setBounds(0, 125, 450, 217);
       add(weatherConditionImage);

       //temperature text
       JLabel temperatureText = new JLabel("10 C");
       temperatureText.setBounds(0, 350, 450, 54);
       temperatureText.setFont(new Font("Dialog", Font.PLAIN, 48));

       //center the text
       temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
       add(temperatureText);

       //weather condition description
       JLabel weatherConditionDesc = new JLabel("Cloudy");
       weatherConditionDesc.setBounds(0, 405, 450, 36);
       weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
       weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
       add(weatherConditionDesc);

       //humidity image
       JLabel humidityImage = new JLabel(new ImageIcon("src/assets/humidity.png"));
       humidityImage.setBounds(0, 500, 74, 66);
       add(humidityImage);

       //humidity text
       JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
       humidityText.setBounds(90, 500, 85, 55);
       humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
       humidityText.setHorizontalAlignment(SwingConstants.CENTER);
       add(humidityText);

       //windspeed image
       JLabel windspeedImage = new JLabel(new ImageIcon("src/assets/windspeed.png"));
       windspeedImage.setBounds(220, 500, 74, 66);
       add(windspeedImage);

       //windspeed text
       JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
       windspeedText.setBounds(310, 500, 85, 55);
       windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
       add(windspeedText);

       //search button
       JButton searchButton = new JButton(loadImage("src/assets/search.png"));

       //change the cursor to a hand cursor when hovering over this button
       searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
       searchButton.setBounds(375, 13, 47, 45);
       searchButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               //get location from user
               String userInput = searchTextField.getText();

               //validate input - remove whitespace to ensure non-empty text
               if(userInput.replaceAll("\\s", "").length() <= 0){
                   return;
               }

               //retrieve weather data
               weatherData = WeatherApp.getWeatherData(userInput);

               //update gui

               //update weather image
               String weatherCondition = (String) weatherData.get("weather_condition");

               //depending on the condition, we will update the weather image corresponds with the condition
               switch(weatherCondition){
                   case "Clear":
                       weatherConditionImage.setIcon(new ImageIcon("src/assets/clear.png"));
                       break;
                   case "Cloudy":
                       weatherConditionImage.setIcon(new ImageIcon("src/assets/cloudy.png"));
                       break;
                   case "Rain":
                       weatherConditionImage.setIcon(new ImageIcon("src/assets/rain.png"));
                       break;
                   case "Snow":
                       weatherConditionImage.setIcon(new ImageIcon("src/assets/snow.png"));
                       break;
               }

               //update temperature text
               double temperature = (double) weatherData.get("temperature");
               temperatureText.setText(temperature + " C");

               //update weather condition text
               weatherConditionDesc.setText(weatherCondition);

               //update humidity text
               long humidity = (long) weatherData.get("humidity");
               humidityText.setText("<html><b>Humidity</b> " + humidity + " %</html>");

               //update windspeed text
               double windspeed = (double) weatherData.get("windspeed");
               windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");

           }
       });
       add(searchButton);
   }

   private ImageIcon loadImage(String resourcePath) {
       try{
           //read the image file from the pth given
           BufferedImage img = ImageIO.read(new File(resourcePath));
           return new ImageIcon(img);
       }catch(IOException e){
           e.printStackTrace();
       }
       System.out.println("Couldn't find resource: " + resourcePath);
       return null;
   }
}