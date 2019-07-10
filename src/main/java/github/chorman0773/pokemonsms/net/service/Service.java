package github.chorman0773.pokemonsms.net.service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.PacketBuffer;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.Sizes;
import github.lightningcreations.lclib.Hash;
import github.lightningcreations.lclib.LexicographicalCompare;

public class Service implements Comparable<Service> {
	
	private static final Map<String,IBaseProtocol> baseProtocols = new TreeMap<>();
	
	private static final LexicographicalCompare<Service> comparator = LexicographicalCompare.lexicographicalCompare(MethodHandles.lookup(),Service.class,"baseProtocolName","address","port");
	
	public void registerBaseProtocol(IBaseProtocol protocol) {
		if(baseProtocols.putIfAbsent(protocol.getProtocolName(), protocol)!=null)
			throw new IllegalArgumentException("Registering Duplicate Base Protocol");
	}
	
	private IBaseProtocol protocol;
	private String baseProtocolName;
	private String address;
	private int port;
	
	static{
		preserve_unused(new Service().baseProtocolName);
		//make sure the field isn't giving me warnings or disappearing on me, since I'm using it in LexicographicalCompare
	}
	
	
	private Service() {}
	
	public Service(PacketBuffer buff) throws IOException {
		this.protocol = baseProtocols.get(buff.readString());
		this.address = buff.readString();
		this.port = buff.readUnsignedShort();
		if(!protocol.isValidAddress(address)||!protocol.isValidPort(port))
			throw new ProtocolError(address+" or "+port+" is not valid for "+protocol.getProtocolName());
		this.baseProtocolName = protocol.getProtocolName();
	}
	
	public Service(IBaseProtocol protocol,PacketDecoder dec) {
		this.address = protocol.getLocalAddress();
		this.port = protocol.allocatePort();
		this.protocol = protocol;
		this.dec = dec;
		this.baseProtocolName = protocol.getProtocolName();
	}
	public Service(IBaseProtocol protocol,int port,PacketDecoder dec) {
		this.address = protocol.getLocalAddress();
		if(protocol.isValidPort(port))
			this.port = port;
		else
			throw new IllegalArgumentException(port+" is not valid for the protocol");
		this.protocol = protocol;
		this.dec = dec;
		this.baseProtocolName = protocol.getProtocolName();
	}
	
	public IBaseProtocol getBaseProtocol() {
		return protocol;
	}
	
	private static void preserve_unused(Object o) {}
	
	public void write(PacketBuffer buff) throws IOException {
		buff.writeUTF(protocol.getProtocolName());
		buff.writeUTF(address);
		buff.writeShort(port);
	}
	
	private transient PacketDecoder dec;
	
	public void setPacketDecoder(PacketDecoder dec) {
		this.dec = dec;
	}
	
	public INetController createController() {
		return protocol.createController(dec);
	}

	public int size() {
		// TODO Auto-generated method stub
		return Sizes.size(protocol.getProtocolName())+Sizes.size(address)+2;
	}
	
	public int hashCode() {
		return Hash.sum(protocol.getProtocolName(),address,port);
	}
	
	public boolean equals(Object o) {
		if(o==null)
			return false;
		else if(o==this)
			return true;
		else if(!(o instanceof Service))
			return false;
		else{
			Service os = (Service)o;
			if(!Objects.equals(protocol,os.protocol))
				return false;
			else if(!Objects.equals(address, os.address))
				return false;
			else if(port!=os.port)
				return false;
			else
				return true;
		}
	}

	public INetHandlerRemote openConnection() throws IOException {
		// TODO Auto-generated method stub
		return protocol.openRemote(dec, address, port);
	}

	@Override
	public int compareTo(Service arg0) {
		// TODO Auto-generated method stub
		return comparator.compare(this, arg0);
	}
	
	
	
	

}
