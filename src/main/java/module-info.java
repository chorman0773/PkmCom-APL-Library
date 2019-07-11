/**
 * 
 */
/**
 * @author chorm
 *
 */
module github.chorman0773.pkmcom {
	requires transitive github.lightningcreations.lclib;
	requires transitive gson;
	requires typetools;
	requires JavaWebSocket;
	exports github.chorman0773.pokemonsms.net;
	exports github.chorman0773.pokemonsms.net.server;
	exports github.chorman0773.pokemonsms.net.multicast;
	exports github.chorman0773.pokemonsms.net.lan;
	exports github.chorman0773.pokemonsms.net.service;
}