package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.algo.ca.util.IndividualDistanceComparator;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestReactionRuleOnePerson {
    private final Mockery context = new Mockery();
    private EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
    private EvacuationState es;
    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void alertsImmediately() {
        ReactionRuleOnePerson rule = new ReactionRuleOnePerson();
        rule.setEvacuationState(es);

        RoomCell cell = new RoomCell(0, 0);
        Individual i = builder.build();
        IndividualProperty ip = new IndividualProperty(i);
        cell.getState().setIndividual(i);

        assertThat(ip.isAlarmed(), is(false));
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
                allowing(es).propertyFor(i);
                will(returnValue(ip));
        }});
        rule.execute(cell);
        assertThat(ip.isAlarmed(), is(true));
    }
    
    @Test
    public void alertLate() {
        ReactionRuleOnePerson rule = new ReactionRuleOnePerson();
        rule.setEvacuationState(es);

        RoomCell cell = new RoomCell(0, 0);
        Individual evacuee = builder.withAge(0).withReactionTime(7).buildAndReset();
        IndividualProperty ip = new IndividualProperty(evacuee);
        cell.getState().setIndividual(evacuee);
        
        assertThat(ip.isAlarmed(), is(false));
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
                allowing(es).propertyFor(evacuee);
                will(returnValue(ip));
        }});
        rule.execute(cell);
        assertThat(ip.isAlarmed(), is(false));
        
        eca.setAbsoluteMaxSpeed(0.41);
        
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
        }});
        rule.execute(cell);
        for( int i = 0; i < 7; ++i) {
            final int result = i+1;
            context.checking(new Expectations() {{
                    exactly(1).of(es).getTimeStep();
                    will(returnValue(result));
            }});
            rule.execute(cell);
            assertThat(ip.isAlarmed(), is(false));        
        }
        // Individuals reaction time is 7 
        // one additional time steps sets time to 7.175
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(8));
        }});
        rule.execute(cell);
        assertThat(ip.isAlarmed(), is(true));
    }

    @Before
    public void initEvacuationProblem() {
        es = context.mock(EvacuationState.class);
        eca = new EvacuationCellularAutomaton();
        context.checking(new Expectations() {{
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
        }});
    }
}
