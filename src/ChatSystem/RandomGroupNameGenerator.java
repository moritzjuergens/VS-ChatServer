package ChatSystem;

import java.util.Random;

/**
 * Class is used to generate random group names. Random groupnames will be
 * displayed instead of group's id
 * 
 * @author timos
 *
 */
public class RandomGroupNameGenerator {

	private static String[] suff = new String[] { "Kickers", "Falcons", "Waterballs", "Cyborgs", "Commando Squad",
			"Geckos", "Busters", "Soldiers", "Clintons", "Racers", "Predators", "Fighter", "Monsters" };
	private static String[] pre = new String[] { "Red", "New York", "Awesome", "Incredible", "Marvelous", "Quicksilver",
			"Thunder", "Night", "Xtreme" };

	/**
	 * 
	 * @return returns a random groupname
	 */
	public static String generate() {
		return getRand(pre) + " " + getRand(suff);
	}

	/**
	 * 
	 * @param values String-Array
	 * @return returns a random String
	 */
	private static String getRand(String[] values) {
		return values[new Random().nextInt(values.length)];
	}

}
