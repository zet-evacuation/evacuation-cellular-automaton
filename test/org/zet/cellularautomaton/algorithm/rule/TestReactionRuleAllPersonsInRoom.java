package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.algorithm.EvacuationState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;

import java.util.Arrays;
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

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestReactionRuleAllPersonsInRoom {
    private final Mockery context = new Mockery();
    EvacuationState es;
    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void testSingleIndividual() {
        Individual i = builder.build();
        Room room = mockRoom(i);
        RoomCell cell = generateCell(room, i);
        
        ReactionRuleAllPersonsInRoom rule = generateRule();
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
        }});
        rule.execute(cell);
        assertThat(i.isAlarmed(), is(true));
    }

    @Test
    public void testMultipleIndividuals() {
        Individual i1 = builder.build();
        Individual i2 = builder.build();
        Individual i3 = builder.build();
        Room room = mockRoom(i1, i2, i3);
        RoomCell cell = generateCell(room, i1);
        generateCell(room, i2);
        generateCell(room, i3);
        
        ReactionRuleAllPersonsInRoom rule = generateRule();
        context.checking(new Expectations() {{
                exactly(3).of(es).getTimeStep();
                will(returnValue(0));
        }});
        rule.execute(cell);
        assertThat(i1.isAlarmed(), is(true));
        assertThat(i2.isAlarmed(), is(true));
        assertThat(i3.isAlarmed(), is(true));
    }
    
    @Test
    public void testNotAllReady() {
        Individual i1 = builder.build();
        Individual i2 = builder.withReactionTime(7).build();
        Individual i3 = builder.withReactionTime(0).build();
        Room room = mockRoom(i1, i2, i3);
        RoomCell cell = generateCell(room, i1);
        generateCell(room, i2);
        generateCell(room, i3);
        
        ReactionRuleAllPersonsInRoom rule = generateRule();
        context.checking(new Expectations() {{
                exactly(2).of(es).getTimeStep();
                will(returnValue(0));
        }});
        rule.execute(cell);
        assertThat(i1.isAlarmed(), is(false));
        assertThat(i2.isAlarmed(), is(false));
        assertThat(i3.isAlarmed(), is(false));
    }

    private ReactionRuleAllPersonsInRoom generateRule() {
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        es = context.mock(EvacuationState.class);
        context.checking(new Expectations() {{
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
        }});
        ReactionRuleAllPersonsInRoom rule = new ReactionRuleAllPersonsInRoom();
        rule.setEvacuationSimulationProblem(es);
        return rule;
    }
    
    private Room mockRoom(Individual ... individuals) {
        Room room = context.mock(Room.class);
        context.checking(new Expectations() {{
                allowing(room).getID();
                will(returnValue(1));
                for( Individual i : individuals) {
                    allowing(room).addIndividual(with(any(EvacCell.class)), with(i));
                }
                allowing(room).getIndividuals();
                will(returnValue(Arrays.asList(individuals)));
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
