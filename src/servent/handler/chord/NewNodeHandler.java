package servent.handler.chord;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import app.AppConfig;
import app.ServentInfo;
import app.backup.MySharedFile;
import app.mutex.DistributedMutex;
import app.mutex.RAState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.chord.NewNodeMessage;
import servent.message.chord.SorryMessage;
import servent.message.chord.WelcomeMessage;
import servent.message.util.MessageUtil;
public class NewNodeHandler implements MessageHandler {

	private Message clientMessage;
	private DistributedMutex mutex;

	public NewNodeHandler(Message clientMessage, DistributedMutex mutex) {
		this.clientMessage = clientMessage;
		this.mutex = mutex;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.NEW_NODE) {
			int newNodePort = clientMessage.getSenderPort();
			ServentInfo newNodeInfo = new ServentInfo("localhost", newNodePort);

			//check if the new node collides with another existing node.
			if (AppConfig.chordState.isCollision(newNodeInfo.getChordId())) {
				Message sry = new SorryMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort());
				MessageUtil.sendMessage(sry);
				return;
			}

			//check if he is my predecessor
			boolean isMyPred = AppConfig.chordState.isKeyMine(newNodeInfo.getChordId());
			if (isMyPred) { //if yes, prepare and send welcome message
				mutex.lock();
				synchronized (RAState.lock) {
					AppConfig.timestampedStandardPrint("Adding node with port: " + newNodeInfo);
					ServentInfo hisPred = AppConfig.chordState.getPredecessor();
					if (hisPred == null) {
						hisPred = AppConfig.myServentInfo;
					}

					AppConfig.chordState.setPredecessor(newNodeInfo);

					Map<Integer, MySharedFile> myValues = AppConfig.chordState.getValueMap();
					Map<Integer, MySharedFile> hisValues = new HashMap<>();

					int myId = AppConfig.myServentInfo.getChordId();
					int hisPredId = hisPred.getChordId();
					int newNodeId = newNodeInfo.getChordId();

					for (Entry<Integer, MySharedFile> valueEntry : myValues.entrySet()) {
						if (hisPredId == myId) { //i am first and he is second
							if (myId < newNodeId) {
								if (valueEntry.getKey() <= newNodeId && valueEntry.getKey() > myId) {
									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							} else {
								if (valueEntry.getKey() <= newNodeId || valueEntry.getKey() > myId) {
									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							}
						} else if (hisPredId < myId) { //my old predecesor was before me
							if (valueEntry.getKey() <= newNodeId) {
								hisValues.put(valueEntry.getKey(), valueEntry.getValue());
							}
						} else { //my old predecesor was after me
							if (hisPredId > newNodeId) { //new node overflow
								if (valueEntry.getKey() <= newNodeId || valueEntry.getKey() > hisPredId) {
									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							} else { //no new node overflow
								if (valueEntry.getKey() <= newNodeId && valueEntry.getKey() > hisPredId) {
									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							}

						}

					}
					for (Integer key : hisValues.keySet()) { //remove his values from my map
						myValues.remove(key);
					}
					AppConfig.chordState.setValueMap(myValues);

					WelcomeMessage wm = new WelcomeMessage(AppConfig.myServentInfo.getListenerPort(), newNodePort, hisValues);
					MessageUtil.sendMessage(wm);
					// Wait for global state to finish reorganizing before unlocking
					// We are sure that when adding nodes to system at the start, attacker can't attack our system
					// So we can peacefully wait for it to finish reorganizing
				}
				waitForReorganizedMessage();
				mutex.unlock();
			} else { //if he is not my predecessor, let someone else take care of it
				ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(newNodeInfo.getChordId());
				NewNodeMessage nnm = new NewNodeMessage(newNodePort, nextNode.getListenerPort());
				MessageUtil.sendMessage(nnm);
			}

		} else {
			AppConfig.timestampedErrorPrint("NEW_NODE handler got something that is not new node message.");
		}

	}

	private void waitForReorganizedMessage(){
		while(!RAState.isReorganizationDone.get()){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		RAState.isReorganizationDone.set(false);
	}

}

