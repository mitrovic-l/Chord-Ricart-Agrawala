package cli.command.friends;

import app.AppConfig;
import cli.command.CLICommand;
import cli.command.PauseCommand;
import servent.message.friends.FriendRequestMessage;
import servent.message.util.MessageUtil;

public class AddFriendCommand implements CLICommand {
    @Override
    public String commandName() {
        return "add_friend";
    }

    @Override
    public void execute(String args) {
        try{
            int friend = Integer.parseInt(args);
            AppConfig.chordState.addFriend(friend);
            AppConfig.timestampedStandardPrint("New friend: " + friend);
            AppConfig.chordState.sendFriendRequest(friend);
        }catch (NumberFormatException e){
            AppConfig.timestampedErrorPrint("Invalid arguments for add_fiend command");
        }

    }
}
