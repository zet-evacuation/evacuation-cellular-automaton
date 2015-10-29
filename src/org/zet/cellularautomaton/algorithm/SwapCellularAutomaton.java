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
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SwapCellularAutomaton extends EvacuationCellularAutomatonRandom {

    public SwapCellularAutomaton() {
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
        getProblem().getCa().nextTimeStep();

        // Suche movement rule und setze auf not direct action
        setDirectExecute(false);

        AbstractMovementRule movement = null;
        // erster Lauf: bis zur movement-rule und dann anmelden lassen.
        ArrayList<Individual> unfinished = new ArrayList<>();
        HashMap<Individual, List<EvacCell>> individualPossibleMapping = new HashMap<>();
        HashSet<Individual> individualSwapped = new HashSet<>();

        for (Individual i : getIndividuals()) {
            Iterator<EvacuationRule> loop = getProblem().getRuleSet().loopIterator();

            if (i.id() == 114 && i.getCell().getAbsoluteX() == 2) {
                System.out.println("114");
            }

            while (loop.hasNext()) {
                EvacuationRule r = loop.next();
                r.execute(i.getCell());
                if (r instanceof AbstractMovementRule) {
                    movement = (AbstractMovementRule) r;
                    break;
                }
            }

            // hier ist movementrule die aktuelle movement rule.
            if (movement.isMoveCompleted() && movement.executableOn(i.getCell())) {
                unfinished.add(i);

                List<EvacCell> possibleTargets = movement.getPossibleTargets();

                for (EvacCell c : possibleTargets) {
                    try {
                        if (i.getCell() != c) {
                            Direction8 dir = i.getCell().getRelative(c);

                        }
                    } catch (AssertionError e) {
                        System.out.println(e);
                        EvacCell target2 = movement.selectTargetCell(i.getCell(), possibleTargets);
                        movement.getPossibleTargets();
                    }

                }

                individualPossibleMapping.put(i, movement.getPossibleTargets());
                //movement.move( i, movement.selectTargetCell( i.getCell(), movement.getPossibleTargets() ) );
            } else {
                // perform the other rules because this individual has finished its movement for this round
                while (loop.hasNext()) {
                    EvacuationRule r = loop.next();
                    r.execute(i.getCell());
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
            List<EvacCell> possibleTargets = individualPossibleMapping.get(i);
            EvacCell target = movement.selectTargetCell(i.getCell(), possibleTargets);

            try {
                if (i.getCell() != target) {
                    Direction8 dir = i.getCell().getRelative(target);

                }
            } catch (AssertionError e) {
                System.out.println(e);
                EvacCell target2 = movement.selectTargetCell(i.getCell(), possibleTargets);
            }

            if (target.getIndividual() == null) {
                // Klappt alles
                movement.move(i.getCell(), target);
                //individualSwapped.add( i );
                unfinished2.add(i);
            } else {
                if (target.equals(i.getCell())) {
                    unfinished2.add(i);
                } else {
                    // steht ein individual drauf.
                    Individual i2 = target.getIndividual();
                    if (individualSwapped.contains(i2)) {
                        unfinished2.add(i);
                    } else {
                        List<EvacCell> possibleTargets2 = individualPossibleMapping.get(i2);
                        if (possibleTargets2 == null) {
                            // das andere individual hat wohl seinen weg ausgeführt!
                            unfinished2.add(i);
                        } else {
                            EvacCell target2 = movement.selectTargetCell(i2.getCell(), possibleTargets2);
                            if (i.getCell().equals(target2) && i2.getCell().equals(target)) {
                                //if( util.DebugFlags.CA_SWAP )
                                System.out.println("SWAP Individual " + i.id() + " und Individual " + i2.id());
                                movement.swap(i.getCell(), i2.getCell());
                                individualSwapped.add(i2);
                                individualSwapped.add(i);
                                if (unfinished2.contains(i2)) {
                                    unfinished2.remove(i2); // perform last rules for them
                                }
                                Iterator<EvacuationRule> loop = getProblem().getRuleSet().loopIterator();
                                boolean movementFound = false;
                                while (loop.hasNext()) {
                                    EvacuationRule r = loop.next();
                                    if (r instanceof AbstractMovementRule) {
                                        movementFound = true;
                                    } else if (movementFound) {
                                        r.execute(i.getCell());
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
                                        r.execute(i2.getCell());
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
            Iterator<EvacuationRule> loop = getProblem().getRuleSet().loopIterator();
            boolean movementFound = false;
            while (loop.hasNext()) {
                EvacuationRule r = loop.next();
                if (r instanceof AbstractMovementRule) {
                    r.execute(i.getCell());
                    movementFound = true;
                } else if (movementFound) {
                    r.execute(i.getCell());
                }
            }
        }

		//setDirectExecute( true );
        getProblem().getCa().removeMarkedIndividuals();
        getProblem().getPotentialController().updateDynamicPotential(getProblem().getParameterSet().probabilityDynamicIncrease(),
                getProblem().getParameterSet().probabilityDynamicDecrease());
    }

    private void setDirectExecute(boolean val) {
        Iterator<EvacuationRule> loop = getProblem().getRuleSet().loopIterator();
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