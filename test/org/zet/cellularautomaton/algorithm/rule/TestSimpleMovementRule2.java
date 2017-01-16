package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.same;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.List;
import java.util.Collections;
import java.util.function.Function;
import static org.hamcrest.Matchers.closeTo;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestSimpleMovementRule2 {

    private static class FakeSimpleMovementRule2 extends SimpleMovementRule2 {
        private final EvacCell targetCell;
        int counter = 0;

        public FakeSimpleMovementRule2(EvacCell targetCell) {
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
        Mockery context = new Mockery();

        Room room = context.mock(Room.class);

        context.checking(new Expectations() {
            {
                allowing(room).getID();
                will(returnValue(1));
            }
        });
        EvacCell testCell = new RoomCell(1, 0, 0, room);
        
        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        testCell.getState().setIndividual(i);
        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(testCell) {

            @Override
            public void move(EvacCellInterface from, EvacCellInterface targetCell) {
                throw new AssertionError("Move should not be called!");
            }
        };
        
        EvacuationState es = context.mock(EvacuationState.class);
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        IndividualProperty ip = new IndividualProperty(i);
        ip.setCell(testCell);
        ip.setStepEndTime(0);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(ip));
                allowing(ec).move(testCell, testCell);
                allowing(room).getID();
                will(returnValue(1));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).getTimeStep();
                will(returnValue(4));
                allowing(room).existsCellAt(with(any(Integer.class)), with(any(Integer.class)));
                
                will(returnValue(false));
            }
        });
        assertThat(ip.isAlarmed(), is(false));
        
        rule.setEvacuationState(es);
        rule.setEvacuationStateController(ec);

        
        rule.execute(testCell);
        assertThat(rule.counter, is(equalTo(0)));
        
        assertThat(ip.getStepStartTime(), is(closeTo(0.0, 0.0001)));
        assertThat(ip.getStepEndTime(), is(closeTo(1.0, 0.0001)));
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
