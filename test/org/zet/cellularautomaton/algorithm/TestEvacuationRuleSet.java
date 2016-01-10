package org.zet.cellularautomaton.algorithm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.algorithm.rule.EvacuateIndividualsRule;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;

/**
 *
 * @author Jan-Philipp Kappmeieer
 */
public class TestEvacuationRuleSet {
    public static class FakeEvacuationRuleSet extends EvacuationRuleSet {

        public FakeEvacuationRuleSet() {
        }
        
    }
    
    private EvacuationRuleSet ruleSet;

    private final Mockery context = new Mockery();
    
    @Before
    public void init() {
        ruleSet = new EvacuationRuleSet() {
        };
        
    }
    
    @Test
    public void testInstantiation() {
        for( EvacuationRule _unused : ruleSet) {
            throw new AssertionError("Should be empty!");
        }
        for (Iterator<EvacuationRule> it = ruleSet.loopIterator(); it.hasNext();) {
            it.next();
            throw new AssertionError("Should be empty!");
        }
        for (Iterator<EvacuationRule> it = ruleSet.primaryIterator(); it.hasNext();) {
            it.next();
            throw new AssertionError("Should be empty!");
        }
    }
    
    @Test
    public void testAdding() {
        EvacuationRule ruleBoth = context.mock(EvacuationRule.class, "both");
        EvacuationRule rulePrimary = context.mock(EvacuationRule.class, "primary");
        EvacuationRule ruleLoop = context.mock(EvacuationRule.class, "loop");
        EvacuationRule ruleNone = context.mock(EvacuationRule.class, "none");
        EvacuationRule ruleDefault = context.mock(EvacuationRule.class, "default");
        ruleSet.add(ruleBoth, true, true);
        ruleSet.add(rulePrimary, true, false);
        ruleSet.add(ruleLoop, false, true);
        ruleSet.add(ruleNone, false, false);
        ruleSet.add(ruleDefault);
        
        List<EvacuationRule> all = new LinkedList<>();
        List<EvacuationRule> primary = new LinkedList<>();
        List<EvacuationRule> loop = new LinkedList<>();
        for( EvacuationRule rule : ruleSet ) {
            all.add(rule);
        }
        for (Iterator<EvacuationRule> it = ruleSet.loopIterator(); it.hasNext();) {
            loop.add(it.next());
        }
        for (Iterator<EvacuationRule> it = ruleSet.primaryIterator(); it.hasNext();) {
            primary.add(it.next());
        }
        
        assertThat(primary, contains(ruleBoth, rulePrimary, ruleDefault));
        assertThat(loop, contains(ruleBoth, ruleLoop, ruleDefault));
        assertThat(all, contains(ruleBoth, rulePrimary, ruleLoop, ruleNone, ruleDefault));
    }
    
    @Test
    public void testNoDoubleAdding() {
        EvacuationRule mockRule = context.mock(EvacuationRule.class);
        ruleSet.add(mockRule, true, false);
        ruleSet.add(mockRule, true, true);
        ruleSet.add(mockRule);
        
        List<EvacuationRule> all = new LinkedList<>();
        List<EvacuationRule> primary = new LinkedList<>();
        List<EvacuationRule> loop = new LinkedList<>();
        for( EvacuationRule rule : ruleSet ) {
            all.add(rule);
        }
        for (Iterator<EvacuationRule> it = ruleSet.loopIterator(); it.hasNext();) {
            loop.add(it.next());
        }
        for (Iterator<EvacuationRule> it = ruleSet.primaryIterator(); it.hasNext();) {
            primary.add(it.next());
        }
        assertThat(all, contains(mockRule));
        assertThat(primary, contains(mockRule, mockRule, mockRule));
        assertThat(loop, contains(mockRule, mockRule));
    }
    
    @Test
    public void testRuleCreation() {
        EvacuationRule r = EvacuationRuleSet.createRule("EvacuateIndividualsRule");
        assertThat(r, is(instanceOf(EvacuateIndividualsRule.class)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRuleCreateionFails() {
        EvacuationRuleSet.createRule("NotExistingRule");
    }
    
    @Test
    public void testRuleCreationSet() {
        EvacuationRuleSet r = EvacuationRuleSet.createRuleSet("TestEvacuationRuleSet$FakeEvacuationRuleSet");
        assertThat( r, is(instanceOf(FakeEvacuationRuleSet.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRuleCreationSetFails() {
        EvacuationRuleSet.createRuleSet("NoSuchClass");
    }
}
