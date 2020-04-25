package org.zet.cellularautomaton.potential;

import org.zet.cellularautomaton.RoomCell;
import org.zetool.simulation.cellularautomaton.tools.CellFormatter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PotentialCellFormatter implements CellFormatter<RoomCell> {

    private final Potential dynamicPotential;

    public PotentialCellFormatter(Potential d) {
        this.dynamicPotential = d;
    }

    @Override
    public String format(RoomCell cell) {
        int potential = dynamicPotential.getPotential(cell);
        if (potential < 10) {
            return " " + potential + " ";
        }
        if (potential < 100) {
            return " " + potential;
        }
        if (potential < 1000) {
            return "" + potential;
        }
        return "###";
    }

}
