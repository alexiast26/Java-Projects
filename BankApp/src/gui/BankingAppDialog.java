package gui;
import db_obj.MyJDBC;
import db_obj.Transaction;
import db_obj.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;

/*
    Display a custom dialog for out BankingAppGui
 */

public class BankingAppDialog extends JDialog implements ActionListener {
    private User user;
    private BankingAppGui bankingAppGui;
    private JLabel balanceLabel, enterAmountLabel, enterUserLabel;
    private JTextField enterAmountTextField, enterUserField;
    private JButton actionButton;
    private JPanel pastTransactionsPanel;
    private ArrayList<Transaction> pastTransactions;

    public BankingAppDialog(BankingAppGui bankingAppGui, User user) {
        //set the size
        setSize(400, 400);

        //add focus to the dialog (can't interact with anything else until dialog is closed
        setModal(true);

        setLocationRelativeTo(bankingAppGui);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        //we will need to reference out gui so that we can update the current balance
        this.bankingAppGui = bankingAppGui;

        //we will need access to the user info to make updates to our db or retrieve data about the user
        this.user = user;
    }

    public void addCurrentBalanceAndAmount(){
        //balance label
        balanceLabel = new JLabel("Balance: $" + user.getCurrentBalance());
        balanceLabel.setBounds(0, 10, getWidth() - 20, 20);
        balanceLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(balanceLabel);

        //enter amount label
        enterAmountLabel = new JLabel("Enter Amount:");
        enterAmountLabel.setBounds(0, 50, getWidth() - 20, 20);
        enterAmountLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        enterAmountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(enterAmountLabel);

        //enter amount field
        enterAmountTextField = new JTextField();
        enterAmountTextField.setBounds(15, 80, getWidth() - 50, 40);
        enterAmountTextField.setFont(new Font("Dialog", Font.BOLD, 20));
        enterAmountTextField.setHorizontalAlignment(SwingConstants.CENTER);
        add(enterAmountTextField);
    }

    public void addActionButton(String actionButtonType){
        actionButton = new JButton(actionButtonType);
        actionButton.setBounds(15, 300, getWidth() - 20, 40);
        actionButton.setFont(new Font("Dialog", Font.BOLD, 20));
        actionButton.addActionListener(this);
        actionButton.setHorizontalAlignment(SwingConstants.CENTER);
        add(actionButton);
    }

    public void addUserField(){
        //enter user label
        enterUserLabel = new JLabel("Enter User:");
        enterUserLabel.setBounds(0, 160, getWidth() - 20, 20);
        enterUserLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        enterUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(enterUserLabel);

        //enter user field
        enterUserField = new JTextField();
        enterUserField.setBounds(15, 190, getWidth() - 50, 40);
        enterUserField.setFont(new Font("Dialog", Font.BOLD, 20));
        enterUserField.setHorizontalAlignment(SwingConstants.CENTER);
        add(enterUserField);

    }

    public void addPastTransactionComponents(){
        //container where we store each transaction
        pastTransactionsPanel = new JPanel();

        // make layout 1x1
        pastTransactionsPanel.setLayout(new BoxLayout(pastTransactionsPanel, BoxLayout.Y_AXIS));

        //add scrollavility to the container
        JScrollPane scrollPane = new JScrollPane(pastTransactionsPanel);

        //display the vertical scroll only when it's required
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(0, 20, getWidth() - 15, getHeight() - 80);

        //perform db call to retrieve all the past transaction and store into array list
        pastTransactions = MyJDBC.getPastTransactions(user);

        //iterate through the list and add to the gui
        for(int i = 0; i < pastTransactions.size();i++){
            //store current transaction
            System.out.println("1");

            Transaction transaction = pastTransactions.get(i);

            //create a container to store an individual transaction
            JPanel pastTransactionContainer = new JPanel();
            pastTransactionContainer.setLayout(new BorderLayout());

            //create transaction type label
            JLabel transactionTypeLabel = new JLabel(transaction.getTransactionType());
            transactionTypeLabel.setFont(new Font("Dialog", Font.BOLD, 20));

            //create transaction amount label
            JLabel transactionAmountLabel = new JLabel(String.valueOf(transaction.getTransactionAmount()));
            transactionAmountLabel.setFont(new Font("Dialog", Font.BOLD, 20));

            //create transaction date label
            JLabel transactionDateLabel = new JLabel(String.valueOf(transaction.getTransactionDate()));
            transactionDateLabel.setFont(new Font("Dialog", Font.BOLD, 20));

            //add to the container
            pastTransactionContainer.add(transactionTypeLabel, BorderLayout.WEST);
            pastTransactionContainer.add(transactionAmountLabel, BorderLayout.EAST);
            pastTransactionContainer.add(transactionDateLabel, BorderLayout.SOUTH);

            //give a white background to each container
            pastTransactionContainer.setBackground(Color.WHITE);

            //give a black border to each transaction container
            pastTransactionContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            //add transaction component to the transaction panel
            pastTransactionsPanel.add(pastTransactionContainer);
        }
        add(scrollPane);
    }

