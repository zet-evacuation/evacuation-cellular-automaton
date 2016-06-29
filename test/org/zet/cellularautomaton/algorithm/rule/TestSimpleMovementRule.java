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
import java.util.function.Function;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

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
        public EvacCellInterface selectTargetCell(EvacCellInterface cell, List<EvacCellInterface> targets) {
            counter++;
            return targetCell;
        }
        
        @Override
        protected List<EvacCellInterface> computePossibleTargets(EvacCellInterface fromCell, boolean onlyFreeNeighbours) {
            return Collections.emptyList();
        }

        @Override
        public boolean executableOn(EvacCellInterface cell) {
            return true;
        }

    };

    @Test
    public void executeableIfNotEmpty() {
        SimpleMovementRule rule = new SimpleMovementRule();
        EvacCell cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));
        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        cell.getState().setIndividual(i);
        assertThat(rule, is(executeableOn(cell)));
    }

    @Test
    public void noActionIfSameCell() {
        EvacCell testCell = new RoomCell(0, 0);
        FakeSimpleMovementRule rule = new FakeSimpleMovementRule(testCell) {

            @Override
            public void move(EvacCellInterface from, EvacCellInterface targetCell) {
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
            public void move(EvacCellInterface from, EvacCellInterface to) {
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
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        EvacCell startCell = new RoomCell(0, 0);
        EvacCell targetCell = new RoomCell(0, 0);

        context.checking(new Expectations() {
            {
                exactly(1).of(ec).move(with(startCell), with(targetCell));
            }
        });
        SimpleMovementRule rule = new SimpleMovementRule();

        rule.setEvacuationStateController(ec);
        rule.move(startCell, targetCell);

        context.assertIsSatisfied();
    }

    @Test
    public void sameCellSelectedIfEmptyTargetList() {
        List<EvacCellInterface> targets = Collections.emptyList();
        SimpleMovementRule rule = new SimpleMovementRule();
        EvacCell currentCell = new RoomCell(0, 0);

        EvacCellInterface selectedCell = rule.selectTargetCell(currentCell, targets);

        assertThat(selectedCell, is(same(currentCell)));
    }

    @Test
    public void singleCellSelected() {
        SimpleMovementRule rule = new SimpleMovementRule();
        EvacCellInterface currentCell = new RoomCell(0, 0);
        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        currentCell.getState().setIndividual(i);                
        EvacCell targetCell = new RoomCell(0, 0);
        List<EvacCellInterface> targets = Collections.singletonList(targetCell);

        Mockery context = new Mockery();
        EvacuationState es = context.mock(EvacuationState.class);
        Computation c = context.mock(Computation.class);
        EvacuationCellularAutomatonInterface ca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(ca));
                allowing(c).effectivePotential(with(i), with(targetCell), with(any(Function.class)));
                will(returnValue(1.0));
            }
        });
        rule.setEvacuationState(es);
        rule.setComputation(c);

        EvacCellInterface selectedCell = rule.selectTargetCell(currentCell, targets);

        assertThat(selectedCell, is(same(targetCell)));
    }
}
