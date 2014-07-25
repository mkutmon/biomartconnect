package org.pathvisio.biomartconnect.impl;

import java.util.HashMap;
import java.util.Map;

public class Utils {
	
	/**
	 * maps between BridgeDb species names
	 * and Ensembl BioMart species names
	 */
	public static String mapOrganism(String organism) {
		Map<String, String> orgMap = new HashMap<String, String>();
		orgMap.put("BosTaurus", "btaurus_gene_ensembl");
		orgMap.put("CaenorhabditisElegans", "celegans_gene_ensembl");
		orgMap.put("CanisFamiliaris", "cfamiliaris_gene_ensembl");
		orgMap.put("DanioRerio", "drerio_gene_ensembl");
		orgMap.put("DasypusNovemcinctus", "dnovemcinctus_gene_ensembl");
		orgMap.put("DrosophilaMelanogaster", "dmelanogaster_gene_ensembl");
		orgMap.put("EchinposTelfairi", "etelfairi_gene_ensembl");
		orgMap.put("EquusCaballus", "ecaballus_gene_ensembl");
		orgMap.put("GallusGallus", "ggallus_gene_ensembl");	
		orgMap.put("HomoSapiens", "hsapiens_gene_ensembl");
		orgMap.put("LoxodontaAfricana", "lafricana_gene_ensembl");
		orgMap.put("MacacaMulatta", "mmulatta_gene_ensembl");
		orgMap.put("MusMusculus", "mmusculus_gene_ensembl");
		orgMap.put("MonodelphisDomestica", "mdomestica_gene_ensembl");
		orgMap.put("OrnithorhynchusAnatinus", "oanatinus_gene_ensembl");
		orgMap.put("OryziasLatipes", "olatipes_gene_ensembl");
		orgMap.put("OryctolagusCuniculus", "ocuniculus_gene_ensembl");
		orgMap.put("PanTroglodytes", "ptroglodytes_gene_ensembl");
		orgMap.put("SusScrofa", "sscrofa_gene_ensembl");
		orgMap.put("RattusNorvegicus", "rnorvegicus_gene_ensembl");
		orgMap.put("SaccharomycesCerevisiae", "scerevisiae_gene_ensembl");
		orgMap.put("SorexAraneus", "saraneus_gene_ensembl");
		orgMap.put("TetraodonNigroviridis", "tnigroviridis_gene_ensembl");		
		orgMap.put("XenopusTropicalis", "xtropicalis_gene_ensembl");
		
		if(orgMap.containsKey(organism)) {
			return orgMap.get(organism);
		}
		return null;
	}
}
