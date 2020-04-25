package org.zet.cellularautomaton;

import org.zetool.common.util.Helper;

/**
 * Creates individuals with unique ids. The ids for the individuals are consecutively given whenever {@link #build() }
 * is called. Consecutive calls of {@link #build() } create instances of {@link Individual} which have the same
 * parameters if they have not been changed in the mean time.
 *
 * @author Jan-Philipp Kappmeier
 */
public class IndividualBuilder {

    private int id = 0;
    private int age = 30;
    private double familiarity = 1;
    private double panicFactor;
    private double slackness;
    private double exhaustionFactor;
    private double relativeMaxSpeed = 1;
    private double reactionTime;

    public IndividualBuilder withAge(int age) {
        this.age = Helper.requireNonNegative(age);
        return this;
    }

    public IndividualBuilder withFamiliarity(double familiarity) {
        this.familiarity = Helper.requireInRange(0, 1, familiarity);
        return this;
    }

    public IndividualBuilder withPanicFactor(double panicFactor) {
        this.panicFactor = Helper.requireInRange(0, 1, panicFactor);
        return this;
    }

    public IndividualBuilder withSlackness(double slackness) {
        this.slackness = Helper.requireInRange(0, 1, slackness);
        return this;
    }

    public IndividualBuilder withExhaustionFactor(double exhaustionFactor) {
        this.exhaustionFactor = Helper.requireInRange(0, 1, exhaustionFactor);
        return this;
    }

    public IndividualBuilder withRelativeMaxSpeed(double relativeMaxSpeed) {
        this.relativeMaxSpeed = Helper.requireNonNegative(relativeMaxSpeed);
        return this;
    }

    public IndividualBuilder withReactionTime(double reactionTime) {
        this.reactionTime = Helper.requireNonNegative(reactionTime);
        return this;
    }

    public Individual build() {
        return new Individual(id++, age, familiarity, panicFactor, slackness, exhaustionFactor, relativeMaxSpeed, reactionTime);
    }

    public Individual buildAndReset() {
        Individual built = build();
        age = 30;
        familiarity = relativeMaxSpeed = 1;
        panicFactor = reactionTime = slackness = exhaustionFactor = 0;
        return built;
    }
}
