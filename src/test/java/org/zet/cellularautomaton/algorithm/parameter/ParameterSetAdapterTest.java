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
import static org.junit.Assert.assertThat;
import static org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSetTest.assertParameterSetDefaults;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ParameterSetAdapterTest {

    @Test
    public void adapterInitialization() {
        final double panicToProbabilityOfPotentialChangeRatio = 0.7;
        final double slacknessToIdleRatio = 2;
        final double panicDecrease = 4;
        final double panicIncrease = 3;
        final double panicWeightOnSpeed = 0.6;
        final double panicWeightOnPotentials = 0.7;
        final double exhaustionWeightOnSpeed = 0.8;
        final double panicThreshold = 5;

        ParameterSet ps = new ParameterSetAdapter(panicToProbabilityOfPotentialChangeRatio, slacknessToIdleRatio,
                panicDecrease, panicIncrease, panicWeightOnSpeed, panicWeightOnPotentials, exhaustionWeightOnSpeed, panicThreshold) {
            @Override
            public double getSpeedFromAge(double pAge) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }

            @Override
            public double getSlacknessFromDecisiveness(double pDecisiveness) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }

            @Override
            public double getExhaustionFromAge(double pAge) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }

            @Override
            public double getReactionTimeFromAge(double pAge) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }
        };

        assertParameterSetDefaults(ps);
        assertAdaptedParameters(ps, panicToProbabilityOfPotentialChangeRatio, slacknessToIdleRatio, panicDecrease, panicIncrease,
                panicWeightOnSpeed, panicWeightOnPotentials, exhaustionWeightOnSpeed, panicThreshold);
    }

    public static void assertAdaptedParameters(ParameterSet ps, double panicToProbabilityOfPotentialChangeRatio,
            double slacknessToIdleRatio, double panicDecrease, double panicIncrease, double panicWeightOnSpeed,
            double panicWeightOnPotential, double exhaustionWeightOnSpeed, double panicThreshold) {
        assertThat(Double.doubleToLongBits(ps.panicToProbOfPotentialChangeRatio()),
                is(equalTo(Double.doubleToLongBits(panicToProbabilityOfPotentialChangeRatio))));
        assertThat(Double.doubleToLongBits(ps.slacknessToIdleRatio()), is(equalTo(Double.doubleToLongBits(slacknessToIdleRatio))));
        assertThat(Double.doubleToLongBits(ps.getPanicDecrease()), is(equalTo(Double.doubleToLongBits(panicDecrease))));
        assertThat(Double.doubleToLongBits(ps.getPanicIncrease()), is(equalTo(Double.doubleToLongBits(panicIncrease))));
        assertThat(Double.doubleToLongBits(ps.panicWeightOnSpeed()), is(equalTo(Double.doubleToLongBits(panicWeightOnSpeed))));
        assertThat(Double.doubleToLongBits(ps.getPanicWeightOnPotentials()),
                is(equalTo(Double.doubleToLongBits(panicWeightOnPotential))));
        assertThat(Double.doubleToLongBits(ps.exhaustionWeightOnSpeed()),
                is(equalTo(Double.doubleToLongBits(exhaustionWeightOnSpeed))));
        assertThat(Double.doubleToLongBits(ps.getPanicThreshold()), is(equalTo(Double.doubleToLongBits(panicThreshold))));
    }
    
}
