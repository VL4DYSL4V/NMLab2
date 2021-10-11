package command;

import framework.command.RunnableCommand;
import framework.state.ApplicationState;
import framework.state.ApplicationStateAware;
import framework.utils.ConsoleUtils;
import framework.utils.ValidationUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class ConditionNumberCommand implements RunnableCommand,
        ApplicationStateAware {

    private ApplicationState state;

    @Override
    public void execute(String[] strings) {
        Array2DRowRealMatrix matrix = (Array2DRowRealMatrix) state.getVariable("matrix");
        RealMatrix inversed = MatrixUtils.inverse(matrix);
        // Frobenius norm is Euclidian norm. It is equal to sqrt of sum of all elements^2
        double conditionNumber = matrix.getFrobeniusNorm() * inversed.getFrobeniusNorm();
        ConsoleUtils.println(String.format("Condition number: %f", conditionNumber));
    }

    @Override
    public void setApplicationState(ApplicationState applicationState) {
        ValidationUtils.requireNonNull(applicationState);
        this.state = applicationState;
    }

}
