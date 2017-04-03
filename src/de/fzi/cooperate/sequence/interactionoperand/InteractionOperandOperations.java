package de.fzi.cooperate.sequence.interactionoperand;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionConstraint;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;

public class InteractionOperandOperations {

    /**
     * Creates a new {@link InteractionOperand} Object and adds it to the given
     * {@link CombinedFragment} object's containment.
     * 
     * @param cf
     *            the {@link CombinedFragment} object for which the interaction operand should be
     *            created
     * @param constraint
     *            the constraint that must evaluate to true for the execution of the interaction
     *            operand's content, not <code>null</code>
     */
    public static void createInteractionOperand(final CombinedFragment cf, final String constraint) {

        // Local variable declarations
        final EList<InteractionOperand> operands = cf.getOperands();

        // Create new InteractionOperand object and set name (adds a number depending on the amount
        // of interaction operands already existing)
        final InteractionOperand iop = UMLFactory.eINSTANCE.createInteractionOperand();
        iop.setName(cf.getName() + "Operand" + operands.size());

        // Create guard for the new interaction operand and set its constraint value
        final InteractionConstraint g = iop.createGuard(cf.getName() + "Guard" + operands.size());
        final LiteralString ls = (LiteralString) g.createSpecification(cf.getName() + "Specification" + operands.size(),
                null, UMLPackage.eINSTANCE.getLiteralString());
        ls.setValue(constraint);

        // Add the new interaction operand to the CombinedFragment's containment
        operands.add(iop);
    }

    /**
     * Edits the constraint on an {@link InteractionOperand}
     * 
     * @param editOperand
     *            the {@link InteractionOperand} to change the constraint for
     * @param constraint
     *            the new value for the constraint
     */
    public static void editInteractionOperand(final InteractionOperand editOperand, final String constraint) {
        ((LiteralString) editOperand.getGuard().getSpecification()).setValue(constraint);
    }

    /**
     * Deletes an {@link InteractionOperand} and moves the messages back to the interaction
     * containment
     * 
     * @param deleteOperand
     *            the {@link InteractionOperand} to delete
     */
    public static void deleteInteractionOperand(final InteractionOperand deleteOperand) {
        final CombinedFragment cf = (CombinedFragment) deleteOperand.eContainer();
        final Interaction inter = cf.getEnclosingInteraction();
        final EList<InteractionFragment> fragments = inter.getFragments();
        final EList<InteractionFragment> operandFragments = deleteOperand.getFragments();
        for (int i = 0; i < operandFragments.size(); i++) {
            fragments.add(fragments.indexOf(cf), operandFragments.get(i));
        }
        EcoreUtil.remove(deleteOperand);
    }
}
