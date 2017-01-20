package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.List;
import java.util.Collections;
import java.util.function.Function;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
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
import org.zetool.common.util.Direction8;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestSimpleMovementRule2 {

    private static class FakeSimpleMovementRule2 extends SimpleMovementRule2 {

        private final EvacCell targetCell;
        int counter = 0;
        private final Direction8 direction;

        public FakeSimpleMovementRule2(EvacCell targetCell) {
            this(targetCell, null);
        }

        public FakeSimpleMovementRule2(EvacCell targetCell, Direction8 direction8) {
            this.targetCell = targetCell;
            this.direction = direction8;
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

        @Override
        protected Direction8 getDirection() {
            return direction != null ? direction : super.getDirection();
        }
    };

    private final int TIME_STEP = 13;
    private final int STEP_END_TIME = 5;

    private Mockery context = new Mockery();
    private EvacCell testCell;
    private Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
    private Room room;
    private EvacuationState es;
    private EvacuationStateControllerInterface ec;
    private IndividualProperty ip;

    @Before
    public void initState() {
        room = context.mock(Room.class);
        es = context.mock(EvacuationState.class);
        ec = context.mock(EvacuationStateControllerInterface.class);
        ip = new IndividualProperty(i);

        context.checking(new Expectations() {
            {
                allowing(room).getID();
                will(returnValue(1));
                allowing(es).propertyFor(i);
                will(returnValue(ip));
                allowing(es).getTimeStep();
                will(returnValue(TIME_STEP));
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
        testCell = new RoomCell(1, 0, 0, room);
        testCell.getState().setIndividual(i);
        ip.setCell(testCell);
        ip.setStepEndTime(STEP_END_TIME);

    }

    @Test
    public void executeableIfNotEmpty() {
        SimpleMovementRule2 rule = new SimpleMovementRule2();

        // Not excuteable when empty
        assertThat(rule, is(not(executeableOn(new RoomCell(0, 0)))));

        // Executeable when filled
        assertThat(rule, is(executeableOn(testCell)));
    }

    @Test
    public void noMoveIfNotAlarmed() {

        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(testCell) {

            @Override
            public void move(EvacCellInterface from, EvacCellInterface targetCell) {
                throw new AssertionError("Move should not be called!");
            }
        };

        context.checking(new Expectations() {
            {
                allowing(ec).move(testCell, testCell);

                // No cells
                allowing(room).existsCellAt(with(any(Integer.class)), with(any(Integer.class)));
                will(returnValue(false));
            }
        });

        assertThat(ip.isAlarmed(), is(false));

        rule.setEvacuationState(es);
        rule.setEvacuationStateController(ec);

        rule.execute(testCell);
        assertThat(rule.counter, is(equalTo(0)));

        // Assert that one time step is used for not doing anything
        assertThat(ip.getStepStartTime(), is(closeTo(STEP_END_TIME, 0.0001)));
        assertThat(ip.getStepEndTime(), is(closeTo(STEP_END_TIME + 1, 0.0001)));
    }

    /**
     * Asserts that an individual is not moving when the last move is not yet finished
     */
    @Test
    public void noMoveWhenLastMoveOngoing() {
        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(testCell) {

            @Override
            public void move(EvacCellInterface from, EvacCellInterface targetCell) {
                throw new AssertionError("Move should not be called!");
            }
        };

        ip.setCell(testCell);
        final double stepStartTime = TIME_STEP - 0.8;
        final double stepEndTime = TIME_STEP + 0.8;

        ip.setStepEndTime(stepEndTime);
        ip.setStepStartTime(stepStartTime);
        ip.setAlarmed();

        context.checking(new Expectations() {
            {
                never(ec).move(with(any(EvacCell.class)), with(any(EvacCell.class)));

                allowing(room).existsCellAt(with(any(Integer.class)), with(any(Integer.class)));
                will(returnValue(false));
            }
        });

        rule.setEvacuationState(es);
        rule.setEvacuationStateController(ec);

        rule.execute(testCell);
        assertThat(rule.counter, is(equalTo(0)));

        // Assert that step start and end times have not been modified
        assertThat(Double.doubleToRawLongBits(stepStartTime), is(equalTo(Double.doubleToRawLongBits(ip.getStepStartTime()))));
        assertThat(Double.doubleToRawLongBits(ip.getStepEndTime()), is(equalTo(Double.doubleToRawLongBits(stepEndTime))));
    }

    @Test
    public void moveCallsCellularAutomaton() {
        EvacCell targetCell = new RoomCell(0, 0);

        context.checking(new Expectations() {
            {
                exactly(1).of(ec).move(with(testCell), with(targetCell));
            }
        });
        SimpleMovementRule rule = new SimpleMovementRule();

        rule.setEvacuationStateController(ec);
        rule.move(testCell, targetCell);

        context.assertIsSatisfied();
    }

    @Test
    public void sameCellSelectedIfEmptyTargetList() {
        List<EvacCellInterface> targets = Collections.emptyList();
        SimpleMovementRule rule = new SimpleMovementRule();

        EvacCellInterface selectedCell = rule.selectTargetCell(testCell, targets);

        assertThat(selectedCell, is(same(testCell)));
    }

    @Test
    public void singleCellSelected() {
        SimpleMovementRule2 rule = new SimpleMovementRule2() {
            @Override
            protected void noMove() {
                // Nothing
            }
        };

        List<EvacCellInterface> targets = Collections.singletonList(testCell);

        Computation c = context.mock(Computation.class);
        EvacuationCellularAutomatonInterface ca = context.mock(EvacuationCellularAutomatonInterface.class);
        context.checking(new Expectations() {
            {
                allowing(c).effectivePotential(with(i), with(testCell), with(any(Function.class)));
                will(returnValue(1.0));
            }
        });
        rule.setEvacuationState(es);
        rule.setComputation(c);
        ip.setAlarmed();

        rule.execute(testCell);
        EvacCellInterface selectedCell = rule.selectTargetCell(testCell, targets);

        assertThat(selectedCell, is(same(testCell)));
    }
}
