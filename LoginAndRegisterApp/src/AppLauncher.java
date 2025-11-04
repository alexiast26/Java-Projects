import db.MyJDBC;
import guis.LoginFormGUI;
import guis.RegisterFormGUI;

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //instantiate a loginFormGui obj and make it visible
                new LoginFormGUI().setVisible(true);
                //new RegisterFormGUI().setVisible(true);

                //check user test
                //System.out.println(MyJDBC.checkUser("username"));

                //check register test
                //System.out.println(MyJDBC.register("username1", "password1"));

                //check validate login test
                //System.out.println(MyJDBC.validateLogin("username1", "password1"));
            }
        });
    }
}