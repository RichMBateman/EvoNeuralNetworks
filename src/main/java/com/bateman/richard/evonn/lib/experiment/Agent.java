package com.bateman.richard.evonn.lib.experiment;

import com.bateman.richard.evonn.lib.ann.Network;

/**
 * Represents an intelligent (we hope) actor in an experiment.
 * Uses a neural network to make decisions.
 */
public class Agent {

    private final Network m_network;
    private int m_age;
    private double m_fitnessScore;

    public Agent(Network network) {
        m_network=network;
        m_age = 0;
    }

    /**
     * The neural network brain of this agent.
     * @return
     */
    public Network getNetwork() {
        return m_network;
    }

    /**
     * How old is this agent?
     * @return
     */
    public int getAge() {
        return m_age;
    }

    public void setAge(int age) {
        m_age = age;
    }

    /**
     * A score representing the latest fitness of this individual.
     * @return
     */
    public double getFitnessScore() {
        return m_fitnessScore;
    }

    public void setFitnessScore(double fitnessScore) {
        m_fitnessScore = fitnessScore;
    }

    public Agent deepCopy() {
        Network n = m_network.copy();
        Agent a = new Agent(n);
        a.m_fitnessScore = m_fitnessScore;
        return a;
    }

    @Override
    public String toString() {
        return "Id#" + m_network.getId() + ", F=" + m_fitnessScore;
    }
}
