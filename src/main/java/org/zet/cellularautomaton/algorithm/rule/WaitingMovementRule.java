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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.zetool.common.util.Direction8;
import org.zetool.rndutils.RandomUtils;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.algorithm.state.PropertyUpdate;
import org.zet.cellularautomaton.results.MoveAction;

/**
 * Waiting movement rule that allows individuals to wait when they are not able to walk in a direction nearer to an
 * exit. When all good targets are occupied the individual can remain standing on its position. When an individual moves ore remains, the
 * exhaustion is increased and decreased, respectively. When an individual is not able to move the panic is updated.
 * @author Daniel R. Schmidt
 */
public class WaitingMovementRule extends SimpleMovementRule2 {

    private static final double DIRECTION_BOOST_FACTOR = 10.5;

    /**
     * Decides randomly if an individual idles.
     *
     * @param i An individual with a given slackness
     * @return {@code true} with a probability of slackness or {@code false} otherwise.
     */
    @Override
    boolean wishToMove() {
        double randomNumber = RandomUtils.getInstance().getRandomGenerator().nextDouble();
        return (c.idleThreshold(individual) <= randomNumber);
    }

    @Override
    protected MoveAction noMove(EvacCellInterface cell) {
        MoveAction a = super.noMove(cell);

        if (es.propertyFor(individual).isAlarmed()) {
            double newExhaustion = updateExhaustion(individual, cell);
            PropertyUpdate update = PropertyUpdate.extend(a.getPropertyUpdate()).withExhaustion(newExhaustion).createUpdate();
            return new MoveAction(a, update);
        }
        return a;
    }

    @Override
    public MoveAction move(EvacCellInterface from, EvacCellInterface targetCell) {
        Individual ind = from.getState().getIndividual();
        double newPanic = updatePanic(ind, targetCell);
        double newExhaustion = updateExhaustion(ind, targetCell);
        MoveAction a = super.move(from, targetCell);
        PropertyUpdate update = PropertyUpdate.extend(a.getPropertyUpdate()).withPanic(newPanic).withExhaustion(newExhaustion).createUpdate();
        return new MoveAction(a, update);
    }

    protected double updatePanic(Individual individual, EvacCellInterface targetCell) {
        double oldPanic = es.propertyFor(individual).getPanic();
        double newPanic = c.updatePanic(individual, targetCell, this.neighboursByPriority(es.propertyFor(individual).getCell()));
        if (oldPanic != newPanic) {
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addPanicToStatistic(individual, es.getTimeStep(), es.propertyFor(individual).getPanic());
        }
        return newPanic;
    }

    protected double updateSpeed(Individual i) {
        return c.updatePreferredSpeed(i);
    }

    /**
     * Returns all reachable neighbours sorted according to their priority which is calculated by
     * mergePotential(). The first element in the list is the most probable neighbour, the last
     * element is the least probable neighbour.
     *
     * @param cell The cell whose neighbours are to be sorted
     * @return A sorted list of the neighbour cells of {@code cell}, sorted in an increasing fashion
     * according to their potential computed by {@code mergePotential}.
     */
    protected ArrayList<EvacCellInterface> neighboursByPriority(EvacCellInterface cell) {
        class CellPrioritySorter implements Comparator<EvacCellInterface> {

            final EvacCellInterface referenceCell;

            CellPrioritySorter(EvacCellInterface referenceCell) {
                this.referenceCell = referenceCell;
            }

            @Override
            public int compare(EvacCellInterface cell1, EvacCellInterface cell2) {
                Individual ind = referenceCell.getState().getIndividual();

                final double potential1 = c.effectivePotential(ind, cell1, es::getDynamicPotential);
                final double potential2 = c.effectivePotential(ind, cell2, es::getDynamicPotential);
                if (potential1 < potential2) {
                    return -1;
                } else if (potential1 == potential2) {
                    return 0;
                } else {
                    return 1;
                }
            }
        }

        ArrayList<EvacCellInterface> result = new ArrayList<>(cell.getNeighbours());
        Collections.sort(result, new CellPrioritySorter(cell));
        return result;
    }

    /**
     * Given a starting cell, this method picks one of its reachable neighbors at random. The
     * {@literal i}th neighbor is chosen with probability
     * {@code p(i) := N * exp[mergePotentials(i, cell)]} where {@literal N} is a constant used for
     * normalization.
     *
     * @param cell The starting cell
     * @return A neighbor of {@code cell} chosen at random.
     */
    @Override
    public EvacCellInterface selectTargetCell(EvacCellInterface cell, List<EvacCellInterface> targets) {
        if (targets.isEmpty()) {
            return cell;
        }

        double p[] = new double[targets.size()];

        double max = Integer.MIN_VALUE;
        int max_index = 0;

        Individual ind = cell.getState().getIndividual();
        for (int i = 0; i < targets.size(); i++) {
            p[i] = Math.exp(c.effectivePotential(ind, targets.get(i), es::getDynamicPotential));
            if (p[i] > max) {
                max = p[i];
                max_index = i;
            }
        }

        // raising probablities only makes sense if the cell and all its neighbours are in the same room
        boolean inSameRoom = true;
        for (int i = 0; i < targets.size(); i++) {
            if (!(cell.getRoom().equals(targets.get(i).getRoom()))) {
                inSameRoom = false;
                break;
            }
        }
        if (inSameRoom) {
            EvacCellInterface mostProbableTarget = targets.get(max_index);

            Direction8 oldDir = es.propertyFor(individual).getDirection();
            Direction8 newDir = cell.equals(mostProbableTarget) ? oldDir : cell.getRelative(mostProbableTarget);

            if (oldDir.equals(newDir)) {
                // No swaying
            } else {
                // swaying!
                // check, if one of the targets is in the old direction

                for (int j = 0; j < targets.size(); ++j) {
                    EvacCellInterface target = targets.get(j);
                    if (target != cell && oldDir.equals(cell.getRelative(target))) {
                        // We found a cell in the current direciton
                        p[j] = p[j] * DIRECTION_BOOST_FACTOR;

                    }
                }
            }
        }// end if inSameRoom

        int number = RandomUtils.getInstance().chooseRandomlyAbsolute(p);
        return targets.get(number);
    }

    /**
     * Updates the exhaustion for the individual and updates the statistic.
     *
     * @param individual
     * @param targetCell
     * @return 
     */
    protected double updateExhaustion(Individual individual, EvacCellInterface targetCell) {
        double oldExhaustion = es.propertyFor(individual).getExhaustion();
        double newExhaustion = c.updateExhaustion(individual, targetCell);
        if (oldExhaustion != newExhaustion) {
            es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExhaustionToStatistic(individual, es.getTimeStep(), newExhaustion);
        }
        return newExhaustion;
    }

}
