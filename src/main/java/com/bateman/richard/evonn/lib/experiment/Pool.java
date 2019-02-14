package com.bateman.richard.evonn.lib.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A group of networks with identical topologies.
 */
public class Pool {
    private ArrayList<Agent> m_agentsBufferLive;
    private ArrayList<Agent> m_agentsBufferNextGen;
    private final ArrayList<Agent> m_agentsBufferA = new ArrayList<>();
    private final ArrayList<Agent> m_agentsBufferB = new ArrayList<>();

    /**
     * Returns the list of active agents in this pool.
     * @return
     */
    public ArrayList<Agent> getAgentsLive() {
        return m_agentsBufferLive;
    }

    public Pool() {
        m_agentsBufferLive = m_agentsBufferA;
        m_agentsBufferNextGen = m_agentsBufferB;
    }

    public void sortAgentsByFitness() {
        Collections.sort(m_agentsBufferLive, (a1, a2) -> Double.compare(a2.getFitnessScore(), a1.getFitnessScore()));
    }

    public void prepareForNextGen() {
        m_agentsBufferNextGen.clear();
    }

    public void addAgentToNextGen(Agent a) {
        m_agentsBufferNextGen.add(a);
    }

    public void makeNextGenLive() {
        ArrayList<Agent> previousLive = m_agentsBufferLive;
        m_agentsBufferLive = m_agentsBufferNextGen;
        m_agentsBufferNextGen = previousLive;
    }
}
