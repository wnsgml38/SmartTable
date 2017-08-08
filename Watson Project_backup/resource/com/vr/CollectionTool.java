package com.vr;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import com.ibm.watson.developer_cloud.http.HttpHeaders;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.AddImageToCollectionOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Collection;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Collection.Status;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JProgressBar;
import java.awt.Dimension;
import java.awt.Color;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
@SuppressWarnings("rawtypes")
public class CollectionTool extends JFrame
{
	private static final String API_KEY = "eefb0ae9edf72ba5fd48d02298b77ff8420afbea";
	private static final String META_FILE = "OXFORD.CSV";
	private static final String CATEGORY = "CATEGORY";
	private static final String IMAGE_FILE = "IMAGE_FILE";
	private static final String PRICE = "PRICE";
	private static final String BRAND = "BRAND";
	
	static class META
	{
		String category;
		String ImageFile;
		String price;
		String brand;
	}

	static class COLLECTION
	{
		String collectionId;
		String collectionName;
		
		public String toString()
		{
			return collectionName;
		}
	}
	
	private VisualRecognition service = null;

	private JComboBox cbCollection;
	private JButton cmdRef;
	private JButton cmdCreate;
	private JTextField txtPath;
	private JButton cmdAdd;
	private JProgressBar progressBar;
	private JButton cmdDelete;
	
