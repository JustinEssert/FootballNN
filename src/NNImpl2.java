/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;
import java.io.Console;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;


public class NNImpl2{
	public ArrayList<Node> inputNodes=null;//list of the output layer nodes.
	public ArrayList<Node> hiddenNodes=null;//list of the hidden layer nodes
	public ArrayList<Node> outputNodes=null;// list of the output layer nodes

	public ArrayList<Instance> trainingSet=null;//the training set

	Double learningRate=Config.LEARNINGRATE; // variable to store the learning rate
	int maxEpoch= Config.MAXEPOCH; // variable to store the maximum number of epochs
	int hiddenNodeCount = Config.NOHIDDENNODES;

	/**
	 * This constructor creates the nodes necessary for the neural network
	 * Also connects the nodes of different layers
	 * After calling the constructor the last node of both inputNodes and  
	 * hiddenNodes will be bias nodes. 
	 */

	public NNImpl2(ArrayList<Instance> trainingSet, Double [][]hiddenWeights, Double[][] outputWeights)
	{
		this.trainingSet=trainingSet;

		//input layer nodes
		inputNodes=new ArrayList<Node>();
		int inputNodeCount=trainingSet.get(0).attributes.size();
		int outputNodeCount=trainingSet.get(0).classValues.size();
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}

		//bias node from input layer to hidden
		Node biasToHidden=new Node(1);
		inputNodes.add(biasToHidden);

		//hidden layer nodes
		hiddenNodes=new ArrayList<Node> ();
		for(int i=0;i<hiddenNodeCount;i++)
		{
			Node node=new Node(2);
			//Connecting hidden layer nodes with input layer nodes
			for(int j=0;j<inputNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(j),hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}

		//bias node from hidden layer to output
		Node biasToOutput=new Node(3);
		hiddenNodes.add(biasToOutput);

		//Output node layer
		outputNodes=new ArrayList<Node> ();
		for(int i=0;i<outputNodeCount;i++)
		{
			Node node=new Node(4);
			//Connecting output layer nodes with hidden layer nodes
			for(int j=0;j<hiddenNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
				node.parents.add(nwp);
			}	
			outputNodes.add(node);
		}	
	}

	/**
	 * Get the output from the neural network for a single instance
	 * Return the idx with highest output values. For example if the outputs
	 * of the outputNodes are [0.1, 0.5, 0.2], it should return 1. If outputs
	 * of the outputNodes are [0.1, 0.5, 0.5], it should return 2. 
	 * The parameter is a single instance. 
	 */

	public ArrayList<Double> calcInstance(Instance inst){
		ArrayList<Double> outputs = new ArrayList<Double>();
		for(int i = 0; i<inst.attributes.size();i++){
			inputNodes.get(i).setInput(inst.attributes.get(i));
		}
		for(Node hidNode : hiddenNodes){

			hidNode.calculateOutput();

		}

		for(Node outNode : outputNodes){
			outNode.calculateOutput();
			outputs.add(outNode.getOutput());
		}
		return outputs;
	}

	private void initialize(Instance inst){
		for(int i = 0; i<inst.attributes.size();i++){
			inputNodes.get(i).setInput(inst.attributes.get(i));
		}
		for(Node hidNode : hiddenNodes){
			hidNode.calculateOutput();
		}
		for(Node outNode : outputNodes){
			outNode.calculateOutput();
		}

	}

	public int calculateOutputForInstance(Instance inst)
	{
		ArrayList<Double> vals = calcInstance(inst);
		Double max = 0.00;
		int maxIndex = -1;
		for(int i=0;i<vals.size();i++){

			if(vals.get(i)>=max){
				max = vals.get(i);
				maxIndex = i;
			}
		}
		return maxIndex;
		// TODO: add code here
	}

	/**
	 * Train the neural networks with the given parameters
	 * 
	 * The parameters are stored as attributes of this class
	 */

	public void train()
	{
		// TODO: add code here
		JFrame f = new JFrame("Loading Bar");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = f.getContentPane();
		JProgressBar progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		Border border = BorderFactory.createTitledBorder("Training...");
		progressBar.setBorder(border);
		content.add(progressBar, BorderLayout.NORTH);
		f.setSize(300, 100);
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints cons = new GridBagConstraints();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		
		
		for(int i=0;i<maxEpoch;i++){
			if(i%(maxEpoch/100)==0) progressBar.setValue(100*i/maxEpoch);

			for(Instance inst: trainingSet){
				initialize(inst);
				int k = 0;
				for(Node o : outputNodes){
					Double ok = o.getOutput();
					if(ok>0){
						Double tk = inst.classValues.get(k);
						for(NodeWeightPair parent : o.parents){
							Double aj = parent.node.getOutput();
							parent.weight += learningRate*aj*(tk-ok);
						}
					}
					k++;
				}
				int j = 0;
				for(Node h : hiddenNodes){
					if(j<hiddenNodes.size()-1){
						Double aj = h.getOutput();
						k=0;
						for(NodeWeightPair parent : h.parents){
							Double sumK = 0.00;
							for(Node o : outputNodes){
								Double wjk = 0.00;
								for(NodeWeightPair parents : o.parents){
									if(parents.node.equals(h)){
										wjk = parents.weight;
									}
								}
								Double ok = o.getOutput();

								Double tk = inst.classValues.get(outputNodes.indexOf(o));
								if(ok>0) sumK += wjk*(tk-ok);
							}
							k++;
							Double ai = parent.node.getOutput();
							if(aj>0) parent.weight += learningRate*ai*sumK;
						}
					}
					j++;
				}
			}
		}

	}
}

