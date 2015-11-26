package org.zet.cellularautomaton.algorithm;

import ds.PropertyContainer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.algorithm.rule.AbstractMovementRule;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;
import org.zet.cellularautomaton.algorithm.rule.MockRule;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestDefaultRuleSet {

    private final String INIT_RULE_TAG = "algo.ca.defaultRuleSet.init";
    private final String LOOP_RULE_TAG = "algo.ca.defaultRuleSet.loop";

    private final Mockery context = new Mockery();
    List<String> loopRules;
    List<String> initRules;

    @Before
    public void init() {
        loopRules = new LinkedList<>();
        initRules = new LinkedList<>();
        PropertyContainer pc = PropertyContainer.getGlobal();
        if(pc.isDefined(INIT_RULE_TAG)) {
            pc.set(INIT_RULE_TAG, initRules);
            pc.set(LOOP_RULE_TAG, loopRules);            
        } else {
            pc.define(INIT_RULE_TAG, List.class, initRules);
            pc.define(LOOP_RULE_TAG, List.class, loopRules);            
        }
    }

    @Test
    public void testPropertyListLoads() {
        loopRules.add("MockRule$TestLoopRule");
        initRules.add("MockRule$TestInitRule");
        DefaultRuleSet ruleSet = new DefaultRuleSet();

        List<EvacuationRule> all = new LinkedList<>();
        List<EvacuationRule> primary = new LinkedList<>();
        List<EvacuationRule> loop = new LinkedList<>();
        for (EvacuationRule rule : ruleSet) {
            all.add(rule);
        }
        for (Iterator<EvacuationRule> it = ruleSet.loopIterator(); it.hasNext();) {
            loop.add(it.next());
        }
        for (Iterator<EvacuationRule> it = ruleSet.primaryIterator(); it.hasNext();) {
            primary.add(it.next());
        }

        assertThat(all, hasSize(2));
        assertThat(primary, hasSize(1));
        assertThat(loop, hasSize(1));
        assertThat(primary.get(0), is(instanceOf(MockRule.TestInitRule.class)));
        assertThat(loop.get(0), is(instanceOf(MockRule.TestLoopRule.class)));
        assertThat(ruleSet.getMovementRule(), is(nullValue()));
    }
    
    @Test
    public void testNoDoubleAdding() {
        DefaultRuleSet ruleSet = new DefaultRuleSet();
        AbstractMovementRule movementRule = new MockAbstractMovementRule();
        EvacuationRule mockRule = context.mock(EvacuationRule.class);
        ruleSet.add(mockRule, true, false);
        ruleSet.add(movementRule, true, true);
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
        assertThat(ruleSet.getMovementRule(), is(equalTo(movementRule)));
        assertThat(all, contains(mockRule, movementRule));
        assertThat(primary, contains(mockRule, movementRule, mockRule));
        assertThat(loop, contains(movementRule, mockRule));
    }
    
    @Test
    public void testMovementRuleDetectedOverloaded() {        
        DefaultRuleSet ruleSet = new DefaultRuleSet();
        AbstractMovementRule rule1 = new MockAbstractMovementRule();
        ruleSet.add(rule1, false, true);
        assertThat(ruleSet.getMovementRule(), is(equalTo(rule1)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleMovementRulesFails() {
        DefaultRuleSet ruleSet = new DefaultRuleSet();
        AbstractMovementRule rule1 = new MockAbstractMovementRule();
        AbstractMovementRule rule2 = new MockAbstractMovementRule();
        ruleSet.add(rule1);
        ruleSet.add(rule2);
    }

    
    private static class MockAbstractMovementRule extends AbstractMovementRule {

        @Override
        public void move(EvacCell from, EvacCell target) {
        }

        @Override
        public void swap(EvacCell cell1, EvacCell cell2) {
        }

        @Override
        protected void onExecute(EvacCell cell) {
        }
        
    }
}
