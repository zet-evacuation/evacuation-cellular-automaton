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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ReactionActionTest {

    private final Mockery context = new Mockery();

    private final Individual i1 = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
    private final Individual i2 = new Individual(1, 0, 0, 0, 0, 0, 1, 0);
    private final IndividualProperty ip1 = new IndividualProperty(i1);
    private final IndividualProperty ip2 = new IndividualProperty(i2);

    @Test
    public void individualDies() throws InconsistentPlaybackStateException {
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        EvacuationState es = context.mock(EvacuationState.class);
        List<Individual> individuals = Arrays.asList(i1, i2);

        ReactionAction actionUnderTest = new ReactionAction(individuals);

        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i1);
                will(returnValue(ip1));
                allowing(es).propertyFor(i2);
                will(returnValue(ip2));
            }
        });
        actionUnderTest.execute(es, ec);
        assertThat(ip1.isAlarmed(), is(true));
        assertThat(ip2.isAlarmed(), is(true));
    }

    @Test
    public void initialization() throws InconsistentPlaybackStateException {
        List<Individual> individuals = Arrays.asList(i1, i2);

        ReactionAction actionUnderTest = new ReactionAction(individuals);
        String representation = actionUnderTest.toString();

        // Action type contained in String
        assertThat(representation, containsString("ReactionAction"));
        // The individuals in the list are contained in the representation
        assertThat(representation, containsString("0"));
        assertThat(representation, containsString("1"));
    }

}
