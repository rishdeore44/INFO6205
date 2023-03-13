package edu.neu.coe.info6205.sort.linearithmic;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.SortWithHelper;
import edu.neu.coe.info6205.sort.elementary.InsertionSort;
import edu.neu.coe.info6205.util.Config;

import java.util.Arrays;

/**
 * Class MergeSort.
 *
 * @param <X> the underlying comparable type.
 */
public class MergeSort<X extends Comparable<X>> extends SortWithHelper<X> {

	public static final String DESCRIPTION = "MergeSort";

	/**
	 * Constructor for MergeSort
	 * <p>
	 * NOTE this is used only by unit tests, using its own instrumented helper.
	 *
	 * @param helper an explicit instance of Helper to be used.
	 */
	public MergeSort(Helper<X> helper) {
		super(helper);
		insertionSort = new InsertionSort<>(helper);
	}

	/**
	 * Constructor for MergeSort
	 *
	 * @param N      the number elements we expect to sort.
	 * @param config the configuration.
	 */
	public MergeSort(int N, Config config) {
		super(DESCRIPTION + ":" + getConfigString(config), N, config);
		insertionSort = new InsertionSort<>(getHelper());
	}

	@Override
	public X[] sort(X[] xs, boolean makeCopy) {
		getHelper().init(xs.length);
		X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
		sort(result, 0, result.length);
		return result;
	}

	@Override
	public void sort(X[] a, int from, int to) {
		// CONSIDER don't copy but just allocate according to the xs/aux interchange optimization
		X[] aux = Arrays.copyOf(a, a.length);
		sort(a, aux, from, to);
	}

	private void sort(X[] a, X[] aux, int from, int to) {
		final Helper<X> helper = getHelper();
		Config config = helper.getConfig();
		boolean insurance = config.getBoolean(MERGESORT, INSURANCE);
		boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);
		if (to <= from + helper.cutoff()) {
			insertionSort.sort(a, from, to);
			return;
		}

		// FIXME : implement merge sort with insurance and no-copy optimizations
		/*
		 * final int n = to - from; int mid = from + n/2; if(insurance){
		 * if(helper.less(a[mid-1],a[mid])) return; } if(noCopy){ noCopyTrue(a, aux,
		 * from, to, mid); } else{ noCopyFalse(a, aux, from, to, n, mid); } merge(aux,
		 * a, from, mid, to);
		 */

		int mid =  from + (to-from)/2;
		if(noCopy){
			noCopyTrue(a, aux, from, to, helper, insurance, mid);
		}else{
			noCopyFalse(a, aux, from, to, helper, insurance, mid);
		}
		// END
	}

	protected void noCopyTrue(X[] a, X[] aux, int from, int to, final Helper<X> helper, boolean insurance, int mid) {
		sort (aux, a, from, mid);
		sort (aux, a, mid, to);
		if(insurance && helper.less(aux[mid-1],aux[mid])){
			for (int k = from; k < to; k++){
				helper.copy(aux, k, a, k);
			}
			return;
		}
		merge(aux, a, from, mid, to);
	}

	protected void noCopyFalse(X[] a, X[] aux, int from, int to, final Helper<X> helper, boolean insurance, int mid) {
		sort (a, aux, from, mid);
		sort (a, aux, mid, to);
		if(insurance && helper.less(a[mid-1],a[mid]))
			return;
		merge(a, aux, from, mid, to, false);
	}


	

	// CONSIDER combine with MergeSortBasic perhaps.

	private void merge(X[] sorted, X[] result, int from, int mid, int to) {
		final Helper<X> helper = getHelper(); 
		int i = from; int j = mid; 
		for (int k = from; k < to; k++) 
			if (i >= mid) helper.copy(sorted, j++, result, k); 
			else if (j >= to) helper.copy(sorted, i++, result, k);
			else if (helper.less(sorted[j],sorted[i])) { 
				helper.incrementFixes(mid - i); 
				helper.copy(sorted, j++,result, k);
			}
			else
				helper.copy(sorted, i++, result, k);
	}


	private void merge(X[] a, X[] aux, int from, int mid, int to, boolean nocopy) {
		final Helper<X> helper = getHelper();
		boolean instrumentation = helper.instrumented();
		if(instrumentation){
			for (int k = from; k < to; k++)
				helper.copy(a, k, aux, k );
		}else{
			for (int k = from; k < to; k++)
				aux[k] = a[k];
		}
		int i = from;
		int j = mid;
		if(instrumentation){
			for (int k = from; k < to; k++){
				if (i >= mid) helper.copy(aux, j++, a, k);
				else if (j >= to) helper.copy(aux, i++, a, k);
				else if (helper.less(aux[j], aux[i])) {
					helper.incrementFixes(mid - i);
					helper.copy(aux, j++, a, k);
				} else helper.copy(aux, i++, a, k);
			}
		}else{
			for (int k = from; k < to; k++){
				if (i >= mid)
					a[k] = aux[j++];
				else if (j >= to)
					a[k] = aux[i++];
				else if (less(aux[j], aux[i])) {
					a[k] = aux[j++];
				} else
					a[k] = aux[i++];
			}
		}

	}

	private static boolean less(Comparable v, Comparable w) {
		return v.compareTo(w) < 0;
	}
	public static final String MERGESORT = "mergesort";
	public static final String NOCOPY = "nocopy";
	public static final String INSURANCE = "insurance";

	private static String getConfigString(Config config) {
		StringBuilder stringBuilder = new StringBuilder();
		if (config.getBoolean(MERGESORT, INSURANCE)) stringBuilder.append(" with insurance comparison");
		if (config.getBoolean(MERGESORT, NOCOPY)) stringBuilder.append(" with no copy");
		return stringBuilder.toString();
	}

	private final InsertionSort<X> insertionSort;
}

