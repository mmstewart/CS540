import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.lang.Math;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	// ordered list of class labels
	private List<String> labels;
	// ordered list of attributes
	private List<String> attributes;
	// map to ordered discrete values taken by attributes
	private Map<String, List<String>> attributeValues;
	// map for getting the index
	private HashMap<String, Integer> label_inv;
	private HashMap<String, Integer> attr_inv;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train:
	 *            the training set
	 */
	DecisionTreeImpl(DataSet train) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		this.root = buildTree(train.instances, this.attributes, null, majorityLabel(train.instances));
	}

	private DecTreeNode buildTree(List<Instance> instances, List<String> attributes, String parentAttributeValue,
			String label) {
		if (instances.isEmpty()) {
			return new DecTreeNode(label, null, parentAttributeValue, true);
		}

		if (sameLabel(instances)) {
			return new DecTreeNode(instances.get(0).label, null, parentAttributeValue, true);
		}

		if (attributes.isEmpty()) {
			return new DecTreeNode(majorityLabel(instances), null, parentAttributeValue, true);
		}

		String bestAttr = bestAttr(instances, attributes);
		DecTreeNode tree = new DecTreeNode(majorityLabel(instances), bestAttr, parentAttributeValue, false);

		for (String attrValue : this.attributeValues.get(bestAttr)) {
			List<Instance> subSetInstances = new ArrayList<Instance>();
			int attrIndex = getAttributeIndex(bestAttr);
			for (Instance instance : instances) {
				if (instance.attributes.get(attrIndex).equals(attrValue)) {
					subSetInstances.add(instance);
				}
			}
			List<String> subAttrs = new ArrayList<String>(attributes);
			subAttrs.remove(bestAttr);
			tree.addChild(buildTree(subSetInstances, subAttrs, attrValue, majorityLabel(instances)));
		}
		return tree;
	}

	private String bestAttr(List<Instance> instances, List<String> attributes) {
		String bestAttr = attributes.get(0);
		double maxInfoGain = Double.MIN_VALUE;
		for (String attr : attributes) {
			double currInfoGain = InfoGain(instances, attr);
			if (currInfoGain > maxInfoGain) {
				maxInfoGain = currInfoGain;
				bestAttr = attr;
			}
		}
		return bestAttr;
	}

	boolean sameLabel(List<Instance> instances) {
		for (int i = 1; i < instances.size(); ++i) {
			if (!instances.get(i - 1).label.equals(instances.get(i).label))
				return false;
		}
		return true;
	}

	String majorityLabel(List<Instance> instances) {
		int label0 = 0;
		int label1 = 0;
		for (Instance attr : instances)
			if (attr.label.equals(labels.get(0)))
				label0++;
			else
				label1++;
		if (label0 < label1)
			return labels.get(1);
		else
			return labels.get(0);
	}

	double entropy(List<Instance> instances) {
		int[] nlabels = new int[labels.size()];
		for (Instance instance : instances) {
			nlabels[getLabelIndex(instance.label)]++;
		}
		double entropy = 0.0;
		for (int nlabel : nlabels) {
			double probability = ((double) nlabel / instances.size());
			entropy += -probability * (Math.log(probability) / Math.log(2));
		}
		return entropy;
	}

	double conditionalEntropy(List<Instance> instances, String attr) {
		int numAttr = attributeValues.get(attr).size();
		int[][] numLabels = new int[numAttr][labels.size()];
		int index = getAttributeIndex(attr);
		for (Instance instance : instances) {
			int attrValueIndex = getAttributeValueIndex(attr, instance.attributes.get(index));
			numLabels[attrValueIndex][getLabelIndex(instance.label)]++;
		}
		double totconEnt = 0.0;
		for (int i = 0; i < numAttr; i++) {
			int numValue = 0;
			for (int j = 0; j < labels.size(); j++) {
				numValue += numLabels[i][j];
			}
			double conEnt = 0.0;
			for (int j = 0; j < labels.size(); j++) {
				if (numLabels[i][j] == 0) {
					continue;
				}
				double probability = ((double) numLabels[i][j] / numValue);
				conEnt += -probability * (Math.log(probability) / Math.log(2));
			}
			totconEnt += (double) numValue / instances.size() * conEnt;
		}
		return totconEnt;
	}

	double InfoGain(List<Instance> instances, String attr) {
		return entropy(instances) - conditionalEntropy(instances, attr);
	}

	@Override
	public String classify(Instance instance) {
		DecTreeNode current = this.root;
		while (!current.terminal) {
			int index = getAttributeIndex(current.attribute);
			String attrValue = instance.attributes.get(index);
			int valueIndex = getAttributeValueIndex(current.attribute, attrValue);
			current = current.children.get(valueIndex);
		}
		return current.label;
	}

	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		for (String attr : attributes) {
			double arg = InfoGain(train.instances, attr); // for each attribute, calculate gain
			System.out.print(attr + " ");
			System.out.format("%.5f\n", arg);
		}
	}

	@Override
	public void printAccuracy(DataSet test) {
		int instanceTotal = test.instances.size();
		int correctNum = 0;

		for (int i = 0; i < instanceTotal; i++) {
			String actualLabel = test.instances.get(i).label;
			String treeLabel = classify(test.instances.get(i));
			if (actualLabel.equals(treeLabel)) {
				correctNum++;
			}
		}
		System.out.format("%.5f\n", (double) correctNum / instanceTotal);
	}

	@Override
	/**
	 * Print the decision tree in the specified format Do not modify
	 */
	public void print() {

		printTreeNode(root, null, 0);
	}

	/**
	 * Prints the subtree of the node with each line prefixed by 4 * k spaces. Do
	 * not modify
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else {
			int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
			value = attributeValues.get(parent.attribute).get(attributeValueIndex);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + p.label + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + p.attribute + "?}");
			System.out.println(sb.toString());
			for (DecTreeNode child : p.children) {
				printTreeNode(child, p, k + 1);
			}
		}
	}

	/**
	 * Helper function to get the index of the label in labels list
	 */
	private int getLabelIndex(String label) {
		if (label_inv == null) {
			this.label_inv = new HashMap<String, Integer>();
			for (int i = 0; i < labels.size(); i++) {
				label_inv.put(labels.get(i), i);
			}
		}
		return label_inv.get(label);
	}

	/**
	 * Helper function to get the index of the attribute in attributes list
	 */
	private int getAttributeIndex(String attr) {
		if (attr_inv == null) {
			this.attr_inv = new HashMap<String, Integer>();
			for (int i = 0; i < attributes.size(); i++) {
				attr_inv.put(attributes.get(i), i);
			}
		}
		return attr_inv.get(attr);
	}

	/**
	 * Helper function to get the index of the attributeValue in the list for the
	 * attribute key in the attributeValues map
	 */
	private int getAttributeValueIndex(String attr, String value) {
		for (int i = 0; i < attributeValues.get(attr).size(); i++) {
			if (value.equals(attributeValues.get(attr).get(i))) {
				return i;
			}
		}
		return -1;
	}
}
