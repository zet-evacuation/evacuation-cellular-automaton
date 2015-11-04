package org.zetool.simulation.cellularautomaton.tools;

import org.zetool.simulation.cellularautomaton.Cell;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @param <C>
 */
@FunctionalInterface
public interface CellFormatter<C extends Cell> {
    public String format(C cell);
}
