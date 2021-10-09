package command;

import framework.command.RunnableCommand;
import framework.state.ApplicationState;
import framework.state.ApplicationStateAware;
import framework.utils.ConsoleUtils;
import framework.utils.MatrixUtils;
import framework.utils.ValidationUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class DeterminantCommand implements RunnableCommand, ApplicationStateAware {

    private ApplicationState state;

    @Override
    public void execute(String[] strings) {
        Array2DRowRealMatrix matrix = (Array2DRowRealMatrix) state.getVariable("matrix");
        ValidationUtils.requireEquals(matrix.getRowDimension(), matrix.getColumnDimension(), "Matrix is not square");
        double determinant = getDeterminant(matrix);
        ConsoleUtils.println(String.format("Determinant: %f", determinant));
    }

    private static double getDeterminant(Array2DRowRealMatrix matrix) {
        ValidationUtils.requireGreaterOrEqualThan(matrix.getRowDimension(), 1,
                "Matrix Row count must be >= 1");
        double result = 1.0;
        int swapCount = 0;
        for (int columnIndex = 0; columnIndex < matrix.getRowDimension(); columnIndex++) {
            int rowIndex = getIndexOfMaxByModule(matrix, columnIndex, columnIndex);
            if (rowIndex != columnIndex) {
                MatrixUtils.swapRows(matrix, rowIndex, columnIndex);
                swapCount++;
            }
            rowIndex = columnIndex;
            subtractRowFromOthersToMakeZeroesInColumn(matrix, rowIndex, columnIndex);
            result *= matrix.getEntry(rowIndex, columnIndex);
        }
        return Math.pow(-1, swapCount) * result;
    }

    private static void subtractRowFromOthersToMakeZeroesInColumn(RealMatrix matrix, int rowIndex, int columnIndex) {
        RealVector rowDividedByMaxElement = getRowDividedByMaxElement(matrix, rowIndex, columnIndex);
        for (int row = rowIndex + 1; row < matrix.getRowDimension(); row++) {
            RealVector rowFromWhichWeSubtract = matrix.getRowVector(row);
            double factor = rowFromWhichWeSubtract.getEntry(columnIndex);
            RealVector subtractedRow = rowDividedByMaxElement.mapMultiply(factor);
            RealVector result = rowFromWhichWeSubtract.subtract(subtractedRow);
            matrix.setRow(row, result.toArray());
        }
    }

    private static RealVector getRowDividedByMaxElement(RealMatrix matrix, int rowIndex, int columnIndex) {
        RealVector row = matrix.getRowVector(rowIndex);
        double maxByModuleElement = row.getEntry(columnIndex);
        ValidationUtils.requireNotEquals(maxByModuleElement, 0, "Column must not consist of zeroes");
        return row.mapDivide(maxByModuleElement);
    }

    private static int getIndexOfMaxByModule(Array2DRowRealMatrix matrix, int row, int column) {
        RealVector columnVector = matrix.getColumnVector(column);
        RealVector vectorWithAbsEntries = columnVector
                .getSubVector(row, matrix.getRowDimension() - row)
                .map(Math::abs);
        return row + vectorWithAbsEntries.getMaxIndex();
    }

    @Override
    public void setApplicationState(ApplicationState applicationState) {
        ValidationUtils.requireNonNull(applicationState);
        this.state = applicationState;
    }
}
