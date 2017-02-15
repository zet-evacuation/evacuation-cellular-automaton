package org.zet.cellularautomaton.algorithm.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.results.MoveAction;
import static org.zet.cellularautomaton.results.MoveAction.NO_MOVE;
import static org.zetool.common.datastructure.SimpleTuple.asTuple;
import org.zetool.common.datastructure.Tuple;
import org.zetool.common.util.Direction8;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.generators.GeneralRandom;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class WaitingMovementRuleTest {

    private Mockery context;
    private MovementRuleTestHelper helper;

    @Before
    public void initState() {
        context = new Mockery();
        helper = new MovementRuleTestHelper(context);
    }

    @Test
    public void notExecutable() {
        WaitingMovementRule rule = new WaitingMovementRule();

        helper.assertThatNotExecutableOnExitCells(rule);
        helper.assertThatNotExecuteableIfEmpty(rule);
        helper.executeableIfNotEmpty(rule);
    }

    @Test
    public void notAlarmed() {
        helper.test(new WaitingMovementRuleSpy(), MovementRuleTestHelper.MovementRuleStep.REMAIN_INACTIVE);
    }

    @Test
    public void moving() {
        WaitingMovementRuleSpy rule = new WaitingMovementRuleSpy();
        Computation c = context.mock(Computation.class);
        rule.setComputation(c);
        GeneralRandom r = context.mock(GeneralRandom.class);
        (RandomUtils.getInstance()).setRandomGenerator(r);

        double idleThreshold = 0.6;
        context.checking(new Expectations() {
            {
                allowing(c).idleThreshold(helper.getIndividual());
                will(returnValue(idleThreshold - 10e-4));
                allowing(r).nextDouble();
                will(returnValue(idleThreshold + 10e-4));
            }
        });

        helper.test(rule, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);
    }

    @Test
    public void currentlyMoving() {
        helper.test(new WaitingMovementRuleSpy(), MovementRuleTestHelper.MovementRuleStep.CURRENTLY_MOVING);
    }

    @Test
    public void skipStep() {
        WaitingMovementRuleSpy rule = new WaitingMovementRuleSpy();
        Computation c = context.mock(Computation.class);
        rule.setComputation(c);
        GeneralRandom r = context.mock(GeneralRandom.class);
        (RandomUtils.getInstance()).setRandomGenerator(r);

        double idleThreshold = 0.6;
        context.checking(new Expectations() {
            {
                allowing(c).idleThreshold(helper.getIndividual());
                will(returnValue(idleThreshold + 10e-4));
                allowing(r).nextDouble();
                will(returnValue(idleThreshold - 10e-4));
            }
        });

        helper.test(rule, MovementRuleTestHelper.MovementRuleStep.SKIP_STEP);
    }

    @Test
    public void emptyTargetList() {
        WaitingMovementRule rule = new WaitingMovementRule();
        helper.sameCellSelectedIfEmptyTargetList(rule);
    }

    @Test
    public void singleTargetList() {
        WaitingMovementRule rule = new WaitingMovementRule();
        helper.singleCellSelected(rule);
    }

    @Test
    public void targetsInSameRoomWithoutSway() {
        WaitingMovementRule rule = new WaitingMovementRule();

        List<Tuple<Double, Direction8>> t = Arrays.asList(
                asTuple(20d, Direction8.TopRight),
                asTuple(50d, Direction8.Right),
                asTuple(30d, Direction8.Top)
        );

        // Array is: [20, 50, 30]]
        // Because the item with value 50 is in direction, 30 + 50 = 80 is the limit necessary for taking 20
        List<EvacCellInterface> targetCells = prepare(rule, t, helper.getRoom(), 0.55);

        EvacCellInterface selectedCell = rule.selectTargetCell(helper.getTestCell(), targetCells);

        assertThat(selectedCell, is(same(targetCells.get(2))));
    }

    @Test
    public void targetsInSameRoomWithSway() {
        WaitingMovementRule rule = new WaitingMovementRule();

        List<Tuple<Double, Direction8>> t = Arrays.asList(
                asTuple(20d, Direction8.TopRight),
                asTuple(30d, Direction8.Top),
                asTuple(50d, Direction8.Right)
        );

        // Original array would be: [20, 30, 50]]
        // After sway-boost: [20, 30 * x, 50]
        // When selecting something larger than 50, it should still be returned the second item if it is not too large!
        List<EvacCellInterface> targetCells = prepare(rule, t, helper.getRoom(), 0.55);

        EvacCellInterface selectedCell = rule.selectTargetCell(helper.getTestCell(), targetCells);

        assertThat(selectedCell, is(same(targetCells.get(1))));
    }

    @Test
    public void targetsInDifferentRooms() {
        WaitingMovementRule rule = new WaitingMovementRule();

        List<Tuple<Double, Direction8>> t = Arrays.asList(
                asTuple(20d, Direction8.TopRight),
                asTuple(30d, Direction8.Top),
                asTuple(50d, Direction8.Right)
        );

        Room theRoom = context.mock(Room.class, "another room");

        // Original array would be: [20, 30, 50]]
        // No swift boost
        // When selecting something larger than 20, it should still be returned the first item!
        List<EvacCellInterface> targetCells = prepare(rule, t, theRoom, 0.55);

        EvacCellInterface selectedCell = rule.selectTargetCell(helper.getTestCell(), targetCells);

        assertThat(selectedCell, is(same(targetCells.get(2))));
    }

    private List<EvacCellInterface> prepare(WaitingMovementRule rule, List<Tuple<Double, Direction8>> t, Room room, double probability) {
        List<EvacCellInterface> targetCells = new ArrayList<>(t.size());
        for (int i = 0; i < t.size(); ++i) {
            targetCells.add(context.mock(EvacCellInterface.class, "target" + i + t.get(i).toString()));
        }

        helper.prepareFor(rule, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);
        GeneralRandom r = context.mock(GeneralRandom.class);
        (RandomUtils.getInstance()).setRandomGenerator(r);

        Computation c = context.mock(Computation.class);
        context.checking(new Expectations() {
            {
                allowing(helper.getEs()).getDynamicPotential(with(any(EvacCellInterface.class)));
                for (int i = 0; i < t.size(); ++i) {
                    final EvacCellInterface targetCell = targetCells.get(i);
                    allowing(c).effectivePotential(with(helper.getIndividual()), with(targetCell), with(any(Function.class)));
                    will(returnValue(Math.log(t.get(i).getU())));
                    allowing(targetCell).getRoom();
                    will(returnValue(room));
                    // Here the target in direction has not the highest potential.
                    allowing(helper.getTestCell()).getRelative(targetCell);
                    will(returnValue(t.get(i).getV()));
                }

                allowing(r).nextDouble();
                will(returnValue(probability));
            }
        });
        rule.setComputation(c);
        return targetCells;
    }

    @Test
    public void exhaustionUpdate() {
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "normalMoveTarget");
        WaitingMovementRule ruleUnderTest = new WaitingMovementRule() {
            @Override
            public EvacCellInterface selectTargetCell(EvacCellInterface cell, List<EvacCellInterface> targets) {
                return targetCell;
            }

            @Override
            protected List<EvacCellInterface> computePossibleTargets(EvacCellInterface fromCell, boolean onlyFreeNeighbours) {
                return Collections.singletonList(targetCell);
            }

            @Override
            boolean wishToMove() {
                return true;
            }

        };

        helper.injectTargetCell(targetCell, Direction8.Right);

        helper.prepareFor(ruleUnderTest, MovementRuleTestHelper.MovementRuleStep.PERFORM_MOVE);

        EvacCellInterface lowPriorityCell = context.mock(EvacCellInterface.class, "lowPriorityCell");
        EvacCellInterface highPriorityCell = context.mock(EvacCellInterface.class, "highPriorityCell");

        Computation c = context.mock(Computation.class);
        context.checking(new Expectations() {
            {
                allowing(helper.getTestCell()).getNeighbours();
                will(returnValue(Arrays.asList(targetCell, highPriorityCell, lowPriorityCell)));

                allowing(c).effectivePotential(with(helper.getIndividual()), with(helper.getTestCell()), with(any(Function.class)));
                will(returnValue(0.0));
                allowing(c).effectivePotential(with(helper.getIndividual()), with(targetCell), with(any(Function.class)));
                will(returnValue(30.0));
                allowing(c).effectivePotential(with(helper.getIndividual()), with(highPriorityCell), with(any(Function.class)));
                will(returnValue(10.0));
                allowing(c).effectivePotential(with(helper.getIndividual()), with(lowPriorityCell), with(any(Function.class)));
                will(returnValue(100.0));
            }
        });

        ruleUnderTest.setComputation(c);

        List<EvacCellInterface> cells = ruleUnderTest.neighboursByPriority(helper.getTestCell());
        assertThat(cells, contains(highPriorityCell, targetCell, lowPriorityCell));
        context.checking(new Expectations() {
            {
                atLeast(1).of(c).updatePanic(with(helper.getIndividual()), with(targetCell), with(cells));
                atLeast(1).of(c).updateExhaustion(helper.getIndividual(), targetCell);
                allowing(c).idleThreshold(helper.getIndividual());
            }
        });

        ruleUnderTest.execute(helper.getTestCell());

        context.assertIsSatisfied();
    }

    @Test
    public void noPanicUpdateIfNotAlarmed() {
        WaitingMovementRule ruleUnderTest = new WaitingMovementRuleSpy();
        helper.prepareFor(ruleUnderTest, MovementRuleTestHelper.MovementRuleStep.REMAIN_INACTIVE);
        Computation c = context.mock(Computation.class);
        ruleUnderTest.setComputation(c);
        context.checking(new Expectations() {
            {
                never(c).updatePanic(with(any(Individual.class)), with(any(EvacCellInterface.class)), with(any(Collection.class)));
                never(c).updateExhaustion(with(any(Individual.class)), with(any(EvacCellInterface.class)));
            }
        });

        ruleUnderTest.execute(helper.getTestCell());

        context.assertIsSatisfied();
    }

    @Test
    public void panicUpdateIfSkips() {
        WaitingMovementRule ruleUnderTest = new WaitingMovementRule() {
            @Override
            boolean wishToMove() {
                return false;
            }
        };
        helper.prepareFor(ruleUnderTest, MovementRuleTestHelper.MovementRuleStep.SKIP_STEP);
        Computation c = context.mock(Computation.class);
        ruleUnderTest.setComputation(c);
        GeneralRandom r = context.mock(GeneralRandom.class);
        (RandomUtils.getInstance()).setRandomGenerator(r);
        context.checking(new Expectations() {
            {
                never(c).updatePanic(with(any(Individual.class)), with(any(EvacCellInterface.class)), with(any(Collection.class)));
                atLeast(1).of(c).updateExhaustion(with(helper.getIndividual()), with(helper.getTestCell()));
                allowing(helper.getTestCell()).getNeighbor(with(any(Direction8.class)));
                will(returnValue(null));
                allowing(r).nextInt(with(any(Integer.class)));
            }
        });

        ruleUnderTest.execute(helper.getTestCell());

        context.assertIsSatisfied();
    }

    private static class WaitingMovementRuleSpy extends WaitingMovementRule implements MovementRuleTestHelper.TestableMovementRule {

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
        protected MoveAction remainInactive(EvacCellInterface cell) {
            counter.put(MovementRuleTestHelper.MovementRuleStep.REMAIN_INACTIVE,
                    counter.getOrDefault(MovementRuleTestHelper.MovementRuleStep.REMAIN_INACTIVE, 0) + 1);
            return NO_MOVE;
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
}
