package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jmock.AbstractExpectations.returnValue;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.results.ReactionAction;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ReactionRuleAllPersonsInRoomTest {
    private final Mockery context = new Mockery();
    EvacuationState es;
    private final static IndividualBuilder BUILDER = new IndividualBuilder();
            
    @Test
    public void testSingleIndividual() {
        Individual i = BUILDER.build();
        IndividualProperty ip = new IndividualProperty(i);
        Room room = mockRoom(i);
        RoomCell cell = generateCell(room, i, ip);
        
        ReactionRuleAllPersonsInRoom rule = generateRule();
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
                allowing(es).propertyFor(i);
                will(returnValue(ip));
        }});
        ReactionAction action = rule.execute(cell).get();
        assertThat(action.getIndividuals(), contains(i));
        assertThat(ip.isAlarmed(), is(false));
    }

    @Test
    public void testMultipleIndividuals() {
        Individual i1 = BUILDER.build();
        IndividualProperty ip1 = new IndividualProperty(i1);
        Individual i2 = BUILDER.build();
        IndividualProperty ip2 = new IndividualProperty(i2);
        Individual i3 = BUILDER.build();
        IndividualProperty ip3 = new IndividualProperty(i3);
        Room room = mockRoom(i1, i2, i3);
        RoomCell cell = generateCell(room, i1, ip1);
        generateCell(room, i2, ip2);
        generateCell(room, i3, ip3);
        
        ReactionRuleAllPersonsInRoom rule = generateRule();
        context.checking(new Expectations() {{
                exactly(3).of(es).getTimeStep();
                will(returnValue(0));
                allowing(es).propertyFor(i1);
                will(returnValue(ip1));
                allowing(es).propertyFor(i2);
                will(returnValue(ip2));
                allowing(es).propertyFor(i3);
                will(returnValue(ip3));
        }});
        ReactionAction a = rule.execute(cell).get();
        assertThat(a.getIndividuals(), containsInAnyOrder(i1, i2, i3));
        assertThat(ip1.isAlarmed(), is(false));
        assertThat(ip2.isAlarmed(), is(false));
        assertThat(ip3.isAlarmed(), is(false));
    }
    
    @Test
    public void testNotAllReady() {
        Individual i1 = BUILDER.build();
        IndividualProperty ip1 = new IndividualProperty(i1);
        Individual i2 = BUILDER.withReactionTime(7).build();
        IndividualProperty ip2 = new IndividualProperty(i2);
        Individual i3 = BUILDER.withReactionTime(0).build();
        IndividualProperty ip3 = new IndividualProperty(i3);
        Room room = mockRoom(i1, i2, i3);
        RoomCell cell = generateCell(room, i1, ip1);
        generateCell(room, i2, ip2);
        generateCell(room, i3, ip3);
        
        ReactionRuleAllPersonsInRoom rule = generateRule();
        context.checking(new Expectations() {{
                exactly(2).of(es).getTimeStep();
                will(returnValue(0));
                allowing(es).propertyFor(i1);
                will(returnValue(ip1));
                allowing(es).propertyFor(i2);
                will(returnValue(ip2));
                allowing(es).propertyFor(i3);
                will(returnValue(ip3));
        }});
        Optional<ReactionAction> noAction = rule.execute(cell);
        assertThat(noAction.isPresent(), is(false));
        assertThat(ip1.isAlarmed(), is(false));
        assertThat(ip2.isAlarmed(), is(false));
        assertThat(ip3.isAlarmed(), is(false));
    }

    private ReactionRuleAllPersonsInRoom generateRule() {
        MultiFloorEvacuationCellularAutomaton eca = new MultiFloorEvacuationCellularAutomaton();
        es = context.mock(EvacuationState.class);
        context.checking(new Expectations() {{
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
        }});
        ReactionRuleAllPersonsInRoom rule = new ReactionRuleAllPersonsInRoom();
        rule.setEvacuationState(es);
        EvacuationSimulationSpeed sp = new EvacuationSimulationSpeed(1);
        rule.setEvacuationSimulationSpeed(sp);
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
    
    private RoomCell generateCell(Room room, Individual i, IndividualProperty ip) {
        RoomCell cell = new RoomCell(1, 0, 0, Objects.requireNonNull(room));
        cell.getState().setIndividual(Objects.requireNonNull(i));
        ip.setCell(cell);
        return cell;
    }

}
