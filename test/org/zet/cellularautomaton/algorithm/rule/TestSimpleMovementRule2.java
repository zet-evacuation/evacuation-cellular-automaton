package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.onConsecutiveCalls;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellState;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.Stairs;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;
import org.zetool.common.util.Direction8;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.generators.GeneralRandom;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestSimpleMovementRule2 {

    private static interface StairCellMock extends EvacCellInterface, Stairs {
    }

    private static class FakeSimpleMovementRule2 extends SimpleMovementRule2 {

        private final EvacCellInterface targetCell;
        int counter = 0;
        private final Direction8 direction;

        public FakeSimpleMovementRule2(EvacCellInterface targetCell) {
            this(targetCell, null);
        }

        public FakeSimpleMovementRule2(EvacCellInterface targetCell, Direction8 direction8) {
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
    private EvacCellInterface testCell;
    private Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
    private Room room;
    private EvacuationState es;
    private EvacuationStateControllerInterface ec;
    private IndividualProperty ip;
    private EvacuationCellularAutomatonInterface ca;
    private static final double STEPS_PER_SECOND = 10.0;

    @Before
    public void initState() {
        room = context.mock(Room.class);
        es = context.mock(EvacuationState.class);
        ec = context.mock(EvacuationStateControllerInterface.class);
        ip = new IndividualProperty(i);
        testCell = context.mock(EvacCellInterface.class);
        ca = context.mock(EvacuationCellularAutomatonInterface.class);

        context.checking(new Expectations() {
            {
                allowing(testCell).getState();
                will(returnValue(new EvacuationCellState(i)));
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
                allowing(es).getCellularAutomaton();
                will(returnValue(ca));
                allowing(ca).getStepsPerSecond();
                will(returnValue(STEPS_PER_SECOND));
                allowing(ca).getSecondsPerStep();
                will(returnValue(1 / STEPS_PER_SECOND));
            }
        });
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
    public void notExecutableOnExitCells() {
        SimpleMovementRule2 rule = new SimpleMovementRule2();
        
        ExitCell exitCell = new ExitCell(0, 0);
        assertThat(rule, is(not(executeableOn(exitCell))));
    }

    @Test
    public void noMoveIfNotAlarmed() {

        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(testCell, Direction8.Top) {

            @Override
            public void move(EvacCellInterface from, EvacCellInterface targetCell) {
                throw new AssertionError("Move should not be called!");
            }
        };

        context.checking(new Expectations() {
            {
                allowing(ec).move(testCell, testCell);
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
        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(testCell, Direction8.Top) {

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
    public void normalMove() {
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "normalMoveTarget");
        assertMovePaarameters(targetCell, 0.7, 3.5, 2.3, Direction8.Top, Direction8.Right, 90, 1.0);
    }

    @Test
    public void normalMoveDiagonal() {
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "normalMoveTargetDiagonal");
        assertMovePaarameters(targetCell, 0.7, 3.5, 2.3, Direction8.Top, Direction8.TopRight, 45, 1.0);
    }

    @Test(expected = IllegalStateException.class)
    public void individualNeedsPositiveSpeed() {
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "failNoSpeedTarget");
        double arbitraryValue = 1.0;
        assertMovePaarameters(targetCell, arbitraryValue, 0.0, arbitraryValue, Direction8.Top, Direction8.Right, 90, 1.0);
    }

    @Test
    public void moveOnStairs() {
        StairCellMock targetCell = context.mock(StairCellMock.class);
        double stairSpeedFactor = 4.0;
        context.checking(new Expectations() {
            {
                allowing(targetCell).getStairSpeedFactor(Direction8.Right);
                will(returnValue(stairSpeedFactor));
            }
        });
        assertMovePaarameters(targetCell, 0.7, 3.5, 2.3, Direction8.Top, Direction8.Right, 90, stairSpeedFactor * 1.1);
    }

    /**
     * Moving on cells does not involve factors and no distance.
     */
    @Test
    public void moveDoors() {
        DoorCell doorStart = new DoorCell(1, 2);
        // Directly above
        DoorCell doorTarget = new DoorCell(1, 3);
        double speedFactor = 0.2;
        doorTarget.setSpeedFactor(speedFactor);
        
        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(doorTarget, Direction8.Top) {

            @Override
            protected List<EvacCellInterface> computePossibleTargets(EvacCellInterface fromCell, boolean onlyFreeNeighbours) {
                if (onlyFreeNeighbours != true) {
                    throw new AssertionError("Only free neighbours are available");
                }
                return Collections.singletonList(doorTarget);
            }
        };
        ip.setCell(doorStart);
        ip.setAlarmed();
        final double stepEndTime = TIME_STEP - 0.8;
        ip.setStepEndTime(stepEndTime);
        doorStart.getState().setIndividual(i);

        Direction8 startDirection = Direction8.Top;
        Direction8 newDirection = Direction8.Top;
        double arbitraryRelativeSpeed = 0.3;
        double arbitraryButPositiveAbsoluteSpeed = 3.0;

        context.checking(new Expectations() {
            {
                // Assert that the move is actually called within the controller
                oneOf(ec).move(with(doorStart), with(doorTarget));

                // Assert that the dynamic potential for target Cell is increased
                oneOf(ec).increaseDynamicPotential(with(doorTarget));

                allowing(ca).absoluteSpeed(arbitraryRelativeSpeed);
                will(returnValue(arbitraryButPositiveAbsoluteSpeed));

                allowing(testCell).getOccupiedUntil();
                will(returnValue(3.0));

                allowing(testCell).setOccupiedUntil(with(any(Double.class)));
            }
        });

        ip.setDirection(startDirection);
        ip.setRelativeSpeed(arbitraryRelativeSpeed);

        rule.setEvacuationState(es);
        rule.setEvacuationStateController(ec);
        
        rule.execute(doorStart);

        // The actual distance is 0 for door cells in the current implementation (because cells are in different rooms)
        // Additional factor of infinity leads to the desired result of time to move = 0
        assertSMoveResults(rule, arbitraryButPositiveAbsoluteSpeed, speedFactor, newDirection, 0, Double.POSITIVE_INFINITY, stepEndTime);
    }

    /**
     * Checks that properties of individuals are set correctly during move depending on some
     * parameters.
     */
    private void assertMovePaarameters(EvacCellInterface targetCell, double relativeSpeed, double absoluteSpeed,
            double speedFactor, Direction8 startDirection, Direction8 newDirection, int degree, double additionalSpeedFactor) {

        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(targetCell, Direction8.DownLeft) {

            @Override
            protected List<EvacCellInterface> computePossibleTargets(EvacCellInterface fromCell, boolean onlyFreeNeighbours) {
                if (onlyFreeNeighbours != true) {
                    throw new AssertionError("Only free neighbours are available");
                }
                return Collections.singletonList(targetCell);
            }
        };

        ip.setAlarmed();

        final double stepEndTime = TIME_STEP - 0.8;

        ip.setStepEndTime(stepEndTime);

        context.checking(new Expectations() {
            {
                // Assert that the move is actually called within the controller
                oneOf(ec).move(with(testCell), with(targetCell));

                // Assert that the dynamic potential for target Cell is increased
                oneOf(ec).increaseDynamicPotential(with(targetCell));

                allowing(testCell).getRelative(targetCell);
                will(returnValue(newDirection));

                allowing(ca).absoluteSpeed(relativeSpeed);
                will(returnValue(absoluteSpeed));

                allowing(targetCell).getSpeedFactor();
                will(returnValue(speedFactor));

                // todo check.
                allowing(testCell).getOccupiedUntil();
                will(returnValue(3.0));

                allowing(testCell).setOccupiedUntil(with(any(Double.class)));
            }
        });
        ip.setDirection(startDirection);
        ip.setRelativeSpeed(relativeSpeed);

        rule.setEvacuationState(es);
        rule.setEvacuationStateController(ec);

        rule.execute(testCell);

        assertSMoveResults(rule, absoluteSpeed, speedFactor, newDirection, degree, additionalSpeedFactor, stepEndTime);
    }
    
    private void assertSMoveResults(FakeSimpleMovementRule2 rule, double absoluteSpeed, double speedFactor,
            Direction8 newDirection, int degree, double additionalSpeedFactor, double stepEndTime) {
        double stepLength = degree == 0 || degree == 90 ? 0.4 : Math.sqrt(0.4 * 0.4 + 0.4 * 0.4);

        // Is that correct?
        assertThat(rule.isMoveCompleted(), is(false));

        // Assertions
        // Move called
        assertThat(rule.counter, is(equalTo(1)));

        // New view direction
        assertThat(ip.getDirection(), is(equalTo(newDirection)));

        // Step start and end time
        final double sway = swayFromDegree(degree);
        final double timeToWalk = (stepLength /* dist */ / (absoluteSpeed * speedFactor * additionalSpeedFactor));
        double expectedStepEndTime = stepEndTime + timeToWalk * STEPS_PER_SECOND + sway * STEPS_PER_SECOND;
        assertThat(ip.getStepEndTime(), is(closeTo(expectedStepEndTime, 10e-6)));
//            setStepEndTime(individual, es.propertyFor(individual).getStepEndTime() + (dist / speed) * es.getCellularAutomaton().getStepsPerSecond() + 0);
        
    }

    private double swayFromDegree(int degree) {
        switch (degree) {
            case 0:
                return 0;
            case 45:
                return 0.5;
            case 90:
                return 1;
            case 135:
                return 2;
        }
        throw new AssertionError();
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
    // Map of the possible directions for each current view direction
    static final Map<Direction8, Direction8[]> POSSIBLE_DIRECTIONS = new EnumMap<>(Direction8.class);

    static {
        POSSIBLE_DIRECTIONS.put(Direction8.Top, new Direction8[]{Direction8.Left, Direction8.TopLeft, Direction8.Top, Direction8.TopRight, Direction8.Right});
        POSSIBLE_DIRECTIONS.put(Direction8.TopRight, new Direction8[]{Direction8.TopLeft, Direction8.Top, Direction8.TopRight, Direction8.Right, Direction8.DownRight});
        POSSIBLE_DIRECTIONS.put(Direction8.Right, new Direction8[]{Direction8.Top, Direction8.TopRight, Direction8.Right, Direction8.DownRight, Direction8.Down});
        POSSIBLE_DIRECTIONS.put(Direction8.DownRight, new Direction8[]{Direction8.TopRight, Direction8.Right, Direction8.DownRight, Direction8.Down, Direction8.DownLeft});
        POSSIBLE_DIRECTIONS.put(Direction8.Down, new Direction8[]{Direction8.Right, Direction8.DownRight, Direction8.Down, Direction8.DownLeft, Direction8.Left});
        POSSIBLE_DIRECTIONS.put(Direction8.DownLeft, new Direction8[]{Direction8.DownRight, Direction8.Down, Direction8.DownLeft, Direction8.Left, Direction8.TopLeft});
        POSSIBLE_DIRECTIONS.put(Direction8.Left, new Direction8[]{Direction8.Down, Direction8.DownLeft, Direction8.Left, Direction8.TopLeft, Direction8.Top});
        POSSIBLE_DIRECTIONS.put(Direction8.TopLeft, new Direction8[]{Direction8.DownLeft, Direction8.Left, Direction8.TopLeft, Direction8.Top, Direction8.TopRight});
    }

    /**
     * When no of the five possible neighbors of the current direction is available, individual
     * turns randomly.
     */
    @Test
    public void newViewDirection() {
        context.checking(new Expectations() {
            {
                allowing(testCell).getNeighbor(with(any(Direction8.class)));
                will(returnValue(null));
            }
        });

        SimpleMovementRule2 rule = initRuleForDirectionTest();
        for (Direction8 current : POSSIBLE_DIRECTIONS.keySet()) {
            Set<Direction8> s = get(rule, current);
            assertThat(s, containsInAnyOrder(POSSIBLE_DIRECTIONS.get(current)));
        }
    }

    @Test
    public void newViewDirectionOccupied() {
        SimpleMovementRule2 rule = initRuleForDirectionTest();

        EvacCellInterface occupiedNeighbor = context.mock(EvacCellInterface.class, "evc");

        context.checking(new Expectations() {
            {
                allowing(occupiedNeighbor).isOccupied();
                will(returnValue(true));
                allowing(testCell).getNeighbor(with(any(Direction8.class)));
                will(returnValue(occupiedNeighbor));
            }
        });

        for (Direction8 current : POSSIBLE_DIRECTIONS.keySet()) {
            Set<Direction8> s = get(rule, current);
            assertThat(s, containsInAnyOrder(POSSIBLE_DIRECTIONS.get(current)));
        }
    }

    @Test
    public void newViewDirectionMinimum() {
        SimpleMovementRule2 rule = initRuleForDirectionTest();

        context.checking(new Expectations() {
            {
                allowing(room).existsCellAt(with(any(Integer.class)), with(any(Integer.class)));
                will(returnValue(true));
            }
        });

        for (Direction8 current : POSSIBLE_DIRECTIONS.keySet()) {
            Direction8 s = get2(rule, current);

            Direction8 minimumByOrdinal = Arrays.stream(POSSIBLE_DIRECTIONS.get(current)).min(Comparator.comparing(Direction8::ordinal)).get();

            assertThat(s, is(equalTo(minimumByOrdinal)));
        }
    }

    private Direction8 get2(SimpleMovementRule2 rule, Direction8 current) {
        ip.setDirection(current);

        StaticPotential sp = new StaticPotential();
        ip.setStaticPotential(sp);
        context.checking(new Expectations() {
            {
                Direction8[] directions = POSSIBLE_DIRECTIONS.get(current);

                for (int i = 0; i < directions.length; ++i) {
                    EvacCellInterface freeNeighbor = context.mock(EvacCellInterface.class, current.toString() + "->" + directions[i].toString());
                    oneOf(testCell).getNeighbor(with(directions[i]));
                    will(returnValue(freeNeighbor));
                    allowing(freeNeighbor).isOccupied();
                    will(returnValue(false));
                    sp.setPotential(freeNeighbor, directions[i].ordinal());
                }
            }
        });

        return rule.getDirection();
    }

    private SimpleMovementRule2 initRuleForDirectionTest() {
        GeneralRandom r = context.mock(GeneralRandom.class);
        (RandomUtils.getInstance()).setRandomGenerator(r);

        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(testCell) {
            @Override
            protected void noMove() {
                // Nothing
            }
        };

        rule.setEvacuationState(es);

        // Prepare the individual reference
        rule.execute(testCell);

        int availableDirections = 5;
        int directions = Direction8.values().length;
        context.checking(new Expectations() {
            {
                allowing(r).nextInt(availableDirections);
                Action[] actions = new Action[availableDirections * directions];
                for (int i = 0; i < directions; ++i) {
                    for (int randomInt = 0; randomInt < availableDirections; ++randomInt) {
                        actions[i * availableDirections + randomInt] = returnValue(randomInt);
                    }
                }
                will(onConsecutiveCalls(actions));
            }
        });

        return rule;
    }

    private Set<Direction8> get(SimpleMovementRule2 rule, Direction8 current) {
        ip.setDirection(current);

        EnumSet<Direction8> ret = EnumSet.noneOf(Direction8.class);
        for (int i = 0; i < 5; ++i) {
            ret.add(rule.getDirection());
        }
        return ret;
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

        Computation c = context.mock(Computation.class);
        context.checking(new Expectations() {
            {
                allowing(c).effectivePotential(with(i), with(testCell), with(any(Function.class)));
                will(returnValue(1.0));

                allowing(testCell).getFreeNeighbours();
                will(returnValue(Collections.emptyList()));
            }
        });
        rule.setEvacuationState(es);
        rule.setComputation(c);
        ip.setAlarmed();

        rule.execute(testCell);
        EvacCellInterface selectedCell = rule.selectTargetCell(testCell, Collections.singletonList(testCell));

        assertThat(selectedCell, is(same(testCell)));
    }
}
