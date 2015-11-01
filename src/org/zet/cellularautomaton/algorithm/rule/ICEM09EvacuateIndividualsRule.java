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
package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.Individual;

public class ICEM09EvacuateIndividualsRule extends AbstractEvacuationRule {

    public ICEM09EvacuateIndividualsRule() {
    }

    @Override
    protected void onExecute( org.zet.cellularautomaton.EvacCell cell ) {
        esp.getCa().markIndividualForRemoval( cell.getIndividual() );
        // Potential needed for statistics:
        org.zet.cellularautomaton.StaticPotential exit = esp.getPotentialController().getNearestExitStaticPotential( cell );
        esp.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExitToStatistic( cell.getIndividual(), exit );
        // safetyTime etc will be set in the SaveIndividualsRule
    }

    @Override
    public boolean executableOn( org.zet.cellularautomaton.EvacCell cell ) {
        Individual i = cell.getIndividual();
        boolean testval = false;
        if( (i != null) && (cell instanceof org.zet.cellularautomaton.ExitCell)) {
            testval = i.getStepEndTime() < esp.getCa().getTimeStep() + 1;
        }
        return (i != null) && (cell instanceof org.zet.cellularautomaton.ExitCell) && testval;
    }
}

