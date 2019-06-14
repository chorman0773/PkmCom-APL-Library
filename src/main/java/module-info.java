/**
 * 
 */
/**
 * @author chorm
 *
 */
module github.chorman0773.pkmcom {
	requires github.lightningcreations.lclib;
	requires transitive gson;
	exports github.chorman0773.pokemonsms.net;
	exports github.chorman0773.pokemonsms.net.server;
	exports github.chorman0773.pokemonsms.net.client;
	exports github.chorman0773.pokemonsms.net.multicast;
}