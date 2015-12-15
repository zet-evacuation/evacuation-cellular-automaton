package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;

import java.util.UUID;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestReactionRuleOnePerson {
    private final Mockery context = new Mockery();
    EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();

    @Test
    public void alertsImmediately() {
        ReactionRuleOnePerson rule = new ReactionRuleOnePerson();
        rule.setEvacuationSimulationProblem(getEvacuationProblem());

        RoomCell cell = new RoomCell(0, 0);
        Individual i = new Individual();
        cell.getState().setIndividual(i);

        assertThat(i.isAlarmed(), is(false));
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(true));
    }
    
    @Test
    public void alertLate() {
        ReactionRuleOnePerson rule = new ReactionRuleOnePerson();
        EvacuationSimulationProblem p = getEvacuationProblem();
        rule.setEvacuationSimulationProblem(p);

        RoomCell cell = new RoomCell(0, 0);
        Individual evacuee = new Individual(0, 0, 0, 0, 0, 1, 7, new UUID(0, 0));
        cell.getState().setIndividual(evacuee);
        
        assertThat(evacuee.isAlarmed(), is(false));
        rule.execute(cell);
        assertThat(evacuee.isAlarmed(), is(false));
        
        eca.setAbsoluteMaxSpeed(0.41);
        
        rule.execute(cell);
        for( int i = 0; i < 7; ++i) {
            p.getCellularAutomaton().nextTimeStep();
            rule.execute(cell);
            assertThat(evacuee.isAlarmed(), is(false));        
        }
        // Individuals reaction time is 7 
        // one additional time steps sets time to 7.175
        p.getCellularAutomaton().nextTimeStep();
        rule.execute(cell);
        assertThat(evacuee.isAlarmed(), is(true));
    }

    private EvacuationSimulationProblem getEvacuationProblem() {
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        eca = new EvacuationCellularAutomaton();
        context.checking(new Expectations() {
            {
                allowing(p).getCellularAutomaton();
                will(returnValue(eca));
            }
        });
        return p;
    }
}
