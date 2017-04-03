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
 * Custom action listener for the accept button in a StateInvariantDialog
 */
public class StateInvariantCreateListener implements ActionListener {

    private final StateInvariantDialog siDialog;

    public StateInvariantCreateListener(final StateInvariantDialog siDialog) {
        this.siDialog = siDialog;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(this.siDialog.interaction);
        domain.getCommandStack().execute(new RecordingCommand(domain) {

            @Override
            protected void doExecute() {
                StateInvariantOperations.createStateInvariant(StateInvariantCreateListener.this.siDialog.interaction,
                        StateInvariantCreateListener.this.siDialog.txtName.getText(),
                        (Lifeline) StateInvariantCreateListener.this.siDialog.cmbLifeline.getSelectedItem(),
                        StateInvariantCreateListener.this.siDialog.txtConstraint.getText());

            }
        });
        SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
    }

}
