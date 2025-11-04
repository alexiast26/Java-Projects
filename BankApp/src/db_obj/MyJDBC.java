package db_obj;

/*
    JDBC class is used to interact with our MySql DataBase to perform activities sus as retrieving and updating our db
 */

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class MyJDBC {
    //database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    //if valid return an object with the users information
    public static User validateLogin(String username, String password) {
        try{
            //establish a connection to the database using configurations
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            //create awl query
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            //next() returns true or false
            //true - query returned data and result set now points to the first row
            //false - query returned no data and result set is null
            if(resultSet.next()) {
                //success
                int userId = resultSet.getInt("id");

                //get current balance
                BigDecimal balance = resultSet.getBigDecimal("current_balance");

                //return user obj
                return new User(userId, username, password, balance);

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        //not valid
        return null;
    }

    //register new user to the database
    //true - register success
    //false - register failed
    public static boolean register(String username, String password) {
        try{
            //first we check if the username exists
            if(!checkUser(username)){
                Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (username, password, current_balance) VALUES (?,?,?)");
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setBigDecimal(3, BigDecimal.ZERO);
                preparedStatement.executeUpdate();
                return true;

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private static boolean checkUser(String username){
        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username=?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            //if it returns no data then it's available
            if(!resultSet.next()) {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    //true - update to db was a success
    //false - update to the db was a fail
    public static boolean addTransactionToDataBase(Transaction transaction) {
        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement insertTransaction = connection.prepareStatement("INSERT INTO transactions (user_id, transaction_type, transactions_amount, transaction_date) VALUES (?,?,?,NOW())");

            insertTransaction.setInt(1, transaction.getUserId());
            insertTransaction.setString(2, transaction.getTransactionType());
            insertTransaction.setBigDecimal(3, transaction.getTransactionAmount());

            insertTransaction.executeUpdate();

            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    //true - update successful
    //false - update failed
    public static boolean updateCurrentBalance(User user) {
        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement updateBalance = connection.prepareStatement("UPDATE users SET current_balance=? WHERE id=?");

            updateBalance.setBigDecimal(1, user.getCurrentBalance());
            updateBalance.setInt(2, user.getId());

            updateBalance.executeUpdate();

            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    //true - success
    //false - fail
    public static boolean transfer(User user, String transferredUsername, float amountVal) {
        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username=?");
            preparedStatement.setString(1, transferredUsername);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                //perform transfer
                User transferrUser = new User(
                        resultSet.getInt("id"),
                        transferredUsername,
                        resultSet.getString("password"),
                        resultSet.getBigDecimal("current_balance")
                );

                //create transaction
                Transaction transferTransaction = new Transaction(
                        user.getId(),
                        "Transfer",
                        new BigDecimal(-amountVal),
                        null
                );

                //this transaction will belong to the transferred user
                Transaction recievedTransaction = new Transaction(
                        transferrUser.getId(),
                        "Transaction",
                        new BigDecimal(amountVal),
                        null
                );

                //update transfer user
                transferrUser.setCurrentBalance(transferrUser.getCurrentBalance().add(BigDecimal.valueOf(amountVal)));
                updateCurrentBalance(transferrUser);

                //update users current balance
                user.setCurrentBalance(user.getCurrentBalance().subtract(BigDecimal.valueOf(amountVal)));
                updateCurrentBalance(user);

                //add transactions to the database
                addTransactionToDataBase(transferTransaction);
                addTransactionToDataBase(recievedTransaction);

                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<Transaction> getPastTransactions(User user) {
        ArrayList<Transaction> pastTransactions = new ArrayList<>();
        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transactions WHERE user_id=?");

            preparedStatement.setInt(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            //iterate through the results (if any)
            while(resultSet.next()) {
                //create transaction object
                Transaction transaction = new Transaction(
                        user.getId(),
                        resultSet.getString("transaction_type"),
                        resultSet.getBigDecimal("transactions_amount"),
                        resultSet.getDate("transaction_date")
                );

                pastTransactions.add(transaction);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return pastTransactions;
    }
}
