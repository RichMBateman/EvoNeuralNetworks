package com.bateman.richard.evonn.lib.ann;

/**
 * Describes a unidirectional connection between two nodes.
 */
public class Link {
    private final int m_id;
    private Node m_nodeIn;
    private Node m_nodeOut;
    private double m_weight;

    /**
     * Creates a new link with the supplied id.
     * @param id
     */
    public Link(int id) {
        m_id=id;
    }

    /**
     * A unique id for this link across the entire application.  Meant for debugging purposes, and to
     * differentiate the object amonst others.
     * @return
     */
    public int getId() {return m_id;}

    /**
     * All links are directed.  This represents the node that feeds into this link.
     * (i.e., this node's output value will multiply by this link's weight, and will form part
     * of the input of NodeOut)
     * @return
     */
    public Node getNodeIn() {
        return m_nodeIn;
    }

    public void setNodeIn(Node nodeIn) {
        m_nodeIn = nodeIn;
    }

    /**
     * All links are directed.  This represents the node that this link feeds into.
     * (i.e., this node's input will represent the sum of the output of all incoming links)
     * @return
     */
    public Node getNodeOut() {
        return m_nodeOut;
    }

    public void setNodeOut(Node nodeOut) {
        m_nodeOut = nodeOut;
    }

    /**
     * The weight of this link.
     * @return
     */
    public double getWeight() {
        return m_weight;
    }

    public void setWeight(double weight) {
        m_weight = weight;
    }

    /**
     * Returns a copy of the link (shallow copies of the connecting nodes).
     * The id is also copied.
     * @return
     */
    public Link copy() {
        Link copy = new Link(m_id);
        copy.m_nodeIn = m_nodeIn;
        copy.m_nodeOut = m_nodeOut;
        copy.m_weight = m_weight;

        return copy;
    }

    @Override
    public String toString() {
        return ANNIO.toStringRepresentation(this);
    }
}
