package de.fzi.cooperate.sequence.lifeline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;

/**
 * Custom action listener for the accept button in a LifelineDialog
 */
public class LifelineCreateListener implements ActionListener {

    private final LifelineDialog llDialog;

    public LifelineCreateListener(final LifelineDialog llDialog) {
        this.llDialog = llDialog;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(this.llDialog.interaction);
        domain.getCommandStack().execute(new RecordingCommand(domain) {

            @Override
            protected void doExecute() {
                // Create function call
                final int insertIndex = LifelineCreateListener.this.llDialog.chAfterBefore.isSelected()
                        ? LifelineCreateListener.this.llDialog.cmbInsert.getSelectedIndex()
                        : LifelineCreateListener.this.llDialog.cmbInsert.getSelectedIndex() + 1;
                LifelineOperations.createLifeline(LifelineCreateListener.this.llDialog.interaction,
                        LifelineCreateListener.this.llDialog.txtName.getText(),
                        (org.eclipse.uml2.uml.Class) LifelineCreateListener.this.llDialog.cmbClass.getSelectedItem(),
                        insertIndex);
            }
        });
        SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
    }

}
