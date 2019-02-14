package com.bateman.richard.evonn.lib.experiment;

import com.bateman.richard.evonn.lib.ann.Link;
import com.bateman.richard.evonn.lib.ann.Network;
import com.bateman.richard.math.RNG;

import java.io.Console;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base class from which to extend to create an experiment.
 */
public abstract class ExperimentBase {
    /**
     * Whether this experiment has been initialized.
     */
    protected boolean m_isInitialized = false;

    /**
     * A list of the absolute best agents of all time.
     * We never use this list to populate pools; only as a reference.
     */
    protected final ArrayList<Agent> m_bestAgentsAllTime = new ArrayList<>();

    /**
     * A set of isolated populations.  Populations may grow in count until it hits a limit.
     * At that point, the worst performing populations will be killed.
     */
    protected final ArrayList<Pool> m_pools = new ArrayList<>();

    /**
     * The configuration for the experiment.
     */
    protected ExperimentConfig m_config;

    /**
     * The number of inputs each network will always have.  Excludes the bias.
     * This number never changes throughout the course of the experiment.
     */
    protected int m_numInputs;

    /**
     * The number of outputs each network will always have.
     * This number never changes throughout the course of the experiment.
     */
    protected int m_numOutputs;

    /**
     * The best performing agents that this experiment has seen.
     */
    public ArrayList<Agent> getBestAgents() {
        return m_bestAgentsAllTime;
    }

    /**
     * A method that should test how this agent performs in the scenario, and assigns it a fitness score.
     * @param a
     */
    protected abstract void EvaluateFitness(Agent a);

    public void run(){
        Instant starts = Instant.now();
        if (!m_isInitialized) throw new IllegalStateException("Failed to initialize experiment with a configuration.");

        createInitialPool();
        double bestFitness = getBestFitness();
        Instant stops = Instant.now();
        System.out.println("Elapsed MS: " + Duration.between(starts, stops));
        starts = Instant.now();
        while(bestFitness < m_config.DesiredFitness)
        {
            // For each pool, for some number of iterations:
            // >Evaluate the members of the pool
            // >Add the best performers to the list of top performers
            for (Pool p : m_pools)
            {
                int numGenerationIterations = m_config.NumGenerationIterations;
                while (numGenerationIterations > 0)
                {
                    EvaluatePool(p);
                    CreateNextGenForPool(p);
                    numGenerationIterations--;
                }
                // Do one final evaluation.
                EvaluatePool(p);
                p.sortAgentsByFitness();
                stops = Instant.now();
                System.out.println("Elapsed MS for Pool Generations: " + Duration.between(starts, stops));
                starts = Instant.now();
            }
            bestFitness = GetBestFitness();
            // At this point, all the pools have gone through many mutations, breeding, etc.
            // It's time to make some new populations, and eliminate old ones.
            EliminateWorstPoolsIfNecessary();
            CreatePoolsWithNewTopologies();

            System.out.println("Best fitness is: " + bestFitness);
            starts = Instant.now();
        }
    }

    /**
     * Initialize the experiment with a configuration.
     * @param config
     */
    protected void initializeWithConfig(ExperimentConfig config) {
        m_isInitialized = true;
        m_config = config;
        m_numInputs = config.NumInputs;
        m_numOutputs = config.NumOutputs;
    }

    /**
     * Creates the first pool, which is just the minimal network.
     */
    private void createInitialPool() {
        Pool p = new Pool();
        for(int agentIndex = 1; agentIndex <= m_config.PoolSize; agentIndex++)
        {
            Network nn = new Network(m_numInputs, m_numOutputs);
            nn.randomizeWeights();
            Agent a = new Agent(nn);
            p.getAgentsLive().add(a);
        }
        m_pools.add(p);
    }

    /**
     * Looks at the top performers and returns the absolute best fitness.
     *  If there are no top performers, returns the lowest fitness possible.
     * @return
     */
    private double getBestFitness() {
        double bestFitness = Double.MIN_VALUE;
        if(m_bestAgentsAllTime.size() > 0)
        {
            SortTopPerformersList();
            bestFitness = m_bestAgentsAllTime.get(0).getFitnessScore();
        }
        return bestFitness;
    }

    private void createNextGenForPool(Pool p) {
        p.sortAgentsByFitness();
        p.prepareForNextGen();
        // The best agents are preserved
        for(int i = 0; i < m_config.NextGenNumToPreserve; i++)
        {
            Agent elite = p.getAgentsLive().get(i);
            elite.setAge(elite.getAge() + 1);
            elite.setFitnessScore(0);
            p.addAgentToNextGen(elite);
        }
        // Some agents are bred
        for(int i = 0; i < m_config.NextGenNumToBreed; i++)
        {
            Agent parent1 = p.getAgentsLive().get((int) RNG.rnd(p.getAgentsLive().size()));
            Agent parent2 = p.getAgentsLive().get((int) RNG.rnd(p.getAgentsLive().size()));
            Agent child = BreedTwoAgents(parent1, parent2);
            child.setFitnessScore(0);
            p.addAgentToNextGen(child);
        }
        // Some agents are mutated (again, simple mutations)
        for (int i = 0; i < m_config.NextGenNumToMutateSimple; i++)
        {
            Agent victim = p.getAgentsLive().get((int) RNG.rnd(p.getAgentsLive().size())).deepCopy();
            MutateAgentSimple(victim);
            victim.setFitnessScore(0);
            p.addAgentToNextGen(victim);
        }

        p.makeNextGenLive();
    }

