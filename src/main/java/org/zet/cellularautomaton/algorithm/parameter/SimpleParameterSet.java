/* zet evacuawtion tool copyright (c) 2007-20 zet evacuation team
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

/**
 * @author Jan-Philipp Kappmeier
 */
public class SimpleParameterSet extends AbstractParameterSet {

    public SimpleParameterSet() {
        super(0, 1, 0, 0, 0, 4);
    }

    @Override
    public double getSpeedFromAge(double pAge) {
        return 1;
    }

    @Override
    public double getSlacknessFromDecisiveness(double pDecisiveness) {
        return (1 - pDecisiveness) * 0.25;
    }

    @Override
    public double getExhaustionFromAge(double pAge) {
        return 0.1;
    }

    /**
     * Returns a reactin time of 5.
     *
     * @return 5
     */
    @Override
    public double getReactionTimeFromAge(double pAge) {
        return 1;
    }


    @Override
    public double getAbsoluteMaxSpeed() {
        return 1.8;
    }

    @Override
    public double getReactionTime() {
        return 0;
    }

    @Override
    public double getPanicWeightOnPotentials() {
        return 0;
    }

    @Override
    public double getPanicThreshold() {
        return 0;
    }

    @Override
    public double panicWeightOnSpeed() {
        return 0;
    }

    @Override
    public double exhaustionWeightOnSpeed() {
        return 0;
    }

    @Override
    public double slacknessToIdleRatio() {
        return 0;
    }

    @Override
    public double panicToProbOfPotentialChangeRatio() {
        return 0;
    }

    @Override
    public double getPanicDecrease() {
        return 0;
    }

    @Override
    public double getPanicIncrease() {
        return 0;
    }
}
