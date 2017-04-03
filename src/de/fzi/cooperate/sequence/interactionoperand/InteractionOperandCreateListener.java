package de.fzi.cooperate.sequence.interactionoperand;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;

/**
 * Custom action listener for the accept button in a InteractionOperandDialog
 */
public class InteractionOperandCreateListener implements ActionListener {

    private final InteractionOperandDialog iopDialog;

    public InteractionOperandCreateListener(final InteractionOperandDialog iopDialog) {
        this.iopDialog = iopDialog;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(this.iopDialog.interaction);
        domain.getCommandStack().execute(new RecordingCommand(domain) {

            @Override
            protected void doExecute() {
                InteractionOperandOperations.createInteractionOperand(
                        InteractionOperandCreateListener.this.iopDialog.comFragment,
                        InteractionOperandCreateListener.this.iopDialog.txtConstraint.getText());
            }
        });
        SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
    }

}
