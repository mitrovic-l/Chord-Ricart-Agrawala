package cli.command.files;

import app.AppConfig;
import app.ChordState;
import app.backup.MySharedFile;
import app.mutex.RAState;
import cli.command.CLICommand;

public class AddFileCommand implements CLICommand {

    @Override
    public String commandName() {
        return "add_file";
    }

    @Override
    public void execute(String args) {

        String[] splitArgs = args.split(" ");

        if (splitArgs.length == 2) {
            try {
                int key = ChordState.chordHash(AppConfig.WORKING_DIRECTORY.concat(splitArgs[0]));
                AppConfig.timestampedStandardPrint("-------<*>---> Adding file with key: " + key + ", file name: " + splitArgs[0]);
                boolean isPublic = splitArgs[1].equals("public");
                MySharedFile mySharedFile = new MySharedFile(
                        AppConfig.WORKING_DIRECTORY.concat(splitArgs[0]),
                        isPublic,
                        AppConfig.myServentInfo.getListenerPort()
                );

                if (key < 0 || key >= ChordState.CHORD_SIZE) {
                    throw new NumberFormatException();
                }

                // Sinhronizovani blok nad RAState.lock
                synchronized (RAState.lock) {
                    AppConfig.chordState.putValue(key, mySharedFile);
                    AppConfig.timestampedStandardPrint("----<***>---> Added: " + splitArgs[0] + ".");
                }
            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Invalid key. Key should be in range 0 <= key < " + ChordState.CHORD_SIZE);
            }
        } else {
            AppConfig.timestampedErrorPrint("Invalid arguments for add_file.");
        }
    }
}

