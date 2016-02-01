package org.zet.cellularautomaton;

import java.util.Collection;
import java.util.Map;
import org.zet.cellularautomaton.potential.DynamicPotential;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.simulation.cellularautomaton.CellularAutomaton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface EvacuationCellularAutomatonInterface extends CellularAutomaton<EvacCell> {

    public double getStepsPerSecond();

    public double absoluteSpeed(double relativeSpeed);

    public void moveIndividual(EvacCell from, EvacCell to);

    public double getSecondsPerStep();

    public void swapIndividuals(EvacCell cell1, EvacCell cell2);

    public Map<StaticPotential, Double> getExitToCapacityMapping();

    public Collection<Room> getRooms();

    // new
    public Collection<StaticPotential> getStaticPotentials();

    public StaticPotential getSafePotential();

    public StaticPotential minPotentialFor(EvacCell c);

}
