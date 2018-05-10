import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class Profile {

	private static final String ROOT = "/home/limafoxtrottango/Downloads/20100825-SchPaperRecData/SeniorR/m";
	private static final Integer NO_SENIOR_RESEARCHERS = 13;

	public static void main(String... args) throws IOException {
		ArrayList<HashMap<String, Double>> fv_list = new ArrayList<>();
		ArrayList<HashMap<String, Double>> all_up = new ArrayList<HashMap<String, Double>>();
		for (int i = 1; i <= NO_SENIOR_RESEARCHERS; i++) {
			System.out.println("For researcher m" + i);
			String researcher_path = ROOT + i;
			File dir = new File(researcher_path);
			File[] researcher_papers = dir.listFiles();
			for (File file : researcher_papers) {
				if (file.isDirectory()) {
					File[] paper_details = new File(file.getAbsolutePath())
							.listFiles();
					HashMap<String, Double> fv = new HashMap<>();
					ArrayList<HashMap<String, Double>> citations_fvs = new ArrayList<>();
					ArrayList<HashMap<String, Double>> references_fvs = new ArrayList<>();
					for (File f : paper_details) {
						if (f.isFile()) {
							// System.out.println(f.getAbsolutePath());
							// feature vector for that paper
							try {
								fv = readFeatureVector(f.getAbsolutePath());
							} catch (IOException e) {
								System.out.println("File not found: "
										+ f.getAbsolutePath());
								continue;
							}
						} else {
							if (f.getAbsolutePath().contains("Cits")) {
								// citation directory
								citations_fvs = readDirForFeatureVectors(f
										.getAbsolutePath());
							} else {
								// reference directory
								references_fvs = readDirForFeatureVectors(f
										.getAbsolutePath());
							}
						}
					}
					HashMap<String, Double> final_FV = getRawUserProfile(fv,
							citations_fvs, references_fvs);
					fv_list.add(final_FV);
				}
			}
			all_up.add(discountFV(fv_list));
			fv_list.clear();
			// matrix(all_up);
			// System.out.println(findCosineSim(all_up.get(0), all_up.get(0)));
		}
		candidatePapers(all_up);
	}

	/**
	 * implements equation 2 in the reference paper
	 * 
	 * @param fv
	 * @param fv_citations_list
	 * @param fv_references_list
	 */
	private static HashMap<String, Double> getRawUserProfile(
			final HashMap<String, Double> fv,
			final ArrayList<HashMap<String, Double>> fv_citations_list,
			final ArrayList<HashMap<String, Double>> fv_references_list) {
		// second term without sigma

		for (HashMap<String, Double> citation : fv_citations_list) {
			// find the cosine similarity b/w this citation's FV and paper's FV
			Double cosine = findCosineSim(fv, citation);
			for (Entry<String, Double> e : citation.entrySet()) {
				e.setValue(e.getValue() * cosine);
			}
		}
		// third term without sigma
		for (HashMap<String, Double> reference : fv_references_list) {
			// find the cosine similarity b/w this citation's FV and paper's FV
			Double cosine = findCosineSim(fv, reference);
			for (Entry<String, Double> e : reference.entrySet()) {
				e.setValue(e.getValue() * cosine);
			}
		}

		for (Entry<String, Double> e : fv.entrySet()) {
			String term = e.getKey();
			Double total_tf = e.getValue();
			for (HashMap<String, Double> citation : fv_citations_list) {
				Double corresponding_term_tf = (citation.get(term) == null) ? 0
						: citation.get(term);
				total_tf += corresponding_term_tf;
			}

			for (HashMap<String, Double> reference : fv_references_list) {
				Double corresponding_term_tf = (reference.get(term) == null) ? 0
						: reference.get(term);
				total_tf += corresponding_term_tf;
			}
			// System.out.println(total_tf);

			e.setValue(total_tf);
		}
		return fv;

	}

	/**
	 * method to find cosine similarity b/w two feature vectors
	 * 
	 * @param fv1
	 * @param fv2
	 * @return
	 */
	private static Double findCosineSim(final HashMap<String, Double> fv1,
			final HashMap<String, Double> fv2) {
		// System.out.println(fv2);
		double cosine = 0.0;
		for (Entry<String, Double> e : fv1.entrySet()) {
			Double val = (fv2.get(e.getKey()) == null) ? 0 : fv2
					.get(e.getKey()) * e.getValue();
			cosine += val;
		}
		return cosine;
	}

	/**
	 * Reads a directory and generate an array of feature vectors (directories
	 * here are for citations and references)
	 * 
	 * @param directory
	 * @return
	 */
	private static ArrayList<HashMap<String, Double>> readDirForFeatureVectors(
			final String directory) {
		File dir = new File(directory);
		File[] files_list = dir.listFiles();

		ArrayList<HashMap<String, Double>> fv_list = new ArrayList<>(); // each
																		// feature
																		// vector
																		// is
																		// stored
																		// as a
																		// hashmap

		for (File file : files_list) {
			if (file.isFile()) {
				String file_path = file.getAbsolutePath();
				try {
					fv_list.add(readFeatureVector(file_path));
				} catch (IOException e) {
					System.out.println("File not found at: " + file_path);
					continue;
				}
			}
		}
		return fv_list;
	}

	/**
	 * Reads feature vector of a given paper into a hashmap
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private static HashMap<String, Double> readFeatureVector(final String path)
			throws IOException {
		HashMap<String, Double> fv = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = "";
		while ((line = br.readLine()) != null) {
			try {
				fv.put(line.split(" ")[0],
						Double.parseDouble(line.split(" ")[1]));
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}
		}
		return fv;
	}

	private static void printUP(final HashMap<String, Double> fv) {
		for (Entry<String, Double> e : fv.entrySet()) {
			System.out.println(e.getKey() + ": " + e.getValue());
		}
	}

	private static HashMap<String, Double> discountFV(
			final ArrayList<HashMap<String, Double>> fv_list) {
		// first, scale
		double discount_factor = 0.9;
		int count = 0;
		HashSet<String> terms = new HashSet<>(); // holds the union of terms
		for (HashMap<String, Double> fv : fv_list) {
			for (Entry<String, Double> e : fv.entrySet()) {
				e.setValue(e.getValue() * Math.pow(discount_factor, count));
				terms.add(e.getKey());
			}
			count++;
		}

		// all papers are discounted at this point, now, simply add and
		// construct a FV
		// construct a union
		HashMap<String, Double> up = new HashMap<>();
		for (String term : terms) {
			double freq = 0;
			for (HashMap<String, Double> fv : fv_list) {
				freq += (fv.get(term) == null) ? 0 : fv.get(term);
			}
			up.put(term, freq);
			freq = 0;
		}

		printUP(up);
		return up;
	}

	private static void matrix(final ArrayList<HashMap<String, Double>> all_up) {
		double[][] mat = new double[all_up.size()][all_up.size()];

		for (int i = 0; i < all_up.size(); i++) {
			for (int j = 0; j < all_up.size(); j++) {
				mat[i][j] = findCosineSim(all_up.get(i), all_up.get(j));
				System.out.print(mat[i][j] + ", ");
			}
			System.out.println();
		}
	}

	private static void candidatePapers(
			final ArrayList<HashMap<String, Double>> all_up) throws IOException {
		System.out.println(all_up.size());
		ArrayList<HashMap<String, Double>> fv_cp = new ArrayList<>();
		String path = "/home/limafoxtrottango/Downloads/20100825-SchPaperRecData/RecCandidatePapersFV";
		File dir = new File(path);
		File[] candidate_papers = dir.listFiles();
		for (File file : candidate_papers) {
			fv_cp.add(readFeatureVector(file.getAbsolutePath()));
		}
		for (int i = 0; i < all_up.size(); i++) {
			for (int j = 0; j < fv_cp.size(); j++) {
				System.out.print(findCosineSim(all_up.get(i), fv_cp.get(j))
						+ ", ");
			}
			System.out.println();
		}
	}
}

