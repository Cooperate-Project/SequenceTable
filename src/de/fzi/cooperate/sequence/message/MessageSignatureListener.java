package de.fzi.cooperate.sequence.message;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Custom action listener for the signature toggle on a {@link MessageDialog}
 */
public class MessageSignatureListener implements ActionListener {

    private final MessageDialog msgDialog;

    public MessageSignatureListener(final MessageDialog msgDialog) {
        this.msgDialog = msgDialog;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (this.msgDialog.txtParameters.isEnabled() && this.msgDialog.txtReturns.isEnabled()) {
            this.msgDialog.txtParameters.setText("");
            this.msgDialog.txtReturns.setText("");
            this.msgDialog.txtParameters.setEnabled(false);
            this.msgDialog.txtReturns.setEnabled(false);
            this.msgDialog.lblParameters.setEnabled(false);
            this.msgDialog.lblReturns.setEnabled(false);
        } else {
            this.msgDialog.txtParameters.setEnabled(true);
            this.msgDialog.txtReturns.setEnabled(true);
            this.msgDialog.lblParameters.setEnabled(true);
            this.msgDialog.lblReturns.setEnabled(true);
        }

    }

}
