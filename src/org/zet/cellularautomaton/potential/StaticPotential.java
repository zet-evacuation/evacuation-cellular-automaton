/* zet evacuation tool copyright (c) 2007-15 zet evacuation team
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
import java.util.List;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;

/**
 * A StaticPotential is special type of {@link AbstractPotential}, of which exist several in one {@link PotentialManager}.
 * Therefore it has a unique ID. The {@code StaticPotential} consists of two potentials. Both describe the distance to
 * an {@link ExitCell}. But the first one represents this distance with an smoothly calculated value while the other
 * one contains the exact distance.
 */
public class StaticPotential extends AbstractPotential {

    protected String name = "DefaultNameForStaticPotential";

    /** Counts the number of existing StaticPotentials. Every new StaticPotential gets automatically a unique ID. */
    protected static int idCount = 0;

    /** Attractivity for this potential. */
    private int attractivity;

    /** Id of the StaticPotential. */
    protected int id;

    /** contains the associated ExitCells. */
    private List<ExitCell> associatedExitCells;

    /**
     * Creates a StaticPotential with a automatic generated unique ID, that can not be changed.
     */
    public StaticPotential() {
        super();
        this.id = idCount;
        idCount++;
        associatedExitCells = new ArrayList<>();
    }
    
    /**
     * Get the ID of this StaticPotential.
     *
     * @return ID of this StaticPotential
     */
    public int getID() {
        return id;
    }

    public int getAttractivity() {
        return attractivity;
    }

    public void setAttractivity(int attractivity) {
        this.attractivity = attractivity;
    }

    /**
     * Stores the specified distance for an {@link EvacCell} in this {@code StaticPotential}. If an {@link EvacCell}
     * is specified that already exists, the value will be overwritten.
     *
     * @param cell cell which has to be updated or mapped
     * @param i distance of the cell
     */
    public void setDistance(EvacCell cell, double i) {
        setPotential(cell, i);
    }

    /**
     * Gets the distance of a specified EvacCell. The method returns -1 if you try to get the distance of a cell that
     * does not exists.
     *
     * @param cell A cell which distance you want to know.
     * @return distance of the specified cell or -1 if the cell is not mapped by this potential
     */
    public double getDistance(EvacCell cell) {
        return hasValidPotential(cell) ? getPotentialDouble(cell) : -1;
    }

    public double getMaxDistance() {
        return Math.max(0, getMaxPotentialDouble());
    }

    /**
     * Removes the mapping for the specified EvacCell. The method throws {@code IllegalArgumentExceptions} if you try to
     * remove the mapping of a EvacCell that does not exists.
     *
     * @param cell an {@link EvacCell} that mapping you want to remove.
     * @throws IllegalArgumentException if no distance was stored for cell
     */
    public void deleteDistanceCell(EvacCell cell) {
        deleteCell(cell);
    }

    /**
     * Returns {@code true} if the mapping for the specified {@link EvacCell} exists.
     *
     * @param cell an {@link EvacCell} of that you want to know if it exists.
     * @return {@code true} if the distance has been defined, {@code false} otherwise
     */
    public boolean containsDistance(EvacCell cell) {
        return hasValidPotential(cell);
    }

    public List<ExitCell> getAssociatedExitCells() {
        return associatedExitCells;
    }

    public void setAssociatedExitCells(List<ExitCell> associatedExitCells) {
        this.associatedExitCells = associatedExitCells;
        setName(associatedExitCells.get(0).getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTruePotential(EvacCell cell) {
        return getPotential(cell);
    }

    public EvacPotential getAsEvacPotential(Individual i, CellularAutomatonDirectionChecker checker) {
        EvacPotential evacPotential = new EvacPotential(i, checker);
        for( EvacCell c : getMappedCells()) {
            evacPotential.setPotential(c, getPotential(c));
        }
        evacPotential.setAssociatedExitCells(this.associatedExitCells);
        evacPotential.setAttractivity(this.attractivity);
        evacPotential.potential = this.potential;
        evacPotential.setName(this.name);
        evacPotential.id = this.id;
        return evacPotential;
    }

}
