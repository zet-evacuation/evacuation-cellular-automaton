/* zet evacuation tool copyright (c) 2007-15 zet evacuation team
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
package org.zet.cellularautomaton;

import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.container.mapping.Identifiable;

/**
 * A Individual represets a Person in the evacuationtool with the following characteristics: familiarity, panic,
 * slackness, relativeMaxSpeed. Also an * exhaustion factor exists, which simulates exhaustion after walking a long way.
 * An Individual is located in a {@link EvacCell} of the building and each {@code Individual} has a
 * {@link StaticPotential}, which guides the person to an exit.
 */
public class Individual implements Identifiable {

    private final int age;
    private final double familiarity;
    private final double panicFactor;
    private final double slackness;
    private final double exhaustionFactor;
    private final double relativeMaxSpeed;
    /** The number of the individual. Each Individual of an CA should have a unique identifier. */
    private final int number;
    private final double reactionTime;

    public Individual(int number, int age, double familiarity, double panicFactor, double slackness, double exhaustionFactor, double relativeMaxSpeed, double reactionTime) {
        this.number = number;
        this.age = age;
        this.familiarity = familiarity;
        this.panicFactor = panicFactor;
        this.slackness = slackness;
        this.exhaustionFactor = exhaustionFactor;
        this.relativeMaxSpeed = relativeMaxSpeed;
        this.reactionTime = reactionTime;

    }

    Individual(Individual individual) {
        this(individual.number, individual.age, individual.familiarity, individual.panicFactor, individual.slackness,
                individual.exhaustionFactor, individual.relativeMaxSpeed, individual.reactionTime);
    }

    /**
     * Get the age of the individual.
     *
     * @return The age
     */
    public int getAge() {
        return age;
    }

    /**
     * Get the left reaction time of the individual.
     *
     * @return the left reaction time
     */
    public double getReactionTime() {
        return reactionTime;
    }

    /**
     * Returns the exchaustion factor of the {@code Individual}.
     *
     * @return the exhaustion factor
     */
    public double getExhaustionFactor() {
        return this.exhaustionFactor;
    }

    /**
     * Get the familiarity of the individual.
     *
     * @return The familiarity
     */
    public double getFamiliarity() {
        return familiarity;
    }

    /**
     * Returns the identifier of this individual.
     *
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the identifier of this individual.
     *
     * @return the number
     */
    @Override
    public int id() {
        return number;
    }

    public double getPanicFactor() {
        return panicFactor;
    }

    /**
     * Get the slackness of the individual.
     *
     * @return The slackness
     */
    public double getSlackness() {
        return slackness;
    }

    /**
     * Returns the relativeMaxSpeed of the individual.
     *
     * @return The relativeMaxSpeed
     */
    public double getMaxSpeed() {
        return relativeMaxSpeed;
    }

    /**
     * Returns a string "Individual" and the id number of the individual.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return "Individual " + id();
    }

    /**
     * Returns a string containing all parameters of the individueal, such as familiarity, exhaustion etc.
     *
     * @return the property string
     */
    public String toStringProperties() {
        return "Familiarity: " + familiarity + "\n"
                + "Panic factor: " + panicFactor + "\n"
                + "Slackness: " + slackness + "\n"
                + "Exhaustion factor: " + exhaustionFactor + "\n"
                + "MaxSpeed: " + relativeMaxSpeed;
    }

    /**
     * The hashcode of individuals is their id numer.
     *
     * @return the hashcode of the individual
     */
    @Override
    public int hashCode() {
        return getNumber();
    }

    /**
     * Two individuals are equal, if they have both the same id.
     *
     * @param o the reference object with which to compare.
     * @return {@code true} if this object is the same as {@code o}; {@code false} otherwise.
     * @see #hashCode()
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Individual ? ((Individual) o).id() == id() : false;
    }
}
