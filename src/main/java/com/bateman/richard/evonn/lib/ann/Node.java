package com.bateman.richard.evonn.lib.ann;

import java.util.HashMap;

/**
 * Represents a node within a neural network
 */
public class Node {

    /**
     * Describes how a node functions within its network.
     */
    public enum NodeRole {
        INPUT,
        BIAS,
        HIDDEN,
        OUTPUT,
    }

    private final HashMap<Integer, Link> m_linksIncoming = new HashMap<>();
    private final HashMap<Integer, Link> m_linksOutgoing = new HashMap<>();
    private final int m_id;
    private NodeRole m_nodeRole;
    private double m_incomingActivity;
    private double m_activationCurrent;
    private double m_activationPrevious;

    public Node(int id) {
        m_id = id;
    }

    /**
     * The links coming into this node.  The key is the id of the source node.
     * @return
     */
    public HashMap<Integer, Link> getLinksIncoming() {
        return m_linksIncoming;
    }

    /**
     * The links leaving this node.  The key is the id of the target node.
     * @return
     */
    public HashMap<Integer, Link> getLinksOutgoing() {
        return m_linksOutgoing;
    }

    /**
     * A unique id for this node across the entire application.
     * @return
     */
    public int getId() {
        return m_id;
    }

    /**
     * The function this node performs in the network.
     * @return
     */
    public NodeRole getNodeRole() {
        return m_nodeRole;
    }

    public void setNodeRole(NodeRole nodeRole) {
        m_nodeRole = nodeRole;
    }

    /**
     * For all nodes that feed into this node, the output of that node * the weight of the link to this node.
     * That is summed across all incoming nodes.
     * @return
     */
    public double getIncomingActivity() {
        return m_incomingActivity;
    }

    public void setIncomingActivity(double incomingActivity) {
        m_incomingActivity = incomingActivity;
    }

    public double getActivationCurrent() {
        return m_activationCurrent;
    }

    public void setActivationCurrent(double activationCurrent) {
        m_activationCurrent = activationCurrent;
    }

    public double getActivationPrevious() {
        return m_activationPrevious;
    }

    public void setActivationPrevious(double activationPrevious) {
        m_activationPrevious = activationPrevious;
    }

    /**
     * Makes a copy of the node.  Links are shallow copied (and should be deep copied by the caller).
     * The id is copied as well.  Any activation current or previous is cleared.
     * @return
     */
    public Node copy() {
        Node copy = new Node(m_id);
        copy.m_nodeRole = m_nodeRole;
        m_linksIncoming.forEach((k,v)-> copy.m_linksIncoming.put(k, v));
        m_linksOutgoing.forEach((k,v)-> copy.m_linksOutgoing.put(k, v));
        return copy;
    }

    @Override
    public String toString() {
        return ANNIO.ToStringRepresentation(this);
    }
}
