package github.chorman0773.pokemonsms.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class PacketDecoder {
	
	private final Map<Integer,Supplier<? extends IPacket>> packetProviders = new HashMap<>();
	
	public PacketDecoder registerProvider(int id,Supplier<? extends IPacket> provider) {
		if(packetProviders.putIfAbsent(id,provider)!=null)
			throw new IllegalArgumentException("Provider for packet "+Integer.toHexString(id)+" already found");
		return this;
	}
	
	public PacketDecoder() {
		registerProvider(255,HandshakeComplete::new);
	}
	public IPacket read(DataInputStream strm) throws IOException {
		int id= strm.readUnsignedByte();
		int hashcode = strm.readInt();
		int size = strm.readInt();
		byte[] b = new byte[size];
		strm.readFully(b);
		PacketBuffer buff = new PacketBuffer(b);
		if(id>=packetProviders.size()) {
			throw new ProtocolError("Invalid Packet Id "+Integer.toHexString(id));
		}else {
			IPacket ret = packetProviders.get(id).get();
			ret.read(buff);
			if(ret.size()!=size)
				throw new ProtocolError("Packet Size Mismatch ("+ret.size()+"!="+size+")");
			else if((ret.hashcode()*31+ret.getId())!=hashcode)
				throw new ProtocolError("Packet Hash Mismatch ("+ret.hashCode()+"!="+hashcode+")");
			return ret;
		}
	}
	public void write(DataOutputStream out,IPacket packet)throws IOException{
		PacketBuffer buff = new PacketBuffer();
		out.writeByte(packet.getId());
		out.writeInt(packet.hashcode()*31+packet.getId());
		out.writeInt(packet.size());
		packet.write(buff);
		out.write(buff.getBytes());
	}
}
