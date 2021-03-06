package templates;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import math.MultivariateNormalDistribution;
import structure.Coordinates;
import utils.ThreadLocalSpreadDate;
import utils.Utils;

public class AnalyzeTree implements Runnable {

	// how many days one year holds
	private static final int DaysInYear = 365;

	private RootedTree currentTree;
	private String precisionString;
	private String coordinatesName;
	private String rateString;
	private double timescaler;
	private double[] sliceHeights;
	private ThreadLocalSpreadDate mrsd;
	private ConcurrentMap<Double, List<Coordinates>> slicesMap;
	private boolean useTrueNoise;

	public AnalyzeTree(RootedTree currentTree, String precisionString,
			String coordinatesName, String rateString, double[] sliceHeights,
			double timescaler, ThreadLocalSpreadDate mrsd,
			ConcurrentMap<Double, List<Coordinates>> slicesMap,
			boolean useTrueNoise) {

		this.currentTree = currentTree;
		this.precisionString = precisionString;
		this.coordinatesName = coordinatesName;
		this.rateString = rateString;
		this.sliceHeights = sliceHeights;
		this.timescaler = timescaler;
		this.mrsd = mrsd;
		this.slicesMap = slicesMap;
		this.useTrueNoise = useTrueNoise;

	}

	public void run() throws ConcurrentModificationException {

		try {

			// attributes parsed once per tree
			double currentTreeNormalization = Utils.getTreeLength(currentTree,
					currentTree.getRootNode());
			double[] precisionArray = Utils.getTreeDoubleArrayAttribute(
					currentTree, precisionString);

			for (Node node : currentTree.getNodes()) {
				if (!currentTree.isRoot(node)) {

					// attributes parsed once per node
					Node parentNode = currentTree.getParent(node);

					double nodeHeight = Utils.getNodeHeight(currentTree, node);
					double parentHeight = Utils.getNodeHeight(currentTree,
							parentNode);

					double[] location = Utils.getDoubleArrayNodeAttribute(node,
							coordinatesName);

					double[] parentLocation = Utils
							.getDoubleArrayNodeAttribute(parentNode,
									coordinatesName);

					double rate = Utils
							.getDoubleNodeAttribute(node, rateString);

					for (int i = 0; i < sliceHeights.length; i++) {

						double sliceHeight = sliceHeights[i];

						if (nodeHeight < sliceHeight
								&& sliceHeight <= parentHeight) {

							int days = (int) (sliceHeight * DaysInYear * timescaler);
							double sliceTime = mrsd.minus(days);

							// grow map entry if key exists
							if (slicesMap.containsKey(sliceTime)) {

								double[] imputedLocation = imputeValue(
										location, parentLocation, sliceHeight,
										nodeHeight, parentHeight, rate,
										useTrueNoise, currentTreeNormalization,
										precisionArray);

								slicesMap.get(sliceTime).add(
										new Coordinates(imputedLocation[1],
												imputedLocation[0], 0.0));

								// start new entry if no such key in the map
							} else {

								List<Coordinates> coords = new ArrayList<Coordinates>();

								double[] imputedLocation = imputeValue(
										location, parentLocation, sliceHeight,
										nodeHeight, parentHeight, rate,
										useTrueNoise, currentTreeNormalization,
										precisionArray);

								coords.add(new Coordinates(imputedLocation[1],
										imputedLocation[0], 0.0));

								slicesMap.put(sliceTime, coords);

							}// END: key check
						}// END: sliceTime check
					}// END: numberOfIntervals loop
				}
			}// END: node loop

		} catch (Exception e) {
			e.printStackTrace();
		}

	}// END: run

	private double[] imputeValue(double[] location, double[] parentLocation,
			double sliceHeight, double nodeHeight, double parentHeight,
			double rate, boolean trueNoise, double treeNormalization,
			double[] precisionArray) {

		int dim = (int) Math.sqrt(1 + 8 * precisionArray.length) / 2;
		double[][] precision = new double[dim][dim];
		int c = 0;
		for (int i = 0; i < dim; i++) {
			for (int j = i; j < dim; j++) {
				precision[j][i] = precision[i][j] = precisionArray[c++]
						* treeNormalization;
			}
		}

		dim = location.length;
		double[] nodeValue = new double[2];
		double[] parentValue = new double[2];

		for (int i = 0; i < dim; i++) {

			nodeValue[i] = location[i];
			parentValue[i] = parentLocation[i];

		}

		final double scaledTimeChild = (sliceHeight - nodeHeight) * rate;
		final double scaledTimeParent = (parentHeight - sliceHeight) * rate;
		final double scaledWeightTotal = (1.0 / scaledTimeChild)
				+ (1.0 / scaledTimeParent);

		if (scaledTimeChild == 0)
			return location;

		if (scaledTimeParent == 0)
			return parentLocation;

		// Find mean value, weighted average
		double[] mean = new double[dim];
		double[][] scaledPrecision = new double[dim][dim];

		for (int i = 0; i < dim; i++) {
			mean[i] = (nodeValue[i] / scaledTimeChild + parentValue[i]
					/ scaledTimeParent)
					/ scaledWeightTotal;

			if (trueNoise) {
				for (int j = i; j < dim; j++)
					scaledPrecision[j][i] = scaledPrecision[i][j] = precision[i][j]
							* scaledWeightTotal;
			}
		}

		if (trueNoise) {
			mean = MultivariateNormalDistribution
					.nextMultivariateNormalPrecision(mean, scaledPrecision);
		}

		double[] result = new double[dim];
		for (int i = 0; i < dim; i++)
			result[i] = mean[i];

		return result;
	}// END: ImputeValue

}// END: AnalyzeTree
