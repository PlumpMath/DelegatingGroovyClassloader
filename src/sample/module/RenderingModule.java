package sample.module;

import java.util.Map;

public interface RenderingModule {

	public String render();

	public Map<String, String> metadata();

	public void metadata(Map<String, String> md);

}// END
