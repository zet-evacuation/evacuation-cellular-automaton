package org.zet.cellularautomaton.algorithm.parameter;

import org.junit.Test;
import static org.zet.cellularautomaton.algorithm.parameter.TestAbstractParameterSet.assertParameterSetDefaults;
import static org.zet.cellularautomaton.algorithm.parameter.TestAbstractParameterSet.assertSpeedFromAge;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestSimpleParameterSet {
    
    @Test
    public void initialization() {
        ParameterSet ps = new SimpleParameterSet();
        
        assertParameterSetDefaults(ps);
        assertSpeedFromAge(ps);
    }
}
