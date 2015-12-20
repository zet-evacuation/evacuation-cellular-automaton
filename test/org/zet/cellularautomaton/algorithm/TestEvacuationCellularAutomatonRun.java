package org.zet.cellularautomaton.algorithm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;
import org.junit.Test;

import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.RoomImpl;
import org.zet.cellularautomaton.algorithm.rule.EvacuateIndividualsRule;
import org.zet.cellularautomaton.algorithm.rule.SimpleMovementRule;
import org.zet.cellularautomaton.potential.PotentialAlgorithm;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 * Sets up a simple evacuation scenario and runs the algorithm.
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuationCellularAutomatonRun {
    private static class TestEvacuationRuleSet extends EvacuationRuleSet {

        public TestEvacuationRuleSet() {
            this.add(new SimpleMovementRule(), false, true);
            this.add(new EvacuateIndividualsRule());
        }
    }
    
    @Test
    public void run() {
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        
        eca.addFloor("floor");
        
        RoomImpl r = new RoomImpl(3, 1, 0, 0, 0);
        
        ExitCell exit = new ExitCell(1, 0, 0);
        r.setCell(exit);
        RoomCell middleCell = new RoomCell(1, 1, 0, r);
        r.setCell(middleCell);
        RoomCell rightCell = new RoomCell(1, 2, 0, r);
        r.setCell(rightCell);
        eca.addRoom(r);
        
        List<List<ExitCell>> exitClusters = eca.clusterExitCells();
        assertThat(exitClusters, hasSize(1));
        List<ExitCell> exitCluster = exitClusters.get(0);
        assertThat(exitCluster, hasSize(1));
        assertThat(exitCluster, contains(exit));

        for (List<ExitCell> cells : exitClusters) {
            PotentialAlgorithm pa = new PotentialAlgorithm();
            pa.setProblem(cells);
            StaticPotential sp = pa.call();
            eca.getPotentialManager().addStaticPotential(sp);
            // Bestimme die angrenzenden Save-Cells
            //saveCellSearch(cells, sp);
        }
        
        //eca.getPotentialManager().addStaticPotential(null);
        
        Individual i = new Individual();
        //rightCell.getState().setIndividual(i);
        eca.addIndividual(rightCell, i);
        
        
        
        EvacuationCellularAutomatonAlgorithm caAlgorithm = new EvacuationCellularAutomatonAlgorithm();

        
        String ruleSet = "TestEvacuationCellularAutomatonRun$TestEvacuationRuleSet";
        ds.PropertyContainer.getGlobal().define("algo.ca.ruleSet", String.class, ruleSet);
        ds.PropertyContainer.getGlobal().define("algo.ca.parameterSet", String.class, "SimpleParameterSet");
        
        EvacuationSimulationProblem esp = new EvacuationSimulationProblemImpl(eca);
        
        caAlgorithm.setProblem(esp);
        
        caAlgorithm.runAlgorithm();
        
        EvacuationSimulationResult result = caAlgorithm.getSolution();
        
        assertThat(result.getSteps(), is(equalTo(2)));
    }
}
