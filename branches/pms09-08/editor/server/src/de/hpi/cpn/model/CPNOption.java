package de.hpi.cpn.model;


public class CPNOption
{
   private String name;
   private CPNValue value;

   public String getName()
   {
      return this.name;
   }

   public void setName(String _name)
   {
      this.name = _name;
   }

   public CPNValue getValue()
   {
      return value;
   }

   public void setValue(CPNValue _value)
   {
      this.value = _value;
   }
   
   public CPNOption(String _name, String _value, boolean valuetype)
   {
	   this.name = _name;
	   this.value = new CPNValue(_value, valuetype);
   }
   
}