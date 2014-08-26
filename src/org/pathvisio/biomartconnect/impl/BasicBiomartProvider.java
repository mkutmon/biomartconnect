package org.pathvisio.biomartconnect.impl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.inforegistry.IInfoProvider;
import org.w3c.dom.Document;

/**
 * 
 * This provider queries ensembl database for basic features of the selected gene node
 * and displays them in the tabular form in the info panel. The features supported are
 * extensive since they are loaded dynamically from the server at run time. It also provides
 * a settings button to select the features to be shown in the panel.
 *   
 * @author mkutmon
 * @author Sravanthi Sinha
 * @author Rohan Saxena
 */

public class BasicBiomartProvider implements IInfoProvider {

	private PvDesktop desktop;
	private SettingsDialog settingsDlg;
	private JPanel resultPanel;
	private String s; //To contain results from the biomart
	
	public BasicBiomartProvider(PvDesktop desktop) {
		this.desktop = desktop;
	}
	
	
	
	/**
	 * Implementing the required function of IInfoProvider interface.
	 * Gives the name to be shown in the inforegistry plugin.
	 */
	@Override
	public String getName() {
		return("BiomartConnect Basic");
	}
	
	

	/**
	 * Implementing the required function of IInfoProvider interface.
	 * Tells inforegistry that it works only for gene products.
	 */
	@Override
	public Set<DataNodeType> getDatanodeTypes() {
		Set<DataNodeType> s = new HashSet<DataNodeType>();
		s.add(DataNodeType.GENEPRODUCT);
		return s;
	}
	
	

	/**
	 * Implementing the required function of IInfoProvider interface.
	 * Queries ensembl biomart to find basic information regarding the selected data node.
	 * 
	 * @param xref - to provide id and data source of the selected data node
	 * @return JComponent containing the features in the tabular form to be displayed 
	 * in the info panel. 
	 */
	@Override
	public JComponent getInformation(Xref xref) {
		
		//Makes sure organism for the selected gene product is know
		if(desktop.getSwingEngine().getCurrentOrganism() == null)
			return(new JLabel ("Organism not set for active pathway."));

		//Queries Utils.mapId to find any corresponding ensembl gene id
		Xref mapped = Utils.mapId(xref,desktop);
		if(mapped.getId().equals("")){
			return(new JLabel ("This identifier cannot be mapped to Ensembl."));
		}
		
		//Checks is the internet connection working before proceeding forward
		if(BiomartQueryService.isInternetReachable()) {

			//Holds attributes for the organism of the gene product selected
			Map<String,String> attr_map;
			
			String organism = Utils.mapOrganism(desktop.getSwingEngine().getCurrentOrganism().toString());
			
			if(organism != null) {

				//Importing attributes for the selected gene product
				AttributesImporter ai = new AttributesImporter(organism,"__gene__main");
				attr_map = ai.getAttributes();
				
				//Creating settings dialog panel to display all the attributes
				settingsDlg = new SettingsDialog(this, attr_map);
				
				//Creating result panel to contain the information in tabular form and display it in info panel
				resultPanel = new JPanel();
				
				Collection<String> attrs = new HashSet<String>();
				Iterator<String> it = attr_map.keySet().iterator();
				while(it.hasNext()){
					String temp = attr_map.get(it.next());
					attrs.add(temp);
				}
				Collection<String> identifierFilters = new HashSet<String>();
				identifierFilters.add(mapped.getId().toString());
				
				//Querying Biomart
				Document result = BiomartQueryService.createQuery(organism, attrs, identifierFilters,"TSV");
				
				//Creating input stream of the results obtained from the biomart
				InputStream is = BiomartQueryService.getDataStream(result);

				//Converting InputStream in to a string	
				s = Utils.getStringFromInputStream(is);
				
				if(s.equals("Invalid")){
					return new JLabel ("No information returned.");
				}
				else{
					sendResult();
					return resultPanel;
				}
				
			}
			else{
				return(new JLabel ("This organism is not supported by Ensembl Biomart."));
			}
		}
		else{
			System.err.println("Internet not working");
			JLabel jl = new JLabel ("Error: Cannot connect to the internet.");
			jl.setHorizontalAlignment(JLabel.RIGHT);
			return jl;			
		}
	}
	

	
	/**
	 * Updates information shown in info panel but updating elements attached to the result panel
	 */
	public void sendResult() {
		
		//Emptying info panel
		resultPanel.removeAll();
		resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.Y_AXIS));
		
		//Adding table containing features of the gene product
		resultPanel.add(Utils.arrayToTable(dialogToArray(csvReader(s))));
		
		JButton settingsButton = new JButton("Settings");
		settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Adding attribute setting button
		resultPanel.add(settingsButton);
		
		settingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				settingsDlg.setVisible(true);
			}
	    });
		
		resultPanel.revalidate();
		resultPanel.repaint();
	}
	
	
	
	/**
	 * Helper function to convert features and values from csv format to double array format
	 * 
	 * @param s - csv in the form of string
	 * @return - converted double array
	 */
	private String[][] csvReader(String s) {
		String[] lines = s.split("\n");
		String[] keys = lines[0].split("\t");
		String[] values = lines[1].split("\t");
		String[][] attr = {keys,values};
		return(attr);
	}
	
	
	
	/**
	 * Helper function to filter out unselected features in the setting dialog from 
	 * being displayed in the info panel
	 * 
	 * @param m - Array of all the features and their values
	 * @return - Array containing only selected features and their values
	 */
	private String[][] dialogToArray(String[][] m){
		
		ArrayList<String> options = settingsDlg.selectedOptions();
		ArrayList<String> temp_options = new ArrayList<String>();
		ArrayList<String> val = new ArrayList<String>();
					
		for(int i=0; i < m[0].length; i++ ){
			
			if(options.contains(m[0][i])){
				temp_options.add(m[0][i]);
				val.add(m[1][i]);
			}
		}		
		String[][] temp = {temp_options.toArray(new String[temp_options.size()]),val.toArray(new String[val.size()])};
		return (temp);
	}
}
