package github.chorman0773.pokemonsms.net.discovery.lan;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.PacketBuffer;
import github.chorman0773.pokemonsms.net.Sizes;
import github.chorman0773.pokemonsms.net.service.Service;
import github.lightningcreations.lclib.Hash;

public class ServiceAdvertisementPacket implements IPacket {
	
	private Service service;
	private int flags;
	private Set<String> modes;
	
	public ServiceAdvertisementPacket() {
		
	}
	
	public ServiceAdvertisementPacket(Service base,int flags,Set<String> modes) {
		this.service = base;
		this.modes = modes;
		this.flags = flags;
	}

	@Override
	public void read(PacketBuffer buff) throws IOException {
		this.service = new Service(buff);
		int len = buff.readUnsignedShort();
		flags = buff.readUnsignedByte();
		for(;len>0;len--)
			modes.add(buff.readString());
	}

	@Override
	public void write(PacketBuffer buff) throws IOException {
		service.write(buff);
		buff.writeShort(modes.size());
		buff.writeByte(flags);
		for(String s:modes)
			buff.writeString(s);
	}

	@Override
	public int size() {
		int size = service.size();
		size += 2;
		for(String s:modes)
			size += Sizes.size(s);
		return size;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0x00;
	}

	@Override
	public int hashcode() {
		// TODO Auto-generated method stub
		return Hash.sum(service,modes);
	}

	public Service getService() {
		// TODO Auto-generated method stub
		return service;
	}
	
	public Set<String> getModes(){
		return Collections.unmodifiableSet(modes);
	}
	
	public boolean hasFlags(int flg) {
		return (flags&flg)==flg;
	}

}
