package org.zet.cellularautomaton;

import org.zetool.simulation.cellularautomaton.CellFormatter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonCellFormatter implements CellFormatter<EvacCell> {
    @Override
    public String format(EvacCell cell) {
        return cell.graphicalToString();
    }
    
}
