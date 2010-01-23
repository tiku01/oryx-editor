package de.hpi.cpn.model;

import java.util.ArrayList;


import com.thoughtworks.xstream.XStream;

public class CPNOptions
{
   private ArrayList<CPNOption> options = new ArrayList<CPNOption>();

   public ArrayList<CPNOption> getOptions()
   {
      return this.options;
   }
   public void setOptions(ArrayList<CPNOption> _options)
   {
      this.options = _options;
   }
   public void addOption(CPNOption _option)
   {
      this.options.add(_option);
   }
   public void removeOption(CPNOption _option)
   {
      this.options.remove(_option);
   }
   public CPNOption getOption( int i)
   {
      return (CPNOption)this.options.get(i);
   }
   
   public CPNOptions()
   {
	  setOptions(this.defaultOptions());
   }
   
   private ArrayList<CPNOption> defaultOptions()
   {
	   ArrayList<CPNOption> tempOptions = new ArrayList<CPNOption>();
	   
	   String[] tempOptionsArray = getOptionsArrayDictionary();
	   
	   tempOptions.add(new CPNOption(tempOptionsArray[0], tempOptionsArray[1], false));
	   for (int i = 2; i < tempOptionsArray.length; i = i + 2)
		   tempOptions.add(new CPNOption(tempOptionsArray[i], tempOptionsArray[i+1], true));
	   
	   return tempOptions;	
   }
   
   private String[] getOptionsArrayDictionary() // a pair is one option
   {
	   String[] temp = 
	   {
			   /* name */			/* value */
			   "outputdirectory", 	"&lt;same as model&gt;",
			   "repavg", 			"true",
			   "repciavg", 			"true",
			   "repcount", 			"false",
			   "repfirstval", 		"false",
			   "replastval", 		"false",
			   "repmax", 			"true",
			   "repmin", 			"true",
			   "repssquare",		"false",
			   "repssqdev", 		"false",
			   "repstddev", 		"true",
			   "repsum", 			"false",
			   "repvariance",	 	"false",
			   "avg", 				"true",
			   "ciavg", 			"false",
			   "count", 			"true",
			   "firstval", 			"false",
			   "lastval", 			"false",
			   "max", 				"true",
			   "min", 				"true",
			   "ssquare", 			"false",
			   "ssqdev", 			"false",
			   "stddev", 			"false",
			   "sum", 				"false",
			   "variance", 			"false",
			   "firstupdate", 	    "false",
			   "interval", 			"false",
			   "lastupdate", 		"false",
			   "untimedavg", 		"true",
			   "untimedciavg", 		"false",
			   "untimedcount", 		"true",
			   "untimedfirstval", 	"false",
			   "untimedlastval",	"false",
			   "untimedmax", 		"true",
			   "untimedmin", 		"true",
			   "untimedssquare", 	"false",
			   "untimedssqdev", 	"false",
			   "untimedstddev", 	"false",
			   "untimedsum", 		"true",
			   "untimedvariance", 	"false"
	   };
	   
	   return temp;   
   }
   
      
}