package de.fzi.cooperate.sequence.lifeline;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.PackageableElement;
import org.jdal.swing.SpringUtilities;

import de.fzi.cooperate.sequence.ModelUtilities;
import de.fzi.cooperate.sequence.NamedElementCellRenderer;

/**
 * Class providing a dialog window for creating and editing {@link Lifeline}s.
 */
public class LifelineDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String LIFELINE_DIALOG_TITLE = "Create New Lifeline";
    private static final String LIFELINE_DIALOG_INSTANCE_NAME = "Lifeline Instance Name";
    private static final String LIFELINE_DIALOG_CLASS = "Class";
    private static final String LIFELINE_DIALOG_INSERT = "Insert At";
    private static final String LIFELINE_DIALOG_AFTERORBEFORE = "Insert Before";
    private static final String LIFELINE_DIALOG_ACCEPT = "Ok";
    private static final String LIFELINE_DIALOG_CANCEL = "Cancel";

    // Variables declaration
    private JLabel lblName;
    private JLabel lblClass;
    private JLabel lblInsert;
    private JLabel lblAfterBefore;

    protected JComboBox<PackageableElement> cmbClass;
    protected JComboBox<Lifeline> cmbInsert;
    protected JCheckBox chAfterBefore;
    protected JTextField txtName;
    protected JButton btnAccept;
    private JButton btnCancel;
    private JPanel contentPane;

    protected final Interaction interaction;
    // End of variables declaration

    /**
     * Constructor for creating a dialog for creating a new {@link Lifeline}.
     *
     * @param inter
     *            the interaction holding the containment with the {@link Lifeline}, not
     *            <code>null</code>
     */
    public LifelineDialog(final Interaction inter) {
        this.interaction = inter;
        this.setWindowProperties();
        this.initialize();
        this.setVisible(true);
    }

    /**
     * Sets the {@link JFrame}'s properties.
     */
    private void setWindowProperties() {
        this.setTitle(LIFELINE_DIALOG_TITLE);
        this.setSize(new Dimension(400, 180));
        this.setLocation(ModelUtilities.getScreenCenterPosition(this));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
    }

    /**
     * Initializes the data and swing components.
     */
    private void initialize() {
        final EList<Lifeline> lifelines = this.interaction.getLifelines();

        // Fetch all possible classes the lifeline could represent
        final EList<PackageableElement> packagedElements = this.interaction.getModel().getPackagedElements();

        // TODO: Remove restriction of class packaged elements
        final PackageableElement[] classes = packagedElements.stream().filter(t -> t.eClass().getName().equals("Class"))
                .toArray(PackageableElement[]::new);

        boolean isLifelineAmountZero = false;
        if (lifelines.size() > 0) {
            this.initializeComponents(classes, lifelines.toArray(new Lifeline[lifelines.size()]));
        } else {
            this.initializeComponents(classes, null);
            isLifelineAmountZero = true;
        }
        this.setLabels();
        this.addComponents();
        this.setLayout();
        this.setActionListeners();
        this.setCellRenderers(isLifelineAmountZero);
    }

    /**
     * Initializes the swing components.
     *
     * @param classes
     *            the string array containing the names of all classes, not <code>null</code>
     * @param lifelines
     *            the string array containing the names of all existing {@link Lifeline}s, not
     *            <code>null</code>
     */
    private void initializeComponents(final PackageableElement[] classes, final Lifeline[] lifelines) {
        this.lblName = new JLabel(LIFELINE_DIALOG_INSTANCE_NAME);
        this.lblClass = new JLabel(LIFELINE_DIALOG_CLASS);
        this.lblInsert = new JLabel(LIFELINE_DIALOG_INSERT);
        this.lblAfterBefore = new JLabel(LIFELINE_DIALOG_AFTERORBEFORE);
        this.txtName = new JTextField(3);
        this.cmbClass = new JComboBox<PackageableElement>(classes);
        this.chAfterBefore = new JCheckBox();
        if (lifelines != null) {
            this.cmbInsert = new JComboBox<Lifeline>(lifelines);
        } else {
            this.cmbInsert = new JComboBox<Lifeline>();
            this.cmbInsert.setEnabled(false);
            this.lblInsert.setEnabled(false);
            this.chAfterBefore.setEnabled(false);
            this.lblAfterBefore.setEnabled(false);
        }

        this.btnAccept = new JButton(LIFELINE_DIALOG_ACCEPT);
        this.btnCancel = new JButton(LIFELINE_DIALOG_CANCEL);
        this.contentPane = (JPanel) this.getContentPane();
    }

    /**
     * Adds all components to the {@link JFrame}'s content pane.
     */
    private void addComponents() {
        this.contentPane.add(this.lblName);
        this.contentPane.add(this.txtName);
        this.contentPane.add(this.lblClass);
        this.contentPane.add(this.cmbClass);
        this.contentPane.add(this.lblInsert);
        this.contentPane.add(this.cmbInsert);
        this.contentPane.add(this.lblAfterBefore);
        this.contentPane.add(this.chAfterBefore);
        this.contentPane.add(this.btnAccept);
        this.contentPane.add(this.btnCancel);
    }

    /**
     * Set the labels for the components.
     */
    private void setLabels() {
        this.lblName.setLabelFor(this.txtName);
        this.lblClass.setLabelFor(this.cmbClass);
        this.lblInsert.setLabelFor(this.cmbInsert);
        this.lblAfterBefore.setLabelFor(this.chAfterBefore);
    }

    /**
     * Initialize and set the layout.
     */
    private void setLayout() {
        final SpringLayout layout = new SpringLayout();
        this.contentPane.setLayout(layout);
        SpringUtilities.makeCompactGrid(this.contentPane, 5, 2, // rows, cols
                6, 3, // initX, initY
                6, 3); // xPad, yPad
    }

    /**
     * Set the action listeners.
     *
     * @param ps
     *            the list containing all possible {@link Class}es, not <code>nul</code>
     * @param lifelines
     *            the {@link EList} containing all existing {@link Lifeline}s, null if there are no
     *            lifelines
     */
    private void setActionListeners() {
        this.btnAccept.addActionListener(new LifelineCreateListener(this));
        this.btnCancel.addActionListener(ModelUtilities.getCancelActionListener());
    }

    private void setCellRenderers(final boolean isLifelineAmountZero) {
        this.cmbClass.setRenderer(new NamedElementCellRenderer());
        if (!isLifelineAmountZero) {
            this.cmbInsert.setRenderer(new NamedElementCellRenderer());
        }
    }
}