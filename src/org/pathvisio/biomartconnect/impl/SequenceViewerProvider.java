package org.pathvisio.biomartconnect.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.MutableComboBoxModel;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import javax.swing.text.JTextComponent;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

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
		return("Biomart Sequence Viewer");
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
			return(new JLabel("<html>This identifier cannot be mapped to Ensembl.<br/>Check if the correct identifier mapping database is loaded.</html>"));
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
				final SequenceContainer sc = new SequenceContainer();
				sc.fastaParser(is,mapped.getId().toString(),false);

				attrs.remove("coding");
				attrs.add("gene_exon");
				result = BiomartQueryService.createQuery(organism, attrs, identifierFilters,"FASTA");
				is = BiomartQueryService.getDataStream(result);
				//String s = Utils.getStringFromInputStream(is);
				//System.err.println(s);				
				sc.fastaParser(is,mapped.getId().toString(),true);
				
				System.err.println("*****************");
				sc.print();
				
		
		//return (new JLabel(s));
				final JComboBox transcriptIdList = new JComboBox();
				MutableComboBoxModel model = (MutableComboBoxModel)transcriptIdList.getModel();
				JPanel jp = new JPanel();


				//JPanel noWrapPanel = new JPanel( new BorderLayout() );
				//noWrapPanel.add( jta );
				//JScrollPane jsp = new JScrollPane( jta );
				//jsp.setViewportView(noWrapPanel);
				//noWrapPanel.add(jta);
				

				final JTextArea jta = new JTextArea();
				jta.setLineWrap(true);

				JScrollPane jsp = new JScrollPane(jta,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				jta.setEditable(false);
				
				for(InfoPerTranscriptId obj: sc.transcriptIdList){
				model.addElement(obj.getTranscriptId());
				}
				
				transcriptIdList.addActionListener(
						new ActionListener(){
							public void actionPerformed(ActionEvent e){
								System.err.println("Triggering");
		                        JComboBox temp_combo = (JComboBox)e.getSource();
		                        String currentQuantity = (String)temp_combo.getSelectedItem();
		                        System.err.println(currentQuantity);
		                        jta.setText(sc.find(currentQuantity).getSequence());
		                        //System.err.println();
							}
						});
				
				JToggleButton mark_exon = new JToggleButton("Mark Exons");
				
				mark_exon.addActionListener(
						new ActionListener(){
							String replacedStr = null;
								public void actionPerformed(ActionEvent e){
								if( ((JToggleButton)e.getSource()).isSelected()){
									replacedStr = sc.find(transcriptIdList.getSelectedItem().toString()).getSequence();
									
									jta.setText(replacedStr);
									Highlighter highlighter = jta.getHighlighter();
								      HighlightPainter painter = 
								             new DefaultHighlighter.DefaultHighlightPainter(Color.PINK);
									for(String temp_exon: sc.find(transcriptIdList.getSelectedItem().toString()).getExon()){
									if(sc.find(transcriptIdList.getSelectedItem().toString()).getSequence().contains(temp_exon)){

										
										 int p0 = replacedStr.indexOf(temp_exon);										
									      int p1 = p0 + temp_exon.length();
									      
									      try {
											highlighter.addHighlight(p0, p1, painter );
										} catch (BadLocationException e1) {
											
											e1.printStackTrace();
										}
									}}}}});

				
				jp.setLayout(new BorderLayout());
				jp.add(transcriptIdList, BorderLayout.NORTH);
				jp.add(jsp,BorderLayout.CENTER);
				jp.add(mark_exon,BorderLayout.SOUTH);
				
				
				
				
				return jp;
		
		
	}
		}
		return null;
	}
	


}