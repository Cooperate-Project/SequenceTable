package de.fzi.cooperate.sequence.message;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;

/**
 * Provides a dialog for editing a {@link Message}
 */
public class MessageEditDialog extends MessageDialog {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE_EDIT_DIALOG_TITLE = "Edit Message";
    private static final String MESSAGE_EDIT_DIALOG_MOVE = "Move To";

    private final Message editMessage;

    /**
     * Constructor for creating a dialog for editing an existing {@link Message}.
     *
     * @param inter
     *            the interaction holding the containment for the new {@link Message}, not
     *            <code>null</code>
     * @param operation
     *            the {@link DialogOps} enum representing wether a {@link Message} gets created or
     *            edited, not <code>null</code>
     * @param editMessage
     *            the {@link Message} to edit, not <code>null</code>
     */
    public MessageEditDialog(final Interaction inter, final Message editMessage) {
        super(inter);
        this.editMessage = editMessage;
        this.setEditProperties();
    }

    /**
     * Sets the properties of the {@link Message} to edit.
     */
    private void setEditProperties() {
        this.setTitle(MESSAGE_EDIT_DIALOG_TITLE);
        this.txtName.setText(this.editMessage.getName());
        this.cmbOriginLifeline.setEnabled(false);
        this.cmbDestinationLifeline.setEnabled(false);
        this.lblOriginLifeline.setEnabled(false);
        this.lblDestinationLifeline.setEnabled(false);
        this.lblMessageKind.setEnabled(false);
        this.lblMessageSort.setEnabled(false);
        this.cmbMessageSort.setSelectedItem(this.editMessage.getMessageSort());
        this.cmbMessageKind.setSelectedItem(this.editMessage.getMessageKind());
        this.cmbMessageKind.setEnabled(false);
        this.cmbMessageSort.setEnabled(false);
        this.setInsertBox(((MessageOccurrenceSpecification) this.editMessage.getSendEvent()).getCovered());
        this.cmbInsert.setSelectedItem(this.editMessage);
        this.lblInsert.setText(MESSAGE_EDIT_DIALOG_MOVE);
        this.btnAccept.removeActionListener(this.btnAccept.getActionListeners()[0]);
        this.btnAccept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final TransactionalEditingDomain domain = TransactionUtil
                        .getEditingDomain(MessageEditDialog.this.interaction);
                domain.getCommandStack().execute(new RecordingCommand(domain) {

                    @Override
                    protected void doExecute() {
                        final Message selMsg = (Message) MessageEditDialog.this.cmbInsert.getSelectedItem();
                        final MessageOccurrenceSpecification sendE = (MessageOccurrenceSpecification) selMsg
                                .getSendEvent();
                        final MessageOccurrenceSpecification recvE = (MessageOccurrenceSpecification) selMsg
                                .getReceiveEvent();
                        final MessageOccurrenceSpecification insert = selMsg.getMessageSort()
                                .equals(MessageSort.REPLY_LITERAL) ? recvE : sendE;
                        MessageOperations.editMessage(MessageEditDialog.this.txtName.getText(), insert,
                                MessageEditDialog.this.editMessage);

                    }
                });
                SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
            }
        });
    }

}
