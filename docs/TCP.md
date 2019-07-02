# PkmCom/TCP 

This Document Describes the addition information necessary to implement PkmCom/TCP, including the connection handshake.

## Connection Handshaking [pkmcom.handshake]


The PkmCom/TCP protocol is built on TCP. When the connection is opened, and after the TCP Handshake, The Server and Client preform a Secret Key exchange to send data over a channel secured by AES-256, using Cipher Block Chaining, and Padded with PKCS5 Padding.<br/>
The steps of the key exchange are preformed as follows:
<ol type="1">
<li>The Server has a prepared RSA Key Pair (RSA-2048). The Public Key of this Key Pair is sent to the Client</li>
<li>The Client generates an RSA Key pair (RSA-2048), and sends the public key to the server. This key pair is to be disposed of by the client by the time connection is closed. It may be disposed of earlier, but only after all points which the Complete Protocol Definition applying PkmCom permits the server to re-establish the shared secret</li>
<li>The server and the client each generate (in a secure fashion), a 2048-bit message. This message encrypted using the public key of the target side (using PKCS1 Padding). They both also generate and send a 128-byte iv</li>
<li>The 2 messages are Combined by XORing each byte in sequence, and also combine the IVs by the same method. The message is then used to Derive a 256-bit AES Key, using SHA-256. Past this point AES Encryption is used, with the XORed IVs used for Cipher Block Chaining</li>
<li>The Client should then send a Handshaking Packet (0xFF, See below). If its read correctly, then the server should respond with the same packet. If either packet is read incorrectly, the connection is closed (though may be reopened).</li>
</ol>
### Handshaking Packet [pkmcom.handshake.packet] ###



This Packet is sent and verified at the end of the handshake sequence. It contains a single Unsigned Int Enum Field, which MUST be exactly 0x504B4D00. The Id of the Packet is 0xFF.

### Alternative Handshaking [pkmcom.handshake.alt]

In certain situations, an alternative method is used to derive the Session Shared Secret, such as a password. 
After the messages are exchanged, if indicated by the server, the client and server should append some sort of alternatively exchanged secret to the combined messages (usually a password exchanged physically, such as in person). The AES Key should then be derived from that, and handshaking should be completed from that point. 

It is implementation-defined if a client supports alternative handshaking. 
