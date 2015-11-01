package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestAbstractReactionRule {
    @Test
    public void executeableOnlyIfNotAlarmed() {
        AbstractReactionRule rule = new AbstractReactionRule() {

            @Override
            protected void onExecute(EvacCell cell) {
            }
        };
        RoomCell cell = new RoomCell(0, 0);
        assertThat(rule.executableOn(cell), is(false));

        Individual i = new Individual();
        cell.getState().setIndividual(i);
        i.setAlarmed(false);
        assertThat(rule.executableOn(cell), is(true));
        
        i.setAlarmed(true);
        assertThat(rule.executableOn(cell), is(false));
    }
}
