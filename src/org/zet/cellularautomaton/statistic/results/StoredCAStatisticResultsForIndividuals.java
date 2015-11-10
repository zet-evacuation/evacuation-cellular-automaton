/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.statistic.results;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Sylvie Temme
 */
public class StoredCAStatisticResultsForIndividuals {

    HashMap<Individual, Integer> safetyTimes;
    HashMap<Individual, ArrayList<Integer>> changePotentialTimes;
    private final HashMap<Individual, List<List<ExitCell>>> potentials;
    HashMap<Individual, ArrayList<Integer>> coveredDistanceTimes;
    private final HashMap<Individual, ArrayList<Double>> coveredDistance;
    HashMap<Individual, ArrayList<Integer>> waitedTimeTimes;
    private final HashMap<Individual, ArrayList<Integer>> waitedTime;
    HashMap<Individual, Double> minDistanceToNearestExit;
    HashMap<Individual, Double> minDistanceToPlannedExit;
    private final HashMap<Individual, StaticPotential> takenExit;
    HashMap<Individual, ArrayList<Integer>> panicTimes;
    private final HashMap<Individual, ArrayList<Double>> panic;
    HashMap<Individual, ArrayList<Integer>> exhaustionTimes;
    private final HashMap<Individual, ArrayList<Double>> exhaustion;
    HashMap<Individual, ArrayList<Integer>> currentSpeedTimes;
    private final HashMap<Individual, ArrayList<Double>> currentSpeed;

    public StoredCAStatisticResultsForIndividuals() {
        safetyTimes = new HashMap<>();
        changePotentialTimes = new HashMap<>();
        potentials = new HashMap<>();
        coveredDistanceTimes = new HashMap<>();
        coveredDistance = new HashMap<>();
        waitedTimeTimes = new HashMap<>();
        waitedTime = new HashMap<>();
        minDistanceToNearestExit = new HashMap<>();
        minDistanceToPlannedExit = new HashMap<>();
        takenExit = new HashMap<>();
        panicTimes = new HashMap<>();
        panic = new HashMap<>();
        exhaustionTimes = new HashMap<>();
        exhaustion = new HashMap<>();
        currentSpeedTimes = new HashMap<>();
        currentSpeed = new HashMap<>();
    }

    public void addSafeIndividualToStatistic(Individual ind) {

        if (!(safetyTimes.containsKey(ind))) {
            safetyTimes.put(ind, ind.getSafetyTime());
        }
    }

    public void addChangedPotentialToStatistic(Individual ind, int t) {
        if (!(changePotentialTimes.containsKey(ind))) {
            changePotentialTimes.put(ind, new ArrayList<>());
            potentials.put(ind, new ArrayList<>());
        }
        changePotentialTimes.get(ind).add(t);
        potentials.get(ind).add(ind.getStaticPotential().getAssociatedExitCells());
    }

    public void addCoveredDistanceToStatistic(Individual ind, int t, double distance) {
        double lastCoveredDistance = 0;
        if (!(coveredDistanceTimes.containsKey(ind))) {
            coveredDistanceTimes.put(ind, new ArrayList<>());
            coveredDistance.put(ind, new ArrayList<>());
        } else {
            lastCoveredDistance = coveredDistance.get(ind).get(coveredDistance.get(ind).size() - 1);
        }
        coveredDistanceTimes.get(ind).add(t);
        coveredDistance.get(ind).add(distance + lastCoveredDistance);
    }

    public void addWaitedTimeToStatistic(Individual ind, int t) {
        int lastWaitedTime = 0;
        if (!(waitedTimeTimes.containsKey(ind))) {
            waitedTimeTimes.put(ind, new ArrayList<>());
            waitedTime.put(ind, new ArrayList<>());
        } else {
            lastWaitedTime = waitedTime.get(ind).get(waitedTime.get(ind).size() - 1);
        }
        waitedTimeTimes.get(ind).add(t);
        waitedTime.get(ind).add(1 + lastWaitedTime);
    }

    public void addMinDistancesToStatistic(Individual ind, double distNearest, double distPlanned) {
        if (!(minDistanceToNearestExit.containsKey(ind))) {
            minDistanceToNearestExit.put(ind, distNearest);
        }
        if (!(minDistanceToPlannedExit.containsKey(ind))) {
            minDistanceToPlannedExit.put(ind, distPlanned);
        }
    }

    public void addExitToStatistic(Individual ind, StaticPotential exit) {
        takenExit.put(ind, exit);
    }

    public void addExhaustionToStatistic(Individual ind, int t, double actualExhaustion) {
        if (!(exhaustionTimes.containsKey(ind))) {
            exhaustionTimes.put(ind, new ArrayList<>());
            exhaustion.put(ind, new ArrayList<>());
        }
        exhaustionTimes.get(ind).add(t);
        exhaustion.get(ind).add(actualExhaustion);

    }

    public void addPanicToStatistic(Individual ind, int t, double actualPanic) {
        if (!(panicTimes.containsKey(ind))) {
            panicTimes.put(ind, new ArrayList<>());
            panic.put(ind, new ArrayList<>());
        }
        panicTimes.get(ind).add(t);
        panic.get(ind).add(actualPanic);
    }

    public void addCurrentSpeedToStatistic(Individual ind, int t, double speed) {
        if (!(currentSpeedTimes.containsKey(ind))) {
            currentSpeedTimes.put(ind, new ArrayList<>());
            currentSpeed.put(ind, new ArrayList<>());
        }
        currentSpeedTimes.get(ind).add(t);
        currentSpeed.get(ind).add(speed);
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapChangePotentialTimes() {
        return changePotentialTimes;
    }

    public HashMap<Individual, ArrayList<Double>> getHashMapCoveredDistance() {
        return coveredDistance;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapCoveredDistanceTimes() {
        return coveredDistanceTimes;
    }

    public HashMap<Individual, Double> getHashMapMinDistanceToNearestExit() {
        return minDistanceToNearestExit;
    }

    public HashMap<Individual, Double> getHashMapMinDistanceToPlannedExit() {
        return minDistanceToPlannedExit;
    }

    public HashMap<Individual, List<List<ExitCell>>> getHashMapPotentials() {
        return potentials;
    }

    public HashMap<Individual, Integer> getHashMapSafetyTimes() {
        return safetyTimes;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapWaitedTime() {
        return waitedTime;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapWaitedTimeTimes() {
        return waitedTimeTimes;
    }

    public HashMap<Individual, StaticPotential> getHashMapTakenExit() {
        return takenExit;
    }

    public HashMap<Individual, ArrayList<Double>> getHashMapExhaustion() {
        return exhaustion;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapExhaustionTimes() {
        return exhaustionTimes;
    }

    public HashMap<Individual, ArrayList<Double>> getHashMapPanic() {
        return panic;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapPanicTimes() {
        return panicTimes;
    }

    public HashMap<Individual, ArrayList<Double>> getHashMapCurrentSpeed() {
        return currentSpeed;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapCurrentSpeedTimes() {
        return currentSpeedTimes;
    }

}
