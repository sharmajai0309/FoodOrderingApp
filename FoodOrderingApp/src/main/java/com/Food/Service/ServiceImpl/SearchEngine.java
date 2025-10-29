package com.Food.Service.ServiceImpl;


import java.util.*;

public class SearchEngine {

    // Enhanced food dictionary with categories
    private static final Set<String> FOOD_DICTIONARY = new HashSet<>(Arrays.asList(
            // Meats & Proteins
            "chicken", "mutton", "fish", "prawn", "shrimp", "egg", "eggs", "paneer", "tofu",

            // Fast Food
            "pizza", "burger", "sandwich", "fries", "nuggets", "hotdog", "frankie", "wrap",

            // Indian Cuisine
            "biryani", "pulao", "curry", "sabzi", "dal", "paneer", "naan", "roti", "paratha",
            "chapati", "tandoori", "tikka", "masala", "butter", "korma", "vindaloo", "kebab",

            // Italian Cuisine
            "pasta", "spaghetti", "lasagna", "ravioli", "risotto", "alfredo", "carbonara",

            // Chinese & Asian
            "noodles", "friedrice", "manchurian", "sushi", "ramen", "dumpling", "springroll",

            // Mexican
            "taco", "burrito", "quesadilla", "nachos", "salsa", "guacamole",

            // Beverages
            "coffee", "tea", "juice", "smoothie", "milkshake", "lassi", "colddrink", "soda",

            // Desserts
            "icecream", "cake", "pastry", "chocolate", "cookie", "brownie", "gulabjamun",
            "rasgulla", "jalebi", "barfi",

            // Common Attributes
            "spicy", "sweet", "sour", "hot", "cold", "fresh", "fried", "grilled", "baked",
            "steamed", "boiled", "roasted",

            // Categories
            "vegetarian", "nonveg", "vegan", "glutenfree", "healthy", "organic"
    ));

    // Common spelling mistakes mapping
    private static final Map<String, String> COMMON_MISTAKES = createCommonMistakesMap();

    // Phonetic similar characters
    private static final Map<Character, Set<Character>> PHONETIC_SIMILAR = createPhoneticMap();

    private static Map<String, String> createCommonMistakesMap() {
        Map<String, String> mistakes = new HashMap<>();
        mistakes.put("chiccken", "chicken");
        mistakes.put("chiken", "chicken");
        mistakes.put("chickn", "chicken");
        mistakes.put("piza", "pizza");
        mistakes.put("pizzza", "pizza");
        mistakes.put("burgur", "burger");
        mistakes.put("burgar", "burger");
        mistakes.put("biriyani", "biryani");
        mistakes.put("briyani", "biryani");
        mistakes.put("biran", "biryani");
        mistakes.put("panner", "paneer");
        mistakes.put("paneer", "paneer");
        mistakes.put("veggie", "vegetable");
        mistakes.put("veg", "vegetarian");
        mistakes.put("nonveg", "nonvegetarian");
        mistakes.put("spicey", "spicy");
        mistakes.put("spyci", "spicy");
        mistakes.put("noodels", "noodles");
        mistakes.put("noodels", "noodles");
        mistakes.put("cofee", "coffee");
        mistakes.put("coffe", "coffee");
        mistakes.put("choclate", "chocolate");
        mistakes.put("choclates", "chocolate");
        mistakes.put("icecreame", "icecream");
        mistakes.put("icecrem", "icecream");
        return mistakes;
    }

    private static Map<Character, Set<Character>> createPhoneticMap() {
        Map<Character, Set<Character>> phoneticMap = new HashMap<>();

        phoneticMap.put('c', new HashSet<>(Arrays.asList('k', 's')));
        phoneticMap.put('k', new HashSet<>(Arrays.asList('c')));
        phoneticMap.put('s', new HashSet<>(Arrays.asList('c', 'z')));
        phoneticMap.put('z', new HashSet<>(Arrays.asList('s')));
        phoneticMap.put('i', new HashSet<>(Arrays.asList('e', 'y')));
        phoneticMap.put('e', new HashSet<>(Arrays.asList('i', 'a')));
        phoneticMap.put('a', new HashSet<>(Arrays.asList('e', 'o')));
        phoneticMap.put('o', new HashSet<>(Arrays.asList('a', 'u')));
        phoneticMap.put('u', new HashSet<>(Arrays.asList('o')));
        phoneticMap.put('g', new HashSet<>(Arrays.asList('j')));
        phoneticMap.put('j', new HashSet<>(Arrays.asList('g')));

        return phoneticMap;
    }

