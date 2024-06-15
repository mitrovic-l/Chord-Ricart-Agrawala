package cli.command.files;

import app.AppConfig;
import cli.command.CLICommand;
import servent.message.files.FileGetMessage;
import servent.message.util.MessageUtil;

public class GetFilesCommand implements CLICommand {
    @Override
    public String commandName() {
        return "get_files";
    }

    @Override
    public void execute(String args) {
        String[] splitArgs = args.split(":");

        if (splitArgs.length == 2) {
            int port = Integer.parseInt(splitArgs[1]);
            AppConfig.timestampedStandardPrint("Getting files from port " + port);
            FileGetMessage fileGetMessage = new FileGetMessage(AppConfig.myServentInfo.getListenerPort(), port);
            MessageUtil.sendMessage(fileGetMessage);
        } else {
            AppConfig.timestampedErrorPrint("Invalid arguments for get from port");
        }
    }
}
