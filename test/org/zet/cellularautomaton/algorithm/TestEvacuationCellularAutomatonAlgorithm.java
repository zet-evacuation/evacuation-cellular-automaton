package org.zet.cellularautomaton.algorithm;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellState;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuationCellularAutomatonAlgorithm {
    private final Mockery context = new Mockery();

    private static class MockEvacCell extends EvacCell {

        public MockEvacCell(int x, int y) {
            super(new EvacuationCellState(null), 1, x, y);
        }

        @Override
        public EvacCell clone() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    };
    
    class MockEvacuationCellularAutomatonAlgorithm extends EvacuationCellularAutomatonAlgorithm {

        @Override
        protected List<Individual> getIndividuals() {
            return Collections.EMPTY_LIST;
        };
    }
    
    @Test
    public void testInitializationPerformsRules() {
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm() {

            @Override
            protected void performStep() {
            }

            @Override
            protected boolean isFinished() {
                return true;
            }
        };
        
        Individual i1 = new Individual();
        EvacCell cell1 = new MockEvacCell(0, 0);
        i1.setCell(cell1);
        Individual i2 = new Individual();
        EvacCell cell2 = new MockEvacCell(0, 0);
        i2.setCell(cell2);
        List<Individual> individuals = new LinkedList<>();
        individuals.add(i1);
        individuals.add(i2);
        
        
        
        // Set up a rule set
        EvacuationRuleSet rules = new EvacuationRuleSet() {
            
        };
        EvacuationRule primary1 = context.mock(EvacuationRule.class, "primary rule 1");
        EvacuationRule primary2 = context.mock(EvacuationRule.class, "primary rule 2");
        EvacuationRule loop = context.mock(EvacuationRule.class);
        
        rules.add(primary1, true, false);
        rules.add(primary2, true, true);
        rules.add(loop, false, true);
        
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(3));
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(rules));
                
                allowing(eca).start();
                allowing(eca).getIndividuals();
                will(returnValue(individuals));
                
                allowing(eca).removeMarkedIndividuals();
                allowing(eca).getNotSafeIndividualsCount();
                will(returnValue(0));
                allowing(eca).getTimeStep();
                will(returnValue(0));
                allowing(eca).getNeededTime();
                will(returnValue(0));
                allowing(eca).stop();
                
                // primary rules are allowed to be called exactly once for each of the cells
                exactly(1).of(primary1).execute(with(cell1));
                exactly(1).of(primary1).execute(with(cell2));
                exactly(1).of(primary2).execute(with(cell1));
                exactly(1).of(primary2).execute(with(cell2));
                never(loop).execute(with(any(EvacCell.class)));
            }
        });        
        
        algorithm.setProblem(esp);
        algorithm.runAlgorithm();
    }
    
    @Test
    public void testMaxStepsTakenFromProblem() {
        final AtomicInteger c = new AtomicInteger();
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm() {

            @Override
            protected void performStep() {
                super.increaseStep();
                c.incrementAndGet();
            }
            
        };
        
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(3));
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                
                allowing(eca).start();
                allowing(eca).getIndividuals();
                will(returnValue(Collections.EMPTY_LIST));
                allowing(eca).removeMarkedIndividuals();
                allowing(eca).getNotSafeIndividualsCount();
                will(returnValue(0));
                allowing(eca).getTimeStep();
                will(returnValue(0));
                allowing(eca).getNeededTime();
                will(returnValue(0));
                allowing(eca).stop();
            }
        });        
        
        algorithm.setProblem(esp);
        algorithm.runAlgorithm();
        
        assertThat(algorithm.getMaxSteps(), is(equalTo(3)));
        assertThat(c.get(), is(equalTo(3)));
    }
}
