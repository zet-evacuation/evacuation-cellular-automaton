package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestAbstractPotentialChangeRule {
    private final static IndividualBuilder builder = new IndividualBuilder();
    
    private static class FakeAbstractPotentialChangeRule extends AbstractPotentialChangeRule {

        private final boolean wantsToChange;

        public FakeAbstractPotentialChangeRule(boolean wantsToChange) {
            this.wantsToChange = wantsToChange;
        }

        @Override
        protected boolean wantsToChange(Individual i) {
            return wantsToChange;
        }

        @Override
        protected void onExecute(EvacCell cell) {

        }
    };
    
    private EvacCell createCell(boolean occupied, boolean safe) {
        EvacCell cell = new RoomCell(0, 0);
        if( occupied ) {
            Individual i = builder.buildNewIndividual();
            cell.getState().setIndividual(i);
            i.setCell(cell);
            i.setSafe(safe);
        }
        return cell;
    }
    
    @Test
    public void notExecuteableIfNotWillingToChange() {
        AbstractPotentialChangeRule rule = new FakeAbstractPotentialChangeRule(false);
        assertThat(rule, is(not(executeableOn(createCell(true, false)))));
        assertThat(rule, is(not(executeableOn(createCell(false, false)))));
    }
    
    @Test
    public void executeableIfWillingToChange() {
        AbstractPotentialChangeRule rule = new FakeAbstractPotentialChangeRule(true);

        assertThat(rule, is(executeableOn(createCell(true, false))));
        assertThat(rule, is(not(executeableOn(createCell(false, false)))));
    }

    
}
