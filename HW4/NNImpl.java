import java.util.*;

/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 */

public class NNImpl {
    private ArrayList<Node> inputNodes; //list of the output layer nodes.
    private ArrayList<Node> hiddenNodes;    //list of the hidden layer nodes
    private ArrayList<Node> outputNodes;    // list of the output layer nodes
    private Double[][] outputWeights;
    private ArrayList<Instance> trainingSet;    //the training set

    private double learningRate;    // variable to store the learning rate
    private int maxEpoch;   // variable to store the maximum number of epochs
    private Random random;  // random number generator to shuffle the training set

    /**
     * This constructor creates the nodes necessary for the neural network
     * Also connects the nodes of different layers
     * After calling the constructor the last node of both inputNodes and
     * hiddenNodes will be bias nodes.
     */

    NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Random random, Double[][] hiddenWeights, Double[][] outputWeights) {
        this.trainingSet = trainingSet;
        this.learningRate = learningRate;
        this.maxEpoch = maxEpoch;
        this.random = random;
        this.outputWeights = outputWeights;

        //input layer nodes
        inputNodes = new ArrayList<>();
        int inputNodeCount = trainingSet.get(0).attributes.size();
        int outputNodeCount = trainingSet.get(0).classValues.size();
        for (int i = 0; i < inputNodeCount; i++) {
            Node node = new Node(0);
            inputNodes.add(node);
        }

        //bias node from input layer to hidden
        Node biasToHidden = new Node(1);
        inputNodes.add(biasToHidden);

        //hidden layer nodes
        hiddenNodes = new ArrayList<>();
        for (int i = 0; i < hiddenNodeCount; i++) {
            Node node = new Node(2);
            //Connecting hidden layer nodes with input layer nodes
            for (int j = 0; j < inputNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(inputNodes.get(j), hiddenWeights[i][j]);
                node.parents.add(nwp);
            }
            hiddenNodes.add(node);
        }

        //bias node from hidden layer to output
        Node biasToOutput = new Node(3);
        hiddenNodes.add(biasToOutput);

        //Output node layer
        outputNodes = new ArrayList<>();
        for (int i = 0; i < outputNodeCount; i++) {
            Node node = new Node(4);
            //Connecting output layer nodes with hidden layer nodes
            for (int j = 0; j < hiddenNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
                node.parents.add(nwp);
            }
            outputNodes.add(node);
        }
    }

    public int predict(Instance instance) 
    {
        forwards(instance);
        
        int output = -1;
        double value = -1.0;
        for (int i = 0; i < outputNodes.size(); i++)
            if (outputNodes.get(i).getOutput() > value) 
            {
                output = i;
                value = outputNodes.get(i).getOutput();
            }
        return output;
    }
    
    public void train() 
    {
        for (int i = 0; i < maxEpoch; i++) 
        {
        	Collections.shuffle(trainingSet, random);
        	double l = 0.0;
        	
            for (Instance instance : trainingSet) 
            {
            	forwards(instance);
            	backwards(instance);
            	
                for(Node n : outputNodes) 
                {
                	n.updateWeight(learningRate);
                }
                
                for(Node n : hiddenNodes) 
                {
                	n.updateWeight(learningRate);
                }
            }
            
            for (Instance instance : trainingSet) 
            {
            	l += loss(instance);
            }
            double loss = l/trainingSet.size();
            
        System.out.printf("Epoch: %d, Loss: %.8e\n", i, loss);
        }
    }
    
    private void outputForInstance(ArrayList<Node> node) 
    {
        for (Node n : node) 
        {
            n.calculateOutput(outputNodes);
        }
    }
    
    private void forwards(Instance instance) 
    {
        for (int i = 0; i < inputNodes.size() - 1; i++) 
        {
            inputNodes.get(i).setInput(instance.attributes.get(i));
        }
        
        for (int i = 0; i < hiddenNodes.size(); i++) 
        {
            hiddenNodes.get(i).setInput(instance.attributes.get(i));
        }
        
        outputForInstance(hiddenNodes);
        outputForInstance(outputNodes);
    }

    private void backwards(Instance instance) 
    {
        for (int i = 0; i < outputNodes.size(); i++)
        {
            outputNodes.get(i).calculateDelta(outputNodes, instance.classValues.get(i));
        }
        
        for (Node n : hiddenNodes) 
        {
            n.calculateDelta(outputNodes, 1);
        }
    }

	private double loss(Instance instance) 
    {
		forwards(instance);
		
    	double loss = 0.0;
    	for(int i = 0; i < outputNodes.size(); i++) 
    	{
    		loss -= (Math.log(outputNodes.get(i).getOutput()) * (double) instance.classValues.get(i));
    	}
    	return loss;
    }
}
    
