package com.automine;

import java.util.concurrent.TimeUnit;

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

        Runtime.getRuntime().addShutdownHook(new Thread(ethMiner::killCurrentRunningMiner));
        // start miner ourselves
        boolean programNotCancelled = true;
        while (programNotCancelled){
            if(!ethMiner.IsRunning())
                ethMiner.start();

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("An error occurred while we were checking the status of the miner.");
                Main.endProgram(-1);
            }

            System.out.println("Miner running ...");
        }


        endProgram(0);
    }

    public static void endProgram(int statusCode){
        System.out.println("Ending Program");
        System.exit(statusCode);
    }
}
