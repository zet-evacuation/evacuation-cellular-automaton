package org.zet.cellularautomaton.algorithm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.jmock.AbstractExpectations.returnValue;
import static org.junit.Assert.assertThat;
import static org.zetool.common.util.Helper.in;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellState;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;
import org.zet.cellularautomaton.algorithm.rule.EvacuationState;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.common.algorithm.AlgorithmDetailedProgressEvent;
import org.zetool.common.algorithm.AlgorithmProgressEvent;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuationCellularAutomatonAlgorithm {
    private Mockery context = new Mockery();
    private final static IndividualBuilder builder = new IndividualBuilder();

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
        
        List<Individual> individuals = getIndividuals();
        
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
                
                allowing(primary1).setEvacuationSimulationProblem(with(any(EvacuationState.class)));
                allowing(primary2).setEvacuationSimulationProblem(with(any(EvacuationState.class)));
                allowing(loop).setEvacuationSimulationProblem(with(any(EvacuationState.class)));
                
                allowing(eca).start();
                allowing(eca).getIndividuals();
                will(returnValue(individuals));
                
                allowing(eca).removeMarkedIndividuals();
                allowing(eca).getNotSafeIndividualsCount();
                
                // primary rules are allowed to be called exactly once for each of the cells
                exactly(1).of(primary1).execute(with(individuals.get(0).getCell()));
                exactly(1).of(primary1).execute(with(individuals.get(1).getCell()));
                exactly(1).of(primary2).execute(with(individuals.get(0).getCell()));
                exactly(1).of(primary2).execute(with(individuals.get(1).getCell()));
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
        
        List<Individual> individuals = getIndividuals();

        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {
            @Override
            protected void initialize() {
                
            }

            @Override
            protected EvacuationSimulationResult terminate() {
                return null;
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
        //PotentialController pc = context.mock(PotentialController.class);
        ParameterSet ps = context.mock(ParameterSet.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(rules));

                exactly(1).of(eca).getIndividuals();
                will(returnValue(individuals));
                
                exactly(1).of(eca).removeMarkedIndividuals();

                allowing(esp).getParameterSet();
                will(returnValue(ps));
                allowing(ps).probabilityDynamicDecrease();
                allowing(ps).probabilityDynamicIncrease();
                exactly(1).of(eca).updateDynamicPotential(with(0.0), with(0.0));

                allowing(eca).getIndividualCount();
                allowing(eca).getInitialIndividualCount();

                // loop rules are allowed to be called exactly once for each of the cells
                never(primary1).execute(with(any(EvacCell.class)));
                exactly(1).of(primary2).execute(with(individuals.get(0).getCell()));
                exactly(1).of(primary2).execute(with(individuals.get(1).getCell()));
                exactly(1).of(loop).execute(with(individuals.get(0).getCell()));
                exactly(1).of(loop).execute(with(individuals.get(1).getCell()));
            }
        });        
        
        algorithm.setProblem(esp);
        algorithm.runAlgorithm();
        context.assertIsSatisfied();
    }
    
    @Test
    public void testTerminateEventAllSave() {
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {
            @Override
            protected void initialize() {
                
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
            }
        });
        algorithm.addAlgorithmListener(event -> {
            if( event instanceof AlgorithmProgressEvent ) {
                assertThat(((AlgorithmDetailedProgressEvent) event).getProgress(), is(closeTo(1.0, 10e-6)));
            }
        });
        
        algorithm.setProblem(esp);
        EvacuationSimulationResult result = algorithm.terminate();
    }

    @Test
    public void testTerminateSomeIndividualsLeft() {
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {
            @Override
            protected void initialize() {
            }

        };
        List<Individual> individuals = getIndividuals();
        individuals.get(1).setSafe(true);

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
                exactly(1).of(eca).setIndividualDead(individuals.get(0), DeathCause.NOT_ENOUGH_TIME);
                never(eca).setIndividualDead(individuals.get(1), DeathCause.NOT_ENOUGH_TIME);

                allowing(eca).stop();
            }
        });
        
        algorithm.setProblem(esp);
        EvacuationSimulationResult result = algorithm.terminate();
        context.assertIsSatisfied();
    }

    /**
     * Returns a list of two individuals including cell information.
     * @return a list of individuals
     */
    private List<Individual> getIndividuals() {
        Individual i1 = builder.build();
        EvacCell cell1 = new MockEvacCell(0, 0);
        i1.setCell(cell1);
        cell1.getState().setIndividual(i1);
        Individual i2 = builder.build();
        EvacCell cell2 = new MockEvacCell(0, 0);
        i2.setCell(cell2);
        cell2.getState().setIndividual(i2);
        List<Individual> individuals = new LinkedList<>();
        individuals.add(i1);
        individuals.add(i2);
        return individuals;
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
        algorithm.setNeededTime(neededTime);
        
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(eca).getNotSafeIndividualsCount();
                will(returnValue(notSave));
                
                //allowing(eca).getNeededTime();
                //will(returnValue(neededTime));
            }
        });
        algorithm.setMaxSteps(maxSteps);
        algorithm.performStep();
        algorithm.setProblem(esp);
        return algorithm;
    }
    
    @Test
    public void testDefaultIterator() {
        List<Individual> individuals = getIndividuals(new int[] {4, 2, 6, 1} );
        List<Individual> expectedOrder = new ArrayList<>();
        for (int i : new int[]{0, 1, 2, 3}) {
            expectedOrder.add(individuals.get(i));
        }
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(false);
        assertOrder(algorithm, individuals, expectedOrder);
    }
    
    @Test
    public void testManualIterator() {
        List<Individual> individuals = getIndividuals(new int[] {4, 2, 6, 1} );
        List<Individual> expectedOrder = new ArrayList<>();
        for (int i : new int[]{2, 1, 3, 0}) {
            expectedOrder.add(individuals.get(i));
        }
        Function<List<Individual>,Iterator<Individual>> manualOrder = x -> expectedOrder.iterator();
        EvacuationCellularAutomatonAlgorithm algorithm = new EvacuationCellularAutomatonAlgorithm(manualOrder);
        assertOrder(algorithm, individuals, expectedOrder);
    }
    
    private void assertOrder(EvacuationCellularAutomatonAlgorithm algorithm, List<Individual> individuals, List<Individual> expectedOrder) {
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                
                exactly(1).of(eca).getIndividuals();
                will(returnValue(individuals));
            }
        });       
        algorithm.setProblem(esp);
        
        List<Individual> callOrder = new LinkedList<>();
        for( EvacCell individual : algorithm ) {
            callOrder.add(individual.getState().getIndividual());
        }
        assertThat(callOrder, hasSize(expectedOrder.size()));
        assertThat(callOrder, is(equalTo(expectedOrder)));
    }
    
    @Test
    public void testIteratorFrontToBack() {
        List<Individual> individuals = getIndividuals(new int[] {4, 2, 6, 1} );
        List<Individual> expectedOrder = new ArrayList<>();
        for (int i : new int[]{3, 1, 0, 2}) {
            expectedOrder.add(individuals.get(i));
        }
        assertCorrectOrder(EvacuationCellularAutomatonAlgorithm.FRONT_TO_BACK, individuals, expectedOrder);
    }
    
    @Test
    public void testIteratorBackToFront() {
        List<Individual> individuals = getIndividuals(new int[] {4, 2, 6, 1} );
        List<Individual> expectedOrder = new ArrayList<>();
        for (int i : new int[]{2, 0, 1, 3}) {
            expectedOrder.add(individuals.get(i));
        }
        assertCorrectOrder(EvacuationCellularAutomatonAlgorithm.BACK_TO_FRONT, individuals, expectedOrder);
    }
    
    /**
     * Returns a list containing individuals with associated static potentials and the given distance.
     * @param distance array containing the distances of the individuals
     * @return list containing as many individuals as distances
     */
    private List<Individual> getIndividuals(int[] distance) {
        List<Individual> individuals = new LinkedList<>();
        for( int d : distance ) {
            Individual individual = builder.build();
            EvacCell cell = new MockEvacCell(0, 0);
            individual.setCell(cell);
            cell.getState().setIndividual(individual);
            StaticPotential sp = new StaticPotential();
            sp.setPotential(cell, d);
            individual.setStaticPotential(sp);
            individuals.add(individual);
        }        
        return individuals;
    }
    
    private void assertCorrectOrder(Function<List<Individual>, Iterator<Individual>> frontToBack,
            List<Individual> original, List<Individual> expectedOrder) {
        Iterator<Individual> resultIterator = frontToBack.apply(original);
 
        List<Individual> callOrder = new LinkedList<>();
        for( Individual individual : in(resultIterator) ) {
            callOrder.add(individual);
        }
        assertThat(callOrder, hasSize(expectedOrder.size()));
        assertThat(callOrder, is(equalTo(expectedOrder)));
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
        ParameterSet ps = context.mock(ParameterSet.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));

                exactly(1).of(eca).getIndividuals();
                will(returnValue(Collections.EMPTY_LIST));

                allowing(eca).removeMarkedIndividuals();

                allowing(esp).getParameterSet();
                will(returnValue(ps));
                allowing(ps).probabilityDynamicDecrease();
                allowing(ps).probabilityDynamicIncrease();
                exactly(1).of(eca).updateDynamicPotential(with(0.0), with(0.0));
                

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
        final AtomicInteger stepCounter = new AtomicInteger();
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm() {

            @Override
            protected void performStep() {
                super.increaseStep();
                stepCounter.incrementAndGet();
            }
        };
        algorithm.setNeededTime(Integer.MAX_VALUE);
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        
        context.checking(new Expectations() {
            {
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(3));
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(new EvacuationRuleSet() {}));
                
                allowing(eca).start();
                allowing(eca).getIndividuals();
                will(returnValue(Collections.EMPTY_LIST));
                allowing(eca).removeMarkedIndividuals();
                allowing(eca).getNotSafeIndividualsCount();
                will(returnValue(0));
                //allowing(eca).getNeededTime();
                //will(returnValue(Integer.MAX_VALUE));
                allowing(eca).stop();
            }
        });        
        
        algorithm.setProblem(esp);
        algorithm.runAlgorithm();
        
        assertThat(algorithm.getMaxSteps(), is(equalTo(3)));
        assertThat(stepCounter.get(), is(equalTo(3)));
    }
}
