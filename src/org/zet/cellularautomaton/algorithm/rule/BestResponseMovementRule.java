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
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.StairCell;
import org.zet.cellularautomaton.results.IndividualStateChangeAction;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Level;
import org.zetool.rndutils.RandomUtils;

/**
 *
 * @author Sylvie
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
    public boolean executableOn(org.zet.cellularautomaton.EvacCell cell) {
        return !cell.getState().isEmpty();
    }

    @Override
    protected void onExecute(org.zet.cellularautomaton.EvacCell cell) {
        Individual ind = cell.getState().getIndividual();

        if (canMove(ind)) {
            if (this.isDirectExecute()) {
                EvacCell targetCell = this.selectTargetCell(cell, computePossibleTargets(cell, true));
                setMoveRuleCompleted(true);
                move(cell, targetCell);
            } else {
                computePossibleTargets(cell, false);
                setMoveRuleCompleted(true);
            }
        } else // Individual can't move, it is already moving
        {
            setMoveRuleCompleted(false);
        }
        recordAction(new IndividualStateChangeAction(ind, es));
    }

    @Override
    public void move(EvacCell from, EvacCell targetCell) {
        Individual ind = from.getState().getIndividual();
        //public void move( EvacCell targetCell ) {
        if (es.getIndividualState().isSafe(ind) && !((targetCell instanceof org.zet.cellularautomaton.SaveCell) || (targetCell instanceof org.zet.cellularautomaton.ExitCell))) // Rauslaufen aus sicheren Bereichen ist nicht erlaubt
        {
            targetCell = from;
        }
        if (es.propertyFor(ind).getCell().equals(targetCell)) {
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addWaitedTimeToStatistic(ind, es.getTimeStep());
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToWaitingStatistic(targetCell, es.getTimeStep());
        }
        //set statistic for targetCell and timestep
        es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToUtilizationStatistic(targetCell, es.getTimeStep());
        this.doMove(ind, targetCell);
        setMoveRuleCompleted(false);
    }

    private void doMove(Individual i, EvacCell targetCell) {
        if (es.propertyFor(i).getCell().equals(targetCell)) {
            es.propertyFor(i).setStepStartTime(es.propertyFor(i).getStepEndTime());
            setStepEndTime(i, es.propertyFor(i).getStepEndTime() + 1);
            //i.setStepEndTime( i.getStepEndTime() + 1 );
            //es.propertyFor(i).getCell().getRoom().moveIndividual( targetCell, targetCell );
            es.moveIndividual(es.propertyFor(i).getCell(), targetCell);
            setMoveRuleCompleted(false);
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic(i, es.getTimeStep(), 0);
            return;
        }

        doMoveWithDecision(i, targetCell, true);
        setMoveRuleCompleted(false);
    }

    private void doMoveWithDecision(Individual i, EvacCell targetCell, boolean performMove) {
        es.increaseDynamicPotential(targetCell);
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
            } else if (es.propertyFor(i).getCell().getX() + es.propertyFor(i).getCell().getRoom().getXOffset() == targetCell.getX() + targetCell.getRoom().getXOffset() | es.propertyFor(i).getCell().getY() + es.propertyFor(i).getCell().getRoom().getYOffset() == targetCell.getY() + targetCell.getRoom().getYOffset()) {
                dist = 0.4;
            } else {
                dist = sqrt2;
            }
        } else if (es.propertyFor(i).getCell().getX() == targetCell.getX() && es.propertyFor(i).getCell().getY() == targetCell.getY()) {
            dist = 0;
        } else if (es.propertyFor(i).getCell().getX() == targetCell.getX() | es.propertyFor(i).getCell().getY() == targetCell.getY()) {
            dist = 0.4;
        } else {
            dist = sqrt2;
        }

        // Perform Movement if the individual changes the room!
        //if( es.propertyFor(i).getCell().getRoom() != targetCell.getRoom() )
        //	es.propertyFor(i).getCell().getRoom().moveIndividual( i.getCell(), targetCell );
        // update times
        if (es.getCellularAutomaton().absoluteSpeed(es.propertyFor(i).getRelativeSpeed()) >= 0.0001) {
            double speed = es.getCellularAutomaton().absoluteSpeed(es.propertyFor(i).getRelativeSpeed());
            speed *= targetCell.getSpeedFactor() * stairSpeedFactor;
            //System.out.println( "Speed ist " + speed );
            // zu diesem zeitpunkt ist die StepEndtime aktualisiert, falls ein individual vorher geslackt hat
            // oder sich nicht bewegen konnte.
            es.propertyFor(i).setStepStartTime(es.propertyFor(i).getStepEndTime());
            setStepEndTime(i, es.propertyFor(i).getStepEndTime() + (dist / speed) * es.getCellularAutomaton().getStepsPerSecond());
            if (performMove) {
                //es.propertyFor(i).getCell().getRoom().moveIndividual( i.getCell(), targetCell );
                es.moveIndividual(es.propertyFor(i).getCell(), targetCell);
                es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic(i, es.getTimeStep(), speed * es.getCellularAutomaton().getSecondsPerStep());
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
    public EvacCell selectTargetCell(EvacCell cell, List<EvacCell> targets) {
        if (targets.isEmpty()) {
            return cell;
        }

        double p[] = new double[targets.size()];

        for (int i = 0; i < targets.size(); i++) {
            p[i] = Math.exp(es.getParameterSet().effectivePotential(cell, targets.get(i), es.getCellularAutomaton().getDynamicPotential()));
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
    protected boolean canMove(Individual i) {
        if (es.getTimeStep() >= es.propertyFor(i).getStepEndTime()) {
            return true;
        }
        return false;
    }

    @Override
    public void swap(EvacCell cell1, EvacCell cell2) {
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
        //cell1.getRoom().swapIndividuals( cell1, cell2 );
        es.swapIndividuals(cell1, cell2);
    }

    /**
     * Selects the possible targets including the current cell.
     *
     * @param fromCell the current sell
     * @param onlyFreeNeighbours indicates whether only free neighbours or all neighbours are included
     * @return a list containing all neighbours and the from cell
     */
    @Override
    protected List<EvacCell> computePossibleTargets(EvacCell fromCell, boolean onlyFreeNeighbours) {
        List<EvacCell> targets = super.computePossibleTargets(fromCell, onlyFreeNeighbours);
        targets.add(fromCell);
        return targets;
    }
}