    private void EliminateWorstPoolsIfNecessary()
    {
        int maxPoolsForCreation = (m_config.MaxPoolCount - m_config.NumNewPoolsToCreate + 1);
        if (m_pools.Count >= maxPoolsForCreation)
        {
            m_pools.Sort(delegate (Pool a, Pool b)
            {
                return b.Agents[0].FitnessScore.CompareTo(a.Agents[0].FitnessScore);
            });
            while (m_pools.Count >= maxPoolsForCreation)
            {
                // Removes the last pool
                m_pools.RemoveAt(m_pools.Count - 1);
            }
        }
    }

    private void CreatePoolsWithNewTopologies()
    {
        List<Pool> newPools = new List<Pool>();
        int poolsToMake = m_config.NumNewPoolsToCreate;
        while(poolsToMake > 0)
        {
            Pool newPool = new Pool();
            Pool randomSelection = m_pools[RNG.Rnd(m_pools.Count)];
            Agent bestAgentTemplate = randomSelection.Agents[0].DeepCopy();
            double selection = RNG.Rnd();
            if(selection <= 0.40)
            {
                bestAgentTemplate.NeuralNetwork.Mutator.MutateNewNode();
            }
            else if(selection <= 0.80)
            {
                bestAgentTemplate.NeuralNetwork.Mutator.MutateNewLink();
            }
            else if(selection <= 0.90)
            {
                bestAgentTemplate.NeuralNetwork.Mutator.MutateDeleteLink();
            }
            else
            {
                bestAgentTemplate.NeuralNetwork.Mutator.MutateDeleteNode();
            }

            for(int i = 1; i <= m_config.PoolSize; i++)
            {
                Agent a = bestAgentTemplate.DeepCopy();
                if(RNG.Rnd() < 0.5)
                {
                    MutateAgentSimple(a);
                }
                else
                {
                    a.NeuralNetwork.RandomizeWeights();
                }
                newPool.Agents.Add(a);
            }
            newPools.Add(newPool);
            poolsToMake--;
        }
        m_pools.AddRange(newPools);
    }

    private void sortTopPerformersList() {
        m_bestAgentsAllTime.Sort(delegate (Agent a, Agent b)
        {
            return b.FitnessScore.CompareTo(a.FitnessScore);
        });
    }

    private void evaluatePool(Pool p)
    {
        for(Agent a : p.Agents)
        {
            evaluateFitness(a);
            checkEligibilityForBestPerformerList(a);
        }
    }

    private void checkEligibilityForBestPerformerList(Agent a)
    {
        if (m_bestAgentsAllTime.size() < m_config.BestAgentCount)
        {
            m_bestAgentsAllTime.add(a.deepCopy());
        }
        else
        {
            double lowestFitness = m_bestAgentsAllTime.last().getFitnessScore();
            if(a.getFitnessScore() > lowestFitness)
            {
                m_bestAgentsAllTime.remove(m_bestAgentsAllTime.size() - 1);
                m_bestAgentsAllTime.add(a.deepCopy());
                SortTopPerformersList();
            }
        }
    }

    /**
     * Performs a mutation that does not affect the topology of the network.
     * @param a
     */
    private void mutateAgentSimple(Agent a)
    {
        a.getNetwork().getMutator().mutateWeight();
    }

    /**
     * Takes two agents (with the same network topology) and breeds a child.
     * @param a
     * @param b
     * @return
     */
    private Agent breedTwoAgents(Agent a, Agent b){
        Agent child = a.deepCopy();
        ArrayList<Link> childLinks = new ArrayList<>(child.getNetwork().getMapIdToAllLinks().values());

        for(int l = 0; l < childLinks.size(); l++) {
            if(RNG.rnd() < 0.5){
                childLinks.get(l).setWeight(a.getNetwork().getMapIdToAllLinks().get(childLinks.get(l).getId()).getWeight());
            }
            else {
                childLinks.get(l).setWeight(b.getNetwork().getMapIdToAllLinks().get(childLinks.get(l).getId()).getWeight());
            }
        }

        return child;
    }

    private void mutateAgent(Agent a){
        ExperimentConfig.MutationType mutationToPerform = m_config.chooseMutationType();
        switch(mutationToPerform)
        {
            case ADD_LINK: a.getNetwork().getMutator().mutateNewLink(); break;
            case ADD_NODE: a.getNetwork().getMutator().mutateNewNode(); break;
            case DELETE_LINK: a.getNetwork().getMutator().mutateDeleteLink(); break;
            case DELETE_NODE: a.getNetwork().getMutator().mutateDeleteNode(); break;
            case MODIFY_WEIGHT: a.getNetwork().getMutator().mutateWeight(); break;
        }
    }
}
