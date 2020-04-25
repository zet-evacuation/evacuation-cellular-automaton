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

import java.util.List;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.StairCell;
import org.zet.cellularautomaton.results.IndividualStateChangeAction;
import org.zet.cellularautomaton.results.MoveAction;
import org.zet.cellularautomaton.results.SwapAction;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Level;
import org.zetool.rndutils.RandomUtils;

/**
 *
 * @author Sylvie Temme
 */
public class BestResponseMovementRule extends AbstractMovementRule {

    private static final int TIME_STEP_LIMIT_FOR_NASH_EQUILIBRIUM = 25;

    public BestResponseMovementRule() {
    }

    /**
     * Decides whether the rule can be applied to the current cell. Returns {@code true} if the cell is occupied by an
     * individual or {@code false} otherwise.
     *
     * @param cell
     * @return true if the rule can be executed
     */
    @Override
    public boolean executableOn(EvacCellInterface cell) {
        return !cell.getState().isEmpty();
    }

    @Override
    protected MoveAction onExecute(EvacCellInterface cell) {
        Individual ind = cell.getState().getIndividual();

        if (canMove(ind)) {
            if (this.isDirectExecute()) {
                EvacCellInterface targetCell = this.selectTargetCell(cell, computePossibleTargets(cell, true));
                setMoveRuleCompleted(true);
                return move(cell, targetCell);
            } else {
                computePossibleTargets(cell, false);
                setMoveRuleCompleted(true);
                return MoveAction.NO_MOVE;
            }
        } else { // Individual can't move, it is already moving
            setMoveRuleCompleted(false);
            return null;
        }
    }

    @Override
    public MoveAction move(EvacCellInterface from, EvacCellInterface currentTargetCell) {
        EvacCellInterface targetCell = currentTargetCell;
        Individual ind = from.getState().getIndividual();
        if (es.propertyFor(ind).isSafe() && !((targetCell instanceof org.zet.cellularautomaton.SaveCell) || (targetCell instanceof org.zet.cellularautomaton.ExitCell))) // Rauslaufen aus sicheren Bereichen ist nicht erlaubt
        {
            targetCell = from;
        }
        if (es.propertyFor(ind).getCell().equals(targetCell)) {
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addWaitedTimeToStatistic(ind, es.getTimeStep());
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToWaitingStatistic(targetCell, es.getTimeStep());
        }
        //set statistic for targetCell and timestep
        es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToUtilizationStatistic(targetCell, es.getTimeStep());
        setMoveRuleCompleted(false);
        return this.doMove(ind, targetCell);
    }

    private MoveAction doMove(Individual i, EvacCellInterface targetCell) {
        if (es.propertyFor(i).getCell().equals(targetCell)) {
            setMoveRuleCompleted(false);
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic(i, es.getTimeStep(), 0);
            double oldStepEndTime = es.propertyFor(i).getStepEndTime();
            return new MoveAction(es.propertyFor(i).getCell(), targetCell, oldStepEndTime + 1, oldStepEndTime);
        }

        doMoveWithDecision(i, targetCell, true);
        setMoveRuleCompleted(false);
        return null;
    }

