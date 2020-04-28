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
package org.zet.cellularautomaton.algorithm.state;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zetool.common.debug.Debug;

/**
 * Provides actions to change the evacuation state. Alters the state of the simulation and of individuals and ensures
 * that after each action the simulation remains in a well-defined state.
 * 
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationStateController implements EvacuationStateControllerInterface {
    /** The logger object of this algorithm. */
    private static final Logger LOG = Debug.globalLogger;
    static {
        LOG.setLevel(Level.ALL);
    }
    private final MutableEvacuationState evacuationState;
    
    public EvacuationStateController(MutableEvacuationState evacuationState) {
        this.evacuationState = evacuationState;
    }

    @Override
    public void move(EvacCellInterface from, EvacCellInterface to) {
        Individual i = getAndCheck(from);
        if (from.equals(to)) {
            return;
        }
        remove(i);
        add(i, to);
    }

    @Override
    public void swap(EvacCellInterface from, EvacCellInterface to) {
        Individual i1 = getAndCheck(from);
        Individual i2 = getAndCheck(to);
        if (from.equals(to)) {
            return;
        }
        remove(i1);
        remove(i2);
        add(i2, from);
        add(i1, to);
    }
    
    private Individual getAndCheck(EvacCellInterface cell) {
        if (cell.getState().isEmpty()) {
            throw new IllegalArgumentException("No Individual standing on cell " + cell);
        }
        return cell.getState().getIndividual();
    }

    void add(Individual i, EvacCellInterface cell) {
        LOG.fine("Add " + i + " to " + cell);
        evacuationState.propertyFor(i).setCell(cell);
        cell.getState().setIndividual(i);
        cell.getRoom().addIndividual(cell, i);
    }

    @Override
    public void die(Individual i, DeathCause cause) {
        evacuationState.propertyFor(i).setDeathCause(cause);
        evacuationState.addToDead(i);
        remove(i);
    }
    
    @Override
    public void setSafe(Individual i) {
        evacuationState.propertyFor(i).setSafetyTime(evacuationState.getStep());
        evacuationState.addToSafe(i);
    }
    
    @Override
    public void evacuate(Individual i) {
        evacuationState.propertyFor(i).setEvacuationTime(evacuationState.getStep());
        LOG.fine("Evacuate " + i);
        remove(i);
        evacuationState.addToEvacuated(i);
    }

    /**
     * Removes an individual. This has to be called when an individual dies, is evacuated, moved or swapped.
     * @param i an individual
     */
    void remove(Individual i) {
        EvacCellInterface from = evacuationState.propertyFor(i).getCell();
        LOG.fine("Remove " + i + " from " + from);
        from.getRoom().removeIndividual(i);
        evacuationState.propertyFor(i).setCell(null);
        from.getState().removeIndividual();
//        
//            if(!propertyFor(i).isEvacuated()) {
//                propertyFor(i).setEvacuationTime(evacuationState.getStep());
//            }
//            addToEvacuated(individual);
        
    }

    @Override
    public void increaseDynamicPotential(EvacCellInterface c) {
        evacuationState.increaseDynamicPotential(c);
    }

    @Override
    public void updateDynamicPotential(double probabilityDynamicIncrease, double probabilityDynamicDecrease) {
        evacuationState.updateDynamicPotential(probabilityDynamicIncrease, probabilityDynamicDecrease);
    }
}