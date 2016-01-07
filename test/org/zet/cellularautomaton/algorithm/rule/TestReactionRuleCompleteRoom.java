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
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestReactionRuleCompleteRoom {
    private final Mockery context = new Mockery();
    private EvacuationState es;
    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void roomAlerted() {
        Individual i = builder.build();
        Room room = mockRoom(i, false, 1);
        RoomCell cell = generateCell(room, i);
        
        ReactionRuleCompleteRoom rule = generateReactionRule();
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
        }});
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(true));
    }
    
    @Test
    public void roomNotAlerted() {
        Individual i = builder.withAge(0).withReactionTime(7).buildAndReset();
        Room room = mockRoom(i, false, 0);
        RoomCell cell = generateCell(room, i);
        
        ReactionRuleCompleteRoom rule = generateReactionRule();
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
        }});
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(false));
    }
    
    @Test
    public void alertedByRoom() {
        Individual i = builder.withAge(0).withReactionTime(7).build();
        Room room = mockRoom(i, true, 0);
        RoomCell cell = generateCell(room, i);
        
        ReactionRuleCompleteRoom rule = generateReactionRule();
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(true));
    }

    private ReactionRuleCompleteRoom generateReactionRule() {
        es = context.mock(EvacuationState.class);
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
            }
        });
        ReactionRuleCompleteRoom rule = new ReactionRuleCompleteRoom();
        rule.setEvacuationSimulationProblem(es);
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
