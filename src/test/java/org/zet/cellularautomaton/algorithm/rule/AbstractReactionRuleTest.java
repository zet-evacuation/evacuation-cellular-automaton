package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.junit.Test;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.results.ReactionAction;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AbstractReactionRuleTest {
    @Test
    public void executeableOnlyIfNotAlarmed() {
        AbstractReactionRule rule = new AbstractReactionRule() {

            @Override
            protected ReactionAction onExecute(EvacCellInterface cell) {
                return null;
            }
        };
        
        
        RoomCell cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));

        Individual i = new IndividualBuilder().build();
        IndividualProperty ip = new IndividualProperty(i);
        Mockery context = new Mockery();
        EvacuationState es = context.mock(EvacuationState.class);
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(ip));
            }
        });
        cell.getState().setIndividual(i);
        rule.setEvacuationState(es);
        assertThat(rule, is(executeableOn(cell)));
        
        ip.setAlarmed();
        
        assertThat(rule, is(not(executeableOn(cell))));
    }
}
