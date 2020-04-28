package com.cups.splashin.peer2party.networker.networking.singleton;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import networking.singleton.data.NodeData;

class NodeDataList {

    List<NodeData> nodeData;

    private final Object nodeDataLock = new Object();

    NodeDataList(){

    }

    void appendNodeData(String ip, Integer p, String alias){
        synchronized (nodeDataLock){
            nodeData.add(new NodeData(ip, p, alias));
        }
    }

    int getIndexOfIPPort(String ip, Integer p){
        OptionalInt res;

        synchronized (nodeDataLock){
            res = IntStream.range(0, nodeData.size())
                    .parallel()
                    .filter(i -> ip.equals(nodeData.get(i).getIP()))
                    .filter(i -> p.equals(nodeData.get(i).getPORT()))
                    .findFirst();
        }

        if (res.isPresent())
            return res.getAsInt();
        else
            return -1;
    }

    void removeIpPort(String ip, Integer p){
        synchronized (nodeDataLock){
            nodeData.stream().parallel()
                    .filter(nodeData1 -> ip.equals(nodeData1.getIP()))
                    .filter(nodeData1 -> p.equals(nodeData1.getPORT()))
                    .forEach(node -> nodeData.remove(node));
        }
    }

    boolean containsIP(String ip){
        synchronized (nodeDataLock){
            Optional<NodeData> result = nodeData.stream().parallel()
                    .filter(nodeData1 -> nodeData1.getIP().equals(ip))
                    .findFirst();
            return result.isPresent();
        }
    }

    boolean containsPORT(Integer p){
        synchronized (nodeDataLock){
            Optional<NodeData> result = nodeData.stream().parallel()
                    .filter(nodeData1 -> nodeData1.getPORT().equals(p))
                    .findFirst();

            return result.isPresent();
        }
    }

    String[][] getAllNodeAliasPort(){
        NodeData[] nodeArray;
        int len;

        synchronized (nodeDataLock){
            nodeArray = new NodeData[nodeData.size()];
            nodeData.toArray(nodeArray);
            len = nodeArray.length;
        }

        if(len > 1){
            String[][] nodeData  = new String[len - 1][2];

            int j, i = 1;
            for(NodeData node : nodeArray){
                j = 0;
                nodeData[i][j++] = node.getALIAS();
                nodeData[i++][j] = node.getPORT().toString();
            }

            return nodeData;
        }

        return null;
    }

}
