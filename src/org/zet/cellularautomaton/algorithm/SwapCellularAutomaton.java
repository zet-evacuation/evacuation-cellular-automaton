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

import org.zet.cellularautomaton.algorithm.rule.AbstractMovementRule;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;
import org.zetool.common.util.Direction8;
import org.zet.cellularautomaton.Individual;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.zet.cellularautomaton.EvacCellInterface;

/**
 * By default swap cellular automaton are randomized.
 *
 * @see RandomOrdering
 * @author Jan-Philipp Kappmeier
 */
public class SwapCellularAutomaton extends EvacuationCellularAutomatonAlgorithm {

    public SwapCellularAutomaton() {
        super(new RandomOrdering());
    }

    public SwapCellularAutomaton(Function<List<Individual>, Iterator<Individual>> reorder) {
        super(reorder);
    }

    /**
     *
     */
    @Override
    protected void performStep() {
//	protected void executeStep() {
        //if( !isInitialized() ) {
        //	throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "algo.ca.NotInitializedException" ) );
        //}
        increaseStep();

        // Suche movement rule und setze auf not direct action
        setDirectExecute(false);

        AbstractMovementRule movement = null;
        // erster Lauf: bis zur movement-rule und dann anmelden lassen.
        ArrayList<Individual> unfinished = new ArrayList<>();
        HashMap<Individual, List<EvacCellInterface>> individualPossibleMapping = new HashMap<>();
        HashSet<Individual> individualSwapped = new HashSet<>();

        for (EvacCellInterface cell : this) {
            Iterator<EvacuationRule<?>> loop = getProblem().getRuleSet().loopIterator();

            while (loop.hasNext()) {
                EvacuationRule r = loop.next();
                r.execute(cell);
                if (r instanceof AbstractMovementRule) {
                    movement = (AbstractMovementRule) r;
                    break;
                }
            }

            // hier ist movementrule die aktuelle movement rule.
            if (movement.isMoveCompleted() && movement.executableOn(cell)) {
                unfinished.add(cell.getState().getIndividual());

                List<EvacCellInterface> possibleTargets = movement.getPossibleTargets();

                for (EvacCellInterface c : possibleTargets) {
                    try {
                        if (cell != c) {
                            Direction8 dir = cell.getRelative(c);

                        }
                    } catch (AssertionError e) {
                        System.out.println(e);
                        EvacCellInterface target2 = movement.selectTargetCell(cell, possibleTargets);
                        movement.getPossibleTargets();
                    }

                }

                individualPossibleMapping.put(cell.getState().getIndividual(), movement.getPossibleTargets());
                //movement.move( i, movement.selectTargetCell( es.propertyFor(i).getCell(), movement.getPossibleTargets() ) );
            } else {
                // perform the other rules because this individual has finished its movement for this round
                while (loop.hasNext()) {
                    EvacuationRule r = loop.next();
                    r.execute(cell);
                }
            }
        }

        setDirectExecute(true);

        ArrayList<Individual> unfinished2 = new ArrayList<>();

        // erster Lauf ist beendet. Versuche nun ob welche swappen können
        for (Individual i : unfinished) {
            if (individualSwapped.contains(i)) {
                continue;
            }
            List<EvacCellInterface> possibleTargets = individualPossibleMapping.get(i);
            EvacCellInterface target = movement.selectTargetCell(es.propertyFor(i).getCell(), possibleTargets);

            try {
                if (es.propertyFor(i).getCell() != target) {
                    Direction8 dir = es.propertyFor(i).getCell().getRelative(target);

                }
            } catch (AssertionError e) {
                System.out.println(e);
                EvacCellInterface target2 = movement.selectTargetCell(es.propertyFor(i).getCell(), possibleTargets);
            }

            if (target.getState().isEmpty()) {
                // Klappt alles
                movement.move(es.propertyFor(i).getCell(), target);
                //individualSwapped.add( i );
                unfinished2.add(i);
            } else {
                if (target.equals(es.propertyFor(i).getCell())) {
                    unfinished2.add(i);
                } else {
                    // steht ein individual drauf.
                    Individual i2 = target.getState().getIndividual();
                    if (individualSwapped.contains(i2)) {
                        unfinished2.add(i);
                    } else {
                        List<EvacCellInterface> possibleTargets2 = individualPossibleMapping.get(i2);
                        if (possibleTargets2 == null) {
                            // das andere individual hat wohl seinen weg ausgeführt!
                            unfinished2.add(i);
                        } else {
                            EvacCellInterface target2 = movement.selectTargetCell(es.propertyFor(i2).getCell(), possibleTargets2);
                            if (es.propertyFor(i).getCell().equals(target2) && es.propertyFor(i2).getCell().equals(target)) {
                                //if( util.DebugFlags.CA_SWAP )
                                System.out.println("SWAP Individual " + i.id() + " und Individual " + i2.id());
                                movement.swap(es.propertyFor(i).getCell(), es.propertyFor(i2).getCell());
                                individualSwapped.add(i2);
                                individualSwapped.add(i);
                                if (unfinished2.contains(i2)) {
                                    unfinished2.remove(i2); // perform last rules for them
                                }
                                Iterator<EvacuationRule<?>> loop = getProblem().getRuleSet().loopIterator();
                                boolean movementFound = false;
                                while (loop.hasNext()) {
                                    EvacuationRule r = loop.next();
                                    if (r instanceof AbstractMovementRule) {
                                        movementFound = true;
                                    } else if (movementFound) {
                                        r.execute(es.propertyFor(i).getCell());
                                    }
                                }
                                // perform last rules for them
                                loop = getProblem().getRuleSet().loopIterator();
                                movementFound = false;
                                while (loop.hasNext()) {
                                    EvacuationRule r = loop.next();
                                    if (r instanceof AbstractMovementRule) {
                                        movementFound = true;
                                    } else if (movementFound) {
                                        r.execute(es.propertyFor(i2).getCell());
                                    }
                                }
                            } else {
                                // perform movement rule a second time with direct execute on
                                unfinished2.add(i);
                            }
                        }
                    }
                }
            }
        }

        // Führe alle übrigen Individuals aus (Individuen, die nicht geswappt haben
        for (Individual i : unfinished2) {
            Iterator<EvacuationRule<?>> loop = getProblem().getRuleSet().loopIterator();
            boolean movementFound = false;
            while (loop.hasNext()) {
                EvacuationRule r = loop.next();
                if (r instanceof AbstractMovementRule) {
                    r.execute(es.propertyFor(i).getCell());
                    movementFound = true;
                } else if (movementFound) {
                    r.execute(es.propertyFor(i).getCell());
                }
            }
        }

        //setDirectExecute( true );
        es.removeMarkedIndividuals();
        ec.updateDynamicPotential(getProblem().getParameterSet().probabilityDynamicIncrease(),
                getProblem().getParameterSet().probabilityDynamicDecrease());
    }

    private void setDirectExecute(boolean val) {
        Iterator<EvacuationRule<?>> loop = getProblem().getRuleSet().loopIterator();
        while (loop.hasNext()) {
            EvacuationRule r = loop.next();
            if (r instanceof AbstractMovementRule) {
                ((AbstractMovementRule) r).setDirectExecute(val);
            }
        }
    }

    @Override
    public String toString() {
        return "SwapCellularAutomaton";
    }
}