    private void handleTransaction(String transactionType, float amountVal){
        Transaction transaction;

        if (transactionType.equalsIgnoreCase("Deposit")){
            //add crt balance
            user.setCurrentBalance(user.getCurrentBalance().add(new BigDecimal(amountVal)));

            //create transaction
            //we leave date null cuz we are going to be using the NOW() in sql which will get the current date
            transaction = new Transaction(user.getId(), transactionType, new BigDecimal(amountVal), null);
        }else{
            //withdraw transaction type
            user.setCurrentBalance(user.getCurrentBalance().subtract(new BigDecimal(amountVal)));

            //we want to show a negative sign for the amount val when withdrawing
            transaction = new Transaction(user.getId(), transactionType, new BigDecimal(-amountVal), null);
        }

        //update database
        if(MyJDBC.addTransactionToDataBase(transaction) && MyJDBC.updateCurrentBalance(user)){
            //show success dialog
            JOptionPane.showMessageDialog(null, "Transaction added successfully");

            //reset the fields
            resetFieldsAndUpdateCurrentBalance();
        }else{
            //show failure dialog
            JOptionPane.showMessageDialog(null, "Transaction could not be added");
        }

    }

    public void handleTransfer(User user, String tranferredUser, float amountVal){
        if(MyJDBC.transfer(user, tranferredUser, amountVal)){
            JOptionPane.showMessageDialog(null, "Transaction added successfully");
            resetFieldsAndUpdateCurrentBalance();
        }else {
            JOptionPane.showMessageDialog(null, "Transaction could not be added");
        }
    }

    private void resetFieldsAndUpdateCurrentBalance(){
        //reset fields
        enterAmountTextField.setText("");

        //only appears when transfer is clicked
        if(enterUserField != null) {
            enterUserField.setText("");
        }

        //update current balance dialog
        balanceLabel.setText("Balance: $" + user.getCurrentBalance());

        //update current balance on main gui
        bankingAppGui.getCurrentBalanceField().setText("$" + user.getCurrentBalance());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonPressed = e.getActionCommand();

        //get amount val
        float amount = Float.parseFloat(enterAmountTextField.getText());

        //pressed deposit
        if(buttonPressed.equalsIgnoreCase("Deposit")){
            //we want to handle the deposit transaction
            handleTransaction(buttonPressed, amount);
        }else{
            //pressed withdraw or transfer


            //validate input by making sure that withdraw transfer amount is less than current balance
            //if result is -1 it means that the entered amount is more, 0 means they're equal and 1 means that is less
            int result = user.getCurrentBalance().compareTo(BigDecimal.valueOf(amount));
            if(result < 0){
                //display error dialog
                JOptionPane.showMessageDialog(null, "Error: Input value is more then current balance");
                return;
            }

            //check to see if withdraw or transfer was pressed
            if(buttonPressed.equalsIgnoreCase("Withdraw")){
                //we want to handle withdraw transactions
                handleTransaction(buttonPressed, amount);
            }else{
                //we want to handle transfer transactions
                String transferredUser = enterUserField.getText();

                //handle transfer
                handleTransfer(user, transferredUser, amount);
            }

        }
    }
}
