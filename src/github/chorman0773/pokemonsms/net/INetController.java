package github.chorman0773.pokemonsms.net;

import java.net.ServerSocket;
import java.util.List;
import java.util.stream.Stream;

public interface INetController extends INetHandler {
	public Stream<? extends INetHandlerRemote> getRemotes();
	public List<? extends INetHandlerRemote> listRemotes();
	public ServerSocket getServer();
}
