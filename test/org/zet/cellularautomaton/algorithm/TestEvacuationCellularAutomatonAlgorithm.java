package org.zet.cellularautomaton.algorithm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.jmock.AbstractExpectations.returnValue;
import static org.junit.Assert.assertThat;
import static org.zetool.common.util.Helper.in;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellState;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.algorithm.state.MutableEvacuationState;
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
    private StaticPotential sp = new StaticPotential();

    public static class MockEvacCell extends EvacCell {
        
        public MockEvacCell(int x, int y) {
            super(new EvacuationCellState(null), 1, x, y);
        }

        @Override
        public EvacCell clone() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setRoom(Room room) {
            super.room = room;
        }

    };

    class MockEvacuationCellularAutomatonAlgorithm extends EvacuationCellularAutomatonAlgorithm {

        boolean finished;
        final boolean specialFinishHandler;
        private final EvacuationState specialEs = null;

        public MockEvacuationCellularAutomatonAlgorithm() {
            specialFinishHandler = false;
        }

        public MockEvacuationCellularAutomatonAlgorithm(boolean execute) {
            this.finished = !execute;
            specialFinishHandler = true;
        }

        @Override
        protected boolean isFinished() {
            if (!specialFinishHandler) {
                return super.isFinished();
            }
            if (finished) {
                return true;
            }
            finished = true;
            return false;
        }

        @Override
        public EvacuationState getEvacuationState() {
            if( specialEs != null ) {
                return specialEs;
            } else {
                return super.getEvacuationState();
            }
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

        List<Individual> individuals = getTwoIndividuals();
        Map<Individual, MockEvacCell> isp = getIndividualPositions(individuals);

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
        context.checking(new Expectations() {{
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(3));
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(rules));

                allowing(primary1).setEvacuationState(with(any(EvacuationState.class)));
                allowing(primary2).setEvacuationState(with(any(EvacuationState.class)));
                allowing(loop).setEvacuationState(with(any(EvacuationState.class)));
                allowing(primary1).setEvacuationStateController(with(any(EvacuationStateControllerInterface.class)));
                allowing(primary2).setEvacuationStateController(with(any(EvacuationStateControllerInterface.class)));
                allowing(loop).setEvacuationStateController(with(any(EvacuationStateControllerInterface.class)));
                
                allowing(esp).getIndividuals();
                will(returnValue(individuals));
                allowing(esp).individualStartPositions();
                will(returnValue(isp));

                allowing(eca).minPotentialFor(with(any(EvacCell.class)));
                will(returnValue(sp));
                allowing(esp).getParameterSet();
                will(returnValue(context.mock(ParameterSet.class)));

                // primary rules are allowed to be called exactly once for each of the cells
                exactly(1).of(primary1).execute(with(isp.get(individuals.get(0))));
                exactly(1).of(primary1).execute(with(isp.get(individuals.get(1))));
                exactly(1).of(primary2).execute(with(isp.get(individuals.get(0))));
                exactly(1).of(primary2).execute(with(isp.get(individuals.get(1))));
                never(loop).execute(with(any(EvacCell.class)));
            }});

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

        List<Individual> individuals = getTwoIndividuals();
        Map<Individual, MockEvacCell> isp = getIndividualPositions(individuals);
        EvacCell dynamicPotentialCheckCell = isp.get(individuals.get(0));

        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {

            @Override
            protected EvacuationSimulationResult terminate() {
                return null;
            }

        };

        // Set up a rule set
        EvacuationRuleSet rules = new EvacuationRuleSet() {
        };
        
        int[] counter = new int[]{0};
        
        EvacuationRule initDynamicPotential = new EvacuationRule() {
            private EvacuationStateControllerInterface ec;
            private EvacuationState es;
            @Override
            public void execute(EvacCellInterface cell) {
                ec.increaseDynamicPotential(cell);
                counter[0]++;
                assertThat(es.getDynamicPotential(cell), is(equalTo(1.0)));
            }

            @Override
            public boolean executableOn(EvacCellInterface cell) {
                return true;
            }

            @Override
            public void setEvacuationState(EvacuationState es) {
                this.es = es;
            }

            @Override
            public void setEvacuationStateController(EvacuationStateControllerInterface ec) {
                this.ec = ec;
            }

            @Override
            public void setComputation(Computation c) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            
        };

        EvacuationRule primary1 = context.mock(EvacuationRule.class, "primary rule 1");
        EvacuationRule primary2 = context.mock(EvacuationRule.class, "primary rule 2");
        EvacuationRule loop = context.mock(EvacuationRule.class);

        rules.add(initDynamicPotential, true, false);
        rules.add(primary1, true, false);
        rules.add(primary2, true, true);
        rules.add(loop, false, true);

        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        ParameterSet ps = context.mock(ParameterSet.class);        
        
        context.checking(new Expectations() {{
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(rules));

                allowing(esp).getIndividuals();
                will(returnValue(individuals));

                allowing(esp).getParameterSet();
                will(returnValue(ps));
                allowing(esp).individualStartPositions();
                will(returnValue(isp));
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(300));

                allowing(ps).probabilityDynamicDecrease();
                will(returnValue(1.0));
                allowing(ps).probabilityDynamicIncrease();

                allowing(primary1).setEvacuationState(with(any(EvacuationState.class)));
                allowing(primary2).setEvacuationState(with(any(EvacuationState.class)));
                allowing(loop).setEvacuationState(with(any(EvacuationState.class)));
                allowing(primary1).setEvacuationStateController(with(any(EvacuationStateControllerInterface.class)));
                allowing(primary2).setEvacuationStateController(with(any(EvacuationStateControllerInterface.class)));
                allowing(loop).setEvacuationStateController(with(any(EvacuationStateControllerInterface.class)));
                
                allowing(eca).minPotentialFor(with(any(EvacCell.class)));
                will(returnValue(sp));

                // loop rules are allowed to be called exactly once for each of the cells
                exactly(1).of(primary1).execute(with(isp.get(individuals.get(0))));
                exactly(1).of(primary1).execute(with(isp.get(individuals.get(1))));
                never(primary1).execute(with(any(EvacCell.class)));
                exactly(2).of(primary2).execute(with(isp.get(individuals.get(0))));
                exactly(2).of(primary2).execute(with(isp.get(individuals.get(1))));
                exactly(1).of(loop).execute(with(isp.get(individuals.get(0))));
                exactly(1).of(loop).execute(with(isp.get(individuals.get(1))));
            }});

        
        algorithm.setProblem(esp);
                
        algorithm.runAlgorithm();

        // check for updated dynamic potential
        assertThat(counter[0], is(equalTo(2)));
        assertThat(algorithm.getEvacuationState().getDynamicPotential(dynamicPotentialCheckCell), is(lessThan(0.5)));
        
        context.assertIsSatisfied();
    }

    @Test
    public void testTerminateEventAllSave() {
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true);
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        
        context.checking(new Expectations() {{
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(new TestEvacuationRuleSet.FakeEvacuationRuleSet()));
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(300));
                allowing(esp).getIndividuals();                
                will(returnValue(Collections.emptyList()));
                allowing(esp).getParameterSet();
                will(returnValue(context.mock(ParameterSet.class)));
                allowing(esp).individualStartPositions();
                will(returnValue(Collections.EMPTY_MAP));
            }});
        algorithm.addAlgorithmListener(event -> {
            if (event instanceof AlgorithmProgressEvent) {
                assertThat(((AlgorithmDetailedProgressEvent) event).getProgress(), is(closeTo(1.0, 10e-6)));
            }
        });

        algorithm.setProblem(esp);
        algorithm.initialize();
        EvacuationSimulationResult result = algorithm.terminate();
    }

    @Test
    public void terminateLeftIndividualsDie() {
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {
        };
        List<Individual> individuals = getTwoIndividuals();
        Map<Individual, MockEvacCell> isp = getIndividualPositions(individuals);

        Room room = context.mock(Room.class);
        isp.get(individuals.get(0)).setRoom(room);

        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {{
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));

                allowing(esp).getRuleSet();
                will(returnValue(new EvacuationRuleSet() {

                }));
                allowing(esp).individualStartPositions();
                will(returnValue(isp));
                allowing(esp).getEvacuationStepLimit();
                allowing(eca).minPotentialFor(with(any(EvacCell.class)));
                will(returnValue(sp));

                allowing(esp).getIndividuals();
                will(returnValue(individuals));
                allowing(esp).getParameterSet();
                will(returnValue(context.mock(ParameterSet.class)));
                
                allowing(room).removeIndividual(individuals.get(0));
            }});
        algorithm.setProblem(esp);
        algorithm.initialize();

        EvacuationState es = algorithm.getEvacuationState();
        EvacuationStateControllerInterface ec = algorithm.getEvacuationController();
        ec.setSafe(individuals.get(1));

        EvacuationSimulationResult result = algorithm.terminate();
        assertThat(es.propertyFor(individuals.get(0)).isDead(), is(true));
        assertThat(es.propertyFor(individuals.get(0)).getDeathCause(), is(equalTo(DeathCause.NOT_ENOUGH_TIME)));
        IndividualProperty ip = es.propertyFor(individuals.get(1));
        assertThat(ip.isDead(), is(false));
    }

    private List<EvacCell> getCells() {
        List<EvacCell> cells = new LinkedList<>();
        cells.add(new MockEvacCell(0, 0));
        cells.add( new MockEvacCell(1, 1));
        return cells;
    }
    
    /**
     * Returns a list of two individuals including cell information.
     *
     * @return a list of individuals
     */
    private List<Individual> getTwoIndividuals() {
        List<Individual> individuals = new LinkedList<>();
        individuals.add(builder.build());
        individuals.add(builder.build());
        return individuals;
    }
    
    private Map<Individual, MockEvacCell> getIndividualPositions(List<Individual> individuals) {
        Map<Individual, MockEvacCell> isp = new HashMap<>();
        for( int i = 0; i < individuals.size(); ++i) {
            MockEvacCell cell = new MockEvacCell(i, 0);
            cell.getState().setIndividual(individuals.get(i));
            isp.put(individuals.get(i), cell);
        }
        return isp;
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
        context.checking(new Expectations() {{
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(new TestEvacuationRuleSet.FakeEvacuationRuleSet()));
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(maxSteps));
                allowing(esp).getIndividuals();
                will(returnValue(Collections.emptyList()));
                allowing(esp).individualStartPositions();
                will(returnValue(Collections.emptyMap()));
                allowing(esp).getParameterSet();
                will(returnValue(context.mock(ParameterSet.class)));
            }});
        algorithm.performStep();
        algorithm.setProblem(esp);
        algorithm.initialize();
        IndividualBuilder b = new IndividualBuilder();
        for( int i = 0; i < notSave; ++i) {
            ((MutableEvacuationState)algorithm.getEvacuationState()).addIndividual(b.build());         
        }
        algorithm.setNeededTime(neededTime);
        return algorithm;
    }

    @Test
    public void testDefaultIterator() {
        List<Individual> individuals = getIndividuals(new int[]{4, 2, 6, 1});
        List<Individual> expectedOrder = new ArrayList<>();
        for (int i : new int[]{0, 1, 2, 3}) {
            expectedOrder.add(individuals.get(i));
        }
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(false);
        assertOrder(algorithm, individuals, expectedOrder);
    }

    @Test
    public void testManualIterator() {
        List<Individual> individuals = getIndividuals(new int[]{4, 2, 6, 1});
        List<Individual> expectedOrder = new ArrayList<>();
        for (int i : new int[]{2, 1, 3, 0}) {
            expectedOrder.add(individuals.get(i));
        }
        Function<List<Individual>, Iterator<Individual>> manualOrder = x -> expectedOrder.iterator();
        EvacuationCellularAutomatonAlgorithm algorithm = new EvacuationCellularAutomatonAlgorithm(manualOrder);
        assertOrder(algorithm, individuals, expectedOrder);
    }

    private void assertOrder(EvacuationCellularAutomatonAlgorithm algorithm, List<Individual> individuals, List<Individual> expectedOrder) {
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {{
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(new TestEvacuationRuleSet.FakeEvacuationRuleSet()));
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(300));
                
                allowing(esp).getParameterSet();
                will(returnValue(context.mock(ParameterSet.class)));
                allowing(eca).minPotentialFor(with(any(EvacCell.class)));
                will(returnValue(sp));

                allowing(esp).getIndividuals();
                will(returnValue(individuals));
                allowing(esp).individualStartPositions();
                will(returnValue(getIndividualPositions(individuals)));
            }});
        algorithm.setProblem(esp);
        algorithm.initialize();
        List<Individual> callOrder = new LinkedList<>();
        for (EvacCellInterface individual : algorithm) {
            callOrder.add(individual.getState().getIndividual());
        }
        assertThat(callOrder, hasSize(expectedOrder.size()));
        assertThat(callOrder, is(equalTo(expectedOrder)));
    }

    @Test
    public void testIteratorFrontToBack() {
        int[] distances = new int[]{4, 2, 6, 1};
        List<Individual> individuals = getIndividuals(distances);
        List<Individual> expectedOrder = new ArrayList<>();
        for (int i : new int[]{3, 1, 0, 2}) {
            expectedOrder.add(individuals.get(i));
        }
        
        EvacuationCellularAutomatonAlgorithm algorithm = EvacuationCellularAutomatonAlgorithm.getFrontToBackAlgorithm();
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        EvacuationRuleSet rs = new TestEvacuationRuleSet.FakeEvacuationRuleSet();
        ParameterSet ps = context.mock(ParameterSet.class);
        context.checking(new Expectations() {{
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(rs));
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(300));
                
                allowing(esp).getParameterSet();
                will(returnValue(ps));
                allowing(eca).minPotentialFor(with(any(EvacCell.class)));
                will(returnValue(sp));

                allowing(esp).getIndividuals();
                will(returnValue(individuals));
                allowing(esp).individualStartPositions();
                will(returnValue(getIndividualPositions(individuals)));
                
                allowing(ps).probabilityDynamicDecrease();
                allowing(ps).probabilityDynamicIncrease();
            }});
        algorithm.setProblem(esp);
        algorithm.initialize();
       
        assertCorrectOrder(algorithm, individuals, expectedOrder, distances, rs);
    }

    @Test
    public void testIteratorBackToFront() {
        int[] distances = new int[]{4, 2, 6, 1};
        List<Individual> individuals = getIndividuals(distances);
        List<Individual> expectedOrder = new ArrayList<>();
        for (int i : new int[]{2, 0, 1, 3}) {
            expectedOrder.add(individuals.get(i));
        }
        
        
        EvacuationCellularAutomatonAlgorithm algorithm = EvacuationCellularAutomatonAlgorithm.getBackToFrontAlgorithm();
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        EvacuationRuleSet rs = new TestEvacuationRuleSet.FakeEvacuationRuleSet();
        ParameterSet ps = context.mock(ParameterSet.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(rs));
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(300));
                
                allowing(esp).getParameterSet();
                will(returnValue(ps));
                allowing(eca).minPotentialFor(with(any(EvacCell.class)));
                will(returnValue(sp));

                allowing(esp).getIndividuals();
                will(returnValue(individuals));
                allowing(esp).individualStartPositions();
                will(returnValue(getIndividualPositions(individuals)));
                
                allowing(ps).probabilityDynamicDecrease();
                allowing(ps).probabilityDynamicIncrease();
            }
        });
        algorithm.setProblem(esp);
        algorithm.initialize();
       
        assertCorrectOrder(algorithm, individuals, expectedOrder, distances, rs);
    }

    /**
     * Returns a list containing individuals with associated static potentials and the given distance.
     *
     * @param distance array containing the distances of the individuals
     * @return list containing as many individuals as distances
     */
    private List<Individual> getIndividuals(int[] distance) {
        List<Individual> individuals = new LinkedList<>();
        for (int d : distance) {
            Individual individual = builder.build();
            individuals.add(individual);
        }
        return individuals;
    }

    private void assertCorrectOrder(EvacuationCellularAutomatonAlgorithm algorithm,
            List<Individual> original, List<Individual> expectedOrder, int[] distance,
            EvacuationRuleSet rs) {
        LinkedList<Individual> resultList = new LinkedList<>();

        int index = 0;
        for( Individual i : original) {
            IndividualProperty ip = algorithm.getEvacuationState().propertyFor(i);
            EvacCellInterface cell = ip.getCell();
            sp = new StaticPotential();
            sp.setPotential(cell, distance[index]);
            ip.setStaticPotential(sp);
            index++;            
        }
        
        EvacuationRule rule = new EvacuationRule() {

            @Override
            public void execute(EvacCellInterface cell) {
                resultList.add(cell.getState().getIndividual());
            }

            @Override
            public boolean executableOn(EvacCellInterface cell) {
                return true;
            }

            @Override
            public void setEvacuationState(EvacuationState es) {
                
            }

            @Override
            public void setEvacuationStateController(EvacuationStateControllerInterface ec) {
                
            }

            @Override
            public void setComputation(Computation c) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        rs.add(rule);
   
        algorithm.performStep();
        
        List<Individual> callOrder = new LinkedList<>();
        for (Individual individual : in(resultList.iterator())) {
            callOrder.add(individual);
        }
        assertThat(callOrder, hasSize(expectedOrder.size()));
        assertThat(callOrder, is(equalTo(expectedOrder)));
    }

    @Test
    public void testProgress() {
        List<Individual> individuals = new LinkedList<>();
        IndividualBuilder b = new IndividualBuilder();
        individuals.add(b.build());
        individuals.add(b.build());
        final Individual dead = individuals.get(0);
        
        Individual i2 = individuals.get(1);
        EvacCell cell1 = new MockEvacCell(0, 0);
        cell1.getState().setIndividual(i2);
        
        MockEvacuationCellularAutomatonAlgorithm algorithm = new MockEvacuationCellularAutomatonAlgorithm(true) {

            @Override
            protected void initialize() {
                super.initialize();
                es.propertyFor(dead).setEvacuationTime(0);
                es.addToEvacuated(dead);
            }
            
            @Override
            protected EvacuationSimulationResult terminate() {
                return null;
            }
        };

        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        ParameterSet ps = context.mock(ParameterSet.class);
        context.checking(new Expectations() {{
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));

                allowing(esp).getRuleSet();
                will(returnValue(new TestEvacuationRuleSet.FakeEvacuationRuleSet()));
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(300));
                allowing(esp).getIndividuals();
                will(returnValue(individuals));
                allowing(esp).individualStartPositions();
                will(returnValue(getIndividualPositions(individuals)));

                allowing(esp).getParameterSet();
                will(returnValue(ps));
                allowing(ps).probabilityDynamicDecrease();
                allowing(ps).probabilityDynamicIncrease();
                allowing(eca).minPotentialFor(with(any(EvacCell.class)));
                will(returnValue(new StaticPotential()));
            }});

        Double d[] = new Double[1];
        algorithm.addAlgorithmListener(event -> {
            if (event instanceof AlgorithmProgressEvent) {
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
                setNeededTime(Integer.MAX_VALUE);
            }
        };
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);

        context.checking(new Expectations() {{
                allowing(esp).getEvacuationStepLimit();
                will(returnValue(3));
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                allowing(esp).getRuleSet();
                will(returnValue(new EvacuationRuleSet() {
                }));

                allowing(esp).getIndividuals();
                will(returnValue(Collections.EMPTY_LIST));
                allowing(esp).individualStartPositions();
                will(returnValue(Collections.EMPTY_MAP));
                allowing(esp).getParameterSet();
                will(returnValue(context.mock(ParameterSet.class)));
            }});

        algorithm.setProblem(esp);
        algorithm.runAlgorithm();

        assertThat(algorithm.getMaxSteps(), is(equalTo(3)));
        assertThat(stepCounter.get(), is(equalTo(3)));
    }
}
