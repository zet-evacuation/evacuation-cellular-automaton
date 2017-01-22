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
package org.zet.cellularautomaton.algorithm.rule;

import java.util.Collections;
import java.util.List;
import org.zetool.common.util.Direction8;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.generators.GeneralRandom;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.Stairs;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.results.IndividualStateChangeAction;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @author Sylvie Temme
 */
public class SimpleMovementRule2 extends AbstractMovementRule {

    Individual individual;

    /**
     * Decides whether the rule can be applied to the current cell. Returns {@code true} if the cell is occupied by an
     * individual or {@code false} otherwise. Individuals standing on an exit cell do not move any more. This is
     * necessary, as the rule can take out individuals out of the simulation only, if their last step is finished. To
     * avoid problems of individuals moving forever, the movement rule should only be applied if an individual is not
     * already standing on an evacuation cell.
     *
     * @param cell the cell
     * @return true if the rule can be executed
     */
    @Override
    public boolean executableOn(EvacCellInterface cell) {
        return !(cell instanceof ExitCell) && !cell.getState().isEmpty();
    }

    @Override
    protected void onExecute(EvacCellInterface cell) {
        individual = cell.getState().getIndividual();
        if (es.propertyFor(individual).isAlarmed()) {
            if (canMove(individual)) {
                if (isDirectExecute()) { // we are in a "normal" simulation
                    EvacCellInterface targetCell = selectTargetCell(cell, computePossibleTargets(cell, true));
                    setMoveRuleCompleted(true);
                    move(cell, targetCell);
                } else { // only calculate possible movements, used for swap cellular automaton
                    computePossibleTargets(cell, false);
                    setMoveRuleCompleted(true);
                }
            } else { // Individual can't move, it is already moving
                setMoveRuleCompleted(false); // TODO why is here false?
            }
        } else { // Individual is not alarmed, that means it remains standing on the cell
            setMoveRuleCompleted(true);
            noMove();
        }

        recordAction(new IndividualStateChangeAction(individual, es));
    }

    @Override
    public void move(EvacCellInterface from, EvacCellInterface targetCell) {
        if (es.propertyFor(individual).getCell().equals(targetCell)) {
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals()
                    .addWaitedTimeToStatistic(individual, es.getTimeStep());
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForCells()
                    .addCellToWaitingStatistic(targetCell, es.getTimeStep());
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForCells()
                    .addCellToUtilizationStatistic(targetCell, es.getTimeStep());
            noMove();
        } else {
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForCells()
                    .addCellToUtilizationStatistic(targetCell, es.getTimeStep());
            initializeMove(from, targetCell);
            performMove(from, targetCell);
            setMoveRuleCompleted(false);
        }
    }

    /**
     * A function called if the individual is not moving. The individual will stand on the cell for exactly one time
     * step bevore it can move again. But, even if the individual does not move, the view direction may be changed.
     */
    protected void noMove() {
        es.propertyFor(individual).setStepStartTime(es.propertyFor(individual).getStepEndTime());
        setStepEndTime(individual, es.propertyFor(individual).getStepEndTime() + 1);
        ec.move(es.propertyFor(individual).getCell(), es.propertyFor(individual).getCell());

        es.propertyFor(individual).setDirection(getDirection());
        setMoveRuleCompleted(false);
        es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic(individual, es.getTimeStep(), 0);
    }

    /**
     * Computes a new viewing direction if the individual is not moving.
     *
     * @return
     */
    protected Direction8 getDirection() {
        Direction8 current = es.propertyFor(individual).getDirection();
        Direction8[] possible = {current.getClockwise().getClockwise(),
            current.getClockwise(),
            current,
            current.getCounterClockwise(),
            current.getCounterClockwise().getCounterClockwise()};
        Direction8 ret = current;
        int minDistance = Integer.MAX_VALUE;
        EvacCellInterface cell = es.propertyFor(individual).getCell();
        for (Direction8 dir : possible) {
            EvacCellInterface target = cell.getNeighbor(dir);
            if (target != null && !target.isOccupied()) {
                Potential staticPotential = es.propertyFor(individual).getStaticPotential();
                int cellDistance = staticPotential.getPotential(target);
                if (cellDistance < minDistance) {
                    minDistance = cellDistance;
                    ret = dir;
                }
            }
        }

        if (minDistance != Integer.MAX_VALUE) {
            return ret;
        }

        GeneralRandom rnd = (RandomUtils.getInstance()).getRandomGenerator();
        int randomDirection = rnd.nextInt(5);
        return possible[randomDirection];
    }

