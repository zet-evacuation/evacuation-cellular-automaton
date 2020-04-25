package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellState;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.results.MoveAction;
import org.zet.cellularautomaton.results.SwapAction;
import org.zetool.common.util.Direction8;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AbstractMovementRuleTest {
    private final static Direction8 DEFAULT_DIRECTION = Direction8.Top;
    private final static IndividualBuilder INDIVIDUAL_BUILDER = new IndividualBuilder();
    private Mockery context = new Mockery();
    private Individual individual;
    private FakeIndividualProperty ip;
    private AbstractMovementRule rule;

    private static class FakeEvacCell extends EvacCell {
        private final boolean isFreeNeighbors;
        private List<EvacCellInterface> neighbors = Collections.emptyList();
        boolean safe;
        Direction8 direction = DEFAULT_DIRECTION;
        public FakeEvacCell() {
            this(false);
        }
        public FakeEvacCell(boolean freeNeighbors) {
            super(new EvacuationCellState(null), 1, 0, 0);
            this.isFreeNeighbors = freeNeighbors;
        }

        @Override
        public EvacCell clone() {
            return null;
        }

        @Override
        public List<EvacCellInterface> getFreeNeighbours() {
            if(!isFreeNeighbors) {
                throw new AssertionError("Free neighbors not set!");
            }
            return neighbors;
        }

        @Override
        public List<EvacCellInterface> getNeighbours() {
            if(isFreeNeighbors) {
                throw new AssertionError("Free neighbors set!");
            }
            return neighbors;
        }

        @Override
        public boolean isSafe() {
            return safe;
        }
        
        @Override
        public Direction8 getRelative(EvacCellInterface c) {
            if( c instanceof FakeEvacCell ) {
                return ((FakeEvacCell)c).direction;
            }
            return direction;
        }
    }

    private static class FakeIndividualProperty extends IndividualProperty {

        boolean safe = false;

        public FakeIndividualProperty(Individual i) {
            super(i);
        }

        @Override
        public boolean isSafe() {
            return safe;
        }

    }
    
    @Before
    public void initRule() {
        rule = new AbstractMovementRule() {

            @Override
            public MoveAction move(EvacCellInterface from, EvacCellInterface target) {
                return MoveAction.NO_MOVE;
            }

            @Override
            public SwapAction swap(EvacCellInterface cell1, EvacCellInterface cell2) {
                return SwapAction.NO_MOVE;
            }

            @Override
            protected MoveAction onExecute(EvacCellInterface cell) {
                return MoveAction.NO_MOVE;
            }
        };
        EvacuationState es = context.mock(EvacuationState.class);
        individual = INDIVIDUAL_BUILDER.build();
        ip = new FakeIndividualProperty(individual);
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(with(individual));
                will(returnValue(ip));
            }
        });
        rule.setEvacuationState(es);
    }
        
    @Test
    public void testPossibleTargetsNeighbors() {
        EvacCell cell = new FakeEvacCell(true);
        cell.getState().setIndividual(individual);
        
        assertThat(rule.computePossibleTargets(cell, true), is(empty()));

        cell = new FakeEvacCell(false);
        cell.getState().setIndividual(individual);
        
        assertThat(rule.computePossibleTargets(cell, false), is(empty()));
    }
    
    @Test
    public void testSafeNeighbors() {
        ip.setDirection(DEFAULT_DIRECTION);
        ip.safe = true;
        
        List<EvacCellInterface> cellList = new LinkedList<>();
        
        EvacCell n1 = new FakeEvacCell();
        cellList.add(n1);
        FakeEvacCell n2 = new FakeEvacCell();
        n2.safe = true;
        cellList.add(n2);
        FakeEvacCell n3 = new FakeEvacCell();
        n3.safe = true;
        cellList.add(n3);
        EvacCell n4 = new FakeEvacCell();
        cellList.add(n4);
        
        FakeEvacCell cell = new FakeEvacCell();
        cell.getState().setIndividual(individual);
        cell.neighbors = cellList;
        
        List<EvacCellInterface> result = rule.computePossibleTargets(cell, false);
        assertThat(result, hasSize(2));
        assertThat(result, Matchers.hasItem(n2));
        assertThat(result, Matchers.hasItem(n3));
    }
    
    @Test
    public void testDirections() {
        ip.setDirection(DEFAULT_DIRECTION);
        
        List<EvacCellInterface> cellList = new ArrayList<>(Direction8.values().length);

        for( Direction8 dir : Direction8.values()) {
            FakeEvacCell cell = new FakeEvacCell();
            cell.direction = dir;
            cellList.add(cell);
        }
        
        FakeEvacCell cell = new FakeEvacCell();
        cell.getState().setIndividual(individual);
        cell.neighbors = cellList;


        List<EvacCellInterface> result = rule.computePossibleTargets(cell, false);
        assertThat(result, hasSize(5));
        for( int i : new int[] {0, 1, 2, 6, 7}) {
            assertThat(result, Matchers.hasItem(cellList.get(i)));        
        }
    }
    
    @Test
    public void testTargetSelection() {
        List<EvacCellInterface> cells = new LinkedList<>();
        FakeEvacCell cell1 = new FakeEvacCell();
        FakeEvacCell cell2 = new FakeEvacCell();
        cells.add(cell1);
        cells.add(cell2);
        
        EvacCellInterface result = rule.selectTargetCell(new FakeEvacCell(), cells);
        assertThat(result, is(equalTo(cell1)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTargetSelectionRequiresNonEmptyList() {
        rule.selectTargetCell(new FakeEvacCell(), Collections.emptyList());
    }
    
    @Test
    public void testSway() {
        ip.setDirection(DEFAULT_DIRECTION);
        assertThat(rule.getSwayDelay(individual, Direction8.Top), is(closeTo(0, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.TopLeft), is(closeTo(0.5, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.TopRight), is(closeTo(0.5, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.Left), is(closeTo(1, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.Right), is(closeTo(1, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.DownLeft), is(closeTo(2, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.DownRight), is(closeTo(2, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.Down), is(closeTo(2, 10e-8)));
    }
    
    @Test
    public void testStepEndTime() {
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        MultiFloorEvacuationCellularAutomaton eca = new MultiFloorEvacuationCellularAutomaton();
        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
            }
        });        
        
        rule.setStepEndTime(individual, 2.68);

        assertThat(ip.getStepEndTime(), is(closeTo(2.68, 10e-8)));
    }
    
    @Test
    public void testInitialization() {
        assertThat(rule.isDirectExecute(), is(true));
        assertThat(rule.isMoveCompleted(), is(false));
        
    }
    
    @Test
    public void testSimpleGetters() {
        rule.setMoveRuleCompleted(true);
        rule.setDirectExecute(false);
        assertThat(rule.isMoveCompleted(), is(true));
        assertThat(rule.isDirectExecute(), is(false));
    }
    
}
