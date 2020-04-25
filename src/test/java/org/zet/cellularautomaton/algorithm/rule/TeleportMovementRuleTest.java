package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.Collections;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.TeleportCell;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportMovementRuleTest {

    private Mockery context;
    private MovementRuleTestHelper helper;

    @Before
    public void initState() {
        context = new Mockery();
        helper = new MovementRuleTestHelper(context);
    }

    @Test
    public void notExecutable() {
        TeleportMovementRule rule = new TeleportMovementRule();

        helper.assertThatNotExecutableOnExitCells(rule);
        helper.assertThatNotExecuteableIfEmpty(rule);
        helper.executeableIfNotEmpty(rule);
    }

    @Test
    public void executableOnTeleportCell() {
        MovementRule mr = context.mock(MovementRule.class);
        TeleportCell tr = new TeleportCell(0, 0);
        context.checking(new Expectations() {
            {
                allowing(mr).executableOn(tr);
                will(returnValue(true));
            }
        });

        TeleportMovementRule rule = new TeleportMovementRule(mr);

        assertThat(rule, is(executeableOn(tr)));
    }

    @Test
    public void notExecutableOnTeleportCell() {
        MovementRule mr = context.mock(MovementRule.class);
        TeleportCell tr = new TeleportCell(0, 0);
        context.checking(new Expectations() {
            {
                allowing(mr).executableOn(tr);
                will(returnValue(false));
            }
        });

        TeleportMovementRule rule = new TeleportMovementRule(mr);

        assertThat(rule, is(not(executeableOn(tr))));
    }

    @Test
    public void notExecuteableWhenTeleportFailed() {
        MovementRule mr = context.mock(MovementRule.class);
        TeleportCell tr = new TeleportCell(0, 0);
        tr.setTeleportFailed(true);
        context.checking(new Expectations() {
            {
                allowing(mr).executableOn(tr);
                will(returnValue(true));
            }
        });

        TeleportMovementRule rule = new TeleportMovementRule(mr);

        assertThat(rule, is(not(executeableOn(tr))));
    }

    @Test
    public void delegation() {
        context = new Mockery();
        EvacuationState es = context.mock(EvacuationState.class);
        MovementRule mr = context.mock(MovementRule.class);
        EvacCellInterface cell = context.mock(EvacCellInterface.class, "cell1");
        EvacCellInterface cell2 = context.mock(EvacCellInterface.class, "cell2");
        List<EvacCellInterface> targets = Collections.singletonList(cell2);
        Computation c = context.mock(Computation.class);
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);

        TeleportMovementRule rule = new TeleportMovementRule(mr);
        context.checking(new Expectations() {
            {
                exactly(1).of(mr).execute(cell);
                exactly(1).of(mr).getPossibleTargets();
                exactly(1).of(mr).isDirectExecute();
                exactly(1).of(mr).isMoveCompleted();
                exactly(1).of(mr).move(cell, cell2);
                exactly(1).of(mr).selectTargetCell(cell, targets);
                exactly(1).of(mr).setComputation(c);
                exactly(1).of(mr).setDirectExecute(true);
                exactly(1).of(mr).setEvacuationState(es);
                exactly(1).of(mr).swap(cell, cell2);
            }
        });

        rule.execute(cell);
        rule.getPossibleTargets();
        rule.isDirectExecute();
        rule.isMoveCompleted();
        rule.move(cell, cell2);
        rule.selectTargetCell(cell, targets);
        rule.setComputation(c);
        rule.setDirectExecute(true);
        rule.setEvacuationState(es);
        rule.swap(cell, cell2);
        
        context.assertIsSatisfied();
    }
}
