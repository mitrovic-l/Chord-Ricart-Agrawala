package cli.command;

import app.AppConfig;
import app.backup.Backupper;
import app.backup.Heartbeat;
import cli.CLIParser;
import servent.SimpleServentListener;

public class StopCommand implements CLICommand {

	private CLIParser parser;
	private SimpleServentListener listener;
	private Backupper backupper;
	
	public StopCommand(CLIParser parser, SimpleServentListener listener, Backupper backupper) {
		this.parser = parser;
		this.listener = listener;
		this.backupper = backupper;
	}
	
	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("Stopping...");
		parser.stop();
		listener.stop();
		Heartbeat.getInstance().stop();
		backupper.stop();
	}
}
