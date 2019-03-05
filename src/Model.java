import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Model {

	public float[] unigramFreq;
	public float[] unigramProb;
	public float[][] bigramFreq;
	public float[][] bigramProb;
	public float uniTotal = 0;
	public float biTotal = 0;

	public float[] enUnigramFreq = new float[26];
	public float[] enUnigramProb = new float[26];
	public float[][] enBigramFreq = new float[26][27];
	public float[][] enBigramProb = new float[26][26];

	public float[] frUnigramFreq = new float[26];
	public float[] frUnigramProb = new float[26];
	public float[][] frBigramFreq = new float[26][27];
	public float[][] frBigramProb = new float[26][26];

	public float[] esUnigramFreq = new float[26];
	public float[] esUnigramProb = new float[26];
	public float[][] esBigramFreq = new float[26][27];
	public float[][] esBigramProb = new float[26][26];

	public void trainingModel(int inputType, String [] fileName) {
		PrintWriter pw = null;
		Scanner sc = null;
		unigramFreq = new float[26];
		unigramProb = new float[26];
		bigramFreq = new float[26][27];
		bigramProb = new float[26][26];
		uniTotal = 0;
		biTotal = 0;

		/*// 1:en, 2:fr, 3:sp
		int inputType = 2;//
		// en-moby-dick, en-the-little-prince, fr-le-petit-prince,
		// fr-vingt-mille-lieues-sous-les-mers
		// String[] fileName = { "en-moby-dick.txt", "en-the-little-prince.txt" };
		String[] fileName = { "fr-le-petit-prince.txt", "fr-vingt-mille-lieues-sous-les-mers.txt" };*/
		try {
			for (int k = 0; k < fileName.length ; k++) {
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

								for (int j = i + 1; j < line.length(); j++) {
									if (isLetter(line.charAt(j))) {
										int index2 = getIndex(line.charAt(j));
										bigramFreq[index][index2]++;
										bigramFreq[index][bigramFreq.length]++;
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
			//displayArrayFreq(unigramFreq, bigramFreq);
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

	public void calculateProb() {
		for (int i = 0; i < bigramFreq.length; i++) {
			for (int j = 0; j < bigramFreq[i].length - 1; j++)
				bigramProb[i][j] = bigramFreq[i][j] / bigramFreq[i][bigramFreq.length];
		}

		for (int i = 0; i < unigramFreq.length; i++)
			unigramProb[i] = unigramFreq[i] / uniTotal;
	}

	public void addingSmooth() {
		for (int i = 0; i < bigramFreq.length; i++) {
			for (int j = 0; j < bigramFreq[i].length - 1; j++)
				bigramFreq[i][j] += 0.5;
			bigramFreq[i][bigramFreq.length] += bigramFreq[i].length * 0.5;
		}
		biTotal += Math.pow(bigramFreq[0].length * 0.5, 2);

		for (int i = 0; i < unigramFreq.length; i++) {
			unigramFreq[i] += 0.5;
		}
		uniTotal += 26 * 0.5;
		//System.out.println(uniTotal);
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
						char row = (char) (i + 97);
						char column = (char) (j + 97);
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
						char row = (char) (i + 97);
						char column = (char) (j + 97);
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
						char row = (char) (i + 97);
						char column = (char) (j + 97);
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

	public boolean isLetter(char c) {
		int value = (int) c;
		if (value >= 97 && value <= 122)
			return true;
		else
			return false;
	}

	public int getIndex(char c) {
		int value = (int) (c - 'a');
		return value;
	}

	
	public String checkLanguage(float[] list) {
		float max = list[0];
		int index = -1;
		float result = 0;
		 for (int i = 0; i < list.length; i++) {
		        if (list[i] < 0) {
		            if (result == 0 || list[i] > result) {
		                result = list[i];
		                index = i;
		            }
		        }
		    }

		String s = "";
		switch (index) {
		case 0:
			s = "English";
			break;
		case 1:
			s = "French";
			break;
		case 2:
			s = "Spanish";
			break;
		default:
			s = "Invalid";
			break;
		}
		return s;
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
					pw.println(line);
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
						if (isLetter(line.charAt(i))) {
							int index = getIndex(line.charAt(i));
							for (int j = i + 1; j < line.length(); j++) {
								if (isLetter(line.charAt(j))) {
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
	
	public void displayArrayFreq(float[] unigramFreq, float[][] bigramFreq) {
		System.out.println();
		for (int i = 0; i < unigramFreq.length; i++) {
			char ch = (char) (i + 97);
			System.out.format("%-1c : %.1f\n", ch, unigramFreq[i]);
			// System.out.format("%-1c : %.5f\n", ch, unigramProb[i]);
		}

		char c = 'a';

		System.out.println();
		System.out.print("\t");
		for (int i = 0; i < bigramFreq[0].length; i++) {
			// System.out.print(" ");
			// System.out.printf("%-5c", c);
			System.out.print(c + "\t");
			// System.out.printf("%-10c",c );
			c++;
		}
		c = 'a';
		System.out.print("total");
		System.out.println();
		for (int i = 0; i < bigramFreq.length; i++) {
			System.out.print(c + "\t");
			c++;
			for (int j = 0; j < bigramFreq[i].length - 1; j++) {
				System.out.print(bigramFreq[i][j] + "\t");
				// System.out.printf("%5.1f", bigramFreq[i][j]);
				// System.out.printf("%10.5f", bigramProb[i][j]);
			}
			System.out.print(bigramFreq[i][bigramFreq[0].length]);
			// System.out.printf("%7.1f", bigramFreq[i][26]);
			System.out.println();
		}
	}

	public void displayArrayProb(float[] unigramProb, float[][] bigramProb) {
		System.out.println();
		// System.out.printf("%.5f",n);
		for (int i = 0; i < unigramProb.length; i++) {
			char ch = (char) (i + 97);
			// System.out.format("%-1c : %.5f\n", ch, unigramProb[i]);
			System.out.println(ch + " : " + unigramProb[i]);
		}

		char c = 'a';
		System.out.println();
		System.out.print("    ");
		for (int i = 0; i < bigramFreq[0].length; i++) {
			// System.out.print(" ");
			System.out.printf("%-10c", c);
			c++;
		}
		c = 'a';
		System.out.println();
		for (int i = 0; i < bigramProb.length; i++) {
			System.out.print(c + "");
			c++;
			for (int j = 0; j < bigramProb[i].length; j++) {
				System.out.printf("%10.5f", bigramProb[i][j]);
			}
			System.out.println();
		}
	}


}
