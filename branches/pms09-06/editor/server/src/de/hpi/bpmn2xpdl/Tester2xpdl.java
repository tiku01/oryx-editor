package de.hpi.bpmn2xpdl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.json.JSONException;

public class Tester2xpdl {
	
	protected static String path = "C:/Users/Markus Goetz/workspace/Oryx/editor/server/src/de/hpi/bpmn2xpdl/";
	
	public static void main(String[] args) throws JSONException {
		BPMN2XPDLConverter converter = new BPMN2XPDLConverter();
//		String fileContent = readFile(path + "all.json");
//		System.out.println(converter.exportXPDL(fileContent));
		String fileContent = readFile(path + "import001.xml");
		
		long start = System.currentTimeMillis();
		String result = converter.importXPDL(fileContent);
		System.out.println(result);
		writeFile(path + "export000.json", result);
		try {
			fileContent = readFile(path + "export000.json");
			result = converter.exportXPDL(fileContent);
			System.out.println(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	public static String readFile(String fileName) {
		try {
			FileInputStream fileStream = new FileInputStream(fileName);
			DataInputStream dataStream = new DataInputStream(fileStream);
			InputStreamReader inputReader = new InputStreamReader(dataStream);
			BufferedReader bufferedReader = new BufferedReader(inputReader);
			String fileContentString = "";
			while (bufferedReader.ready()) {
				fileContentString += bufferedReader.readLine() + "\n";
			}
			bufferedReader.close();
			return fileContentString;
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
			return "";
		}
	}
	
	public static void writeFile(String fileName, String toWrite) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(toWrite);
			bufferedWriter.close();
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
	}
}