    private void doMoveWithDecision(Individual i, EvacCellInterface targetCell, boolean performMove) {
        // Calculate a factor that is later multiplied with the speed,
        // this factor is only != 1 for stair cells to
        // give different velocities for going a stair up or down.
        double stairSpeedFactor = 1;
        if (targetCell instanceof StairCell) {

            StairCell stairCell = (StairCell) targetCell;
            int x = targetCell.getX() - es.propertyFor(i).getCell().getX();
            int y = targetCell.getY() - es.propertyFor(i).getCell().getY();
            Direction8 direction = Direction8.getDirection(x, y);
            Level lvl = stairCell.getLevel(direction);
            if (lvl == Level.Higher) {
                stairSpeedFactor = stairCell.getSpeedFactorUp();
            } else if (lvl == Level.Lower) {
                stairSpeedFactor = stairCell.getSpeedFactorDown();
            }
        }

        // TODO check if this big stuff is really necessery! maybe easier!
        // calculate distance
        double dist;
        final double sqrt2 = Math.sqrt(2) * 0.4;
        if (!targetCell.getRoom().equals(es.propertyFor(i).getCell().getRoom())) {
            if (es.propertyFor(i).getCell().getX() + es.propertyFor(i).getCell().getRoom().getXOffset() == targetCell.getX() + targetCell.getRoom().getXOffset() && es.propertyFor(i).getCell().getY() + es.propertyFor(i).getCell().getRoom().getYOffset() == targetCell.getY() + targetCell.getRoom().getYOffset()) {
                System.err.println("SelfCell reached or Stockwerkwechsel!");
                dist = 0.4;
            } else if (es.propertyFor(i).getCell().getX() + es.propertyFor(i).getCell().getRoom().getXOffset() == targetCell.getX() + targetCell.getRoom().getXOffset() || es.propertyFor(i).getCell().getY() + es.propertyFor(i).getCell().getRoom().getYOffset() == targetCell.getY() + targetCell.getRoom().getYOffset()) {
                dist = 0.4;
            } else {
                dist = sqrt2;
            }
        } else if (es.propertyFor(i).getCell().getX() == targetCell.getX() && es.propertyFor(i).getCell().getY() == targetCell.getY()) {
            dist = 0;
        } else if (es.propertyFor(i).getCell().getX() == targetCell.getX() || es.propertyFor(i).getCell().getY() == targetCell.getY()) {
            dist = 0.4;
        } else {
            dist = sqrt2;
        }

        // Perform Movement if the individual changes the room!
        //if( es.propertyFor(i).getCell().getRoom() != targetCell.getRoom() )
        //	es.propertyFor(i).getCell().getRoom().moveIndividual( i.getCell(), targetCell );
        // update times
        if (sp.absoluteSpeed(es.propertyFor(i).getRelativeSpeed()) >= 0.0001) {
            double speed = sp.absoluteSpeed(es.propertyFor(i).getRelativeSpeed());
            speed *= targetCell.getSpeedFactor() * stairSpeedFactor;
            // zu diesem zeitpunkt ist die StepEndtime aktualisiert, falls ein individual vorher geslackt hat
            // oder sich nicht bewegen konnte.
            es.propertyFor(i).setStepStartTime(es.propertyFor(i).getStepEndTime());
            setStepEndTime(i, es.propertyFor(i).getStepEndTime() + (dist / speed) * sp.getStepsPerSecond());
            if (performMove) {
                //ec.move(es.propertyFor(i).getCell(), targetCell);
                es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic(i, es.getTimeStep(), speed * sp.getSecondsPerStep());
                es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCoveredDistanceToStatistic(i, (int) Math.ceil(es.propertyFor(i).getStepEndTime()), dist);
            }
        } else {
            throw new IllegalStateException("Individuum has no speed.");
        }
    }

    /**
     * Given a starting cell, this method picks one of its reachable neighbours at random. The i-th neighbour is chosen
     * with probability {@code p(i) := N * exp[mergePotentials(i, cell)]} where N is a constant used for normalisation.
     *
     * @param cell The starting cell
     * @return A neighbour of {@code cell} chosen at random.
     */
    @Override
    public EvacCellInterface selectTargetCell(EvacCellInterface cell, List<EvacCellInterface> targets) {
        Individual ind = cell.getState().getIndividual();
        if (targets.isEmpty()) {
            return cell;
        }

        double[] p = new double[targets.size()];

        for (int i = 0; i < targets.size(); i++) {
            p[i] = Math.exp(c.effectivePotential(ind, targets.get(i), es::getDynamicPotential));
        }

        int number = RandomUtils.getInstance().chooseRandomlyAbsolute(p);
        return targets.get(number);
    }

    /**
     * Decides randomly if an individual moves. (falsch)
     *
     * @param i An individual with a given parameters
     * @return {@code true} if the individual moves or {@code false} otherwise.
     */
    //gibt true wieder, wenn geschwindigkeit von zelle und individuel (wkeit darueber) bewegung bedeuten
    @Override
    protected boolean canMove(Individual i) {
        return es.getTimeStep() >= es.propertyFor(i).getStepEndTime();
    }

    @Override
    public SwapAction swap(EvacCellInterface cell1, EvacCellInterface cell2) {
        if (cell1.getState().isEmpty()) {
            throw new IllegalArgumentException("No Individual standing on cell #1!");
        }
        if (cell2.getState().isEmpty()) {
            throw new IllegalArgumentException("No Individual standing on cell #2!");
        }
        if (cell1.equals(cell2)) {
            throw new IllegalArgumentException("The cells are equal. Can't swap on equal cells.");
        }
        doMoveWithDecision(cell1.getState().getIndividual(), cell2, false);
        doMoveWithDecision(cell2.getState().getIndividual(), cell1, false);
        return new SwapAction(cell1, cell2, es);
    }

    /**
     * Selects the possible targets including the current cell.
     *
     * @param fromCell the current sell
     * @param onlyFreeNeighbours indicates whether only free neighbours or all neighbours are included
     * @return a list containing all neighbours and the from cell
     */
    @Override
    protected List<EvacCellInterface> computePossibleTargets(EvacCellInterface fromCell, boolean onlyFreeNeighbours) {
        List<EvacCellInterface> targets = super.computePossibleTargets(fromCell, onlyFreeNeighbours);
        targets.add(fromCell);
        return targets;
    }
}
