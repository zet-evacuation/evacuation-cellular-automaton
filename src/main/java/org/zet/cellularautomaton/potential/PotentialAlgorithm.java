/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.potential;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zetool.common.algorithm.AbstractAlgorithm;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PotentialAlgorithm extends AbstractAlgorithm<Collection<ExitCell>, StaticPotential> {
    private static final int APPROXIMATE_ORTHOGONAL_DISTANCE = 10;
    private static final int APPROXIMATE_DIAGONAL_DISTANCE = 14;
    
    @Override
    protected StaticPotential runAlgorithm(Collection<ExitCell> problem) {
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
    public StaticPotential createStaticPotential(Collection<ExitCell> exitBlock) {
        StaticPotential staticPotential = new StaticPotential();
        //staticPotential.setAssociatedExitCells(exitBlock);
        //staticPotential.setAttractivity(exitBlock.get(0).getAttractivity());
        Collection<? extends EvacCellInterface> parentList;

        for (ExitCell c : exitBlock) {
            staticPotential.setPotential(c, 0);
            staticPotential.setDistance(c, 0.0);
        }

        parentList = exitBlock;
        while (!parentList.isEmpty()) {
            Map<EvacCellInterface, SmoothingTuple> childTuple = computeChildTuples(staticPotential, parentList);
            parentList = computeChildList(staticPotential, childTuple);
        }
        return staticPotential;
    }

    private Map<EvacCellInterface, SmoothingTuple> computeChildTuples(StaticPotential staticPotential, 
            Collection<? extends EvacCellInterface> parentList) {
        Map<EvacCellInterface, SmoothingTuple> childTuple = new HashMap<>();
        for (EvacCellInterface parent : parentList) {
            addToChildTuples(staticPotential, parent, childTuple);
        }
        return childTuple;
    }
    
    private void addToChildTuples(StaticPotential staticPotential, EvacCellInterface parent,
            Map<EvacCellInterface, SmoothingTuple> childTuple) {
        for (EvacCellInterface c : getNeighbours(parent)) {
            if (!(c instanceof ExitCell) && !(staticPotential.hasValidPotential(c))) {
                //check if there already exists a tuple for this cell
                if (!childTuple.containsKey(c)) {
                    childTuple.put(c, createTuple(staticPotential, parent, c));
                } else {                    
                    updateTuple(childTuple.get(c), staticPotential, parent, c);
                }
            }
        }
    }

    private SmoothingTuple createTuple(StaticPotential staticPotential, EvacCellInterface parent, EvacCellInterface c) {
        return new SmoothingTuple(c, staticPotential.getPotentialDouble(parent), calculateDistance(parent, c),
                calculateRealDistance(parent, c) + staticPotential.getDistance(parent));
        
    }
    
    private void updateTuple(SmoothingTuple s, StaticPotential staticPotential, EvacCellInterface parent, EvacCellInterface c) {
        s.addParent(staticPotential.getPotentialDouble(parent), calculateDistance(parent, c));
        s.addDistanceParent(staticPotential.getDistance(parent), calculateRealDistance(parent, c));
    }
    
    private List<EvacCellInterface> computeChildList(StaticPotential staticPotential,
            Map<EvacCellInterface, SmoothingTuple> childTuple) {
        List<EvacCellInterface> childList = new ArrayList<>();
        for (SmoothingTuple smoothingTuple : childTuple.values()) {
            smoothingTuple.applySmoothing();
            staticPotential.setPotential(smoothingTuple.getCell(), smoothingTuple.getValue());
            staticPotential.setDistance(smoothingTuple.getCell(), smoothingTuple.getDistanceValue());
            childList.add(smoothingTuple.getCell());
        }
        return childList;
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
