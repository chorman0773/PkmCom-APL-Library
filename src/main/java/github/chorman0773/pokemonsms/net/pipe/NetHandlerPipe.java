package github.chorman0773.pokemonsms.net.pipe;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.ProtocolError;

/**
* The Default implementation of {@link INetHandlerPipe}.
*/
public abstract class NetHandlerPipe implements INetHandlerPipe {
	
	private Deque<IPacket> enqueued = new LinkedList<>();
	private Object enqueLock = new Object();
	
	public NetHandlerPipe() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public IPacket recieve() throws ProtocolError {
		synchronized(enqueLock) {
			while(enqueued.isEmpty())
				try {
					enqueued.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return enqueued.pop();
		}
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void enqueuePacket(IPacket packet) throws ProtocolError {
		synchronized(enqueLock) {
			enqueued.push(packet);
			enqueLock.notifyAll();
		}
	}

}
