package gui;

import db_obj.MyJDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterGui extends BaseFrame{
    public RegisterGui() {
        super("Banking App Register");
    }

    @Override
    protected void addGuiComponent() {
        //banking app label
        JLabel bankingAppLabel = new JLabel("Banking Application");
        bankingAppLabel.setBounds(0, 20, super.getWidth(), 40);
        bankingAppLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        bankingAppLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(bankingAppLabel);

        //username label
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(20, 120, getWidth() - 30, 24);
        usernameLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(usernameLabel);

        //create username field
        JTextField usernameField = new JTextField();
        usernameField.setBounds(20, 160, getWidth() - 50, 40);
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 28));
        add(usernameField);

        //password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(20, 220, getWidth() - 50, 24);
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(passwordLabel);

        //create password field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(20, 260, getWidth() - 50, 40);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 28));
        add(passwordField);

        //re-type password
        JLabel reTypePassword = new JLabel("Re-type Password");
        reTypePassword.setBounds(20, 320, getWidth() - 50, 24);
        reTypePassword.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(reTypePassword);

        //create re-type password field
        JPasswordField reTypePasswordField = new JPasswordField();
        reTypePasswordField.setBounds(20, 360, getWidth() - 50, 40);
        reTypePasswordField.setFont(new Font("Dialog", Font.PLAIN, 28));
        add(reTypePasswordField);

        //create register button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(20, 460, getWidth() - 50, 40);
        registerButton.setFont(new Font("Dialog", Font.BOLD, 20));
    registerButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //get username
            String username = usernameField.getText();

            //get password
            String password = String.valueOf(passwordField.getPassword());

            //get re-type password
            String reType = reTypePasswordField.getText();

            //validate the user input
            if(validateUserInput(username, password, reType)){
                //attempt to register the user to the database
                if(MyJDBC.register(username, password)){
                    RegisterGui.this.dispose();
                    LoginGui loginGui = new LoginGui();
                    loginGui.setVisible(true);

                    JOptionPane.showMessageDialog(loginGui, "User Registered Successfully");
                }else{
                    JOptionPane.showMessageDialog(RegisterGui.this, "Error: Username already taken");
                }
            }else {
                JOptionPane.showMessageDialog(RegisterGui.this, "Error: Username has to be at least 6 characters and/or passwords must match");
            }
        }
    });
        add(registerButton);

        //create login label
        JLabel loginLabel = new JLabel("<html><a href = \"#\">Have an account? Sign-in here </a></html>");
        loginLabel.setBounds(0, 510, getWidth() - 10, 30);
        loginLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegisterGui.this.dispose();
                new LoginGui().setVisible(true);
            }
        });
        add(loginLabel);
    }

    private boolean validateUserInput(String username, String password, String reType) {
        //all fields must have a value
        if(username.length() == 0 || password.length() == 0 || reType.length() == 0) {
            return false;
        }

        //user has to be at least 6 characters long
        if(username.length() < 6){
            return false;
        }

        //password and re-type have to be the same
        if(!password.equals(reType)){
            return false;
        }
        return true;
    }
}
