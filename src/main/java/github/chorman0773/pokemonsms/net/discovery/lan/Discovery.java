package github.chorman0773.pokemonsms.net.discovery.lan;

import java.net.InetAddress;
import java.net.UnknownHostException;

import github.chorman0773.pokemonsms.net.PacketDecoder;

public interface Discovery {

	
	
	
	public static final PacketDecoder discoveryDecoder = new PacketDecoder()
			.registerProvider(0, ServiceAdvertisementPacket::new)
			.registerProvider(1, ListeningForServersPacket::new)
			.registerProvider(2, NoLongerAvailablePacket::new);
	
	public static final int advertisePort = Integer.getInteger("pkmcom.lan.advertisePort", 1<<15+1<<14+1<<8+1); //Temporary Port, will be replaced. 
	
	public static final InetAddress advertiseAddress = UncheckGetByName.uncheck_getByName(System.getProperty("pkmcom.lan.advertiseGroup","224.0.0.1"));
	

}
