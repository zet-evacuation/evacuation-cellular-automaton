package org.zet.cellularautomaton.algorithm.rule;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellState;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.Stairs;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import static org.zet.cellularautomaton.algorithm.rule.MovementRuleTestHelper.ABSOLUTE_SPEED;
import static org.zet.cellularautomaton.algorithm.rule.MovementRuleTestHelper.OCCUPIED_UNTIL;
import static org.zet.cellularautomaton.algorithm.rule.MovementRuleTestHelper.RELATIVE_SPEED;
import static org.zet.cellularautomaton.algorithm.rule.MovementRuleTestHelper.SPEED_FACTOR_TARGET_CELL;
import static org.zet.cellularautomaton.algorithm.rule.MovementRuleTestHelper.STEPS_PER_SECOND;
import static org.zet.cellularautomaton.algorithm.rule.MovementRuleTestHelper.STEP_END_TIME_CAN_MOVE;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.results.MoveAction;
import org.zetool.common.util.Direction8;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.generators.GeneralRandom;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SimpleMovementRule2Test {

    private Mockery context = new Mockery();

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

    MovementRuleTestHelper helper;

    @Before
    public void initState() {
        helper = new MovementRuleTestHelper(context);
    }

    @Test
    public void notExecutable() {
        SimpleMovementRule2 rule = new SimpleMovementRule2();

        helper.assertThatNotExecutableOnExitCells(rule);
        helper.assertThatNotExecuteableIfEmpty(rule);
        helper.executeableIfNotEmpty(rule);
    }

    @Test
    public void notAlarmed() {
        helper.test(new SimpleMovementRule2Spy(), MovementRuleTestHelper.MovementRuleStep.REMAIN_INACTIVE);
    }

    @Test
    public void moving() {
        helper.test(new SimpleMovementRule2Spy(), MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);
    }

    @Test
    public void currentlyMoving() {
        helper.test(new SimpleMovementRule2Spy(), MovementRuleTestHelper.MovementRuleStep.CURRENTLY_MOVING);
    }

    @Test
    public void noMoveIfNotAlarmed() {
        FakeSimpleMovementRule2 rule = noMoveRule();
        context.checking(new Expectations() {
            {
                allowing(helper.getEc()).move(helper.getTestCell(), helper.getTestCell());
            }
        });

        helper.prepareFor(rule, MovementRuleTestHelper.MovementRuleStep.REMAIN_INACTIVE);

        rule.execute(helper.getTestCell());
        assertThat(rule.counter, is(equalTo(0)));

        helper.assertDefaultResults(MovementRuleTestHelper.MovementRuleStep.REMAIN_INACTIVE);
    }

    /**
     * Asserts that an individual is not moving when the last move is not yet finished
     */
    @Test
    public void noMoveWhenLastMoveOngoing() {
        FakeSimpleMovementRule2 rule = noMoveRule();

        helper.prepareFor(rule, MovementRuleTestHelper.MovementRuleStep.CURRENTLY_MOVING);

        rule.execute(helper.getTestCell());
        assertThat(rule.counter, is(equalTo(0)));

        helper.assertDefaultResults(MovementRuleTestHelper.MovementRuleStep.CURRENTLY_MOVING);
    }

    @Test
    public void normalMove() {
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "normalMoveTarget");
        FakeSimpleMovementRule2 ruleUnderTest = getMovementRule(targetCell);

        helper.injectTargetCell(targetCell, Direction8.Right);

        helper.prepareFor(ruleUnderTest, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);
        helper.getIndividualProperties().setDirection(Direction8.Top);

        assertMovePaarameters(ruleUnderTest, ABSOLUTE_SPEED, SPEED_FACTOR_TARGET_CELL, Direction8.Right, 90, 1.0);
    }

    @Test
    public void normalMoveDiagonal() {
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "normalMoveTargetDiagonal");
        FakeSimpleMovementRule2 ruleUnderTest = getMovementRule(targetCell);
        helper.injectTargetCell(targetCell, Direction8.TopRight);
        helper.prepareFor(ruleUnderTest, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);
        helper.getIndividualProperties().setDirection(Direction8.Top);

        assertMovePaarameters(ruleUnderTest, ABSOLUTE_SPEED, SPEED_FACTOR_TARGET_CELL, Direction8.TopRight, 45, 1.0);
    }

    @Test(expected = IllegalStateException.class)
    public void individualNeedsPositiveSpeed() {
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "failNoSpeedTarget");
        FakeSimpleMovementRule2 ruleUnderTest = getMovementRule(targetCell);
        double arbitraryValue = 1.0;

        helper.injectTargetCell(targetCell, Direction8.TopRight);
        helper.prepareFor(ruleUnderTest, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);
        helper.getIndividualProperties().setDirection(Direction8.Top);
        context.checking(new Expectations() {
            {
                helper.getIndividualProperties().setRelativeSpeed(0.0);
                //allowing(helper.getEs().getCellularAutomaton()).absoluteSpeed(0.0);
                //will(returnValue(0.0));
            }
        });

        assertMovePaarameters(ruleUnderTest,
                0.0, arbitraryValue, Direction8.Right, 90, 1.0);
    }

    @Test
    public void moveOnStairs() {
        StairCellMock targetCell = context.mock(StairCellMock.class);
        FakeSimpleMovementRule2 ruleUnderTest = getMovementRule(targetCell);
        helper.injectTargetCell(targetCell, Direction8.Right);
        helper.prepareFor(ruleUnderTest, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);
        double stairSpeedFactor = 4.0;
        context.checking(new Expectations() {
            {
                allowing(targetCell).getStairSpeedFactor(Direction8.Right);
                will(returnValue(stairSpeedFactor));
            }
        });
        assertMovePaarameters(ruleUnderTest, ABSOLUTE_SPEED, SPEED_FACTOR_TARGET_CELL, Direction8.Right, 90, stairSpeedFactor * 1.1);
    }

    @Test
    public void singleDoorTarget() {
        DoorCell doorTarget = new DoorCell(1, 3);
        FakeSimpleMovementRule2 rule = getMovementRule(doorTarget);

        Direction8 direction = Direction8.Top;
        helper.injectTargetCell(doorTarget);
        helper.prepareFor(rule, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);

        assertRest(rule, helper.getTestCell(), ABSOLUTE_SPEED, SPEED_FACTOR_TARGET_CELL, direction, 0, 1);
    }

    @Test
    public void singleDoorStart() {
        DoorCell doorStart = new DoorCell(1, 1, 3, helper.getRoom());
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "singleDoorStart");
        FakeSimpleMovementRule2 rule = getMovementRule(targetCell);

        helper.injectTargetCell(targetCell);
        helper.prepareFor(rule, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);
        // Location top
        context.checking(new Expectations() {
            {
                allowing(helper.getRoom()).getXOffset();
                will(returnValue(0));
                allowing(helper.getRoom()).getYOffset();
                will(returnValue(0));
                allowing(targetCell).getAbsoluteX();
                will(returnValue(1));
                allowing(targetCell).getAbsoluteY();
                will(returnValue(2));
                oneOf(helper.getEc()).move(doorStart, targetCell);
            }
        });

        helper.getIndividualProperties().setCell(doorStart);
        doorStart.getState().setIndividual(helper.getIndividual());
        helper.getIndividualProperties().setCell(doorStart);

        Direction8 direction = Direction8.Top;

        assertRest(rule, doorStart, ABSOLUTE_SPEED, SPEED_FACTOR_TARGET_CELL, direction, 0, 1);
    }

    /**
     * Moving on cells does not involve factors and no distance.
     */
    @Test
    public void moveDoors() {
        DoorCell doorStart = new DoorCell(1, 2);
        // Directly above
        DoorCell doorTarget = new DoorCell(1, 3);

        doorTarget.setSpeedFactor(SPEED_FACTOR_TARGET_CELL);

        FakeSimpleMovementRule2 rule = getMovementRule(doorTarget);

        helper.injectTargetCell(doorTarget);
        helper.prepareFor(rule, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);

        helper.getIndividualProperties().setCell(doorStart);
        doorStart.getState().setIndividual(helper.getIndividual());
        helper.getIndividualProperties().setCell(doorStart);

        context.checking(new Expectations() {
            {
                oneOf(helper.getEc()).move(doorStart, doorTarget);
            }
        });

        Direction8 direction = Direction8.Top;

        assertRest(rule, doorStart, ABSOLUTE_SPEED, SPEED_FACTOR_TARGET_CELL, direction, 0, Double.POSITIVE_INFINITY);
    }

    private static FakeSimpleMovementRule2 getMovementRule(EvacCellInterface targetCell) {
        return new FakeSimpleMovementRule2(targetCell, Direction8.DownLeft) {

            @Override
            protected List<EvacCellInterface> computePossibleTargets(EvacCellInterface fromCell, boolean onlyFreeNeighbours) {
                if (onlyFreeNeighbours != true) {
                    throw new AssertionError("Only free neighbours are available");
                }
                return Collections.singletonList(targetCell);
            }
        };
    }

    /**
     * Checks that properties of individuals are set correctly during move depending on some
     * parameters.
     */
    private void assertMovePaarameters(FakeSimpleMovementRule2 rule, double absoluteSpeed,
            double speedFactor, Direction8 newDirection, int degree, double additionalSpeedFactor) {
        assertRest(rule, helper.getTestCell(), absoluteSpeed, speedFactor, newDirection, degree, additionalSpeedFactor);
    }

    private void assertRest(FakeSimpleMovementRule2 rule, EvacCellInterface testCell, double absoluteSpeed,
            double speedFactor, Direction8 newDirection, int degree, double additionalSpeedFactor) {
        rule.execute(testCell);

        double stepLength = degree == 0 || degree == 90 ? 0.4 : Math.sqrt(0.4 * 0.4 + 0.4 * 0.4);

        // Is that correct?
        assertThat(rule.isMoveCompleted(), is(false));

        // Assertions
        // Move called
        assertThat(rule.counter, is(equalTo(1)));

        // New view direction
        assertThat(helper.getIndividualProperties().getDirection(), is(equalTo(newDirection)));

        // Step start and end time
        final double sway = swayFromDegree(degree);
        final double timeToWalk = (stepLength /* dist */ / (absoluteSpeed * speedFactor * additionalSpeedFactor));
        double expectedStepEndTime = STEP_END_TIME_CAN_MOVE + timeToWalk * STEPS_PER_SECOND + sway * STEPS_PER_SECOND;
        assertThat(helper.getIndividualProperties().getStepEndTime(), is(closeTo(expectedStepEndTime, 10e-6)));
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

    /**
     * When no of the five possible neighbors of the current direction is available, individual
     * turns randomly.
     */
    @Test
    public void newViewDirection() {
        context.checking(new Expectations() {
            {
                allowing(helper.getTestCell()).getNeighbor(with(any(Direction8.class)));
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
                allowing(helper.getTestCell()).getNeighbor(with(any(Direction8.class)));
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

        for (Direction8 current : POSSIBLE_DIRECTIONS.keySet()) {
            Direction8 s = get2(rule, current);

            Direction8 minimumByOrdinal = Arrays.stream(POSSIBLE_DIRECTIONS.get(current)).min(Comparator.comparing(Direction8::ordinal)).get();

            assertThat(s, is(equalTo(minimumByOrdinal)));
        }
    }

    private Direction8 get2(SimpleMovementRule2 rule, Direction8 current) {
        helper.getIndividualProperties().setDirection(current);

        StaticPotential sp = new StaticPotential();
        helper.getIndividualProperties().setStaticPotential(sp);
        context.checking(new Expectations() {
            {
                Direction8[] directions = POSSIBLE_DIRECTIONS.get(current);

                for (int i = 0; i < directions.length; ++i) {
                    EvacCellInterface freeNeighbor = context.mock(EvacCellInterface.class, current.toString() + "->" + directions[i].toString());
                    oneOf(helper.getTestCell()).getNeighbor(with(directions[i]));
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

        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(helper.getTestCell()) {
            @Override
            protected void noMove(EvacCellInterface cell) {
                // Nothing
            }
        };

        helper.prepareFor(rule, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);

        // Prepare the individual reference
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
        helper.getIndividualProperties().setDirection(current);

        EnumSet<Direction8> ret = EnumSet.noneOf(Direction8.class);
        for (int i = 0; i < 5; ++i) {
            ret.add(rule.getDirection());
        }
        return ret;
    }

    @Test
    public void sameCellSelectedIfEmptyTargetList() {
        helper.sameCellSelectedIfEmptyTargetList(new SimpleMovementRule2());
    }

    @Test
    public void singleCellSelected() {
        helper.singleCellSelected(new SimpleMovementRule2());
    }

    @Test
    public void minimumCellSelected() {
        SimpleMovementRule2Spy rule = new SimpleMovementRule2Spy();

        List<EvacCellInterface> targetCells = Arrays.asList(new EvacCellInterface[]{context.mock(EvacCellInterface.class, "target1"),
            context.mock(EvacCellInterface.class, "target2"), context.mock(EvacCellInterface.class, "target3")});

        helper.prepareFor(rule, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);

        Computation c = context.mock(Computation.class);
        context.checking(new Expectations() {
            {
                allowing(helper.getEs()).getDynamicPotential(with(any(EvacCellInterface.class)));
                allowing(c).effectivePotential(with(helper.getIndividual()), with(helper.getTestCell()), with(any(Function.class)));
                will(returnValue(1.0));
                allowing(c).effectivePotential(with(helper.getIndividual()), with(targetCells.get(0)), with(any(Function.class)));
                will(returnValue(0.7));
                allowing(c).effectivePotential(with(helper.getIndividual()), with(targetCells.get(1)), with(any(Function.class)));
                will(returnValue(1.5));
                allowing(c).effectivePotential(with(helper.getIndividual()), with(targetCells.get(2)), with(any(Function.class)));
                will(returnValue(1.0));
            }
        });
        rule.setComputation(c);

        EvacCellInterface selectedCell = rule.selectTargetCell(helper.getTestCell(), targetCells);

        assertThat(selectedCell, is(same(targetCells.get(1))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void swapTargetEmpty() {
        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(null);
        rule.swap(helper.getTestCell(), new RoomCell(0, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void swapStartEmpty() {
        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(null);
        rule.swap(new RoomCell(0, 0), helper.getTestCell());
    }

    @Test(expected = IllegalArgumentException.class)
    public void swapSelf() {
        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(null);
        rule.swap(helper.getTestCell(), helper.getTestCell());
    }

    @Test
    public void swap() {
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "normalMoveTargetDiagonal");

        FakeSimpleMovementRule2 rule = new FakeSimpleMovementRule2(targetCell);
        helper.injectTargetCell(targetCell);
        helper.prepareFor(rule, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);

        Individual targetCellIndividual = new Individual(1, 0, 0, 0, 0, 0, 1, 0);
        IndividualProperty targetCellIndividualProperties = new IndividualProperty(targetCellIndividual);
        targetCellIndividualProperties.setStepEndTime(STEP_END_TIME_CAN_MOVE);
        context.checking(new Expectations() {
            {
                allowing(targetCell).getState();
                will(returnValue(new EvacuationCellState(targetCellIndividual)));
                allowing(helper.getEs()).propertyFor(targetCellIndividual);
                will(returnValue(targetCellIndividualProperties));
                allowing(helper.getTestCell()).getRelative(targetCell);
                will(returnValue(Direction8.Top));
                allowing(targetCell).getRelative(helper.getTestCell());
                will(returnValue(Direction8.Top));

                // Assert that the move is actually called within the controller
                oneOf(helper.getEc()).swap(with(helper.getTestCell()), with(targetCell));

                // Assert that the dynamic potential for target Cell is increased
                oneOf(helper.getEc()).increaseDynamicPotential(with(targetCell));
                oneOf(helper.getEc()).increaseDynamicPotential(with(helper.getTestCell()));

                allowing(helper.getTestCell()).getSpeedFactor();
                will(returnValue(SPEED_FACTOR_TARGET_CELL));
                allowing(targetCell).getOccupiedUntil();
                will(returnValue(OCCUPIED_UNTIL));
            }
        });
        helper.getIndividualProperties().setDirection(Direction8.Top);
        targetCellIndividualProperties.setDirection(Direction8.Top);
        targetCellIndividualProperties.setRelativeSpeed(RELATIVE_SPEED);

        rule.swap(helper.getTestCell(), targetCell);

        // Simple test case only, different move-properties are tested within other tests. Only for swap here
        final double timeToWalk = (0.4 /* dist */ / (ABSOLUTE_SPEED * SPEED_FACTOR_TARGET_CELL));
        double expectedStepEndTime = STEP_END_TIME_CAN_MOVE + timeToWalk * STEPS_PER_SECOND;
        assertThat(helper.getIndividualProperties().getStepEndTime(), is(closeTo(expectedStepEndTime, 10e-6)));
        assertThat(targetCellIndividualProperties.getStepEndTime(), is(closeTo(expectedStepEndTime, 10e-6)));
    }

    private static class SimpleMovementRule2Spy extends SimpleMovementRule2 implements MovementRuleTestHelper.TestableMovementRule {

        Map<MovementRuleTestHelper.MovementRuleStep, Integer> counter = new EnumMap<>(MovementRuleTestHelper.MovementRuleStep.class);

        @Override
        protected void performMove(EvacCellInterface cell) {
            counter.put(MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE,
                    counter.getOrDefault(MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE, 0) + 1);
        }

        @Override
        protected void skipStep(EvacCellInterface cell) {
            counter.put(MovementRuleTestHelper.MovementRuleStep.SKIP_STEP,
                    counter.getOrDefault(MovementRuleTestHelper.MovementRuleStep.SKIP_STEP, 0) + 1);
        }

        @Override
        protected void currentlyMoving(EvacCellInterface cell) {
            counter.put(MovementRuleTestHelper.MovementRuleStep.CURRENTLY_MOVING,
                    counter.getOrDefault(MovementRuleTestHelper.MovementRuleStep.CURRENTLY_MOVING, 0) + 1);
        }

        @Override
        protected void remainInactive(EvacCellInterface cell) {
            counter.put(MovementRuleTestHelper.MovementRuleStep.REMAIN_INACTIVE,
                    counter.getOrDefault(MovementRuleTestHelper.MovementRuleStep.REMAIN_INACTIVE, 0) + 1);
        }

        @Override
        public int methodCalled(MovementRuleTestHelper.MovementRuleStep step) {
            return counter.getOrDefault(step, 0);
        }

        @Override
        public SmoothMovementRule getAsSmoothRule() {
            return this;
        }

    }

    private FakeSimpleMovementRule2 noMoveRule() {
        return new FakeSimpleMovementRule2(helper.getTestCell(), Direction8.Top) {

            @Override
            public MoveAction move(EvacCellInterface from, EvacCellInterface targetCell) {
                throw new AssertionError("Move should not be called!");
            }
        };
    }

    private static interface StairCellMock extends EvacCellInterface, Stairs {
    }

    private static class FakeSimpleMovementRule2 extends SimpleMovementRule2 implements MovementRuleTestHelper.TestableMovementRule {

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

        @Override
        public int methodCalled(MovementRuleTestHelper.MovementRuleStep step) {
            throw new UnsupportedOperationException("Not supported, use spy!");
        }

        @Override
        public SmoothMovementRule getAsSmoothRule() {
            return this;
        }
    };

}
