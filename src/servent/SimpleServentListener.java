package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import app.mutex.DistributedMutex;
import servent.handler.*;
import servent.handler.backup.*;
import servent.handler.check.TickHandler;
import servent.handler.check.TackHandler;
import servent.handler.failure.*;
import servent.handler.chord.*;
import servent.handler.files.FileAddHandler;
import servent.handler.files.FileAskHandler;
import servent.handler.files.FileRemoveHandler;
import servent.handler.files.FileTellHandler;
import servent.handler.chord.ReorganizationHandler;
import servent.handler.friends.FriendRequestHandler;
import servent.handler.files.FileDeniedHandler;
import servent.handler.mutex.ReplyMessageHandler;
import servent.handler.mutex.RequestMessageHandler;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;
	private DistributedMutex mutex;
	public SimpleServentListener(DistributedMutex mutex) {
		this.mutex = mutex;
	}

	/*
	 * Thread pool for executing the handlers. Each client will get it's own handler thread.
	 */
	private final ExecutorService threadPool = Executors.newWorkStealingPool();
	
	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}
		
		
		while (working) {
			try {
				Message clientMessage;
				
				Socket clientSocket = listenerSocket.accept();
				
				//GOT A MESSAGE! <3
				clientMessage = MessageUtil.readMessage(clientSocket);
				
				MessageHandler messageHandler = new NullHandler(clientMessage);
				
				/*
				 * Each message type has it's own handler.
				 * If we can get away with stateless handlers, we will,
				 * because that way is much simpler and less error prone.
				 */
				switch (clientMessage.getMessageType()) {
				case NEW_NODE:
					messageHandler = new NewNodeHandler(clientMessage, mutex);
					break;
				case REMOVE_NODE:
					messageHandler = new RemoveNodeHandler(clientMessage);
					break;
				case WELCOME:
					messageHandler = new WelcomeHandler(clientMessage, mutex);
					break;
				case SORRY:
					messageHandler = new SorryHandler(clientMessage);
					break;
				case UPDATE:
					messageHandler = new UpdateHandler(clientMessage);
					break;
				case FILE_ADD:
					messageHandler = new FileAddHandler(clientMessage);
					break;
				case FILE_REMOVE:
					messageHandler = new FileRemoveHandler(clientMessage);
					break;
				case FILE_ASK:
					messageHandler = new FileAskHandler(clientMessage);
					break;
				case FILE_TELL:
					messageHandler = new FileTellHandler(clientMessage);
					break;
				case POISON:
					break;
				case FILE_DENIED:
					messageHandler = new FileDeniedHandler(clientMessage);
					break;
				case FRIEND_REQUEST:
					messageHandler = new FriendRequestHandler(clientMessage);
					break;
				case TICK:
					messageHandler = new TickHandler(clientMessage);
					break;
				case TACK:
					messageHandler = new TackHandler(clientMessage);
					break;
				case SOFT_FAIL:
					messageHandler = new SoftFailHandler(clientMessage);
					break;
				case HARD_FAIL:
					messageHandler = new HardFailHandler(clientMessage);
					break;
				case HARD_FAIL_CONFIRM:
					messageHandler = new HardFailConfirmHandler(clientMessage);
					break;
				case GIVE_BACKUP:
					messageHandler = new BackupGetHandler(clientMessage);
					break;
				case REORGANIZATION:
					messageHandler = new ReorganizationHandler(clientMessage);
					break;
				case RA_REPLY:
					messageHandler = new ReplyMessageHandler(clientMessage);
					break;
				case RA_REQUEST:
					messageHandler = new RequestMessageHandler(clientMessage);
					break;
				}

				
				threadPool.submit(messageHandler);
			} catch (SocketTimeoutException timeoutEx) {
				//Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		this.working = false;
	}

}
