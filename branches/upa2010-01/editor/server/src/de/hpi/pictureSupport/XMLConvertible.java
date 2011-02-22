package de.hpi.pictureSupport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.DomElement;
import org.xmappr.Element;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

/**
 * The Class XMLConvertible.
 */
public abstract class XMLConvertible {
	
	/** The unknown attributes. */
	@Attribute("*")
	protected HashMap<String,String> unknownAttributes = new HashMap<String,String>();

	/** The unknown children. */
	@Element("*")
	protected ArrayList<DomElement> unknownChildren;
	
	/** The resource id to shape. */
	protected HashMap<String, JSONObject> resourceIdToShape;
	//protected Map<String, XPDLThing> resourceIdToObject;
	
	/**
	 * Gets the unknown attributes.
	 *
	 * @return the unknown attributes
	 */
	public HashMap<String,String> getUnknownAttributes() {
		return unknownAttributes;
	}
	
	/**
	 * Gets the unknown children.
	 *
	 * @return the unknown children
	 */
	public ArrayList<DomElement> getUnknownChildren() {
		return unknownChildren;
	}
	
	/*public Map<String, XPDLThing> getResourceIdToObject() {
		return resourceIdToObject;
	}*/
	
	/**
	 * Gets the resource id to shape.
	 *
	 * @return the resource id to shape
	 */
	public HashMap<String, JSONObject> getResourceIdToShape() {
		return resourceIdToShape;
	}
	
	/**
	 * Parses the JSON Object.
	 *
	 * @param modelElement the model element
	 */
	@SuppressWarnings("rawtypes")
	public void parse(JSONObject modelElement) {
		Iterator jsonKeys = modelElement.keys();
		while (jsonKeys.hasNext()) {
			String key = (String) jsonKeys.next();
			String readMethodName = "readJSON" + key;
			if (hasJSONMethod(readMethodName)) {
				try {
					if (keyNotEmpty(modelElement, key)) {
						getClass().getMethod(readMethodName, JSONObject.class).invoke(this, modelElement);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				readJSONunknownkey(modelElement, key);
			}
		}
	}
	
	/**
	 * Read unknowns.
	 *
	 * @param modelElement the model element
	 * @param key the key
	 */
	public void readUnknowns(JSONObject modelElement, String key) {
		String storedData = modelElement.optString(key);
		if (storedData != null) {
			XMLUnknownsContainer unknownContainer = (XMLUnknownsContainer) fromStorable(storedData);
			
			setUnknownAttributes(unknownContainer.getUnknownAttributes());
			setUnknownChildren(unknownContainer.getUnknownElements());
		}
	}

	/**
	 * Read JSON unknown Key.
	 *
	 * @param modelElement the model element
	 * @param key the key
	 */
	public void readJSONunknownkey(JSONObject modelElement, String key) {
		System.err.println("Unknown JSON-key: " + key + "\n" +
							"in JSON-Object: " + modelElement + "\n" +
							"while parsing in: " + getClass() + "\n");
	}
	
	/*public void setResourceIdToObject(Map<String, XPDLThing> mapping) {
		resourceIdToObject = mapping;
	}*/
	
	/**
	 * Sets the resource id to shape.
	 *
	 * @param mapping the mapping
	 */
	public void setResourceIdToShape(HashMap<String, JSONObject> mapping) {
		resourceIdToShape = mapping;
	}
	
	/**
	 * Sets the unknown attributes.
	 *
	 * @param unknowns the unknowns
	 */
	public void setUnknownAttributes(HashMap<String,String> unknowns) {
		unknownAttributes = unknowns;
	}
	
	/**
	 * Sets the unknown children.
	 *
	 * @param unknownElements the new unknown children
	 */
	public void setUnknownChildren(ArrayList<DomElement> unknownElements) {
		unknownChildren = unknownElements;
	}
	
	/**
	 * Write the JSON.
	 *
	 * @param modelElement the model element
	 */
	public void write(JSONObject modelElement) {
		Method[] methods = getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			if (methodName.startsWith("writeJSON")) {
				try {
					getClass().getMethod(methodName, JSONObject.class).invoke(this, modelElement);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Write unknowns.
	 *
	 * @param modelElement the model element
	 * @param key the key
	 * @throws JSONException the jSON exception
	 */
	public void writeUnknowns(JSONObject modelElement, String key) throws JSONException {
		HashMap<String,String> unknownAttributes = getUnknownAttributes();
		ArrayList<DomElement> unknownElements = getUnknownChildren();
		if (!unknownAttributes.isEmpty() || unknownElements != null) {
			XMLUnknownsContainer unknownsContainer = new XMLUnknownsContainer();
			unknownsContainer.setUnknownAttributes(getUnknownAttributes());
			unknownsContainer.setUnknownElements(getUnknownChildren());
		
			modelElement.put(key, makeStorable(unknownsContainer));
		}
	}

	
	/**
	 * From storable.
	 *
	 * @param stored the stored
	 * @return the object
	 */
	protected Object fromStorable(String stored) {
		
		try {
			//Read Base64 String and decode them
			byte[] decodedBytes = Base64.decodeBase64(stored.getBytes("utf-8"));
			ByteArrayInputStream byteStreamIn = new ByteArrayInputStream(decodedBytes);
			//Restore the object
			ObjectInputStream objectStreamIn = new ObjectInputStream(byteStreamIn);
			return objectStreamIn.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Make storable.
	 *
	 * @param objectToStore the object to store
	 * @return the string
	 */
	protected String makeStorable(Object objectToStore) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			//Serialize the Java object
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(objectToStore);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Encode the byte stream with Base64 -> Readable characters for the JSONObject
		return new String(Base64.encodeBase64(byteStream.toByteArray()));	
	}
	
	/**
	 * Checks for json method.
	 *
	 * @param methodName the method name
	 * @return true, if successful
	 */
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

	/**
	 * Checks for method json parameter.
	 *
	 * @param method the method
	 * @return true, if successful
	 */
	@SuppressWarnings("rawtypes")
	protected boolean hasMethodJSONParameter(Method method) {
		Class[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 1) {
			return parameterTypes[0].equals(JSONObject.class);
		}
		return false;
	}
	
	/**
	 * Key not empty.
	 *
	 * @param modelElement the model element
	 * @param key the key
	 * @return true, if successful
	 */
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
}
