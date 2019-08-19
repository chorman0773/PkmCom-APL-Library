# PkmCom IRC Protocol

PkmCom may be used to implement IRC or internet relay chat. This protocol may be included in any concrete protocol. 
PacketIds for IRC Packets Start at 0x20 and continue through 0x30. Protocols which include this protocol may

## IRC Channels

An IRC Channel consists of 2 parts, the first being the group and the second is the component.

Group names MUST be a valid entity name which matches the regex `[a-z0-9_]:[a-z0-9_](\[a-z0-9_])*)` except that no component may start with 2 consecutive underscore characters. 

## 0x20 Join IRC

Sent by clients who wish to opt-in to a concrete protocol's IRC. 



