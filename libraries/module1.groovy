package sample.module;

import sample.module.*;
import module.libraries.*;

public class SampleModule implements RenderingModule{
//
private HashMap<String,String> data = new HashMap<String,String>();

	public String render(){
	  StringBuilder sb = new StringBuilder();
	  sb 
	    .append(Tags.o_p())
	    .append(Tags.o_ul());
	  
	  Set<String> keys = data.keySet();
	  for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key =  iterator.next();
			sb
			  .append(Tags.o_li())
			  .append(key)
			  .append(Tags.c_li());
		}
		
	  sb
	    .append(Tags.c_ul())
	    .append(Tags.c_p());
	    
	  String result = sb.toString();
	  sb.setLength(0);
	  sb = null
	  return result;
	}

	public Map<String, String> metadata(){
	 return data;
	}

	public void metadata(Map<String, String> md){
	 data = md;
	}

}//END