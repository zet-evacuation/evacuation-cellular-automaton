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
package org.zet.cellularautomaton.results;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import static org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton.getCellCount;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.algorithm.EvacuationCellularAutomatonAlgorithm;
import org.zet.cellularautomaton.algorithm.EvacuationInitializationCompleteEvent;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.EvacuationStepCompleteEvent;
import static org.zet.cellularautomaton.algorithm.TestEvacuationCellularAutomatonRun.getSmallExampleAutomaton;
import static org.zet.cellularautomaton.algorithm.TestEvacuationCellularAutomatonRun.getSmallProblem;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateController;
import org.zet.cellularautomaton.algorithm.state.MutableEvacuationState;
import org.zetool.common.algorithm.AbstractAlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ReplayTest {

    @Test
    public void replay() throws InconsistentPlaybackStateException {
        MultiFloorEvacuationCellularAutomaton eca = getSmallExampleAutomaton();
        EvacuationCellularAutomatonAlgorithm caAlgorithm = new EvacuationCellularAutomatonAlgorithm();
        EvacuationSimulationProblem esp = getSmallProblem(eca);

        caAlgorithm.setProblem(esp);

        List<Action> allActions = new LinkedList<>();

        AlgorithmListener listener = new AlgorithmListener() {
            @Override
            public void eventOccurred(AbstractAlgorithmEvent event) {

                System.out.println(caAlgorithm.getEvacuationState().getTimeStep() + ": Event: " + event);
                if (event instanceof EvacuationStepCompleteEvent) {

                    allActions.addAll(((EvacuationStepCompleteEvent) event).getInitializationActions());
                } else if (event instanceof EvacuationInitializationCompleteEvent) {
                    allActions.addAll(((EvacuationInitializationCompleteEvent) event).getInitializationActions());
                }
            }
        };

        caAlgorithm.addAlgorithmListener(listener);
        caAlgorithm.runAlgorithm();

        MultiFloorEvacuationCellularAutomaton otherAutomaton = getSmallExampleAutomaton();

        Map<EvacCellInterface, EvacCellInterface> selfMap = new HashMap<>(getCellCount(eca));
        for (Room r : otherAutomaton.getRooms()) {
            for (EvacCellInterface cell : r.getAllCells()) {
                selfMap.put(cell, cell);
            }
        }

        EvacuationSimulationProblem otherProblem = getSmallProblem(otherAutomaton);
        MutableEvacuationState es = new MutableEvacuationState(new MultiFloorEvacuationCellularAutomaton(), otherProblem.getIndividuals());
        for (Map.Entry<Individual, ? extends EvacCellInterface> e : otherProblem.individualStartPositions().entrySet()) {
            es.propertyFor(e.getKey()).setCell(e.getValue());
            es.propertyFor(e.getKey()).setStaticPotential(otherAutomaton.minPotentialFor(e.getValue()));
            e.getValue().getState().setIndividual(e.getKey());
            e.getValue().getRoom().addIndividual(e.getValue(), e.getKey());

        }
        EvacuationStateController ec = new EvacuationStateController((MutableEvacuationState) es);

        System.out.println(otherAutomaton.graphicalToString());
        for (Action a : allActions) {
            a.adoptToCA(selfMap);
            System.out.println("Replaying: " + a);
            a.execute(es, ec);
            a.executeDelayed(es, ec);
            System.out.println(otherAutomaton.graphicalToString());

        }
    }
}
