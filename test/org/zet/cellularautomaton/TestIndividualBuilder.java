package org.zet.cellularautomaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.jmock.AbstractExpectations.same;

import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestIndividualBuilder {
    @Test
    public void buildStarted() {
        IndividualBuilder builder = new IndividualBuilder();
        Individual i1 = builder.build();
        Individual i2 = builder.build();
        assertThat(i2.getNumber(), is(equalTo(i1.getNumber() + 1)));
        assertThat(i1, is(not(same(i2))));
        assertThat(i1, is(not(equalTo(i2))));
    }

    final int age = 43;
    final double exhaustionFactor = 0.1;
    final double familiarity = 0.2;
    final double panicFactor = 0.3;
    final double reactionTime = 14;
    final double relativeMaxSpeed = 3.1;
    final double slackness = 0.8;
    
    @Test
    public void propertiesStored() {
        IndividualBuilder b = new IndividualBuilder();
        
        Individual i = b.withAge(age).withExhaustionFactor(exhaustionFactor).withFamiliarity(familiarity)
                .withPanicFactor(panicFactor).withReactionTime(reactionTime).withRelativeMaxSpeed(relativeMaxSpeed)
                .withSlackness(slackness).build();
        
        assertThat(i.getAge(), is(equalTo(age)));
        assertThat(i.getExhaustionFactor(), is(closeTo(exhaustionFactor, 10e-6)));
        assertThat(i.getFamiliarity(), is(closeTo(familiarity, 10e-6)));
        assertThat(i.getPanicFactor(), is(closeTo(panicFactor, 10e-6)));
        assertThat(i.getReactionTime(), is(closeTo(reactionTime, 10e-6)));
        assertThat(i.getRelativeSpeed(), is(closeTo(relativeMaxSpeed, 10e-6)));
        assertThat(i.getSlackness(), is(closeTo(slackness, 10e-6)));

        Individual i2 = b.build();
        assertEqualProperties(i, i2);
    }
    
    @Test
    public void resetting() {
        IndividualBuilder b = new IndividualBuilder();
        
        Individual defaultIndividual = b.build();
        
        b.withAge(age).withExhaustionFactor(exhaustionFactor).withFamiliarity(familiarity)
                .withPanicFactor(panicFactor).withReactionTime(reactionTime).withRelativeMaxSpeed(relativeMaxSpeed)
                .withSlackness(slackness).buildAndReset();
        
        Individual resettedIndividual = b.build();
        assertEqualProperties(defaultIndividual, resettedIndividual);
    }
    
    private void assertEqualProperties(Individual i, Individual i2) {
        assertThat(i2.getAge(), is(equalTo(i.getAge())));
        assertThat(i2.getExhaustionFactor(), is(equalTo(i.getExhaustionFactor())));
        assertThat(i2.getFamiliarity(), is(equalTo(i.getFamiliarity())));
        assertThat(i2.getPanicFactor(), is(equalTo(i.getPanicFactor())));
        assertThat(i2.getReactionTime(), is(equalTo(i.getReactionTime())));
        assertThat(i2.getRelativeSpeed(), is(equalTo(i.getRelativeSpeed())));
        assertThat(i2.getSlackness(), is(equalTo(i.getSlackness())));        
    }
}
