package de.hpi.yawl.resourcing;

import java.util.ArrayList;

import de.hpi.yawl.FileWritingForYAWL;

public class DistributionSet implements FileWritingForYAWL {

	private ArrayList<ResourcingType> initialSetList = new ArrayList<ResourcingType>();
	

	public ArrayList<ResourcingType> getInitialSetList() {
		return initialSetList;
	}


	@Override
	public String writeToYAWL() {
		String s = "";
		s += "\t\t<distributionSet>\n";
        s += "\t\t\t<initialSet>\n";
        for(ResourcingType resource : initialSetList){
        	//SUBCLASS RESPONSIBILITY
        	if (resource instanceof Capability)
        		s += ((Capability)resource).writeAsMemberOfDistributionSetToYAWL();
        	else if (resource instanceof Participant)
        		s += ((Participant)resource).writeAsMemberOfDistributionSetToYAWL();
        	else if (resource instanceof Position)
        		s += ((Position)resource).writeAsMemberOfDistributionSetToYAWL();
        	else if (resource instanceof Role)
        		s += ((Role)resource).writeAsMemberOfDistributionSetToYAWL();
        }
        
        s += "\t\t\t</initialSet>\n";
        s += "\t\t\t</distributionSet>\n";
		return s;
	}

}
