package gui;
import db_obj.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
    Performs banking functions such as depositing, withdrawing, seeing past transaction, and transferring
 */


public class BankingAppGui extends BaseFrame implements ActionListener {

    private JTextField currentBalanceField;
    public JTextField getCurrentBalanceField(){
        return currentBalanceField;
    }

    public BankingAppGui(User user) {
        super("Banking Application", user);
    }

    @Override
    protected void addGuiComponent() {
        //create welcome message
        String welcomeMessage = "<html><body style = 'text-align:center'>" +
                 "<b>Hello " + user.getUsername() + "</b><br>" +
                 "What would you like to do today?</body></html>";
        JLabel welcomeMessageLabel = new JLabel(welcomeMessage);
        welcomeMessageLabel.setBounds(0, 20, getWidth() - 10, 40);
        welcomeMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeMessageLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(welcomeMessageLabel);

        //create current balance label
        JLabel currentBalanceLabel = new JLabel("Current Balance:");
        currentBalanceLabel.setBounds(0, 80, getWidth() - 10, 30);
        currentBalanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentBalanceLabel.setFont(new Font("Dialog", Font.BOLD, 22));
        add(currentBalanceLabel);

        //create current balance field
        currentBalanceField = new JTextField("$" + user.getCurrentBalance());
        currentBalanceField.setBounds(15, 120, getWidth() - 50, 40);
        currentBalanceField.setHorizontalAlignment(SwingConstants.RIGHT);
        currentBalanceField.setFont(new Font("Dialog", Font.BOLD, 28));
        currentBalanceField.setEditable(false);
        add(currentBalanceField);

        //deposit button
        JButton depositButton = new JButton("Deposit");
        depositButton.setBounds(15, 180, getWidth() - 50, 50);
        depositButton.setFont(new Font("Dialog", Font.BOLD, 22));
        depositButton.addActionListener(this);
        add(depositButton);

        //withdraw button
        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(15, 250, getWidth() - 50, 50);
        withdrawButton.setFont(new Font("Dialog", Font.BOLD, 22));
        withdrawButton.addActionListener(this);
        add(withdrawButton);

        //past transactions button
        JButton pastTransactionsButton = new JButton("Past Transactions");
        pastTransactionsButton.setBounds(15, 320, getWidth() - 50, 50);
        pastTransactionsButton.setFont(new Font("Dialog", Font.BOLD, 22));
        pastTransactionsButton.addActionListener(this);
        add(pastTransactionsButton);

        //transfer button
        JButton transferButton = new JButton("Transaction");
        transferButton.setBounds(15, 390, getWidth() - 50, 50);
        transferButton.setFont(new Font("Dialog", Font.BOLD, 22));
        transferButton.addActionListener(this);
        add(transferButton);

        //logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(15, 500, getWidth() - 50, 50);
        logoutButton.setFont(new Font("Dialog", Font.BOLD, 22));
        logoutButton.addActionListener(this);
        add(logoutButton);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonPressed = e.getActionCommand();

        if (buttonPressed.equals("Logout")) {
            //return user to the login gui
            new LoginGui().setVisible(true);
            this.dispose();
            return;
        }

        //other functions
        BankingAppDialog bankingAppDialog = new BankingAppDialog(this, user);

        //set the title of the dialog header to the action
        bankingAppDialog.setTitle(buttonPressed);

        //if the button pressed is deposit, withdraw or transfer
        if(buttonPressed.equalsIgnoreCase("Deposit") || buttonPressed.equalsIgnoreCase("Withdraw") || buttonPressed.equalsIgnoreCase("Transaction")){
            //add in the current balance and amount gui component to the dialog
            bankingAppDialog.addCurrentBalanceAndAmount();

            //add action button
            bankingAppDialog.addActionButton(buttonPressed);

            //for the transfer action it will require more components
            if(buttonPressed.equalsIgnoreCase("Transaction")){
                bankingAppDialog.addUserField();
            }

            bankingAppDialog.setVisible(true);
        }else if(buttonPressed.equalsIgnoreCase("Past Transactions")){
            bankingAppDialog.addPastTransactionComponents();
        }

        //make the app dialog visible
        bankingAppDialog.setVisible(true);



    }
}
