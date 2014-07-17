package org.pathvisio.biomartconnect.impl;



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import java.util.Map;

import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.pathvisio.core.ApplicationEvent;
import org.pathvisio.core.Engine.ApplicationEventListener;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.core.model.PathwayElementEvent;
import org.pathvisio.core.model.PathwayElementListener;
import org.pathvisio.core.view.SelectionBox.SelectionEvent;
import org.pathvisio.core.view.SelectionBox.SelectionListener;
import org.pathvisio.core.view.VPathway;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.inforegistry.IInfoProvider;
import org.pathvisio.inforegistry.InfoRegistry;
import org.w3c.dom.Document;


import java.util.Iterator;


/**
 * 
 * @author rohansaxena
 * @author martina
 *
 */
public class BiomartConnectPlugin extends JPanel implements  SelectionListener, ApplicationEventListener, PathwayElementListener, Plugin, IInfoProvider {
	
	private InfoRegistry registry;
	private PvDesktop desktop;
	private JPanel sidePanel;

	private SettingsDialog sd;
	private JPanel resultPanel;
	private String s;

	
	@Override
	public void done() {
		desktop.getSideBarTabbedPane().remove(sidePanel);
		// TODO Auto-generated method stub
	}
	

	
	@Override
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		
		// registers plugin as information provider
		registry = InfoRegistry.getInfoRegistry();
		registry.registerInfoProvider(this);
		

		//sd = new SettingsDialog(this,attr_map);
		//resultPanel = new JPanel();		
		new GeneticVariationProvider(registry,desktop,this);
		
