package org.zet.cellularautomaton;

/**
 * Creates individuals with unique ids.
 * 
 * @author Jan-Philipp Kappmeier
 */
public class IndividualBuilder {
    private int id = 0;
    private int age;
    private double familiarity;
    private double panicFactor;
    private double slackness;
    private double exhaustionFactor;
    private double relativeMaxSpeed;
    private double reactionTime;
    private boolean buildStarted = false;

    public Individual buildNewIndividual() {
        newIndividual(30);
        return build();
    }
    
    public IndividualBuilder newIndividual(int age) {
        if(buildStarted) {
            throw new IllegalStateException("Build not completed.");
        }
        buildStarted = true;
        id++;
        initDefault();
        this.age = age;
        return this;
    }
    
    private void initDefault() {
        familiarity = relativeMaxSpeed = 1;
        panicFactor = slackness = exhaustionFactor = reactionTime = 0;
    }
    
    public IndividualBuilder withFamiliarity(double familiarity) {
        this.familiarity = familiarity;
        return this;
    }
    
    public IndividualBuilder withPanicFactor(double panicFactor) {
        this.panicFactor = panicFactor;
        return this;
    }
    
    public IndividualBuilder withSlackness(double slackness) {
        this.slackness = slackness;
        return this;
    }
    
    public IndividualBuilder withExhaustionFactor(double exhaustionFactor) {
        this.exhaustionFactor = exhaustionFactor;
        return this;
    }
    
    public IndividualBuilder withRelativeMaxSpeed(double relativeMaxSpeed) {
        this.relativeMaxSpeed = relativeMaxSpeed;
        return this;
    }
    
    public IndividualBuilder withReactionTime(double reactionTime) {
        this.reactionTime = reactionTime;
        return this;
    }
    
    public Individual build() {
        if(!buildStarted) {
            throw new IllegalStateException("Build not started.");
        }
        buildStarted = false;
        return new Individual(id++, age, familiarity, panicFactor, slackness, exhaustionFactor, relativeMaxSpeed, reactionTime);
    }
}
