package org.zet.cellularautomaton;

import java.util.List;
import org.zetool.simulation.cellularautomaton.LocatedCellMatrix;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Room extends LocatedCellMatrix<EvacCell> {

    // Overridden from interface and specialized returns
    @Override
    public List<EvacCell> getAllCells();

    // multi-matrix cellular automata
    @Override
    public int getXOffset();

    @Override
    public int getYOffset();
    
    // necessary, but probably bad name?
    public List<DoorCell> getDoors();
    public int getCellCount(boolean allCells);
    
    // special parameters for evacuation simulation
    public int getID();

    public boolean isAlarmed();

    public void setAlarmstatus(boolean status);

    public int getFloorID();


    // status-related, probably to be moved somewhere else!
    
    public void addIndividual(EvacCell c, Individual i);
    public List<Individual> getIndividuals();

    public void moveIndividual(EvacCell from, EvacCell to);

    public void removeIndividual(Individual i);

    public void swapIndividuals(EvacCell cell1, EvacCell cell2);
}
