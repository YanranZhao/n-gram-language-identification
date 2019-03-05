import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class ModelAdvanced extends Model {
	public float[][] enBigramFreq = new float[27][28];
	public float[][] enBigramProb = new float[27][27];

	public float[][] frBigramFreq = new float[27][28];
	public float[][] frBigramProb = new float[27][27];

	public float[][] esBigramFreq = new float[27][28];
	public float[][] esBigramProb = new float[27][27];
	
	//space--32; Apostrophe--39
	public int space = 32;
	//public int Apostrophe = 39;
	
	public void trainingModel(int inputType, String [] fileName) {
		PrintWriter pw = null;
		Scanner sc = null;
		unigramFreq = new float[26];
		unigramProb = new float[26];
		bigramFreq = new float[27][28];
		bigramProb = new float[27][27];
		uniTotal = 0;
		biTotal = 0;

		try {
			for (int k = 0; k < 2; k++) {
				sc = new Scanner(new FileInputStream(fileName[k]));
				String line;

				while (sc.hasNextLine()) {
					line = sc.nextLine().trim().toLowerCase();
					if (!line.isEmpty()) {
						for (int i = 0; i < line.length(); i++) {
							if (isLetter(line.charAt(i))) {
								int index = getIndex(line.charAt(i));
								unigramFreq[index]++;
								uniTotal++;
							}
							if(isValid(line.charAt(i))) {
								for (int j = i + 1; j < line.length(); j++) {
									int index1 = getIndex(line.charAt(i));
									if (isValid(line.charAt(j))) {
										int index2 = getIndex(line.charAt(j));
										bigramFreq[index1][index2]++;
										bigramFreq[index1][bigramFreq.length]++;
										biTotal++;
										break;
									}
								}
							}
						}
					}
				}
				System.out.println(fileName[k] + " was read");
				/*System.out.println("uniTotal: " + uniTotal);
				System.out.println("biTotal: " + biTotal);*/
			}

			addingSmooth();

			/*System.out.println("uniTotal: " + uniTotal);
			System.out.println("biTotal: " + biTotal);*/
			// displayArrayFreq(unigramFreq, bigramFreq);
			calculateProb();
			// displayArrayProb(unigramProb, bigramProb);
			copyArray(inputType);
			//displayArrayProb(frUnigramProb, frBigramProb);
			outputModel(inputType);
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Problem opening files.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isValid(char c) {
		int value = (int) c;
		if (value >= 97 && value <= 122 || value == space)
			return true;
		else
			return false;
	}
	
	public int getIndex(char c) {
		int value = (int) c;
		if (value >= 97 && value <= 122)
			value = (int) (c - 'a');
		else
			value = 26;
		return value;
	}
	
	public char convert(int num) {
		char letter;
		if (num == 26)
			letter = (char) (space);
		else
			letter = (char) (num + 97);
		return letter;
	}

	public void outputModel(int inputType) {
		// unigramFR.txt, bigramFR.txt, unigramEN.txt, bigramEN.txt, unigramOT.txt,
		// bigramOT.txt

		PrintWriter pw1 = null;
		PrintWriter pw2 = null;
		String[] fileName = { "unigramEN.txt", "bigramEN.txt", "unigramFR.txt", "bigramFR.txt", "unigramOT.txt",
				"bigramOT.txt" };
		try {
			if (inputType == 1) {
				pw1 = new PrintWriter(new FileOutputStream(fileName[0]));
				System.out.println(fileName[0] + " was created");
				for (int i = 0; i < enUnigramProb.length; i++) {
					char letter = (char) (i + 97);
					pw1.println("P(" + letter + ") = " + enUnigramProb[i]);
				}
				pw2 = new PrintWriter(new FileOutputStream(fileName[1]));
				System.out.println(fileName[1] + " was created\n");
				for (int i = 0; i < enBigramProb.length; i++) {
					for (int j = 0; j < enBigramProb[i].length; j++) {
						char row = convert(i);
						char column = convert(j);
						pw2.println("P(" + column + "|" + row + ") = " + enBigramProb[i][j]);
					}
				}
			} else if (inputType == 2) {
				pw1 = new PrintWriter(new FileOutputStream(fileName[2]));
				System.out.println(fileName[2] + " was created");
				for (int i = 0; i < frUnigramProb.length; i++) {
					char letter = (char) (i + 97);
					pw1.println("P(" + letter + ") = " + frUnigramProb[i]);
				}
				pw2 = new PrintWriter(new FileOutputStream(fileName[3]));
				System.out.println(fileName[3] + " was created\n");
				for (int i = 0; i < frBigramProb.length; i++) {
					for (int j = 0; j < frBigramProb[i].length; j++) {
						char row = convert(i);
						char column = convert(j);
						pw2.println("P(" + column + "|" + row + ") = " + frBigramProb[i][j]);
					}
				}
			} else if (inputType == 3) {
				pw1 = new PrintWriter(new FileOutputStream(fileName[4]));
				System.out.println(fileName[4] + " was created");
				for (int i = 0; i < esUnigramProb.length; i++) {
					char letter = (char) (i + 97);
					pw1.println("P(" + letter + ") = " + esUnigramProb[i]);
				}
				pw2 = new PrintWriter(new FileOutputStream(fileName[5]));
				System.out.println(fileName[5] + " was created\n");
				for (int i = 0; i < esBigramProb.length; i++) {
					for (int j = 0; j < esBigramProb[i].length; j++) {
						char row = convert(i);
						char column = convert(j);
						pw2.println("P(" + column + "|" + row + ") = " + esBigramProb[i][j]);
					}
				}
			} else
				System.out.println("Wrong type, input again");
			pw1.close();
			pw2.close();
		} catch (FileNotFoundException e) {
			System.out.println("Problem creating files.");
			System.exit(0);
		}
	}
	
	public void copyArray(int inputType) {
		if (inputType == 1) {
			enUnigramProb = Arrays.copyOf(unigramProb, unigramProb.length);
			for (int i = 0; i < bigramProb.length; i++) {
				enBigramProb[i] = Arrays.copyOf(bigramProb[i], bigramProb[i].length);
			}
		} else if (inputType == 2) {
			frUnigramProb = Arrays.copyOf(unigramProb, unigramProb.length);
			for (int i = 0; i < bigramProb.length; i++) {
				frBigramProb[i] = Arrays.copyOf(bigramProb[i], bigramProb[i].length);
			}
		} else if (inputType == 3) {
			esUnigramProb = Arrays.copyOf(unigramProb, unigramProb.length);
			for (int i = 0; i < bigramProb.length; i++) {
				esBigramProb[i] = Arrays.copyOf(bigramProb[i], bigramProb[i].length);
			}
		} else
			System.out.println("Wrong type, input again");
	}
	
	
	
	public void testingSentence(String testingFile) {
		PrintWriter pw = null;
		Scanner sc = null;
		float[] unigram; // 0-English, 1-French, 2-Spanish
		float[] bigram; // 0-English, 1-French, 2-Spanish
		String line;
		int count = 0;
		try {
			sc = new Scanner(new FileInputStream(testingFile));

			while (sc.hasNextLine()) {
				line = sc.nextLine().trim().toLowerCase();

				if (!line.isEmpty()) {
					count++;
					pw = new PrintWriter(new FileOutputStream("output" + count + ".txt"));
					unigram = new float[3];
					bigram = new float[3];
					pw.println("UNIGRAM MODEL:");
					for (int i = 0; i < line.length(); i++) {
						if (isLetter(line.charAt(i))) {
							int index = getIndex(line.charAt(i));
							unigram[0] += Math.log10(enUnigramProb[index]);
							unigram[1] += Math.log10(frUnigramProb[index]);
							unigram[2] += Math.log10(esUnigramProb[index]);
							pw.println("\nUNIGRAM:" + line.charAt(i));
							pw.println("FRENCH: P(" + line.charAt(i) + ") = " + frUnigramProb[index]
									+ " ==> log prob of sentence so far: " + unigram[1]);
							pw.println("ENGLISH: P(" + line.charAt(i) + ") = " + enUnigramProb[index]
									+ " ==> log prob of sentence so far: " + unigram[0]);
							pw.println("OTHER: P(" + line.charAt(i) + ") = " + esUnigramProb[index]
									+ " ==> log prob of sentence so far: " + unigram[2]);
						}
					}
					pw.flush();
					pw.println("\nAccording to the unigram model, the sentence is in " + checkLanguage(unigram));
					pw.flush();
					pw.println("------------------------------");
					pw.println("BIGRAM MODEL:");

					for (int i = 0; i < line.length(); i++) {
						if (isValid(line.charAt(i))) {
							int index = getIndex(line.charAt(i));
							for (int j = i + 1; j < line.length(); j++) {
								if (isValid(line.charAt(j))) {
									int index2 = getIndex(line.charAt(j));
									bigram[0] += Math.log10(enBigramProb[index][index2]);
									bigram[1] += Math.log10(frBigramProb[index][index2]);
									bigram[2] += Math.log10(esBigramProb[index][index2]);
									pw.println("\nBIGRAM:" + line.charAt(i) + line.charAt(j));
									pw.println("FRENCH: P(" + line.charAt(j) + "|" + line.charAt(i) + ") = "
											+ frBigramProb[index][index2] + " ==> log prob of sentence so far: "
											+ bigram[1]);
									pw.println("ENGLISH: P(" + line.charAt(j) + "|" + line.charAt(i) + ") = "
											+ enBigramProb[index][index2] + " ==> log prob of sentence so far: "
											+ bigram[0]);
									pw.println("OTHER: P(" + line.charAt(j) + "|" + line.charAt(i) + ") = "
											+ esBigramProb[index][index2] + " ==> log prob of sentence so far: "
											+ bigram[2]);
									break;
								}
							}
						}
					}
					pw.println("\nAccording to the bigram model, the sentence is in " + checkLanguage(bigram));
					pw.flush();
				}

			}
			sc.close();
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Problem opening files.");
			System.out.println("Program will terminate.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
