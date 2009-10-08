package de.hpi.bpmn2_0.migration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MigrationTest {
	
	final static String path = "/Users/Phil/Documents/Studium/signavio/oryx BPMN 2.0/editor/server/src/de/hpi/bpmn2_0/migration/";
	
	
	public static void main(String[] args) throws Exception {		
		
		File json = new File(path + "bpmn1.1.json");
		BufferedReader br = new BufferedReader(new FileReader(json));
		String bpmnJson = "";
		String line;
		while((line = br.readLine()) != null) {
			bpmnJson += line;
		}
		
		BPMN2Migrator mig = new BPMN2Migrator(bpmnJson);
		
		String outJSON = mig.migrate();
		
		System.out.println(outJSON);
	}
}
