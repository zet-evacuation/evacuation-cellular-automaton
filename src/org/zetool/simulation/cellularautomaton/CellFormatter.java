package org.zetool.simulation.cellularautomaton;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @param <C>
 */
@FunctionalInterface
public interface CellFormatter<C extends Cell> {
    public String format(C cell);
}
