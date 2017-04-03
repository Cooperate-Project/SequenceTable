package de.fzi.cooperate.sequence.interactionoperand;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;

/**
 * Custom action listener for the accept button in a InteractionOperandEditDialog
 */
public class InteractionOperandEditListener implements ActionListener {

    private final InteractionOperandEditDialog iopEditDialog;

    public InteractionOperandEditListener(final InteractionOperandEditDialog iopEditDialog) {
        this.iopEditDialog = iopEditDialog;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(this.iopEditDialog.interaction);
        domain.getCommandStack().execute(new RecordingCommand(domain) {

            @Override
            protected void doExecute() {
                InteractionOperandOperations.editInteractionOperand(
                        InteractionOperandEditListener.this.iopEditDialog.editOperand,
                        InteractionOperandEditListener.this.iopEditDialog.txtConstraint.getText());
            }
        });
        SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
    }

}
