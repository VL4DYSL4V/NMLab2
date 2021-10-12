package command;

import framework.command.RunnableCommand;
import framework.command.parser.ArgsParser;
import framework.exception.LaboratoryFrameworkException;
import framework.state.ApplicationState;
import framework.state.ApplicationStateAware;
import framework.utils.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import util.EquationCommandUtils;

import java.util.Map;

public class JacobiCommand implements RunnableCommand, ApplicationStateAware {

    private ApplicationState applicationState;

    @Override
    public void execute(String[] args) {
        try {
            EquationCommandUtils.assertParametersSanity(this.applicationState);
            double precision = getPrecision(args);
            Array2DRowRealMatrix matrix = (Array2DRowRealMatrix) applicationState.getVariable("matrix");
            ArrayRealVector vector = (ArrayRealVector) applicationState.getVariable("vector");
            ConsoleUtils.println("Equation:");
            ConsoleUtils.printSystemOfLinearEquations(2, matrix, vector);
            RealVector result = solve(matrix, vector, precision);
            final int numbersAfterPointForResult = getNumbersAfterPointForResult(precision);
            ConsoleUtils.println("Result:");
            ConsoleUtils.printVector(numbersAfterPointForResult, result);
        } catch (LaboratoryFrameworkException e) {
            ConsoleUtils.println(e.getMessage());
        }
    }

    private static double getPrecision(String[] args) throws LaboratoryFrameworkException {
        final Map<String, String> paramToValue = ArgsParser.parseArgs(args);
        String precisionAsString = paramToValue.get("precision");
        if (precisionAsString == null) {
            return 1e-5;
        }
        double precision = ConverterUtils.doubleFromString(precisionAsString);
        ValidationUtils.requireLesserThan(precision, 1.0, "Precision must be < 1");
        return precision;
    }

    private static int getNumbersAfterPointForResult(double precision) {
        return String.valueOf(precision).substring(2).length();
    }

    private static RealVector solve(RealMatrix matrix, RealVector vector, double precision) {
        for (int columnIndex = 0; columnIndex < matrix.getColumnDimension(); columnIndex++) {
            int rowIndex = getIndexOfMaxByModule(matrix, columnIndex);
            divideRowByMaxElement(matrix, vector, rowIndex, columnIndex);
            swapRowsOfEquation(matrix, vector, rowIndex, columnIndex);
            rowIndex = columnIndex;
            matrix.setEntry(rowIndex, columnIndex, 0.0);
        }
        RealVector constantVector = vector.copy();
        RealVector currentGuess = vector;
        RealVector previousGuess;
        do {
            previousGuess = currentGuess;
            RealVector multiplicationResult = matrix.operate(currentGuess);
            currentGuess = constantVector.subtract(multiplicationResult);
        } while (computeFault(previousGuess, currentGuess) > precision);
        return currentGuess;
    }

    private static double computeFault(RealVector previousSolution, RealVector currentSolution) {
        RealVector diff = previousSolution.subtract(currentSolution);
        RealVector diffAbs = diff.map(Math::abs);
        return diffAbs.getMaxValue();
    }

    private static void divideRowByMaxElement(RealMatrix matrix, RealVector vector, int rowIndex, int columnIndex) {
        RealVector row = matrix.getRowVector(rowIndex);
        double maxByModuleElement = row.getEntry(columnIndex);
        ValidationUtils.requireNotEquals(maxByModuleElement, 0.0, "Column must not consist of zeroes");
        RealVector dividedRow = row.mapDivide(maxByModuleElement);
        matrix.setRow(rowIndex, dividedRow.toArray());
        vector.setEntry(rowIndex, vector.getEntry(rowIndex) / maxByModuleElement);
    }

    private static void swapRowsOfEquation(RealMatrix matrix, RealVector vector, int index1, int index2) {
        MatrixUtils.swapRows(matrix, index1, index2);
        VectorUtils.swapCoordinates(vector, index1, index2);
    }

    private static int getIndexOfMaxByModule(RealMatrix matrix, int column) {
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
