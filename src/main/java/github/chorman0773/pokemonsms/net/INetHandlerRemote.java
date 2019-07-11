package github.chorman0773.pokemonsms.net;


/**
 * An INetHandlerRemote is a Single-pipeline connection to another INetHandlerRemote. <br/>
 * INetHandlerRemotes can recieve and send packets, which is not provided for basic INetHandlers, 
 *  as it doesn't particuarily make sense for Pipeline hubs to recieve packets (though broadcasting does make sense).
 * @author chorm
 */
public interface INetHandlerRemote extends INetHandler {
	public NetworkSide getRemote();
	public IPacket recieve()throws ProtocolError;
	public void send(IPacket packet)throws ProtocolError;
	public void handleProtocolError(ProtocolError e)throws ProtocolError;
}
