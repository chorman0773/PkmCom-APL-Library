# PkmCom
PkmCom is a application level, packet buffered protocol, built over lower level protocols, called Base Protocols. 

PkmCom is designed only to define transports and structure. Concrete Protocols are used to define the operation of the PkmCom. 

## Abstract Protocol 
The Top level Protocol is called the Abstract Protocol, and defines how Packets are structured, and verified. 
The abstract Protocol can be found [here](/AbstractProtocol.md). 

## Base Protocols

There are a number official base protocols, which are usually indicated as `PkmCom/<underlying protocol>` (To be read as PkmCom over *underlying protocol*). 

Applications may define there own base protocols, and are encouraged to release the specification for such Base Protocols. 

The currently defined Base Protocols are:
* [PkmCom/TCP](https://chorman0773.github.io/PkmCom-APL-Library/TCP.md)
* [PkmCom/Multicast UDP](https://chorman0773.github.io/PkmCom-APL-Library/Multicast.md)

### PkmCom/TCP
PkmCom/TCP is the primary Base Protocol 


## Concrete Protocols

It is expected that applications design there own Protocol around PkmCom, called a Concrete Protocol, which supplies its own packet ids and payloads. 

### Local Network Service Discovery

An official Concrete Protocol is used with the PkmCom/Multicast UDP Base Protocol to discover services of PkmCom Local Servers. 

The definition of this Concrete Protocol, called the PkmCom LAN Service Discovery Protocol, can be found [here](https://chorman0773.github.io/PkmCom-APL-Library/LAN.md)

### Remote Service Discovery

An additional Concrete Protocol is used with the PkmCom/TCP Base Protocol to discover services and check availability of remote servers implementing various Concrete Protocols. 

The definition of this Concrete Protocol can be found [here](https://chorman0773.github.io/PkmCom-APL-Library/RemoteServiceDiscovery.md). 
