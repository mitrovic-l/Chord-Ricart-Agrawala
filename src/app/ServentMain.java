package app;

import app.backup.Backupper;
import app.backup.Heartbeat;
import app.mutex.DistributedMutex;
import app.mutex.RAMutex;
import cli.CLIParser;
import servent.SimpleServentListener;

/**
 * Describes the procedure for starting a single Servent
 *
 * @author bmilojkovic
 */
public class ServentMain {

	/**
	 * Command line arguments are:
	 * 0 - path to servent list file
	 * 1 - this servent's id
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			AppConfig.timestampedErrorPrint("Please provide servent list file and id of this servent.");
		}
		
		int serventId = -1;
		int portNumber = -1;
		
		String serventListFile = args[0];
		
		try {
			serventId = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Second argument should be an int. Exiting...");
			System.exit(0);
		}
		
		AppConfig.readConfig(serventListFile, serventId);
		
		try {
			portNumber = AppConfig.myServentInfo.getListenerPort();
			
			if (portNumber < 1000 || portNumber > 2000) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Port number should be in range 1000-2000. Exiting...");
			System.exit(0);
		}
		
		AppConfig.timestampedStandardPrint("Starting servent " + AppConfig.myServentInfo);

		DistributedMutex mutex = new RAMutex();

		SimpleServentListener simpleListener = new SimpleServentListener(mutex);
		Thread listenerThread = new Thread(simpleListener);
		listenerThread.start();

		// Heartbeat - detect failure mechanism
		Heartbeat heartbeatThread = Heartbeat.getInstance();
		heartbeatThread.setMutex(mutex);
		Thread heartbeat = new Thread(heartbeatThread);
		heartbeat.start();

		// Maintain backup system
		Backupper backupManager = new Backupper();
		Thread backupThread = new Thread(backupManager);
		backupThread.start();

		CLIParser cliParser = new CLIParser(simpleListener, mutex, backupManager);
		Thread cliThread = new Thread(cliParser);
		cliThread.start();
	}
}
