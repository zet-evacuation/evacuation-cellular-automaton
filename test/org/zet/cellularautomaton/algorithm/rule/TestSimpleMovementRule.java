package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.List;
import java.util.Collections;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestSimpleMovementRule {

    private static class FakeSimpleMovementRule extends SimpleMovementRule {
        private final EvacCell targetCell;
        int counter = 0;

        public FakeSimpleMovementRule(EvacCell targetCell) {
            this.targetCell = targetCell;
        }
        
        @Override
        public EvacCell selectTargetCell(EvacCell cell, List<EvacCell> targets) {
            counter++;
            return targetCell;
        }
        
        @Override
        protected List<EvacCell> computePossibleTargets(EvacCell fromCell, boolean onlyFreeNeighbours) {
            return Collections.emptyList();
        }

        @Override
        public boolean executableOn(EvacCell cell) {
            return true;
        }

    };

    @Test
    public void executeableIfNotEmpty() {
        SimpleMovementRule rule = new SimpleMovementRule();
        EvacCell cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));
        Individual i = new Individual();
        cell.getState().setIndividual(i);
        i.setCell(cell);
        assertThat(rule, is(executeableOn(cell)));
    }

    @Test
    public void noActionIfSameCell() {
        EvacCell testCell = new RoomCell(0, 0);
        FakeSimpleMovementRule rule = new FakeSimpleMovementRule(testCell) {

            @Override
            public void move(EvacCell from, EvacCell targetCell) {
                throw new AssertionError("Move should not be called!");
            }
        };
        rule.execute(testCell);
        assertThat(rule.counter, is(equalTo(1)));
    }

    @Test
    public void moveExecuted() {
        EvacCell startCell = new RoomCell(0, 0);
        EvacCell expectedTargetCell = new RoomCell(0, 0);
        FakeSimpleMovementRule rule = new FakeSimpleMovementRule(expectedTargetCell) {

            @Override
            public void move(EvacCell from, EvacCell to) {
                assertThat(from, is(same(startCell)));
                assertThat(to, is(same(expectedTargetCell)));
            }
        };
        rule.execute(startCell);
        assertThat(rule.counter, is(equalTo(1)));
    }

    @Test
    public void moveCallsCellularAutomaton() {
        Mockery context = new Mockery();
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        EvacCell startCell = new RoomCell(0, 0);
        EvacCell targetCell = new RoomCell(0, 0);

        context.checking(new Expectations() {
            {
                allowing(esp).getCellularAutomaton();
                will(returnValue(eca));
                exactly(1).of(eca).moveIndividual(with(startCell), with(targetCell));
            }
        });
        SimpleMovementRule rule = new SimpleMovementRule();
        rule.setEvacuationSimulationProblem(esp);

        rule.move(startCell, targetCell);

        context.assertIsSatisfied();
    }

    @Test
    public void sameCellSelectedIfEmptyTargetList() {
        List<EvacCell> targets = Collections.emptyList();
        SimpleMovementRule rule = new SimpleMovementRule();
        EvacCell currentCell = new RoomCell(0, 0);

        EvacCell selectedCell = rule.selectTargetCell(currentCell, targets);

        assertThat(selectedCell, is(same(currentCell)));
    }

    @Test
    public void singleCellSelected() {
        SimpleMovementRule rule = new SimpleMovementRule();
        EvacCell currentCell = new RoomCell(0, 0);
        EvacCell targetCell = new RoomCell(0, 0);
        List<EvacCell> targets = Collections.singletonList(targetCell);

        Mockery context = new Mockery();
        EvacuationSimulationProblem esp = context.mock(EvacuationSimulationProblem.class);
        ParameterSet ps = context.mock(ParameterSet.class);
        context.checking(new Expectations() {
            {
                allowing(esp).getParameterSet();
                will(returnValue(ps));
                allowing(ps).effectivePotential(currentCell, targetCell);
                will(returnValue(1.0));
            }
        });
        rule.setEvacuationSimulationProblem(esp);

        EvacCell selectedCell = rule.selectTargetCell(currentCell, targets);

        assertThat(selectedCell, is(same(targetCell)));
    }
}
