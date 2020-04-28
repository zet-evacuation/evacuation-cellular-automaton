/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;

import java.util.Objects;
import static org.hamcrest.Matchers.contains;
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
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.results.ReactionAction;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ReactionRuleCompleteRoomTest {
    private final Mockery context = new Mockery();
    private EvacuationState es;
    private final static IndividualBuilder BUILDER = new IndividualBuilder();

    @Test
    public void roomAlerted() {
        Individual i = BUILDER.build();
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
        ReactionAction a = (ReactionAction)rule.execute(cell).get();
        assertThat(a.getIndividuals(), contains(i));
    }
    
    @Test
    public void roomNotAlerted() {
        Individual i = BUILDER.withAge(0).withReactionTime(7).buildAndReset();
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
        Individual i = BUILDER.withAge(0).withReactionTime(7).build();
        IndividualProperty ip = new IndividualProperty(i);
        Room room = mockRoom(i, true, 0);
        RoomCell cell = generateCell(room, i, ip);

        ReactionRuleCompleteRoom rule = generateReactionRule();
        context.checking(new Expectations() {{
                allowing(es).propertyFor(i);
                will(returnValue(ip));
        }});

        ReactionAction a = rule.execute(cell).get();
        assertThat(a.getIndividuals(), contains(i));
    }

    private ReactionRuleCompleteRoom generateReactionRule() {
        es = context.mock(EvacuationState.class);
        MultiFloorEvacuationCellularAutomaton eca = new MultiFloorEvacuationCellularAutomaton();
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
            }
        });
        ReactionRuleCompleteRoom rule = new ReactionRuleCompleteRoom();
        rule.setEvacuationState(es);
        EvacuationSimulationSpeed sp = new EvacuationSimulationSpeed(1);
        rule.setEvacuationSimulationSpeed(sp);
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
