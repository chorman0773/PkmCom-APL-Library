package github.chorman0773.pokemonsms.net.discovery.lan;

import java.net.InetAddress;
import java.net.UnknownHostException;
 class UncheckGetByName {

	public UncheckGetByName() {
		// TODO Auto-generated constructor stub
	}
	
	static InetAddress uncheck_getByName(String name) {
		try {
			return InetAddress.getByName(name);
		} catch (UnknownHostException e) {
			throw new ExceptionInInitializerError(e);//Since doing some sort of uncheck hack will just result in this exception anyways, this is permissible.
		}
	}

}
