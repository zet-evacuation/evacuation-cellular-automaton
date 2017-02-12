package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.TeleportCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.results.MoveAction;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportRuleTest {

    private final Mockery context = new Mockery();
    private TeleportCell testCell;
    private TeleportRule rule;
    private final Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
    private EvacuationState es;
    private IndividualProperty ip;
    private static final double STEP_END_TIME = 3.5;
    private static final int CURRENT_TIME_STEP = 4;

    @Before
    public void initState() {
        es = context.mock(EvacuationState.class);
        ip = new IndividualProperty(i);
        testCell = new TeleportCell(0, 0);

        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(ip));
                allowing(es).getTimeStep();
                will(returnValue(CURRENT_TIME_STEP));
            }
        });
        rule = new TeleportRule();
        rule.setEvacuationState(es);
        ip.setStepEndTime(STEP_END_TIME);
        prepareCell(testCell);
    }

    private void prepareCell(EvacCellInterface cell) {
        cell.getState().setIndividual(i);
        ip.setCell(testCell);
    }

    @Test
    public void notExecuteableIfNoTeleportCell() {
        EvacCellInterface evacCell = new RoomCell(0, 0);
        prepareCell(evacCell);

        assertThat(rule, is(not(executeableOn(evacCell))));
    }

    @Test
    public void notExecuteableIfNoIndividual() {
        assertThat(rule, is(not(executeableOn(new TeleportCell(0, 0)))));
    }

    @Test
    public void notExecuteableIfLastStepNotFinished() {
        ip.setStepEndTime(CURRENT_TIME_STEP + 1);
        assertThat(rule, is(not(executeableOn(testCell))));
    }

    @Test
    public void executableOnTeleportCell() {
        assertThat(rule, is(executeableOn(testCell)));
    }

    @Test
    public void noMoveIfTargetListEmpty() {
        MoveAction a = rule.execute(testCell).get();
        assertThat(a, is(equalTo(MoveAction.NO_MOVE)));
    }

    @Test
    public void noMoveIfTargetOccupied() {
        TeleportCell targetCell = new TeleportCell(0, 1);
        targetCell.getState().setIndividual(new Individual(1, 0, 0, 0, 0, 0, 1, 0));
        testCell.addTarget(targetCell);
        MoveAction a = rule.execute(testCell).get();
        assertThat(a, is(equalTo(MoveAction.NO_MOVE)));
        assertThat(testCell.isTeleportFailed(), is(true));
    }

    @Test
    public void noMoveIfAlreadyMovedInCurrentStep() {
        TeleportCell targetCell = new TeleportCell(0, 1);
        targetCell.setUsedInTimeStep(CURRENT_TIME_STEP);
        testCell.addTarget(targetCell);
        MoveAction a = rule.execute(testCell).get();
        assertThat(a, is(equalTo(MoveAction.NO_MOVE)));
        context.assertIsSatisfied();
        assertThat(testCell.isTeleportFailed(), is(true));
    }

    @Test
    public void noMoveIfFullAndUsed() {
        TeleportCell targetCell = new TeleportCell(0, 1);
        targetCell.getState().setIndividual(new Individual(1, 0, 0, 0, 0, 0, 1, 0));
        targetCell.setUsedInTimeStep(CURRENT_TIME_STEP);
        testCell.addTarget(targetCell);
        MoveAction a = rule.execute(testCell).get();
        assertThat(a, is(equalTo(MoveAction.NO_MOVE)));
        assertThat(testCell.isTeleportFailed(), is(true));
    }

    @Test
    public void teleport() {
        TeleportCell targetCell = new TeleportCell(0, 1);

        testCell.addTarget(targetCell);

        // Set move time, it is the maximum of the following:
        final double occupiedUntil = 3.8;
        //stepEndTime is 3.5

        targetCell.setOccupiedUntil(occupiedUntil);

        MoveAction a = rule.execute(testCell).get();

        assertThat(a.getStartTime(), is(equalTo(occupiedUntil)));
        assertThat(a.getArrivalTime(), is(equalTo(occupiedUntil)));

        assertThat(testCell.isTeleportFailed(), is(false));        
    }

}
