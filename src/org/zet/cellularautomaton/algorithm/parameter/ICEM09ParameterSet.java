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

import ds.PropertyContainer;

/**
 * @author Sylvie Temme
 */
public class ICEM09ParameterSet extends AbstractParameterSet {

    protected final double panicToProbabilityOfPotentialChangeRatio;
    protected final double slacknessToIdleRatio;
    protected final double panicDecrease;
    protected final double panicIncrease;
    protected final double panicWeightOnSpeed;
    protected final double panicWeightOnPotentials;
    protected final double exhaustionWeightOnSpeed;
    protected final double panicThreshold;

    /**
     * Initializes the default parameter set and loads some constants from the property container.
     */
    public ICEM09ParameterSet() {
        panicToProbabilityOfPotentialChangeRatio = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO");
        slacknessToIdleRatio = PropertyContainer.getGlobal().getAsDouble("algo.ca.SLACKNESS_TO_IDLE_RATIO");
        panicDecrease = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_DECREASE");
        panicIncrease = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_INCREASE");
        panicWeightOnSpeed = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_WEIGHT_ON_SPEED");
        panicWeightOnPotentials = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_WEIGHT_ON_POTENTIALS");
        exhaustionWeightOnSpeed = PropertyContainer.getGlobal().getAsDouble("algo.ca.EXHAUSTION_WEIGHT_ON_SPEED");
        panicThreshold = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_THRESHOLD");
    }

    @Override
    public double getAbsoluteMaxSpeed() {
        return ABSOLUTE_MAX_SPEED;
    }

    @Override
    public double slacknessToIdleRatio() {
        return slacknessToIdleRatio;
    }

    @Override
    public double panicToProbOfPotentialChangeRatio() {
        return panicToProbabilityOfPotentialChangeRatio;
    }

    @Override
    public double getPanicIncrease() {
        return panicIncrease;
    }

    @Override
    public double getPanicDecrease() {
        return panicDecrease;
    }

    @Override
    public double panicWeightOnSpeed() {
        return panicWeightOnSpeed;
    }

    ////* Conversion parameters *////
    @Override
    public double getSpeedFromAge(double pAge) {
        return 0.595;
    }

    @Override
    public double getSlacknessFromDecisiveness(double pDecisiveness) {
        return 0;
    }

    @Override
    public double getExhaustionFromAge(double pAge) {
        return 0;
    }

    @Override
    public double getReactionTimeFromAge(double pAge) {
        return 0;
    }

    @Override
    public double getReactionTime() {
        return 1;
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
    public double exhaustionWeightOnSpeed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
