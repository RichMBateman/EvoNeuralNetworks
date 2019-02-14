package com.bateman.richard.evonn.lib.ann;

import com.bateman.richard.math.NNMath;
import com.bateman.richard.math.RNG;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Representation of a neural network.
 */
public class Network {
    public static final String NODE_NAME_PREFIX_INPUT = "Input";
    public static final String NODE_NAME_PREFIX_BIAS = "Bias";
    public static final String NODE_NAME_PREFIX_HIDDEN = "Hidden";
    public static final String NODE_NAME_PREFIX_OUTPUT = "Output";

    public static final double LINK_WEIGHT_INIT_MIN = -1;
    public static final double LINK_WEIGHT_INIT_MAX = +1;
    private static int s_networkUniqueIdGenerator = 0;
    private int m_id;

    /**
     * A dictionary of all nodes, based on Id.
     */
    private final HashMap<Integer, Node> m_mapIdToAllNodes = new HashMap<>();

    /**
     * A list of input nodes, excluding the bias.  This list is only populated once, and its contents never change.
     */
    private final ArrayList<Node> m_nodesInput = new ArrayList<>();

    /**
     * The one bias node that will ever exist.  If the input vector presented to the network is all 0, the bias neuron gives
     * any output or hidden neurons a chance to activate.
     */
    private Node m_nodeBias;

    /**
     * A list of input nodes and the bias.  Only populated once.  For convenience when we need to iterate
     * over input and bias together.  Only populated once.
     */
    private final ArrayList<Node> m_nodesInputAndBias = new ArrayList<>();

    /**
     * The list of hidden nodes.  This list will grow or shrink as the network evolves.
     */
    private final ArrayList<Node> m_nodesHidden = new ArrayList<>();

    /**
     * A list of output nodes.  Populated once; never changes.
     */
    private final ArrayList<Node> m_nodesOutput = new ArrayList<>();

    /**
     * All links in the system.
     */
    private final HashMap<Integer, Link> m_mapIdToAllLinks = new HashMap<>();

    /**
     * This mutator is responsible for adjusting the network.
     */
    private NetworkMutator m_mutator;

    /**
     * An id generator for nodes in the network
     */
    private int m_idGeneratorNode;
    /**
     * An id generator for links in the network.
     */
    private int m_idGeneratorLink;

    /// <summary>
    /// Create an empty network.
    /// </summary>

    /**
     * Create an empty network
     */
    public Network(){
        m_id = s_networkUniqueIdGenerator;
        s_networkUniqueIdGenerator++;
    }

    /**
     * Creates a network from a string representation.
     * @param lines
     */
    public Network(ArrayList<String> lines) {
        ANNIO.fromStringRepresentation(this, lines);
        s_networkUniqueIdGenerator++;
    }

    /**
     *  Creates a fully-connected, minimal network with a number of input and output neurons.
     * @param numInputs
     * @param numOutputs
     */
    public Network(int numInputs, int numOutputs) {
        this();
        createMinimalNetwork(numInputs, numOutputs);
    }

    /**
     * A unique id number for this neural network, to help distinguish it from others.
     * @return
     */
    public int getId() {
        return m_id;
    }

    public void setId(int id) {m_id = id;}

    /**
     * Dictionary of all nodes (mapped by id)
     * @return
     */
    public HashMap<Integer, Node> getMapIdToAllNodes() {
        return m_mapIdToAllNodes;
    }

    public ArrayList<Node> getNodesInput() {
        return m_nodesInput;
    }

    public Node getNodeBias() {
        return m_nodeBias;
    }

    /**
     * Input nodes (including bias)
     * @return
     */
    public ArrayList<Node> getNodesInputAndBias() {
        return m_nodesInputAndBias;
    }

    /**
     * Hidden nodes
     * @return
     */
    public ArrayList<Node> getNodesHidden() {
        return m_nodesHidden;
    }

    /**
     * A list of the output nodes for this network.
     * @return
     */
    public ArrayList<Node> getNodesOutput() {
        return m_nodesOutput;
    }

    /**
     * Dictionary of all links (mapped by id)
     * @return
     */
    public HashMap<Integer, Link> getMapIdToAllLinks() {
        return m_mapIdToAllLinks;
    }

