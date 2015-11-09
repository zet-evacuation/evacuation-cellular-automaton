package org.zetool.simulation.cellularautomaton.tools;

import org.zetool.simulation.cellularautomaton.Cell;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @param <E>
 */
@FunctionalInterface
public interface CellFormatter<E extends Cell<?>> {
    public String format(E cell);
}
