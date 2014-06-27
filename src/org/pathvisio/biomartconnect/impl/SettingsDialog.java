package org.pathvisio.biomartconnect.impl;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	BiomartConnectPlugin bcp;
	
	public SettingsDialog(BiomartConnectPlugin bcp){
		
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
		this.bcp = bcp;
		
		initUI();
	}
	
	public final void initUI(){

		JLabel title = new JLabel("Choose attributes:");
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
        applyButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){
				bcp.sendResult();
			}
        });
        
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
	
	public ArrayList<String> selectedOptions() {
		
		ArrayList<String> temp = new ArrayList<String>();
		
		if(ensembl_gene_id.isSelected()){
			temp.add("Ensembl Gene ID");
		}
		if(external_gene_id.isSelected()){
			temp.add("Ensembl Gene ID");
		}
		if(description.isSelected()){
			temp.add("Description");
		}
		if(chromosome_name.isSelected()){
			temp.add("Chromosome Name");
		}
		if(start_position.isSelected()){
			temp.add("Gene Start (bp)");
		}
		if(end_position.isSelected()){
			temp.add("Gene End (bp)");
		}
		if(strand.isSelected()){
			temp.add("Strand");
		}
		if(band.isSelected()){
			temp.add("Band");
		}
		if(transcript_count.isSelected()){
			temp.add("Transcript count");
		}
		if(percentage_gc_content.isSelected()){
			temp.add("% GC content");
		}
		if(status.isSelected()){
			temp.add("Status (gene)");
		}
		
		return temp;
	}

}
