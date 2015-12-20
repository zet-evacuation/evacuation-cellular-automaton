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

import java.util.Collection;

import ds.PropertyContainer;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Daniel R. Schmidt
 *
 */
public abstract class AbstractParameterSet implements ParameterSet {

    final private double DYNAMIC_POTENTIAL_WEIGHT;
    final private double STATIC_POTENTIAL_WEIGHT;
    final private double PROB_DYNAMIC_POTENTIAL_INCREASE;
    final private double PROB_DYNAMIC_POTENTIAL_DECREASE;
    final private double PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT;
    final protected double ABSOLUTE_MAX_SPEED;

    /**
     * Initializes the default parameter set and loads some constants from the property container.
     */
    public AbstractParameterSet() {
        DYNAMIC_POTENTIAL_WEIGHT = PropertyContainer.getGlobal().getAsDouble("algo.ca.DYNAMIC_POTENTIAL_WEIGHT");
        STATIC_POTENTIAL_WEIGHT = PropertyContainer.getGlobal().getAsDouble("algo.ca.STATIC_POTENTIAL_WEIGHT");
        PROB_DYNAMIC_POTENTIAL_INCREASE = PropertyContainer.getGlobal().getAsDouble("algo.ca.PROB_DYNAMIC_POTENTIAL_INCREASE");
        PROB_DYNAMIC_POTENTIAL_DECREASE = PropertyContainer.getGlobal().getAsDouble("algo.ca.PROB_DYNAMIC_POTENTIAL_DECREASE");
        PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT = PropertyContainer.getGlobal().getAsDouble("algo.ca.PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT");
        ABSOLUTE_MAX_SPEED = PropertyContainer.getGlobal().getAsDouble("algo.ca.ABSOLUTE_MAX_SPEED");
    }

    public AbstractParameterSet(double dynamicPotentialWeight, double staticPotentialWeight,
            double ProbDynamicPotentialIncrease, double ProbDynamicPotentialDecrease,
            double probFamiliarityOrAttractivityOfExit, double absoluteMaxSpeed) {
        this.DYNAMIC_POTENTIAL_WEIGHT = dynamicPotentialWeight;
        this.STATIC_POTENTIAL_WEIGHT = staticPotentialWeight;
        this.PROB_DYNAMIC_POTENTIAL_INCREASE = ProbDynamicPotentialIncrease;
        this.PROB_DYNAMIC_POTENTIAL_DECREASE = ProbDynamicPotentialDecrease;
        this.PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT = probFamiliarityOrAttractivityOfExit;
        this.ABSOLUTE_MAX_SPEED = absoluteMaxSpeed;
    }
    
    

    /**
     * {@inheritDoc}
     *
     * @return the dynamic potential weight
     * @see algo.ca.parameter.ParameterSet#dynamicPotentialWeight()
     */
    @Override
    public double dynamicPotentialWeight() {
        return DYNAMIC_POTENTIAL_WEIGHT;
    }

    /**
     * {@inheritDoc}
     *
     * @return the static potential weight
     * @see algo.ca.parameter.ParameterSet#staticPotentialWeight()
     */
    @Override
    public double staticPotentialWeight() {
        return STATIC_POTENTIAL_WEIGHT;
    }

    @Override
    public double probabilityDynamicDecrease() {
        return PROB_DYNAMIC_POTENTIAL_DECREASE;
    }

    @Override
    public double probabilityDynamicIncrease() {
        return PROB_DYNAMIC_POTENTIAL_INCREASE;
    }

    @Override
    public double probabilityChangePotentialFamiliarityOrAttractivityOfExitRule() {
        return PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT;
    }

    @Override
    public abstract double changePotentialThreshold(Individual individual);

    @Override
    public abstract double effectivePotential(EvacCell referenceCell, EvacCell targetCell);

    @Override
    public abstract double idleThreshold(Individual individual);

    @Override
    public abstract double movementThreshold(Individual individual);

    @Override
    public abstract double updateExhaustion(Individual individual, EvacCell targetCell);

    @Override
    public abstract double updatePanic(Individual individual, EvacCell targetCell, Collection<EvacCell> preferedCells);

    @Override
    public abstract double updatePreferredSpeed(Individual individual);

    /**
     * Creates a {@link ParameterSet} of a specified subclass.
     *
     * @param parameterSetName the subclass
     * @return the object of the subclass type.
     */
    public static AbstractParameterSet createParameterSet(String parameterSetName) {
        Class<?> parameterSetClass = null;
        AbstractParameterSet parameterSet = null;
        try {
            parameterSetClass = Class.forName("org.zet.cellularautomaton.algorithm.parameter." + parameterSetName);
            parameterSet = (AbstractParameterSet) parameterSetClass.getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace(System.err);
        }
        return parameterSet;
    }

    /**
     * Returns the absolute maximum speed of any evacuee.
     *
     * @return the absolute maximum speed of any evacuee
     */
    @Override
    public double getAbsoluteMaxSpeed() {
        return ABSOLUTE_MAX_SPEED;
    }

}
