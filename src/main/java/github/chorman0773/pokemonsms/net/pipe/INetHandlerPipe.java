package github.chorman0773.pokemonsms.net.pipe;

import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.ProtocolError;

/**
* An INetHandlerPipe is a Connection to an internal INetHandlerRemote via an internal pipe.
* Packets that are sent across pipes are not serialized, and simply queued at the other end.
*/
public interface INetHandlerPipe extends INetHandlerRemote {
	INetHandlerPipe getOtherSide();
	void enqueuePacket(IPacket packet) throws ProtocolError;
	default void send(IPacket packet) throws ProtocolError {
		getOtherSide().enqueuePacket(packet);
	}
}
