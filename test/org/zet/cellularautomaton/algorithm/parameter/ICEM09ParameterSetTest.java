package org.zet.cellularautomaton.algorithm.parameter;

import org.junit.Test;
import static org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSetTest.assertParameterSetDefaults;
import static org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSetTest.assertSpeedFromAge;
import static org.zet.cellularautomaton.algorithm.parameter.DefaultParameterSetTest.assertExhaustionFromAge;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ICEM09ParameterSetTest {
    
    @Test
    public void initialization() {
        ParameterSet ps = new DefaultParameterSet();

        assertParameterSetDefaults(ps);
        assertSpeedFromAge(ps);

        final double defaultPanicThreshold = 3;
        ParameterSetAdapterTest.assertAdaptedParameters(ps, 0, 0, 0, 0, 0, 0, 0, defaultPanicThreshold);
        assertExhaustionFromAge(ps);
    }

}
