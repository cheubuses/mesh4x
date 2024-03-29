package org.mesh4j.sync.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessHelper;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.mappings.SyncMode;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.message.channel.sms.connection.smslib.ModemHelper;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.message.protocol.IItemEncoding;
import org.mesh4j.sync.message.protocol.ItemEncoding;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.ui.translator.MeshUITranslator;
import org.mesh4j.sync.utils.ConsoleNotification;
import org.mesh4j.sync.utils.SourceIdMapper;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.swtdesigner.FocusTraversalOnArray;
import com.swtdesigner.SwingResourceManager;

public class MeshUI{

	private final static Log Logger = LogFactory.getLog(MeshUI.class);

	// CONSTANTS
	private final static int SYNCHRONIZE_HTTP = -1;
	private final static int SYNCHRONIZE_SMS = 0;
	private final static int CANCEL_SYNC = 1;
	private final static int ADD_DATA_SOURCE = 2;
	private static final int DISCOVERY_MODEMS = 3;
	private final static int CHANGE_DEVICE = 4;
	private final static int SAVE_DEFAULTS = 5;
	private final static int GENERATE_KML = 6;
	private final static int GENERATE_KML_WEB = 7;
	private final static int DOWNLOAD_SCHEMA = 8;
	private final static int DOWNLOAD_MAPPINGS = 9;
	
	// MODEL VARIABLES
	private JFrame frame;
	private JComboBox comboSMSDevice;
	private JTextField textFieldPhoneNumber;
	private JTextField textFieldDataSource;
	private JComboBox comboTables;
	private JTextArea textAreaConsole;
	private JButton buttonSynchronize;
	private JButton buttonCancel;
	private JButton buttonClean;
	private JScrollPane scrollPaneConsole;
	private JButton buttonModemDiscovery;
	private JButton buttonAddDataSource;
	private JButton buttonOpenFileDataSource;
	private ButtonGroup buttonGroup = new ButtonGroup();	
	private JTextField textFieldURL;
	private JLabel labelUrl;
	private JLabel labelSMSDevice;
	private JLabel labelPhoneNumber;
	private JRadioButton radioEndpointSMS;
	private JRadioButton radioEndpointHTTP;
	private JButton buttonSaveDefaults;
	private JButton buttonKmlGenerator;
	private JButton buttonKmlWebGenerator;
	private JButton buttonDownloadSchema;
	private JButton buttonHideShowConsole;
	private JButton buttonHideShowConsole1;
	private JButton buttonHideShowConsole2;
	private JLabel imageStatus;
	
	private ConsoleNotification consoleNotification;
	private SourceIdMapper sourceIdResolver;
	
	private Modem modem = null;
	private MessageSyncEngine syncEngine;
	private boolean syncInProcess = false;
	private int channel = SYNCHRONIZE_HTTP;
	
	private IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
	private String baseDirectory;
	private int senderDelay;
	private int receiverDelay;
	private int maxMessageLenght;
	private IMessageEncoding messageEncoding;
	private String portName;
	private int baudRate;
	private String defaultPhoneNumber;
	private String defaultDataSource;
	private String defaultTableName;
	private String defaultURL;
	private String kmlTemplateFileName;
	private String kmlTemplateNetworkLinkFileName;
	private String geoCoderKey;
	private boolean discoveryModems = false;
	private String inDir = "";
	private String outDir = "";
	private String myEndpointId = "";
	
