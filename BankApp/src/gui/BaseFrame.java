package gui;

import db_obj.User;

import javax.swing.*;

/*
    An abstract class that will be the blueprint for our GUIS
 */
public abstract class BaseFrame extends JFrame {
    //store user information
    protected User user;

    public BaseFrame(String title) {
        initialize(title);
    }

    public BaseFrame(String title, User user) {
        //initialize user
        this.user = user;
        initialize(title);
    }

    private void initialize(String title) {
        setTitle(title);

        setSize(420, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        setLocationRelativeTo(null);

        addGuiComponent();
    }

    //this will need to be defined by subclasses when this class is being inherited
    protected abstract void addGuiComponent();
}
