package github.chorman0773.pokemonsms.net;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import github.chorman0773.pokemonsms.net.server.INetHandlerServer;

/**
 * An INetController is a manager object which manages multiple remote connections to a single side, 
 *  usualy a server or a client acting as a LAN Server.
 * INetControllers provide facilities to obtain the list of Remote Connections open to the Controller.
 * @author chorm
 */
public interface INetController extends INetHandler {
	public Stream<? extends INetHandlerRemote> getRemotes();
	public List<? extends INetHandlerRemote> listRemotes();
	public INetHandlerServer accept()throws IOException;
}