	public CollectionTool()
	{
		setTitle("Collection Tool");
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Collection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		cbCollection = new JComboBox();
		cbCollection.setEditable(true);
		GridBagConstraints gbc_cbCollection = new GridBagConstraints();
		gbc_cbCollection.insets = new Insets(0, 0, 0, 5);
		gbc_cbCollection.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbCollection.gridx = 0;
		gbc_cbCollection.gridy = 0;
		panel.add(cbCollection, gbc_cbCollection);
		
		cmdRef = new JButton("Refresh");
		cmdRef.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				Runnable r = new Runnable() {
					@Override
					public void run() {
						cmdRef.setEnabled(false);
						_refresh();
						cmdRef.setEnabled(true);
					}
				};
				
				(new Thread(r)).start();
			}
		});
		GridBagConstraints gbc_cmdRef = new GridBagConstraints();
		gbc_cmdRef.insets = new Insets(0, 0, 0, 5);
		gbc_cmdRef.gridx = 1;
		gbc_cmdRef.gridy = 0;
		panel.add(cmdRef, gbc_cmdRef);
		
		cmdCreate = new JButton("Create");
		cmdCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				Runnable r = new Runnable() {
					@Override
					public void run() {
						cmdCreate.setEnabled(false);
						_createCollection();
						cmdCreate.setEnabled(true);
					}
				};
				
				(new Thread(r)).start();				
			}
		});
		GridBagConstraints gbc_cmdCreate = new GridBagConstraints();
		gbc_cmdCreate.insets = new Insets(0, 0, 0, 5);
		gbc_cmdCreate.gridx = 2;
		gbc_cmdCreate.gridy = 0;
		panel.add(cmdCreate, gbc_cmdCreate);
		
		cmdDelete = new JButton("Delete");
		cmdDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				Runnable r = new Runnable() {
					@Override
					public void run() {
						cmdDelete.setEnabled(false);
						_deleteCollection();
						cmdDelete.setEnabled(true);
					}
				};
				
				(new Thread(r)).start();
			}
		});
		GridBagConstraints gbc_cmdDelete = new GridBagConstraints();
		gbc_cmdDelete.gridx = 3;
		gbc_cmdDelete.gridy = 0;
		panel.add(cmdDelete, gbc_cmdDelete);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Images", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panel_1, BorderLayout.CENTER);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblNewLabel = new JLabel("이미지 폴더");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_1.add(lblNewLabel, gbc_lblNewLabel);
		
		txtPath = new JTextField();
		txtPath.setBackground(Color.WHITE);
		txtPath.setEditable(false);
		GridBagConstraints gbc_txtPath = new GridBagConstraints();
		gbc_txtPath.insets = new Insets(0, 0, 5, 5);
		gbc_txtPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPath.gridx = 1;
		gbc_txtPath.gridy = 0;
		panel_1.add(txtPath, gbc_txtPath);
		txtPath.setColumns(40);
		
		JButton cmdBrowse = new JButton("..");
		cmdBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				_open();
			}
		});
		GridBagConstraints gbc_cmdBrowse = new GridBagConstraints();
		gbc_cmdBrowse.insets = new Insets(0, 0, 5, 0);
		gbc_cmdBrowse.gridx = 2;
		gbc_cmdBrowse.gridy = 0;
		panel_1.add(cmdBrowse, gbc_cmdBrowse);
		
		JLabel lblNewLabel_1 = new JLabel("진행");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panel_1.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(146, 20));
		progressBar.setMaximumSize(new Dimension(32767, 20));
		progressBar.setMinimumSize(new Dimension(10, 20));
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.insets = new Insets(0, 0, 5, 5);
		gbc_progressBar.gridx = 1;
		gbc_progressBar.gridy = 1;
		panel_1.add(progressBar, gbc_progressBar);
		
		cmdAdd = new JButton("Add Images");
		cmdAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				final String path = txtPath.getText();
				final COLLECTION c = (COLLECTION)cbCollection.getSelectedItem();
				
				if ( path.length() > 0 && c != null )
				{
					Runnable r = new Runnable() {
						@Override
						public void run() {
							_addImages(path, c.collectionId);
						}
					};
					
					(new Thread(r)).start();
				}
			}
		});
		GridBagConstraints gbc_cmdAdd = new GridBagConstraints();
		gbc_cmdAdd.insets = new Insets(20, 0, 10, 5);
		gbc_cmdAdd.gridx = 1;
		gbc_cmdAdd.gridy = 2;
		panel_1.add(cmdAdd, gbc_cmdAdd);
		
		_init();
	}
	
	/**
	 * @return
	 */
	protected Map<String, String> getDefaultHeaders()
	{
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpHeaders.X_WATSON_LEARNING_OPT_OUT, String.valueOf(true));
		headers.put(HttpHeaders.X_WATSON_TEST, String.valueOf(true));
		return headers;
	}

	/**
	 * 
	 */
	private void _init()
	{
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		service.setApiKey(API_KEY);
		service.setDefaultHeaders(getDefaultHeaders());
		
		_refresh();
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void _refresh()
	{
		List<Collection> list = service.getCollections().execute();
		
		cbCollection.removeAllItems();
		
		for(Collection c : list)
		{
			COLLECTION col = new COLLECTION();
			col.collectionId = c.getId();
			
			if ( c.getStatus() == Status.UNAVAILABLE )
				col.collectionName = c.getName() + " [" + col.collectionId + "] *";
			else
				col.collectionName = c.getName() + " [" + col.collectionId + "]";
			
			cbCollection.addItem(col);
		}
	}
	
	/**
	 * 
	 */
	private void _createCollection()
	{
		String name = ((JTextComponent)cbCollection.getEditor().getEditorComponent()).getText();
		
		if ( name != null && name.length() > 0 )
		{
			service.createCollection(name).execute();
			_refresh();
		}
	}
	
	/**
	 * 
	 */
	private void _deleteCollection()
	{
		Object o = cbCollection.getSelectedItem();
		if ( o != null )
		{
			COLLECTION c = (COLLECTION)o;
			service.deleteCollection(c.collectionId).execute();
			cbCollection.removeItem(o);
			
			_refresh();
		}
	}
	
	/**
	 * 
	 */
	private void _open()
	{
        JFileChooser jfilechooser = new JFileChooser();
        jfilechooser.setDialogTitle("이미지 파일이 저장된 폴더를 선택하세요");
        jfilechooser.setFileSelectionMode(1);
        jfilechooser.setAcceptAllFileFilterUsed(false);
        if(jfilechooser.showOpenDialog(this) == 0) {
            txtPath.setText(jfilechooser.getSelectedFile().toString());
        }
	}
	
	/**
	 * @param fileName
	 * @return
	 */
	private Map<String, META> _readMeta(String fileName)
	{
		Map<String, META> meta = new HashMap<String, META>();
		
		BufferedReader br = null;
		
		try
		{
			br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			do
			{
				line = br.readLine();
				
				if ( line != null )
				{
					// CATEGORY,IMAGE_FILE,PRICE,BRAND
					String [] info = line.split(",");
					
					META m = new META();
					
					m.category = info[0];
					m.ImageFile = info[1];
					m.price = info[2];
					m.brand = info[3];
					
					meta.put(info[1], m);
				}
			}while(line != null);
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally{
			try{br.close();}catch(Exception x){}
		}
		
		return meta;
	}
	
	/**
	 * @param path
	 */
	private void _addImages(String path, String colId)
	{
		cmdAdd.setEnabled(false);
		
		/*
		 * META 정보
		 */
		Map<String, META> meta = _readMeta(path + File.separator + META_FILE);
		
		File root = new File(path);
		
		File [] list = root.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toUpperCase().endsWith(".JPG");
			}
		});
		
		progressBar.setValue(0);
		progressBar.setMaximum(list.length);
		
		for(int i = 0; i < list.length; i++)
		{
			String filename = list[i].getName();
			
			System.out.println(filename);
			
			try
			{
				AddImageToCollectionOptions options = new AddImageToCollectionOptions.Builder()
				          .collectionId(colId)
				          .images(list[i])
				          .metadata(CATEGORY, meta.get(filename).category)
				          .metadata(IMAGE_FILE, meta.get(filename).price)
				          .metadata(PRICE, meta.get(filename).price)
				          .metadata(BRAND, meta.get(filename).brand)		          
				          .build();
				
				service.addImageToCollection(options).execute();
				
				Thread.sleep(3000);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			progressBar.setValue(i+1);
		}
		
		cmdAdd.setEnabled(true);
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}catch(Exception ex) {}

		CollectionTool main = new CollectionTool();
		main.pack();
		main.show();	
	}
}
