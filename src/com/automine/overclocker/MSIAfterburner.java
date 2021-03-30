package com.automine.overclocker;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MSIAfterburner implements Overclocker{

    private static final String MSI_AFTERBURNER_LOC = "C:\\Program Files (x86)\\MSI Afterburner\\MSIAfterburner.exe";
    private static final String OC_PROFILE      = "-profile2";
    private static final String DEFAULT_PROFILE = "-profile1";

    public MSIAfterburner(){
        if (!(new File(MSI_AFTERBURNER_LOC).exists())){
            throw new IllegalStateException("MSIAfterburner is not installed. Please install first then try running the program again.");
        }
    }

    @Override
    public boolean setOverclockSettings() {
        try {
            Runtime.getRuntime().exec(MSI_AFTERBURNER_LOC +" "+OC_PROFILE);
            System.out.println("MSIAfterburner: Setting overclocked settings.");
            TimeUnit.SECONDS.sleep(5);
            System.out.println("MSIAfterburner: Set overclocked settings.");
            return true;
        } catch(Exception e){
            System.out.println("MSIAfterburner: Could not set overclocked profile.");
            return false;
        }
    }

    public boolean setDefaultSettings(){
        try {
            Runtime.getRuntime().exec(MSI_AFTERBURNER_LOC +" "+ DEFAULT_PROFILE);
            System.out.println("MSIAfterburner: Setting default settings.");
            TimeUnit.SECONDS.sleep(5);
            System.out.println("MSIAfterburner: Set default settings.");
            return true;
        } catch(Exception e){
            System.out.println("MSIAfterburner: Could not set default profile.");
            return false;
        }
    }
}
