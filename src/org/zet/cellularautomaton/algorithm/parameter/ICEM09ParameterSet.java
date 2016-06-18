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
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;

/**
 * @author Sylvie Temme
 */
public class ICEM09ParameterSet extends AbstractParameterSet {
    /* im AbstractParameterSet:
     final protected double DYNAMIC_POTENTIAL_WEIGHT;
     final protected double STATIC_POTENTIAL_WEIGHT;
     final protected double PROB_DYNAMIC_POTENTIAL_INCREASE;
     final protected double PROB_DYNAMIC_POTENTIAL_DECREASE;
     final protected double PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT;
     final protected double ABSOLUTE_MAX_SPEED;
     */

    final protected double PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO;
    final protected double SLACKNESS_TO_IDLE_RATIO;
    final protected double PANIC_DECREASE;
    final protected double PANIC_INCREASE;
    final protected double PANIC_WEIGHT_ON_SPEED;
    final protected double PANIC_WEIGHT_ON_POTENTIALS;
    final protected double EXHAUSTION_WEIGHT_ON_SPEED;
    final protected double PANIC_THRESHOLD;
    private PropertyAccess es;

    /**
     * Initializes the default parameter set and loads some constants from the property container.
     */
    public ICEM09ParameterSet() {
        /* im AbstractParameterSet:
         DYNAMIC_POTENTIAL_WEIGHT = PropertyContainer.getGlobal().getAsDouble( "algo.ca.DYNAMIC_POTENTIAL_WEIGHT" );
         STATIC_POTENTIAL_WEIGHT = PropertyContainer.getGlobal().getAsDouble( "algo.ca.STATIC_POTENTIAL_WEIGHT" );
         PROB_DYNAMIC_POTENTIAL_INCREASE = PropertyContainer.getGlobal().getAsDouble( "algo.ca.PROB_DYNAMIC_POTENTIAL_INCREASE" );
         PROB_DYNAMIC_POTENTIAL_DECREASE = PropertyContainer.getGlobal().getAsDouble( "algo.ca.PROB_DYNAMIC_POTENTIAL_DECREASE" );
         PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT = PropertyContainer.getGlobal().getAsDouble( "algo.ca.PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT" );
         ABSOLUTE_MAX_SPEED = PropertyContainer.getGlobal().getAsDouble( "algo.ca.ABSOLUTE_MAX_SPEED" );
         */
        PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO");
        SLACKNESS_TO_IDLE_RATIO = PropertyContainer.getGlobal().getAsDouble("algo.ca.SLACKNESS_TO_IDLE_RATIO");
        PANIC_DECREASE = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_DECREASE");
        PANIC_INCREASE = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_INCREASE");
        PANIC_WEIGHT_ON_SPEED = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_WEIGHT_ON_SPEED");
        PANIC_WEIGHT_ON_POTENTIALS = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_WEIGHT_ON_POTENTIALS");
        EXHAUSTION_WEIGHT_ON_SPEED = PropertyContainer.getGlobal().getAsDouble("algo.ca.EXHAUSTION_WEIGHT_ON_SPEED");
        PANIC_THRESHOLD = PropertyContainer.getGlobal().getAsDouble("algo.ca.PANIC_THRESHOLD");
    }


	////* Some constants*////
	/* im AbstractParameterSet:
     @Override
     public double dynamicPotentialWeight(){
     return DYNAMIC_POTENTIAL_WEIGHT;
     }
     @Override
     public double staticPotentialWeight(){
     return STATIC_POTENTIAL_WEIGHT;
     }
     @Override
     public double probabilityDynamicIncrease(){
     return PROB_DYNAMIC_POTENTIAL_INCREASE;
     }
     @Override
     public double probabilityDynamicDecrease(){
     return PROB_DYNAMIC_POTENTIAL_DECREASE;
     }
     @Override
     public double probabilityChangePotentialFamiliarityOrAttractivityOfExitRule(){
     return PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT;
     }
     */
    @Override
    public double getAbsoluteMaxSpeed() {
        return ABSOLUTE_MAX_SPEED;
    }

    @Override
    public double slacknessToIdleRatio() {
        return SLACKNESS_TO_IDLE_RATIO;
    }

    @Override
    public double panicToProbOfPotentialChangeRatio() {
        return PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO;
    }

    @Override
    public double getPanicIncrease() {
        return PANIC_INCREASE;
    }

    @Override
    public double getPanicDecrease() {
        return PANIC_DECREASE;
    }

    @Override
    public double panicWeightOnSpeed() {
        return PANIC_WEIGHT_ON_SPEED;
    }

    @Override
    public double exhaustionWeightOnSpeed() {
        return EXHAUSTION_WEIGHT_ON_SPEED;
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
    public double PANIC_WEIGHT_ON_POTENTIALS() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double PANIC_THRESHOLD() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
