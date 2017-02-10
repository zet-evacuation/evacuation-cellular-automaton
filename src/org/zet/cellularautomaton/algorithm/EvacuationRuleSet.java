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
package org.zet.cellularautomaton.algorithm;

import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;
import org.zet.cellularautomaton.results.Action;
import org.zetool.algorithm.simulation.cellularautomaton.RuleSet;

/**
 * The abstract base class for rule sets. A {@code EvacuationRuleSet} basically is a container for {@link EvacuationRule} objects. The rules
 * fall into two different types: the initialization rules and the loop rules.
 *
 * As the objects are divided in two parts, a {@code EvacuationRuleSet} provides three different iterators: one that iterates
 * through all known rules, one iterating the initialization rules and a third one iterating the loop rules.
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class EvacuationRuleSet implements RuleSet<EvacuationRule<? extends Action>> {

    /** The {@code ArrayList} containing all rules in only one instance. */
    private final List<EvacuationRule<?>> allRules;
    /** The {@code ArrayList} containing all initialization rules, maybe twice or more often. */
    private final List<EvacuationRule<?>> primaryRules;
    /** The {@code ArrayList} containing all loop rules, maybe twice or more often. */
    private final List<EvacuationRule<?>> loopRules;

    /**
     * Creates a new instance of {@code RuleSet} and initializes the container.
     */
    public EvacuationRuleSet() {
        allRules = new ArrayList<>();
        primaryRules = new ArrayList<>();
        loopRules = new ArrayList<>();
    }

    /**
     * Returns an {@code Iterator} that iterates through all known rules. All rules are only contained once.
     *
     * @return the iterator
     */
    @Override
    public Iterator<EvacuationRule<? extends Action>> iterator() {
        return allRules.iterator();
    }

    /**
     * Adds a new {@link EvacuationRule} to both, the initialization list and the loop list.
     *
     * @param rule the rule
     */
    public void add(EvacuationRule rule) {
        if (!allRules.contains(rule)) {
            allRules.add(rule);
        }
        primaryRules.add(rule);
        loopRules.add(rule);
    }

    /**
     * Adds a new {@link EvacuationRule} only to the specified lists. In any case the rule is added to the list of all used rules.
     *
     * @param rule the rule that is to be inserted
     * @param useInPrimarySet true if the rule should be added to the primary set
     * @param useInLoopSet true if the rule should be added to the loop set
     * @throws java.lang.IllegalArgumentException if two movement rules are inserted
     */
    public void add(EvacuationRule rule, boolean useInPrimarySet, boolean useInLoopSet) {
        if (!allRules.contains(rule)) {
            allRules.add(rule);
        }
        if (useInPrimarySet) {
            primaryRules.add(rule);
        }
        if (useInLoopSet) {
            loopRules.add(rule);
        }
    }

    /**
     * Returns an {@code Iterator} that iterates through the initialization rules. These rules can be added twice or
     * more often.
     *
     * @return the iterator
     */
    public Iterator<EvacuationRule<?>> loopIterator() {
        return loopRules.iterator();
    }

    /**
     * Returns an {@code Iterator} that iterates through the loop rules. These rules can be added twice or more often.
     *
     * @return the iterator
     */
    public Iterator<EvacuationRule<?>> primaryIterator() {
        return primaryRules.iterator();
    }

    /**
     * Creates a new instance of the {@link EvacuationRule} interface. The object has the specified type and is created using the
     * default constructor, thus the {@code EvacuationRule} shall have at least this public constructor.
     *
     * @param ruleName the classname of the rule without the classpath "algo.ca.rule"
     * @return the new instace
     */
    public static EvacuationRule createRule(String ruleName) {
        String ruleClassName = "org.zet.cellularautomaton.algorithm.rule." + ruleName;
        try {
            Class<?> ruleClass = Class.forName(ruleClassName);
            return (EvacuationRule) ruleClass.getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Error creating Rule " + ruleClassName, e);
        }
    }

    /**
     * Creates a new instance of {@code EvacuationRuleSet} of a specified class. The default constructor is called, thus the rule
     * set shall have at least this public constructor.
     *
     * @param ruleSetName the classname of the rule set without the classpath "algo.ca"
     * @return the new instance
     */
    public static EvacuationRuleSet createRuleSet(String ruleSetName) {
        String ruleSetClassName = "org.zet.cellularautomaton.algorithm." + ruleSetName;
        try {
            Class<?> ruleSetClass = Class.forName(ruleSetClassName);
            return (EvacuationRuleSet) ruleSetClass.getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Error creating RuleSet " + ruleSetClassName, e);
        }
    }
}
