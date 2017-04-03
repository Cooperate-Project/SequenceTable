package de.fzi.cooperate.sequence.combinedfragment;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionOperatorKind;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.jdal.swing.SpringUtilities;

import de.fzi.cooperate.sequence.ModelUtilities;
import de.fzi.cooperate.sequence.NamedElementCellRenderer;

/**
 * Class for providing a dialog window for creating or editing a {@link CombinedFragment}.
 */
public class CombinedFragmentDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String COMBINEDFRAG_DIALOG_TITLE = "Create New CombinedFragment";
    private static final String COMBINEDFRAG_DIALOG_CFNAME = "CF Name";
    private static final String COMBINEDFRAG_DIALOG_TYPE = "Type";
    private static final String COMBINEDFRAG_DIALOG_CONSTRAINT = "Constraint";
    private static final String COMBINEDFRAG_DIALOG_INSERT = "Insert At";
    private static final String COMBINEDFRAG_DIALOG_AFTERBEFORE = "Insert Before";
    private static final String COMBINEDFRAG_DIALOG_COVERAGE = "Lifeline Coverage";
    private static final String COMBINEDFRAG_DIALOG_ACCEPT = "Ok";
    private static final String COMBINEDFRAG_DIALOG_CANCEL = "Cancel";

    // Variables declaration
    private JLabel lblName;
    private JLabel lblType;
    protected JLabel lblConstraint;
    private JLabel lblLifelines;
    private JLabel lblInsert;
    private JLabel lblAfterBefore;

    protected JComboBox<InteractionOperatorKind> cmbType;
    protected JComboBox<Message> cmbInsert;

    protected JTextField txtName;
    protected JTextField txtConstraint;

    protected JList<Lifeline> lstLifelines;

    protected JCheckBox chAfterBefore;

    protected JButton btnAccept;
    private JButton btnCancel;

    private JScrollPane scrllList;

    private JPanel contentPane;

    protected final Interaction interaction;
    // End of variables declaration

    /**
     * Constructor for a dialog for creating a new {@link CombinedFragment}.
     *
     * @param inter
     *            the interaction holding the containment for the new {@link CombinedFragment}, not
     *            <code>null</code>
     * @param operation
     *            the {@link DialogOps} enum representing wether a {@link CombinedFragment} gets
     *            created or edited, not <code>null</code>
     */
    public CombinedFragmentDialog(final Interaction inter) {
        this.interaction = inter;
        this.setWindowProperties();
        this.initialize();
        this.setVisible(true);
    }

    /**
     * Initializes the necessary data and swing components
     */
    private void initialize() {

        // Get lifelines
        final Lifeline[] lifelines = (Lifeline[]) this.interaction.getLifelines().toArray();

        // Get messages
        final EList<Message> messages = this.interaction.getMessages();
        // final List<Message> filteredMessages = ModelUtilities.getRelevantMessages(messages,
        // lifelines[0]);

        final InteractionOperatorKind[] types = new InteractionOperatorKind[] { InteractionOperatorKind.ALT_LITERAL,
                InteractionOperatorKind.OPT_LITERAL, InteractionOperatorKind.PAR_LITERAL,
                InteractionOperatorKind.CRITICAL_LITERAL, InteractionOperatorKind.LOOP_LITERAL };

        // Initialize the swing components
        this.initializeComponents(lifelines, types, messages.toArray(new Message[messages.size()]));

        // JList properties
        this.lstLifelines.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.lstLifelines.setLayoutOrientation(JList.VERTICAL);
        this.lstLifelines.setVisibleRowCount(2);

        // Scrollpane properties
        this.scrllList = new JScrollPane(this.lstLifelines);
        this.scrllList.setPreferredSize(new Dimension(250, 100));

        this.setLabels();
        this.addComponents();
        this.setLayout();
        this.setActionListeners(lifelines);
        this.setCellRenderers();
    }

    /**
     * Initializes the swing components.
     *
     * @param strLifelines
     *            the {@link Lifeline} names as string array, not <code>null</code>
     * @param strTypes
     *            the {@link InteractionOperatorKind} literals as string array, not
     *            <code>null</code>
     * @param strMessages
     *            the {@link Message} names as string array, <code>null</code>
     */
    private void initializeComponents(final Lifeline[] lifelines, final InteractionOperatorKind[] types,
            final Message[] messages) {
        this.lblName = new JLabel(COMBINEDFRAG_DIALOG_CFNAME);
        this.lblType = new JLabel(COMBINEDFRAG_DIALOG_TYPE);
        this.lblConstraint = new JLabel(COMBINEDFRAG_DIALOG_CONSTRAINT);
        this.lblInsert = new JLabel(COMBINEDFRAG_DIALOG_INSERT);
        this.lblLifelines = new JLabel(COMBINEDFRAG_DIALOG_COVERAGE);
        this.lblAfterBefore = new JLabel(COMBINEDFRAG_DIALOG_AFTERBEFORE);

        this.txtName = new JTextField(1);
        this.txtConstraint = new JTextField(1);

        this.chAfterBefore = new JCheckBox();

        this.cmbType = new JComboBox<InteractionOperatorKind>(types);
        this.cmbInsert = new JComboBox<Message>(messages);

        this.lstLifelines = new JList<Lifeline>(lifelines);

        this.btnAccept = new JButton(COMBINEDFRAG_DIALOG_ACCEPT);
        this.btnCancel = new JButton(COMBINEDFRAG_DIALOG_CANCEL);

        this.contentPane = (JPanel) this.getContentPane();
    }

    /**
     * Adds the swing components to the {@link JFrame}'s content pane.
     */
    private void addComponents() {
        this.contentPane.add(this.lblName);
        this.contentPane.add(this.txtName);
        this.contentPane.add(this.lblType);
        this.contentPane.add(this.cmbType);
        this.contentPane.add(this.lblConstraint);
        this.contentPane.add(this.txtConstraint);
        this.contentPane.add(this.lblInsert);
        this.contentPane.add(this.cmbInsert);
        this.contentPane.add(this.lblAfterBefore);
        this.contentPane.add(this.chAfterBefore);
        this.contentPane.add(this.lblLifelines);
        this.contentPane.add(this.scrllList);
        this.contentPane.add(this.btnAccept);
        this.contentPane.add(this.btnCancel);
    }

    /**
     * Sets the labels for the components.
     */
    private void setLabels() {
        this.lblName.setLabelFor(this.txtName);
        this.lblType.setLabelFor(this.cmbType);
        this.lblConstraint.setLabelFor(this.txtConstraint);
        this.lblInsert.setLabelFor(this.cmbInsert);
        this.lblAfterBefore.setLabelFor(this.chAfterBefore);
        this.lblLifelines.setLabelFor(this.scrllList);
    }

    /**
     * Initializes and sets the layout.
     */
    private void setLayout() {
        final SpringLayout layout = new SpringLayout();
        this.contentPane.setLayout(layout);
        SpringUtilities.makeCompactGrid(this.contentPane, 7, 2, // rows, cols
                6, 3, // initX, initY
                6, 3); // xPad, yPad
    }

    /**
     * Sets the {@link JFrame}'s properties.
     */
    private void setWindowProperties() {
        this.setTitle(COMBINEDFRAG_DIALOG_TITLE);
        this.setSize(new Dimension(400, 330));
        this.setLocation(ModelUtilities.getScreenCenterPosition(this));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
    }

    /**
     * Sets the action listeners for the buttons.
     */
    private void setActionListeners(final Lifeline[] lifelines) {
        this.btnAccept.addActionListener(new CombinedFragmentCreateListener(this, lifelines));
        this.btnCancel.addActionListener(ModelUtilities.getCancelActionListener());
    }

    private void setCellRenderers() {
        this.cmbInsert.setRenderer(new NamedElementCellRenderer());
        this.lstLifelines.setCellRenderer(new NamedElementCellRenderer());
    }

}