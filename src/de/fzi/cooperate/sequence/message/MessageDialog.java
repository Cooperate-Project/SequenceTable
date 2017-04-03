package de.fzi.cooperate.sequence.message;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageKind;
import org.eclipse.uml2.uml.MessageSort;
import org.jdal.swing.SpringUtilities;

import de.fzi.cooperate.sequence.InsertPoint;
import de.fzi.cooperate.sequence.ModelUtilities;
import de.fzi.cooperate.sequence.NamedElementCellRenderer;

/**
 * Class providing a dialog window for creating and editing a {@link Message}.
 */
public class MessageDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE_DIALOG_TITLE = "Create New Message";
    private static final String MESSAGE_DIALOG_MSGNAME = "Message Name";
    private static final String MESSAGE_DIALOG_ORIGLL = "Origin Lifeline";
    private static final String MESSAGE_DIALOG_DESTLL = "Destination Lifeline";
    private static final String MESSAGE_DIALOG_SORT = "Message Sort";
    private static final String MESSAGE_DIALOG_KIND = "Message Kind";
    private static final String MESSAGE_DIALOG_INSERT = "Insert After/Before";
    private static final String MESSAGE_DIALOG_EMPTY = "";
    private static final String MESSAGE_DIALOG_SIGNATURE = "Without Signature";
    private static final String MESSAGE_DIALOG_PARAMETERS = "Parameters";
    private static final String MESSAGE_DIALOG_RETURN = "Return";
    private static final String MESSAGE_DIALOG_ACCEPT = "Ok";
    private static final String MESSAGE_DIALOG_CANCEL = "Cancel";

    // Variables declaration
    private JLabel lblName;
    protected JLabel lblOriginLifeline;
    protected JLabel lblDestinationLifeline;
    protected JLabel lblMessageSort;
    protected JLabel lblMessageKind;
    protected JLabel lblInsert;
    protected JLabel lblAfterBefore;
    private JLabel lblSignature;
    protected JLabel lblParameters;
    protected JLabel lblReturns;

    protected JComboBox<Lifeline> cmbOriginLifeline;
    protected JComboBox<Lifeline> cmbDestinationLifeline;
    protected JComboBox<MessageSort> cmbMessageSort;
    protected JComboBox<MessageKind> cmbMessageKind;
    protected JComboBox<Message> cmbInsert;
    protected JComboBox<InsertPoint> cmbAfterBefore;

    protected JTextField txtName;
    protected JTextField txtParameters;
    protected JTextField txtReturns;

    protected JButton btnAccept;
    private JButton btnCancel;

    protected JCheckBox chSignature;

    private JPanel contentPane;

    protected final Interaction interaction;

    // End of variables declaration

    /**
     * Constructor for creating a dialog for creating a new {@link Message}.
     *
     * @param inter
     *            the interaction holding the containment for the new {@link Message}, not
     *            <code>null</code>
     * @param operation
     *            the {@link DialogOps} enum representing wether a {@link Message} gets created or
     *            edited, not <code>null</code>
     */
    public MessageDialog(final Interaction inter) {
        super();
        this.interaction = inter;
        this.setWindowProperties();
        this.initialize();
        this.setVisible(true);
    }

    /**
     * Sets the {@link JFrame}'s properties
     *
     * @param title
     *            the title for the {@link JFrame}, not <code>null</code>
     */
    private void setWindowProperties() {
        this.setTitle(MESSAGE_DIALOG_TITLE);
        this.setSize(new Dimension(400, 390));
        this.setLocation(ModelUtilities.getScreenCenterPosition(this));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
    }

    /**
     * Initializes data and swing components.
     */
    private void initialize() {
        // Get lifelines and messages
        final Lifeline[] lifelines = (Lifeline[]) this.interaction.getLifelines().toArray();

        boolean isMessagesAmountZero = false;
        if (this.interaction.getMessages().size() > 0) {
            final List<Message> filteredMessages = ModelUtilities.getRelevantMessages(this.interaction.getMessages(),
                    lifelines[0]);
            this.initializeComponents(lifelines, MessageSort.values(), MessageKind.values(),
                    filteredMessages.toArray(new Message[filteredMessages.size()]));
        } else {
            this.initializeComponents(lifelines, MessageSort.values(), MessageKind.values(), null);
            isMessagesAmountZero = true;
        }
        this.setLabels();
        this.addComponents();
        this.setLayout();
        this.setActionListeners();
        this.setCellRenderers(isMessagesAmountZero);
    }

    /**
     * Initializes the swing components.
     *
     * @param strLifelines
     *            the string array containing the names of all existing {@link Lifeline}s, not
     *            <code>null</code>
     * @param strMessageSort
     *            the string array containing the {@link MessageSort} literals, not
     *            <code>null</code>
     * @param strMessageKind
     *            the string array containing the {@link MessageKind} literals, not
     *            <code>null</code>
     * @param strMessages
     *            the string array containing the relvant {@link Message} names, null if there are
     *            no messages
     */
    private void initializeComponents(final Lifeline[] lifelines, final MessageSort[] messageSorts,
            final MessageKind[] messageKinds, final Message[] messages) {
        this.lblName = new JLabel(MESSAGE_DIALOG_MSGNAME);
        this.lblOriginLifeline = new JLabel(MESSAGE_DIALOG_ORIGLL);
        this.lblDestinationLifeline = new JLabel(MESSAGE_DIALOG_DESTLL);
        this.lblMessageSort = new JLabel(MESSAGE_DIALOG_SORT);
        this.lblMessageKind = new JLabel(MESSAGE_DIALOG_KIND);
        this.lblInsert = new JLabel(MESSAGE_DIALOG_INSERT);
        this.lblAfterBefore = new JLabel(MESSAGE_DIALOG_EMPTY);
        this.lblSignature = new JLabel(MESSAGE_DIALOG_SIGNATURE);
        this.lblParameters = new JLabel(MESSAGE_DIALOG_PARAMETERS);
        this.lblReturns = new JLabel(MESSAGE_DIALOG_RETURN);

        this.txtName = new JTextField();
        this.txtParameters = new JTextField();
        this.txtReturns = new JTextField();

        this.cmbOriginLifeline = new JComboBox<Lifeline>(lifelines);
        this.cmbDestinationLifeline = new JComboBox<Lifeline>(lifelines);
        this.cmbMessageSort = new JComboBox<MessageSort>(messageSorts);
        this.cmbMessageKind = new JComboBox<MessageKind>(messageKinds);
        if (messages != null) {
            this.cmbInsert = new JComboBox<Message>(messages);
        } else {
            this.cmbInsert = new JComboBox<Message>();
            this.cmbInsert.setEnabled(false);
            this.lblInsert.setEnabled(false);
        }
        // this.cmbAfterBefore = new JComboBox<String>(new String[] { MESSAGE_DIALOG_AFTER,
        // MESSAGE_DIALOG_BEFORE});
        this.cmbAfterBefore = new JComboBox<InsertPoint>(InsertPoint.values());

        this.chSignature = new JCheckBox();

        this.btnAccept = new JButton(MESSAGE_DIALOG_ACCEPT);
        this.btnCancel = new JButton(MESSAGE_DIALOG_CANCEL);

        this.contentPane = (JPanel) this.getContentPane();
    }

    /**
     * Adds the components to the {@link JFrame}'s content pane.
     */
    private void addComponents() {
        this.contentPane.add(this.lblName);
        this.contentPane.add(this.txtName);
        this.contentPane.add(this.lblOriginLifeline);
        this.contentPane.add(this.cmbOriginLifeline);
        this.contentPane.add(this.lblDestinationLifeline);
        this.contentPane.add(this.cmbDestinationLifeline);
        this.contentPane.add(this.lblMessageSort);
        this.contentPane.add(this.cmbMessageSort);
        this.contentPane.add(this.lblMessageKind);
        this.contentPane.add(this.cmbMessageKind);
        this.contentPane.add(this.lblInsert);
        this.contentPane.add(this.cmbInsert);
        this.contentPane.add(this.lblAfterBefore);
        this.contentPane.add(this.cmbAfterBefore);
        this.contentPane.add(this.lblSignature);
        this.contentPane.add(this.chSignature);
        this.contentPane.add(this.lblParameters);
        this.contentPane.add(this.txtParameters);
        this.contentPane.add(this.lblReturns);
        this.contentPane.add(this.txtReturns);
        this.contentPane.add(this.btnAccept);
        this.contentPane.add(this.btnCancel);
    }

    /**
     * Sets the labels for the components.
     */
    private void setLabels() {
        this.lblName.setLabelFor(this.txtName);
        this.lblOriginLifeline.setLabelFor(this.cmbOriginLifeline);
        this.lblOriginLifeline.setLabelFor(this.cmbDestinationLifeline);
        this.lblMessageSort.setLabelFor(this.cmbMessageSort);
        this.lblMessageKind.setLabelFor(this.cmbMessageKind);
        this.lblInsert.setLabelFor(this.cmbInsert);
        this.lblAfterBefore.setLabelFor(this.cmbAfterBefore);
        this.lblSignature.setLabelFor(this.chSignature);
        this.lblParameters.setLabelFor(this.txtParameters);
        this.lblReturns.setLabelFor(this.txtReturns);
    }

    /**
     * Initializes and sets the layout.
     */
    private void setLayout() {
        final SpringLayout layout = new SpringLayout();
        this.contentPane.setLayout(layout);
        SpringUtilities.makeCompactGrid(this.contentPane, 11, 2, // rows, cols
                6, 3, // initX, initY
                6, 3); // xPad, yPad
    }

    /**
     * Sets the action listeners for the components.
     */
    private void setActionListeners() {
        this.btnAccept.addActionListener(new MessageCreateListener(this));
        this.btnCancel.addActionListener(ModelUtilities.getCancelActionListener());
        this.cmbOriginLifeline.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MessageDialog.this.setInsertBox((Lifeline) MessageDialog.this.cmbOriginLifeline.getSelectedItem());
            }
        });
        this.chSignature.addActionListener(new MessageSignatureListener(this));
        this.cmbInsert.addActionListener(new MessageInsertListener(this));
    }

    /**
     * Updates the insert combobox with the relevant {@link Message}s for the selected origin
     * {@link Lifeline}.
     *
     * @param l
     *            the currently selected origin {@link Lifeline}, not <code>null</code>
     */
    protected void setInsertBox(final Lifeline l) {
        final List<Message> filteredMessages = ModelUtilities.getRelevantMessages(this.interaction.getMessages(), l);
        final DefaultComboBoxModel<Message> model = new DefaultComboBoxModel<Message>(
                filteredMessages.toArray(new Message[filteredMessages.size()]));
        this.cmbInsert.setModel(model);
    }

    private void setCellRenderers(final boolean isMessagesAmountZero) {
        final NamedElementCellRenderer nameRenderer = new NamedElementCellRenderer();
        this.cmbOriginLifeline.setRenderer(nameRenderer);
        this.cmbDestinationLifeline.setRenderer(nameRenderer);
        if (!isMessagesAmountZero) {
            this.cmbInsert.setRenderer(new NamedElementCellRenderer());
        }
    }

}