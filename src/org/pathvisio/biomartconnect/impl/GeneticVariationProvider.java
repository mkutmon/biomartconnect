package org.pathvisio.biomartconnect.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.pathvisio.inforegistry.IInfoProvider;
import org.pathvisio.inforegistry.InfoRegistry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.desktop.PvDesktop;
import org.w3c.dom.Document;

public class GeneticVariationProvider extends JPanel implements IInfoProvider{

	private PvDesktop desktop;
	private BiomartConnectPlugin bcp;
	private String s;
	private JComponent resultPanel;
	
	public GeneticVariationProvider(InfoRegistry registry, PvDesktop desktop, BiomartConnectPlugin bcp) {
		
		registry.registerInfoProvider(this);
		this.desktop = desktop;
		this.bcp = bcp;
		resultPanel = new JPanel();
	}
	
	public String getName(){
		String name = "Genetic Variation Provider";
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
				System.err.println("Internet is ok");
				if(datasetMapper() != null){
				set = datasetMapper();
				}
				else{
					return(new JLabel ("This organism is not supported by Ensembl."));
				}
				
				//TODO: move this to biomart basic class as properties!
				Collection<String> attrs = new HashSet<String>();
				attrs.add("ensembl_gene_id");
				attrs.add("ensembl_transcript_id");
				attrs.add("external_id");
				attrs.add("external_gene_id");
				attrs.add("allele");
				attrs.add("minor_allele_freq");
				attrs.add("transcript_location");
				attrs.add("chromosome_location");
				attrs.add("sift_prediction_2076");
				attrs.add("polyphen_score_2076");
				attrs.add("peptide_location");
				
				Collection<String> identifierFilters = new HashSet<String>();
				identifierFilters.add(mapped.getId().toString());
				
				Document result = BiomartQueryService.createQuery(set, attrs, identifierFilters);
				System.err.println("10");
				InputStream is = BiomartQueryService.getDataStream(result);
				System.err.println("20");
				ArrayList<String []> temp = matrixFromInputStream(is);
				System.err.println("30");
				String temp_ensembl_gene_id=null;
				String temp_associated_gene_name=null;
				int temp_num_of_snp=0;
				for(int i=0;i<temp.get(0).length;i++){
					if(temp.get(0)[i].equals("Ensembl Gene ID") ){
						temp_ensembl_gene_id = temp.get(1)[i];
					}
					else if (temp.get(0)[i].equals("Associated Gene Name") ){
						temp_associated_gene_name = temp.get(1)[i];
					}
						
				}
				temp_num_of_snp = temp.size()-1;
				System.err.println(temp.get(0)[0]);
				if(temp.size() == 1){
					return new JLabel ("No information returned.");
				}
				else{
				System.err.println("I am here");
				resultPanel.removeAll();
				resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.Y_AXIS));
				resultPanel.add(new JLabel("Ensembl Gene ID: " + temp_ensembl_gene_id));
				resultPanel.add(new JLabel("Associated Gene Name: " + temp_associated_gene_name));
				resultPanel.add(new JLabel("Number of SNPs: " + temp_num_of_snp));
				JButton show_table = new JButton("Show Table");
				//show_table.setAlignmentX(CENTER_ALIGNMENT);
				resultPanel.add(show_table);
				TableDialog td = new TableDialog(this,arrayToTable(temp));
				//resultPanel.add(arrayToTable(temp));
				show_table.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e){

						td.setVisible(true);
						
					}
					
			    });
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
		
		private ArrayList<String []> matrixFromInputStream(InputStream is){
			ArrayList<String []> al = new ArrayList<String []>(); 
			int count = 0;
			BufferedReader br = null;
			//StringBuilder sb = new StringBuilder();
			String line;
			
			try {
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					al.add(line.split("\t"));
					count++;		
					System.err.println(count);
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

			//sb.deleteCharAt(sb.length()-1);
			return al;
		}
		
		private JScrollPane arrayToTable(final ArrayList<String []> m ) {
			
			TableModel dataModel = new AbstractTableModel() {
				 String[] columnNames= m.get(0);
		          
		          public int getRowCount() { return (m.size()-1);}
		          public Object getValueAt(int row, int col) { 
		        	  return m.get(row+1)[col]; 
		          }
		          public String getColumnName(int column) {	        	 
					return columnNames[column];
		        	}
		          public int getColumnCount(){
		              return columnNames.length;
		          }
		      };
		      
		      TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(dataModel);
		      JTable table = new JTable(dataModel);
		      table.setRowSorter(sorter); 	
		      table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		     table.setAutoCreateRowSorter(true);
		      JScrollPane scrollpane = new JScrollPane(table,  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);		      
			return scrollpane;
		}
}