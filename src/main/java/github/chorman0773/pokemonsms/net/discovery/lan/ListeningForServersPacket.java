package github.chorman0773.pokemonsms.net.discovery.lan;

import java.io.IOException;

import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.PacketBuffer;

public class ListeningForServersPacket implements IPacket {

	public ListeningForServersPacket() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void read(PacketBuffer buff) throws IOException {
	}

	@Override
	public void write(PacketBuffer buff) throws IOException {
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public int getId() {
		return 1;
	}

	@Override
	public int hashcode() {
		return 0;
	}

}
