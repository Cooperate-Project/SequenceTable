package de.fzi.cooperate.sequence.combinedfragment;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionConstraint;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.InteractionOperatorKind;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;

public class CombinedFragmentOperations {

    private CombinedFragmentOperations() {
    };

    /**
     * Create a new {@link CombinedFragment} object, the corresponding {@link InteractionOperand}
     * and {@link InteractionConstraint} objects and set the necessary properties.
     * 
     * @param interaction
     *            the {@link Interaction} object which holds the containment for the new
     *            {@link CombinedFragment} object, not <code>null</code>
     * @param name
     *            the name chosen by the user for the new {@link CombinedFragment} object, not
     *            <code>null</code>
     * @param type
     *            the {@link InteractionOperatorKind} literal for the new {@link CombinedFragment}
     *            object, not <code>null</code>
     * @param lifelines
     *            the {@link Lifeline} objects which should cover the new {@link CombinedFragment}
     *            object, not <code>null</code>
     * @param constraint
     *            the constraint chosen by the user for the new {@link CombinedFragment} object's
     *            {@link InteractionOperand} object, not <code>null</code>
     */
    public static void createCombinedFragment(final Interaction interaction, final String name,
            final InteractionOperatorKind type, final Lifeline[] lifelines, final String usrConstraint,
            final Message insertMsg) {

        // Create new combined fragment
        final CombinedFragment cf = UMLFactory.eINSTANCE.createCombinedFragment();
        cf.setName(name);
        cf.setInteractionOperator(type);
        final EList<Lifeline> cfCovereds = cf.getCovereds();

        // Create new interaction operand
        final InteractionOperand io = cf.createOperand(name + "Operand");
        final EList<Lifeline> ioCovereds = io.getCovereds();

        // Set the covered property for the combined fragment and interaction operand
        for (final Lifeline l : lifelines) {
            cfCovereds.add(l);
            ioCovereds.add(l);
        }

        // Create new interaction constraint and set the value
        final InteractionConstraint g = io.createGuard(name + "Guard");
        final LiteralString ls = (LiteralString) g.createSpecification(name + "Specification", null,
                UMLPackage.eINSTANCE.getLiteralString());
        ls.setValue(usrConstraint);
        final EList<InteractionFragment> fragments = interaction.getFragments();
        final int insertIndex = insertMsg.getMessageSort().equals(MessageSort.REPLY_LITERAL)
                ? fragments.indexOf(insertMsg.getReceiveEvent()) + 1 : fragments.indexOf(insertMsg.getSendEvent());
        fragments.add(insertIndex, cf);
    }

    /**
     * Edits an already existing {@link CombinedFragment} and sets possible changed properties.
     * 
     * @param name
     *            the possible new name for the {@link CombinedFragment}, not <code>null</code>
     * @param type
     *            the possible changed {@link InteractionOperatorKind} literal for the
     *            {@link CombinedFragment}, not <code>null</code>
     * @param lifelines
     *            the {@link Lifeline}s that are covered by the {@link CombinedFragment}, not
     *            <code>null</code>
     * @param constraint
     *            the possible changed constraint for the {@link CombinedFragment}'s
     *            {@link InteractionOperand}, not <code>null</code>
     * @param editFragment
     *            the {@link CombinedFragment} that subject to change, not <code>null</code>
     */
    public static void editCombinedFragment(final String name, final InteractionOperatorKind type,
            final Lifeline[] lifelines, final CombinedFragment editFragment) {

        // If the name variable differs from the combined fragment's current name, change all names
        // associated with the combined fragment
        editFragment.getOperand(editFragment.getName() + "Operand").getGuard().getSpecification()
                .setName(name + "Specification");
        editFragment.getOperand(editFragment.getName() + "Operand").getGuard().setName(name);
        editFragment.getOperand(editFragment.getName() + "Operand").setName(name + "Operand");
        editFragment.setName(name);

        // If the type variable differs from the combined fragment's current type, change the type
        editFragment.setInteractionOperator(type);

        // TODO: Depending on what type the combined fragment gets changed to, there might be some
        // special cases about the number of interaction operands (i.e. loop only has one, par has
        // two, some can have even more)
    }

    /**
     * Deletes a {@link CombinedFragment} and its {@link InteractionOperand}s and moves the
     * {@link InteractionFragment} inside out of the {@link CombinedFragment}
     * 
     * @param cf
     *            the {@link CombinedFragment} to delete
     */
    public static void deleteCombinedFragment(final CombinedFragment cf) {
        final EList<InteractionFragment> interFragments = cf.getEnclosingInteraction().getFragments();
        int cfIndex = interFragments.indexOf(cf);
        for (final InteractionOperand iop : cf.getOperands()) {
            final EList<InteractionFragment> fragments = iop.getFragments();
            final int end = fragments.size();
            for (int i = 0; i < end; i++) {
                interFragments.add(i + cfIndex, fragments.get(0));
            }
            cfIndex += end;
        }
        EcoreUtil.remove(cf);
    }

}