    /**
     * A module capable of mutating this network.
     * @return
     */
    public NetworkMutator getMutator() {
        if(m_mutator == null) {
            m_mutator = new NetworkMutator(this);
        }
        return m_mutator;
    }

    /**
     * The total count of all nodes.
     * @return
     */
    public int getNodeCount() {
        return m_mapIdToAllNodes.size();
    }

    /**
     * The total count of all links.
     * @return
     */
    public int getLinkCount() {
        return m_mapIdToAllLinks.size();
    }

    /**
     * Adds this node to the network.
     * @param n
     */
    public void addNode(Node n) {
        m_mapIdToAllNodes.put(n.getId(), n);
        switch(n.getNodeRole())
        {
            case INPUT:
                m_nodesInput.add(n);
                m_nodesInputAndBias.add(n);
                break;
            case BIAS:
                m_nodeBias = n;
                m_nodesInputAndBias.add(n);
                n.setActivationCurrent(1.0);
                n.setActivationPrevious(1.0);
                break;
            case HIDDEN:
                m_nodesHidden.add(n);
                break;
            case OUTPUT:
                m_nodesOutput.add(n);
                break;
        }
    }

    /**
     * Adds this link to the network and hooks up the nodes to it.
     * @param link
     */
    public void addLink(Link link) {
        m_mapIdToAllLinks.put(link.getId(), link);
        m_mapIdToAllNodes.get(link.getNodeIn().getId()).getLinksOutgoing().put(link.getNodeOut().getId(), link);
        m_mapIdToAllNodes.get(link.getNodeOut().getId()).getLinksIncoming().put(link.getNodeIn().getId(), link);
    }

    /**
     * Removes a hidden node from the network
     * @param n
     */
    public void removeHiddenNode(Node n){
        m_mapIdToAllNodes.remove(n.getId());
        m_nodesHidden.remove(n);
    }

    /**
     * Removes this link from the network.  Assumes no node refers to this link anymore.
     * @param link
     */
    public void removeLink(Link link) {
        m_mapIdToAllLinks.remove(link.getId());
    }

    /// <summary>
    /// Computes the activation for the given input vector.
    /// </summary>
    /// <param name="inputVector">Ordered array of inputs.  (Bias is always 1)</param>

    /**
     * Computes the activation for the given input vector.
     * @param inputVector Ordered array of inputs.  (Bias input is always 1 and shouldn't be included in this array)
     * @return
     */
    public ArrayList<Double> computeActivation(ArrayList<Double> inputVector){
        // First, update the activation of all input neurons, remembering the previous activation.
        UpdateInputLayerActivation(inputVector);

        // Figure out activation of output layer by recursively figuring out output of all inputs,
        // one level at a time.  If we have to ask for the activation of a neuron more than once,
        // we use the previous activation (must have a recurrent link)
        for (Node outputNode : m_nodesOutput)
        {
            ArrayList<Node> previousNodeRequests = new ArrayList<>();
            computeActivation(outputNode, previousNodeRequests);
        }

        ArrayList<Double> activations = new ArrayList<Double>();
        for (int o = 0; o < m_nodesOutput.size(); o++)
        {
            activations.add(m_nodesOutput.get(o).getActivationCurrent());
        }
        return activations;
    }

    /**
     * Iterates through all nodes in the network, looking for errors.
     * All nodes and links should make sense.
     */
    public void verifyNetworkConnectivity(){
        // iterate over all known nodes and check connectivity.
        // Each pair of nodes better agree they share the same state of connectivity.
        for(Node nSrc : m_mapIdToAllNodes.values())
        {
            for (Node nTgt : m_mapIdToAllNodes.values())
            {
                areNodesConnected(nSrc, nTgt);
            }
        }

        // Iterate over all links, looking for problems.
        for(Link l : m_mapIdToAllLinks.values())
        {
            // We better know about all nodes each link is referring to
            if (!m_mapIdToAllNodes.containsKey(l.getNodeIn().getId())) throw new InvalidStateException("Cannot find a node.");
            if (!m_mapIdToAllNodes.containsKey(l.getNodeOut().getId())) throw new InvalidStateException("Cannot find a node");
            // If the nodes in the link are not actually connected, that's a problem.
            if (!areNodesConnected(l.getNodeIn(), l.getNodeOut())) throw new InvalidStateException("Nodes are not connected.");
        }

        // Count all the links (each link should be counted twice).  Should match the links we know about.
        int linkCount = 0;
        for(Node n : m_mapIdToAllNodes.values()) {
            linkCount += n.getLinksIncoming().size();
            linkCount += n.getLinksOutgoing().size();
        }
        if(linkCount != m_mapIdToAllLinks.size() * 2) {
            throw new InvalidStateException("Unexpected link count found.");
        }
    }

