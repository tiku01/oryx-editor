package de.hpi.pattern;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Stores pattern serialized in a simple file per stencilset. Not safe for multiple user environment, 
 * because Dirty Reads, Non-repeatable reads and alike are all possible and not prevented.
 * This is a transitional implementation until database support is implemented.
 * 
 * @author Kai Höwelmeyer <kai.hoewelmeyer@student.hpi.uni-potsdam.de>
 *
 */
public class PatternFilePersistance implements PatternPersistanceProvider {
	private static int patternId = 0;
	
	private final String ssNameSpace;
	private File patternFile;
	private ArrayList<Pattern> patternList = new ArrayList<Pattern>();
	
	@SuppressWarnings("unchecked")
	public PatternFilePersistance(String ssNameSpace, String baseDir) {
		this.ssNameSpace = ssNameSpace;
		this.patternFile = new File(baseDir + "/" + this.ssNameSpace.hashCode() + ".patternStore");
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(this.patternFile);
			ois = new ObjectInputStream(fis);
			this.patternList = (ArrayList<Pattern>) ois.readObject();
		} catch (FileNotFoundException e) {
			// do nothing
		} catch (Exception e) { //TODO handle it better!
			e.printStackTrace();
		}
		finally {
			try {
				if (ois != null) ois.close();
				if (fis != null) fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void deletePattern(int id) {
		ListIterator<Pattern> it = this.patternList.listIterator();
		while(it.hasNext()) {
			Pattern p = it.next();
			if(p.getId() == id) {
				it.remove();
				break;
			}
		}
		commit();
	}
	
	@Override
	public Pattern getPattern(int id) {
		ListIterator<Pattern> it = this.patternList.listIterator();
		while(it.hasNext()) {
			Pattern p = it.next();
			if(p.getId() == id)
				return p;  //found the matching pattern
		}
		return null; //didn't find the pattern
	}

	@Override
	public Pattern saveNewPattern(String serializedPattern, String description) {
		int id = generateId();
		String imageUrl = generateImage(id, serializedPattern);
		Pattern newPattern = new Pattern(id, serializedPattern, imageUrl, description);
		this.patternList.add(newPattern);
		commit();
		return newPattern;
	}
	
	private String generateImage(int id, String serializedPattern) {
		// TODO Auto-generated method stub
		return "/fakeimage.png";
	}

	private int generateId() {
		return PatternFilePersistance.newPatternId();
	}

	public void commit() {
		//TODO maybe delete file before rewriting it?
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try {
			fos = new FileOutputStream(this.patternFile);
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(this.patternList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) oos.close();
				if (fos != null) fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	//TODO add serializable construct!
	private static int newPatternId() {
		return patternId++;
	}

	@Override
	public List<Pattern> getPatterns() {
		return this.patternList;
	}

	@Override
	public Pattern changePatternDescription(int id, String newDescription) {
		ListIterator<Pattern> it = this.getPatterns().listIterator();
		
		while(it.hasNext()) {
			Pattern p = it.next();
			if (p.getId() == id){
				p.setDescription(newDescription);
				this.commit();
				return p;
			}
		}
		
		return null;
	}
}
