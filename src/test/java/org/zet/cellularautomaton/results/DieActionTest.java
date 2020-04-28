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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DieActionTest {

    private final Mockery context = new Mockery();

    private final Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);

    @Test
    public void individualDies() throws InconsistentPlaybackStateException {
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        EvacuationState es = context.mock(EvacuationState.class);
        EvacCell cell = new RoomCell(0, 0);
        cell.getState().setIndividual(i);

        DieAction actionUnderTest = new DieAction(cell, DeathCause.EXIT_UNREACHABLE, i);
        context.checking(new Expectations() {
            {
                exactly(1).of(ec).die(with(i), with(DeathCause.EXIT_UNREACHABLE));
            }
        });
        actionUnderTest.execute(es, ec);
        context.assertIsSatisfied();
    }
    
    @Test(expected = InconsistentPlaybackStateException.class)
    public void failsInconsistentEmptyCell() throws InconsistentPlaybackStateException {
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        EvacuationState es = context.mock(EvacuationState.class);
        EvacCell cell = new RoomCell(0, 0);

        DieAction actionUnderTest = new DieAction(cell, DeathCause.EXIT_UNREACHABLE, i);
        actionUnderTest.execute(es, ec);
    }

}
