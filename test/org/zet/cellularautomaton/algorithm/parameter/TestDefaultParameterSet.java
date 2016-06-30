package org.zet.cellularautomaton.algorithm.parameter;

import org.junit.Test;
import static org.zet.cellularautomaton.algorithm.parameter.TestAbstractParameterSet.assertParameterSetDefaults;
import static org.zet.cellularautomaton.algorithm.parameter.TestAbstractParameterSet.assertSpeedFromAge;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestDefaultParameterSet {
  
    @Test
    public void initialization() {
        ParameterSet ps = new DefaultParameterSet();
        
        assertParameterSetDefaults(ps);
        assertSpeedFromAge(ps);
    }
}
