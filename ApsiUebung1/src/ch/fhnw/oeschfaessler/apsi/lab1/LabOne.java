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

	private int collisionsFound = 0;
	private int nCollisions;
	private int lastCombination = -1;
	private int blockSize = 1024;
	private String templateOriginal;
	private String templateFake;
	private boolean useRandom = false;
	private final Random rand = new Random();
	
	private HashMap<Integer, Integer> hashesOriginal = new HashMap<>();
	private HashMap<Integer, Integer> hashesFake = new HashMap<>();
	private ArrayList<Integer> collisions = new ArrayList<>();
	private HashMap<Integer, ArrayList<String>> map = new HashMap<>();

	public static void main(String[] args) {
		LabOne app = new LabOne();
		
		app.setOriginalTemplate("templateOriginal.txt");
		app.setFakeTemplate("templateFake.txt");
		app.setOptions("options.ini");
		
		app.setRandomUsage(true);
		
		app.setBlockSize(1000);
		
		app.setExpectedCollisionCount(1);
		
		app.createAllVariation();
	}
	
	public boolean setOptions(String filename) {
		boolean done = true;
		try {
			Scanner in = new Scanner(new File(filename));
			int index = 0;
			while (in.hasNextLine()) {
				ArrayList<String> arrayList = new ArrayList<>();
				String line = in.nextLine();
				arrayList.add(line.substring(1, line.indexOf('"', 1)));
				arrayList.add(line.substring(line.indexOf('"', 1) + 3, line.length() - 1));
				map.put(index++, arrayList);
			}
			in.close();
		} catch (FileNotFoundException e) { done = false; }
		return done;
	}

	public void setExpectedCollisionCount(int count) {
		nCollisions = count;
	}
	
	public void setBlockSize(int size) {
		blockSize = size;
	}
	
	public void setRandomUsage(boolean bool) {
		useRandom = bool;
	}

	public boolean setOriginalTemplate(String filename) {
		boolean done = true;
		try {
			templateOriginal = readFile(filename, StandardCharsets.UTF_8);
		} catch (IOException e) { done = false; }
		return done;
	}

	public boolean setFakeTemplate(String filename) {
		boolean done = true;
		try {
			templateFake = readFile(filename, StandardCharsets.UTF_8);
		} catch (IOException e) { done = false; }
		return done;
	}

	private String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	public void createAllVariation() {
		collisionsFound = 0;
		while (collisionsFound != nCollisions) {
			generateHashes();
			System.out.println("the cake is a lie");
			checkCollisions();
		}
	}
	
	private void generateHashes() {
		int fakeCombination = lastCombination, originalCombination = lastCombination;
		int hash;
		for (int i = 0; i < blockSize; i++) {
			if (useRandom) {
				do { originalCombination = rand.nextInt(); } while (hashesOriginal.containsValue(originalCombination));
				do { fakeCombination     = rand.nextInt(); } while (hashesFake.containsValue(fakeCombination));
			} else {
				originalCombination++;
				fakeCombination++;
			}
			hash = createVariation(originalCombination, templateOriginal);
			hashesOriginal.put(hash, originalCombination);
			hash = createVariation(fakeCombination, templateFake);
			hashesFake.put(hash, fakeCombination);
		}
	}
	
	private void checkCollisions() {
		int fakeCombination, originalCombination, hash;
		Iterator<Integer> it = hashesOriginal.keySet().iterator();
		while (it.hasNext()) {
			hash = it.next();
			if (hashesFake.containsKey(hash) && !collisions.contains(hash)) {
				collisionsFound++;
				collisions.add(hash);
				fakeCombination     = hashesFake.get(hash);
				originalCombination = hashesOriginal.get(hash);
				System.out.println("-----------------------------------");
				System.out.println(collisions.size() + "# collision hash: " + hash);
				System.out.println("hash count: "+hashesFake.size());
				System.out.println("Original Text: ");
				System.out.println(resolveCombination(originalCombination, templateOriginal));
				System.out.println("Fake Text: ");
				System.out.println(resolveCombination(fakeCombination, templateFake));
				checkSuccess(originalCombination, fakeCombination);
				System.out.println("-----------------------------------");
			}
		}
	}

	private void checkSuccess(int originalComb, int fakeComb) {
		int hash = createVariation(originalComb, templateOriginal);
		int hash2 = createVariation(fakeComb, templateFake);
		System.out.println("Success? "+ (hash == hash2 ? "YES" : "NO"));
	}
	
	private int createVariation(int combination, String template) {
		String text = resolveCombination(combination, template);
		return HashingMachine.createHash(text.getBytes());
	}
	
	private String resolveCombination(int combination, String template) {
		String text = new String(template);
		for (int i = 0; i < 32; i++) {
			text = text.replace("{#" + Integer.toString(i) + "}", map.get(i).get(combination & 1));
			combination >>>= 1;
		}
		return text;
	}

}
