package org.pathvisio.biomartconnect.impl;
import java.awt.BorderLayout;
import java.awt.Component;
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
		
/*		

		ensembl_gene_id.setSelected(true);
		external_gene_id.setSelected(true);		
		description.setSelected(true);
		chromosome_name.setSelected(true);
		start_position.setSelected(true);
		

		jp.add(ensembl_gene_id);
		jp.add(external_gene_id);
		jp.add(description);
		jp.add(chromosome_name);
		jp.add(start_position);
		jp.add(end_position);
		jp.add(strand);
		jp.add(band);
		jp.add(transcript_count);
		jp.add(percentage_gc_content);
		jp.add(status);

	*/	

/*		JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){
				bcp.sendResult();
			}
        });
  */      
	/*	JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 1;
		con.gridy = 0;
		con.gridwidth = 3;
        southPanel.add(applyButton,con);
		*/

		//southPanel.add(applyButton,BorderLayout.CENTER);
		//add(southPanel,BorderLayout.SOUTH);

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
