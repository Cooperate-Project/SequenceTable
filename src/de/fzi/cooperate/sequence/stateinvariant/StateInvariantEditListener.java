package de.fzi.cooperate.sequence.stateinvariant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.uml2.uml.Lifeline;

/**
 * Custom action listener for the accept button in a StateInvariantEditDialog
 */
public class StateInvariantEditListener implements ActionListener {

    private final StateInvariantEditDialog siEditDialog;

    public StateInvariantEditListener(final StateInvariantEditDialog siEditDialog) {
        this.siEditDialog = siEditDialog;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(this.siEditDialog.interaction);
        domain.getCommandStack().execute(new RecordingCommand(domain) {

            @Override
            protected void doExecute() {
                StateInvariantOperations.editStateInvariant(
                        StateInvariantEditListener.this.siEditDialog.txtName.getText(),
                        (Lifeline) StateInvariantEditListener.this.siEditDialog.cmbLifeline.getSelectedItem(),
                        StateInvariantEditListener.this.siEditDialog.txtConstraint.getText(),
                        StateInvariantEditListener.this.siEditDialog.editInvariant);

            }
        });
        SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
    }

}
