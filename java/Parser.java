import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

public class Parser {

	private static ArrayList<String> rawWords = new ArrayList<>();
	private static HashSet<String> stopWords = new HashSet<>();
	private static ArrayList<String> tokens = new ArrayList<>();

	public static void main(String... args) throws IOException {
		getStopWords();
		createFV("/home/limafoxtrottango/Downloads/ir2.pdf");
	}

	private static void getStopWords() throws IOException {
		final String filePath = "/home/limafoxtrottango/Desktop/IR/stopwords.txt";
		String line = "";
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		while ((line = br.readLine()) != null) {
			stopWords.add(line);
		}
	}

	private static void createFV(final String filePath) {
		String text = convertPDFToText(filePath);
		// first, remove all the stop-words
		String[] words = text.split("\\s+");
		for (String word : words) {
			rawWords.add(word);
		}
		ArrayList<String> al = new ArrayList<>();
		for (String rawWord : rawWords) {
			if (stopWords.contains(rawWord.toLowerCase())) {
				al.add(rawWord);
			}
		}
		rawWords.removeAll(al);

		for (String rawWord : rawWords) {
			String wordAfterRemovingPunchuation = rawWord.replaceAll(
					"[^a-zA-Z. ]", "").toLowerCase();
			if (wordAfterRemovingPunchuation.contains(".")) {
				String[] wordsAfterRemovingFullStop = wordAfterRemovingPunchuation
						.split(".");
				for (final String word : wordsAfterRemovingFullStop) {
					tokens.add(stemWords(wordAfterRemovingPunchuation));
				}
			} else {
				tokens.add(stemWords((wordAfterRemovingPunchuation)));
			}
		}
		frequency();
		
	}

	private static String stemWords(final String word) {
		Stemmer stemmer = new Stemmer();
		for (int i = 0; i < word.length(); i++) {
			stemmer.add(word.charAt(i));
		}
		stemmer.stem();
		return stemmer.toString();
	}

	private static String convertPDFToText(final String filePath) {
		byte[] pdfFileBytes = readFileAsBytes(filePath);
		PDDocument pdDoc;
		try {
			pdDoc = PDDocument.load(pdfFileBytes);
			PDFTextStripper reader = new PDFTextStripper();
			String pageText = reader.getText(pdDoc);
			pdDoc.close();
			return pageText;
		} catch (InvalidPasswordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] readFileAsBytes(String filePath) {
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(filePath);
			return IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void frequency() {
		HashMap<String, Integer> fv = new HashMap<>();
		for (final String token : tokens) {
			if (fv.get(token) == null) {
				int freq = 0;
				if (token != "") {
					for (final String token_check : tokens) {
						if (token_check.equals(token)) {
							freq++;
						}
					}

				}
				fv.put(token, freq);
				freq++;
			}
		}
		printFV(fv);
	}
	
	private static void printFV(final HashMap<String, Integer> fv) {
		for(final Entry<String, Integer> e : fv.entrySet()) {
			System.out.println(e.getKey() + ": " + e.getValue());
		}
	}
}
