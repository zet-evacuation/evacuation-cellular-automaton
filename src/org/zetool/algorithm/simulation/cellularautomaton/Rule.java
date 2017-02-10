package org.zetool.algorithm.simulation.cellularautomaton;

import org.zetool.simulation.cellularautomaton.Cell;

/**
 * A rule that can be executed on a given cell type.
 * @param <R> the result type
 * @param <S> the cell type
 * @author Jan-Philipp Kappmeier
 */
public interface Rule<R, S extends Cell> {

    public R execute(S cell);

    public boolean executableOn(S cell);
}
