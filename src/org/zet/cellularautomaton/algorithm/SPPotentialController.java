/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
package org.zet.cellularautomaton.algorithm;

import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.generators.GeneralRandom;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.potential.DynamicPotential;
import org.zet.cellularautomaton.potential.PotentialManager;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.localization.CellularAutomatonLocalization;

/**
 * This class provides general functionality for manipulation of PotentialMaps.
 *
 * @author Matthias Woste
 *
 */
public class SPPotentialController implements PotentialController {

    /** Reference to the cellular automaton. */
    private EvacuationCellularAutomaton ca;

    /**Reference to a PotentialManager. */
    private PotentialManager pm;

    /**
     * Constructs an PotentialController instance for a given cellular automaton.
     *
     * @param ca EvacuationCellularAutomaton instance to work with
     */
    public SPPotentialController(EvacuationCellularAutomaton ca) {
        this.ca = ca;
        this.pm = ca.getPotentialManager();
    }

    /**
     * Returns the associated potential manager.
     *
     * @return associated potential manager
     */
    @Override
    public PotentialManager getPm() {
        return pm;
    }

    /**
     * This method updates the values stored in the dynamic potential in the following way. With the probability decay a
     * cell decreases its dynamic potential by one. Afterwards a cell with a dynamic potential greater than zero
     * increases the dynamic potential of one of its neighbour cells by one.
     *
     * @param diffusion The probability of increasing the dynamic potential of one neighbour cell of a cell with a
     * dynamic potential greater than zero by one.
     * @param decay The probability of decreasing the dynamic potential of a cell.
     */
    @Override
    public void updateDynamicPotential(double diffusion, double decay) {
        GeneralRandom rnd = RandomUtils.getInstance().getRandomGenerator();
        DynamicPotential dynPot = pm.getDynamicPotential();
        //ArrayList<Cell> diffusionCells = new ArrayList<Cell>();
        EvacCell[] cellsCopy = dynPot.getMappedCells().toArray(new EvacCell[dynPot.getMappedCells().size()]);
        /* NEW CODE */
        for (EvacCell c : cellsCopy) {
            //System.out.println( "DynPot: "+ dynPot.getPotential(c));
            double randomNumber = rnd.nextDouble();
//			System.out.println( "Randomnumber " + randomNumber + " in updateDynamicPotential" );
            if ( /*dynPot.getPotential(c) > 0 && */diffusion > randomNumber) {
				// Potential diffuses to a a neighbour cell. It should not increase, so
                // reduce it afterwards on this cell!
                EvacCell randomNeighbour = null;
                while (randomNeighbour == null) {
                    final int randomInt = rnd.nextInt((c.getNeighbours()).size());
                    randomNeighbour = (c.getNeighbours()).get(randomInt);
                }
                decreaseDynamicPotential(c);
                // test, if now potential is 0 so the potential in the diffused cell can decrease already in this step.
                randomNumber = rnd.nextDouble();
                if (!(dynPot.getPotential(c) == 0 && decay > randomNumber)) {
                    increaseDynamicPotential(randomNeighbour);
                }
            }
            randomNumber = rnd.nextDouble();
            if (dynPot.getPotential(c) > 0 && decay > randomNumber) {
                decreaseDynamicPotential(c);
            }
        }

    }

    /**
     * Increases the potential of the specified EvacCell about one. Associates the specified potential with the
     * specified EvacCell in this PotentialMap.
     *
     * @param cell A cell which potential you want to increase.
     */
    @Override
    public void increaseDynamicPotential(EvacCell cell) {
        int potential;
        DynamicPotential dynPot = pm.getDynamicPotential();
        if (dynPot.hasValidPotential(cell)) {
            potential = dynPot.getPotential(cell) + 1;
            dynPot.deleteCell(cell);
            dynPot.setPotential(cell, (double) potential);
        } else {
            potential = 1;
            dynPot.setPotential(cell, (double) potential);
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
    @Override
    public void decreaseDynamicPotential(EvacCell cell) throws IllegalArgumentException {
        DynamicPotential dynPot = pm.getDynamicPotential();
        if (!(dynPot.hasValidPotential(cell))) {
            throw new IllegalArgumentException(CellularAutomatonLocalization.LOC.getString("algo.ca.InsertCellPreviouslyException"));
        }

		//if(cell.getIndividual() != null){
        //    return;
        //}
        if (dynPot.getPotential(cell) == 1) {
            dynPot.deleteCell(cell);
        } else {
            int potential = dynPot.getPotential(cell) - 1;
            dynPot.setPotential(cell, potential);
        }
        /*if(dynPot.contains(cell)){
         if(dynPot.getPotential(cell) == 0){
         dynPot.deleteCell(cell);
         }
         }*/
    }

    /**
     * Returns a random StaticPotential
     *
     * @return random StaticPotential
     */
    @Override
    public StaticPotential getRandomStaticPotential() {
        GeneralRandom rnd = RandomUtils.getInstance().getRandomGenerator();
        return pm.getStaticPotential(rnd.nextInt(pm.getStaticPotentials().size()));
    }

    /**
     * Returns a StaticPotential which contains the lowest potential for the specified cell. If this cell is not in any
     * staticPotetial null is returned.
     *
     * @param c EvacCell for which the lowest potential is searched
     * @return StaticPotential that provides the fastest way out or null, if this cell is not mapped to any static
     * potential
     */
    @Override
    public StaticPotential getNearestExitStaticPotential(EvacCell c) {
        StaticPotential nearestPot = new StaticPotential();
        int distance = Integer.MAX_VALUE;
        int numberOfDisjunctStaticPotentials = 0;
        for (StaticPotential sP : pm.getStaticPotentials()) {
            if (sP.getPotential(c) == -1) {
                numberOfDisjunctStaticPotentials++;
            } else {
                if (sP.getPotential(c) < distance) {
                    nearestPot = sP;
                    distance = sP.getPotential(c);
                }
            }
        }
        return (numberOfDisjunctStaticPotentials == pm.getStaticPotentials().size() ? null : nearestPot);
    }

}