	// BUSINESS METHODS
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MeshUI window = new MeshUI();
					window.frame.pack();
					window.frame.setSize(window.frame.getPreferredSize());
					window.frame.setVisible(true);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
		});
	}

	public MeshUI() throws Exception {
		this.initializeProperties();
		this.initializeModem();
		this.createUI();
		this.consoleNotification = new ConsoleNotification(this.textAreaConsole, this.imageStatus, this);
		this.consoleNotification.setReadyImageStatus();
		this.startUpSyncEngine();
	}

	private void initializeModem() {
		if(portName.length() > 0 && baudRate > 0){
			modem = ModemHelper.getModem(portName, baudRate);
		}
	}

	private void initializeProperties() throws Exception {
		PropertiesProvider propertiesProvider = new PropertiesProvider();
		
		this.baseDirectory = propertiesProvider.getBaseDirectory();
		this.senderDelay = propertiesProvider.getDefaultSendRetryDelay();
		this.receiverDelay = propertiesProvider.getDefaultReceiveRetryDelay();
		this.maxMessageLenght = propertiesProvider.getDefaultMaxMessageLenght();
		this.identityProvider = propertiesProvider.getIdentityProvider();
		this.messageEncoding = propertiesProvider.getDefaultMessageEncoding();
		this.portName = propertiesProvider.getDefaultPort();
		this.baudRate = propertiesProvider.getDefaultBaudRate();
		this.defaultPhoneNumber = propertiesProvider.getDefaultPhoneNumber();
		this.defaultDataSource = propertiesProvider.getDefaultDataSource();
		this.defaultTableName = propertiesProvider.getDefaultTable();
		this.defaultURL = propertiesProvider.getDefaultURL();
		this.kmlTemplateFileName = propertiesProvider.getDefaultKMLTemplateFileName();
		this.kmlTemplateNetworkLinkFileName = propertiesProvider.getDefaultKMLTemplateNetworkLinkFileName();
		this.geoCoderKey = propertiesProvider.getGeoCoderKey();
		this.sourceIdResolver = new SourceIdMapper(this.baseDirectory+ "/myDataSources.properties");
		this.inDir = propertiesProvider.getEmulationInFolder();
		this.outDir = propertiesProvider.getEmulationOutRootFolder();
		this.myEndpointId = propertiesProvider.getEmulationEndpointId();
	}
	
	protected void startUpSyncEngine() throws Exception {

		IItemEncoding itemEncoding = new ItemEncoding(100);
		//IItemEncoding itemEncoding = new ItemEncodingFixedBlock(100);

		
		if(modem != null && !modem.getManufacturer().equals(MeshUITranslator.getLabelDemo())){

			this.syncEngine = SyncEngineUtil.createSyncEngine(this.sourceIdResolver, modem, baseDirectory, senderDelay, receiverDelay, maxMessageLenght,
				identityProvider, itemEncoding, messageEncoding, new ISmsConnectionInboundOutboundNotification[]{consoleNotification}, new IMessageSyncAware[]{consoleNotification}); 
			this.syncEngine.getChannel().startUp();
		}
				
		if(this.syncEngine == null){
			this.syncEngine = SyncEngineUtil.createSyncEngineEmulator(this.sourceIdResolver,
					messageEncoding, 
					identityProvider, 
					itemEncoding,
					baseDirectory+"/", 
					senderDelay, 
					receiverDelay, 
					maxMessageLenght,
					new SmsEndpoint(MeshUITranslator.getLabelDemo()),
					new ISmsConnectionInboundOutboundNotification[]{consoleNotification}, 
					new IMessageSyncAware[]{consoleNotification}, 
					false, 
					inDir,
					outDir,
					myEndpointId);
		}
	}

	private void createUI() {
		frame = new JFrame();
		
		WindowAdapter windowAdapter = new WindowAdapter() {
			public void windowClosed(final WindowEvent e) {
				shutdownSyncEngine();
			}
		};
		
		frame.addWindowListener(windowAdapter);
		frame.setIconImage(SwingResourceManager.getImage(MeshUI.class, "/cdc.gif"));
		frame.getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("322dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("108dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("41dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("103dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		frame.setResizable(false);
		frame.setTitle(MeshUITranslator.getTitle());
		frame.setBounds(100, 100, 664, 572);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final JPanel panelCommunications = new JPanel();
		panelCommunications.setFocusCycleRoot(true);
		panelCommunications.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), MeshUITranslator.getGroupCommunications(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panelCommunications.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("44dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("221dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("36dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("9dlu"),
				RowSpec.decode("11dlu"),
				RowSpec.decode("14dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		frame.getContentPane().add(panelCommunications, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));

		textFieldPhoneNumber = new JTextField();
		textFieldPhoneNumber.setToolTipText(MeshUITranslator.getToolTipPhoneNumber());
		panelCommunications.add(textFieldPhoneNumber, new CellConstraints(4, 5, 3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		textFieldPhoneNumber.setText(this.defaultPhoneNumber);
		
		labelSMSDevice = DefaultComponentFactory.getInstance().createLabel(MeshUITranslator.getLabelSMSDevice());
		panelCommunications.add(labelSMSDevice, new CellConstraints(2, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		labelPhoneNumber = DefaultComponentFactory.getInstance().createLabel(MeshUITranslator.getLabelPhoneNumber());
		panelCommunications.add(labelPhoneNumber, new CellConstraints(2, 5, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		ActionListener deviceActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(CHANGE_DEVICE);
				task.execute();
			}
		};
		
		comboSMSDevice = new JComboBox();
		comboSMSDevice.setFocusable(false);
		
		Modem demoModem = getDemoModem();
		comboSMSDevice.setModel(modem == null ? new DefaultComboBoxModel(new Modem[]{demoModem}) : new DefaultComboBoxModel(new Modem[]{modem}));
		comboSMSDevice.setToolTipText(modem == null ? demoModem.toString() : modem.toString());
		comboSMSDevice.addActionListener(deviceActionListener);
		panelCommunications.add(comboSMSDevice, new CellConstraints(4, 3, CellConstraints.FILL, CellConstraints.DEFAULT));

		ActionListener modemDiscoveryActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(isDicoveringModems()){
					consoleNotification.stopDiscovery();
				} else {
					Task task = new Task(DISCOVERY_MODEMS);
					task.execute();
				}
			}
		};	
		
		buttonModemDiscovery = new JButton();
		buttonModemDiscovery.setFocusable(false);
		buttonModemDiscovery.setText(MeshUITranslator.getLabelModemDiscovery());
		buttonModemDiscovery.setToolTipText(MeshUITranslator.getToolTipAutoDetect());
		buttonModemDiscovery.addActionListener(modemDiscoveryActionListener);
		panelCommunications.add(buttonModemDiscovery, new CellConstraints(6, 3, CellConstraints.FILL, CellConstraints.FILL));

		labelUrl = new JLabel();
		labelUrl.setText(MeshUITranslator.getLabelURL());
		panelCommunications.add(labelUrl, new CellConstraints(2, 8, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		textFieldURL = new JTextField();
		textFieldURL.setText(this.defaultURL);
		panelCommunications.add(textFieldURL, new CellConstraints(4, 8, 3, 1));

		ActionListener channelSMSActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				textFieldURL.setEnabled(false);
				labelUrl.setEnabled(false);
				
				textFieldPhoneNumber.setEnabled(true);
				comboSMSDevice.setEnabled(true);
				buttonModemDiscovery.setEnabled(true);
				labelSMSDevice.setEnabled(true);
				labelPhoneNumber.setEnabled(true);
				
				channel = SYNCHRONIZE_SMS;
			}
		};
		
		radioEndpointSMS = new JRadioButton();
		buttonGroup.add(radioEndpointSMS);
		radioEndpointSMS.setText(MeshUITranslator.getLabelChannelSMS());
		radioEndpointSMS.addActionListener(channelSMSActionListener);
		panelCommunications.add(radioEndpointSMS, new CellConstraints(2, 2));

		ActionListener channelHTTPActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				textFieldURL.setEnabled(true);
				labelUrl.setEnabled(true);
				
				textFieldPhoneNumber.setEnabled(false);
				comboSMSDevice.setEnabled(false);
				buttonModemDiscovery.setEnabled(false);
				labelSMSDevice.setEnabled(false);
				labelPhoneNumber.setEnabled(false);
				 
				channel = SYNCHRONIZE_HTTP;
			}
		};
		
		radioEndpointHTTP = new JRadioButton();
		buttonGroup.add(radioEndpointHTTP);
		radioEndpointHTTP.setText(MeshUITranslator.getLabelChannelWEB());
		radioEndpointHTTP.setSelected(true);
		radioEndpointHTTP.addActionListener(channelHTTPActionListener);

		panelCommunications.add(radioEndpointHTTP, new CellConstraints(2, 7));
		panelCommunications.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] {labelSMSDevice, labelPhoneNumber, textFieldPhoneNumber, buttonSynchronize}));

		final JPanel panelDataSource = new JPanel();
		panelDataSource.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), MeshUITranslator.getLabelDataSource(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panelDataSource.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("251dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("35dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC}));
		frame.getContentPane().add(panelDataSource, new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));

		textFieldDataSource = new JTextField();
		textFieldDataSource.setToolTipText(MeshUITranslator.getToolTipDataSource());
		textFieldDataSource.setText(this.defaultDataSource);
		panelDataSource.add(textFieldDataSource, new CellConstraints(2, 2, 3, 1, CellConstraints.FILL, CellConstraints.FILL));

		ActionListener fileChooserFileActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String selectedFileName = openFileDialog(textFieldDataSource.getText());
				if(selectedFileName != null){
					textFieldDataSource.setText(selectedFileName);
				}
			}
		};
		
		buttonOpenFileDataSource = new JButton();
		buttonOpenFileDataSource.setToolTipText(MeshUITranslator.getToolTipFileChooser());
		buttonOpenFileDataSource.setText(MeshUITranslator.getLabelFileChooser());
		buttonOpenFileDataSource.addActionListener(fileChooserFileActionListener);
		panelDataSource.add(buttonOpenFileDataSource, new CellConstraints(6, 2, CellConstraints.FILL, CellConstraints.FILL));

		final JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC},
			new RowSpec[] {
				}));
		panelDataSource.add(panelButtons, new CellConstraints(2, 6, 5, 1));

		final JLabel labelTable = new JLabel();
		labelTable.setText(MeshUITranslator.getLabelTable());
		panelDataSource.add(labelTable, new CellConstraints(2, 4));

		comboTables = new JComboBox();
		comboTables.setModel(this.getDataSourceTableModel());
		panelDataSource.add(comboTables, new CellConstraints(4, 4));

		ActionListener addDataSourceActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(ADD_DATA_SOURCE);
				task.execute();
			}
		};	
		
		buttonAddDataSource = new JButton();
		buttonAddDataSource.setText(MeshUITranslator.getLabelAddDataSource());
		buttonAddDataSource.addActionListener(addDataSourceActionListener);
		panelDataSource.add(buttonAddDataSource, new CellConstraints(6, 4));
		
		final JPanel panelFooter = new JPanel();
		panelFooter.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("37dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("177dlu"),
				ColumnSpec.decode("5dlu"),
				ColumnSpec.decode("95dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				RowSpec.decode("104dlu")}));
		frame.getContentPane().add(panelFooter, new CellConstraints(2, 8, CellConstraints.FILL, CellConstraints.FILL));

		scrollPaneConsole = new JScrollPane();
		scrollPaneConsole.setBorder(new BevelBorder(BevelBorder.LOWERED));
		scrollPaneConsole.setAutoscrolls(true);
		panelFooter.add(scrollPaneConsole, new CellConstraints("2, 1, 5, 1, fill, fill"));

		textAreaConsole = new JTextArea();
		scrollPaneConsole.setViewportView(textAreaConsole);
		textAreaConsole.setFocusAccelerator('\b');
		textAreaConsole.setAutoscrolls(true);
		textAreaConsole.setWrapStyleWord(false);
		textAreaConsole.setLineWrap(false);
		textAreaConsole.setOpaque(false);
		textAreaConsole.setToolTipText("");
		textAreaConsole.setName("");
		textAreaConsole.setEditable(false);

		scrollPaneConsole.setVisible(false);

		final JLabel labelLogo = new JLabel();
		labelLogo.setIcon(SwingResourceManager.getIcon(MeshUI.class, "/Epi2002.jpg"));
		labelLogo.setText("");
		panelFooter.add(labelLogo, new CellConstraints(3, 1, 4, 1, CellConstraints.FILL, CellConstraints.FILL));

		imageStatus = new JLabel();
		imageStatus.setText("");
		panelFooter.add(imageStatus, new CellConstraints(1, 1, CellConstraints.CENTER, CellConstraints.CENTER));

		// disable sms channel group
		textFieldPhoneNumber.setEnabled(false);
		comboSMSDevice.setEnabled(false);
		buttonModemDiscovery.setEnabled(false);
		labelSMSDevice.setEnabled(false);
		labelPhoneNumber.setEnabled(false);

		// Tabbed panel
		JPanel panelExchange = new JPanel(false);
		panelExchange.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("53dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("91dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("65dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
				
		ActionListener synchronizeActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(channel);
				task.execute();
			}
		};	
		buttonSynchronize = new JButton();
		panelExchange.add(buttonSynchronize, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
		buttonSynchronize.setText(MeshUITranslator.getSynchronize());
		buttonSynchronize.addActionListener(synchronizeActionListener);
		
		ActionListener cancelActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(CANCEL_SYNC);
				task.execute();
			}
		};	
		buttonCancel = new JButton();
		panelExchange.add(buttonCancel, new CellConstraints(4, 2, CellConstraints.FILL, CellConstraints.FILL));
		buttonCancel.setText(MeshUITranslator.getCancel());
		buttonCancel.addActionListener(cancelActionListener);
		
		ActionListener hiShowActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scrollPaneConsole.setVisible(!scrollPaneConsole.isVisible());
				if(scrollPaneConsole.isVisible()){
					buttonHideShowConsole.setText(MeshUITranslator.getLabelHideConsole());
					buttonHideShowConsole.setToolTipText(MeshUITranslator.getToolTipHideConsole());
					buttonHideShowConsole1.setText(MeshUITranslator.getLabelHideConsole());
					buttonHideShowConsole1.setToolTipText(MeshUITranslator.getToolTipHideConsole());
					buttonHideShowConsole2.setText(MeshUITranslator.getLabelHideConsole());
					buttonHideShowConsole2.setToolTipText(MeshUITranslator.getToolTipHideConsole());
					labelLogo.setVisible(false);
				}else{
					buttonHideShowConsole.setText(MeshUITranslator.getLabelShowConsole());
					buttonHideShowConsole.setToolTipText(MeshUITranslator.getToolTipShowConsole());
					buttonHideShowConsole1.setText(MeshUITranslator.getLabelShowConsole());
					buttonHideShowConsole1.setToolTipText(MeshUITranslator.getToolTipShowConsole());
					buttonHideShowConsole2.setText(MeshUITranslator.getLabelShowConsole());
					buttonHideShowConsole2.setToolTipText(MeshUITranslator.getToolTipShowConsole());
					labelLogo.setVisible(true);
				}				
				frame.pack();
				frame.repaint();
			}
		};	
		buttonHideShowConsole = new JButton();
		buttonHideShowConsole.setText(MeshUITranslator.getLabelShowConsole());
		buttonHideShowConsole.setToolTipText(MeshUITranslator.getToolTipShowConsole());
		buttonHideShowConsole.addActionListener(hiShowActionListener);
		panelExchange.add(buttonHideShowConsole, new CellConstraints(6, 2, CellConstraints.CENTER, CellConstraints.FILL));
	       
	    JPanel panelMap = new JPanel(false);
	    panelMap.setLayout(new FormLayout(
	    	new ColumnSpec[] {
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("53dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("47dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("65dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC},
	    	new RowSpec[] {
	    		FormFactory.RELATED_GAP_ROWSPEC,
	    		FormFactory.DEFAULT_ROWSPEC,
	    		FormFactory.RELATED_GAP_ROWSPEC}));
	    
		ActionListener kmlGeneratorActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(GENERATE_KML);
				task.execute();
			}
		};
	    buttonKmlGenerator = new JButton();
	    buttonKmlGenerator.addActionListener(kmlGeneratorActionListener);
	    buttonKmlGenerator.setText(MeshUITranslator.getLabelKML());
	    panelMap.add(buttonKmlGenerator, new CellConstraints(2, 2));


		ActionListener kmlWebGeneratorActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(GENERATE_KML_WEB);
				task.execute();
			}
		};
	    buttonKmlWebGenerator = new JButton();
	    buttonKmlWebGenerator.setText(MeshUITranslator.getLabelKMLWEB());
	    buttonKmlWebGenerator.addActionListener(kmlWebGeneratorActionListener);
	    panelMap.add(buttonKmlWebGenerator, new CellConstraints(4, 2));

		buttonHideShowConsole1 = new JButton();
		buttonHideShowConsole1.setText(MeshUITranslator.getLabelShowConsole());
		buttonHideShowConsole1.setToolTipText(MeshUITranslator.getToolTipShowConsole());
		buttonHideShowConsole1.addActionListener(hiShowActionListener);
	    panelMap.add(buttonHideShowConsole1, new CellConstraints(6, 2));
	    	    
	    JPanel panelSettings = new JPanel(false);
	    panelSettings.setLayout(new FormLayout(
	    	new ColumnSpec[] {
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("57dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("78dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("72dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("76dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC},
	    	new RowSpec[] {
	    		FormFactory.RELATED_GAP_ROWSPEC,
	    		FormFactory.DEFAULT_ROWSPEC,
	    		FormFactory.RELATED_GAP_ROWSPEC}));
	    
		ActionListener saveDefaultActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(SAVE_DEFAULTS);
				task.execute();
			}
		};		
		buttonSaveDefaults = new JButton();
		buttonSaveDefaults.setText(MeshUITranslator.getLabelSaveDefaults());
		buttonSaveDefaults.addActionListener(saveDefaultActionListener);
	    panelSettings.add(buttonSaveDefaults, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.CENTER));

		ActionListener downloadSchemaActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(DOWNLOAD_MAPPINGS);
				task.execute();
			}
		};
		buttonDownloadSchema = new JButton();
		buttonDownloadSchema.setText(MeshUITranslator.getLabelDownloadMappings());
		buttonDownloadSchema.addActionListener(downloadSchemaActionListener);
		panelSettings.add(buttonDownloadSchema, new CellConstraints(4, 2, CellConstraints.FILL, CellConstraints.CENTER));

		ActionListener cleanConsoleActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				textAreaConsole.setText("");
			}
		};	
		buttonClean = new JButton();
		buttonClean.setText(MeshUITranslator.getLabelCleanConsole());
		buttonClean.addActionListener(cleanConsoleActionListener);
	    panelSettings.add(buttonClean, new CellConstraints(6, 2, CellConstraints.FILL, CellConstraints.CENTER));

		buttonHideShowConsole2 = new JButton();
		buttonHideShowConsole2.setText(MeshUITranslator.getLabelShowConsole());
		buttonHideShowConsole2.setToolTipText(MeshUITranslator.getToolTipShowConsole());
		buttonHideShowConsole2.addActionListener(hiShowActionListener);
	    panelSettings.add(buttonHideShowConsole2, new CellConstraints(8, 2));
	    
	    JTabbedPane tabbedPane = new JTabbedPane();
	    tabbedPane.addTab(MeshUITranslator.getLabelTabDataExchange(), panelExchange);
	    tabbedPane.addTab(MeshUITranslator.getLabelTabMap(), panelMap);
	    tabbedPane.addTab(MeshUITranslator.getLabelTabSettings(), panelSettings);
	    
	    frame.getContentPane().add(tabbedPane, new CellConstraints(2, 6));
	}

	private Modem getDemoModem() {
		return new Modem("", 0, MeshUITranslator.getLabelDemo(), "", "", "", 0, 0);
	}

	private ComboBoxModel getDataSourceTableModel() {
		try{
			Set<String> tableNames = MsAccessHelper.getTableNames(this.defaultDataSource);
			ComboBoxModel model = new DefaultComboBoxModel(tableNames.toArray());
			model.setSelectedItem(this.defaultTableName);
			return model;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return new DefaultComboBoxModel();
		}
	}

	private void shutdownSyncEngine(){
		try{
			this.syncEngine.getChannel().shutdown();
		} catch(Throwable e){
			Logger.error(e.getMessage(), e);
		}
	}
	
	private class Task extends SwingWorker<Void, Void> {
		 
		// MODEL VARIABLES
		private int action = 0;
		 
		// BUSINESS METHODS
	    public Task(int action) {
			super();
			this.action = action;
		}
	
		@Override
	    public Void doInBackground() {
			disableAllButtons();
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			consoleNotification.setInProcessImageStatus();
			
    		if(action == SYNCHRONIZE_SMS){
    			textAreaConsole.setText("");
    			
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidMSAccessTable());
	    			consoleNotification.setErrorImageStatus();
					return null;
				}
    			
				SyncEngineUtil.addDataSource(sourceIdResolver, dataSource, tableName);

    			try{
        			syncInProcess = true;
        			buttonCancel.setEnabled(true);

	    			String sourceAlias = sourceIdResolver.getSourceName(dataSource, tableName);
					SyncEngineUtil.synchronize(
						syncEngine, 
						SyncMode.SendAndReceiveChanges,
						textFieldPhoneNumber.getText(),
						sourceAlias, 
	    				identityProvider, 
	    				baseDirectory, 
	    				sourceIdResolver);

	    		} catch(Throwable t){
	    			syncInProcess = false;
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, MeshUITranslator.getLabelFailed());
	    		}
    		} 

    		if(action == SYNCHRONIZE_HTTP){
				String url = textFieldURL.getText();
				if(!HttpSyncAdapterFactory.isValidURL(url)){
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidURL());
	    			consoleNotification.setErrorImageStatus();
					return null;
				}
				
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidMSAccessTable());
	    			consoleNotification.setErrorImageStatus();
					return null;
				}
				
    			try{
    				String sourceAlias = sourceIdResolver.getSourceName(dataSource, tableName);
    				consoleNotification.beginSync(url, sourceAlias);
    				List<Item> conflicts = SyncEngineUtil.synchronize(textFieldURL.getText(), sourceIdResolver.getSourceName(dataSource, tableName), identityProvider, baseDirectory, sourceIdResolver, SyncMode.SendAndReceiveChanges);
    				consoleNotification.endSync(textFieldURL.getText(), sourceAlias, conflicts);
	    			consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, MeshUITranslator.getLabelFailed());
	    		}
    		} 

    		if(action == CANCEL_SYNC){
    			String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidMSAccessTable());
	    			consoleNotification.setErrorImageStatus();
					return null;
				}
				
    			SyncEngineUtil.cancelSynchronize(syncEngine, textFieldPhoneNumber.getText(), sourceIdResolver.getSourceName(dataSource, tableName));
    			syncInProcess = false;
    			consoleNotification.setEndSyncImageStatus();
    		} 

    		if(action == ADD_DATA_SOURCE){
    			try{
    				String dataSource = textFieldDataSource.getText();
    				String tableName = (String)comboTables.getSelectedItem();
    				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
    	    			consoleNotification.log(MeshUITranslator.getErrorInvalidMSAccessTable());
    	    			consoleNotification.setErrorImageStatus();
    					return null;
    				}
    				
    				SyncEngineUtil.addDataSource(sourceIdResolver, dataSource, tableName);
    				consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, t.getMessage());
	    		}
    		}
    		
    		if(action == DISCOVERY_MODEMS){
				consoleNotification.log(MeshUITranslator.getMessageBeginModemDiscovery());
    			setModeDiscoveryModems();
				buttonModemDiscovery.setText(MeshUITranslator.getLabelStopModemDiscovery());
				buttonModemDiscovery.setToolTipText(MeshUITranslator.getToolTipStopAutoDetect());    			
    			
    			Modem[] modems = SyncEngineUtil.getAvailableModems(consoleNotification);
    			consoleNotification.log(MeshUITranslator.getMessageEndModemDiscovery(modems.length));
    			
    			if(modems.length == 0){
    				modems = new Modem[]{getDemoModem()};
    			}
    			comboSMSDevice.setModel(new DefaultComboBoxModel(modems));
    			    			
    			setModeNoDiscoveryModems();
    			buttonModemDiscovery.setText(MeshUITranslator.getLabelModemDiscovery());
    			buttonModemDiscovery.setToolTipText(MeshUITranslator.getToolTipAutoDetect());
    			    			
    			consoleNotification.setEndSyncImageStatus();
    		}
    		
    		if(action == CHANGE_DEVICE){
				modem = (Modem)comboSMSDevice.getSelectedItem();
				shutdownSyncEngine();
				
				try{
					startUpSyncEngine();
	    			consoleNotification.setEndSyncImageStatus();
				} catch (Exception exc) {
					shutdownSyncEngine();
					consoleNotification.setErrorImageStatus();
					consoleNotification.logError(exc, MeshUITranslator.getLabelDeviceConnectionFailed(modem.toString()));
					Logger.error(exc.getMessage(), exc);
				}
				comboSMSDevice.setToolTipText(modem.toString());
    		}
    		
    		if(action == SAVE_DEFAULTS){
    			try{
    				SyncEngineUtil.saveDefaults(modem, textFieldPhoneNumber.getText(), textFieldDataSource.getText(), (String)comboTables.getSelectedItem(), textFieldURL.getText());
    				consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, t.getMessage());
	    		}
    		}
    		
    		if(action == GENERATE_KML){
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidMSAccessTable());
					return null;
				}
    			try{
    				SyncEngineUtil.generateKML(geoCoderKey, kmlTemplateFileName, dataSource, tableName, baseDirectory, sourceIdResolver, identityProvider);
	    			consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, MeshUITranslator.getLabelKMLFailed());
	    		}
    		}
    		
    		if(action == GENERATE_KML_WEB){
				String url = textFieldURL.getText();
				if(!HttpSyncAdapterFactory.isValidURL(url)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidURL());
					return null;
				}
				
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidMSAccessTable());
					return null;
				}
				
    			try{
	    			String urlWebKml = url + "?format=kml";
	    			String fileName = baseDirectory + "/"+ tableName + "_web.kml";

	    			SyncEngineUtil.makeKMLWithNetworkLink(kmlTemplateNetworkLinkFileName, fileName, tableName, urlWebKml);
	    			consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, MeshUITranslator.getLabelKMLFailed());
	    		}
	    		
    		}
    		
    		if(action == DOWNLOAD_SCHEMA){
				String url = textFieldURL.getText();
				if(!HttpSyncAdapterFactory.isValidURL(url)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidURL());
					return null;
				}
				
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidMSAccessTable());
					return null;
				}
				
    			try{
	    			SyncEngineUtil.downloadSchema(url, tableName, baseDirectory);
	    			consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, MeshUITranslator.getLabelDownloadSchemaFailed());
	    		}
    		}
    		
    		if(action == DOWNLOAD_MAPPINGS){
				String url = textFieldURL.getText();
				if(!HttpSyncAdapterFactory.isValidURL(url)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidURL());
					return null;
				}
				
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(MeshUITranslator.getErrorInvalidMSAccessTable());
					return null;
				}
				
    			try{
	    			SyncEngineUtil.downloadMappings(url, tableName, baseDirectory);
	    			consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, MeshUITranslator.getLabelDownloadMappingsFailed());
	    		}
    		}
	        return null;
	    }

		@Override
	    public void done() {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	        enableAllButtons();
	    }
		
	}

	 private void enableAllButtons(){
		 if(!syncInProcess){
			 buttonCancel.setEnabled(true);
			 buttonSynchronize.setEnabled(true);
			 buttonClean.setEnabled(true);
			 
			 radioEndpointHTTP.setEnabled(true);
			 radioEndpointSMS.setEnabled(true);
			 
			 if(channel == SYNCHRONIZE_SMS){
				 labelSMSDevice.setEnabled(true);
				 labelPhoneNumber.setEnabled(true);
				 comboSMSDevice.setEnabled(true);
				 buttonModemDiscovery.setEnabled(true);
				 textFieldPhoneNumber.setEnabled(true);
			 }
			 
			 textFieldDataSource.setEnabled(true);
			 comboTables.setEnabled(true);
			 buttonAddDataSource.setEnabled(true);
			 buttonOpenFileDataSource.setEnabled(true);
	
			 if(channel == SYNCHRONIZE_HTTP){
				 labelUrl.setEnabled(true);
				 textFieldURL.setEnabled(true);
			 }
			 
			 buttonSaveDefaults.setEnabled(true);
			 
			 buttonKmlGenerator.setEnabled(true);
			 buttonKmlWebGenerator.setEnabled(true);
			 buttonDownloadSchema.setEnabled(true);
		 }
	 }
	 
	 private void disableAllButtons(){
		 buttonSynchronize.setEnabled(false);
		 buttonClean.setEnabled(false);
		 buttonCancel.setEnabled(false);

		 radioEndpointHTTP.setEnabled(false);
		 radioEndpointSMS.setEnabled(false);
		 
		 labelSMSDevice.setEnabled(false);
		 labelPhoneNumber.setEnabled(false);
		 textFieldPhoneNumber.setEnabled(false);
		 comboSMSDevice.setEnabled(false);
		 
		 textFieldDataSource.setEnabled(false);
		 comboTables.setEnabled(false);
		 buttonAddDataSource.setEnabled(false);
		 buttonOpenFileDataSource.setEnabled(false);

		 labelUrl.setEnabled(false);
		 textFieldURL.setEnabled(false);
		 
		 //buttonModemDiscovery.setEnabled(false);
		 
		 buttonSaveDefaults.setEnabled(false);
		 
		 buttonKmlGenerator.setEnabled(false);
		 buttonKmlWebGenerator.setEnabled(false);
		 buttonDownloadSchema.setEnabled(false);
	 }
	
	private String openFileDialog(String fileName){
		String fileNameSelected = openFileDialog(fileName, new FileNameExtensionFilter(MeshUITranslator.getLabelDataSourceFileExtensions(), "mdb"));
		return fileNameSelected;
	}
	
	private String openFileDialog(String fileName, FileNameExtensionFilter filter){
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		
		if(fileName != null && fileName.trim().length() > 0){
			File file = new File(fileName);
			chooser.setSelectedFile(file);
		}
		
		int returnVal = chooser.showOpenDialog(this.frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else{
			return null;
		}
	}
	
	private void setModeNoDiscoveryModems() {
		discoveryModems = false;
		consoleNotification.startDiscovery();
		
	}

	private void setModeDiscoveryModems() {
		discoveryModems = true;
		consoleNotification.startDiscovery();		
	}
	
	private boolean isDicoveringModems() {
		return discoveryModems;
	}

	public void setEndSync() {
		this.syncInProcess = false;
		this.enableAllButtons();		
	}

	public IMessageSyncProtocol getSyncProtocol() {
		return this.syncEngine.getSyncProtocol();
	}
}
