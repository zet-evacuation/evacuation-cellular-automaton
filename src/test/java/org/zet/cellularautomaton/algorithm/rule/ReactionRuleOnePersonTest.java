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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.jmock.AbstractExpectations.returnValue;

import java.util.Optional;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.results.ReactionAction;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ReactionRuleOnePersonTest {
    private final Mockery context = new Mockery();
    private MultiFloorEvacuationCellularAutomaton eca = new MultiFloorEvacuationCellularAutomaton();
    private EvacuationState es;
    private final static IndividualBuilder INDIVIDUAL_BUILDER = new IndividualBuilder();

    @Test
    public void alertsImmediately() {
        ReactionRuleOnePerson rule = new ReactionRuleOnePerson();
        rule.setEvacuationState(es);
        rule.setEvacuationSimulationSpeed(new EvacuationSimulationSpeed(0.4));

        RoomCell cell = new RoomCell(0, 0);
        Individual i = INDIVIDUAL_BUILDER.build();
        IndividualProperty ip = new IndividualProperty(i);
        cell.getState().setIndividual(i);

        assertThat(ip.isAlarmed(), is(false));
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
                allowing(es).propertyFor(i);
                will(returnValue(ip));
        }});
        ReactionAction a = rule.execute(cell).get();
        assertThat(a.getIndividuals(), contains(i));
    }
    
    @Test
    public void alertLate() {
        ReactionRuleOnePerson rule = new ReactionRuleOnePerson();
        rule.setEvacuationState(es);

        // We need an absolute max speed of 0.41 to pass the following assertions, i.e. after 7 steps the individual is activated
        EvacuationSimulationSpeed sp = new EvacuationSimulationSpeed(0.41);
        rule.setEvacuationSimulationSpeed(sp);

        RoomCell cell = new RoomCell(0, 0);
        Individual evacuee = INDIVIDUAL_BUILDER.withAge(0).withReactionTime(7).buildAndReset();
        IndividualProperty ip = new IndividualProperty(evacuee);
        cell.getState().setIndividual(evacuee);
        
        assertThat(ip.isAlarmed(), is(false));
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
                allowing(es).propertyFor(evacuee);
                will(returnValue(ip));
        }});
        Optional<ReactionAction> noAction = rule.execute(cell);
        assertThat(noAction.isPresent(), is(false));
        
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(0));
        }});
        rule.execute(cell);
        for( int i = 0; i < 7; ++i) {
            final int result = i+1;
            context.checking(new Expectations() {{
                    exactly(1).of(es).getTimeStep();
                    will(returnValue(result));
            }});
            noAction = rule.execute(cell);
            assertThat(noAction.isPresent(), is(false));
        }
        // Individuals reaction time is 7 
        // one additional time steps sets time to 7.175
        context.checking(new Expectations() {{
                exactly(1).of(es).getTimeStep();
                will(returnValue(8));
        }});
        ReactionAction a = rule.execute(cell).get();
        assertThat(a.getIndividuals(), contains(evacuee));
    }

    @Before
    public void initEvacuationProblem() {
        es = context.mock(EvacuationState.class);
        eca = new MultiFloorEvacuationCellularAutomaton();
        context.checking(new Expectations() {{
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
        }});
    }
}
