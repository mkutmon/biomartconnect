package org.pathvisio.biomartconnect.impl;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class TableDialog extends JDialog {

	JScrollPane jt;
	/*

	JCheckBox ensembl_gene_id;
	JCheckBox external_gene_id;
	JCheckBox description;
	JCheckBox chromosome_name;
	JCheckBox start_position;
	JCheckBox end_position;
	JCheckBox strand;
	JCheckBox band;
	JCheckBox transcript_count;
	JCheckBox percentage_gc_content;
	JCheckBox status;

	*/
	GeneticVariationProvider bcp;
	String [] attr;
	JPanel jp;
		
	public TableDialog(GeneticVariationProvider bcp, JScrollPane jt, String [] attr){
/*		

		ensembl_gene_id = new JCheckBox("Ensembl Gene ID");
		external_gene_id = new JCheckBox("External Gene ID");
		description = new JCheckBox("Description");
		chromosome_name = new JCheckBox("Chromosome Name");
		start_position = new JCheckBox("Start Position");
		end_position = new JCheckBox("End Position");
		strand = new JCheckBox("Strand");
		band = new JCheckBox("Band");
		transcript_count = new JCheckBox("Transcript Count");
		percentage_gc_content = new JCheckBox("%GC Content");
		status = new JCheckBox("Status");

		*/
		this.bcp = bcp;
		this.jt = jt;
		this.attr = attr;

		
		initUI();
	}
	
	public final void initUI(){

		JLabel title = new JLabel("Variation Data Table");
		JPanel master = new JPanel();
		master.setLayout(new BorderLayout());
		

		//JPanel north_panel = new JPanel();
		//north_panel.setLayout(new FlowLayout());
		//jtf = new JTextField("Search");
		
		
		//JButton filter = new JButton("Filter");
		
		//filter.addActionListener(new ActionListener(){

			//public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				//sendResult(jp,notSelectedOptions());
				


				
			//}
		//}
		//);
		
		//north_panel.add(jtf);
		//north_panel.add(filter);
		
		master.add(title,BorderLayout.NORTH);
		
		JScrollPane scrollpane = new JScrollPane(jt,  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		master.add(scrollpane,BorderLayout.CENTER);
        
        final TableSettingsDialog tsd = new TableSettingsDialog(bcp,attr,master,scrollpane);
        JButton settings = new JButton("Settings");
        settings.setAlignmentX(Component.CENTER_ALIGNMENT);
        
		settings.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent e){

			tsd.setVisible(true);
			
		}
		
    });
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));
        southPanel.add(settings);
        master.add(southPanel,BorderLayout.SOUTH);
        
        add(master);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setSize(900,660);
        
	}
	
}
