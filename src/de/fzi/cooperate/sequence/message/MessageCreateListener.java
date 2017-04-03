package de.fzi.cooperate.sequence.message;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageKind;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;

import de.fzi.cooperate.sequence.InsertPoint;
import de.fzi.cooperate.sequence.ModelUtilities;

/**
 * Custom action listener for the accept button in a MessageDialog
 */
public class MessageCreateListener implements ActionListener {

    private final MessageDialog msgDialog;

    public MessageCreateListener(final MessageDialog msgDialog) {
        this.msgDialog = msgDialog;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(this.msgDialog.interaction);
        domain.getCommandStack().execute(new RecordingCommand(domain) {

            @Override
            protected void doExecute() {
                // TODO: Changes for the first message to use the normal createMessage

                if (MessageCreateListener.this.msgDialog.interaction.getMessages().size() > 0) {
                    final Message selMsg = (Message) MessageCreateListener.this.msgDialog.cmbInsert.getSelectedItem();
                    final MessageOccurrenceSpecification sendE = (MessageOccurrenceSpecification) selMsg.getSendEvent();
                    final MessageOccurrenceSpecification recvE = (MessageOccurrenceSpecification) selMsg
                            .getReceiveEvent();
                    final MessageOccurrenceSpecification insert = sendE
                            .getCovered() == MessageCreateListener.this.msgDialog.cmbOriginLifeline.getSelectedItem()
                                    ? sendE : recvE;
                    final InsertPoint insertPoint = (InsertPoint) MessageCreateListener.this.msgDialog.cmbAfterBefore
                            .getSelectedItem();
                    MessageOperations.createMessage(MessageCreateListener.this.msgDialog.interaction,
                            MessageCreateListener.this.msgDialog.txtName.getText(),
                            (Lifeline) MessageCreateListener.this.msgDialog.cmbOriginLifeline.getSelectedItem(),
                            (Lifeline) MessageCreateListener.this.msgDialog.cmbDestinationLifeline.getSelectedItem(),
                            (MessageSort) MessageCreateListener.this.msgDialog.cmbMessageSort.getSelectedItem(),
                            (MessageKind) MessageCreateListener.this.msgDialog.cmbMessageKind.getSelectedItem(), insert,
                            insertPoint,
                            ModelUtilities.getParameters(MessageCreateListener.this.msgDialog.txtParameters.getText()),
                            ModelUtilities.getReturn(MessageCreateListener.this.msgDialog.txtReturns.getText()));
                } else {
                    MessageOperations.createFirstMessage(MessageCreateListener.this.msgDialog.interaction,
                            MessageCreateListener.this.msgDialog.txtName.getText(),
                            (Lifeline) MessageCreateListener.this.msgDialog.cmbOriginLifeline.getSelectedItem(),
                            (Lifeline) MessageCreateListener.this.msgDialog.cmbDestinationLifeline.getSelectedItem(),
                            (MessageSort) MessageCreateListener.this.msgDialog.cmbMessageSort.getSelectedItem(),
                            (MessageKind) MessageCreateListener.this.msgDialog.cmbMessageKind.getSelectedItem(),
                            ModelUtilities.getParameters(MessageCreateListener.this.msgDialog.txtParameters.getText()),
                            ModelUtilities.getReturn(MessageCreateListener.this.msgDialog.txtReturns.getText()));
                }
            }
        });
        SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
    }

}
