package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ReactionRuleOnePersonTest {
    private final Mockery context = new Mockery();
    private MultiFloorEvacuationCellularAutomaton eca = new MultiFloorEvacuationCellularAutomaton();
    private EvacuationState es;
    private final static IndividualBuilder INDIVIDUAL_BUILDER = new IndividualBuilder();

    @Test
    public void alertsImmediately() {
        ReactionRuleOnePerson rule = new ReactionRuleOnePerson();
        rule.setEvacuationState(es);
        rule.setEvacuationSimulationSpeed(new EvacuationSimulationSpeed(0.4));

        RoomCell cell = new RoomCell(0, 0);
        Individual i = INDIVIDUAL_BUILDER.build();
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

        EvacuationSimulationSpeed sp = new EvacuationSimulationSpeed(0.41);
        rule.setEvacuationSimulationSpeed(sp);

        RoomCell cell = new RoomCell(0, 0);
        Individual evacuee = INDIVIDUAL_BUILDER.withAge(0).withReactionTime(7).buildAndReset();
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
        
        //eca.setAbsoluteMaxSpeed(0.41);
        
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
        eca = new MultiFloorEvacuationCellularAutomaton();
        context.checking(new Expectations() {{
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
        }});
    }
}