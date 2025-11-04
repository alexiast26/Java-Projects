package constants;

import java.awt.*;

public class CommonConstants {
    //color hex values
    public static final Color PRIMARY_COLOR = Color.decode("#4D384D");
    public static final Color SECONDARY_COLOR = Color.decode("#784778");
    public static final Color TEXT_COLOR = Color.decode("#DEB6DE");

    //mysql credentials
    public static final String DB_URL = "jdbc:mysql://localhost:3306/login_schema";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root";
    public static final String DB_USERS_TABLE_NAME = "users";
}
