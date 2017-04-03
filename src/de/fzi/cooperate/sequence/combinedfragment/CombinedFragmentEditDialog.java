package de.fzi.cooperate.sequence.combinedfragment;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;

public class CombinedFragmentEditDialog extends CombinedFragmentDialog {

    private static final long serialVersionUID = 1L;
    private static final String COMBINEDFRAG_EDIT_DIALOG_TITLE = "Edit CombinedFragment";

    protected CombinedFragment editFragment;

    /**
     * Constructor for creating a dialog for editing an existing {@link CombinedFragment}.
     *
     * @param inter
     *            the interaction which holds the {@link CombinedFragment}, not <code>null</code>
     * @param operation
     *            the {@link DialogOps} enum representing wether a {@link CombinedFragment} gets
     *            created or edited, not <code>null</code>
     * @param editFragment
     *            the {@link CombinedFragment} to edit, not <code>null</code>
     */
    public CombinedFragmentEditDialog(final Interaction inter, final CombinedFragment editFragment) {
        super(inter);
        this.editFragment = editFragment;
        this.setEditProperties();
    }

    /**
     * Sets the properties of the {@link CombinedFragment} that gets edited.
     */
    private void setEditProperties() {
        this.setTitle(COMBINEDFRAG_EDIT_DIALOG_TITLE);
        // Set name
        this.txtName.setText(this.editFragment.getName());

        // Set interaction operator kind
        this.cmbType.setSelectedItem(this.editFragment.getInteractionOperator().getName());

        // Disable constraint
        this.lblConstraint.setEnabled(false);
        this.txtConstraint.setEnabled(false);

        // Get lifelines covered by the combined fragment
        final EList<Lifeline> coveredList = this.editFragment.getCovereds();

        // Get the selected indices for the lifelines and set the selection
        final int[] indices = new int[coveredList.size()];
        final EList<Lifeline> lifelines = this.interaction.getLifelines();
        for (int i = 0; i < coveredList.size(); i++) {
            indices[i] = lifelines.indexOf(coveredList.get(i));
        }

        this.lstLifelines.setSelectedIndices(indices);
        this.btnAccept.removeActionListener(this.btnAccept.getActionListeners()[0]);
        this.btnAccept.addActionListener(
                new CombinedFragmentEditListener(this, lifelines.toArray(new Lifeline[lifelines.size()])));

    }

}
