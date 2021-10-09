package command;

import framework.command.RunnableCommand;
import framework.state.ApplicationState;
import framework.state.ApplicationStateAware;
import framework.utils.ConsoleUtils;
import framework.utils.ValidationUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
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
        for (int columnIndex = 0; columnIndex < matrix.getColumnDimension(); columnIndex++) {
            int rowIndex = getIndexOfMaxByModule(matrix, columnIndex);
            divideRowByMaxElement(matrix, vector, rowIndex, columnIndex);
            swapRowsOfEquation(matrix, vector, rowIndex, columnIndex);
            rowIndex = columnIndex;
            subtractRowFromOthersToMakeZeroesInColumn(matrix, vector, rowIndex, columnIndex);
        }

        printMatrix(matrix);
        ConsoleUtils.println(vector.toString());
    }

    private static void printMatrix(RealMatrix matrix) {
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            ConsoleUtils.println(matrix.getRowVector(i).toString());
        }
    }

    private static void subtractRowFromOthersToMakeZeroesInColumn(RealMatrix matrix, RealVector vector, int rowIndex, int columnIndex) {
        RealVector rowToBeSubtracted = matrix.getRowVector(rowIndex);
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            if (row == rowIndex) {
                continue;
            }
            RealVector rowFromWhichWeSubtract = matrix.getRowVector(row);
            double factor = rowFromWhichWeSubtract.getEntry(columnIndex);
            RealVector subtractedRow = rowToBeSubtracted.mapMultiply(factor);
            RealVector result = rowFromWhichWeSubtract.subtract(subtractedRow);
            matrix.setRow(row, result.toArray());

            vector.setEntry(row, vector.getEntry(row) - vector.getEntry(rowIndex) * factor);
        }
    }

    private static void divideRowByMaxElement(RealMatrix matrix, RealVector vector, int rowIndex, int columnIndex) {
        RealVector row = matrix.getRowVector(rowIndex);
        double maxByModuleElement = row.getEntry(columnIndex);
        ValidationUtils.requireNotEquals(maxByModuleElement, 0, "Column must not consist of zeroes");
        RealVector dividedRow = row.mapDivide(maxByModuleElement);
        matrix.setRow(rowIndex, dividedRow.toArray());
        vector.setEntry(rowIndex, vector.getEntry(rowIndex) / maxByModuleElement);
    }

    private static void swapRowsOfEquation(RealMatrix matrix, RealVector vector, int index1, int index2){
        RealVector row1 = new ArrayRealVector(matrix.getRowVector(index1));
        RealVector row2 = new ArrayRealVector(matrix.getRowVector(index2));
        matrix.setRow(index1, row2.toArray());
        matrix.setRow(index2, row1.toArray());

        double entry1 = vector.getEntry(index1);
        double entry2 = vector.getEntry(index2);
        vector.setEntry(index1, entry2);
        vector.setEntry(index2, entry1);
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
