package checks;

import gui.InteractiveTableModel;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.trees.RootedTree;
import utils.Utils;

public class DiscreteSanityCheck {

	private boolean notNull = false;

	public boolean check(String treeFilename, String stateAttName,
			InteractiveTableModel table) throws IOException, ImportException,
			ParseException {

		NexusImporter importer = new NexusImporter(new FileReader(treeFilename));
		RootedTree tree = (RootedTree) importer.importNextTree();

		Set<String> uniqueTreeStates = new HashSet<String>();
		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				String uniqueTreeState = Utils.getStringNodeAttribute(node,
						stateAttName);
				uniqueTreeStates.add(uniqueTreeState);

				if (uniqueTreeState == null) {
					notNull = false;
					break;
				} else {
					notNull = true;
				}

			}
		}// END: node loop

		Object[] uniqueTreeStatesArray = uniqueTreeStates.toArray();

		// Utils.PrintArray(uniqueTreeStatesArray);

		for (int i = 0; i < table.getRowCount(); i++) {

			String state = null;

			for (int j = 0; j < uniqueTreeStatesArray.length; j++) {

				String name = String.valueOf(table.getValueAt(i, 0));

				if (name.toLowerCase().equals(
						((String) uniqueTreeStatesArray[j]).toLowerCase())) {

					state = name;

				}// END: if location and discrete states match

			}// END: unique discrete states loop

			if (state == null) { // if none matches
				System.out.println("Location "
						+ String.valueOf(table.getValueAt(i, 0))
						+ " does not fit any of the discrete states");
			}
		}// END: locations loop
		return notNull;

	}

}
