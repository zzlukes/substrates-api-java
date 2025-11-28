# DPI Concept Map: From Mosquitoes to Packets

This document serves as a "Rosetta Stone" for pivoting the **Mosquito Defense Simulation** into a **Deep Packet Inspection (DPI)** simulation for high-speed networks.

The core physics and latency constraints remain identical; only the metaphors change.

## The Core Metaphor

| Simulation Entity | Network Security Equivalent | Description |
| :--- | :--- | :--- |
| **Mosquito** | **Malicious Packet** | A zero-day exploit or attack vector hidden in high-volume traffic. |
| **Chamber** | **Network Buffer / Switch** | The memory space where packets reside before being processed or forwarded. |
| **Turret** | **DPI Engine / Firewall** | The logic that inspects traffic and enforces security rules. |
| **Laser Shot** | **Packet Drop / Reset** | The active intervention to neutralize the threat (dropping the packet or sending a TCP RST). |
| **Latency** | **Inspection Overhead** | The time taken to analyze the packet (Cloud = Centralized AI Analysis, Edge = In-line FPGA/ASIC). |
| **Wingbeat (600Hz)** | **Packet Frequency / Jitter** | High-frequency signal variations that identify the protocol or threat signature. |
| **3D Evasion** | **Obfuscation / Tunneling** | Techniques used by attackers to hide the payload (e.g., fragmentation, encryption, GRE tunneling). |
| **Arrival Rate** | **Throughput (Gbps)** | The volume of traffic entering the network. |
| **Overrun** | **Buffer Overflow / Breach** | When the inspection engine cannot keep up, packets are dropped (DoS) or malicious traffic slips through. |

## The "Tipping Point" in Networking

In the simulation, we established a **17ms Tipping Point**. In high-frequency trading (HFT) or carrier-grade NAT (CGNAT), the timescales are even tighter (microseconds), but the principle is the same.

### The "Death Spiral"
*   **Simulation**: Missed shot -> Lose Lock -> Re-acquire (Slow) -> Chamber fills up.
*   **Networking**: Missed inspection -> Packet buffered -> Queue fills up -> Latency spikes -> More packets buffered -> **Congestion Collapse**.

## Why Edge (Substrates) Wins
*   **Cloud (Traditional)**: Sending packet headers to a central cloud for analysis introduces **WAN Latency**. By the time the "Drop" command returns, the malicious packet has already passed through the switch.
*   **Edge (Substrates)**: The inspection logic lives **on the device** (e.g., SmartNIC, P4 Switch). It detects and drops the packet within microseconds, preventing the buffer from ever filling up.

## Next Steps for the Simulation
To convert the visualizer:
1.  **Visuals**: Replace Mosquitoes with "Data Packets" (squares/dots flowing in a stream).
2.  **Turrets**: Replace with "Gatekeepers" or "Filters" on the stream.
3.  **Z-Axis**: Visualize as "Encapsulation Layers" (e.g., Packet inside a Tunnel). The filter must "peel back" layers to see the threat.
