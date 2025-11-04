package db;

import constants.CommonConstants;

import java.sql.*;

//JDBC = Java DataBase Connectivity
//with this we will be accessing our MySQL database
public class MyJDBC {
    //register new user to the database
    //true - register success
    //false - register failure

    public static boolean register(String username, String password) {
        try {
            //first check if username already exists in the database
            if (!checkUser(username)) {
                Connection connection = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.USERNAME, CommonConstants.PASSWORD);

                //create insert query
                PreparedStatement insertUser = connection.prepareStatement("INSERT INTO " + CommonConstants.DB_USERS_TABLE_NAME + "(username, password)" + " VALUES (?, ?)");
                insertUser.setString(1, username);
                insertUser.setString(2, password);

                //update db with new user
                insertUser.executeUpdate();
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    //check is username already exists in the database
    //false - user doesn't exists
    //true - user exists in the database
    public static boolean checkUser(String username) {
        try{
            Connection connection = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.USERNAME, CommonConstants.PASSWORD);
            PreparedStatement checkUserExists = connection.prepareStatement("Select * from " + CommonConstants.DB_USERS_TABLE_NAME + " where USERNAME = ?");
            checkUserExists.setString(1, username);
            ResultSet resultSet = checkUserExists.executeQuery();

            //check to see if the result set is empty
            //if it is empty it means that there was no data row that contains the username
            if(!resultSet.isBeforeFirst()) {
                return false;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    //validate login credentials by checking to see if username/password pair exists in the database
    public static boolean validateLogin(String username, String password) {
        try{
            Connection connection = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.USERNAME, CommonConstants.PASSWORD);

            //create select query
            PreparedStatement validateUser = connection.prepareStatement("SELECT * FROM " + CommonConstants.DB_USERS_TABLE_NAME + " WHERE USERNAME = ? AND PASSWORD = ?");
            validateUser.setString(1, username);
            validateUser.setString(2, password);

            ResultSet resultSet = validateUser.executeQuery();

            //check to see if the query returns any rows that match our query
            if(!resultSet.isBeforeFirst()) {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

}
