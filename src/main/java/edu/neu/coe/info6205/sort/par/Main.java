package edu.neu.coe.info6205.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * CONSIDER tidy it up a bit.
 */
public class Main {

	public static void main(String[] args) {
		//dfdf
		//dfdf
		processArgs(args);
		//dfdf
		/*
		 * if(size<1100000) { size+=100000; } else { size=1000000; }
		 */
		for(int a=1000000;a<=3000000;a+=1000000) {
			int thread = 2;
			System.out.println("Size of Array: "+ a);
			while(thread <=64) {
				ForkJoinPool pool = new ForkJoinPool(thread);
				//dfdf
				System.out.println("Degree of parallelism: " + pool.getParallelism());
				Random random = new Random();
				int[] array=new int[a];

				ArrayList<Long> timeList = new ArrayList<>();
				for (int j = 0; j < 10; j+=1) {
					ParSort.cutoff = a * (j+1)/10;
					long time;
					long startTime = System.currentTimeMillis();
					for (int t = 0; t < 10; t++) {

						for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
						ParSort.sort(array, 0, array.length,pool);
					}
					long endTime = System.currentTimeMillis();
					time = (endTime - startTime);
					timeList.add(time);


					System.out.println("cutoff：" + (ParSort.cutoff) + "\t\t10 times Time:" + time + "ms" + " with the size " +a);

				}
				try {
					FileOutputStream fis = new FileOutputStream("./src/" + a + "_result_" + thread + ".csv");

					//FileOutputStream fis = new FileOutputStream("./src/result.csv");
					OutputStreamWriter isr = new OutputStreamWriter(fis);
					BufferedWriter bw = new BufferedWriter(isr);
					int j = 0;
					for (long i : timeList) {
						//String content = (double) 10000 * (j + 1) / 2000000 + "," + (double) i / 10 + "\n";
						String content = (double) 5000 * (j+1) + "," + (double) i / 10 + "\n";
						j++;
						bw.write(content);
						bw.flush();
					}
					bw.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
				thread += thread;
			}
		}
	}

	private static void processArgs(String[] args) {
		String[] xs = args;
		while (xs.length > 0)
			if (xs[0].startsWith("-")) xs = processArg(xs);
	}

	private static String[] processArg(String[] xs) {
		String[] result = new String[0];
		System.arraycopy(xs, 2, result, 0, xs.length - 2);
		processCommand(xs[0], xs[1]);
		return result;
	}

	private static void processCommand(String x, String y) {
		if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
		else
			// TODO sort this out
			if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
				ForkJoinPool.getCommonPoolParallelism();
	}

	private static void setConfig(String x, int i) {
		configuration.put(x, i);
	}

	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private static final Map<String, Integer> configuration = new HashMap<>();


}
