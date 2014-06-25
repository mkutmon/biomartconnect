	package org.pathvisio.biomartconnect.impl;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class SettingsDialog extends JDialog {
	
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
	
	public SettingsDialog(){
		
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
		
		initUI();
	}
	
	public final void initUI(){

		JLabel title = new JLabel("Choose attributes...");
		setLayout(new BorderLayout());
		
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(4,3));
		
		
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
		
		JButton applyButton = new JButton("Apply");		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 1;
		con.gridy = 0;
		con.gridwidth = 3;
        southPanel.add(applyButton,con);
		

		//southPanel.add(applyButton,BorderLayout.CENTER);
		add(southPanel,BorderLayout.SOUTH);
		add(jp,BorderLayout.CENTER);
		add(title,BorderLayout.NORTH);
        setTitle("BiomartConnect");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(450, 330);

	}
	
	public Object[] selectedOptions() {
		
		ArrayList<String> temp = new ArrayList<String>();
		int count = 0;
		
		if(ensembl_gene_id.isSelected()){
			temp.add("ensembl_gene_id");
		}
		if(external_gene_id.isSelected()){
			temp.add("external_gene_id");
		}
		if(description.isSelected()){
			temp.add("description");
		}
		if(chromosome_name.isSelected()){
			temp.add("chromosome_name");
		}
		if(start_position.isSelected()){
			temp.add("start_position");
		}
		if(end_position.isSelected()){
			temp.add("end_position");
		}
		if(strand.isSelected()){
			temp.add("strand");
		}
		if(band.isSelected()){
			temp.add("band");
		}
		if(transcript_count.isSelected()){
			temp.add("transcript_count");
		}
		if(percentage_gc_content.isSelected()){
			temp.add("percentage_gc_content");
		}
		if(status.isSelected()){
			temp.add("status");
		}
		
		return temp.toArray();
	}

}
