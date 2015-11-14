package org.zetool.simulation.cellularautomaton;

import org.zetool.common.function.IntBiFunction;

/**
 * A geometric cell matrix is a {@link FiniteCellMatrix} that has a location. Using the index of the cells in the
 * cell matrix and the location as offset, multiple {@code GeometricCellMatrix} objects can be used to compose a larger
 * cellular automaton cell matrix.
 * 
 * @param <E> the type of cells stored in the matrix
 * @author Jan-Philipp Kappmeier
 */
public class GeometricCellMatrix <E extends Cell> extends FiniteCellMatrix<E> {
    private int xOffset;
    private int yOffset;

    public GeometricCellMatrix(int width, int height, int xOffset, int yOffset) {
        super(width, height);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public GeometricCellMatrix(int width, int height, int xOffset, int yOffset, IntBiFunction<E> cellGenerator) {
        super(width, height, cellGenerator);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }
    
}
