package org.zet.cellularautomaton.algorithm.rule;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.zet.cellularautomaton.EvacCellInterface;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@Ignore
public class RuleTestMatchers {

    public static Matcher<EvacuationRule> executeableOn(final EvacCellInterface cell) {
        return new BaseMatcher<EvacuationRule>() {
            @Override
            public boolean matches(final Object item) {
                final EvacuationRule foo = (EvacuationRule) item;
                return foo.executableOn(cell);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText(" should be executable on ").appendValue(cell);
            }
        };
    }

}
