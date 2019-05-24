package github.chorman0773.pokemonsms.net;

import java.net.Socket;


public interface INetHandlerRemote extends INetHandler {
	public NetworkSide getRemote();
	public IPacket recieve()throws ProtocolError;
	public void send(IPacket packet)throws ProtocolError;
}
