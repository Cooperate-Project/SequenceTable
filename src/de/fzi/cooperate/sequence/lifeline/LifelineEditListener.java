package de.fzi.cooperate.sequence.lifeline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;

/**
 * Custom action listener for the accept button in a LifelineEditDialog
 */
public class LifelineEditListener implements ActionListener {

    private final LifelineEditDialog llEditDialog;

    public LifelineEditListener(final LifelineEditDialog llEditDialog) {
        this.llEditDialog = llEditDialog;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(this.llEditDialog.interaction);
        domain.getCommandStack().execute(new RecordingCommand(domain) {

            @Override
            protected void doExecute() {
                final int selectedInsertIndex = LifelineEditListener.this.llEditDialog.cmbInsert.getSelectedIndex();
                final int insertIndex = LifelineEditListener.this.llEditDialog.chAfterBefore.isSelected()
                        ? selectedInsertIndex : selectedInsertIndex + 1;
                LifelineOperations.editLifeline(LifelineEditListener.this.llEditDialog.editLifeline,
                        LifelineEditListener.this.llEditDialog.txtName.getText(),
                        (org.eclipse.uml2.uml.Class) LifelineEditListener.this.llEditDialog.cmbClass.getSelectedItem(),
                        LifelineEditListener.this.llEditDialog.cmbInsert.getItemAt(selectedInsertIndex), insertIndex);
            }
        });
        SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
    }

}
