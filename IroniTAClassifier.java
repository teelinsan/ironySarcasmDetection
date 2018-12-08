import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.cache.FixIndexKernelCache;
import it.uniroma2.sag.kelp.kernel.cache.KernelCache;
import it.uniroma2.sag.kelp.kernel.cache.StripeKernelCache;
import it.uniroma2.sag.kelp.kernel.standard.LinearKernelCombination;
import it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel;
import it.uniroma2.sag.kelp.kernel.standard.PolynomialKernel;
import it.uniroma2.sag.kelp.kernel.standard.RbfKernel;
import it.uniroma2.sag.kelp.kernel.tree.SubSetTreeKernel;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.BinaryCSvmClassification;
import it.uniroma2.sag.kelp.learningalgorithm.classification.multiclassification.OneVsAllLearning;
import it.uniroma2.sag.kelp.predictionfunction.classifier.BinaryMarginClassifierOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier;
import it.uniroma2.sag.kelp.utils.JacksonSerializerWrapper;
import it.uniroma2.sag.kelp.utils.evaluation.BinaryClassificationEvaluator;
import it.uniroma2.sag.kelp.utils.evaluation.MulticlassClassificationEvaluator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IroniTAClassifier {
	public static void main(String[] args) throws Exception {

        //Parametri di settings per l'output Test
        boolean tuned = true;
        boolean ironyCorpus = true;
        float cIrony = 0.05f;
        float cSarcasm = 0.05f;




		// Reading the input parameters
		//String trainingSetFilePath = "data/dataset_ironia.klp";
        String trainingSetFilePath = "data/train_dataset.klp";


		float[] cs = {0.01f, 0.05f, 0.1f, 0.2f, 0.5f, 1, 5, 10};

		Label positiveLabel = new StringLabel("Irony");
        Label negativeLabel = new StringLabel("NOTIrony");


        // Read the training and test dataset
        Label sarcasmPositiveLabel = new StringLabel("Sarcasmo");

		SimpleDataset inputDatasetSet = new SimpleDataset();
        inputDatasetSet.populate(trainingSetFilePath);

        SimpleDataset sarcasmDataset = new SimpleDataset();

        for( Example ex : inputDatasetSet.getExamples()){
            if(ex.isExampleOf(positiveLabel)){
                sarcasmDataset.addExample(ex);
            }
        }



        SimpleDataset[] split = inputDatasetSet.split(0.8f);

        SimpleDataset trainingSet = split[0];
        SimpleDataset testSet = split[1];



		System.out.println("The training set is made of " + trainingSet.getNumberOfExamples() + " examples.");


		// print the number of train and test examples for each class
		for (Label l : trainingSet.getClassificationLabels()) {
			System.out.println("Positive training examples for the class " + l.toString() + " "
					+ trainingSet.getNumberOfPositiveExamples(l));
			System.out.println("Negative training examples for the class  " + l.toString() + " "
					+ trainingSet.getNumberOfNegativeExamples(l));
		}

		// calculating the size of the gram matrix to store all the examples
		int cacheSize = trainingSet.getNumberOfExamples();




        LinearKernelCombination combination = new LinearKernelCombination();



        float charweight = 1; //0.51711822f;

        float dplweight = 1; //0.48288178f;

        float ironySpecific = 1; //0.50409571f;

        float wordSpace = 1; //0.49590429f;

        float bowweight = 1; //0.50396867f;

        float bigramsweight = 1; //0.49603133f;

        if(ironyCorpus) {

            combination.addKernel(bowweight, new NormalizationKernel(new LinearKernel("bowIC")));
            combination.addKernel(bigramsweight, new NormalizationKernel(new LinearKernel("bowIC2gramSurface")));
            combination.addKernel(bigramsweight, new NormalizationKernel(new LinearKernel("bowIC3gramSurface")));

            combination.addKernel(bowweight, new NormalizationKernel(new LinearKernel("bowICBIN")));
            combination.addKernel(bigramsweight, new NormalizationKernel(new LinearKernel("bowICBIN2gramSurface")));
            combination.addKernel(bigramsweight, new NormalizationKernel(new LinearKernel("bowICBIN3gramSurface")));

            combination.addKernel(bowweight, new RbfKernel(1, new NormalizationKernel(new LinearKernel("densUnigram"))));
            combination.addKernel(bigramsweight, new RbfKernel(1, new NormalizationKernel(new LinearKernel("densBigram"))));
            combination.addKernel(bigramsweight, new RbfKernel(1, new NormalizationKernel(new LinearKernel("densTrigram"))));

        }

        combination.addKernel(charweight, new NormalizationKernel(new LinearKernel("bow5gramChar")));
        combination.addKernel(charweight, new NormalizationKernel(new LinearKernel("bow4gramChar")));
        combination.addKernel(charweight, new NormalizationKernel(new LinearKernel("bow3gramChar")));
        combination.addKernel(charweight, new NormalizationKernel(new LinearKernel("bow2gramChar")));

        //combination.addKernel(dplweight, new NormalizationKernel(new LinearKernel("bowDPL")));
        //combination.addKernel(dplweight, new RbfKernel(1, new NormalizationKernel(new LinearKernel("combDPL"))));



        combination.addKernel(ironySpecific, new NormalizationKernel(new LinearKernel("bowIronySpecific")));
        combination.addKernel(ironySpecific, new NormalizationKernel(new LinearKernel("bowIronySpecificA")));
        combination.addKernel(ironySpecific, new NormalizationKernel(new LinearKernel("bowIronySpecificS")));
        combination.addKernel(ironySpecific, new NormalizationKernel(new LinearKernel("bowIronySpecificV")));
        combination.addKernel(ironySpecific, new RbfKernel(1, new NormalizationKernel(new LinearKernel("VarMeanA"))));
        combination.addKernel(ironySpecific, new RbfKernel(1, new NormalizationKernel(new LinearKernel("VarMeanS"))));
        combination.addKernel(ironySpecific, new RbfKernel(1, new NormalizationKernel(new LinearKernel("VarMeanV"))));
        combination.addKernel(ironySpecific, new RbfKernel(1, new NormalizationKernel(new LinearKernel("VarMean"))));

        combination.addKernel(wordSpace, new RbfKernel(1, new NormalizationKernel(new LinearKernel("WSSurface"))));




        /*
        combination.addKernel(1, new NormalizationKernel(new LinearKernel("bowLemmi")));
        combination.addKernel(1, new NormalizationKernel(new LinearKernel("bowBigramLemmi")));
        combination.addKernel(1, new NormalizationKernel(new LinearKernel("bowBigramSurface")));
        */
        combination.addKernel(1, new RbfKernel(1, new NormalizationKernel(new LinearKernel("featPunt"))));




        // Setting the cache to speed up the computations

		KernelCache cache=new StripeKernelCache(inputDatasetSet);
        combination.setKernelCache(cache);

        int i;

        Map<Float, ArrayList<Float>> mappaC = new HashMap();
        Map<Float, ArrayList<Float>> mappaSarcasm = new HashMap();

        for (float c : cs) {
            mappaC.put(c, new ArrayList<Float>());
            mappaSarcasm.put(c, new ArrayList<Float>());
        }


        SimpleDataset trainingSetSarcasm = new SimpleDataset();
        SimpleDataset testSetSarcasm = new SimpleDataset();

        for(Example e: trainingSet.getExamples()){
            if(e.isExampleOf(positiveLabel))
                trainingSetSarcasm.addExample(e);
        }


        for(Example e: testSet.getExamples()){
            if(e.isExampleOf(positiveLabel))
                testSetSarcasm.addExample(e);
        }


        if (tuned == false) {

            PrintStream ps = new PrintStream("log.txt", "utf8");

            PrintStream outvalIrony = new PrintStream("OutputIrony.txt", "utf8");
            outvalIrony.println("C,F1-media,Sarcasm");

            PrintStream outvalSarcasm = new PrintStream("OutputSarcasm.txt", "utf8");
            outvalSarcasm.println("C,F1-media,Sarcasm");

            for (i = 0; i < 10; i++) {
                Random random = new Random();

                inputDatasetSet.shuffleExamples(random);

                split = inputDatasetSet.split(0.8f);
                trainingSet = split[0];
                testSet = split[1];


                trainingSetSarcasm = new SimpleDataset();
                testSetSarcasm = new SimpleDataset();

                for (Example e : trainingSet.getExamples()) {
                    if (e.isExampleOf(positiveLabel))
                        trainingSetSarcasm.addExample(e);
                }


                for (Example e : testSet.getExamples()) {
                    if (e.isExampleOf(positiveLabel))
                        testSetSarcasm.addExample(e);
                }


                ps.println(i + "-fold Validation");


                for (float c : cs) {


                    // Instantiate the SVM learning Algorithm.
                    BinaryCSvmClassification svmSolver = new BinaryCSvmClassification();
                    svmSolver.setLabel(positiveLabel);
                    //Set the kernel
                    svmSolver.setKernel(combination);
                    //Set the C parameter
                    svmSolver.setCn(c);
                    svmSolver.setCp(c);


                    BinaryCSvmClassification svmSolver2 = new BinaryCSvmClassification();
                    svmSolver2.setLabel(sarcasmPositiveLabel);
                    //Set the kernel
                    svmSolver2.setKernel(combination);
                    //Set the C parameter
                    svmSolver2.setCn(c);
                    svmSolver2.setCp(c);


                    //Learn and get the prediction function
                    svmSolver.learn(trainingSet);
                    //Selecting the prediction function
                    Classifier classifier = svmSolver.getPredictionFunction();

                    svmSolver2.learn(trainingSetSarcasm);
                    //Selecting the prediction function
                    Classifier classifier2 = svmSolver2.getPredictionFunction();


                    //Building the evaluation function
                    BinaryClassificationEvaluator evaluator = new BinaryClassificationEvaluator(positiveLabel);

                    BinaryClassificationEvaluator evaluator2 = new BinaryClassificationEvaluator(sarcasmPositiveLabel);

                    BinaryClassificationEvaluator evaluatorDefaultSarcasm = new BinaryClassificationEvaluator(sarcasmPositiveLabel);


                    for (Example ex : testSet.getExamples()) {
                        ClassificationOutput p = classifier.predict(ex);
                        evaluator.addCount(ex, p);

                        if (p.isClassPredicted(positiveLabel)) {
                            ClassificationOutput q = classifier2.predict(ex);
                            evaluatorDefaultSarcasm.addCount(ex, q);
                        } else {
                            BinaryMarginClassifierOutput defaultNoSarcasm = new BinaryMarginClassifierOutput(sarcasmPositiveLabel, -1);
                            evaluatorDefaultSarcasm.addCount(ex, defaultNoSarcasm);
                        }

                    }

                    for (Example ex : testSetSarcasm.getExamples()) {
                        ClassificationOutput p = classifier2.predict(ex);
                        evaluator2.addCount(ex, p);


                    }


                    ps.println("Ironia: C[" + c + "]F1 Score, Precision, Recall \t" + "\t" + evaluator.getF1() + "\t" + evaluator.getPrecision() + "\t" + evaluator.getRecall());
                    mappaC.get(c).add(evaluator.getF1());
                    mappaSarcasm.get(c).add(evaluatorDefaultSarcasm.getF1());
                    ps.println("Sarcasmo: C[" + c + "]F1 Score, Precision, Recall \t" + "\t" + evaluator2.getF1() + "\t" + evaluator2.getPrecision() + "\t" + evaluator2.getRecall());
                    ps.println("Sarcasmo Default: C[" + c + "]F1 Score, Precision, Recall \t" + "\t" + evaluatorDefaultSarcasm.getF1() + "\t" + evaluatorDefaultSarcasm.getPrecision() + "\t" + evaluatorDefaultSarcasm.getRecall());

                }

            }


            for (Float key : mappaC.keySet()) {
                ArrayList<Float> lisa = mappaC.get(key);
                float media = 0;
                float varianza = 0;
                for (Float f : lisa) {
                    media = media + f;
                }
                media = media / lisa.size();
                for (Float f : lisa) {
                    float diff = f - media;
                    varianza = varianza + (float) Math.pow(diff, 2);
                }
                varianza = varianza / (lisa.size() - 1);
                outvalIrony.println(key + "," + media + "," + varianza);
                ps.println("Irony: Media e Varianza per C pari a [" + key + "]: " + media + " " + varianza);
            }


            for (Float key : mappaSarcasm.keySet()) {
                ArrayList<Float> lisa = mappaSarcasm.get(key);
                float media = 0;
                float varianza = 0;
                for (Float f : lisa) {
                    media = media + f;
                }
                media = media / lisa.size();
                for (Float f : lisa) {
                    float diff = f - media;
                    varianza = varianza + (float) Math.pow(diff, 2);
                }
                varianza = varianza / (lisa.size() - 1);
                outvalSarcasm.println(key + "," + media + "," + varianza);
                ps.println("Sarcasm: Media e Varianza per C pari a [" + key + "]: " + media + " " + varianza);
            }

            ps.close();

        }




        //Altrimenti faccio il test su tutto

        else{


            String testSetFilePath = "data/test_dataset.klp";

            SimpleDataset inputDatasetTestSet = new SimpleDataset();
            inputDatasetTestSet.populate(testSetFilePath);

            PrintStream streamTestOut = new PrintStream("testOutput.tsv", "utf8");


            // Instantiate the SVM learning Algorithm.
            BinaryCSvmClassification svmSolver = new BinaryCSvmClassification();
            svmSolver.setLabel(positiveLabel);
            //Set the kernel
            svmSolver.setKernel(combination);
            //Set the C parameter
            svmSolver.setCn(cIrony);
            svmSolver.setCp(cIrony);


            BinaryCSvmClassification svmSolver2 = new BinaryCSvmClassification();
            svmSolver2.setLabel(sarcasmPositiveLabel);
            //Set the kernel
            svmSolver2.setKernel(combination);
            //Set the C parameter
            svmSolver2.setCn(cSarcasm);
            svmSolver2.setCp(cSarcasm);


            //Learn and get the prediction function
            svmSolver.learn(inputDatasetSet);


            //Selecting the prediction function
            Classifier classifier = svmSolver.getPredictionFunction();

            svmSolver2.learn(sarcasmDataset);
            //Selecting the prediction function
            Classifier classifier2 = svmSolver2.getPredictionFunction();

            combination.disableCache();

            System.out.println("Elaborazione del test set e scrittura dell'output su file in corso....");

            for (Example ex : inputDatasetTestSet.getExamples()) {
                ClassificationOutput p = classifier.predict(ex);
                int ironia = 0;
                int sarcasmo = 0;
                if (p.isClassPredicted(positiveLabel)) {
                    ClassificationOutput q = classifier2.predict(ex);
                    //Allora è ironico
                    ironia = 1;
                    if (q.isClassPredicted(sarcasmPositiveLabel)) {
                        sarcasmo = 1;
                    }
                }
                streamTestOut.println(ex.getRepresentation("IDTweet") + "\t" + ironia + "\t" + sarcasmo);

            }

        }

	}
}
