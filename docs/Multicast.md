# PkmCom/Multicast UDP

This document describes the modifications to the PkmCom APL necessary to provide it as PkmCom/Multicast UDP. 

1. A Port and Multicast Group are to be specified by the Complete Protocol. Instead of opening a socket as with PkmCom/TCP, clients for PkmCom/Multicast UDP MUST connect to the specified Multicast Group with the specified port.
2. There is a upper limit for the total size of all packets (including header and payload), of 16777216 bytes. Therefore, The maximum size of any packets payload is 16777207 bytes (as the header is fixed at 9 total bytes). 
3. There is no encryption for PkmCom/Multicast UDP. 