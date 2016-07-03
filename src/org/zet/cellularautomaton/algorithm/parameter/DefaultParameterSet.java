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
package org.zet.cellularautomaton.algorithm.parameter;

import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.distribution.continuous.NormalDistribution;

/**
 * @author Daniel R. Schmidt
 * @author Jan-Philipp Kappmeier
 */
public class DefaultParameterSet extends ParameterSetAdapter {

    final static double MIN_EXHAUSTION = 0.0018d;
    final static double MAX_EXHAUSTION = 0.004d;
    public double cumulativeSpeed = 0;

    public double cumulativeFemale = 0;
    public double cumulativeMale = 0;
    public int counterFemale = 0;
    public int counterMale = 0;

    protected final static double SIGMA_SQUARED = 0.26 * 0.26;

    /**
     * Creates a new instance with some static values stored in the {@code PropertyContainer}.
     */
    public DefaultParameterSet() {
        super(getSafe("algo.ca.PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO", 0), getSafe("algo.ca.SLACKNESS_TO_IDLE_RATIO", 0),
        getSafe("algo.ca.PANIC_DECREASE", 0.0), getSafe("algo.ca.PANIC_INCREASE", 0.0), getSafe("algo.ca.PANIC_WEIGHT_ON_SPEED", 0),
        getSafe("algo.ca.PANIC_WEIGHT_ON_POTENTIALS", 0), getSafe("algo.ca.EXHAUSTION_WEIGHT_ON_SPEED", 0),
        getSafe("algo.ca.PANIC_THRESHOLD", 3));
    }
    
    /**
     * Returns an exhaustion value depending on the age. Maximum Exhaustion: individual is fully exhausted after about 450 meters.
     * Minum Exhaustion: individual is fully exhausted after about 200 meters.
     * 
     * @param age
     * @return 
     */
    @Override
    public double getExhaustionFromAge(double age) {
        if (age < 10d) {
            return MAX_EXHAUSTION;
        } else if (age >= 90d) {
            return MAX_EXHAUSTION;
            //assume exhaustion is at the least on age of 20
        } else if (age <= 20) {
            double ageRatio = (age - 10d) / 10;
            double ret = MAX_EXHAUSTION - ageRatio * (MAX_EXHAUSTION - MIN_EXHAUSTION);
            return ret;

        } else {
            double ageRatio = (age - 25d) / (90d - 25d);
            double ret = MIN_EXHAUSTION
                    + ageRatio * (MAX_EXHAUSTION - MIN_EXHAUSTION);
            return ret;
        }

    }

    @Override
    public double getReactionTimeFromAge(double age) {
        return age / 10;
    }

    /**
     * Calculates the maximal speed for a person dependingon the speed-values from the rimea test suite.
     *
     * @param pAge
     * @return the maximal speed as percentage of the overall maximal speed for the simulation run
     */
    @Override
    public double getSpeedFromAge(double pAge) {
        // additional: calculate the average speed.
        double ageArray[] = {
            0.58, // 5  years
            1.15, // 10
            1.42, // 15
            1.61, // 20
            1.55, // 25
            1.54, // 30
            1.5, // 35
            1.48, // 40
            1.47, // 45
            1.41, // 50
            1.33, // 55
            1.29, // 60
            1.2, // 65
            1.08, // 70
            0.85, // 75
            0.68, // 80
            0.5 // 85 // guessed, value not based on weidmann
        };
        final int right = (int) Math.floor(pAge / 5);
        final int left = right - 1;
        double maxSpeedExpected = 0;
        if (pAge <= 5) {
            maxSpeedExpected = ageArray[0];
        } else if (pAge >= 85) {
            maxSpeedExpected = ageArray[16];
        } else {
            final double slope = (ageArray[right] - ageArray[left]);
            maxSpeedExpected = slope * (pAge - ((int) pAge / 5) * 5) / 5 + ageArray[left];
        }

        boolean male = RandomUtils.getInstance().binaryDecision(0.5);

        // Change speeds for male and female individuals:
        // + 5% for male, -5% for female
        maxSpeedExpected *= male ? 1.05 : 0.95;

        // Generate the random speed with a deviation around the expected speed for the person
        if (maxSpeedExpected < ageArray[16]) {
            maxSpeedExpected = ageArray[16];
        } else if (maxSpeedExpected > absoluteMaxSpeed) {
            maxSpeedExpected = absoluteMaxSpeed;
        }
        final NormalDistribution normal = new NormalDistribution(maxSpeedExpected, SIGMA_SQUARED, ageArray[16], absoluteMaxSpeed);
        double randSpeed = normal.getNextRandom();

        if (!male) {
            counterFemale++;
            cumulativeFemale += randSpeed;
        } else {
            counterMale++;
            cumulativeMale += randSpeed;
        }

        return randSpeed;
    }

    /**
     * Returns the invertec probability of decisiveness. This is due to the fact that slackness is a value to stop an
     * individual while decisiveness gives a value how fast an individual performs an action.
     *
     * @param pDecisiveness the decisiveness of the person
     * @return the inverted probability.
     */
    @Override
    public double getSlacknessFromDecisiveness(double pDecisiveness) {
        return 1 - pDecisiveness;
    }
}
