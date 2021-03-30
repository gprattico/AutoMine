package com.automine;

import com.automine.exception.MinerNotKillableException;
import com.automine.overclocker.MSIAfterburner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Miner {

    private static final String CMD_EXE_NAME = "cmd.exe";
    private static final int TIME_FOR_DAG_TO_GEN = 20;
    private final String minerDirectory;
    private final String batchFileName;
    private final String minerExecutableName;
    private final MSIAfterburner overclocker;

    public Miner (String minerDirectory, String batchFileName, String minerExecutableName, MSIAfterburner overclocker){
        this.minerDirectory = minerDirectory;
        this.batchFileName = batchFileName;
        this.minerExecutableName = minerExecutableName;
        this.overclocker = overclocker;
    }

    /**
     * Check to see the miner start script exists in location specified by
     * environment variable @field
     * @return
     * true if the miner file exists
     * false if the miner file cannot be found or doesn't exist
     */
    public boolean validateLocation() {
        System.out.println("Checking environment variable "+Main.MINE_LOC);
        if (minerDirectory == null || minerDirectory.isEmpty()) {
            System.out.println("Environment variable " + Main.MINE_LOC + " is not set or is empty, please set it.");
            return false;
        }
        else if (!Files.exists(Paths.get(minerDirectory))) {
            System.out.println("Could not find the batch file for the miner,\n" +
                    "because the location provided from the environment\n" +
                    "variable " + Main.MINE_LOC + " does not exist. " + Main.MINE_LOC + " was\n" +
                    "set to: " + this.minerDirectory);
            return false;
        }
        else if (!(new File(minerDirectory + File.separator + batchFileName).exists())){
            System.out.println("Could not find the batch file for the miner,\n" +
                    "because the file "+ batchFileName +" does not exist in folder\n" +
                    minerDirectory +
                    "\nMake sure environment variable " + Main.MINE_LOC + " is correctly set");
            return false;
        }
        else {
            return true;
        }
    }

    public boolean IsRunning() {
        try {
            String findProcess = minerExecutableName;
            String filenameFilter = "/nh /fi \"Imagename eq " + findProcess + "\"";
            String tasksCmd = System.getenv("windir") + "/system32/tasklist.exe " + filenameFilter;

            Process p = Runtime.getRuntime().exec(tasksCmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            ArrayList<String> procs = new ArrayList<>();
            String line = null;
            while ((line = input.readLine()) != null)
                procs.add(line);

            input.close();

            return procs.stream().filter(row -> row.indexOf(findProcess) > -1).count() > 0;
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            Main.endProgram(-1);
        }

        return true;
    }

    public void killCurrentRunningMiner() {
        try {
            System.out.println("Attempting to kill currently running miner "+ minerExecutableName);
            Runtime.getRuntime().exec("taskkill /F /IM "+ minerExecutableName);

            //clean running cmd.exe
            Runtime.getRuntime().exec("taskkill /F /FI \"WINDOWTITLE eq AutoMine_cmd*\"");
            Runtime.getRuntime().exec("taskkill /F /FI \"WINDOWTITLE eq AutoMine_cmd\"");

            TimeUnit.SECONDS.sleep(5);

            if (IsRunning())
                throw new MinerNotKillableException();
            else
                System.out.println("Process killed successfully.");

            overclocker.setDefaultSettings();
        }
        catch (Exception e){
            System.err.println("Could not kill Miner "+ minerExecutableName +", please kill with task manager.");
            Main.endProgram(-1);
        }
    }

    public String getMinerExecutableName() {
        return minerExecutableName;
    }

    public void start() {
        try {
            overclocker.setDefaultSettings();
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "\"AutoMine_cmd\"", batchFileName);
            pb.directory(new File(this.minerDirectory));
            pb.start();
            System.out.println("Starting miner...");
            System.out.println("Waiting "+TIME_FOR_DAG_TO_GEN+" seconds to set overclocked settings.");
            TimeUnit.SECONDS.sleep(TIME_FOR_DAG_TO_GEN);
            overclocker.setOverclockSettings();
        }
        catch (IOException e){
            System.err.println("Could not start the miner, file "+this.batchFileName+" is in use or unavailable.");
            Main.endProgram(-1);
        }
        catch (InterruptedException e){
            System.err.println("An error occurred while waiting for the DAG to generate.");
            Main.endProgram(-1);
        }
    }

    public boolean hasProblem() {
        return true;
    }
}
