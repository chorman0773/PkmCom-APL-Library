package github.chorman0773.pokemonsms.net.service.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.LinkedList;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.ProtocolError;

public class NetHandlerWSSClient implements INetHandlerRemote {
	
	private class Client extends WebSocketClient{

		public Client(URI serverUri) {
			super(serverUri);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onClose(int arg0, String arg1, boolean arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(Exception arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMessage(String arg0) {
			// TODO Auto-generated method stub
			
		}
		
		public void onMessage(ByteBuffer buff) {
			byte[] arr = buff.array();
			ByteArrayInputStream in = new ByteArrayInputStream(arr);
			try {
				IPacket pack = dec.read(new DataInputStream(in));
				synchronized(lock) {
					packets.add(pack);
					lock.notify();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
		}

		@Override
		public void onOpen(ServerHandshake arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private Deque<IPacket> packets = new LinkedList<>();
	private final Object lock = new Object();
	private PacketDecoder dec;
	
	private WebSocket sock;
	
	public NetHandlerWSSClient(PacketDecoder dec,URI uri) {
		sock = new Client(uri);
	}

	@Override
	public NetworkSide getActive() {
		// TODO Auto-generated method stub
		return NetworkSide.CLIENT;
	}

	@Override
	public void close() throws IOException {
		sock.close();
	}

	@Override
	public NetworkSide getRemote() {
		// TODO Auto-generated method stub
		return NetworkSide.SERVER;
	}

	@Override
	public IPacket recieve() throws ProtocolError {
		synchronized(lock) {
			while(packets.isEmpty())
				try {
					lock.wait();
				} catch (InterruptedException e) {
					throw new ProtocolError(e);
				}
			return packets.pop();
		}
	}

	@Override
	public void send(IPacket packet) throws ProtocolError {
		ByteArrayOutputStream strm = new ByteArrayOutputStream();
		try {
			dec.write(new DataOutputStream(strm), packet);
			sock.send(strm.toByteArray());
		} catch (IOException e) {
			throw new ProtocolError(e);
		}
		
	}
	
	private static int PROTOCOL_ERROR_CODE = 1002;
	
	@Override
	public void handleProtocolError(ProtocolError e) throws ProtocolError {
		sock.close(PROTOCOL_ERROR_CODE, e.getMessage());
	}

}
