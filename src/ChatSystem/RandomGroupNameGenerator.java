package ChatSystem;

import java.util.Random;

public class RandomGroupNameGenerator {

	private static String[] suff = new String[] { "Kickers", "Falcons", "Waterballs", "Cyborgs", "Commando Squad",
			"Geckos", "Busters", "Soldiers", "Clintons", "Racers", "Predators", "Fighter", "Monsters" };
	private static String[] pre = new String[] { "Red", "New York", "Awesome", "Incredible", "Marvelous", "Quicksilver",
			"Thunder", "Night", "Xtreme" };

	public static String generate() {
		return getRand(pre) + " " + getRand(suff);
	}

	private static String getRand(String[] values) {
		return values[new Random().nextInt(values.length)];
	}

}
