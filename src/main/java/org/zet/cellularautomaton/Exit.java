package org.zet.cellularautomaton;

import java.util.Collection;

/**
 * An exit of the evacuation scenario. Individuals try to reach an exit.
 *
 * An exit consists of its {@link EvacCell}s and the corresponding floor field.
 *
 * @author Jan-Philipp Kappmeier
 */
public class Exit {

    private final String name;
    private Collection<ExitCell> exitCells;
    private int attractivity;

    public Exit(String name, Collection<ExitCell> exitCells) {
        this.name = name;
        this.exitCells = exitCells;
    }

    Exit(Collection<ExitCell> exitCluster) {
        this.exitCells = exitCluster;
        this.name = "";
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Collection<ExitCell> getExitCluster() {
        return exitCells;
    }

    public int getAttractivity() {
        return attractivity;
    }

    public double getCapacity() {
        return Double.POSITIVE_INFINITY;
    }

    public void setAttractivity(int attractivity) {
        this.attractivity = attractivity;
    }

}
