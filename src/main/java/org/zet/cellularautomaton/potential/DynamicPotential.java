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

import java.util.Iterator;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.localization.CellularAutomatonLocalization;
import org.zet.cellularautomaton.results.Action;
import org.zet.cellularautomaton.results.DynamicPotentialChangeAction;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.generators.GeneralRandom;

/**
 * A DynamicPotential is a potential that additionally to handling a potential sends out messages to store the results.
 */
public class DynamicPotential extends AbstractPotential {

    /**
     * Creates a DynamicPotential.
     */
    public DynamicPotential() {
        super();
    }

    @Override
    public void setPotential(EvacCellInterface cell, double value) {
        super.setPotential(cell, value);
        recordAction(new DynamicPotentialChangeAction(cell, value));
    }

    /**
     * {@inheritDoc} The Potential value of the removed cell is saved as 0 in the {@link VisualResultsRecorder}.
     *
     * @param cell A EvacCell whose mapping should be removed
     * @throws IllegalArgumentException if the cell is not contained in the map
     */
    @Override
    public void deleteCell(EvacCellInterface cell) {
        super.deleteCell(cell);
        recordAction(new DynamicPotentialChangeAction(cell, 0));
    }

    protected void recordAction(Action a) {
        // ignore publishing
    }

    /**
     * Get the potential of a specified EvacCell. The method returns 0 if you try to get the potential of a cell that
     * does not exists.
     *
     * @param cell A cell which potential you want to know.
     * @return potential of the specified cell or -1 if the cell is not mapped by this potential
     */
    @Override
    public int getPotential(EvacCellInterface cell) {
        return hasValidPotential(cell) ? super.getPotential(cell) : 0;
    }

    @Override
    public double getMaxPotentialDouble() {
        return Math.max(0, super.getMaxPotentialDouble());
    }

    @Override
    public int getMaxPotential() {
        return Math.max(0, super.getMaxPotential());
    }

    /**
     * This method updates the values stored in the dynamic potential in the following way. With the
     * probability decay a cell decreases its dynamic potential by one. Afterwards a cell with a
     * dynamic potential greater than zero increases the dynamic potential of one of its neighbour
     * cells by one.
     *
     * @param diffusion The probability of increasing the dynamic potential of one neighbour cell of
     * a cell with a dynamic potential greater than zero by one.
     * @param decay The probability of decreasing the dynamic potential of a cell.
     */
    public void update(double diffusion, double decay) {
        GeneralRandom rnd = RandomUtils.getInstance().getRandomGenerator();
        DynamicPotential dynPot = this;
        EvacCellInterface[] cellsCopy = dynPot.getMappedCells().toArray(new EvacCellInterface[dynPot.getMappedCells().size()]);
        /* NEW CODE */
        for (EvacCellInterface c : cellsCopy) {
            double randomNumber = rnd.nextDouble();
            if ( /*dynPot.getPotential(c) > 0 && */diffusion > randomNumber) {
                // Potential diffuses to a a neighbour cell. It should not increase, so
                // reduce it afterwards on this cell!
                EvacCellInterface randomNeighbour = null;
                while (randomNeighbour == null) {
                    final int randomInt = rnd.nextInt((c.getNeighbours()).size());
                    randomNeighbour = (c.getNeighbours()).get(randomInt);
                }
                decrease(c);
                // test, if now potential is 0 so the potential in the diffused cell can decrease already in this step.
                randomNumber = rnd.nextDouble();
                if (!(dynPot.getPotential(c) == 0 && decay > randomNumber)) {
                    increase(randomNeighbour);
                }
            }
            randomNumber = rnd.nextDouble();
            if (dynPot.getPotential(c) > 0 && decay > randomNumber) {
                decrease(c);
            }
        }
    }

    /**
     * Increases the potential of the specified EvacCell about one. Associates the specified potential with the
     * specified EvacCell in this PotentialMap.
     *
     * @param cell A cell which potential you want to increase.
     */
    public void increase(EvacCellInterface cell) {
        int newPotential;
        DynamicPotential dynPot = this;
        if (dynPot.hasValidPotential(cell)) {
            newPotential = dynPot.getPotential(cell) + 1;
            dynPot.deleteCell(cell);
            dynPot.setPotential(cell, (double) newPotential);
        } else {
            newPotential = 1;
            dynPot.setPotential(cell, (double) newPotential);
        }
    }

    /**
     * Decreases the potential of the specified EvacCell about one if its dynamic potential is greater than zero.
     * Associates the specified potential with the specified EvacCell in this PotentialMap. The method throws
     * {@code IllegalArgumentExceptions} if you try to decrease the potential of a EvacCell that not exists in this
     * PotentialMap.
     *
     * @param cell A cell which potential you want to decrease.
     */
    public void decrease(EvacCellInterface cell) {
        if (!hasValidPotential(cell)) {
            throw new IllegalArgumentException(CellularAutomatonLocalization.LOC.getString("algo.ca.InsertCellPreviouslyException"));
        }

        if (getPotential(cell) == 1) {
            deleteCell(cell);
        } else {
            int newPotential = getPotential(cell) - 1;
            setPotential(cell, newPotential);
        }
    }

    @Override
    public Iterator<EvacCellInterface> iterator() {
        return potential.keySet().iterator();
    }
}
