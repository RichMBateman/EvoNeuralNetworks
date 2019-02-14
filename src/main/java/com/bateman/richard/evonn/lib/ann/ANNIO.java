package com.bateman.richard.evonn.lib.ann;

import com.bateman.richard.io.TextFileHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ANNIO {

    private static final String FILE_REP_COMMENT = "#";
    private static final String LABEL_NETWORK_ID="Network #";
    private static final String LABEL_NODE_COUNT = "Node Count: ";
    private static final String LABEL_NODE_ID = "Node #";
    private static final String LABEL_LINK_COUNT = "Link Count: ";
    private static final String LABEL_LINK_ID = "Link #";
    private static final String LINE_DELIMITER = ",";

    /**
     * Creates a network from a list of strings.
     * Assumes that comment strings have been removed.
     * @param network
     * @param lines
     */
    public static void fromStringRepresentation(Network network, ArrayList<String> lines)
    {
        int lineIndex = 0;
        network.setId(Integer.parseInt(lines.get(lineIndex++).replace(LABEL_NETWORK_ID, "")));

        int numNodes = Integer.parseInt(lines.get(lineIndex++).replace(LABEL_NODE_COUNT, ""));
        for (int n = 0; n < numNodes; n++)
        {
            String[] lineSplit = lines.get(lineIndex++).split(LINE_DELIMITER);
            Node node = new Node(Integer.parseInt(lineSplit[0].replace(LABEL_NODE_ID, "")));
            node.setNodeRole(Node.NodeRole.valueOf(lineSplit[1]));
            network.addNode(node);
        }

        int numLinks = Integer.parseInt(lines.get(lineIndex++).replace(LABEL_LINK_COUNT, ""));
        for (int n = 0; n < numLinks; n++)
        {
            String[] lineSplit = lines.get(lineIndex++).split(LINE_DELIMITER);
            Link link = new Link(Integer.parseInt(lineSplit[0].replace(LABEL_LINK_ID, "")));
            link.setNodeIn(network.getMapIdToAllNodes().get(Integer.parseInt(lineSplit[1])));
            link.setNodeOut(network.getMapIdToAllNodes().get(Integer.parseInt(lineSplit[2])));
            link.setWeight(Double.parseDouble(lineSplit[3]));
            network.addLink(link);
        }
    }

    /**
     * Generates a list of networks given a filepath.  Ignores all comments in the file.
     * @param filepath
     * @return
     * @throws IOException
     */
    public static List<Network> loadFromFile(String filepath) throws IOException {
        ArrayList<Network> allNets = new ArrayList<>();
        ArrayList<String> allLines = TextFileHelper.readFile(filepath);
        int index = 0;
        while (index < allLines.size())
        {
            ArrayList<String> netLines = new ArrayList<>();
            String line = allLines.get(index);
            while (line != "")
            {
                if (!line.startsWith(FILE_REP_COMMENT))
                {
                    netLines.add(line);
                }
                index++;
                line = allLines.get(index);
            }
            Network n = new Network(netLines);
            allNets.add(n);
            index++;
        }
        return allNets;
    }

    /**
     * Represents this network as a string.
     * @param network
     * @return
     */
    public static String toStringRepresentation(Network network) {
        StringBuilder sb = new StringBuilder();
        sb.append(LABEL_NETWORK_ID + network.getId() + System.lineSeparator());
        sb.append(LABEL_NODE_COUNT + network.getNodeCount() + System.lineSeparator());
        for (Node node : network.getMapIdToAllNodes().values())
        {
            sb.append(toStringRepresentation(node) + System.lineSeparator());
        }

        sb.append(LABEL_LINK_COUNT + network.getLinkCount() + System.lineSeparator());
        for (Link link : network.getMapIdToAllLinks().values())
        {
            sb.append(toStringRepresentation(link) + System.lineSeparator());
        }

        return sb.toString();
    }

    public static String toStringRepresentation(Node node){
        return LABEL_NODE_ID + node.getId() + ", " + node.getNodeRole();
    }

    public static String toStringRepresentation(Link link) {
        return LABEL_LINK_ID + link.getId() + LINE_DELIMITER +
                link.getNodeIn().getId() + LINE_DELIMITER +
                link.getNodeOut().getId() + LINE_DELIMITER +
                link.getWeight();
    }

    /**
     * Saves a list of networks to a file.
     * @param networks
     * @param filepath
     * @throws IOException
     */
    public static void saveToFile(List<Network> networks, String filepath) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Network n : networks)
        {
            sb.append(n.toString());
            sb.append(System.lineSeparator());
        }
        TextFileHelper.writeFile(filepath, sb.toString());
    }
}
