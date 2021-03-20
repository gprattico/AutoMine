package com.automine;

public class Main {

    public static final String MINE_LOC = "MINE_LOC";
    public static final String ETH_MINER_NAME = "lolMiner.exe";
    private static final String ETH_BATCH_NAME = "mine_eth.bat";


    public static void main(String[] args) {
        System.out.println("Welcome to AutoMine "+System.getProperty("user.name"));
        System.out.println("Checking environment variable "+MINE_LOC);

        Miner ethMiner = new Miner(System.getenv("MINE_LOC"), ETH_BATCH_NAME, ETH_MINER_NAME);

        if(!ethMiner.validateLocation()) endProgram(-1);

        if (ethMiner.IsRunning()){
            System.out.println("Found miner "+ethMiner.getMinerExecutableName()+" already running.");
            ethMiner.killCurrentRunningMiner();
        }


        endProgram(0);
    }

    public static void endProgram(int statusCode){
        System.out.println("Ending Program");
        System.exit(statusCode);
    }
}
