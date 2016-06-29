/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
    public double getAbsoluteMaxSpeed() {
        return 1.8;
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
        return 5;
    }

    @Override
    public double getReactionTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getPanicWeightOnPotentials() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getPanicThreshold() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double panicWeightOnSpeed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double exhaustionWeightOnSpeed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double slacknessToIdleRatio() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double panicToProbOfPotentialChangeRatio() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getPanicDecrease() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getPanicIncrease() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
