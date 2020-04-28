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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.results.VoidAction;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AbstractPotentialChangeRuleTest {

    private final static IndividualBuilder builder = new IndividualBuilder();
    private final Mockery context = new Mockery();
    private EvacuationState es;

    @Before
    public void initState() {
        es = context.mock(EvacuationState.class);
    }

    private class FakeAbstractPotentialChangeRule extends AbstractPotentialChangeRule {

        private final boolean wantsToChange;

        public FakeAbstractPotentialChangeRule(EvacuationState es, boolean wantsToChange) {
            this.wantsToChange = wantsToChange;
            this.setEvacuationState(es);

        }

        @Override
        protected boolean wantsToChange(Individual i) {
            return wantsToChange;
        }

        @Override
        protected VoidAction onExecute(EvacCellInterface cell) {
            return VoidAction.VOID_ACTION;
        }
    };

    private EvacCell createCell(boolean occupied, boolean safe) {
        EvacCell cell = new RoomCell(0, 0);
        if (occupied) {
            Individual i = builder.build();
            IndividualProperty ip = new IndividualProperty(i);
            context.checking(new Expectations() {
                {
                    allowing(es).propertyFor(i);
                    will(returnValue(ip));
                }
            });
            cell.getState().setIndividual(i);
            ip.setCell(cell);
            if (safe) {
            }
        }
        return cell;
    }

    @Test
    public void notExecuteableIfNotWillingToChange() {
        AbstractPotentialChangeRule rule = new FakeAbstractPotentialChangeRule(es, false);
        assertThat(rule, is(not(executeableOn(createCell(true, false)))));
        assertThat(rule, is(not(executeableOn(createCell(false, false)))));
    }

    @Test
    public void executeableIfWillingToChange() {
        AbstractPotentialChangeRule rule = new FakeAbstractPotentialChangeRule(es, true);

        assertThat(rule, is(executeableOn(createCell(true, false))));
        assertThat(rule, is(not(executeableOn(createCell(false, false)))));
    }

}
