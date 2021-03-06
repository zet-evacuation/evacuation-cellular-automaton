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
package org.zetool.algorithm.simulation.cellularautomaton;

import java.util.Iterator;
import org.zetool.algorithm.simulation.SimulationAlgorithm;
import org.zetool.simulation.cellularautomaton.Cell;
import org.zetool.simulation.cellularautomaton.CellularAutomaton;

/**
 * A general cellular automaton simulation algorithm that is specified by generic types for simulation problem,
 * simulation result and the type of cells used by the cellular automaton. The algorithms performs a general simulation
 * (with limited amount of steps) by calling the abstract
 * {@link #execute(de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.Cell) } method for each cell.
 * Implementing classes must implement this method to provide the actual simulation process. The iterator may be altered
 * if simulation is only necessary for, e.g. cells that are populated.
 *
 * @param <A> the automaton class
 * @param <P> the simulation problem class
 * @param <C> the cell type
 * @param <R> the result
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractCellularAutomatonSimulationAlgorithm<A extends CellularAutomaton<C>,
        C extends Cell<?>, P extends CellularAutomatonSimulationProblem<A, C>, R>
        extends SimulationAlgorithm<P, R> implements Iterable<C> {

    private int maxSteps = 300;

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    @Override
    protected double getProgress() {
        return (double) getStep() / getMaxSteps();
    }

    @Override
    protected abstract void initialize();

    /**
     * Performs one step of the simulation.
     */
    @Override
    protected void performStep() {
        for (C c : this) {
            execute(c);
        }
    }

    /**
     * Performs simulation of one step for a given cell.
     *
     * @param cell the cell
     */
    protected abstract void execute(C cell);

    /**
     * Performs the simulation until a break-condition is reached. The break condition can be a time limit (by means of
     * step count) or some other constraint that is decided by {@link #isFinished() }.
     */
    @Override
    protected void performSimulation() {
        // assume, initialization has been performed. simulation consists of continuous
        // calls of perform step, until a break-condition is reached.
        while (!isFinished()) {
            performStep();
        }
    }

    /**
     * A simple imlementation of the termination checker that checks the number of steps.
     *
     * @return {@code true} if simulation is over, and {@code false} otherwise
     */
    protected boolean isFinished() {
        return getStep() >= maxSteps;
    }

    @Override
    protected abstract R terminate();

    @Override
    public abstract Iterator<C> iterator();
}
