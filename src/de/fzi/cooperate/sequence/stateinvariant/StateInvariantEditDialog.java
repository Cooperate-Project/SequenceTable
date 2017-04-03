package de.fzi.cooperate.sequence.stateinvariant;

import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.StateInvariant;

/**
 * Provides a dialog for editing a {@link StateInvariant}
 */
public class StateInvariantEditDialog extends StateInvariantDialog {

    private static final long serialVersionUID = 1L;

    protected StateInvariant editInvariant;

    /**
     * Constructor for a dialog for editing an existing {@link StateInvariant}.
     *
     * @param inter
     *            the interaction providing the context for the new {@link StateInvariant}, not
     *            <code>null</code>
     * @param operation
     *            the {@link DialogOps} enum representing wether if a {@link StateInvariant} will be
     *            created or edited, not <code>null</code>
     * @param editInvariant
     *            the {@link StateInvariant} that gets edited, not <code>null</code>
     */
    public StateInvariantEditDialog(final Interaction inter, final StateInvariant editInvariant) {
        super(inter);
        this.editInvariant = editInvariant;
        this.setEditProperties();
        this.setVisible(true);
    }

    /**
     * Sets the properties of the {@link StateInvariant} that gets edited.
     */
    private void setEditProperties() {
        this.txtName.setText(this.editInvariant.getName());
        this.cmbLifeline.setSelectedItem(this.editInvariant.getCovereds().get(0));
        final LiteralString sp = (LiteralString) this.editInvariant.getInvariant().getSpecification();
        this.txtConstraint.setText(sp.getValue());
        this.btnAccept.removeActionListener(this.btnAccept.getActionListeners()[0]);
        this.btnAccept.addActionListener(new StateInvariantEditListener(this));
    }
}
