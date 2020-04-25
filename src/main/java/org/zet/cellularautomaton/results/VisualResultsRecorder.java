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
package org.zet.cellularautomaton.results;

import java.util.HashMap;
import java.util.LinkedList;

import org.zet.cellularautomaton.InitialConfiguration;

import java.util.List;
import java.util.Map;
import org.zet.cellularautomaton.algorithm.EvacuationCellularAutomatonAlgorithm;
import org.zet.cellularautomaton.algorithm.EvacuationInitializationCompleteEvent;
import org.zet.cellularautomaton.algorithm.EvacuationStepCompleteEvent;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zetool.common.algorithm.AbstractAlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;

/**
 * This class helps you to store all the parts of a simulation that are needed to visualize the simulation at a later
 * point. This is done by recording {@code Action}s to a {@code EvacuationRecording}-Object. The actions are stored
 * together with a full deep clone of the initial configuration of your cellular automaton. The cloning is done in the
 * constructor of this class, so the automaton is saved in the state that it has when you <b>create</b>
 * the {@code  VisualResultsRecorder}.
 *
 * Usage: Build your initial configuration and THEN instantiate a new VisualResultsRecorder with it. Call
 * {@code startRecording()}. You can now record Actions by calling {@code recordAction}. The recorded actions are stored
 * based on the simulation time when they occur, starting at time {@code t=0}. Every time your simulation time advances,
 * call {@code nextTimeStep()}. Thus, all actions that are recorded afterwards will be stored at time {@code t+1}. All
 * actions are stored in the order of their recording.
 *
 * To replay the simulation, call {@code getRecording()} to get all recorded actions nicely packed in a
 * {@code EvacuationRecording}.
 *
 * @author Daniel R. Schmidt
 *
 */
public class VisualResultsRecorder {

    static PropertyAccess es;
    /**
     * The current simulation time. Serves as a time stamp for the storage of actions.
     */
    private int timeStep = 0;

    /**
     * Stores a vector of actions for every time step Each vector holds the actions for its time step in the order of
     * occurrence.
     */
    private final Map<Integer, List<Action>> actions = new HashMap<>();

    private boolean doRecord;
    private final InitialConfiguration initialConfiguration;

    public VisualResultsRecorder(InitialConfiguration initialConfiguration, EvacuationCellularAutomatonAlgorithm cellularAutomatonAlgorithm) {
        this.initialConfiguration = initialConfiguration;
        AlgorithmListener listener = createListener(actions);
        cellularAutomatonAlgorithm.addAlgorithmListener(listener);
    }

    private AlgorithmListener createListener(Map<Integer, List<Action>> allActions) {
        return new AlgorithmListener() {
            int step = 0;
            @Override
            public void eventOccurred(AbstractAlgorithmEvent event) {

                //System.out.println(cellularAutomatonAlgorithm.getEvacuationState().getTimeStep() + ": Event: " + event);
                //int timeStep = event.getEventTime(). .getEvacuationState().getTimeStep();
                if (event instanceof EvacuationStepCompleteEvent) {
                    List<Action> actions = getActionsFor(step++);
                    actions.addAll(((EvacuationStepCompleteEvent) event).getInitializationActions());
                } else if (event instanceof EvacuationInitializationCompleteEvent) {
                    List<Action> actions = getActionsFor(step);
                    actions.addAll(((EvacuationInitializationCompleteEvent) event).getInitializationActions());
                }
            }
        };
    }
    
    private List<Action> getActionsFor(int step) {
        if (!actions.containsKey(step)) {
            actions.put(step, new LinkedList<>());
        }
        return actions.get(step);
    }

    public void startRecording() {
        this.doRecord = true;
    }

    public void stopRecording() {
        this.doRecord = false;
    }

    public final void reset() {
        stopRecording();
    }

    /**
     * Records an action at the current time step. The action should refer to cells in the original initial
     * configuration passed to the constructor of this class. The method will convert it automatically to match the
     * cloned configuration (which has clones of the original cells). The method throws
     * {@code IllegalArgumentExceptions} if you try to pass an action that does not refer to the original configuration.
     *
     * @param action An action that you want to record. The parameters of the action should refer to the original
     * configuration.
     */
    public void recordAction(Action action) throws Action.CADoesNotMatchException {
        //@//if( !ZETLoader.useVisualization )
        //@//    return;
        if (doRecord) {
            //Action adoptedAction = action.adoptToCA(this.clonedCA);
            //actions.get(timeStep).add(adoptedAction);
        }
    }

    /**
     * Increases the current time stamp by one. All actions recorded afterwards will be stored at the new time stamp.
     */
    public void nextTimestep() {
        if (doRecord) {
            timeStep++;
        }
    }

    /**
     * Get the current time step.
     *
     * @return The time step at which all actions are currently being recorded.
     */
    public int getTimeStep() {
        return timeStep;
    }

    /**
     * Get the result of the recording. This will include the clone of the initial configuration and all actions ordered
     * by time steps and in the order of their recording. The references to cells and individuals in the returned
     * actions refer to objects in the cloned configuration. A new recording is constructed each time you call this
     * method.
     *
     * @return A new {@code EvacuationRecording} containing all recorded actions and the corresponding configuration.
     */
    public EvacuationRecording getRecording() {
        return new EvacuationRecording(initialConfiguration, actions);
    }

    public int getRecordedCount() {
        return actions.size();
    }

}
