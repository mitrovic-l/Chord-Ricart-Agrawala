package cli.command;

import app.AppConfig;
import app.ServentInitializer;

public class JoinAfterCommand implements CLICommand{
    @Override
    public String commandName() {
        return "join_after";
    }

    @Override
    public void execute(String args) {
        int timeToSleep = -1;

        try {
            timeToSleep = Integer.parseInt(args);

            if (timeToSleep < 0) {
                throw new NumberFormatException();
            }

            AppConfig.timestampedStandardPrint("Pausing for " + timeToSleep + " ms");
            try {
                Thread.sleep(timeToSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Pause command should have one int argument, which is time in ms.");
        }

        ServentInitializer serventInitializer = new ServentInitializer();
        Thread initializerThread = new Thread(serventInitializer);
        initializerThread.start();
    }
}

