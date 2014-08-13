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

import javafx.scene.control.ToggleButton;

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
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
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
				//String s = Utils.getStringFromInputStream(is);
				//System.err.println(s);				
				sc.fastaParser(is,mapped.getId().toString(),true);
				
				System.err.println("*****************");
				sc.print();
				
		
		//return (new JLabel(s));
				JComboBox<String> transcriptIdList = new JComboBox<String>();
				MutableComboBoxModel<String> model = (MutableComboBoxModel<String>)transcriptIdList.getModel();
				JPanel jp = new JPanel();

				JTextPane jta = new JTextPane();
				jta.setEditorKit(new WrapEditorKit());
				//JPanel noWrapPanel = new JPanel( new BorderLayout() );
				//noWrapPanel.add( jta );
				//JScrollPane jsp = new JScrollPane( jta );
				//jsp.setViewportView(noWrapPanel);
				//noWrapPanel.add(jta);
				
				//JTextArea jta = new JTextArea();
				//JTextPane jta = new JTextPane()
				/*{
				    public boolean getScrollableTracksViewportWidth()
				    {
				        return getUI().getPreferredSize(this).width 
				            <= getParent().getSize().width;
				    }
				};
				*/
				//jta.setLineWrap(true);
				JScrollPane jsp = new JScrollPane(jta,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				jta.setEditable(false);
				jta.setContentType("text/html");
				
				for(InfoPerTranscriptId obj: sc.transcriptIdList){
				model.addElement(obj.getTranscriptId());
				}
				
				transcriptIdList.addActionListener(
						new ActionListener(){
							public void actionPerformed(ActionEvent e){
								System.err.println("Triggering");
		                        JComboBox<String> temp_combo = (JComboBox<String>)e.getSource();
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
									for(String temp_exon: sc.find(transcriptIdList.getSelectedItem().toString()).getExon()){
									if(sc.find(transcriptIdList.getSelectedItem().toString()).getSequence().contains(temp_exon)){
										replacedStr = replacedStr.replaceAll(temp_exon, "<font color=red>" + temp_exon + "</font>" );
									}
									}
									
									jta.setText(replacedStr);
									
								}
							}	
						});
				
				jp.setLayout(new BorderLayout());
				jp.add(transcriptIdList, BorderLayout.NORTH);
				jp.add(jsp,BorderLayout.CENTER);
				jp.add(mark_exon,BorderLayout.SOUTH);
				
				
				
				
				return jp;
		
		
	}
		}
		return null;
	}
	
    class WrapEditorKit extends StyledEditorKit {
        ViewFactory defaultFactory=new WrapColumnFactory();
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }

    }

    class WrapColumnFactory implements ViewFactory {
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new WrapLabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new ParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }

            // default to text display
            return new LabelView(elem);
        }
    }

    class WrapLabelView extends LabelView {
        public WrapLabelView(Element elem) {
            super(elem);
        }

        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }

    }


}