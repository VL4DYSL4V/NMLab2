package command;

import framework.command.RunnableCommand;
import framework.state.ApplicationState;
import framework.state.ApplicationStateAware;
import framework.utils.ConsoleUtils;
import framework.utils.ValidationUtils;

public class JacobiCommand implements RunnableCommand, ApplicationStateAware {

    private ApplicationState applicationState;

    @Override
    public void execute(String[] strings) {
        ConsoleUtils.println("Hello from jacobi");

    }

    @Override
    public void setApplicationState(ApplicationState applicationState) {
        ValidationUtils.requireNonNull(applicationState);
        this.applicationState = applicationState;
    }
}
