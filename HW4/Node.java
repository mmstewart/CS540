import java.util.*;

/**
 * Class for internal organization of a Neural Network.
 * There are 5 types of nodes. Check the type attribute of the node for details.
 * Feel free to modify the provided function signatures to fit your own implementation
 */

public class Node {
    private int type = 0; //0=input,1=biasToHidden,2=hidden,3=biasToOutput,4=Output
    public ArrayList<NodeWeightPair> parents = null; //Array List that will contain the parents (including the bias node) with weights if applicable

    private double inputValue = 0.0;
    private double outputValue = 0.0;
    private double outputGradient = 0.0;
    private double delta = 0.0; //input gradient

    Node(int type) 
    {
        if (type > 4 || type < 0) 
        {
            System.out.println("Incorrect value for node type");
            System.exit(1);

        } 
        else 
        {
            this.type = type;
        }

        if (type == 2 || type == 4) 
        {
            parents = new ArrayList<>();
        }
    }

    public void setInput(double inputValue) 
    {
        if (type == 0) 
        {
            this.inputValue = inputValue;
        } 
    }
    
    public double getDelta() 
    {
    	return delta;
    }
  
    public void calculateOutput(ArrayList<Node> nodes)  
	{
		if (type == 2 || type == 4) 
		{
			if (type == 2) //ReLU
			{
				outputValue = ReLU(sumInputs(this));
			} 
			else //Softmax
			{
				outputValue = Softmax(sumInputs(this), nodes);
			}
		}
	}
	
    private double ReLU(double input) 
    {
        return Math.max(0,input);
    }
	
    public double Softmax(double input, ArrayList<Node> nodes)
    {
    	double denom = 0.0;
        double num = Math.pow(Math.E, input);
        for (Node n : nodes) 
        {
            denom += Math.pow(Math.E, n.sumInputs(n));
        }
        return num / denom;
    }

    public double getOutput() 
    {
        if (type == 0) {    //Input node
            return inputValue;
        } else if (type == 1 || type == 3) {    //Bias node
            return 1.00;
        } else {
            return outputValue;
        }
    }
    
    public double sumInputs(Node node) 
    {
        double sum = 0.0;
        
        for (NodeWeightPair nwp : node.parents) 
        {
            sum += (nwp.weight * nwp.node.getOutput());
        }
        return sum;
    }
    
    public void calculateDelta(ArrayList<Node> nodes, double output) {
        if (type == 2 || type == 4)  
        {
        	if(type == 2) //ReLU derivative
        	{
                double sum = 0.0;
                for (Node n : nodes) 
                {
                    for (NodeWeightPair nwp : n.parents) 
                    {
                        if (nwp.node.equals(this)) 
                        {
                            sum += (nwp.weight * n.getDelta());
                        }
                    }
                }
                if(sumInputs(this) > 0) 
                {
                	delta = 1 * sum;
                } else {
                	delta = 0;
                }
        	}
        	else //Softmax derivative
        	{
        		delta = output - outputValue;
        	}
        }
    }

    public void updateWeight(double learningRate) 
    {
        if (type == 2 || type == 4) 
        {
           for(NodeWeightPair nwp : parents) 
           {
        	   nwp.weight += (learningRate * nwp.node.getOutput() * delta);
           }
        }
    }
}


