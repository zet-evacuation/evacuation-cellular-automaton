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
 * An abstract class defining all methods that parameter sets have to support.
 *
 * @author Jan-Philipp Kappmeier
 * @author Daniel R. Schmidt
 */
public interface ParameterSet {
    /* Updating of dynamic parameters */

    public double PANIC_WEIGHT_ON_POTENTIALS();

    double PANIC_THRESHOLD();

    /* Some constants*/
    public double dynamicPotentialWeight();

    public double staticPotentialWeight();

    public double probabilityDynamicIncrease();

    public double probabilityDynamicDecrease();

    public double probabilityChangePotentialFamiliarityOrAttractivityOfExitRule();

    double getAbsoluteMaxSpeed();
    
    /* Other dynamic parameters */
    public double getSpeedFromAge(double pAge);

    public double getSlacknessFromDecisiveness(double pDecisiveness);

    public double getExhaustionFromAge(double pAge);

    public double getReactionTimeFromAge(double pAge);

    public double getReactionTime();

    public double panicWeightOnSpeed();

    public double exhaustionWeightOnSpeed();

    public double slacknessToIdleRatio();

    public double panicToProbOfPotentialChangeRatio();

    public double getPanicDecrease();

    public double getPanicIncrease();
}