    /**
     * Returns a deep copy of this network, with identical weights and id numbers.
     * @return
     */
    public Network copy() {
        Network copyNetwork = new Network();
        copyNetwork.m_idGeneratorLink = m_idGeneratorLink;
        copyNetwork.m_idGeneratorNode = m_idGeneratorNode;
        CopyNodesToNetwork(copyNetwork);
        copyLinksToNetwork(copyNetwork);
        copyNetworkUpdateNodeShallowLinks(copyNetwork);

        copyNetwork.verifyNetworkConnectivity();

        return copyNetwork;
    }

    /**
     * String representation of this network
     * @return
     */
    @Override
    public String toString()
    {
        return ANNIO.toStringRepresentation(this);
    }

    /**
     * Randomizes all weights on the network
     */
    public void randomizeWeights()
    {
        for (Link l : m_mapIdToAllLinks.values()) {
           randomizeLinkWeight(l);
        }
    }

    private void createMinimalNetwork(int numInputs, int numOutputs) {
        createMinimalNetworkNodes(numInputs, numOutputs);
        createMinimalNetworkLinks();
    }

    private void createMinimalNetworkNodes(int numInputs, int numOutputs) {
        m_nodeBias = createNode();
        m_nodeBias.setNodeRole(Node.NodeRole.BIAS);
        addNode(m_nodeBias);

        for (int i = 0; i < numInputs; i++){
            Node input = createNode();
            input.setNodeRole(Node.NodeRole.INPUT);
            addNode(input);
        }

        for (int i = 0; i < numOutputs; i++){
            Node output = createNode();
            output.setNodeRole(Node.NodeRole.OUTPUT);
            addNode(output);
        }
    }

    private void createMinimalNetworkLinks(){
        for (Node input : m_nodesInputAndBias) {
            for (Node output : m_nodesOutput) {
                createNewLinkBetweenExistingNodes(input, output);
            }
        }
    }

    /**
     * Updates the ActivationPrevious with the Current activation,
     * and sets the ActivationCurrent to what's supplied from input vector.
     * The bias neuron is always 1 and doesn't need to change.
     * @param inputVector
     */
    private void updateInputLayerActivation(ArrayList<Double> inputVector) {
        for (int i = 0; i < m_nodesInput.size(); i++) {
            m_nodesInput.get(i).setActivationPrevious(m_nodesInput.get(i).getActivationCurrent());
            m_nodesInput.get(i).setActivationCurrent(inputVector.get(i));
        }
        // The bias node is permanently set to 1, so no need to set again.
    }

    /**
     * Computes the activation of node n.  We initially start with output neurons, and work
     * our way backward.
     * @param n
     * @param previousNodeRequests
     */
    private void computeActivation(Node n, List<Node> previousNodeRequests) {
        if (n.getNodeRole() == Node.NodeRole.INPUT || n.getNodeRole() == Node.NodeRole.BIAS) {
            // It doesn't make sense to compute the activation of Input and Bias nodes.
            // The activation of the Bias node is always 1
            // The activation of the input node comes from an external input vector.
            return;
        }

        n.setIncomingActivity(0);
        for (Link l : n.getLinksIncoming().values()) {
            double activationContribution = 0;
            if(previousNodeRequests.contains(l.getNodeIn())) {
                // We've already asked about this node, so we must have hit a recurrent loop.
                // Use the previous activation, and stop asking.
                activationContribution = l.getNodeIn().getActivationPrevious() * l.getWeight();
            }
            else {
                previousNodeRequests.add(l.getNodeIn());
                computeActivation(l.getNodeIn(), previousNodeRequests);
                // After computing, pop the last item off the list
                previousNodeRequests.remove(previousNodeRequests.size() - 1);
                activationContribution = l.getNodeIn().getActivationCurrent() * l.getWeight();
            }
            n.setIncomingActivity(n.getIncomingActivity() + activationContribution);
        }

        n.setActivationPrevious(n.getActivationCurrent());
        n.setActivationCurrent(NNMath.sigmoidal_0_1(n.getIncomingActivity()));
    }

