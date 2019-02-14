package com.bateman.richard.evonn.lib.experiment;

import com.bateman.richard.math.RNG;

public class ExperimentConfig {
    public enum MutationType {
        NONE,
        MODIFY_WEIGHT,
        ADD_LINK,
        ADD_NODE,
        DELETE_LINK,
        DELETE_NODE,
    }

    public static final double DEFAULT_DESIRED_FITNESS = 0.80;
    public static final int DEFAULT_MAX_POOL_COUNT = 500;
    public static final int DEFAULT_BEST_AGENTS_COUNT = 100;
    public static final int DEFAULT_POOL_SIZE = 25;
    public static final int DEFAULT_NEXT_GEN_NUM_TO_PRESERVE = 5;
    public static final int DEFAULT_NEXT_GEN_NUM_TO_BREED = 15;
    public static final int DEFAULT_NEXT_GEN_NUM_TO_MUTATE_SIMPLE = 5;
    public static final int DEFAULT_NUM_NEW_POOLS_TO_CREATE = 10;

    public static final int DEFAULT_NUM_GENERATION_ITERATIONS = 100;

    public static final int DEFAULT_MUTATION_AMOUNT_MODIFY_WEIGHT = 100;
    public static final int DEFAULT_MUTATION_AMOUNT_ADD_LINK = 10;
    public static final int DEFAULT_MUTATION_AMOUNT_ADD_NODE = 10;
    public static final int DEFAULT_MUTATIONA_MOUNT_DELETE_LINK = 5;
    public static final int DEFAULT_MUTATION_AMOUNT_DELETE_NODE = 5;

    public static final boolean DEFAULT_ENABLE_NETWORK_VERIFICATIONS = true;

    /// <summary>
    /// Whether the network should perform verification on its topology to look for errors.
    /// This is a feature useful for debugging, but shouldn't be enabled when performance is a concern.
    /// </summary>
    public boolean EnableNetworkVerifications;

    /// <summary>
    /// Defines the key stopping criteria for the algorithm.  It will keep training until it
    /// evolves a network with the desired fitness.
    /// </summary>
    public double DesiredFitness;

    public int NumInputs;
    public int NumOutputs;

    public int MaxPoolCount;
    public int BestAgentCount;
    public int PoolSize;

    public int NextGenNumToPreserve;
    public int NextGenNumToBreed;
    public int NextGenNumToMutateSimple;
    public int NumNewPoolsToCreate;

    public int NumGenerationIterations;



    private int m_mutationAmountTotal = 0;

    private int m_mutationAmountModifyWeightScaled = 0;
    private int m_mutationAmountAddLinkScaled = 0;
    private int m_mutationAmountAddNodeScaled = 0;
    private int m_mutationAmountDeleteLinkScaled = 0;
    private int m_mutationAmountDeleteNodeScaled = 0;

    private int m_mutationAmountModifyWeight = 0;
    private int m_mutationAmountAddLink = 0;
    private int m_mutationAmountAddNode = 0;
    private int m_mutationAmountDeleteLink = 0;
    private int m_mutationAmountDeleteNode = 0;

    /**
     * Initializes the experiment configuration.
     * Arguments supplied to the constructor represent non-optional parameters
     * (i.e., parameters for which no sensible default exists).
     * @param numInputs
     * @param numOutputs
     */
    public ExperimentConfig(int numInputs, int numOutputs) {
        EnableNetworkVerifications = DEFAULT_ENABLE_NETWORK_VERIFICATIONS;

        DesiredFitness = DEFAULT_DESIRED_FITNESS;

        NumInputs = numInputs;
        NumOutputs = numOutputs;

        MaxPoolCount = DEFAULT_MAX_POOL_COUNT;
        BestAgentCount = DEFAULT_BEST_AGENTS_COUNT;
        PoolSize = DEFAULT_POOL_SIZE;

        NumGenerationIterations = DEFAULT_NUM_GENERATION_ITERATIONS;

        m_mutationAmountModifyWeight = DEFAULT_MUTATION_AMOUNT_MODIFY_WEIGHT;
        m_mutationAmountAddLink = DEFAULT_MUTATION_AMOUNT_ADD_LINK;
        m_mutationAmountAddNode = DEFAULT_MUTATION_AMOUNT_ADD_NODE;
        m_mutationAmountDeleteLink = DEFAULT_MUTATIONA_MOUNT_DELETE_LINK;
        m_mutationAmountDeleteNode = DEFAULT_MUTATION_AMOUNT_DELETE_NODE;

        NextGenNumToPreserve = DEFAULT_NEXT_GEN_NUM_TO_PRESERVE;
        NextGenNumToBreed = DEFAULT_NEXT_GEN_NUM_TO_BREED;
        NextGenNumToMutateSimple = DEFAULT_NEXT_GEN_NUM_TO_MUTATE_SIMPLE;

        NumNewPoolsToCreate = DEFAULT_NUM_NEW_POOLS_TO_CREATE;
    }

    public void setMutationAmountModifyWeight(int value) {
        m_mutationAmountModifyWeight = value;
        updateMutationAmounts();
    }

    public void setMutationAmountAddLink(int value) {
        m_mutationAmountAddLink = value;
        updateMutationAmounts();
    }

    public void setMutationAmountAddNode(int value) {
        m_mutationAmountAddNode = value;
        updateMutationAmounts();
    }

    public void setMutationAmountDeleteLink(int value) {
        m_mutationAmountDeleteLink = value;
        updateMutationAmounts();
    }

    public void setMutationAmountDeleteNode(int value) {
        m_mutationAmountDeleteNode = value;
        updateMutationAmounts();
    }

    /**
     * Choose a random mutation type, based on the amounts assigned to each type.
     * The higher an amount, the more likely that type will be selected.
     * @return
     */
    public MutationType chooseMutationType() {
        int selection = (int) RNG.rnd(m_mutationAmountTotal);

        if (selection < m_mutationAmountModifyWeightScaled) return MutationType.MODIFY_WEIGHT;
        if (selection < m_mutationAmountAddLinkScaled) return MutationType.ADD_LINK;
        if (selection < m_mutationAmountAddNodeScaled) return MutationType.ADD_NODE;
        if (selection < m_mutationAmountDeleteLinkScaled) return MutationType.DELETE_LINK;
        if (selection < m_mutationAmountDeleteNodeScaled) return MutationType.DELETE_NODE;

        return MutationType.NONE;
    }

    private void updateMutationAmounts() {
        m_mutationAmountTotal = m_mutationAmountModifyWeight +
                m_mutationAmountAddLink +
                m_mutationAmountAddNode +
                m_mutationAmountDeleteLink +
                m_mutationAmountDeleteNode;

        m_mutationAmountModifyWeightScaled = m_mutationAmountModifyWeight;
        m_mutationAmountAddLinkScaled = m_mutationAmountModifyWeightScaled + m_mutationAmountAddLink;
        m_mutationAmountAddNodeScaled = m_mutationAmountAddLinkScaled + m_mutationAmountAddNode;
        m_mutationAmountDeleteLinkScaled = m_mutationAmountAddNodeScaled + m_mutationAmountDeleteLink;
        m_mutationAmountDeleteNodeScaled = m_mutationAmountDeleteLinkScaled + m_mutationAmountDeleteNode;
    }
}
