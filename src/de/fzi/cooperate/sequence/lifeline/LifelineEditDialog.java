package de.fzi.cooperate.sequence.lifeline;

import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;

/**
 * Provides a dialog for editing a {@link Lifeline}
 */
public class LifelineEditDialog extends LifelineDialog {

    private static final long serialVersionUID = 1L;
    private static final String LIFELINE_EDIT_DIALOG_TITLE = "Edit Lifeline";

    protected Lifeline editLifeline;

    public LifelineEditDialog(final Interaction inter, final Lifeline editLifeline) {
        super(inter);
        this.editLifeline = editLifeline;
        this.setEditProperties();
    }

    private void setEditProperties() {
        this.setTitle(LIFELINE_EDIT_DIALOG_TITLE);
        this.txtName.setText(this.editLifeline.getRepresents().getName());
        this.cmbClass.setSelectedItem(this.editLifeline.getRepresents().getType());
        this.cmbInsert.setSelectedItem(this.editLifeline);
        this.btnAccept.removeActionListener(this.btnAccept.getActionListeners()[0]);
        this.btnAccept.addActionListener(new LifelineEditListener(this));
    }

}
