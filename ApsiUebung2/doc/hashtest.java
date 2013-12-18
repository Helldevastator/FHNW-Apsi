for (int i = 0; i < count; i++) {
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