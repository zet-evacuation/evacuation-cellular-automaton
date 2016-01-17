package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.algorithm.EvacuationState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;

import java.util.Objects;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.IndividualProperty;

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
        IndividualProperty ip = new IndividualProperty(i);
        Room room = mockRoom(i, false, 1);
        RoomCell cell = generateCell(room, i, ip);
        
        ReactionRuleCompleteRoom rule = generateReactionRule();
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
    public void roomNotAlerted() {
        Individual i = builder.withAge(0).withReactionTime(7).buildAndReset();
        IndividualProperty ip = new IndividualProperty(i);
        Room room = mockRoom(i, false, 0);
        RoomCell cell = generateCell(room, i, ip);
        
        ReactionRuleCompleteRoom rule = generateReactionRule();
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
                allowing(es).propertyFor(i);
                will(returnValue(ip));
        }});
        rule.execute(cell);
        assertThat(ip.isAlarmed(), is(false));
    }
    
    @Test
    public void alertedByRoom() {
        Individual i = builder.withAge(0).withReactionTime(7).build();
        IndividualProperty ip = new IndividualProperty(i);
        Room room = mockRoom(i, true, 0);
        RoomCell cell = generateCell(room, i, ip);

        ReactionRuleCompleteRoom rule = generateReactionRule();
        context.checking(new Expectations() {{
                allowing(es).propertyFor(i);
                will(returnValue(ip));
        }});

        rule.execute(cell);
        assertThat(ip.isAlarmed(), is(true));
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
        rule.setEvacuationState(es);
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
    
    private RoomCell generateCell(Room room, Individual i, IndividualProperty ip) {
        RoomCell cell = new RoomCell(1, 0, 0, Objects.requireNonNull(room));
        cell.getState().setIndividual(Objects.requireNonNull(i));
        ip.setCell(cell);
        return cell;
    }
    
}
