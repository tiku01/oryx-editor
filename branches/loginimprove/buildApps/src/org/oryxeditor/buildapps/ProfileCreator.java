package org.oryxeditor.buildapps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.FileCopyUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * @author Philipp Berger
 * 
 */
public class ProfileCreator {
	/**
	 * @param args
	 *            path to plugin dir and output dir
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws IOException,
	ParserConfigurationException, SAXException, JSONException {
		if (args.length != 2) {
			System.err.println("Wrong Number of Arguments!");
			System.err.println(usage());
			return;
		}
		String pluginDirPath = args[0];
		
		String pluginXMLPath = pluginDirPath + "/plugins.xml";// args[0];
		String profilePath = pluginDirPath + "/profiles.xml";// ;
		String outputPath = args[1];
		File outDir = new File(outputPath);
		outDir.mkdir();
		HashMap<String, String> nameSrc = new HashMap<String, String>();
		HashMap<String, ArrayList<String>> profilName = new HashMap<String, ArrayList<String>>();
		ArrayList<String> coreNames = new ArrayList<String>();

		extractPluginData(pluginXMLPath, nameSrc, coreNames);
		//HasMap profilName contains the name of the profile
		//as key and an ArrayList of the names of the pluins contained
		//within that profile
		extractProfileData(profilePath, profilName);
		for (String key : profilName.keySet()) {
			ArrayList<String> pluginNames = profilName.get(key);
			//add core plugins to each profile
			pluginNames.addAll(coreNames);

			writeProfileJS(pluginDirPath, outputPath, nameSrc, key, pluginNames);

			writeProfileXML(pluginXMLPath, profilePath, outputPath, key,
					pluginNames);
		}

	}

	/**
	 * Create the profileName.js by reading all path out of the nameSrc Hashmap, the required names
	 * are given
	 * @param pluginDirPath 
	 * 			  path where all plugin-source-paths are relative to
	 * @param outputPath 
	 * 			  location of the generated pfrofile javascript file
	 * @param nameSrc
	 *            plugin name to js source file
	 * @param profileName
	 *            name of the profile, serve as name for the js file
	 * @param pluginNames
	 *            all plugins for this profile
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void writeProfileJS(String pluginDirPath, String outputPath,
			HashMap<String, String> nameSrc, String profileName,
			ArrayList<String> pluginNames) throws IOException,
			FileNotFoundException {
		/*
		 * create a unique set of all plugin names
		 */
		ArrayList<String> pluginNameSet = new ArrayList<String>();
		pluginNameSet.addAll(pluginNames);
		/*
		 * create the output profile file and open a writer
		 */
		File profileFile = new File(outputPath + File.separator + profileName +"Uncompressed.js");
		profileFile.createNewFile();
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(profileFile),"UTF8");
		for (String name : pluginNameSet) {
			/*
			 * lookup the javascript file path of each plugin
			 */
			String source = nameSrc.get(name);
			/*
			 * if no path is found, an error in the plugin xml exists
			 */
			if(source==null)
				throw new IllegalArgumentException("In profile '"+profileName+"' an unknown plugin is referenced named '"+ name+"'");
			
			/*
			 * open the javascript file and append the whole plugin javascript to the profiles javascript file
			 */
			InputStreamReader reader = new InputStreamReader(new FileInputStream(pluginDirPath + File.separator + source),"UTF8");

			writer.append(FileCopyUtils.copyToString(reader));
		}
		writer.close();

		/*
		 * creqate a new file for the compressed version of the profile file
		 */
		File compressOut=new File(outputPath + File.separator + profileName +".js");
		InputStreamReader reader = new InputStreamReader(new FileInputStream(profileFile),"UTF8");
		OutputStreamWriter writer2 = new OutputStreamWriter(new FileOutputStream(compressOut),"UTF8");
		/*
		 * start YUI compression with the concatinated profile javascript file as input and the compress out file as output
		 */
		try{
			com.yahoo.platform.yui.compressor.JavaScriptCompressor x= new JavaScriptCompressor(reader, null);
			x.compress(writer2, 1, true, false, false, false);
			writer2.close();
			reader.close();

		}catch (Exception e) {
			/*
			 * Fallback if yui compression fails
			 */
			System.err.println("Profile Compression failed! profile: "+compressOut.getAbsolutePath()+ " uncompressed version is used, please ensure javascript correctness and compatibility of YUI compressor with your system");
			e.printStackTrace();
			writer2.close();
			reader.close();
			
			OutputStreamWriter writer3 = new OutputStreamWriter(new FileOutputStream(outputPath + File.separator + profileName +".js"),"UTF8");
			InputStreamReader reader1 = new InputStreamReader(new FileInputStream(profileFile),"UTF8");
			FileCopyUtils.copy(reader1, writer3);
			reader1.close();
			writer3.close();

		}
	}

	/**
	 * Create an copy of the plugins.xml, marking all plugins of the profile as engaged
	 * the new plugins.xml is named PROFILENAME.xml
	 * @param pluginXMLPath
	 * 				path to the standard plugins.xml
	 * @param profileXMLPath
	 * 				path to the profile.xml to read from
	 * @param outputPath
	 * 				path were all profile files are located
	 * @param profileName
	 *            	name of the profile, serve as name for the js file
	 * @param pluginNames
	 *            all plugins for this profile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws JSONException
	 */
	private static void writeProfileXML(String pluginXMLPath,
			String profileXMLPath, String outputPath, String profileName,
			ArrayList<String> pluginNames) throws IOException,
			FileNotFoundException, JSONException {
		//All plugins are copied in the xml file to be used with the current
		//profile
		FileCopyUtils.copy(new FileInputStream(pluginXMLPath),
				new FileOutputStream(outputPath + File.separator + profileName + ".xml"));
		//reader for the profile that contains all plugins from the plugins.xml
		InputStream reader = new FileInputStream(outputPath + File.separator + profileName
				+ ".xml");
		DocumentBuilder builder;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			builder = factory.newDocumentBuilder();
			//outProfileXMLdocument contains all plugins from the plugins.xml
			//no plugins are removed according to the profile
			Document outProfileXMLdocument = builder.parse(reader);

			//Reader for the file named profile.xml
			InputStream readerProfileXML  = new FileInputStream(profileXMLPath);
			builder = factory.newDocumentBuilder();
			//profilesXMLdocument contains the contents of profile.xml
			Document profilesXMLdocument = builder.parse(readerProfileXML);
			//The plugin-nodes are saved here
			NodeList pluginNodeList = outProfileXMLdocument
			.getElementsByTagName("plugin");
			//The one node containing the current profile
			Node profileNode = getProfileNodeFromDocument(profileName,
					profilesXMLdocument);

			if (profileNode == null)
				throw new IllegalArgumentException(
				"profile not defined in profile xml");

			NamedNodeMap attr = profileNode.getAttributes();
			JSONObject config=new JSONObject();
			for(int i=0;i<attr.getLength();i++){
				config.put(attr.item(i).getNodeName(), attr.item(i).getNodeValue());
			}
			for (int profilePluginNodeIndex = 0; profilePluginNodeIndex < profileNode
			.getChildNodes().getLength(); profilePluginNodeIndex++) {
				Node tmpNode = profileNode.getChildNodes().item(profilePluginNodeIndex);
				//check if we have a plugin xml tag
				//this if construct is useless because there is no else branch
				if("plugin".equals(tmpNode.getNodeName()) || tmpNode==null)
					continue;
				JSONObject nodeObject = new JSONObject();
				NamedNodeMap attr1 = tmpNode.getAttributes();
				if(attr1==null)
					continue;
				for(int i=0;i<attr1.getLength();i++){
					nodeObject.put(attr1.item(i).getNodeName(), attr1.item(i).getNodeValue());
				}

				if(config.has(tmpNode.getNodeName())){
					config.getJSONArray(tmpNode.getNodeName()).put(nodeObject);
				}else{
					config.put(tmpNode.getNodeName(), new JSONArray().put(nodeObject));
				}

			}
			//Print the JSON configuration file to the file system
			FileCopyUtils.copy(config.toString(), new OutputStreamWriter(new FileOutputStream(outputPath + File.separator+profileName+".conf"),"UTF8"));
			// for each plugin in the copied plugin.xml
			for (int i = 0; i < pluginNodeList.getLength(); i++) {
				Node pluginNode = pluginNodeList.item(i);
				String pluginName = pluginNode.getAttributes().getNamedItem(
				"name").getNodeValue();
				// if plugin is in the current profile
				if (pluginNames.contains(pluginName)) {
					// mark plugin as active
					((Element) pluginNode).setAttribute("engaged", "true");

					// throw new
					// IllegalArgumentException("profile not defined in profile xml");
					// plugin defintion found copy or replace properties
					Node profilePluginNode = getLastPluginNode(profileNode,
							pluginName);
					if(profilePluginNode==null){
						//System.out.println("Plugin: "+pluginName+" assumed to be core");
						continue;}
					saveOrUpdateProperties(pluginNode, profilePluginNode);

				}else{
					((Element) pluginNode).setAttribute("engaged", "false");
				}
			}
			writeXMLToFile(outProfileXMLdocument, outputPath + File.separator
					+ profileName + ".xml");
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Searches the plugin definition for the passed plugin name in the profile node and all depending profiles
	 * @param profileNode 
	 * 				node to begin searching from
	 * @param pluginName 
	 * 				name of the wanted plugin
	 * @return 
	 * 				the first matching node for the current plugin's name, null if no match found
	 */
	private static Node getLastPluginNode(Node profileNode, String pluginName) {
		Node profilePluginNode = null;
		// search plugin defefinition in the current node
		for (int profilePluginNodeIndex = 0; profilePluginNodeIndex < profileNode
		.getChildNodes().getLength(); profilePluginNodeIndex++) {
			Node tmpNode = profileNode.getChildNodes().item(
					profilePluginNodeIndex);
			if (tmpNode.getAttributes() != null
					&& tmpNode.getAttributes().getNamedItem("name") != null
					&& tmpNode.getAttributes().getNamedItem("name")
					.getNodeValue().equals(pluginName))
				profilePluginNode = tmpNode;
		}
		
		/*
		 * if no definition in the passed node found, searches the dependent profiles
		 */
		if (profilePluginNode == null) {
			String[] dependsOnProfiles = getDependencies(profileNode);
			/*
			 * if no dependencies defined return null
			 */
			if (dependsOnProfiles==null ||dependsOnProfiles.length == 0) {
				return null;
			}
			for (String dependsProfile : dependsOnProfiles) {
				/*
				 * recursive call to traverse all depndencies
				 */
				profilePluginNode = getLastPluginNode(
						getProfileNodeFromDocument(dependsProfile, profileNode
								.getOwnerDocument()), pluginName);
				/*
				 * if first match found break
				 */
				if (profilePluginNode != null)
					break;
			}
			// plugin definition not found, plugin defined in depended profiles
			// TODO handle recursive property search
		}
		return profilePluginNode;
	};


	/**
	 * Helper method which writes a XML document to the given filename
	 * @param outProfileXMLdocument
	 * @param xmlFileName
	 * @throws FileNotFoundException
	 */
	private static void writeXMLToFile(Document outProfileXMLdocument,
			String xmlFileName) throws FileNotFoundException {
		// ---- Use a XSLT transformer for writing the new XML file ----
		try {
			Transformer transformer = TransformerFactory.newInstance()
			.newTransformer();
			DOMSource source = new DOMSource(outProfileXMLdocument);
			FileOutputStream os = new FileOutputStream(new File(xmlFileName));
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enables the overwriting of properties
	 * @param pluginNode
	 * @param profilePluginNode
	 * @throws DOMException
	 */
	private static void saveOrUpdateProperties(Node pluginNode,
			Node profilePluginNode) throws DOMException {
		//		for(String dependent:getDependencies(profilePluginNode.getParentNode())){
		//			saveOrUpdateProperties(pluginNode,getProfileNodeFromDocument(dependent, profilePluginNode.getOwnerDocument()));
		//		};
		// for each child node in the profile xml
		for (int index = 0; index < profilePluginNode.getChildNodes()
		.getLength(); index++) {
			Node profilePluginChildNode = profilePluginNode.getChildNodes()
			.item(index);
			// check if property
			if (profilePluginChildNode.getNodeName() == "property") {
				boolean found = false;
				// search for old definitions
				for (int childIndex = 0; childIndex < pluginNode
				.getChildNodes().getLength(); childIndex++) {
					Node pluginChildNode = pluginNode.getChildNodes().item(
							childIndex);
					if (pluginChildNode.getNodeName() == "property") {
						NamedNodeMap propertyAttributes = profilePluginChildNode
						.getAttributes();
						for (int attrIndex = 0; attrIndex < propertyAttributes
						.getLength(); attrIndex++) {
							String newPropertyName = profilePluginChildNode
							.getAttributes().item(attrIndex)
							.getNodeName();
							Node oldPropertyNode = pluginChildNode
							.getAttributes().getNamedItem(
									newPropertyName);
							if (oldPropertyNode != null) {
								// old definition found replace value
								found = true;
								String newValue = profilePluginChildNode
								.getAttributes().item(attrIndex).getNodeValue();
								oldPropertyNode.setNodeValue(newValue);
							}
						}
					}
				}
				if (!found) {
					// no definition found add some
					Node property = pluginNode.getOwnerDocument()
					.createElement("property");
					((Element) property).setAttribute("name",
							profilePluginChildNode.getAttributes()
							.getNamedItem("name").getNodeValue());
					((Element) property).setAttribute("value",
							profilePluginChildNode.getAttributes()
							.getNamedItem("value").getNodeValue());
					pluginNode.appendChild(property);
				}
			}
		}
	}

	/**
	 * Searches the profile with the passed name in the passed document
	 * @param ProfileName
	 * @param profilesXMLdocument
	 * @throws DOMException
	 */
	private static Node getProfileNodeFromDocument(String ProfileName,
			Document profilesXMLdocument) throws DOMException {
		Node profileNode = null;
		NodeList profileNodes = profilesXMLdocument
		.getElementsByTagName("profile");
		for (int i = 0; i < profileNodes.getLength(); i++) {
			if (profileNodes.item(i).getAttributes().getNamedItem("name")
					.getNodeValue().equals(ProfileName)) {
				profileNode = profileNodes.item(i);
				break;
			}
		}
		return profileNode;
	}

	/**
	 * Reads the whole profile.xml to create the mapping from profile names to plugin names
	 * @param profilePath
	 * @param profilName
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws DOMException
	 */
	private static void extractProfileData(String profilePath,
			HashMap<String, ArrayList<String>> profilName)
	throws FileNotFoundException, ParserConfigurationException,
	SAXException, IOException, DOMException {
		/*
		 * read the profile xml
		 */
		InputStream reader = new FileInputStream(profilePath);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = factory.newDocumentBuilder().parse(reader);
		NodeList profiles = document.getElementsByTagName("profile");
		for (int i = 0; i < profiles.getLength(); i++) {
			Node profile = profiles.item(i);
			String name = profile.getAttributes().getNamedItem("name")
			.getNodeValue();
			/*
			 * new entry for the name of the profile
			 */
			profilName.put(name, new ArrayList<String>());
			for (int q = 0; q < profile.getChildNodes().getLength(); q++) {
				if (profile.getChildNodes().item(q).getNodeName()
						.equalsIgnoreCase("plugin")) {
					/*
					 * add each plugin name under the profile name
					 */
					profilName.get(name).add(
							profile.getChildNodes().item(q).getAttributes()
							.getNamedItem("name").getNodeValue());
				}
				;
			}
		}
		resolveDependencies(profilName, profiles);
	}

	/**
	 * Traverse over the dependencies of all profile to complete the profileName map. 
	 * In order that to each profile name all direct contained plugins are list 
	 * and all plugins which are contained over dependencies
	 * @param profilName map to complete
	 * @param profiles the xml nodelist of all plugins
	 * @throws DOMException
	 */
	private static void resolveDependencies(
			HashMap<String, ArrayList<String>> profilName, NodeList profiles)
	throws DOMException {
		
		/*
		 * read the dependencies from the xml document into a HashMap
		 */
		HashMap<String, String[]> profileDepends = new HashMap<String, String[]>();
		for (int i = 0; i < profiles.getLength(); i++) {
			Node profile = profiles.item(i);
			String name = profile.getAttributes().getNamedItem("name")
			.getNodeValue();
			profileDepends.put(name, getDependencies(profile));
		}
		/*
		 * each profile which has no dependencies is complete, and does need anymore attention
		 */
		ArrayList<String> completedProfiles = new ArrayList<String>();
		for (String key : profileDepends.keySet()) {
			if (profileDepends.get(key) == null) {
				completedProfiles.add(key);
			}
		}
		/*
		 * remove all complete profiles from the dependencies
		 */
		for (String cur : completedProfiles)
			profileDepends.remove(cur);

		while (!profileDepends.isEmpty()) {
			for (String key : profileDepends.keySet()) {
				boolean allIn = true;
				/*
				 * check if all dependent profile are complete, 
				 * therefore has no unresolved dependencies
				 */
				for (String name : profileDepends.get(key)) {
					if (!completedProfiles.contains(name)) {
						allIn = false;
						break;
					}
				}
				/*
				 * if all profile resolved, add for each dependent profile all 
				 * plugin names to the list of the current plugin
				 */
				if (allIn) {
					for (String name : profileDepends.get(key)) {
						profilName.get(key).addAll(profilName.get(name));
						completedProfiles.add(key);
					}

				}
			}
			
			/*
			 * remove all complete profiles from the dependencies
			 */
			for (String cur : completedProfiles)
				profileDepends.remove(cur);
		}
	}

	/**
	 * Reads the depends attribute from a profile node, converting it in a list of profile names
	 * @param profil
	 *            DocumentNode containing a profile
	 * @return
	 * 			  null if no dependencies defined
	 * @throws DOMException
	 */
	private static String[] getDependencies(Node profil) throws DOMException {
		String[] dependencies = null;
		if (profil.getAttributes().getNamedItem("depends") != null) {
			dependencies = profil.getAttributes().getNamedItem("depends")
			.getNodeValue().split(",");
		}
		return dependencies;
	}

	/**
	 * @param pluginXMLPath
	 * @param nameSrc
	 *            HashMap links Pluginnames and Sourcefiles
	 * @param coreNames
	 *            ArrayList containing Names of all core plugins
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws DOMException
	 */
	private static void extractPluginData(String pluginXMLPath,
			HashMap<String, String> nameSrc, ArrayList<String> coreNames)
	throws FileNotFoundException, ParserConfigurationException,
	SAXException, IOException, DOMException {
		/*
		 * read the node list of all plugins from the xml path
		 */
		InputStream reader = new FileInputStream(pluginXMLPath);
		DocumentBuilder builder;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		Document document = builder.parse(reader);
		NodeList plugins = document.getElementsByTagName("plugin");

		/*
		 * collect the mapping from name to source file from all plugin nodes
		 * and collect all plugins which are marked as core
		 */
		for (int i = 0; i < plugins.getLength(); i++) {
			String name = plugins.item(i).getAttributes().getNamedItem("name")
			.getNodeValue();
			String src = plugins.item(i).getAttributes().getNamedItem("source")
			.getNodeValue();
			assert (src!=null);
			nameSrc.put(name, src);
			if (plugins.item(i).getAttributes().getNamedItem("core") != null) {
				if (plugins.item(i).getAttributes().getNamedItem("core")
						.getNodeValue().equalsIgnoreCase("true")) {
					coreNames.add(name);
				}
			}
		}
	}

	public static String usage() {
		String use = "Profiles Creator\n"
			+ "Use to parse the profiles.xml and creates\n"
			+ "for each profile an .js-source. Therefore additional\n"
			+ "information from the plugins.xml is required.\n"
			+ "usage:\n" + "java ProfileCreator pluginPath outputDir";
		return use;
	}

}
