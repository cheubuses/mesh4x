package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.FolderUIController;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

public class FolderUI extends AbstractUI {

	private static final long serialVersionUID = 8670884881480486083L;

	private static final Log LOGGER = LogFactory.getLog(FolderUI.class);
	
	// MODEL VARIABLES
	private JLabel labelFileName = null;
	private JTextField txtFileName = null;
	private JButton btnFile = null;
	
	private FolderUIController controller;
	private JFileChooser fileChooser = null;
	private File file = null;

	// BUSINESS METHODS
	public FolderUI(String fileName, FolderUIController controller) {
		super();
		this.controller = controller;
		this.controller.addView(this);
		this.initialize();
		this.file = new File(fileName);
		this.txtFileName.setText(this.file.getName());
	}

	private void initialize() 
	{
		this.setLayout(null);
		this.setBackground(Color.WHITE);
		this.add(getFileNameLabel(), null);
		this.add(getFileNameText(), null);
		this.add(getBtnFile(), null);
	}

	private JLabel getFileNameLabel() {
		if (labelFileName == null) {
			labelFileName = new JLabel();
			labelFileName.setText(EktooUITranslator.getFolderFileNameLabel());
			labelFileName.setSize(new Dimension(85, 16));
			labelFileName.setPreferredSize(new Dimension(85, 16));
			labelFileName.setLocation(new Point(8, 9));
		}
		return labelFileName;
	}

	private JTextField getFileNameText() {
		if (txtFileName == null) {
			txtFileName = new JTextField();
			txtFileName.setBounds(new Rectangle(99, 8, 149, 20));
		}
		return txtFileName;
	}

	public JButton getBtnFile() {
		if (btnFile == null) {
			btnFile = new JButton();
			btnFile.setText(EktooUITranslator.getBrowseButtonLabel());
			btnFile.setBounds(new Rectangle(259, 8, 34, 20));
			btnFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getFileChooser().setSelectedFile(file);
					int returnVal = getFileChooser().showOpenDialog(btnFile);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File selectedFile = getFileChooser().getSelectedFile();
						if (selectedFile != null) {
							try{
								controller.changeFileName(selectedFile.getCanonicalPath());
								txtFileName.setText(selectedFile.getName());
								setFile(selectedFile);
							} catch (Exception ex) {
								LOGGER.error(ex.getMessage(), ex);
							}
						}
					}
				}
			});
		}
		return btnFile;
	}

	
	public String getFileName() {
		try {
			return this.file.getCanonicalPath();
		} catch (IOException e) {
            LOGGER.debug(e.getMessage());
			// nothing to do
			return null;
		}
	}
	
	public FolderUIController getController() {
		return controller;
	}
	
	public JFileChooser getFileChooser() {
		if (fileChooser == null){
			fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}		
		return fileChooser;
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

  @Override
  public void modelPropertyChange(final PropertyChangeEvent evt)
  {
    if ( evt.getPropertyName().equals( FolderUIController.FOLDER_NAME_PROPERTY))
    {
      String newStringValue = evt.getNewValue().toString();
      if (!  getFileNameText().getText().equals(newStringValue))
        getFileNameText().setText(newStringValue);
    }
  }

}