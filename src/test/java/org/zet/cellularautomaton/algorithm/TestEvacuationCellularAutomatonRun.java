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
package org.zet.cellularautomaton.algorithm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.InitialConfiguration;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton.EvacuationCellularAutomatonBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.RoomImpl;
import org.zet.cellularautomaton.algorithm.rule.EvacuateIndividualsRule;
import org.zet.cellularautomaton.algorithm.rule.SimpleMovementRule;
import org.zet.cellularautomaton.potential.PotentialAlgorithm;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.common.algorithm.AbstractAlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;

/**
 * Sets up a simple evacuation scenario and runs the algorithm.
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuationCellularAutomatonRun {

    private static class TestEvacuationRuleSet extends EvacuationRuleSet {

        public TestEvacuationRuleSet() {
            this.add(new SimpleMovementRule(), false, true);
            this.add(new EvacuateIndividualsRule());
        }
    }

    private static ExitCell exit;
    private static RoomCell middleCell;
    private static RoomCell rightCell;

    public static MultiFloorEvacuationCellularAutomaton getSmallExampleAutomaton() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();

        builder.addFloor(0, "floor");

        RoomImpl r = new RoomImpl(3, 1, 0, 0, 0);

        exit = new ExitCell(1, 0, 0);
        r.setCell(exit);
        middleCell = new RoomCell(1, 1, 0, r);
        r.setCell(middleCell);
        rightCell = new RoomCell(1, 2, 0, r);
        r.setCell(rightCell);
        Collection<Exit> newExits = builder.addRoom(r);

        Iterator<Exit> exitIteraterator = newExits.iterator();
        Exit e = exitIteraterator.next();
        Collection<ExitCell> cells = e.getExitCluster();
        PotentialAlgorithm pa = new PotentialAlgorithm();
        pa.setProblem(cells);
        StaticPotential sp = pa.call();
        builder.setPotentialFor(e, sp);

        // TODO: set emtpy potential as default in the builder
        builder.setSafePotential(new StaticPotential());

        // Bestimme die angrenzenden Save-Cells
        //saveCellSearch(cells, sp);
        return builder.build();
    }

    public static EvacuationSimulationProblem getSmallProblem(MultiFloorEvacuationCellularAutomaton eca) {
        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);

        String ruleSet = "TestEvacuationCellularAutomatonRun$TestEvacuationRuleSet";
        try {
            ds.PropertyContainer.getGlobal().define("algo.ca.ruleSet", String.class, ruleSet);
            ds.PropertyContainer.getGlobal().define("algo.ca.parameterSet", String.class, "SimpleParameterSet");
        } catch (IllegalArgumentException ex) {
            // called twice. ok.
        }

        Map<Individual, EvacCellInterface> individualStartPositions = new HashMap<>();
        individualStartPositions.put(i, rightCell);

        InitialConfiguration ic = new InitialConfiguration(eca, Collections.singletonList(i), individualStartPositions);
        return new EvacuationSimulationProblemImpl(ic);
    }

    @Test
    public void run() {
        MultiFloorEvacuationCellularAutomaton eca = getSmallExampleAutomaton();

        List<Exit> exitClusters = eca.getExits();
        assertThat(exitClusters, hasSize(1));
        Exit exitCluster = exitClusters.get(0);
        assertThat(exitCluster.getExitCluster(), hasSize(1));
        assertThat(exitCluster.getExitCluster(), contains(exit));

        EvacuationCellularAutomatonAlgorithm caAlgorithm = new EvacuationCellularAutomatonAlgorithm();

        EvacuationSimulationProblem esp = getSmallProblem(eca);

        caAlgorithm.setProblem(esp);

        AlgorithmListener listener = new AlgorithmListener() {
            @Override
            public void eventOccurred(AbstractAlgorithmEvent event) {

                System.out.println(caAlgorithm.getEvacuationState().getTimeStep() + ": Event: " + event);
                if (event instanceof EvacuationStepCompleteEvent || event instanceof EvacuationInitializationCompleteEvent) {
                    System.out.println(eca.graphicalToString());
                }
            }
        };

        caAlgorithm.addAlgorithmListener(listener);

        caAlgorithm.runAlgorithm();

        EvacuationSimulationResult result = caAlgorithm.getSolution();
        assertThat(result.getSteps(), is(equalTo(2)));
        assertThat(caAlgorithm.isFinished(), is(true));

    }
}
