import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.Scanner;

public class NormalizerFile {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner sc = null;
		PrintWriter pw = null;
		String line;
		//es-fray-perico-y-su-borrico
		//es-el-principito
		sc = new Scanner(new FileInputStream("el-principito.txt"));
		pw = new PrintWriter(new FileOutputStream("es-el-principito.txt"));
		while (sc.hasNextLine()) {
			line = sc.nextLine();

			String convertedSentence = Normalizer.normalize(line, Normalizer.Form.NFD).replaceAll("\\p{M}", ""); //M
			pw.println(convertedSentence);

		}
		sc.close();
		pw.close();

/*		String c= "c¨®mo ";
		String convertedSentence = Normalizer.normalize(c, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		System.out.println(convertedSentence);*/
	}

}
