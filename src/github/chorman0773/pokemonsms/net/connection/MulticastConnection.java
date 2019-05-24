package github.chorman0773.pokemonsms.net.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.ProtocolError;

public class MulticastConnection implements Closeable {
	private MulticastSocket sock;
	private PacketDecoder dec;
	public MulticastConnection(InetAddress multicastGroup,int port) throws IOException {
		sock = new MulticastSocket(port);
		sock.setTimeToLive(255);
		sock.setLoopbackMode(true);
		sock.joinGroup(multicastGroup);
		dec = new PacketDecoder();
	}
	
	
	public byte[] pull() throws IOException{
		ByteBuffer buff = ByteBuffer.allocate(16777215);
		DatagramPacket pack = new DatagramPacket(buff.array(),16777215);
		sock.receive(pack);
		buff.position(0);
		byte[] ret = new byte[pack.getLength()];
		System.arraycopy(buff.array(), 0, ret, 0, ret.length);
		return ret;
	}
	
	public void push(byte[] buff) throws IOException {
		DatagramPacket pack = new DatagramPacket(buff,buff.length);
		sock.send(pack);
	}

	public IPacket recieve() throws ProtocolError {
		try(DataInputStream strm = new DataInputStream(new ByteArrayInputStream(pull()))){
			return dec.read(strm);
		} catch(ProtocolError err) {
			throw err;
		} catch (IOException e) {
			throw new ProtocolError(e);
		} 
	}

	public void send(IPacket packet) throws ProtocolError {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try(DataOutputStream strm = new DataOutputStream(bout)){
			dec.write(strm, packet);
			push(bout.toByteArray());
		} catch (IOException e) {
			throw new ProtocolError(e);
		}
	}


	@Override
	public void close() throws IOException {
		sock.close();
	}
	
	public MulticastSocket getSocket() {
		return sock;
	}

}
