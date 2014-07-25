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
		new GeneticVariationProvider(registry,desktop);
		
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
	
	public JComponent getInformation(Xref xref) {

		if(desktop.getSwingEngine().getCurrentOrganism() == null)
			return(new JLabel ("Organism not set for active pathway."));

		Xref mapped = idMapper(xref);
		if(mapped.getId().equals("")){
			return(new JLabel ("This identifier cannot be mapped to Ensembl."));
		}
		if(BiomartQueryService.isInternetReachable()) {

			Map<String,String> attr_map;
			System.err.println("Internet is ok");

			String organism = Utils.mapOrganism(desktop.getSwingEngine().getCurrentOrganism().toString());
			if(organism != null) {
				AttributesImporter ai = new AttributesImporter(organism);
				attr_map = ai.getAttributes();
				sd = new SettingsDialog(this,attr_map);
	
				resultPanel = new JPanel();
				System.err.println(attr_map.size());
				System.err.println(attr_map);
				
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
				
				Document result = BiomartQueryService.createQuery(organism, attrs, identifierFilters);
				
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
				
			} else {
				return(new JLabel ("This organism is not supported by Ensembl Biomart."));
			}
		} else {
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
