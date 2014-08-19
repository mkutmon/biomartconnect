package org.pathvisio.biomartconnect.impl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bridgedb.Xref;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.inforegistry.IInfoProvider;
import org.w3c.dom.Document;

/**
 * BasicBiomartProvider allows users to load basic attributes
 * of a gene product like name, identifier, %GC, ...
 * 
 * @author mkutmon
 * @author rohansaxena
 *
 */
public class BasicBiomartProvider implements IInfoProvider {

	private PvDesktop desktop;
	private SettingsDialog settingsDlg;
	private JPanel resultPanel;
	private String inputStream;
	
	public BasicBiomartProvider(PvDesktop desktop) {
		this.desktop = desktop;
	}
	
	@Override
	public String getName() {
		return("BiomartConnect Basic");
	}

	@Override
	public Set<DataNodeType> getDatanodeTypes() {
		Set<DataNodeType> s = new HashSet<DataNodeType>();
		s.add(DataNodeType.GENEPRODUCT);
		s.add(DataNodeType.PROTEIN);
		return s;
	}

	@Override
	public JComponent getInformation(Xref xref) {
		// check if the computer is connected to the internet
		if(BiomartQueryService.isInternetReachable()) {
			// check if pathway is annotated with an organism
			if(desktop.getSwingEngine().getCurrentOrganism() == null) {
				return(new JLabel ("Organism not set for active pathway."));
			}
			// check if the organism is supported by biomart
			String organism = Utils.mapOrganism(desktop.getSwingEngine().getCurrentOrganism().toString());
			if(organism == null) {
				return(new JLabel("Organism is not supported by Ensembl Biomart."));
			}
			
			// map identifier to ensembl	
			Xref mapped = Utils.mapId(xref,desktop);
			if(mapped.getId().equals("")){
				return(new JLabel("<html>This identifier cannot be mapped to Ensembl.<br/>Check if the correct identifier mapping database is loaded.</html>"));
			}
		
			// get attributes from biomart
			Map<String,String> attr_map;
			AttributesImporter ai = new AttributesImporter(organism,"__gene__main");
			attr_map = ai.getAttributes();
			
			settingsDlg = new SettingsDialog(this, attr_map);
			resultPanel = new JPanel();
				
			Collection<String> attrs = new HashSet<String>();

			Iterator<String> it = attr_map.keySet().iterator();
			while(it.hasNext()){
				String temp = attr_map.get(it.next());
				attrs.add(temp);
			}

			Collection<String> identifierFilters = new HashSet<String>();
			identifierFilters.add(mapped.getId().toString());
				
			Document result = BiomartQueryService.createQuery(organism, attrs, identifierFilters,"TSV");
			
			InputStream is = BiomartQueryService.getDataStream(result);

			inputStream = Utils.getStringFromInputStream(is);
			if(inputStream.equals("Invalid")) {
				return new JLabel ("No information returned.");
			} else {
				updateResultPanel();
				return resultPanel;
			}
		} else {
			System.err.println("Internet not working");
			JLabel jl = new JLabel ("Error: Cannot connect to the internet.");
			return jl;			
		}
	}
	

	
	
	public void updateResultPanel() {
		resultPanel.removeAll();
		resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.Y_AXIS));	
		resultPanel.add(Utils.arrayToTable(dialogToArray(csvReader(inputStream))));
		JButton settingsButton = new JButton("Settings");
		settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		resultPanel.add(settingsButton);
		
		settingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				settingsDlg.setVisible(true);
			}
	    });
		
		resultPanel.revalidate();
		resultPanel.repaint();
	}
	
	private String[][] csvReader(String s) {
		System.out.println(s);
		String[] lines = s.split("\n");
		String[] keys = lines[0].split("\t");
		String[] values = lines[1].split("\t");
		String[][] attr = {keys,values};
		return(attr);
	}
	
	private String[][] dialogToArray(String[][] m){
		ArrayList<String> options = settingsDlg.selectedOptions();
		ArrayList<String> temp_options = new ArrayList<String>();
		ArrayList<String> val = new ArrayList<String>();
					
		for(int i = 0; i < m[0].length; i++) {
			if(options.contains(m[0][i])){
				temp_options.add(m[0][i]);
				val.add(m[1][i]);
			}
		}
				
		String[][] temp = {temp_options.toArray(new String[temp_options.size()]),val.toArray(new String[val.size()])};
		
		return (temp);
	}
}
