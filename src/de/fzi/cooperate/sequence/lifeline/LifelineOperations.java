package de.fzi.cooperate.sequence.lifeline;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.ExecutionOccurrenceSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.StateInvariant;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.util.UMLSwitch;

import de.fzi.cooperate.sequence.message.MessageOperations;
import de.fzi.cooperate.sequence.stateinvariant.StateInvariantOperations;

public class LifelineOperations {

    private LifelineOperations() {
    };

    /**
     * Creates a new lifeline in the current interaction containment.
     * 
     * @param interaction
     *            the current interaction, not <code>null</code>
     * @param lifelines
     *            the list of lifelines currently present in the current interaction,
     *            <code>null</code> if there are no lifelines yet
     * @param name
     *            the user-selected name for the new instance of class represented by the new
     *            lifeline, <code>null</code> if there is no instance
     * @param selClass
     *            the class represented by the lifeline, not <code>null</code>
     * @param insertIndex
     *            the position at which the new lifeline should be inserted in the lifelines list
     */
    public static void createLifeline(final Interaction interaction, final String name, final Class selClass,
            final int insertIndex) {
        // Create new property (instance) of selected class in interaction containment
        final Property lifelineProperty = interaction.createOwnedAttribute(name, selClass,
                UMLPackage.eINSTANCE.getProperty());

        // Create new lifeline with selected name
        final Lifeline newLifeline = UMLFactory.eINSTANCE.createLifeline();
        newLifeline.setName(name);

        if (interaction.getLifelines().size() == 0) {
            final EList<InteractionFragment> fragments = interaction.getFragments();
            final ExecutionOccurrenceSpecification start = UMLFactory.eINSTANCE
                    .createExecutionOccurrenceSpecification();
            start.setName(name + "Start");
            start.setCovered(newLifeline);

            final ExecutionOccurrenceSpecification end = UMLFactory.eINSTANCE.createExecutionOccurrenceSpecification();
            end.setName(name + "Finish");
            end.setCovered(newLifeline);

            final ActionExecutionSpecification execution = UMLFactory.eINSTANCE.createActionExecutionSpecification();
            execution.setName(name + "Execution");
            execution.getCovereds().add(newLifeline);
            execution.setStart(start);
            execution.setFinish(end);
            start.setExecution(execution);
            end.setExecution(execution);
            fragments.add(start);
            fragments.add(execution);
            fragments.add(end);
        }

        // Add the new lifeline at the specified location
        interaction.getLifelines().add(insertIndex, newLifeline);

        // Set represents property of the new lifeline
        newLifeline.setRepresents(lifelineProperty);
    }

    /**
     * Edits an existing {@link Lifeline} object and potentially changed properties.
     * 
     * @param interaction
     *            the {@link Interaction} object which holds the {@link Lifeline} object to edit,
     *            not <code>null</code>
     * @param editLifeline
     *            the Lifeline object to edit, not <code>null</code>
     * @param lifelines
     *            the containment reference which holds all existing {@link Lifeline} objects, not
     *            <code>null</code>
     * @param name
     *            the name for the representing property of the {@link Lifeline} object, if it
     *            equals the current property's name no changes will be made to the
     *            property/lifeline names, not <code>null</code>
     * @param selClass
     *            the class which the {@link Lifeline} object's property represents, not
     *            <code>null</code>
     * @param insertAt
     *            the {@link Lifeline} object after which the lifeline to edit should be placed, not
     *            <code>null</code>
     */
    public static void editLifeline(final Lifeline editLifeline, final String name, final Class selClass,
            final Lifeline insertAt, final int insertIndex) {

        // Retrieve the Property object represented by the lifeline
        final Property p = (Property) editLifeline.getRepresents();

        // Change the name of the property/lifeline if it has changed
        p.setName(name);
        editLifeline.setName(name);

        // Change the represented type of the property if it has changed
        p.setType(selClass);

        // Change the position of the lifeline if it has changed
        final EList<Lifeline> lifelines = editLifeline.getInteraction().getLifelines();
        if (lifelines.size() > insertIndex) {
            lifelines.move(insertIndex, editLifeline);
        } else {
            lifelines.move(lifelines.size() - 1, editLifeline);
        }

    }

    /**
     * Deletes the specified Lifeline object and all associated elements (except CombinedFragment
     * objects and InteractionOperand ojects).
     * 
     * @param interaction
     *            the interaction holding the lifeline and the elements, not <code>null</code>
     * @param delLifeline
     *            the lifeline that should be deleted, not <code>null</code>
     */
    public static void deleteLifeline(final Interaction interaction, final Lifeline delLifeline) {

        // Local variable declarations
        final EList<InteractionFragment> lifelineFragments = delLifeline.getCoveredBys();

        // EList<InteractionFragment> fragments = interaction.getFragments();
        // For each fragment of the lifeline check if its a CombinedFragment/InteractionOperand, if
        // its not delete it from the containment
        for (final InteractionFragment frag : lifelineFragments) {
            new UMLSwitch<InteractionFragment>() {
                @Override
                public InteractionFragment caseMessageOccurrenceSpecification(
                        final MessageOccurrenceSpecification mos) {
                    MessageOperations.deleteMessage(mos.getMessage());
                    return mos;
                }

                @Override
                public InteractionFragment caseActionExecutionSpecification(final ActionExecutionSpecification aes) {
                    EcoreUtil.remove(aes);
                    return aes;
                }

                @Override
                public InteractionFragment caseExecutionOccurrenceSpecification(
                        final ExecutionOccurrenceSpecification eos) {
                    EcoreUtil.remove(eos);
                    return eos;
                }

                @Override
                public InteractionFragment caseStateInvariant(final StateInvariant si) {
                    StateInvariantOperations.deleteStateInvariant(si);
                    return si;
                }
            }.doSwitch(frag);
        }

        // Delete the property and the lifeline itself
        EcoreUtil.remove(delLifeline.getRepresents());
        EcoreUtil.remove(delLifeline);

        // TODO: Check the action execution specifications, if they still have start and finish, if
        // not create execution occurrence specifications

    }

    public static void checkActionExecutions(final Interaction interaction) {
        final EList<InteractionFragment> fragments = interaction.getFragments();
        for (final InteractionFragment frag : fragments) {
            System.out.println(frag);
            new UMLSwitch<InteractionFragment>() {
                @Override
                public InteractionFragment caseActionExecutionSpecification(final ActionExecutionSpecification aes) {
                    System.out.println(aes.getStart() + " and " + aes.getFinish());
                    if (aes.getStart() == null && aes.getFinish() == null) {
                        EcoreUtil.remove(aes);
                    }
                    return aes;
                }
            }.doSwitch(frag);
        }
    }
}
