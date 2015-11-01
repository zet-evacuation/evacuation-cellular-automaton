package org.zet.cellularautomaton.algorithm.rule;

import java.util.UUID;
import static org.hamcrest.CoreMatchers.is;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestReactionRuleCompleteRoom {
    private final Mockery context = new Mockery();

    @Test
    public void roomAlerted() {
        ReactionRuleCompleteRoom rule = new ReactionRuleCompleteRoom();
        rule.setEvacuationSimulationProblem(getEvacuationProblem());

        Individual i = new Individual();
        Room room = roomExpectations(i, false, 1);

        RoomCell cell = new RoomCell(1, 0, 0, room);
        cell.getState().setIndividual(i);

        i.setCell(cell);
        
        assertThat(i.isAlarmed(), is(false));
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(true));
    }
    
    @Test
    public void roomNotAlerted() {
        ReactionRuleCompleteRoom rule = new ReactionRuleCompleteRoom();
        rule.setEvacuationSimulationProblem(getEvacuationProblem());

        Individual i = new Individual(0, 0, 0, 0, 0, 1, 7, new UUID(0, 0));
        Room room = roomExpectations(i, false, 0);

        RoomCell cell = new RoomCell(1, 0, 0, room);
        cell.getState().setIndividual(i);

        i.setCell(cell);
        
        assertThat(i.isAlarmed(), is(false));
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(false));
    }
    
    @Test
    public void alertedByRoom() {
        ReactionRuleCompleteRoom rule = new ReactionRuleCompleteRoom();
        rule.setEvacuationSimulationProblem(getEvacuationProblem());

        Individual i = new Individual(0, 0, 0, 0, 0, 1, 7, new UUID(0, 0));
        Room room = roomExpectations(i, true, 0);

        RoomCell cell = new RoomCell(1, 0, 0, room);
        cell.getState().setIndividual(i);

        i.setCell(cell);
        
        assertThat(i.isAlarmed(), is(false));
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(true));
    }

    private EvacuationSimulationProblem getEvacuationProblem() {
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        context.checking(new Expectations() {
            {
                allowing(p).getCa();
                will(returnValue(eca));
            }
        });
        return p;
    }
    
    private Room roomExpectations(Individual i, boolean beforeAlarm, int setAlarmExpectations) {
        Room room = context.mock(Room.class);
        context.checking(new Expectations() {{
                allowing(room).getID();
                will(returnValue(1));
                allowing(room).addIndividual(with(any(EvacCell.class)), with(i));
                allowing(room).isAlarmed();
                will(returnValue(beforeAlarm));
                exactly(setAlarmExpectations).of(room).setAlarmstatus(with(true));
        }});
        return room;
    }
    
}
