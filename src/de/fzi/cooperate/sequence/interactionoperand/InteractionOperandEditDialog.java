package de.fzi.cooperate.sequence.interactionoperand;

import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.LiteralString;

/**
 * Provides a dialog for editing an {@link InteractionOperand}
 */
public class InteractionOperandEditDialog extends InteractionOperandDialog {

    private static final long serialVersionUID = 1L;
    private static final String INTERACTIONOP_EDIT_DIALOG_TITLE = "Edit Interaction Operand";

    protected InteractionOperand editOperand;

    /**
     * Constructor for a dialog for editing an existing {@link InteractionOperand}.
     *
     * @param inter
     *            the interaction providing the context for the {@link InteractionOperand}, not
     *            <code>null</code>
     * @param operation
     *            the {@link DialogOps} enum representing wether if a {@link InteractionOperand}
     *            will be created or edited, not <code>null</code>
     * @param cf
     *            the {@link CombinedFragment} holding the {@link InteractionOperand} to be edited,
     *            not <code>null</code>
     */
    public InteractionOperandEditDialog(final InteractionOperand editOperand) {
        super((CombinedFragment) editOperand.eContainer());
        this.editOperand = editOperand;
        this.setEditProperties();
    }

    /**
     * Sets the properties for editing an {@link InteractionOperand}.
     */
    private void setEditProperties() {
        this.setTitle(INTERACTIONOP_EDIT_DIALOG_TITLE);
        final LiteralString operandConstraint = (LiteralString) this.editOperand.getGuard().getSpecification();
        this.txtConstraint.setText(operandConstraint.getValue());
        this.btnAccept.removeActionListener(this.btnAccept.getActionListeners()[0]);
        this.btnAccept.addActionListener(new InteractionOperandEditListener(this));
    }

}
