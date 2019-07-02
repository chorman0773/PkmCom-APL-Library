# Pkmcom LAN Advertisement

Pkmcom Clients are permitted, to act temporarily as a server, over any Base Protocol (except PkmCom/Multicast UDP, as that base protocol has no concept of a server). To do this, clients advertise there local ip address, services, and modes to the Local Multicast Group 224.0.0.3, on port *unassigned*). This advertisements are performed using PkmCom/Multicast UDP.

## BaseProtocol Structure Type

The Service Discovery Protocol specifies the following structure for the BaseProtocol type.

<table>
	<tr>
		<th>Field</th>
		<th>Type</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>ServiceName</td>
		<td>String</td>
		<td>The Name of the Service, such as PkmCom/TCP</td>
	</tr>
	<tr>
		<td>LocalAddress</td>
		<td>String</td>
		<td>The Address the Service is advertised on</td>
	</tr>
	<tr>
		<td>Port</td>
		<td>Unsigned Short</td>
		<td>The port the service is advertised on. May be ignored or used depending on the Service.</td>
	</tr>
</table>

### Preconditions

If `ServiceName` starts with `PkmCom/` then it MUST be a Base Protocol as defined in [Base Protocols](https://chorman0773.github.io/PkmCom-APL-Library#Base%20Protocols). 
Otherwise, if the `ServiceName` is not known by the implementation, it MUST be ignored. 

If `ServiceName` is `PkmCom/TCP`, then `LocalAddress` MUST either be a valid IPv4 or IPv6 Address. 
If `ServiceName` is `PkmCom/Bluetooth`, then `LocalAddress` MUST be a valid 48-bit MAC Address. 
Known `ServiceNames` may enforce additional rules upon the value of `LocalAddress`. 

Additionally, Known `ServiceNames` may enforce rules upon the value of `Port`. 

## Packets

### 0x00 Advertise Services

Sent by PkmCom Clients acting as a Local Server, or other Local Servers to all listening Clients, to indicate which services are available. This packet MUST be sent by such Implementations when they join the Group, and MUST be sent in response to `0x01 Listening For Services`. 

Local Servers may send more than one of these packets. 

#### Payload Fields 

<table>
	<tr>
		<th>Field</th>
		<th>Type</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>Service</td>
		<td>BaseProtocol</td>
		<td>The Underlying Services provided by the Local Server</td>
	</tr>
	<tr>
		<td>NumModes</td>
		<td>Unsigned Short</td>
		<td>The Length of the Modes array</td>
	</tr>
	<tr>
		<td>Flags</td>
		<td>Byte Bitfield</td>
		<td>The Flags of the Service</td>
	</tr>
	<tr>
		<td>Modes</td>
		<td>Array of String</td>
		<td>The Operations Modes applicable</td>
	</tr>
</table>

##### Flags Bitfield

The following Bitfields are defined for the `Flags` Field

<table>
	<tr>
		<th>Bit</th>
		<th>Name</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>0x01</td>
		<td>Password</td>
		<td>If set, alternative handshaking is performed using a user supplied password</td>
	</tr>
</table>





#### Preconditions

`Modes` MUST be structured as `<application>:<modename>`, where *application* matches the regex `[a-z_][a-z0-9_]*` and *modename* matches the regex `[a-z_][a-z0-9_]*(/[a-z_][a-z0-9_]*)*`. Any modes not known MUST be ignored. 

### 0x01 Listening for Services

Empty Packet Broadcast to identify the exposed services and modes of Local Servers. 

### 0x02 No Longer Available

Broadcast for any advertised service which are no longer being advertised

<table>
	<tr>
		<th>Field</th>
		<th>Type</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>Service</td>
		<td>BaseProtocol</td>
		<td>The Service which is no longer available</td>
	</tr>
</table>
