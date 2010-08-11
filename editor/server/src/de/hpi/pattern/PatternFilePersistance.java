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

import org.json.JSONArray;
import org.json.JSONString;

/**
 * Stores pattern serialized in a simple file per stencilset. Not safe for multiple user environment, 
 * because Dirty Reads, Non-repeatable reads and alike are all possible and not prevented.
 * This is a transitional implementation until database support is implemented.
 * 
 * @author Kai Höwelmeyer <kai.hoewelmeyer@student.hpi.uni-potsdam.de>
 *
 */
public class PatternFilePersistance implements PatternPersistanceProvider, JSONString {
	private static int patternId = 0;
	
	private final String ssNameSpace;
	private File patternFile;
	private ArrayList<Pattern> patternList = new ArrayList<Pattern>();
	
	@SuppressWarnings("unchecked")
	public PatternFilePersistance(String ssNameSpace, String baseDir) {
		this.ssNameSpace = ssNameSpace;
		this.patternFile = new File(baseDir + "/" + this.ssNameSpace.hashCode() + ".patternStore");
		
		this.loadPattern();
		this.correctPatternRepos();
	}

	private void correctPatternRepos() {
		ListIterator<Pattern> it = this.patternList.listIterator();
		while(it.hasNext()) {
			it.next().setRepos(this);
		}		
	}

	private void loadPattern() {
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
		} finally {
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
	public void removePattern(Pattern p) {
		ListIterator<Pattern> it = this.patternList.listIterator();
		while(it.hasNext()) {
			Pattern currentPattern = it.next();
			if(currentPattern.getId() == p.getId()) { //TODO implement equals
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
	public Pattern setPattern(Pattern p) {
		if (p.isNew()) {
			return this.addPattern(p);
		} else {
			return this.replacePattern(p); 
		}
	}
	
	private Pattern addPattern(Pattern p) {
		this.patternList.add(p); //TODO clone to prevent influence from outside
		p.setRepos(this);
		this.commit();
		return p;
	}

	private Pattern replacePattern(Pattern p) {
		
		ListIterator<Pattern> it = this.patternList.listIterator();
		
		while(it.hasNext()){
			Pattern currentPattern = it.next();
			if (currentPattern.getId() == p.getId()) { //implement equals in pattern
				it.set(p);
				p.setRepos(this); //new pattern has repos == null
				this.commit();
				break;
			}
		}
		return p;
		
	}

	private String generateImage(int id, String serializedPattern) {
		// TODO Auto-generated method stub
		return "/fakeimage.png";
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
	public List<Pattern> getAll() {
		return this.patternList;
	}

	@Override
	public String toJSONString() {
		JSONArray ja = new JSONArray();
		
		ListIterator<Pattern> it = this.patternList.listIterator();
		while(it.hasNext()) {
			ja.put(it.next().toJSONObject());
		}
		
		return ja.toString();
	}
}