    private void copyNodesToNetwork(Network other) {
        for (Node n : m_mapIdToAllNodes.values()) {
            Node copyNode = n.copy();
            switch (copyNode.getNodeRole())
            {
                case INPUT:
                    other.m_nodesInput.add(copyNode);
                    other.m_nodesInputAndBias.add(copyNode);
                    break;
                case BIAS:
                    other.m_nodeBias = copyNode;
                    other.m_nodesInputAndBias.add(copyNode);
                    copyNode.setActivationCurrent(1);
                    copyNode.setActivationPrevious(1);
                    break;
                case HIDDEN:
                    other.m_nodesHidden.add(copyNode);
                    break;
                case OUTPUT:
                    other.m_nodesOutput.add(copyNode);
                    break;
            }
            other.m_mapIdToAllNodes.put(copyNode.getId(), copyNode);
        }
    }

    /**
     * Copies the link from the other network, and replaces shallow node copies with deep ones.
     * @param other
     */
    private void copyLinksToNetwork(Network other) {
        for (Link l : m_mapIdToAllLinks.values()) {
            Link copyLink = l.copy();
            other.m_mapIdToAllLinks.put(copyLink.getId(), copyLink);
            copyLink.setNodeIn(other.m_mapIdToAllNodes.get(copyLink.getNodeIn().getId()));
            copyLink.setNodeOut(other.m_mapIdToAllNodes.get(copyLink.getNodeOut().getId()));
        }
    }

    private void copyNetworkUpdateNodeShallowLinks(Network other) {
        for (Node n : other.m_mapIdToAllNodes.values())
        {
            List<Link> lDeepIncoming = new ArrayList<>();
            List<Link> lDeepOutgoing = new ArrayList<>();
            for(Link l : n.getLinksIncoming().values()) {
                lDeepIncoming.add(other.m_mapIdToAllLinks.get(l.getId()));
            }
            for (Link l : n.getLinksOutgoing().values()){
                lDeepOutgoing.add(other.m_mapIdToAllLinks.get(l.getId()));
            }
            // Remove shallow copies
            n.getLinksIncoming().clear();
            n.getLinksOutgoing().clear();

            // Add deep copies
            for (Link l : lDeepIncoming) {
                n.getLinksIncoming().put(l.getNodeIn().getId(), l);
            }
            for (Link l : lDeepOutgoing) {
                n.getLinksOutgoing().put(l.getNodeOut().getId(), l);
            }
        }
    }

    /**
     * Creates a new node with a new id unique to this network.
     * @return
     */
    public Node createNode() {
        Node n = new Node(m_idGeneratorNode++);
        return n;
    }

    /**
     * Creates a new link with an appropriate id.
     * @return
     */
    public Link createLink() {
        Link l = new Link(m_idGeneratorLink++);
        return l;
    }

    public void randomizeLinkWeight(Link l) {
        l.setWeight(RNG.rnd(LINK_WEIGHT_INIT_MIN, LINK_WEIGHT_INIT_MAX));
    }

    /**
     * Returns true iff the nodes are connected.
     * Will throw an exception if one node thinks it's connected to the other, but the other is
     * unaware of the connection.
     * @param sourceNode
     * @param targetNode
     * @return
     */
    public boolean areNodesConnected(Node sourceNode, Node targetNode) {
        boolean nodesConnected = false;

        boolean srcConnectsToTgt = sourceNode.getLinksOutgoing().containsKey(targetNode.getId());
        boolean tgtConnectsToSrc = targetNode.getLinksIncoming().containsKey(sourceNode.getId());

        nodesConnected = (srcConnectsToTgt && tgtConnectsToSrc);
        if(srcConnectsToTgt ^ tgtConnectsToSrc)
        {
            throw new InvalidStateException("Node connection mismatch.");
        }
        return nodesConnected;
    }

    public Link createNewLinkBetweenExistingNodes(Node source, Node target) {
        Link link = createLink();
        link.setNodeIn(source);
        link.setNodeOut(target);
        randomizeLinkWeight(link);

        m_mapIdToAllLinks.put(link.getId(), link);
        source.getLinksOutgoing().put(target.getId(), link);
        target.getLinksIncoming().put(source.getId(), link);

        return link;
    }
}
