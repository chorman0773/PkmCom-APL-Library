package github.chorman0773.pokemonsms.net.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;

import github.chorman0773.pokemonsms.net.HandshakeComplete;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.ProtocolError;

public class Connection implements AutoCloseable {
	private PublicKey spub;
	private PublicKey cpub;
	private PrivateKey spriv;
	private SecretKey secret;
	private Socket sock;
	private DataOutputStream out;
	private DataInputStream in;
	private PacketDecoder dec;
	static byte[] combine(byte[] a,byte[] b) {
		byte[] ret = new byte[Math.max(a.length, b.length)];
		for(int i=0;i<ret.length;i++)
			ret[i] = (byte)(a[i]^b[i]);
		return ret;
	}
	private static final SecureRandom rand = new SecureRandom();
	private static final MessageDigest SHA256;
	static {
		try {
			SHA256 = MessageDigest.getInstance("SHA256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Failed to initialize Security Providers",e);
		}
	}
	@Override
	public void close() throws IOException{
		try {
			if(secret!=null&&!secret.isDestroyed())
				secret.destroy();
			sock.close();
		}catch(DestroyFailedException e) {
			sock.close();
			throw new IOException(e);
		}
		
	}
	public Connection(PacketDecoder dec,Socket sock,KeyPair keys) {
		this.sock = sock;
		this.spub = keys.getPublic();
		this.spriv = keys.getPrivate();
		this.dec = dec;
	}
	public synchronized void handshake() throws IOException {
		InputStream in = sock.getInputStream();
		OutputStream out = sock.getOutputStream();
		this.out = new DataOutputStream(out);
		this.in = new DataInputStream(in);
		byte[] keyData = spub.getEncoded();
		this.out.writeInt(keyData.length);
		out.write(keyData);
		keyData = new byte[this.in.readInt()];
		in.read(keyData);
		try {
			cpub = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyData));
			Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			Cipher decryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			Cipher signCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			Cipher unsignCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			encryptCipher.init(Cipher.ENCRYPT_MODE, cpub);
			decryptCipher.init(Cipher.DECRYPT_MODE, spriv);
			signCipher.init(Cipher.ENCRYPT_MODE, spriv);
			unsignCipher.init(Cipher.DECRYPT_MODE, cpub);
			in = new CipherInputStream(new CipherInputStream(in,decryptCipher),unsignCipher);
			out = new CipherOutputStream(new CipherOutputStream(out,encryptCipher),signCipher);
			byte[] smessage = new byte[256];
			byte[] cmessage = new byte[256];
			byte[] message;
			byte[] siv = new byte[16];
			byte[] civ = new byte[16];
			byte[] iv;
			rand.nextBytes(smessage);
			rand.nextBytes(siv);
			out.write(smessage);
			out.write(siv);
			out.flush();
			in.read(cmessage);
			in.read(civ);
			message = combine(cmessage,smessage);
			iv = combine(civ,siv);
			byte[] key = SHA256.digest(message);
			secret = new SecretKeySpec(key,"AES");
			IvParameterSpec spec= new IvParameterSpec(iv);
			encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			encryptCipher.init(Cipher.ENCRYPT_MODE, secret, spec);
			decryptCipher.init(Cipher.DECRYPT_MODE, secret, spec);
			Arrays.fill(message, (byte)0);
			Arrays.fill(smessage, (byte)0);
			Arrays.fill(smessage, (byte)0);
			Arrays.fill(civ, (byte)0);
			Arrays.fill(siv, (byte)0);
			Arrays.fill(iv, (byte)0);
			this.in = new DataInputStream(new CipherInputStream(sock.getInputStream(),encryptCipher));
			this.out = new DataOutputStream(new CipherOutputStream(sock.getOutputStream(),decryptCipher));
			dec.write(this.out, new HandshakeComplete());
			IPacket packet = dec.read(this.in);
			if(!(packet instanceof HandshakeComplete))
				throw new ProtocolError("Could not complete Handshake (HandshakeComplete Packet Not on stream)");
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new IOException(e);
		}
	}
	public synchronized IPacket get()throws IOException {
		return dec.read(in);
	}
	public synchronized void send(IPacket packet)throws IOException{
		dec.write(this.out, packet);
	}
	public Socket getSocket() {
		return sock;
	}
	
}
