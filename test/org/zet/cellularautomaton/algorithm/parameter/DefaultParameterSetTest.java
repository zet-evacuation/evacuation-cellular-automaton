package org.zet.cellularautomaton.algorithm.parameter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSetTest.assertParameterSetDefaults;
import static org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSetTest.assertSpeedFromAge;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultParameterSetTest {

    @Test
    public void initialization() {
        ParameterSet ps = new DefaultParameterSet();

        assertParameterSetDefaults(ps);
        assertSpeedFromAge(ps);

        final double defaultPanicThreshold = 3;
        ParameterSetAdapterTest.assertAdaptedParameters(ps, 0, 0, 0, 0, 0, 0, 0, defaultPanicThreshold);
        assertExhaustionFromAge(ps);
    }

    public static void assertExhaustionFromAge(ParameterSet ps) {
        for (int age = 0; age <= 100; ++age) {
            assertThat(ps.getExhaustionFromAge(age), is(greaterThanOrEqualTo(0.0)));
        }
    }

}
