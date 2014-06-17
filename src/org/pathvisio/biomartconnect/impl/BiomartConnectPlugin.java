package org.pathvisio.biomartconnect.impl;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.w3c.dom.Document;

import javax.swing.JTabbedPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.pathvisio.core.ApplicationEvent;
import org.pathvisio.core.Engine.ApplicationEventListener;
import org.pathvisio.core.data.GdbManager;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.core.view.Graphics;
import org.pathvisio.core.view.VPathway;
import org.pathvisio.core.view.SelectionBox.SelectionEvent;
import org.pathvisio.core.view.SelectionBox.SelectionListener;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.inforegistry.IInfoProvider;
import org.pathvisio.inforegistry.InfoRegistry;


public class BiomartConnectPlugin extends JPanel implements  SelectionListener, ApplicationEventListener, Plugin, IInfoProvider {
	

	private InfoRegistry registry;
	private PvDesktop desktop;
	private JPanel sidePanel;
	//private JPanel sidePanel1;

	
	@Override
	public void done() {
		desktop.getSideBarTabbedPane().remove(sidePanel);
		// TODO Auto-generated method stub
	}

	
	
	@Override
	public void init(PvDesktop desktop) {
		
		this.desktop = desktop;
		IInfoProvider i = new BiomartConnectPlugin();
		
		registry = InfoRegistry.getInfoRegistry();
		registry.registerInfoProvider(i);
		
		
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
	//	System.err.println("This is organism " + xref.getDataSource().getOrganism());
	//	System.err.println(xref.getDataSource());
		
/*		IDMapperStack stack = desktop.getSwingEngine().getGdbManager().getCurrentGdb();
		try {
			Set<Xref> resultSet = stack.mapID(xref, DataSource.getBySystemCode("En"));
			Iterator<Xref> x = resultSet.iterator();
			while(x.hasNext()){
				Xref temp = x.next();
				System.err.println(temp.getId());
				System.err.println(temp.getDataSource());				
			}
		} catch (IDMapperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
		
		
		if(idMapper(xref).getId().toString().isEmpty()){
			return(new JLabel ("This identifier cannot be mapped to Ensembl."));
		}
		if(BiomartQueryService.isInternetReachable())
		{
			System.err.println("Internet is ok");
			
			String set = "hsapiens_gene_ensembl";
			
			Collection<String> attrs = new HashSet<String>();
			attrs.add("ensembl_gene_id");
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
			
			Collection<String> identifierFilters = new HashSet<String>();
			identifierFilters.add(xref.getId().toString());
			
			Document result = BiomartQueryService.createQuery(set, attrs, identifierFilters);
			
			InputStream is = BiomartQueryService.getDataStream(result);
			String s = getStringFromInputStream(is);
			if(s.equals("Invalid")){
				return new JLabel ("No information returned.");
			}
			else{			
			return arrayToTable(csvReader(s));
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

	public void applicationEvent(ApplicationEvent e)
	{
		switch(e.getType())
		{
		case VPATHWAY_CREATED:
			((VPathway)e.getSource()).addSelectionListener(this);
			break;
		case VPATHWAY_DISPOSED:
			((VPathway)e.getSource()).removeSelectionListener(this);
			break;
		}
	}

	private static String getStringFromInputStream(InputStream is) {
		 
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
	
	private static String[][] csvReader(String s){
		
		String[] lines = s.split("\n");
		
		String[] keys = lines[0].split("\t");
		String[] values = lines[1].split("\t");
		
		String[][] attr = {keys,values};

		return(attr);
	}
	
	private static JScrollPane arrayToTable(final String[][] m){
		
		TableModel dataModel = new AbstractTableModel() {
	          public int getColumnCount() { return 2; }
	          public int getRowCount() { return 11;}
	          public Object getValueAt(int row, int col) { 
	        	  return m[col][row]; 
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
		else

		{

			mapper = desktop.getSwingEngine().getGdbManager().getCurrentGdb();

			try {
				Set<Xref> result = mapper.mapID(xref, DataSource.getBySystemCode("En"));
				if(result.isEmpty())
					return (new Xref(null,null));
				else
					return (result.iterator().next());
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (new Xref(null,null));
			}
			
		}
	}
	
	}
 
