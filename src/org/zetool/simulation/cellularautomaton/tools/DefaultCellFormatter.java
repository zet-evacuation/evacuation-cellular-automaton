package org.zetool.simulation.cellularautomaton.tools;

import org.zetool.simulation.cellularautomaton.Cell;

/**
 * 
 * @author Jan-Philipp Kappmeier
 */
public class DefaultCellFormatter<E extends Cell<E,S>,S> implements CellFormatter<E> {
    private static final String DEFAULT_CELL_STRING = "   ";
    @Override
    public String format(E cell) {
        return DEFAULT_CELL_STRING;
    }
    
}
