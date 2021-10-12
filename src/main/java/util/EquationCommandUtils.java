package util;

import framework.exception.LaboratoryFrameworkException;
import framework.state.ApplicationState;
import framework.utils.ValidationUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

public class EquationCommandUtils {

    private EquationCommandUtils() {}

    public static void assertParametersSanity(ApplicationState applicationState) throws LaboratoryFrameworkException {
        ValidationUtils.requireNonNull(applicationState);
        Array2DRowRealMatrix matrix = (Array2DRowRealMatrix) applicationState.getVariable("matrix");
        ArrayRealVector vector = (ArrayRealVector) applicationState.getVariable("vector");
        ValidationUtils.requireNonNull(matrix, vector);
        ValidationUtils.requireEquals(matrix.getRowDimension(), matrix.getColumnDimension(), "Matrix is not square");
        ValidationUtils.requireEquals(vector.getDimension(), matrix.getRowDimension(),
                "Vector length and matrix row count must be equal");
    }

}
