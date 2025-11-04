import db_obj.User;
import gui.BankingAppGui;
import gui.LoginGui;
import gui.RegisterGui;

import javax.swing.*;
import java.math.BigDecimal;

public class AppLauncher {
    public static void main(String[] args) {
        //we use invoke later to make updates to the GUI thread friendly
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginGui().setVisible(true);
                //new RegisterGui().setVisible(true);
                /*new BankingAppGui(
                        new User(1, "username", "password", new BigDecimal("20.00"))
                ).setVisible(true);
                 */
            }
        });
    }
}