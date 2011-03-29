package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import templates.RateIndicatorBFToKML;
import templates.RateIndicatorBFToProcessing;
import utils.Utils;

@SuppressWarnings("serial")
public class RateIndicatorBFTab extends JPanel {

	// Sizing constants
	private final int leftPanelWidth = 230;
	private final int leftPanelHeight = 1100;

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon logIcon;
	private ImageIcon locationsIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;
	private ImageIcon errorIcon;

	// Colors
	private Color backgroundColor;

	// Strings for paths
	private String logFilename;
	private String locationsFilename;
	private String workingDirectory;

	// Text fields
	private JTextField burnInParser;
	private JTextField numberOfIntervalsParser;
	private JTextField maxAltMappingParser;
	private JTextField bfCutoffParser;
	private JTextField kmlPathParser;

	// Buttons for tab
	private JButton openLog;
	private JButton openLocations;
	private JButton generateKml;
	private JButton generateProcessing;
	private JButton saveProcessingPlot;

	// Sliders
	private JSlider redBranchSlider;
	private JSlider greenBranchSlider;
	private JSlider blueBranchSlider;
	private JSlider opacityBranchSlider;

	// left tools pane
	private JPanel leftPanel;
	private JPanel tmpPanel;

	// Processing pane
	private RateIndicatorBFToProcessing rateIndicatorBFToProcessing;

	// Progress bar
	private JProgressBar progressBar;

