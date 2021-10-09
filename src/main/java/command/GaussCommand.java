package command;

import framework.command.RunnableCommand;
import framework.state.ApplicationState;
import framework.state.ApplicationStateAware;
import framework.utils.ConsoleUtils;
import framework.utils.ValidationUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class GaussCommand implements RunnableCommand, ApplicationStateAware {

    private ApplicationState applicationState;

    @Override
    public void execute(String[] strings) {
        Array2DRowRealMatrix matrix = (Array2DRowRealMatrix) applicationState.getVariable("matrix");
        ValidationUtils.requireEquals(matrix.getRowDimension(), matrix.getColumnDimension(), "Matrix is not square");
        ArrayRealVector vector = (ArrayRealVector) applicationState.getVariable("vector");
        ValidationUtils.requireEquals(vector.getDimension(), matrix.getRowDimension(),
                "Vector length and matrix row count must be equal");
        solve(matrix, vector);
    }

    private static void solve(Array2DRowRealMatrix matrix, ArrayRealVector vector) {
        for (int column = 0; column < matrix.getColumnDimension(); column++) {
            ConsoleUtils.println(String.valueOf(getIndexOfMaxByModule(matrix, column)));
        }
    }

    private static int getIndexOfMaxByModule(Array2DRowRealMatrix matrix, int column) {
        RealVector columnVector = matrix.getColumnVector(column);
        RealVector vectorWithAbsEntries = columnVector.map(Math::abs);
        return vectorWithAbsEntries.getMaxIndex();
    }

    @Override
    public void setApplicationState(ApplicationState applicationState) {
        ValidationUtils.requireNonNull(applicationState);
        this.applicationState = applicationState;
    }
}
