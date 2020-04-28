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

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class ParameterSetAdapter extends AbstractParameterSet {

    protected final double panicToProbabilityOfPotentialChangeRatio;
    protected final double slacknessToIdleRatio;
    protected final double panicDecrease;
    protected final double panicIncrease;
    protected final double panicWeightOnSpeed;
    protected final double panicWeightOnPotentials;
    protected final double exhaustionWeightOnSpeed;
    protected final double panicThreshold;

    public ParameterSetAdapter(double panicToProbabilityOfPotentialChangeRatio, double slacknessToIdleRatio,
            double panicDecrease, double panicIncrease, double panicWeightOnSpeed, double panicWeightOnPotentials,
            double exhaustionWeightOnSpeed, double panicThreshold) {
        this.panicToProbabilityOfPotentialChangeRatio = panicToProbabilityOfPotentialChangeRatio;
        this.slacknessToIdleRatio = slacknessToIdleRatio;
        this.panicDecrease = panicDecrease;
        this.panicIncrease = panicIncrease;
        this.panicWeightOnSpeed = panicWeightOnSpeed;
        this.panicWeightOnPotentials = panicWeightOnPotentials;
        this.exhaustionWeightOnSpeed = exhaustionWeightOnSpeed;
        this.panicThreshold = panicThreshold;
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

    @Override
    public double exhaustionWeightOnSpeed() {
        return exhaustionWeightOnSpeed;
    }


    @Override
    public double getPanicWeightOnPotentials() {
        return panicWeightOnPotentials;
    }

    @Override
    public double getPanicThreshold() {
        return panicThreshold;
    }

    @Override
    public double getReactionTime() {
        return 1;
    }
}
