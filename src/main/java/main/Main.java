package main;

import command.GaussCommand;
import command.JacobiCommand;
import framework.application.Application;
import framework.state.ApplicationState;
import state.State;

public class Main {

    private static final String PROPERTY_PATH = "/laboratory-framework.example.properties";

    public static void main(String[] args) {
        ApplicationState state = new State();
        Application application = new Application.ApplicationBuilder(PROPERTY_PATH, state)
                .addCommand("gauss", new GaussCommand())
                .addCommand("jacobi", new JacobiCommand())
                .build();
        application.start();
    }

}
