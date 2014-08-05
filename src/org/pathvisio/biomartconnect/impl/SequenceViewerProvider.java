package org.pathvisio.biomartconnect.impl;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.bridgedb.Xref;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.inforegistry.IInfoProvider;
import org.w3c.dom.Document;

public class SequenceViewerProvider implements IInfoProvider {
	
	private PvDesktop desktop;
	
	public SequenceViewerProvider(PvDesktop desktop){
		this.desktop = desktop;
	}
	
	@Override
	public String getName() {
		return("Sequence Viewer");
	}

	@Override
	public Set<DataNodeType> getDatanodeTypes() {
		Set<DataNodeType> s = new HashSet<DataNodeType>();
		s.add(DataNodeType.GENEPRODUCT);
		return s;
	}

	@Override
	public JComponent getInformation(Xref xref) {

		if(desktop.getSwingEngine().getCurrentOrganism() == null)
			return(new JLabel ("Organism not set for active pathway."));

		Xref mapped = Utils.mapId(xref, desktop);
		System.out.println(mapped);
		if(mapped.getId().equals("")){
			return(new JLabel ("This identifier cannot be mapped to Ensembl."));
		}
		if(BiomartQueryService.isInternetReachable()) {

			Map<String,String> attr_map;
			System.err.println("Internet is ok");

			String organism = Utils.mapOrganism(desktop.getSwingEngine().getCurrentOrganism().toString());
			if(organism != null) {
				
				Collection<String> attrs = new HashSet<String>();
				attrs.add("ensembl_gene_id");
				attrs.add("ensembl_transcript_id");
				attrs.add("coding");
				
				Collection<String> identifierFilters = new HashSet<String>();
				identifierFilters.add(mapped.getId().toString());
				Document result = BiomartQueryService.createQuery(organism, attrs, identifierFilters,"FASTA");
				InputStream is = BiomartQueryService.getDataStream(result);
				//String s = Utils.getStringFromInputStream(is);
				//System.err.println(s);
				//is = BiomartQueryService.getDataStream(result);
				SequenceContainer sc = new SequenceContainer();
				sc.fastaParser(is,mapped.getId().toString(),false);


				attrs.remove("coding");
				attrs.add("gene_exon");
				result = BiomartQueryService.createQuery(organism, attrs, identifierFilters,"FASTA");
				is = BiomartQueryService.getDataStream(result);
				String s = Utils.getStringFromInputStream(is);
				System.err.println(s);				
				sc.fastaParser(is,mapped.getId().toString(),true);
				
				System.err.println("*****************");
				sc.print();
				
		
		//return (new JLabel(s));
				return null;
		
		
	}
		}
		return null;
	}

}