    /**
     * Performs an actual move of an individual (from its cell to another, different cell).
     *
     * @param ind
     * @param targetCell
     * @param performMove decides if the move is actually performed. If swapping is active, only values have to be
     * updated.
     */
    private void initializeMove(EvacCellInterface from, EvacCellInterface targetCell) {
        //Individual individual = from.getState().getIndividual();
        if (individual == null) {
            throw new IllegalStateException("No Individual on from cell " + from);
        }

        if (es.getCellularAutomaton().absoluteSpeed(es.propertyFor(individual).getRelativeSpeed()) < 0.0001) { // if individual moves, update times
            throw new IllegalStateException("Individuum has no speed.");
        }

        ec.increaseDynamicPotential(targetCell);

        if (from instanceof DoorCell && targetCell instanceof DoorCell) {
            speed = es.getCellularAutomaton().absoluteSpeed(es.propertyFor(individual).getRelativeSpeed());
            speed *= targetCell.getSpeedFactor() * 1;
            es.propertyFor(individual).setStepStartTime(Math.max(es.propertyFor(individual).getCell().getOccupiedUntil(), es.propertyFor(individual).getStepEndTime()));
            setStepEndTime(individual, es.propertyFor(individual).getStepEndTime() + (dist / speed) * es.getCellularAutomaton().getStepsPerSecond() + 0);
            es.propertyFor(individual).setDirection(es.propertyFor(individual).getDirection());

        } else {
            Direction8 direction = from.getRelative(targetCell);

            double stairSpeedFactor = targetCell instanceof Stairs ? ((Stairs) targetCell).getStairSpeedFactor(direction) * 1.1 : 1;
            dist = direction.distance() * 0.4; // calculate distance
            double add = getSwayDelay(individual, direction); // add a delay if the person is changing direction

            speed = es.getCellularAutomaton().absoluteSpeed(es.propertyFor(individual).getRelativeSpeed());
            double factor = targetCell.getSpeedFactor() * stairSpeedFactor;
            speed *= factor;
            es.propertyFor(individual).setStepStartTime(Math.max(from.getOccupiedUntil(), es.propertyFor(individual).getStepEndTime()));
            setStepEndTime(individual, es.propertyFor(individual).getStepEndTime() + (dist / speed) * es.getCellularAutomaton().getStepsPerSecond() + add * es.getCellularAutomaton().getStepsPerSecond());
            es.propertyFor(individual).setDirection(direction);
        }
    }

    /**
     * Performs a move after the parameters ({@code speed} and {@code dist}) have alredy been set by {@link #initializeMove(ds.ca.evac.Individual, ds.ca.evac.EvacCell)
     * }
     *
     * @param from
     * @param targetCell
     */
    protected void performMove(EvacCellInterface from, EvacCellInterface targetCell) {
        from.setOccupiedUntil(es.propertyFor(individual).getStepEndTime());
        ec.move(from, targetCell);
        es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic(individual, es.getTimeStep(), speed * es.getCellularAutomaton().getSecondsPerStep());
        es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCoveredDistanceToStatistic(individual, (int) Math.ceil(es.propertyFor(individual).getStepEndTime()), dist);
    }

    /**
     * Chooses the possible target cell with the smallest potential value.
     *
     * @param cell The starting cell
     * @return A neighbour of {@code cell} chosen at random.
     */
    @Override
    public EvacCellInterface selectTargetCell(EvacCellInterface cell, List<EvacCellInterface> targets) {
        Individual ind = cell.getState().getIndividual();
        EvacCellInterface target = cell;
        double minPot = c.effectivePotential(ind, cell, es::getDynamicPotential);
        for (EvacCellInterface targetCell : targets) {
            double pot = c.effectivePotential(individual, targetCell, es::getDynamicPotential);

            if (pot > minPot) {
                target = targetCell;
                minPot = pot;
            }
        }
        return target;
    }

    /**
     * Decides, if an individual can move in individual step. This is possible, when the last move was already finished at a
     * time earlier than this time step.
     *
     * @param individual An individual with a given parameterSet
     * @return {@code true} if the individual moves or {@code false} otherwise.
     */
    protected boolean canMove(Individual individual) {
        return es.getTimeStep() >= es.propertyFor(individual).getStepEndTime();
    }

    @Override
    public void swap(EvacCellInterface cell1, EvacCellInterface cell2) {
        if (cell1.getState().isEmpty()) {
            throw new IllegalArgumentException("No Individual standing on cell #1!");
        }
        if (cell2.getState().isEmpty()) {
            throw new IllegalArgumentException("No Individual standing on cell #2!");
        }
        if (cell1.equals(cell2)) {
            throw new IllegalArgumentException("The cells are equal. Can't swap on equal cells.");
        }
        individual = cell1.getState().getIndividual();
        initializeMove(cell1, cell2);
        individual = cell2.getState().getIndividual();
        initializeMove(cell2, cell1); // do not actually move!
        ec.swap(cell1, cell2);
    }

    /**
     * Selects the possible targets including the current cell.
     *
     * @param fromCell the current sell
     * @param onlyFreeNeighbours indicates whether only free neighbors or all neighbors are included
     * @return a list containing all neighbors and the from cell
     */
    @Override
    protected List<EvacCellInterface> computePossibleTargets(EvacCellInterface fromCell, boolean onlyFreeNeighbours) {
        List<EvacCellInterface> targets = super.computePossibleTargets(fromCell, onlyFreeNeighbours);
        targets.add(fromCell);
        return Collections.unmodifiableList(targets);
    }
}
