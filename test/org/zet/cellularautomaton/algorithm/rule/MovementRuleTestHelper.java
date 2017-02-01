package org.zet.cellularautomaton.algorithm.rule;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellState;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.algorithm.rule.MovementRuleTestHelper.MovementRuleStep;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;
import org.zetool.common.util.Direction8;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class MovementRuleTestHelper {

    private EvacCellInterface testCell;
    private final Individual individual = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
    private Room room;
    private EvacuationState es;
    private EvacuationStateControllerInterface ec;
    private IndividualProperty ip;
    private EvacuationCellularAutomaton ca;
    static final double STEPS_PER_SECOND = 10.0;
    private final int TIME_STEP = 13;
    private final double STEP_END_TIME_CURRENTLY_MOVING = TIME_STEP + 2;
    public final double STEP_END_TIME_CAN_MOVE = TIME_STEP - 0.8;
    private final double STEP_START_TIME = 12.5;
    final double OCCUPIED_UNTIL = 3.0;
    static final double RELATIVE_SPEED = 0.7;
    static final double ABSOLUTE_SPEED = 3.5;
    static final double SPEED_FACTOR_TARGET_CELL = 0.85;
    private final Mockery context;
    private EvacCellInterface targetCell;

    public MovementRuleTestHelper(Mockery context) {
        this.context = context;
        initMocks();
    }

    private void initMocks() {
        room = context.mock(Room.class);
        es = context.mock(EvacuationState.class);
        ec = context.mock(EvacuationStateControllerInterface.class);
        ip = new IndividualProperty(individual);
        testCell = context.mock(EvacCellInterface.class, "testStartCell");
        ca = context.mock(EvacuationCellularAutomaton.class);

        context.checking(new Expectations() {
            {
                allowing(testCell).getState();
                will(returnValue(new EvacuationCellState(individual)));
                allowing(testCell).getRoom();
                will(returnValue(room));
                allowing(room).getID();
                will(returnValue(1));
                allowing(es).propertyFor(individual);
                will(returnValue(ip));
                allowing(room).getID();
                will(returnValue(1));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
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
    }

    /**
     * Not excuteable when empty.
     *
     * @param rule
     */
    public void assertThatNotExecuteableIfEmpty(EvacuationRule rule) {
        assertThat(rule, is(not(executeableOn(new RoomCell(0, 0)))));
    }

    public void assertThatNotExecutableOnExitCells(EvacuationRule rule) {
        ExitCell exitCell = new ExitCell(0, 0);
        assertThat(rule, is(not(executeableOn(exitCell))));
    }

    /**
     * Executeable when filled.
     */
    public void executeableIfNotEmpty(EvacuationRule rule) {
        assertThat(rule, is(executeableOn(testCell)));
    }

    public void sameCellSelectedIfEmptyTargetList(AbstractMovementRule rule) {
        List<EvacCellInterface> targets = Collections.emptyList();

        Computation c = context.mock(Computation.class);
        context.checking(new Expectations() {
            {
                allowing(c).effectivePotential(with(individual), with(testCell), with(any(Function.class)));
                will(returnValue(1.0));

            }
        });

        rule.setEvacuationState(es);
        rule.setComputation(c);
        EvacCellInterface selectedCell = rule.selectTargetCell(testCell, targets);

        assertThat(selectedCell, is(same(testCell)));
    }

    public void singleCellSelected(SmoothMovementRule rule) {

        Computation c = context.mock(Computation.class);
        context.checking(new Expectations() {
            {
                allowing(c).effectivePotential(with(individual), with(testCell), with(any(Function.class)));
                will(returnValue(1.0));

            }
        });
        rule.individual = individual;
        rule.setEvacuationState(es);
        rule.setComputation(c);

        //rule.execute(testCell);
        EvacCellInterface selectedCell = rule.selectTargetCell(testCell, Collections.singletonList(testCell));

        assertThat(selectedCell, is(same(testCell)));
    }

    public void prepareFor(SmoothMovementRule ruleUnderTest, MovementRuleStep step) {
        context.checking(new Expectations() {
            {
                allowing(es).getTimeStep();
                will(returnValue(TIME_STEP));
                ip.setStepStartTime(STEP_START_TIME);
                ip.setRelativeSpeed(RELATIVE_SPEED);
                allowing(ca).absoluteSpeed(RELATIVE_SPEED);
                will(returnValue(ABSOLUTE_SPEED));

                ruleUnderTest.individual = individual;
                switch (step) {
                    case REMAIN_INACTIVE:
                        ip.setStepEndTime(STEP_END_TIME_CAN_MOVE);
                        break;
                    case CURRENTLY_MOVING:
                        es.propertyFor(individual).setAlarmed();
                        ip.setStepEndTime(STEP_END_TIME_CURRENTLY_MOVING);
                        never(ec).move(with(any(EvacCell.class)), with(any(EvacCell.class)));
                        break;
                    case PERFORM_MOVE:
                        es.propertyFor(individual).setAlarmed();
                        ip.setStepEndTime(STEP_END_TIME_CAN_MOVE);
                        if (testCell instanceof EvacCell) {

                        } else {
                            allowing(testCell).getOccupiedUntil();
                            will(returnValue(OCCUPIED_UNTIL));
                        }
                        //oneOf(ec).move(testCell, targetCell);
                        allowing(testCell).setOccupiedUntil(with(any(Double.class)));
                        break;
                    case SKIP_STEP:
                        allowing(ec).move(testCell, testCell);
                        es.propertyFor(individual).setAlarmed();
                        ip.setStepEndTime(STEP_END_TIME_CAN_MOVE);
                        break;
                }
            }
        });
        ruleUnderTest.setEvacuationState(es);
        ruleUnderTest.setEvacuationStateController(ec);
    }

    /**
     * Asserts that one of the methods of four different actions that can happen when a
     * {@link SmoothMovementRule} is called. The methods are {@link SmoothMovementRule#performMove(org.zet.cellularautomaton.EvacCellInterface)  },
     * {@link SmoothMovementRule#remainInactive(org.zet.cellularautomaton.EvacCellInterface) },
     * {@link SmoothMovementRule#currentlyMoving(org.zet.cellularautomaton.EvacCellInterface) } and
     * {@link SmoothMovementRule#skipStep(org.zet.cellularautomaton.EvacCellInterface) }.
     *
     * @param ruleUnderTest
     * @param step
     */
    public void test(TestableMovementRule ruleUnderTest, MovementRuleStep step) {
        prepareFor(ruleUnderTest.getAsSmoothRule(), step);

        step.verify(ruleUnderTest.getAsSmoothRule());

        ruleUnderTest.execute(testCell);

        assertThat(ruleUnderTest.methodCalled(step), is(equalTo(1)));
    }

    Individual getIndividual() {
        return individual;
    }

    EvacCellInterface getTestCell() {
        return testCell;
    }

    IndividualProperty getIndividualProperties() {
        return ip;
    }

    EvacuationStateControllerInterface getEc() {
        return ec;
    }

    void assertDefaultResults(MovementRuleStep movementRuleStep) {
        switch (movementRuleStep) {
            case REMAIN_INACTIVE:
                // Assert that one time step is used for not doing anything
                assertThat(ip.getStepStartTime(), is(closeTo(STEP_END_TIME_CAN_MOVE, 0.0001)));
                assertThat(ip.getStepEndTime(), is(closeTo(STEP_END_TIME_CAN_MOVE + 1, 0.0001)));
                break;
            case CURRENTLY_MOVING:
                // Assert that step start and end times have not been modified
                assertThat(Double.doubleToRawLongBits(STEP_START_TIME), is(equalTo(Double.doubleToRawLongBits(ip.getStepStartTime()))));
                assertThat(Double.doubleToRawLongBits(ip.getStepEndTime()), is(equalTo(Double.doubleToRawLongBits(STEP_END_TIME_CURRENTLY_MOVING))));
                break;
            default:
                throw new UnsupportedOperationException();
        }
        context.assertIsSatisfied();
    }

    void injectTargetCell(EvacCellInterface targetCell) {
        injectTargetCell(targetCell, Direction8.Top);
    }

    void injectTargetCell(EvacCellInterface targetCell, Direction8 direction) {
        this.targetCell = targetCell;
        context.checking(new Expectations() {
            {
                // Assert that the move is actually called within the controller
                oneOf(ec).move(with(testCell), with(targetCell));

                // Assert that the dynamic potential for target Cell is increased
                oneOf(ec).increaseDynamicPotential(with(targetCell));
                if (targetCell instanceof EvacCell) {
                    ((EvacCell) targetCell).setSpeedFactor(SPEED_FACTOR_TARGET_CELL);
                } else {
                    allowing(targetCell).getSpeedFactor();
                    will(returnValue(SPEED_FACTOR_TARGET_CELL));
                }
                allowing(testCell).getRelative(targetCell);
                will(returnValue(direction));
            }
        });
    }

    EvacuationState getEs() {
        return es;
    }

    Room getRoom() {
        return room;
    }

    public static enum MovementRuleStep {
        PERFORM_MOVE {
            @Override
            void verify(SmoothMovementRule ruleUnderTest) {
                assertThat(ruleUnderTest.isActive(), is(true));
                assertThat(ruleUnderTest.canMove(ruleUnderTest.individual), is(true));
                assertThat(ruleUnderTest.wishToMove(), is(true));

            }
        },
        REMAIN_INACTIVE {
            @Override
            void verify(SmoothMovementRule ruleUnderTest) {
                assertThat(ruleUnderTest.isActive(), is(false));
            }
        },
        CURRENTLY_MOVING {
            @Override
            void verify(SmoothMovementRule ruleUnderTest) {
                assertThat(ruleUnderTest.isActive(), is(true));
                assertThat(ruleUnderTest.canMove(ruleUnderTest.individual), is(false));
            }
        },
        SKIP_STEP {
            @Override
            void verify(SmoothMovementRule ruleUnderTest) {
                assertThat(ruleUnderTest.isActive(), is(true));
                assertThat(ruleUnderTest.canMove(ruleUnderTest.individual), is(true));
                assertThat(ruleUnderTest.wishToMove(), is(false));
            }
        };

        abstract void verify(SmoothMovementRule ruleUnderTest);
    }

    public static interface TestableMovementRule extends EvacuationRule {

        int methodCalled(MovementRuleStep step);

        SmoothMovementRule getAsSmoothRule();
    }

}
