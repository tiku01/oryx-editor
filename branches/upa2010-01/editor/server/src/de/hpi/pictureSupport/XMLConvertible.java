package de.hpi.pictureSupport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.DomElement;
import org.xmappr.Element;

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
	 * Sets the unknown attributes.
	 *
	 * @param unknownAttributes the unknown attributes
	 */
	public void setUnknownAttributes(HashMap<String, String> unknownAttributes) {
		this.unknownAttributes = unknownAttributes;
	}

	/**
	 * Gets the unknown children.
	 *
	 * @return the unknown children
	 */
	public ArrayList<DomElement> getUnknownChildren() {
		return unknownChildren;
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
	public void writeJSON(JSONObject modelElement) throws JSONException {
		Method[] methods = getClass().getMethods();
		ArrayList<Method> unusedHandler = new ArrayList<Method>();
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.startsWith("writeJSON") && methodName.length() != 9) {
				if (methodName.contains("unused")){
					unusedHandler.add(method);
				} else {
					try {
						getClass().getMethod(methodName, JSONObject.class).invoke(this, modelElement);
					} catch (Exception e) {
						try {
							Logger.e(this.getClass()+"\t"+methodName+"\n"+modelElement,e);
						} catch (Exception e2){
							Logger.e(this.getClass()+"\t"+methodName,e);
						}
					}
				}
			}
		}
		for (Method unusedMethod : unusedHandler){
			String methodName = unusedMethod.getName();
			try {
				getClass().getMethod(methodName, JSONObject.class).invoke(this, modelElement);
			} catch (Exception e) {
				Logger.e(e.getMessage());
				e.printStackTrace();
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
}
