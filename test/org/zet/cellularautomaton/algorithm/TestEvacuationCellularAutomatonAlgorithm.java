package org.zet.cellularautomaton.algorithm;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellState;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;
import org.zetool.common.algorithm.AlgorithmDetailedProgressEvent;
import org.zetool.common.algorithm.AlgorithmProgressEvent;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuationCellularAutomatonAlgorithm {
    private Mockery context = new Mockery();

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
        boolean finished;
        final boolean specialFinishHandler;

        public MockEvacuationCellularAutomatonAlgorithm() {
            specialFinishHandler = false;
        }
        
        public MockEvacuationCellularAutomatonAlgorithm(boolean execute) {
            this.finished = !execute;
            specialFinishHandler = true;
        }
        
            @Override
            protected boolean isFinished() {
                if(!specialFinishHandler) {
                    return super.isFinished();
                }
                if (finished) {
                    return true;
                }
                finished = true;
                return false;
            }

        @Override
        protected List<Individual> getIndividuals() {
            return Collections.EMPTY_LIST;
        };
    }
    
    @Test
    public void testInitializationPerformsRules() {
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {

            @Override
            protected void performStep() {
            }

            @Override
            protected EvacuationSimulationResult terminate() {
                return null;
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
        context.assertIsSatisfied();
    }

    @Test
    public void testPerformStep() {
        // Tests that
        // - Execute is called for all rules
        // - Marked individuals are removed from CA
        // - Dynamic Potential is updated
        // - Time Step of CA is updated.
        
        Individual i1 = new Individual();
        EvacCell cell1 = new MockEvacCell(0, 0);
        i1.setCell(cell1);
        cell1.getState().setIndividual(i1);
        Individual i2 = new Individual();
        EvacCell cell2 = new MockEvacCell(0, 0);
        i2.setCell(cell2);
        cell2.getState().setIndividual(i2);
        List<Individual> individuals = new LinkedList<>();
        individuals.add(i1);
        individuals.add(i2);

        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {
            @Override
            protected void initialize() {
                
            }

            @Override
            protected EvacuationSimulationResult terminate() {
                return null;
            }

            @Override
            protected List<Individual> getIndividuals() {
                return individuals;
            }
        };
        
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
        PotentialController pc = context.mock(PotentialController.class);
        ParameterSet ps = context.mock(ParameterSet.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(rules));

                exactly(1).of(eca).removeMarkedIndividuals();

                allowing(esp).getPotentialController();
                will(returnValue(pc));
                allowing(esp).getParameterSet();
                will(returnValue(ps));
                allowing(ps).probabilityDynamicDecrease();
                allowing(ps).probabilityDynamicIncrease();
                exactly(1).of(pc).updateDynamicPotential(with(0.0), with(0.0));
                exactly(1).of(eca).nextTimeStep();

                allowing(eca).getIndividualCount();
                allowing(eca).getInitialIndividualCount();

                // loop rules are allowed to be called exactly once for each of the cells
                never(primary1).execute(with(any(EvacCell.class)));
                exactly(1).of(primary2).execute(with(cell1));
                exactly(1).of(primary2).execute(with(cell2));
                exactly(1).of(loop).execute(with(cell1));
                exactly(1).of(loop).execute(with(cell2));
            }
        });        
        
        algorithm.setProblem(esp);
        algorithm.runAlgorithm();
        context.assertIsSatisfied();
    }
    
    @Test
    public void testTerminateAllSave() {
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {
            @Override
            protected void initialize() {
                
            }

            @Override
            protected List<Individual> getIndividuals() {
                return null;
            }
        };
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(eca).getNotSafeIndividualsCount();
                will(returnValue(0));
                
                allowing(eca).stop();
                allowing(eca).getTimeStep();
                will(returnValue(15));
            }
        });
        algorithm.addAlgorithmListener(event -> {
            if( event instanceof AlgorithmProgressEvent ) {
                assertThat(((AlgorithmDetailedProgressEvent) event).getProgress(), is(closeTo(1.0, 10e-6)));
            }
        });
        
        algorithm.setProblem(esp);
        EvacuationSimulationResult result = algorithm.terminate();
        assertThat(result.getSteps(), is(equalTo(15)));
    }
    
    @Test
    public void testTerminateSomeIndividualsLeft() {
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {
            @Override
            protected void initialize() {
                
            }

            @Override
            protected List<Individual> getIndividuals() {
                return null;
            }
        };
        Individual i1 = new Individual();
        i1.setNumber(1);
        EvacCell cell1 = new MockEvacCell(0, 0);
        i1.setCell(cell1);
        cell1.getState().setIndividual(i1);
        Individual i2 = new Individual();
        i2.setNumber(2);
        EvacCell cell2 = new MockEvacCell(0, 0);
        i2.setCell(cell2);
        cell2.getState().setIndividual(i2);
        List<Individual> individuals = new LinkedList<>();
        individuals.add(i1);
        individuals.add(i2);
        i2.setSafe(true);

        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(eca).getNotSafeIndividualsCount();
                will(returnValue(1));
                
                allowing(eca).getIndividuals();
                will(returnValue(individuals));
                exactly(1).of(eca).setIndividualDead(i1, DeathCause.NOT_ENOUGH_TIME);
                never(eca).setIndividualDead(i2, DeathCause.NOT_ENOUGH_TIME);

                allowing(eca).stop();
                allowing(eca).getTimeStep();
            }
        });
        
        algorithm.setProblem(esp);
        EvacuationSimulationResult result = algorithm.terminate();
        context.assertIsSatisfied();
    }
    
    
    @Test
    public void testUnfinished() {
        assertThat(getAlgorithm(1, 0, 1, 2).isFinished(), is(false));
    }

    @Test
    public void testContinueIfTimeNecessary() {
        assertThat(getAlgorithm(0, 0, 1, 2).isFinished(), is(false));
    }
    
    @Test
    public void testContinueIfNotAllSafe() {
        assertThat(getAlgorithm(1, 1, 0, 2).isFinished(), is(false));
    }
    
    @Test
    public void testFinishedIfAllSave() {
        assertThat(getAlgorithm(0, 1, 0, 2).isFinished(), is(true));
    }
    
    @Test
    public void testFinishedIfMaxStep() {
        assertThat(getAlgorithm(1, 0, 1, 1).isFinished(), is(true));
    }
    
    public EvacuationCellularAutomatonAlgorithm getAlgorithm(int notSave, int timeStep, int neededTime, int maxSteps) {
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm() {
            @Override
            protected void performStep() {
                super.increaseStep();
            }
            
            
        };
        
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(eca).getNotSafeIndividualsCount();
                will(returnValue(notSave));
                
                allowing(eca).getTimeStep();
                will(returnValue(timeStep));
                
                allowing(eca).getNeededTime();
                will(returnValue(neededTime));
            }
        });
        algorithm.setMaxSteps(maxSteps);
        algorithm.performStep();
        algorithm.setProblem(esp);
        return algorithm;
    }

    @Test
    public void testProgress() {
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {
            @Override
            protected void initialize() {
                
            }

            @Override
            protected EvacuationSimulationResult terminate() {
                return null;
            }
        };
        
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        PotentialController pc = context.mock(PotentialController.class);
        ParameterSet ps = context.mock(ParameterSet.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));

                allowing(eca).removeMarkedIndividuals();

                allowing(esp).getPotentialController();
                will(returnValue(pc));
                allowing(esp).getParameterSet();
                will(returnValue(ps));
                allowing(ps).probabilityDynamicDecrease();
                allowing(ps).probabilityDynamicIncrease();
                exactly(1).of(pc).updateDynamicPotential(with(0.0), with(0.0));
                exactly(1).of(eca).nextTimeStep();

                allowing(eca).getIndividualCount();
                will(returnValue(1));
                allowing(eca).getInitialIndividualCount();
                will(returnValue(2));
            }
        });
        
        Double d[] = new Double[1];
        algorithm.addAlgorithmListener(event -> {
            if( event instanceof AlgorithmProgressEvent ) {
                d[0] = ((AlgorithmDetailedProgressEvent) event).getProgress();
            }
        });
        
        algorithm.setProblem(esp);
        algorithm.runAlgorithm();
        assertThat(d[0], is(closeTo(0.5, 10e-8)));
        context.assertIsSatisfied();
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
