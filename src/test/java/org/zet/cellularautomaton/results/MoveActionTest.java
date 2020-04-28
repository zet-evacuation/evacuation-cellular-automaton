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
package org.zet.cellularautomaton.results;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;

public class MoveActionTest {

    Mockery context = new Mockery();

    EvacCell from = new RoomCell(0, 0);
    EvacCell to = new RoomCell(1, 0);
    Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
    IndividualProperty ip = new IndividualProperty(i);

    @Test
    public void test() {
        from.getState().setIndividual(i);
        MoveAction action = new MoveAction(from, to, 3, 4);
        assertThat(action.startTime(), is(equalTo(4.0)));
        assertThat(action.arrivalTime(), is(equalTo(3.0)));
    }

    @Test
    public void execute() throws InconsistentPlaybackStateException {
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        EvacuationState es = context.mock(EvacuationState.class);
        from.getState().setIndividual(i);
        IndividualProperty ip = new IndividualProperty(i);

        MoveAction action = new MoveAction(from, to, 4, 3);

        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(ip));
                exactly(1).of(ec).move(with(from), with(to));
                exactly(1).of(ec).increaseDynamicPotential(with(to));
            }
        });

        action.execute(es, ec);

        assertThat(ip.getStepStartTime(), is(equalTo(3d)));
        assertThat(ip.getStepEndTime(), is(equalTo(4d)));
        
        context.assertIsSatisfied();
    }
    
    @Test
    public void executeOnOther() throws InconsistentPlaybackStateException {
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        from.getState().setIndividual(i);
        MoveAction action = new MoveAction(from, to, 3, 4);
        EvacuationState es = context.mock(EvacuationState.class);
        IndividualProperty ip = new IndividualProperty(i);

        // New set        
        EvacCell otherFrom = new RoomCell(0, 0);
        EvacCell otherTo = new RoomCell(1, 0);
        Individual otheri = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(ip));
                exactly(1).of(ec).move(with(otherFrom), with(otherTo));
                exactly(1).of(ec).increaseDynamicPotential(with(otherTo));
            }
        });

        action.execute(es, ec);

        context.assertIsSatisfied();
    }

}
