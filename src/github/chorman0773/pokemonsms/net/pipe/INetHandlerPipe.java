package github.chorman0773.pokemonsms.net.pipe;

import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.ProtocolError;

public interface INetHandlerPipe extends INetHandlerRemote {
	INetHandlerPipe getOtherSide();
	void enqueuePacket(IPacket packet) throws ProtocolError;
	default void send(IPacket packet) throws ProtocolError {
		getOtherSide().enqueuePacket(packet);
	}
}
