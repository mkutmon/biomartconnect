package org.pathvisio.biomartconnect.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SequenceContainer {

	List<InfoPerTranscriptId> transcriptIdList;
	
	public SequenceContainer(){
		
		transcriptIdList = new ArrayList<InfoPerTranscriptId>();
	}
	
	public InfoPerTranscriptId addSequence(){
		
		InfoPerTranscriptId temp = new InfoPerTranscriptId();
		temp.setSequence(null);
		temp.setTranscriptId(null);
		transcriptIdList.add(temp);
		return temp;
	}
	
	public InfoPerTranscriptId find(String id) {
		for(InfoPerTranscriptId temp: transcriptIdList){
			if(temp.getTranscriptId().equals(id)){
				return temp;
			}
		}
		return null;
	}
	
	public void fastaParser(InputStream is, String id, Boolean isExon){

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		String [] temp_array = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				
				System.err.println(line);
				
				if(line.startsWith(">")){

					if(temp_array != null){
						
						for(int i=0;i<temp_array.length && temp_array[i] != null;i++){
							InfoPerTranscriptId it = find(temp_array[i]);
							if(it != null){
								if(isExon){
									it.setExon(sb.toString());
								}
								else {
									it.setSequence(sb.toString());
								}
							}
						}
						
						sb = new StringBuilder();
					}
					
					String temp = line.substring(1);
					temp_array = temp.split("[|]");
					//System.err.println(Arrays.toString(temp_array));
					Set<String> temp_set = new HashSet<String>();
					for(int i=0;i<temp_array.length;i++){
						
						temp_set.addAll(Arrays.asList(temp_array[i].split("[;]")));
					}
					
					//System.err.println(temp_set.toString());
					
					temp_array = temp_set.toArray(temp_array);
					
					for(int i=0;i<temp_array.length && temp_array[i] != null;i++){
						
						System.err.println(temp_array[i]);
						
						if(!temp_array[i].equals(id)){
							if(find(temp_array[i]) == null){
							InfoPerTranscriptId temp_seq = addSequence();
							temp_seq.setTranscriptId(temp_array[i]);
							}
						}
						
						
					}
				}
				
				else {
					sb.append(line);
				}
				//sb.append('\n');		
			}
			
			for(int i=0;i<temp_array.length && temp_array[i] != null;i++){
				InfoPerTranscriptId it = find(temp_array[i]);
				if(it != null){
					if(isExon){
				it.setExon(sb.toString());
					}
					else
					{
						it.setSequence(sb.toString());
					}
				}
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
		
	}

	public void print() {
		// TODO Auto-generated method stub
		for(InfoPerTranscriptId temp: transcriptIdList){			
			System.err.println("starts here" + temp.getTranscriptId());
			
			if(temp.getSequence() != null){
				System.err.println("sequence" + temp.getSequence().substring(0, 10));
			}
			else{
				System.err.println("Null");
			}
			if(temp.getExon() != null){
				List<String> temp_list = temp.getExon();
				for(String temp_string: temp_list){	
					System.err.println("exon" + temp_string.substring(0, 10));	
				}
			}
			else{
				System.err.println("Null");
			}
			
		}
	}
}
