package ChatSystem;

public enum Emoji {

	GRINNING_FACE("grinning-face.png", ":D");
	BICEPS("Biceps.png", "biceps");
	BROGLE("broccoli.png", "broccoli");
	CIGARETTE("eger.png", "Eger");
	EXPLODINGHEAD("exploding.png", "explodinghead");
	FLOWER("Flower.png", "flower");
	HEART("Heart.png"; "<3");
	HEARTEYES("heart_shaped_eyes.png", "<3.<3");
	HEDGEHOG("hedgehog.png", "hedgehog");
	HUNDRED("hundred.png", "100");
	GOAT("Jeannine.png", "Jeannine");
	CAT("kissingcat.png", "cat");
	MONKEY("monkey.png", "monkey");
	MOON("moon.png", "moon");
	COOK("Moritz.png", "Moritz");
	NAUSEATED("nauseated.png", "nauseated");
	CALLME("Nico.png", "Nico");
	OTTER("otter.png", "otter");
	PEACH("peach.png", "butt");
	PIZZA("pizza.png", "pizza");
	POOP("poop.png", "poop");
	PRINCESS("princess.png", "princess");
	SPLASH("splash.png", "splash");
	OK("Timo.png", "Timo");
	TREX("t-rex.png", "trex");
	HEARTS("two-hearts.png", "<33");
	NAILS("Veli.png", "Veli");
	
	
	public String name;
	public String shortcut;
	
	Emoji(String name, String shortcut) {
		this.name = name;
		this.shortcut = shortcut;
	}
	
}
