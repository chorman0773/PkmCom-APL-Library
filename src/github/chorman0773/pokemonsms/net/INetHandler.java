package github.chorman0773.pokemonsms.net;

import java.io.Closeable;


/**
 * The Base of PkmCom's Network Model.
 * 
 * An INetHandler is designed to Provide some sort of connection to one or more other INetHandlers, usually located on a different computer.
 * The Connection is managed in different ways, depending on the Configuration of the Network.
 * 
 * @see INetHandlerRemote
 * @see INetController
 * @see INetHandlerServer
 * @see INetHandlerPipe
 * @author connor
 */
public interface INetHandler extends Closeable {
	public NetworkSide getActive();
}
