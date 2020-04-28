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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.Optional;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.SaveCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.results.Action;
import org.zet.cellularautomaton.results.SaveAction;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SaveIndividualsRuleTest {

    private static final int TIME_STEP = 7;
    private final Mockery context = new Mockery();
    private final States test = context.states("normal-test");
    private SaveIndividualsRule rule;
    private EvacuationStateControllerInterface ec;
    private EvacuationState es;
    private Individual i;
    private IndividualProperty ip;
    private SaveCell cell;

    @Before
    public void init() {
        MultiFloorEvacuationCellularAutomaton eca = new MultiFloorEvacuationCellularAutomaton();
        rule = new SaveIndividualsRule();
        ec = context.mock(EvacuationStateControllerInterface.class);
        es = context.mock(EvacuationState.class);
        rule.setEvacuationState(es);
        i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        ip = new IndividualProperty(i);
        cell = new SaveCell(0, 0);
        cell.getState().setIndividual(i);
        test.become("normal-test");
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).propertyFor(i);
                will(returnValue(ip)); when(test.is("normal-test"));
                allowing(es).getTimeStep();
                will(returnValue(TIME_STEP));
            }
        });
        
    }

    @Test
    public void applicableIfOccupied() {
        SaveCell saveCell = new SaveCell(0, 0);
        ExitCell exitCell = new ExitCell(0, 1);
        RoomCell other = new RoomCell(1, 1);

        saveCell.getState().setIndividual(i);
        exitCell.getState().setIndividual(i);
        other.getState().setIndividual(i);

        assertThat(rule, is(executeableOn(saveCell)));
        assertThat(rule, is(executeableOn(exitCell)));
        assertThat(rule, is(not(executeableOn(other))));
    }

    @Test
    public void notApplicableOnEmptyCell() {
        SaveCell saveCell = new SaveCell(0, 0);
        ExitCell exitCell = new ExitCell(0, 1);
        RoomCell other = new RoomCell(1, 1);

        assertThat(rule, is(not(executeableOn(saveCell))));
        assertThat(rule, is(not(executeableOn(exitCell))));
        assertThat(rule, is(not(executeableOn(other))));
    }

    @Test
    public void unsaveIndividualsSaved() {
        SaveAction a = (SaveAction) rule.execute(cell).get();
        assertThat(a.getSavedIndividual(), is(equalTo(i)));
    }

    @Test
    public void saveIndividualNotSaved() {
        IndividualProperty safeIndividualProperty = new IndividualProperty(i) {

            @Override
            public boolean isSafe() {
                return true;
            }
        };

        test.become("special-property");
        context.checking(new Expectations() {{
                allowing(es).propertyFor(i);
                will(returnValue(safeIndividualProperty));
                when(test.is("special-property"));
            }});
        Optional<Action> noAction = rule.execute(cell);
        assertThat(noAction.isPresent(), is(false));
    }
    
    @Test
    public void exitPotentialSet() {
        ip.setStaticPotential(new StaticPotential());
        
        StaticPotential exitPotential = new StaticPotential();
        cell.setExitPotential(exitPotential);
        
        SaveAction a = (SaveAction) rule.execute(cell).get();
        assertThat(ip.getStaticPotential(), is(sameInstance(exitPotential)));
        assertThat(a.getSavedIndividual(), is(equalTo(i)));
    }

    @Test
    public void exitPotentialNotSetOnExitCell() {
        ExitCell exitCell = new ExitCell(0, 0);
        exitCell.getState().setIndividual(i);

        StaticPotential sp = new StaticPotential();
        ip.setStaticPotential(sp);
        
        SaveAction a = (SaveAction) rule.execute(exitCell).get();
        assertThat(a.getSavedIndividual(), is(equalTo(i)));
    }
}