		desktop.getSwingEngine().getEngine().addApplicationEventListener(this);
		VPathway vp = desktop.getSwingEngine().getEngine().getActiveVPathway();
		if(vp != null) vp.addSelectionListener(this);	
	}
	
	public String getName() {
		
		String name = "BiomartConnect";
		return(name);
	}
	
	public Set<DataNodeType> getDatanodeTypes(){
		
		Set<DataNodeType> s = new HashSet<DataNodeType>();
		s.add(DataNodeType.GENEPRODUCT );
		return s;
	}
	
	public JComponent getInformation(Xref xref){

		String set;

		if(desktop.getSwingEngine().getCurrentOrganism() == null)
			return(new JLabel ("Organism not set for active pathway."));

		Xref mapped = idMapper(xref);
		if(mapped.getId().equals("")){
			return(new JLabel ("This identifier cannot be mapped to Ensembl."));
		}
		if(BiomartQueryService.isInternetReachable())
		{

			Map<String,String> attr_map;
			System.err.println("Internet is ok");

			if(datasetMapper() != null){
			set = datasetMapper();
			AttributesImporter ai = new AttributesImporter(set);
			attr_map = ai.getAttributes();
			sd = new SettingsDialog(this,attr_map);

			resultPanel = new JPanel();
			System.err.println(attr_map.size());
			System.err.println(attr_map);
			}
			else{
				return(new JLabel ("This organism is not supported by Ensembl."));
			}
			
			//TODO: move this to biomart basic class as properties!
			Collection<String> attrs = new HashSet<String>();

			Iterator<String> it = attr_map.keySet().iterator();
			while(it.hasNext()){
				String temp = attr_map.get(it.next());
				attrs.add(temp);
			}
	/*		attrs.add("ensembl_gene_id");

			attrs.add("external_gene_id");
			attrs.add("description");
			attrs.add("chromosome_name");
			attrs.add("start_position");
			attrs.add("end_position");
			attrs.add("strand");
			attrs.add("band");
			attrs.add("transcript_count");
			attrs.add("percentage_gc_content");
			attrs.add("status");
		*/	

			Collection<String> identifierFilters = new HashSet<String>();
			identifierFilters.add(mapped.getId().toString());
			
			Document result = BiomartQueryService.createQuery(set, attrs, identifierFilters);
			
			InputStream is = BiomartQueryService.getDataStream(result);
			s = getStringFromInputStream(is);
			if(s.equals("Invalid")){
				return new JLabel ("No information returned.");
			}
			else{
			//return arrayToTable(csvReader(s));

				sendResult();
				
			
			return resultPanel;
			
			}
		}
		else{
		
			System.err.println("Internet not working");
			JLabel jl = new JLabel ("Error: Cannot connect to the internet.");
			jl.setHorizontalAlignment(JLabel.RIGHT);
			return jl;			
		}
	}
	
	public void selectionEvent(SelectionEvent e) {
		switch(e.type) {
		case SelectionEvent.OBJECT_ADDED:
			break;
		case SelectionEvent.OBJECT_REMOVED:
			break;
		case SelectionEvent.SELECTION_CLEARED:
			break;
		}
	}

	public void applicationEvent(ApplicationEvent e) {
		switch (e.getType()) {
		case VPATHWAY_CREATED:
			((VPathway) e.getSource()).addSelectionListener(this);
			break;
		case VPATHWAY_DISPOSED:
			((VPathway) e.getSource()).removeSelectionListener(this);
			break;
		default:
			break;
		}
	}

	public String getStringFromInputStream(InputStream is) {
		 
		int count = 0;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
				count++;		
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		sb.deleteCharAt(sb.length()-1);
		if(count == 1){
			return("Invalid");
		}
		else{
		return sb.toString();
		}
	}
	
	private String[][] csvReader(String s) {
		System.err.println(s);
		String[] lines = s.split("\n");
		String[] keys = lines[0].split("\t");
		String[] values = lines[1].split("\t");
		String[][] attr = {keys,values};
		return(attr);
	}
	
	public JScrollPane arrayToTable(final String[][] m) {
		
		TableModel dataModel = new AbstractTableModel() {
			 String[] columnNames= {"Attribute","Value"};
	         
	          public int getRowCount() { return m[0].length;}
	          public Object getValueAt(int row, int col) { 
	        	  return m[col][row]; 
	          }
	          public String getColumnName(int column) {	        	 
				return columnNames[column];
	        	}
	          public int getColumnCount(){
	              return columnNames.length;
	          }
	      };
	      
	      
	      JTable table = new JTable(dataModel);
	      JScrollPane scrollpane = new JScrollPane(table);
		return scrollpane;
	}
	
	private Xref idMapper(Xref xref){
		IDMapperStack mapper;
		
		if(xref.getDataSource().toString().equals("Ensembl")){
			return xref;			
		}
		else{
			mapper = desktop.getSwingEngine().getGdbManager().getCurrentGdb();
			Set<Xref> result;
			try {
				result = mapper.mapID(xref, DataSource.getBySystemCode("En"));
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (new Xref("",null));
			}
			if(result.isEmpty())
				return (new Xref("",null));
			else
				return (result.iterator().next());
		}
	}
	
	/**
	 * maps between BridgeDb species names
	 * and Ensembl BioMart species names
	 */
	private String datasetMapper(){
		switch (desktop.getSwingEngine().getCurrentOrganism().toString()) {
			case"AnophelesGambiae": return null;
			case"ArabidopsisThaliana": return null;
			case"Aspergillusniger": return null;
			case"BacillusSubtilis": return null;
			case"BosTaurus": return "btaurus_gene_ensembl";
			case"CaenorhabditisElegans": return "celegans_gene_ensembl";
			case"CanisFamiliaris": return "cfamiliaris_gene_ensembl";		
			case"CionaIntestinalis": return null;
			case"Clostridiumthermocellum": return null;
			case"DanioRerio": return "drerio_gene_ensembl";
			case"DasypusNovemcinctus": return "dnovemcinctus_gene_ensembl";
			case"DrosophilaMelanogaster": return "dmelanogaster_gene_ensembl";		
			case"EscherichiaColi": return null;
			case"EchinposTelfairi": return "etelfairi_gene_ensembl";
			case"EquusCaballus": return "ecaballus_gene_ensembl";	
			case"GallusGallus": return "ggallus_gene_ensembl";
			case"GlycineMax": return null;
			case"GibberellaZeae": return null;		
			case"HomoSapiens": return "hsapiens_gene_ensembl";
			case"LoxodontaAfricana": return "lafricana_gene_ensembl";
			case"MacacaMulatta": return "mmulatta_gene_ensembl";	
			case"MusMusculus": return "mmusculus_gene_ensembl";
			case"MonodelphisDomestica": return "mdomestica_gene_ensembl";
			case"MycobacteriumTuberculosis": return null;
			case"OrnithorhynchusAnatinus": return "oanatinus_gene_ensembl";
			case"OryzaSativa": return null;
			case"OryzaJaponica": return null;
			case"OryzaSativaJaponica": return null;
			case"OryziasLatipes": return "olatipes_gene_ensembl";
			case"OryctolagusCuniculus": return "ocuniculus_gene_ensembl";
			case"PanTroglodytes": return "ptroglodytes_gene_ensembl";
			case"SolanumLycopersicum": return null;
			case"SusScrofa": return "sscrofa_gene_ensembl";
			case"PopulusTrichocarpa": return null;
			case"RattusNorvegicus": return "rnorvegicus_gene_ensembl";
			case"SaccharomycesCerevisiae": return "scerevisiae_gene_ensembl";
			case"SorexAraneus": return "saraneus_gene_ensembl";	
			case"SorghumBicolor": return null;
			case"TetraodonNigroviridis": return "tnigroviridis_gene_ensembl";		
			case"TriticumAestivum": return null;
			case"XenopusTropicalis": return "xtropicalis_gene_ensembl";
			case"VitisVinifera": return null;
			case"ZeaMays": return null;
			default: return null;
		}
	}


	private String[][] dialogToArray(String[][] m){
		
		ArrayList<String> options = sd.selectedOptions();
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

		public void sendResult(){

		resultPanel.removeAll();
		resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.Y_AXIS));	
		resultPanel.add(arrayToTable(dialogToArray(csvReader(s))));
		//resultPanel.add(arrayToTable(csvReader(s)));
		JButton settingsButton = new JButton("Settings");
		settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		resultPanel.add(settingsButton);
		
		settingsButton.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent e){

			sd.setVisible(true);
			
		}
		
    });
		
		resultPanel.revalidate();
		resultPanel.repaint();


	}
	
	@Override
	public void gmmlObjectModified(PathwayElementEvent e) {

	}

}
