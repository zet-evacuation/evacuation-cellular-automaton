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
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Daniel R. Schmidt
 */
public abstract class AbstractParameterSet implements ParameterSet {

    /** The default absolute max speed is such that normal walking is possible. */
    public static final double DEFAULT_ABSOLUTE_MAX_SPEED = 2.1;
    private final double dynamicPotentialWeight;
    private final double staticPotentialWeight;
    private final double probabilityDynamicPotentialIncrease;
    private final double probabilityDynamicPotentialDecrease;
    private final double probabilityFamiliarityOrAttractivityOfExit;
    protected final double absoluteMaxSpeed;

    /**
     * Initializes the default parameter set and loads some constants from the property container. By default, dynamic
     * potential influence is 0 and the static potential weight is set to 1.
     */
    public AbstractParameterSet() {
        dynamicPotentialWeight = getSafe("algo.ca.DYNAMIC_POTENTIAL_WEIGHT", 0);
        staticPotentialWeight = getSafe("algo.ca.STATIC_POTENTIAL_WEIGHT", 1);
        probabilityDynamicPotentialIncrease = getSafe("algo.ca.PROB_DYNAMIC_POTENTIAL_INCREASE", 0);
        probabilityDynamicPotentialDecrease = getSafe("algo.ca.PROB_DYNAMIC_POTENTIAL_DECREASE", 0);
        probabilityFamiliarityOrAttractivityOfExit = getSafe("algo.ca.PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT", 0);
        absoluteMaxSpeed = getSafe("algo.ca.ABSOLUTE_MAX_SPEED", DEFAULT_ABSOLUTE_MAX_SPEED);
    }

    public AbstractParameterSet(double dynamicPotentialWeight, double staticPotentialWeight,
            double probDynamicPotentialIncrease, double probDynamicPotentialDecrease,
            double probFamiliarityOrAttractivityOfExit, double absoluteMaxSpeed) {
        this.dynamicPotentialWeight = dynamicPotentialWeight;
        this.staticPotentialWeight = staticPotentialWeight;
        this.probabilityDynamicPotentialIncrease = probDynamicPotentialIncrease;
        this.probabilityDynamicPotentialDecrease = probDynamicPotentialDecrease;
        this.probabilityFamiliarityOrAttractivityOfExit = probFamiliarityOrAttractivityOfExit;
        this.absoluteMaxSpeed = absoluteMaxSpeed;
    }

    private double getSafe(String parameter, double defaultValue) {
        if (PropertyContainer.getGlobal().isDefined(parameter)) {
            return PropertyContainer.getGlobal().getAsDouble(parameter);
        } else {
            return defaultValue;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return the dynamic potential weight
     * @see algo.ca.parameter.ParameterSet#dynamicPotentialWeight()
     */
    @Override
    public double dynamicPotentialWeight() {
        return dynamicPotentialWeight;
    }

    /**
     * {@inheritDoc}
     *
     * @return the static potential weight
     * @see algo.ca.parameter.ParameterSet#staticPotentialWeight()
     */
    @Override
    public double staticPotentialWeight() {
        return staticPotentialWeight;
    }

    @Override
    public double probabilityDynamicDecrease() {
        return probabilityDynamicPotentialDecrease;
    }

    @Override
    public double probabilityDynamicIncrease() {
        return probabilityDynamicPotentialIncrease;
    }

    @Override
    public double probabilityChangePotentialFamiliarityOrAttractivityOfExitRule() {
        return probabilityFamiliarityOrAttractivityOfExit;
    }

    /**
     * Creates a {@link ParameterSet} of a specified subclass.
     *
     * @param parameterSetName the subclass
     * @return the object of the subclass type.
     */
    public static AbstractParameterSet createParameterSet(String parameterSetName) {
        AbstractParameterSet parameterSet = null;
        try {
            Class<?> parameterSetClass = Class.forName("org.zet.cellularautomaton.algorithm.parameter." + parameterSetName);
            parameterSet = (AbstractParameterSet) parameterSetClass.getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Logger.getLogger(AbstractParameterSet.class.getName()).log(
                    Level.SEVERE, "Cannot instantiate parameter set for '" + parameterSetName + "'", e);
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
        return absoluteMaxSpeed;
    }
}
