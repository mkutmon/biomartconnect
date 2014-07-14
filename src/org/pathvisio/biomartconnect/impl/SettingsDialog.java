package org.pathvisio.biomartconnect.impl;

import java.awt.BorderLayout;
<<<<<<< HEAD
import java.awt.FlowLayout;
=======
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.Iterator;
import java.util.Map;
=======
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
<<<<<<< HEAD
import javax.swing.JScrollPane;

public class SettingsDialog extends JDialog {
	Map<String,String> attr_map;
	JCheckBox[] jc;
	/*
=======

public class SettingsDialog extends JDialog {
	
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7
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
<<<<<<< HEAD
	*/
	BiomartConnectPlugin bcp;
	
	public SettingsDialog(BiomartConnectPlugin bcp, Map<String,String> attr_map){
/*		
=======
	BiomartConnectPlugin bcp;
	
	public SettingsDialog(BiomartConnectPlugin bcp){
		
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7
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
<<<<<<< HEAD
		*/
		this.bcp = bcp;
		this.attr_map = attr_map;
=======
		this.bcp = bcp;
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7
		
		initUI();
	}
	
	public final void initUI(){

		JLabel title = new JLabel("Choose attributes:");
		setLayout(new BorderLayout());
		
		JPanel jp = new JPanel();
<<<<<<< HEAD
		jp.setLayout(new GridLayout((attr_map.size()/4)+1,4));
		
		jc = new JCheckBox[attr_map.size()];
		int temp_counter = 0;
		Iterator<String> it = attr_map.keySet().iterator();
		while(it.hasNext()){
			String temp = it.next();
			jc[temp_counter] = new JCheckBox(temp);
			if(attr_map.get(temp).equals("ensembl_gene_id") || attr_map.get(temp).equals("external_gene_id") || attr_map.get(temp).equals("description") || attr_map.get(temp).equals("chromosome_name") || attr_map.get(temp).equals("start_position"))
				jc[temp_counter].setSelected(true);
			jp.add(jc[temp_counter++]);
		}
		
/*		
=======
		jp.setLayout(new GridLayout(4,3));
		
		
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7
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
<<<<<<< HEAD
	*/	
=======
		
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7
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
<<<<<<< HEAD
		JScrollPane scrollpane = new JScrollPane(jp,  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollpane,BorderLayout.CENTER);
=======
		add(jp,BorderLayout.CENTER);
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7
		add(title,BorderLayout.NORTH);
        setTitle("BiomartConnect");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
<<<<<<< HEAD
        setSize(450,330);
        
=======
        setSize(450, 330);
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7

	}
	
	public ArrayList<String> selectedOptions() {
		
		ArrayList<String> temp = new ArrayList<String>();
		
<<<<<<< HEAD
		int i;
		for(i=0;i<jc.length;i++){
			if(jc[i].isSelected()){
				temp.add(jc[i].getText());
			}
		}
		
/*		if(ensembl_gene_id.isSelected()){
=======
		if(ensembl_gene_id.isSelected()){
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7
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
<<<<<<< HEAD
	*/	
=======
		
>>>>>>> 24020c8f545f359018e5b0625bfa903b67946fd7
		return temp;
	}

}
