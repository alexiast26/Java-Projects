package guis;

import constants.CommonConstants;
import db.MyJDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterFormGUI extends Form{

    public RegisterFormGUI() {
        super("Register");
        addGUIComponents();
    }

    private void addGUIComponents() {
        //create login label
        JLabel registerLabel = new JLabel("Register");

        //configure component's x, y position and width/height values relative to the GUI
        registerLabel.setBounds(0, 25, 520, 100);

        //change font color
        registerLabel.setForeground(CommonConstants.TEXT_COLOR);

        //change the font size
        registerLabel.setFont(new Font("Dialog", Font.BOLD, 40));

        //center text
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(registerLabel);

        //create username label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 150, 400, 25);
        usernameLabel.setForeground(CommonConstants.TEXT_COLOR);
        usernameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        //create username text field
        JTextField usernameField = new JTextField("");
        usernameField.setBounds(30, 185, 450, 55);
        usernameField.setBackground(CommonConstants.SECONDARY_COLOR);
        usernameField.setForeground(CommonConstants.TEXT_COLOR);
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(usernameLabel);
        add(usernameField);

        //create password label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 255, 400, 25);
        passwordLabel.setForeground(CommonConstants.TEXT_COLOR);
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        //create password text field
        JPasswordField passwordField = new JPasswordField("");
        passwordField.setBounds(30, 285, 450, 55);
        passwordField.setBackground(CommonConstants.SECONDARY_COLOR);
        passwordField.setForeground(CommonConstants.TEXT_COLOR);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(passwordLabel);
        add(passwordField);

        //create re-enter password label
        JLabel rePasswordLabel = new JLabel("Re-enter Password:");
        rePasswordLabel.setBounds(30, 365, 400, 25);
        rePasswordLabel.setForeground(CommonConstants.TEXT_COLOR);
        rePasswordLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        //create re-enter password text field
        JPasswordField rePasswordField = new JPasswordField("");
        rePasswordField.setBounds(30, 395, 450, 55);
        rePasswordField.setBackground(CommonConstants.SECONDARY_COLOR);
        rePasswordField.setForeground(CommonConstants.TEXT_COLOR);
        rePasswordField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(rePasswordLabel);
        add(rePasswordField);

        //create login button
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Dialog", Font.BOLD, 18));


        //change the cursor to a hand when hover over the button
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.setBackground(CommonConstants.TEXT_COLOR);
        registerButton.setBounds(125, 520, 250, 50);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get username and password
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String rePassword = new String(rePasswordField.getPassword());

                //validate user input
                if(validateUserInput(username, password, rePassword)){
                    //register user in the database
                    if(MyJDBC.register(username, password)){
                        //disspose of this gui
                        RegisterFormGUI.this.dispose();

                        //take user back to login
                        LoginFormGUI loginFormGUI = new LoginFormGUI();
                        loginFormGUI.setVisible(true);

                        //create a result dialog
                        JOptionPane.showMessageDialog(loginFormGUI, "Register Successful");
                    }else{
                        //register has failed due to user already existing
                        JOptionPane.showMessageDialog(RegisterFormGUI.this, "Register Failed. User Already Exists");
                    }
                }else{
                    JOptionPane.showMessageDialog(RegisterFormGUI.this, "USername must be at least 6 characters and/or passwords do not match");
                }
            }
        });
        add(registerButton);

        //create register label (used to load the register GUI)
        JLabel loginLabel = new JLabel("Have an account? Login Here:");
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.setForeground(CommonConstants.TEXT_COLOR);

        //add functionality to switch to the login form
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegisterFormGUI.this.dispose();
                new LoginFormGUI().setVisible(true);
            }
        });

        loginLabel.setBounds(125, 600, 250, 30);
        add(loginLabel);

    }
    private boolean validateUserInput(String username, String password, String rePassword) {
        //all fields must have a value
        if(username.length() == 0 || password.length() == 0 || rePassword.length() == 0) {
            return false;
        }

        //username has to be at least 6 characters long
        if(username.length() < 6){
            return false;
        }

        //password and re-passwords must be equal
        if(!password.equals(rePassword)) {
            return false;
        }
        return true;
    }

}
