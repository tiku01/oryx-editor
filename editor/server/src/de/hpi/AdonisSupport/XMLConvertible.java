package de.hpi.AdonisSupport;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.DomElement;
import org.xmappr.Element;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * base class for all elements which should be read
 * it provides</br> 
 * a facility to store unused attributes or elements
 * in a String which can be exported if necessary</br>
 * the basic mechanism to read in and write the json model for/from adonis
 *
 */
public abstract class XMLConvertible implements Serializable{
	
	private static final long serialVersionUID = 173231207049052166L;


	static public void writeError(Exception e){
		Log.e(e.getMessage());
		e.printStackTrace();
	}
	
	/**
	 * buckets for unknown attributes and elements (+ accessors)
	 */
	@Attribute("*")
	protected HashMap<String,String> unknownAttributes = new HashMap<String,String>();
	@Element("*")
	protected ArrayList<DomElement> unknownChildren;
	
	
	public HashMap<String,String> getUnknownAttributes() {
		return unknownAttributes;
	}
	
	public void setUnknownAttributes(HashMap<String,String> unknowns) {
		unknownAttributes = unknowns;
	}
	
	public void setUnknownChildren(ArrayList<DomElement> unknownElements) {
		unknownChildren = unknownElements;
	}
	
	public ArrayList<DomElement> getUnknownChildren() {
		return unknownChildren;
	}
	
	
	/**
	 * methods to parse a JSON object tree and prepare them for xmappr
	 * implicit interface for creating a JSON-Object (methods must start with "writeJSON")	  
	 * @throws JSONException 
	 */
	public void readJSON(JSONObject modelElement){
		Iterator<?> jsonKeys = modelElement.keys();
		while (jsonKeys.hasNext()) {
			String key = (String) jsonKeys.next();
			String readMethodName = "readJSON" + key;
			if (key.length() > 0 && hasJSONMethod(readMethodName)) {
				try {
					if (keyNotEmpty(modelElement, key)) {
						getClass().getMethod(readMethodName, JSONObject.class).invoke(this, modelElement);
					}
				} catch (Exception e) {
					try {
						Log.e(e.getMessage()+"\t"+this.getClass()+"\t"+readMethodName+"\n"+modelElement);
					} catch (Exception e2){
						Log.e(e.getMessage()+"\t"+this.getClass()+"\t"+readMethodName);
					}
					e.printStackTrace();
				}
			} else {
				readJSONunknownkey(modelElement, key);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void readUnknowns(JSONObject modelElement, String key) {
		String storedData = modelElement.optString(key);
		if (storedData != null) {
			SerializableContainer<DomElement> unknownContainer = (SerializableContainer<DomElement>) fromStorable(storedData);
			
			setUnknownAttributes(unknownContainer.getAttributes());
			setUnknownChildren(unknownContainer.getElements());
		}
	}

	/**
	 * responsible for writing error messages 
	 * @param modelElement
	 * @param key
	 */
	public void readJSONunknownkey(JSONObject modelElement, String key) {
		Log.e( "Unknown JSON-key: " + key + "\n" +
							"in JSON-Object: " + modelElement + "\n" +
							"while parsing in: " + getClass() + "\n");
	}
	
	
	protected boolean keyNotEmpty(JSONObject modelElement, String key) {
		try {
			JSONObject objectAtKey = modelElement.getJSONObject(key);
			//Value is a valid JSONObject and has members
			return objectAtKey.length() > 0;
		} catch(JSONException objectException) {
			try {
				JSONArray arrayAtKey = modelElement.getJSONArray(key);
				//Value is a valid JSONArray and has at least one element
				return arrayAtKey.length() > 0;
			} catch(JSONException arrayException) {
				return !modelElement.optString(key).equals(""); 
			}
		}
	}
	
	protected boolean hasJSONMethod(String methodName) {
		Method[] methods = getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName) &
				hasMethodJSONParameter(methods[i])) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected boolean hasMethodJSONParameter(Method method) {
		Class[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 1) {
			return parameterTypes[0].equals(JSONObject.class);
		}
		return false;
	}
	
	public JSONArray getJSONArray(JSONObject json,String key) throws JSONException{
		if (json.optJSONArray(key) == null){
			json.put(key,new JSONArray());
		}
		return json.getJSONArray(key);
	}
	
	public JSONObject getJSONObject(JSONObject json,String key) throws JSONException{
		if (json.optJSONObject(key) == null){
			json.put(key,new JSONObject());
		}
		return json.getJSONObject(key);
	}
	
	/**
	 * parses the class and looks for methods starting with writeJSON
	 * these methods are executed in the current context with the json object as base 
	 * @throws JSONException 
	 */
	public void writeJSON(JSONObject modelElement) throws JSONException {
		Method[] methods = getClass().getMethods();
		ArrayList<Method> unusedHandler = new ArrayList<Method>();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			if (methodName.startsWith("writeJSON") && !methodName.endsWith("writeJSON")) {
				if (methodName.contains("unused")){
					unusedHandler.add(method);
				} else {
					try {
						getClass().getMethod(methodName, JSONObject.class).invoke(this, modelElement);
					} catch (Exception e) {
						try {
							Log.e(e.getMessage()+"\t"+this.getClass()+"\t"+methodName+"\n"+modelElement);
						} catch (Exception e2){
							Log.e(e.getMessage()+"\t"+this.getClass()+"\t"+methodName);
						}
						e.printStackTrace();
					}
				}
			}
		}
		for (Method unusedMethod : unusedHandler){
			String methodName = unusedMethod.getName();
			try {
				getClass().getMethod(methodName, JSONObject.class).invoke(this, modelElement);
			} catch (Exception e) {
				Log.e(e.getMessage()+"\t"+this.getClass()+"\t"+((AdonisStencil)this).getAdonisIndentifier()+"\t"+methodName);
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * write unused attributes to the json file
	 * @param json
	 * @throws JSONException
	 */
	public void writeJSONunused(JSONObject json) throws JSONException {
		
	}

	/**
	 * stores not known elements in a container to conserve them
	 * @param modelElement
	 * @param key
	 * @throws JSONException
	 */
	public void writeUnknowns(JSONObject modelElement, String key) throws JSONException {
		HashMap<String,String> unknownAttributes = getUnknownAttributes();
		ArrayList<DomElement> unknownElements = getUnknownChildren();
		if (!unknownAttributes.isEmpty() || unknownElements != null) {
			SerializableContainer<DomElement> unknownsContainer = new SerializableContainer<DomElement>();
			unknownsContainer.setAttributes(getUnknownAttributes());
			unknownsContainer.setElements(getUnknownChildren());
			
			modelElement.put(key, makeStorable(unknownsContainer));
		}
	}

// h e l p e r   m e t h o d s   (Stream enconding and decoding)
	
	protected Object fromStorable(String stored) {
		BASE64Decoder base64dec = new BASE64Decoder();
		
		try {
			//Read Base64 String and decode them
			byte[] decodedBytes = base64dec.decodeBuffer(stored);
			ByteArrayInputStream byteStreamIn = new ByteArrayInputStream(decodedBytes);
			//Restore the object
			ObjectInputStream objectStreamIn = new ObjectInputStream(byteStreamIn);
			return objectStreamIn.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected String makeStorable(Object objectToStore) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			//Serialize the Java object
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(objectToStore);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BASE64Encoder base64enc = new BASE64Encoder();
		//Encode the byte stream with Base64 -> Readable characters for the JSONObject
		return base64enc.encode(byteStream.toByteArray());	
	}
	
	
	
	
}
