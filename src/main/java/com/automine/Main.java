package com.automine;

import com.automine.overclocker.MSIAfterburner;
import sun.misc.Signal;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String MINE_LOC = "MINE_LOC";
    public static final Long hoursBetweenResets = 1L;
    public static final String ETH_MINER_NAME = "lolMiner.exe";
    private static final String ETH_BATCH_NAME = "mine_eth.bat";

    public static long applicationStartTime;


    public static void main(String[] args) {
        System.out.println("Welcome to AutoMine "+System.getProperty("user.name"));

        Miner ethMiner = new Miner(System.getenv("MINE_LOC"), ETH_BATCH_NAME, ETH_MINER_NAME, new MSIAfterburner());

        if(!ethMiner.validateLocation()) endProgram(-1);

        if (ethMiner.IsRunning()){
            System.out.println("Found miner "+ethMiner.getMinerExecutableName()+" already running.");
            ethMiner.killCurrentRunningMiner();
        }

        addShutdownListeners(ethMiner);


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        applicationStartTime = System.currentTimeMillis();

        // start miner ourselves
        boolean cancelled = false;
        while (!cancelled){
            if(!ethMiner.IsRunning()) {
                ethMiner.start();
            }

            try {
                System.out.println(LocalTime.now().format(dtf)+" Miner running ...");

                //edit this one for sleep delay between status checks
                //TimeUnit.HOURS.sleep(2);
                TimeUnit.MINUTES.sleep(2);
            } catch (InterruptedException e) {
                System.out.println("An error occurred while we were checking the status of the miner.");
                Main.endProgram(-1);
            }

            System.out.println("Checking status of miner.");

            if (ethMiner.shouldBeReset()) {
                System.out.println(hoursBetweenResets+ " hour(s) passed, restarting Miner.");
                ethMiner.killCurrentRunningMiner();
            }
        }

        endProgram(0);
    }

    private static void addShutdownListeners(Miner ethMiner) {
        //this is for control c
        Signal.handle(new Signal("INT"), signal -> {
            System.out.println("Interrupted by ctrl c");
            if (ethMiner.IsRunning()) ethMiner.killCurrentRunningMiner();
            endProgram(0);
        });
    }

    public static void endProgram(int statusCode){
        System.out.println("Ending Program");
        System.exit(statusCode);
    }
}
