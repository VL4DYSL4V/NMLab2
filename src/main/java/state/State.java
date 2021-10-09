package state;

import framework.state.AbstractApplicationState;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

public class State extends AbstractApplicationState {

    private final Array2DRowRealMatrix matrix =
            new Array2DRowRealMatrix(new double[][]{
                    {0, 1, 2, -3},
                    {4, 3, -1, 11},
                    {6, 4, 0, -2},
                    {-1, 4, 2, 3}
            });

    private final ArrayRealVector vector =
            new ArrayRealVector(new double[]{3, 4, 8, -6});

    @Override
    protected void initVariableNameToGettersMap() {
        this.variableNameToGetter.put("matrix", () -> this.matrix.copy());
        this.variableNameToGetter.put("vector", () -> this.vector.copy());
    }
}
