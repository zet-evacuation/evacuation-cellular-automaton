package org.zet.cellularautomaton.algorithm.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellState;
import org.zet.cellularautomaton.Individual;
import org.zetool.common.util.Direction8;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestAbstractMovementRule {
    private final static Direction8 DEFAULT_DIRECTION = Direction8.Top;
    
    private class FakeEvacCell extends EvacCell {
        private final boolean isFreeNeighbors;
        private List<EvacCell> neighbors = Collections.EMPTY_LIST;
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
        public void setSpeedFactor(double speedFactor) {
        }

        @Override
        public EvacCell clone() {
            return null;
        }

        @Override
        public List<EvacCell> getFreeNeighbours() {
            if(!isFreeNeighbors) {
                throw new AssertionError("Free neighbors not set!");
            }
            return neighbors;
        }

        @Override
        public List<EvacCell> getNeighbours() {
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
        public Direction8 getRelative(EvacCell c) {
            if( c instanceof FakeEvacCell ) {
                return ((FakeEvacCell)c).direction;
            }
            return direction;
        }
        
        
        
        
        
    }
        AbstractMovementRule rule = new AbstractMovementRule() {

            @Override
            public void move(EvacCell from, EvacCell target) {
            }

            @Override
            public void swap(EvacCell cell1, EvacCell cell2) {
            }

            @Override
            protected void onExecute(EvacCell cell) {
            }
        };
    
    @Test
    public void testPossibleTargetsNeighbors() {
        Individual i = new Individual();

        EvacCell cell = new FakeEvacCell(true);
        cell.getState().setIndividual(i);
        
        assertThat(rule.computePossibleTargets(cell, true), is(Matchers.empty()));

        cell = new FakeEvacCell(false);
        cell.getState().setIndividual(i);
        
        assertThat(rule.computePossibleTargets(cell, false), is(Matchers.empty()));
    }
    
    @Test
    public void testSafeNeighbors() {
        Individual i = new Individual();
        i.setSafe(true);
        i.setDirection(DEFAULT_DIRECTION);
        
        List<EvacCell> cellList = new LinkedList<>();
        
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
        cell.getState().setIndividual(i);
        cell.neighbors = cellList;
        
        List<EvacCell> result = rule.computePossibleTargets(cell, false);
        assertThat(result, hasSize(2));
        assertThat(result, Matchers.hasItem(n2));
        assertThat(result, Matchers.hasItem(n3));
    }
    
    @Test
    public void testDirections() {
        Individual individual = new Individual();
        individual.setDirection(DEFAULT_DIRECTION);
        
        List<EvacCell> cellList = new ArrayList<>(Direction8.values().length);

        for( Direction8 dir : Direction8.values()) {
            FakeEvacCell cell = new FakeEvacCell();
            cell.direction = dir;
            cellList.add(cell);
        }
        
        FakeEvacCell cell = new FakeEvacCell();
        cell.getState().setIndividual(individual);
        cell.neighbors = cellList;
        
        List<EvacCell> result = rule.computePossibleTargets(cell, false);
        assertThat(result, hasSize(5));
        for( int i : new int[] {0, 1, 2, 6, 7}) {
            assertThat(result, Matchers.hasItem(cellList.get(i)));        
        }
    }
    
    @Test
    public void testSway() {
        Individual individual = new Individual();
        individual.setDirection(DEFAULT_DIRECTION);
        assertThat(rule.getSwayDelay(individual, Direction8.Top), is(closeTo(0, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.TopLeft), is(closeTo(0.5, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.TopRight), is(closeTo(0.5, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.Left), is(closeTo(1, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.Right), is(closeTo(1, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.DownLeft), is(closeTo(2, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.DownRight), is(closeTo(2, 10e-8)));
        assertThat(rule.getSwayDelay(individual, Direction8.Down), is(closeTo(2, 10e-8)));
    }
}
