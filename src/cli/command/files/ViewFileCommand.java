package cli.command.files;

import app.AppConfig;
import app.ChordState;
import app.backup.MySharedFile;
import cli.command.CLICommand;

public class ViewFileCommand implements CLICommand {

	@Override
	public String commandName() {
		return "view_file";
	}

	@Override
	public void execute(String args) {
		try {
			// Izračunavanje ključa za fajl koristeći Chord hash funkciju
			int key = ChordState.chordHash(AppConfig.WORKING_DIRECTORY.concat(args));
			AppConfig.timestampedStandardPrint("---[{*}]---> Looking for file: " + args + ", with key: " + key);
			MySharedFile file = AppConfig.chordState.getValue(key); // Dobijanje fajla iz Chord mreže

			if (file != null) {
				AppConfig.timestampedStandardPrint("---[(*)]---> Found File: (" + key + " " + file.getFilePath() +  " ):\n" + file.getContent()); // Prikaz sadržaja fajla
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Invalid argument for view_file: " + args);
		}
	}
}