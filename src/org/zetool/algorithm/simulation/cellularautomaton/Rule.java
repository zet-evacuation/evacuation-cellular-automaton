package org.zetool.algorithm.simulation.cellularautomaton;

import org.zetool.simulation.cellularautomaton.Cell;

/**
 * A rule that can be executed on a given cell type.
 * @param <S> the cell type
 * @author Jan-Philipp Kappmeier
 */
public interface Rule<S extends Cell> {

    public void execute(S cell);

    public boolean executableOn(S cell);
}
