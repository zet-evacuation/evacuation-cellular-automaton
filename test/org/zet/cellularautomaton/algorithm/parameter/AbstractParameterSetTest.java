package org.zet.cellularautomaton.algorithm.parameter;

import ds.PropertyContainer;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AbstractParameterSetTest {
    
    /**
     * Assert that the set can be instantiated using the default constructor without any further initialization and that the resulting
     * values are within the bounds.
     */
    @Test
    public void defaultInitialization() {
        AbstractParameterSet ps = new AbstractParameterSetTestImplementation();

        assertParameterSetDefaults(ps);
    }
    
    public static void assertParameterSetDefaults(ParameterSet ps) {
        assertThat(ps.staticPotentialWeight(), is(greaterThanOrEqualTo(0.0)));
        assertThat(ps.dynamicPotentialWeight(), is(greaterThanOrEqualTo(0.0)));

        assertThat(ps .probabilityDynamicIncrease(), isProbability());
        assertThat(ps .probabilityDynamicDecrease(), isProbability());
        assertThat(ps.probabilityChangePotentialFamiliarityOrAttractivityOfExitRule(), isProbability());

        assertThat(ps.getAbsoluteMaxSpeed(), is(greaterThan(0.0)));
    }
    
    @Test
    public void parameterizedInitialization() {
        double dynamicPotentialWeight = 2.0;
        double staticPotentialWeight = 3.0;
        double probabilityDynamicIncrease = 0.4;
        double probabilityDynamicDecrease = 0.5;
        double probFamiliarityOrAttractivityOfExit = 0.6;
        double absoluteMaxSpeed = 7.0;
        
        AbstractParameterSet ps = new AbstractParameterSetTestImplementation(dynamicPotentialWeight, staticPotentialWeight,
                probabilityDynamicIncrease, probabilityDynamicDecrease, probFamiliarityOrAttractivityOfExit, absoluteMaxSpeed);

        assertValues(ps, dynamicPotentialWeight, staticPotentialWeight, probabilityDynamicIncrease, probabilityDynamicDecrease,
                probFamiliarityOrAttractivityOfExit, absoluteMaxSpeed);
    }

    @Test
    public void initializeWithGlobalPropertyContainer() {
        PropertyContainer.getGlobal().define("algo.ca.DYNAMIC_POTENTIAL_WEIGHT", Double.class, 0.12d);
        PropertyContainer.getGlobal().define("algo.ca.STATIC_POTENTIAL_WEIGHT", Double.class, 0.22d);
        PropertyContainer.getGlobal().define("algo.ca.PROB_DYNAMIC_POTENTIAL_INCREASE", Double.class, 0.32d);
        PropertyContainer.getGlobal().define("algo.ca.PROB_DYNAMIC_POTENTIAL_DECREASE", Double.class, 0.42d);
        PropertyContainer.getGlobal().define("algo.ca.PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT", Double.class, 0.52d);
        PropertyContainer.getGlobal().define("algo.ca.ABSOLUTE_MAX_SPEED", Double.class, 0.92d);
        
        AbstractParameterSet ps = new AbstractParameterSetTestImplementation();

        assertValues(ps, 0.12d, 0.22d, 0.32d, 0.42d, 0.52d, 0.92d);
    }

    public static void assertSpeedFromAge(ParameterSet ps) {
        for (int age = 0; age <= 100; ++age) {
            assertThat(ps.getSpeedFromAge(age), is(lessThanOrEqualTo(ps.getAbsoluteMaxSpeed())));
        }
    }

    private void assertValues(ParameterSet ps, double dynamicPotentialWeight, double staticPotentialWeight,
            double probabilityDynamicIncrease, double probabilityDynamicDecrease, double probFamiliarityOrAttractivityOfExit,
            double absoluteMaxSpeed) {
        assertThat(Double.doubleToLongBits(ps.dynamicPotentialWeight()),
                is(equalTo(Double.doubleToLongBits(dynamicPotentialWeight))));
        assertThat(Double.doubleToLongBits(ps.staticPotentialWeight()), is(equalTo(Double.doubleToLongBits(staticPotentialWeight))));
        assertThat(Double.doubleToLongBits(ps.probabilityDynamicIncrease()),
                is(equalTo(Double.doubleToLongBits(probabilityDynamicIncrease))));
        assertThat(Double.doubleToLongBits(ps.probabilityDynamicDecrease()),
                is(equalTo(Double.doubleToLongBits(probabilityDynamicDecrease))));
        assertThat(Double.doubleToLongBits(ps.probabilityChangePotentialFamiliarityOrAttractivityOfExitRule()),
                is(equalTo(Double.doubleToLongBits(probFamiliarityOrAttractivityOfExit))));
        assertThat(Double.doubleToLongBits(ps.getAbsoluteMaxSpeed()), is(equalTo(Double.doubleToLongBits(absoluteMaxSpeed))));
    }

    @Test
    public void checkCreateFromClassname() {
        ParameterSet ps = AbstractParameterSet.createParameterSet("TestAbstractParameterSet$AbstractParameterSetTestImplementation");
        
        assertParameterSetDefaults(ps);
    }

    @Test
    public void erroneousCreation() {
        ParameterSet ps = AbstractParameterSet.createParameterSet("TestAbstractParameterSet$NonExisting");
        
        assertThat(ps, is(nullValue()));
    }

    public static Matcher<Double> isProbability() {
        return new TypeSafeMatcher<Double>() {

            @Override
            public void describeTo(final Description description) {
                description.appendText("expected probability in ranage [0,1]");
            }

            @Override
            protected boolean matchesSafely(final Double probability) {
                return probability >= 0.0 && probability <= 1.0;
            }

            @Override
            public void describeMismatchSafely(final Double probability, final Description mismatchDescription) {
                mismatchDescription.appendText("was ").appendValue(probability);
            }
        };
    }

    private static class AbstractParameterSetTestImplementation extends AbstractParameterSet {

        public AbstractParameterSetTestImplementation() {
        }

        public AbstractParameterSetTestImplementation(double dynamicPotentialWeight, double staticPotentialWeight,
                double probDynamicPotentialIncrease, double probDynamicPotentialDecrease, double probFamiliarityOrAttractivityOfExit,
                double absoluteMaxSpeed) {
            super(dynamicPotentialWeight, staticPotentialWeight, probDynamicPotentialIncrease, probDynamicPotentialDecrease,
                    probFamiliarityOrAttractivityOfExit, absoluteMaxSpeed);
        }

        @Override
        public double getPanicWeightOnPotentials() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double getPanicThreshold() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double getSpeedFromAge(double pAge) {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double getSlacknessFromDecisiveness(double pDecisiveness) {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double getExhaustionFromAge(double pAge) {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double getReactionTimeFromAge(double pAge) {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double getReactionTime() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double panicWeightOnSpeed() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double exhaustionWeightOnSpeed() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double slacknessToIdleRatio() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double panicToProbOfPotentialChangeRatio() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double getPanicDecrease() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double getPanicIncrease() {
            throw new UnsupportedOperationException("Not supported for test.");
        }
        
    }
}
