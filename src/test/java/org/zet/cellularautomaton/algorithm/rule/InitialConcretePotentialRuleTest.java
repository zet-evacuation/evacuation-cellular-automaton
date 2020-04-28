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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.results.DieAction;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class InitialConcretePotentialRuleTest {

    private final static IndividualBuilder INDIVIDUAL_BUILDER = new IndividualBuilder();
    private final Mockery context = new Mockery();
    InitialConcretePotentialRule rule;
    EvacCell cell;
    Individual i;
    IndividualProperty ip;
    private EvacuationCellularAutomaton eca;
    EvacuationState es;
    EvacuationStateControllerInterface ec;
    private List<Exit> exitList;

    private void init() {
        init(0);
    }

    private void init(double familiarity) {
        rule = new InitialConcretePotentialRule();
        Room room = context.mock(Room.class);
        es = context.mock(EvacuationState.class);
        eca = context.mock(EvacuationCellularAutomaton.class);
        i = INDIVIDUAL_BUILDER.withAge(30).withFamiliarity(familiarity).build();
        ip = new IndividualProperty(i);
        ec = context.mock(EvacuationStateControllerInterface.class);
        exitList = new LinkedList<>();
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(eca).getExits();
                will(returnValue(exitList));
                allowing(room).getID();
                will(returnValue(1));
                allowing(room).getXOffset();
                allowing(room).getYOffset();
                allowing(room).getFloor();
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).propertyFor(i);
                will(returnValue(ip));
            }
        });
        cell = new RoomCell(1, 0, 0, room);
        ip.setCell(cell);
        cell.getState().setIndividual(i);

        rule.setEvacuationState(es);
    }

    @Test
    public void testAppliccableIfNotEmpty() {
        init();
        cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));

        cell.getState().setIndividual(i);
        assertThat(rule, is(executeableOn(cell)));
    }

    @Test
    public void testNotApplicableIfPotentialSet() {
        init();
        StaticPotential sp = new StaticPotential();
        ip.setStaticPotential(sp);
        assertThat(rule, is(not(executeableOn(cell))));
    }

    @Test
    public void testDeadIfNoPotentials() {
        init();

        DieAction a = (DieAction)rule.execute(cell).get();

        assertThat(a.getDeathCause(), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
        assertThat(a.getIndividual(), is(equalTo(i)));
    }

    @Test
    public void testDeadIfPotentialsBad() {
        init();
        StaticPotential sp = new StaticPotential();
        addStaticPotential(sp, 0);

        DieAction a = (DieAction)rule.execute(cell).get();

        assertThat(a.getDeathCause(), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
        assertThat(a.getIndividual(), is(equalTo(i)));
    }

    @Test
    public void testSinglePotentialTaken() {
        init();
        StaticPotential sp = new StaticPotential();
        sp.setPotential(cell, 1);

        addStaticPotential(sp, 0);

        rule.execute(cell);
        assertThat(ip.getStaticPotential(), is(same(sp)));
    }

    @Test
    public void testHighFamiliarityChoosesBest() {
        init(1);
        StaticPotential targetPotential = initFamiliarPotential();

        rule.execute(cell);
        assertThat(ip.isDead(), is(false));
        assertThat(ip.getStaticPotential(), is(same(targetPotential)));
    }

    @Test
    public void testLowFamiliarityChoosesAttractive() {
        init(0);
        StaticPotential targetPotential = initUnfamiliarPotential();

        rule.execute(cell);
        assertThat(ip.isDead(), is(false));
        assertThat(ip.getStaticPotential(), is(same(targetPotential)));
    }

    @Test
    public void testMediumFamiliarity() {
        init(0.5);
        StaticPotential targetPotential = initMediumPotential();

        rule.execute(cell);
        assertThat(ip.isDead(), is(false));
        assertThat(ip.getStaticPotential(), is(same(targetPotential)));
    }

    @Test
    public void testAttractivePotentialShort() {
        init(0.5);
        StaticPotential targetPotential = initAttractiveShortPotential();

        rule.execute(cell);
        assertThat(ip.isDead(), is(false));
        assertThat(ip.getStaticPotential(), is(same(targetPotential)));
    }

    private StaticPotential initFamiliarPotential() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setPotential(cell, 1);
        mediumDistance.setPotential(cell, 2);
        longDistance.setPotential(cell, 3);

//        shortDistance.setAttractivity(10);
//        mediumDistance.setAttractivity(50);
//        longDistance.setAttractivity(100);

        addStaticPotential(longDistance, 100);
        addStaticPotential(shortDistance, 10);
        addStaticPotential(mediumDistance, 50);
        return shortDistance;
    }

    private StaticPotential initUnfamiliarPotential() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setPotential(cell, 1);
        mediumDistance.setPotential(cell, 2);
        longDistance.setPotential(cell, 3);
//        shortDistance.setAttractivity(10);
//        mediumDistance.setAttractivity(50);
//        longDistance.setAttractivity(100);

        addStaticPotential(longDistance, 100);
        addStaticPotential(shortDistance, 10);
        addStaticPotential(mediumDistance, 50);
        return longDistance;
    }

    private StaticPotential initMediumPotential() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setPotential(cell, 1);
        mediumDistance.setPotential(cell, 2);
        longDistance.setPotential(cell, 3);
//        shortDistance.setAttractivity(10);
//        mediumDistance.setAttractivity(50);
//        longDistance.setAttractivity(100);

        addStaticPotential(longDistance, 100);
        addStaticPotential(shortDistance, 10);
        addStaticPotential(mediumDistance, 50);
        return mediumDistance;
    }

    private StaticPotential initAttractiveShortPotential() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setPotential(cell, 1);
        mediumDistance.setPotential(cell, 2);
        longDistance.setPotential(cell, 3);
        //shortDistance.setAttractivity(1000);
        //mediumDistance.setAttractivity(50);
        //longDistance.setAttractivity(100);

        addStaticPotential(longDistance, 100);
        addStaticPotential(shortDistance, 1000);
        addStaticPotential(mediumDistance, 50);
        return shortDistance;
    }

    private void addStaticPotential(Potential p, int attractivity) {
        Exit e = new Exit("", Collections.emptyList());
        e.setAttractivity(attractivity);
        exitList.add(e);
        context.checking(new Expectations() {
            {
                allowing(eca).getPotentialFor(e);
                will(returnValue(p));
            }
        });
    }
}
