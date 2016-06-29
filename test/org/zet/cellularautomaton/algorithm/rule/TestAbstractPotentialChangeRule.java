package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestAbstractPotentialChangeRule {

    private final static IndividualBuilder builder = new IndividualBuilder();
    private final Mockery context = new Mockery();
    private EvacuationState es;

    @Before
    public void initState() {
        es = context.mock(EvacuationState.class);
    }

    private class FakeAbstractPotentialChangeRule extends AbstractPotentialChangeRule {

        private final boolean wantsToChange;

        public FakeAbstractPotentialChangeRule(EvacuationState es, boolean wantsToChange) {
            this.wantsToChange = wantsToChange;
            this.setEvacuationState(es);

        }

        @Override
        protected boolean wantsToChange(Individual i) {
            return wantsToChange;
        }

        @Override
        protected void onExecute(EvacCellInterface cell) {

        }
    };

    private EvacCell createCell(boolean occupied, boolean safe) {
        EvacCell cell = new RoomCell(0, 0);
        if (occupied) {
            Individual i = builder.build();
            IndividualProperty ip = new IndividualProperty(i);
            context.checking(new Expectations() {
                {
                    allowing(es).propertyFor(i);
                    will(returnValue(ip));
                }
            });
            cell.getState().setIndividual(i);
            ip.setCell(cell);
            if (safe) {
            }
        }
        return cell;
    }

    @Test
    public void notExecuteableIfNotWillingToChange() {
        AbstractPotentialChangeRule rule = new FakeAbstractPotentialChangeRule(es, false);
        assertThat(rule, is(not(executeableOn(createCell(true, false)))));
        assertThat(rule, is(not(executeableOn(createCell(false, false)))));
    }

    @Test
    public void executeableIfWillingToChange() {
        AbstractPotentialChangeRule rule = new FakeAbstractPotentialChangeRule(es, true);

        assertThat(rule, is(executeableOn(createCell(true, false))));
        assertThat(rule, is(not(executeableOn(createCell(false, false)))));
    }

}
