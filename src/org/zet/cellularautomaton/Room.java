package org.zet.cellularautomaton;

import java.util.List;
import org.zetool.simulation.cellularautomaton.CellMatrix;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Room extends CellMatrix<EvacCell, EvacuationCellState>, Cloneable {

    @Override
    public List<EvacCell> getAllCells();

    public List<DoorCell> getDoors();

    public List<Individual> getIndividuals();

    public void setCell(EvacCell cell);

    public int getCellCount(boolean allCells);

    public int getID();

    public boolean isAlarmed();

    public void setAlarmstatus(boolean status);

    // TODO: remove
    public int getFloorID();

    public int getXOffset();

    public int getYOffset();

    public Room clone();

    public void clear();

    public void addIndividual(EvacCell c, Individual i);

    public void moveIndividual(EvacCell from, EvacCell to);

    public void removeIndividual(Individual i);

    public void swapIndividuals(EvacCell cell1, EvacCell cell2);
}
