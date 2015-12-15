package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;

import java.util.Objects;
import java.util.UUID;
import org.jmock.Expectations;
import org.jmock.Mockery;
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
        Individual i = new Individual();
        Room room = mockRoom(i, false, 1);
        RoomCell cell = generateCell(room, i);
        
        ReactionRuleCompleteRoom rule = generateReactionRule();
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(true));
    }
    
    @Test
    public void roomNotAlerted() {
        Individual i = new Individual(0, 0, 0, 0, 0, 1, 7, new UUID(0, 0));
        Room room = mockRoom(i, false, 0);
        RoomCell cell = generateCell(room, i);
        
        ReactionRuleCompleteRoom rule = generateReactionRule();
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(false));
    }
    
    @Test
    public void alertedByRoom() {
        Individual i = new Individual(0, 0, 0, 0, 0, 1, 7, new UUID(0, 0));
        Room room = mockRoom(i, true, 0);
        RoomCell cell = generateCell(room, i);
        
        ReactionRuleCompleteRoom rule = generateReactionRule();
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(true));
    }

    private ReactionRuleCompleteRoom generateReactionRule() {
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        context.checking(new Expectations() {
            {
                allowing(p).getCellularAutomaton();
                will(returnValue(eca));
            }
        });
        ReactionRuleCompleteRoom rule = new ReactionRuleCompleteRoom();
        rule.setEvacuationSimulationProblem(p);
        return rule;
    }
    
    private Room mockRoom(Individual i, boolean beforeAlarm, int setAlarmExpectations) {
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
    
    private RoomCell generateCell(Room room, Individual i) {
        RoomCell cell = new RoomCell(1, 0, 0, Objects.requireNonNull(room));
        cell.getState().setIndividual(Objects.requireNonNull(i));
        i.setCell(cell);
        return cell;
    }
    
}
