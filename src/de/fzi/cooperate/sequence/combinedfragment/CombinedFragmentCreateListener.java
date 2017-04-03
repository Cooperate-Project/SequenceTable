package de.fzi.cooperate.sequence.combinedfragment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.uml2.uml.InteractionOperatorKind;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;

import de.fzi.cooperate.sequence.ModelUtilities;

/**
 * Custom action listener for the accept button in a CombinedFragmentDialog
 */
public class CombinedFragmentCreateListener implements ActionListener {

    private final CombinedFragmentDialog cfDialog;
    private final Lifeline[] lifelines;

    /**
     * Public constructor for a new {@link CombinedFragmentCreateListener}
     *
     * @param cfDialog
     *            the corresponding {@link CombinedFragmentDialog}, not <code>null</code>
     */
    public CombinedFragmentCreateListener(final CombinedFragmentDialog cfDialog, final Lifeline[] lifelines) {
        this.cfDialog = cfDialog;
        this.lifelines = lifelines;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(this.cfDialog.interaction);
        domain.getCommandStack().execute(new RecordingCommand(domain) {

            @Override
            protected void doExecute() {
                CombinedFragmentOperations.createCombinedFragment(
                        CombinedFragmentCreateListener.this.cfDialog.interaction,
                        CombinedFragmentCreateListener.this.cfDialog.txtName.getText(),
                        (InteractionOperatorKind) CombinedFragmentCreateListener.this.cfDialog.cmbType
                                .getSelectedItem(),
                        // ModelUtilities.getSelectedLifelines(lifelines,
                        // cfDialog.lstLifelines.getSelectedIndices()),
                        ModelUtilities.getSelectedElements(Lifeline.class,
                                CombinedFragmentCreateListener.this.lifelines,
                                CombinedFragmentCreateListener.this.cfDialog.lstLifelines.getSelectedIndices()),
                        CombinedFragmentCreateListener.this.cfDialog.txtConstraint.getText(),
                        (Message) CombinedFragmentCreateListener.this.cfDialog.cmbInsert.getSelectedItem());
            }
        });
        // Dispose window after creating/editing
        SwingUtilities.getWindowAncestor((JComponent) e.getSource()).dispose();
    }

}
