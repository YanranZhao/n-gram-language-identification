public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		long startTime = System.nanoTime();
		
		String[] trainEN = { "t1.txt" };
		String[] trainFR = { "t2.txt"};
		String[] trainOT = { "t3.txt"};

		// 1:en, 2:fr, 3:sp

		Model model = new Model();
		model.trainingModel(1, trainEN);
		model.trainingModel(2, trainFR);
		model.trainingModel(3, trainOT);
		model.testingSentence("test.txt");
		
		
/*		ModelAdvanced madv = new ModelAdvanced();
		madv.trainingModel(1, trainEN);
		madv.trainingModel(2, trainFR);
		madv.trainingModel(3, trainOT);
		madv.testingSentence("testingSentence.txt");*/
		
		long endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns"); 
	}
}
