package org.zet.cellularautomaton.potential;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zetool.common.algorithm.AbstractAlgorithm;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PotentialAlgorithm extends AbstractAlgorithm<List<ExitCell>, StaticPotential> {
    private static final int APPROXIMATE_ORTHOGONAL_DISTANCE = 10;
    private static final int APPROXIMATE_DIAGONAL_DISTANCE = 14;
    
    @Override
    protected StaticPotential runAlgorithm(List<ExitCell> problem) {
        return createStaticPotential(problem);
    }

    /**
     * Calculates a StaticPotential starting at the ExitCell specified in the parameter exitBlock. The potential
     * describes the distance between a cell an the given ExitCells. Such a potential uses some "Smoothing" to
     * approximate the real distance. It calculates also a second StaticPotential which represents the distance nearly
     * exactly. For the diagonal distance between two cells 1.4 instead of sqrt(2) is used.
     *
     * @param exitBlock list of ExitCells
     * @return the calculated StaticPotential
     */
    public StaticPotential createStaticPotential(List<ExitCell> exitBlock) {
        StaticPotential staticPotential = new StaticPotential();
        staticPotential.setAssociatedExitCells(exitBlock);
        staticPotential.setAttractivity(exitBlock.get(0).getAttractivity());
        List<? extends EvacCellInterface> parentList;
        Map<EvacCellInterface, SmoothingTuple> childTuple;

        for (ExitCell c : exitBlock) {
            staticPotential.setPotential(c, 0);
            staticPotential.setDistance(c, 0.0);
        }

        parentList = exitBlock;
        while (!parentList.isEmpty()) {
            childTuple = new HashMap<>();
            for (EvacCellInterface parent : parentList) {
                for (EvacCellInterface c : getNeighbours(parent)) {
                    if (!(c instanceof ExitCell) && !(staticPotential.hasValidPotential(c))) {
                        //check if there already exists a tuple for this cell
                        if (childTuple.containsKey(c)) {
                            childTuple.get(c).addParent(staticPotential.getPotentialDouble(parent), calculateDistance(parent, c));
                            childTuple.get(c).addDistanceParent(staticPotential.getDistance(parent), calculateRealDistance(parent, c));
                        } else {
                            childTuple.put(c, new SmoothingTuple(c,
                                    staticPotential.getPotentialDouble(parent), calculateDistance(parent, c),
                                    calculateRealDistance(parent, c) + staticPotential.getDistance(parent)
                                    ));

                        }
                    } else {
                        Logger.getGlobal().warning("Reached an exit cell that does not get a potential!");
                    }
                }
            }
            
            List<EvacCellInterface> childList = new ArrayList<>();
            for (SmoothingTuple smoothingTuple : childTuple.values()) {
                smoothingTuple.applySmoothing();
                staticPotential.setPotential(smoothingTuple.getCell(), smoothingTuple.getValue());
                staticPotential.setDistance(smoothingTuple.getCell(), smoothingTuple.getDistanceValue());
                childList.add(smoothingTuple.getCell());
            }
            parentList = childList;
        }
        return staticPotential;
    }

    /**
     * Calculates nearly the physical distance between two neighbour cells.
     *
     * @param c one neighbour
     * @param n the other neighbour
     * @return 10 if the two cells are horizontal or vertical neighbours, 14 else
     */
    public int calculateDistance(EvacCellInterface c, EvacCellInterface n) {
        if ((c.getX() == n.getX()) || (c.getY() == n.getY()) || (c instanceof DoorCell && n instanceof DoorCell)) {
            return APPROXIMATE_ORTHOGONAL_DISTANCE;
        } else {
            return APPROXIMATE_DIAGONAL_DISTANCE;
        }
    }

    /**
     * Calculates nearly the physical distance between two neighbour cells.
     *
     * @param c one neighbour
     * @param n the other neighbour
     * @return 10 if the two cells are horizontal or vertical neighbours, 14 else
     */
    public double calculateRealDistance(EvacCellInterface c, EvacCellInterface n) {
        if ((c.getX() == n.getX()) || (c.getY() == n.getY()) || (c instanceof DoorCell && n instanceof DoorCell)) {
            return 0.4;
        } else {
            return Math.sqrt(2) * 0.4;
        }
    }

    /**
     * Returns the neighbors of the cell. Uses the method of {@code EvacCell}.
     *
     * @param cell A cell in the cellular automaton.
     * @return The neighbor cells of this cell.
     */
    public List<EvacCellInterface> getNeighbours(EvacCellInterface cell) {
        return cell.getNeighbours();
    }
}