	public RateIndicatorBFTab() {

		// Setup miscallenous
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		backgroundColor = new Color(231, 237, 246);

		// Setup icons
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		logIcon = CreateImageIcon("/icons/log.png");
		locationsIcon = CreateImageIcon("/icons/locations.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");
		errorIcon = CreateImageIcon("/icons/error.png");

		// Setup text fields
		burnInParser = new JTextField("0.1", 5);
		numberOfIntervalsParser = new JTextField("100", 5);
		maxAltMappingParser = new JTextField("500000", 5);
		bfCutoffParser = new JTextField("3.0", 5);
		kmlPathParser = new JTextField("output.kml", 10);

		// Setup buttons for tab
		openLog = new JButton("Open", logIcon);
		openLocations = new JButton("Open", locationsIcon);
		generateKml = new JButton("Generate", nuclearIcon);
		generateProcessing = new JButton("Plot", processingIcon);
		saveProcessingPlot = new JButton("Save", saveIcon);

		// Setup progress bar
		progressBar = new JProgressBar();

		/**
		 * left tools pane
		 * */
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		leftPanel.setPreferredSize(new Dimension(leftPanelWidth,
				leftPanelHeight));

		openLog.addActionListener(new ListenOpenLog());
		generateKml.addActionListener(new ListenGenerateKml());
		openLocations.addActionListener(new ListenOpenLocations());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load log file:"));
		tmpPanel.add(openLog);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load locations file:"));
		tmpPanel.add(openLocations);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Specify burn-in:"));
		tmpPanel.add(burnInParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Bayes Factor cut-off:"));
		tmpPanel.add(bfCutoffParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Number of intervals:"));
		tmpPanel.add(numberOfIntervalsParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Maximal altitude:"));
		tmpPanel.add(maxAltMappingParser);
		leftPanel.add(tmpPanel);

		// Branches color mapping:
		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setPreferredSize(new Dimension(leftPanelWidth, 420));
		tmpPanel.setBorder(new TitledBorder("Branches color mapping:"));

		redBranchSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		redBranchSlider.setBorder(BorderFactory.createTitledBorder("Red"));
		redBranchSlider.setMajorTickSpacing(50);
		redBranchSlider.setMinorTickSpacing(25);
		redBranchSlider.setPaintTicks(true);
		redBranchSlider.setPaintLabels(true);
		tmpPanel.add(redBranchSlider);

		greenBranchSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 5);
		greenBranchSlider.setBorder(BorderFactory.createTitledBorder("Green"));
		greenBranchSlider.setMajorTickSpacing(50);
		greenBranchSlider.setMinorTickSpacing(25);
		greenBranchSlider.setPaintTicks(true);
		greenBranchSlider.setPaintLabels(true);
		tmpPanel.add(greenBranchSlider);

		blueBranchSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 50);
		blueBranchSlider.setBorder(BorderFactory.createTitledBorder("Blue"));
		blueBranchSlider.setMajorTickSpacing(50);
		blueBranchSlider.setMinorTickSpacing(25);
		blueBranchSlider.setPaintTicks(true);
		blueBranchSlider.setPaintLabels(true);
		tmpPanel.add(blueBranchSlider);

		opacityBranchSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		opacityBranchSlider.setBorder(BorderFactory
				.createTitledBorder("Opacity"));
		opacityBranchSlider.setMajorTickSpacing(50);
		opacityBranchSlider.setMinorTickSpacing(25);
		opacityBranchSlider.setPaintTicks(true);
		opacityBranchSlider.setPaintLabels(true);
		tmpPanel.add(opacityBranchSlider);

		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("KML name:"));
		tmpPanel.add(kmlPathParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Generate KML / Plot tree:"));
		tmpPanel.setPreferredSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.add(generateKml);
		tmpPanel.add(generateProcessing);
		tmpPanel.add(progressBar);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Save plot:"));
		tmpPanel.add(saveProcessingPlot);
		leftPanel.add(tmpPanel);

		JScrollPane leftScrollPane = new JScrollPane(leftPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		leftScrollPane.setMinimumSize(new Dimension(leftPanelWidth,
				leftPanelHeight));
		add(leftScrollPane, BorderLayout.CENTER);

		/**
		 * Processing pane
		 * */
		rateIndicatorBFToProcessing = new RateIndicatorBFToProcessing();
		rateIndicatorBFToProcessing.setPreferredSize(new Dimension(2048, 1025));
		JScrollPane rightScrollPane = new JScrollPane(
				rateIndicatorBFToProcessing,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(rightScrollPane, BorderLayout.CENTER);

	}

	private class ListenOpenLog implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				String[] logFiles = new String[] { "log" };

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Opening log file...");
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new SimpleFileFilter(logFiles,
						"Log files (*.log)"));

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				logFilename = file.getAbsolutePath();
				System.out.println("Opened " + logFilename + "\n");

				workingDirectory = chooser.getCurrentDirectory().toString();
				System.out.println("Setted working directory to "
						+ workingDirectory + "\n");

			} catch (Exception e) {
				System.err.println("Could not Open! \n");
			}
		}
	}

	private class ListenOpenLocations implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading locations file...");

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				locationsFilename = file.getAbsolutePath();
				System.out.println("Opened " + locationsFilename + "\n");

			} catch (Exception e1) {
				System.err.println("Could not Open! \n");
			}
		}
	}

	private class ListenGenerateKml implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				// Executed in background thread
				public Void doInBackground() {

					try {

						generateKml.setEnabled(false);
						progressBar.setIndeterminate(true);

						RateIndicatorBFToKML rateIndicatorBFToKML = new RateIndicatorBFToKML();

						rateIndicatorBFToKML.setLogFilePath(logFilename, Double
								.parseDouble(burnInParser.getText()));

						rateIndicatorBFToKML.setBfCutoff(Double
								.valueOf(bfCutoffParser.getText()));

						rateIndicatorBFToKML
								.setLocationFilePath(locationsFilename);

						rateIndicatorBFToKML.setMaxAltitudeMapping(Double
								.valueOf(maxAltMappingParser.getText()));

						rateIndicatorBFToKML.setNumberOfIntervals(Integer
								.valueOf(numberOfIntervalsParser.getText()));

						rateIndicatorBFToKML.setKmlWriterPath(workingDirectory
								.concat("/").concat(kmlPathParser.getText()));

						rateIndicatorBFToKML
								.setMaxBranchRedMapping(redBranchSlider
										.getValue());

						rateIndicatorBFToKML
								.setMaxBranchGreenMapping(greenBranchSlider
										.getValue());

						rateIndicatorBFToKML
								.setMaxBranchBlueMapping(blueBranchSlider
										.getValue());

						rateIndicatorBFToKML
								.setMaxBranchOpacityMapping(opacityBranchSlider
										.getValue());

						rateIndicatorBFToKML.GenerateKML();

						System.out.println("Finished in: "
								+ RateIndicatorBFToKML.time + " msec \n");

					} catch (Exception e) {
						e.printStackTrace();

						JOptionPane.showMessageDialog(Utils.getActiveFrame(), e
								.toString(), "Error",
								JOptionPane.ERROR_MESSAGE, errorIcon);
					}

					return null;
				}// END: doInBackground()

				// Executed in event dispatch thread
				public void done() {
					generateKml.setEnabled(true);
					progressBar.setIndeterminate(false);
				}
			};

			worker.execute();
		}
	}// END: ListenGenerateKml

	private class ListenGenerateProcessing implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				// Executed in background thread
				public Void doInBackground() {

					try {

						generateProcessing.setEnabled(false);
						progressBar.setIndeterminate(true);

						rateIndicatorBFToProcessing.setLogFilePath(logFilename,
								Double.parseDouble(burnInParser.getText()));

						rateIndicatorBFToProcessing.setBfCutoff(Double
								.valueOf(bfCutoffParser.getText()));

						rateIndicatorBFToProcessing
								.setLocationFilePath(locationsFilename);

						rateIndicatorBFToProcessing
								.setMaxBranchRedMapping(redBranchSlider
										.getValue());

						rateIndicatorBFToProcessing
								.setMaxBranchGreenMapping(greenBranchSlider
										.getValue());

						rateIndicatorBFToProcessing
								.setMaxBranchBlueMapping(blueBranchSlider
										.getValue());

						rateIndicatorBFToProcessing
								.setMaxBranchOpacityMapping(opacityBranchSlider
										.getValue());

						rateIndicatorBFToProcessing.init();

					} catch (Exception e) {
						e.printStackTrace();

						JOptionPane.showMessageDialog(Utils.getActiveFrame(), e
								.toString(), "Error",
								JOptionPane.ERROR_MESSAGE, errorIcon);
					}

					return null;
				}// END: doInBackground()

				// Executed in event dispatch thread
				public void done() {
					generateProcessing.setEnabled(true);
					progressBar.setIndeterminate(false);
					System.out.println("Finished. \n");
				}
			};

			worker.execute();
		}
	}// END: ListenGenerateProcessing

	private class ListenSaveProcessingPlot implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Saving as png file...");

				chooser.showSaveDialog(chooser);
				File file = chooser.getSelectedFile();
				String plotToSaveFilename = file.getAbsolutePath();

				rateIndicatorBFToProcessing.save(plotToSaveFilename);
				System.out.println("Saved " + plotToSaveFilename + "\n");

			} catch (Exception e) {
				System.err.println("Could not save! \n");
			}

		}// END: actionPerformed
	}// END: class

	private ImageIcon CreateImageIcon(String path) {
		java.net.URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path + "\n");
			return null;
		}
	}

}
