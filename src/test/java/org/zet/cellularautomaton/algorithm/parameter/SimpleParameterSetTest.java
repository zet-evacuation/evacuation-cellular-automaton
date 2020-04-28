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
package org.zet.cellularautomaton.algorithm.parameter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSetTest.assertParameterSetDefaults;
import static org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSetTest.assertSpeedFromAge;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SimpleParameterSetTest {

    @Test
    public void initialization() {
        ParameterSet ps = new SimpleParameterSet();

        assertParameterSetDefaults(ps);
        assertSpeedFromAge(ps);
    }

    @Test
    public void simpleParameterValues() {
        // almost all functions are zero.
        ParameterSet ps = new SimpleParameterSet();
        assertThat(Double.doubleToLongBits(ps.getReactionTime()), is(equalTo(0L)));
        assertThat(Double.doubleToLongBits(ps.getPanicWeightOnPotentials()), is(equalTo(0L)));
        assertThat(Double.doubleToLongBits(ps.getPanicThreshold()), is(equalTo(0L)));
        assertThat(Double.doubleToLongBits(ps.panicWeightOnSpeed()), is(equalTo(0L)));
        assertThat(Double.doubleToLongBits(ps.exhaustionWeightOnSpeed()), is(equalTo(0L)));
        assertThat(Double.doubleToLongBits(ps.slacknessToIdleRatio()), is(equalTo(0L)));
        assertThat(Double.doubleToLongBits(ps.panicToProbOfPotentialChangeRatio()), is(equalTo(0L)));
        assertThat(Double.doubleToLongBits(ps.getPanicDecrease()), is(equalTo(0L)));
        assertThat(Double.doubleToLongBits(ps.getPanicIncrease()), is(equalTo(0L)));
    }

    @Test
    public void creationParameters() {
        ParameterSet ps = new SimpleParameterSet();
        assertThat(ps.getSlacknessFromDecisiveness(0.6), is(closeTo(0.1, 10e-7)));
        assertThat(ps.getExhaustionFromAge(0.537), is(closeTo(0.1, 10e-7)));
        assertThat(ps.getReactionTimeFromAge(0.537), is(closeTo(1, 10e-7)));
    }
}
