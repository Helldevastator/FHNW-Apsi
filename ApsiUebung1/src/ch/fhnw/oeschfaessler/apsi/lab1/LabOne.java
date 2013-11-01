package ch.fhnw.oeschfaessler.apsi.lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class LabOne {

	private int nCollisions;
	private String templateOriginal = null;
	private String templateFake = null;
	
	private HashMap<Integer, Integer> hashesOriginal = new HashMap<>();
	private HashMap<Integer, Integer> hashesFake = new HashMap<>();
	private HashMap<Integer, ArrayList<String>> map = new HashMap<>();

	public static void main(String[] args) throws FileNotFoundException {
		LabOne app = new LabOne();
		app.setExpectedCollisionCount(3);
		app.setOriginalTemplate("templateOriginal.txt");
		app.setFakeTemplate("templateFake.txt");
		app.createAllVariation();
	}

	public LabOne() throws FileNotFoundException {
		fillMap();
	}

	public void setExpectedCollisionCount(int count) {
		nCollisions = count;
	}

	public boolean setOriginalTemplate(String filename) {
		boolean done = true;
		try {
			templateOriginal = readFile(filename, StandardCharsets.UTF_8);
		} catch (IOException e) {
			done = false;
		}
		return done;
	}

	public boolean setFakeTemplate(String filename) {
		boolean done = true;
		try {
			templateFake = readFile(filename, StandardCharsets.UTF_8);
		} catch (IOException e) {
			done = false;
		}
		return done;
	}

	private String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	private int createVariation(int combination, String file) {

		String tmpfile = new String(file);
		for (int i = 0; i < 32; i++) {
			tmpfile = tmpfile.replace("{#" + Integer.toString(i) + "}", map
					.get(i).get(combination & 1));
			combination >>>= 1;
		}

		return HashingMachine.createHash(tmpfile.getBytes());
	}

	public void createAllVariation() {
		int fakeCombination = 0;
		int originalCombination = 0;
		int collisionHash = 0;

		int combination = 0;

		int collisionsFound = 0;
		boolean useRandom = true;
		Random rand = new Random();

		while (collisionsFound != nCollisions) {
			for (int i = 0; i < 1024; i++) {
				int hash;

				if (useRandom) {
					// random combination
					do {
						combination = rand.nextInt();
					} while (this.hashesOriginal.containsValue(combination));

					hash = createVariation(combination, templateOriginal);
					this.hashesOriginal.put(hash, combination);

					// random combination
					do {
						combination = rand.nextInt();
					} while (this.hashesFake.containsValue(combination));
					hash = createVariation(combination, templateFake);
					this.hashesFake.put(hash, combination);

				} else {
					hash = createVariation(combination, templateOriginal);
					this.hashesOriginal.put(hash, combination);

					hash = createVariation(combination, templateFake);
					this.hashesFake.put(hash, combination);

					combination++;
				}
			}

			System.out.println("the cake is a lie");

			// check if there are collisions
			Iterator<Integer> it = hashesOriginal.keySet().iterator();
			while (it.hasNext()) {
				Integer hash = it.next();
				// collision found
				if (this.hashesFake.containsKey(hash)) {
					collisionsFound++;
					collisionHash = hash;
					fakeCombination = hashesFake.get(hash);
					originalCombination = hashesOriginal.get(hash);
					System.out.println("collision hash: " + collisionHash);
					System.out.println("original combination: "
							+ originalCombination);
					System.out.println("fake combination: " + fakeCombination);
					checkSuccess(originalCombination, fakeCombination);
					System.out.println("-----------------------------------");
				}
			}
		}

	}

	private void checkSuccess(int originalComb, int fakeComb) {
		int hash = createVariation(originalComb, templateOriginal);
		int hash2 = createVariation(fakeComb, templateFake);
		System.out.print("Success? ");
		System.out.println(hash == hash2 ? "YES" : "NO");
	}

	private void fillMap() throws FileNotFoundException {
		Scanner in = new Scanner(new File("options.ini"));
		int index = 0;
		while (in.hasNextLine()) {
			ArrayList<String> arrayList = new ArrayList<>();
			String line = in.nextLine();
			arrayList.add(line.substring(1, line.indexOf('"', 1)));
			arrayList.add(line.substring(line.indexOf('"', 1) + 3,
					line.length() - 1));
			map.put(index++, arrayList);
		}
	}
}