    public String searchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return keyword;
        }

        String inputWord = keyword.toLowerCase().trim();

        // Step 1: Quick common mistakes check
        if (COMMON_MISTAKES.containsKey(inputWord)) {
            return COMMON_MISTAKES.get(inputWord);
        }

        // Step 2: Check if exact match exists
        if (FOOD_DICTIONARY.contains(inputWord)) {
            return inputWord;
        }

        // Step 3: Multi-level matching
        CorrectionResult result = findBestCorrection(inputWord);

        return result.getCorrectedWord();
    }

    private CorrectionResult findBestCorrection(String inputWord) {
        List<CorrectionResult> candidates = new ArrayList<>();

        // Strategy 1: Direct character distance
        candidates.addAll(findByLevenshteinDistance(inputWord));

        // Strategy 2: Phonetic similarity
        candidates.addAll(findByPhoneticSimilarity(inputWord));

        // Strategy 3: Common pattern matching
        candidates.addAll(findByCommonPatterns(inputWord));

        // Strategy 4: Word segmentation (for compound words)
        candidates.addAll(findByWordSegmentation(inputWord));

        // Select best candidate
        return selectBestCandidate(candidates, inputWord);
    }

    private List<CorrectionResult> findByLevenshteinDistance(String inputWord) {
        List<CorrectionResult> results = new ArrayList<>();

        for (String word : FOOD_DICTIONARY) {
            int distance = calculateLevenshteinDistance(inputWord, word);
            if (distance <= 3) {
                results.add(new CorrectionResult(word, distance, CorrectionStrategy.LEVENSHTEIN));
            }
        }

        // Sort by distance and limit results
        results.sort(Comparator.comparingInt(CorrectionResult::getDistance));
        return results.subList(0, Math.min(results.size(), 5));
    }

    private List<CorrectionResult> findByPhoneticSimilarity(String inputWord) {
        List<CorrectionResult> results = new ArrayList<>();

        for (String word : FOOD_DICTIONARY) {
            int distance = calculatePhoneticDistance(inputWord, word);
            if (distance <= 2) {
                results.add(new CorrectionResult(word, distance, CorrectionStrategy.PHONETIC));
            }
        }

        results.sort(Comparator.comparingInt(CorrectionResult::getDistance));
        return results.subList(0, Math.min(results.size(), 3));
    }

    private List<CorrectionResult> findByCommonPatterns(String inputWord) {
        List<CorrectionResult> results = new ArrayList<>();

        // Check for double letter patterns (e.g., "chiccken" -> "chicken")
        if (hasDoubleLetters(inputWord)) {
            String simplified = simplifyDoubleLetters(inputWord);
            if (FOOD_DICTIONARY.contains(simplified)) {
                results.add(new CorrectionResult(simplified, 1, CorrectionStrategy.PATTERN));
            }
        }

        // Check for missing vowels
        if (hasMissingVowels(inputWord)) {
            String withVowels = addMissingVowels(inputWord);
            if (FOOD_DICTIONARY.contains(withVowels)) {
                results.add(new CorrectionResult(withVowels, 1, CorrectionStrategy.PATTERN));
            }
        }

        return results;
    }

    private List<CorrectionResult> findByWordSegmentation(String inputWord) {
        List<CorrectionResult> results = new ArrayList<>();

        // Try to split compound words (e.g., "friedrice" -> "fried rice")
        for (int i = 1; i < inputWord.length(); i++) {
            String part1 = inputWord.substring(0, i);
            String part2 = inputWord.substring(i);

            if (FOOD_DICTIONARY.contains(part1) && FOOD_DICTIONARY.contains(part2)) {
                String corrected = part1 + " " + part2;
                results.add(new CorrectionResult(corrected, 0, CorrectionStrategy.SEGMENTATION));
            }
        }

        return results;
    }

    private CorrectionResult selectBestCandidate(List<CorrectionResult> candidates, String original) {
        if (candidates.isEmpty()) {
            return new CorrectionResult(original, Integer.MAX_VALUE, CorrectionStrategy.NONE);
        }

        CorrectionResult best = candidates.get(0);
        for (CorrectionResult candidate : candidates) {
            if (candidate.getDistance() < best.getDistance()) {
                best = candidate;
            }
        }

        return best;
    }

    // Advanced Levenshtein with phonetic consideration
    private int calculateLevenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) dp[i][0] = i;
        for (int j = 0; j <= len2; j++) dp[0][j] = j;

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = getAdvancedSubstitutionCost(s1.charAt(i - 1), s2.charAt(j - 1));
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[len1][len2];
    }

    private int calculatePhoneticDistance(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        int distance = 0;

        for (int i = 0; i < maxLen; i++) {
            char c1 = i < s1.length() ? s1.charAt(i) : ' ';
            char c2 = i < s2.length() ? s2.charAt(i) : ' ';

            if (c1 != c2 && !arePhoneticallySimilar(c1, c2)) {
                distance++;
            }
        }

        return distance;
    }

    private int getAdvancedSubstitutionCost(char c1, char c2) {
        if (c1 == c2) return 0;
        if (arePhoneticallySimilar(c1, c2)) return 1;
        return 2;
    }

    private boolean arePhoneticallySimilar(char c1, char c2) {
        Set<Character> similar1 = PHONETIC_SIMILAR.get(c1);
        Set<Character> similar2 = PHONETIC_SIMILAR.get(c2);

        return (similar1 != null && similar1.contains(c2)) ||
                (similar2 != null && similar2.contains(c1));
    }

    private boolean hasDoubleLetters(String word) {
        for (int i = 0; i < word.length() - 1; i++) {
            if (word.charAt(i) == word.charAt(i + 1)) {
                return true;
            }
        }
        return false;
    }

    private String simplifyDoubleLetters(String word) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (i == 0 || word.charAt(i) != word.charAt(i - 1)) {
                sb.append(word.charAt(i));
            }
        }
        return sb.toString();
    }

    private boolean hasMissingVowels(String word) {
        String vowels = "aeiou";
        int consonantCount = 0;

        for (char c : word.toCharArray()) {
            if (vowels.indexOf(c) == -1) {
                consonantCount++;
            }
        }

        return consonantCount > word.length() * 0.7;
    }

    private String addMissingVowels(String word) {
        // Simple vowel insertion
        return word.replace("brgr", "burger")
                .replace("pzz", "pizza")
                .replace("chckn", "chicken");
    }

    // Helper classes
    private enum CorrectionStrategy {
        LEVENSHTEIN, PHONETIC, PATTERN, SEGMENTATION, NONE
    }

    private static class CorrectionResult {
        private final String correctedWord;
        private final int distance;
        private final CorrectionStrategy strategy;

        public CorrectionResult(String correctedWord, int distance, CorrectionStrategy strategy) {
            this.correctedWord = correctedWord;
            this.distance = distance;
            this.strategy = strategy;
        }

        public String getCorrectedWord() { return correctedWord; }
        public int getDistance() { return distance; }
        public CorrectionStrategy getStrategy() { return strategy; }
    }

    // Test method
//    public static void main(String[] args) {
//        SearchEngine engine = new SearchEngine();
//
//        String[] testCases = {
//                "chiccken", "piza", "burgur", "biriyani", "panner",
//                "spicey", "noodels", "cfee", "choclate", "icecreame",
//                "friedrice", "utton", "veggie", "nonveg", "perfect"
//        };
//
//        System.out.println("=== Advanced Spell Correction ===");
//        for (String test : testCases) {
//            String result = engine.searchKeyword(test);
//            System.out.printf("%-12s -> %-12s\n", test, result);
//        }
//    }
}