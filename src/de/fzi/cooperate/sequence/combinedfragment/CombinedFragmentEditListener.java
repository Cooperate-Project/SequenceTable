package de.fzi.cooperate.sequence.combinedfragment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.uml2.uml.InteractionOperatorKind;
import org.eclipse.uml2.uml.Lifeline;

import de.fzi.cooperate.sequence.ModelUtilities;

public class CombinedFragmentEditListener implements ActionListener {

    private final CombinedFragmentEditDialog cfDialog;
    private final Lifeline[] lifelines;

    public CombinedFragmentEditListener(final CombinedFragmentEditDialog cfDialog, final Lifeline[] lifelines) {
        this.cfDialog = cfDialog;
        this.lifelines = lifelines;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(this.cfDialog.interaction);
        domain.getCommandStack().execute(new RecordingCommand(domain) {

            @Override
            protected void doExecute() {
                CombinedFragmentOperations.editCombinedFragment(
                        CombinedFragmentEditListener.this.cfDialog.txtName.getText(),
                        (InteractionOperatorKind) CombinedFragmentEditListener.this.cfDialog.cmbType.getSelectedItem(),
                        ModelUtilities.getSelectedLifelines(CombinedFragmentEditListener.this.lifelines,
                                CombinedFragmentEditListener.this.cfDialog.lstLifelines.getSelectedIndices()),
                        CombinedFragmentEditListener.this.cfDialog.editFragment);
            }
        });
        // Dispose window after creating/editing
        SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
    }

}
