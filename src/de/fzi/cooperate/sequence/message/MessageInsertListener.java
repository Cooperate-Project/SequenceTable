package de.fzi.cooperate.sequence.message;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;

import de.fzi.cooperate.sequence.ModelUtilities;

/**
 * Custom action listener for the insert combobox of a {@link MessageDialog}
 */
public class MessageInsertListener implements ActionListener {

    private final MessageDialog msgDialog;

    public MessageInsertListener(final MessageDialog msgDialog) {
        this.msgDialog = msgDialog;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Lifeline l = (Lifeline) this.msgDialog.cmbOriginLifeline.getSelectedItem();
        final Message m = (Message) this.msgDialog.cmbInsert.getSelectedItem();
        final MessageOccurrenceSpecification mos = ((MessageOccurrenceSpecification) m.getSendEvent()).getCovered() == l
                ? (MessageOccurrenceSpecification) m.getSendEvent()
                : (MessageOccurrenceSpecification) m.getReceiveEvent();
        final ActionExecutionSpecification soe = ModelUtilities.isExecutionStartOrEnd(l, mos);
        // Check if the message occurrence specification of the selected message on the
        // originating lifeline is a start or finish point on an action execution, if it is
        // disable the after/before combobox
        if (soe != null) {
            this.msgDialog.lblAfterBefore.setEnabled(true);
            this.msgDialog.cmbAfterBefore.setEnabled(true);
        } else {
            this.msgDialog.lblAfterBefore.setEnabled(false);
            this.msgDialog.cmbAfterBefore.setEnabled(false);
        }
    }

}
