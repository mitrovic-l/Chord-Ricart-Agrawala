package cli.command.files;

import app.AppConfig;
import cli.command.CLICommand;
import cli.command.PauseCommand;

public class RemoveFileCommand implements CLICommand {
    @Override
    public String commandName() {
        return "remove_file";
    }

    @Override
    public void execute(String args) {
        AppConfig.chordState.deleteFile(AppConfig.WORKING_DIRECTORY.concat(args));
    }
}
