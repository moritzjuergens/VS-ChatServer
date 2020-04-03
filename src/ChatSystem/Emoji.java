package ChatSystem;

public enum Emoji {

	GRINNING_FACE("grinning-face.png", ":D");
	
	public String name;
	public String shortcut;
	
	Emoji(String name, String shortcut) {
		this.name = name;
		this.shortcut = shortcut;
	}
	
}
