package de.fzi.cooperate.sequence.stateinvariant;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.StateInvariant;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;

public class StateInvariantOperations {

    private StateInvariantOperations() {
    };

    /**
     * Creates a new {@link StateInvariant} object and the corresponding {@link Constraint} and
     * {@link Specification} object.
     * 
     * @param interaction
     *            the current {@link Interaction} object, not <code>null</code>
     * @param name
     *            the name for the new {@link StateInvariant} object, not <code>null</code>
     * @param lifeline
     *            the {@link Lifeline} object with which the new {@link StateInvariant} object is
     *            associated with, not <code>null</code>
     * @param usrConstraint
     *            the constraint chosen by the user for the new {@link StateInvariant} object, not
     *            <code>null</code>
     */
    public static void createStateInvariant(final Interaction interaction, final String name, final Lifeline lifeline,
            final String userConstraint) {

        // Create new state invariant at the beginning of the interaction containment and set
        // necessary properties
        final StateInvariant newStateInvariant = UMLFactory.eINSTANCE.createStateInvariant();
        newStateInvariant.setName(name);
        newStateInvariant.getCovereds().add(lifeline);
        interaction.getFragments().add(0, newStateInvariant);

        // Create new constraint in state invariant containment
        final Constraint invariantConstraint = newStateInvariant.createInvariant(name + "Constraint",
                UMLPackage.eINSTANCE.getConstraint());

        // Create new specification in constraint containment with value usrConstraint
        final LiteralString literalStringConstraint = (LiteralString) invariantConstraint
                .createSpecification(name + "Specification", null, UMLPackage.eINSTANCE.getLiteralString());
        literalStringConstraint.setValue(userConstraint);
    }

    /**
     * Edits an already existing {@link StateInvariant}, changing possibly the name, covers and
     * constraint of the state invariant.
     * 
     * @param name
     *            the name chosen by the user for the {@link StateInvariant}, not <code>null</code>
     * @param lifeline
     *            the {@link Lifeline} for which the {@link StateInvariant} should be associated
     *            with, not <code>null</code>
     * @param constraint
     *            the constraint entered by the user for the {@link StateInvariant}
     * @param i
     */
    public static void editStateInvariant(final String name, final Lifeline lifeline, final String constraint,
            final StateInvariant si) {

        // Local variable declaration
        final EList<Lifeline> siCovered = si.getCovereds();
        final Constraint siConstraint = si.getInvariant();
        final LiteralString siConValue = (LiteralString) siConstraint.getSpecification();

        // If the new name doesn't equal the old name, change to the new name
        if (name != si.getName()) {
            si.setName(name);
            siConstraint.setName(name + "Constraint");
            siConValue.setName(name + "Specification");
        }

        // If the new lifeline doesn't equal the old lifeline, change to the new lifeline
        if (lifeline != siCovered.get(0)) {
            siCovered.remove(0);
            siCovered.add(lifeline);
        }

        // If the new constraint doesn't equal the old constraint, change to the new constraint
        if (!(constraint.equals(siConValue.getValue()))) {
            siConValue.setValue(constraint);
        }
    }

    /**
     * Deletes the specified {@link StateInvariant} object from the {@link Interaction} object's
     * containment
     * 
     * @param interaction
     *            the {@link Interaction} object from which the {@link StateInvariant} object should
     *            be deleted
     * @param delInvariant
     *            the {@link StateInvariant} object which should be deleted
     */
    public static void deleteStateInvariant(final StateInvariant delInvariant) {

        // Get fragments and remove the state invariant
        EcoreUtil.remove(delInvariant);
    }
}
