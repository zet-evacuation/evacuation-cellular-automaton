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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSetTest.assertParameterSetDefaults;
import static org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSetTest.assertSpeedFromAge;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultParameterSetTest {

    @Test
    public void initialization() {
        ParameterSet ps = new DefaultParameterSet();

        assertParameterSetDefaults(ps);
        assertSpeedFromAge(ps);

        final double defaultPanicThreshold = 3;
        ParameterSetAdapterTest.assertAdaptedParameters(ps, 0, 0, 0, 0, 0, 0, 0, defaultPanicThreshold);
        assertExhaustionFromAge(ps);
    }

    public static void assertExhaustionFromAge(ParameterSet ps) {
        for (int age = 0; age <= 100; ++age) {
            assertThat(ps.getExhaustionFromAge(age), is(greaterThanOrEqualTo(0.0)));
        }
    }

}
