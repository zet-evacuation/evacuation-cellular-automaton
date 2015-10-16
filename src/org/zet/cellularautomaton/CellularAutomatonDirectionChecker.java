package org.zet.cellularautomaton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@FunctionalInterface
public interface CellularAutomatonDirectionChecker {
    public boolean canPass(Individual i, EvacCell from, EvacCell to);
}
