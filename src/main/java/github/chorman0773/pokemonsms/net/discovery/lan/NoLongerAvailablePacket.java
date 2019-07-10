package github.chorman0773.pokemonsms.net.discovery.lan;

import java.io.IOException;

import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.PacketBuffer;
import github.chorman0773.pokemonsms.net.service.Service;

public class NoLongerAvailablePacket implements IPacket {
	
	private Service srv;
	
	public NoLongerAvailablePacket() {
		// TODO Auto-generated constructor stub
	}
	
	public NoLongerAvailablePacket(Service srv) {
		this.srv = srv;
	}

	@Override
	public void read(PacketBuffer buff) throws IOException {
		srv = new Service(buff);
	}

	@Override
	public void write(PacketBuffer buff) throws IOException {
		srv.write(buff);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return srv.size();
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int hashcode() {
		// TODO Auto-generated method stub
		return srv.hashCode();
	}

	public Service getService() {
		// TODO Auto-generated method stub
		return srv;
	}

}
