package github.chorman0773.pokemonsms.net;

import java.io.Closeable;


/**
 * The Base of PkmCom's Network Model.
 * @author connor
 *
 */
public interface INetHandler extends Closeable {
	public NetworkSide getActive();
}
