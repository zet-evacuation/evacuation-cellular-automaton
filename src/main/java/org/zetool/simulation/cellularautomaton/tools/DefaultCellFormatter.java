package org.zetool.simulation.cellularautomaton.tools;

import org.zetool.simulation.cellularautomaton.Cell;

/**
 * 
 * @author Jan-Philipp Kappmeier
 * @param <E>
 */
public class DefaultCellFormatter<E extends Cell> implements CellFormatter<E> {
    private static final String DEFAULT_CELL_STRING = "   ";
    @Override
    public String format(Cell cell) {
        return DEFAULT_CELL_STRING;
    }
    
}
