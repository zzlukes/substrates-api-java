# Serventis - Observability Extension for Substrates

**Semantic signaling framework for distributed service observability**

Serventis is an extension module built on [Humainary Substrates](../../README.md) that provides
comprehensive observability primitives for distributed services. It enables structured monitoring,
assessment, and reporting of service health, performance, and interaction patterns through semantic
signal emission.

## Overview

Serventis implements a **layered observability hierarchy** that transforms raw operational signals
into actionable situational awareness:

```
Raw Signs → Conditions → Situations → Actions
    ↓             ↓            ↓
Counters/    Monitors →   Reporters
Gauges/   →              ↑
Caches/                  Actors (dialogue coordination)
Probes/                  Agents (promise coordination)
Routers
```

- **Counters**: Monotonic accumulation signs (requests, events)
- **Gauges**: Bidirectional signs (connections, queue depth, utilization)
- **Caches**: Cache interaction signs (lookup, hit, miss, store, evict, expire, remove)
- **Probes**: Low-level communication outcome observation (connect, send, receive, process)
- **Routers**: Packet routing operation signs (send, receive, forward, route, drop, fragment)
- **Actors**: Speech act communication for human-AI dialogue coordination
- **Agents**: Promise-based autonomous coordination (offer, promise, accept, fulfill)
- **Transactions**: Distributed transaction coordination (start, prepare, commit, rollback)
- **Monitors**: Operational condition assessment (stable, degraded, defective, down)
- **Reporters**: Situational priority reporting (normal, elevated, high, critical)
- **Services**: Rich semantic signaling for service lifecycle and coordination
- **Resources**: Resource availability and exhaustion tracking
- **Queues**: Queue occupancy and flow state monitoring

## Architecture

### Module Structure

```
serventis/
├── api/
│   └── Serventis.java  - Common signal abstraction (Signal × Sign × Dimension)
└── ext/
    ├── Actors.java     - Speech act communication for dialogue
    ├── Agents.java     - Promise-based autonomous coordination
    ├── Caches.java     - Cache interaction observation
    ├── Counters.java   - Monotonic accumulation signs
    ├── Gauges.java     - Bidirectional signs
    ├── Monitors.java   - Operational condition assessment
    ├── Probes.java     - Communication outcome observation
    ├── Queues.java     - Queue state monitoring
    ├── Reporters.java  - Situational priority reporting
    ├── Resources.java  - Resource availability tracking
    ├── Routers.java    - Packet routing operation observation
    └── Services.java   - Service lifecycle signaling
```

### Common Abstractions (Serventis API)

The `Serventis` class provides the foundational interfaces that all signal-based APIs implement:

**Core Pattern**: `Signal = Sign × Dimension`

- **Signal**: Observable event composed of a sign and dimension
- **Sign**: Primary semantic classification (what is being observed)
- **Dimension**: Secondary qualifier (perspective, confidence, or directionality)

All Serventis APIs follow this uniform structural pattern:

- **Probes**: Sign (CONNECT, TRANSMIT, etc.) × Dimension (RELEASE, RECEIPT)
- **Services**: Sign (START, CALL, SUCCESS, etc.) × Dimension (RELEASE, RECEIPT)
- **Agents**: Sign (OFFER, PROMISE, FULFILL, etc.) × Dimension (PROMISER, PROMISEE)
- **Monitors**: Sign (STABLE, DEGRADED, etc.) × Dimension (TENTATIVE, MEASURED, CONFIRMED)

This architectural consistency enables:

- Polymorphic handling of signals across different APIs
- Generic utilities that work with any signal type
- Type-safe composition while maintaining domain-specific semantics
- Consistent patterns across the entire framework

### Percept Interface

All Serventis instruments implement the **Percept** interface from `substrates.api`:

```java
public interface Percept {
    // Marker interface for all observable entities
}
```

**Key Concepts**:

- **Percept**: Marker interface for entities capable of emitting observations to the substrate
- **Instruments as Percepts**: All 12 Serventis instruments (Counters, Gauges, Monitors, etc.)
  implement Percept
- **Type Safety**: The Composer, Conduit, and Circuit APIs use `P extends Percept` constraints
  to ensure type safety
- **Pipe Relationship**: `Pipe` also extends Percept, as it's the primitive emission mechanism

**Type Hierarchy**:

```
Percept (substrates.api)
├── Pipe<E> (primitive emission)
└── Instruments (semantic facades)
    ├── Counter (sign emitter)
    ├── Gauge (sign emitter)
    ├── Monitor (signal emitter: Sign × Dimension)
    ├── Probe (signal emitter: Sign × Dimension)
    └── ... (all 12 instruments)
```

This design ensures:

- **Intentionality**: Composers must return percepts (not arbitrary objects)
- **Semantic clarity**: Instruments provide domain semantics over raw Pipe emissions
- **Framework extensibility**: New percept types can be added while maintaining type safety

### Design Principles

1. **Separation of Concerns**: Probes observe, monitors assess, reporters interpret
2. **Semantic Richness**: Signals carry meaning beyond success/failure
3. **Dual Orientation**: RELEASE (self) vs RECEIPT (observed) perspectives
4. **Statistical Confidence**: Conditions include certainty measures (tentative, measured,
   confirmed)
5. **Substrate Integration**: Built on circuits, conduits, and channels for deterministic ordering

## Core APIs

### Actors API

**Purpose**: Speech act observation for human-AI dialogue coordination grounded in Speech Act Theory

**Speech Act Categories**:

1. **Questions & Inquiry** (1 sign): ASK
2. **Assertions & Explanations** (3 signs): ASSERT, EXPLAIN, REPORT
3. **Coordination** (3 signs): REQUEST, COMMAND, ACKNOWLEDGE
4. **Disagreement & Refinement** (2 signs): DENY, CLARIFY
5. **Commitment & Delivery** (2 signs): PROMISE, DELIVER

**Total Signs**: 11 speech acts for practical conversational coordination

**Key Concepts**:

- **Actor**: Any entity (human or machine) performing speech acts
- **Speech Act**: Communicative action with illocutionary force
- **Dialogue**: Sequences of speech acts between coordinating actors
- **Commitment Arc**: PROMISE → DELIVER tracking for reliability measurement

**Dialogue Patterns**:

- **Question-Answer**: ASK → EXPLAIN → ASSERT → ACKNOWLEDGE
- **Request-Delivery**: REQUEST → ACKNOWLEDGE → PROMISE → DELIVER → ACKNOWLEDGE
- **Correction-Clarification**: ASSERT → DENY → CLARIFY → ACKNOWLEDGE
- **Collaborative Refinement**: REQUEST → EXPLAIN → DENY → CLARIFY → ACKNOWLEDGE → DELIVER

**Use Cases**:

- Human-AI collaboration tracking and measurement
- Conversational quality assessment
- Commitment fulfillment monitoring (PROMISE → DELIVER)
- Coordination effectiveness metrics
- Dialogue pattern analysis (question response latency, correction cycles)

**Performance**: Conversational timescales (seconds to minutes), high semantic density

**Relationship to Other APIs**:

- **Monitors API**: Actors ASSERT monitor conditions about system state
- **Counters, Gauges, Caches**: Actors REPORT observations from instruments
- **Routers, Resources**: Actors REQUEST actions on system components
- Meta-percepts observe dialogue to measure collaboration effectiveness

---

### Agents API

**Purpose**: Promise-based autonomous agent coordination grounded in Promise Theory

**Promise Theory Model**:

Promise Theory (Mark Burgess) models systems as autonomous agents making voluntary commitments
rather than following imposed policies. This API enables observation of promise lifecycles in
distributed autonomous systems.

**Dual-Perspective Model**:

- **PROMISER**: Self-perspective signals ("I am promising", "I am fulfilling")
- **PROMISEE**: Other-perspective signals ("They promised", "They fulfilled")

**Total Signals**: 20 signals (10 signs × 2 perspectives)

**The 10 Promise Signs**:

1. **OFFER**: Agent advertises capability or intent (discovery phase)
2. **PROMISE**: Agent commits to deliver obligation (commitment phase)
3. **ACCEPT**: Agent acknowledges promise from another (commitment phase)
4. **FULFILL**: Agent completes promised obligation (delivery phase)
5. **RETRACT**: Agent withdraws promise before fulfillment (coordination phase)
6. **BREACH**: Agent fails to fulfill promise (failure mode)
7. **INQUIRE**: Agent requests information about capabilities (discovery phase)
8. **OBSERVE**: Agent witnesses another's state or behavior (assessment phase)
9. **DEPEND**: Agent declares dependency on another's promise (coordination phase)
10. **VALIDATE**: Agent confirms another's fulfillment meets expectations (verification phase)

**Key Concepts**:

- **Agent**: Autonomous entity making voluntary promises
- **Promise**: Voluntary commitment to maintain or deliver state
- **Signal**: Perspectival observation (PROMISER self-view, PROMISEE other-view)
- **Sign**: The type of promise-related action being performed
- **Perspective**: PROMISER (self) vs PROMISEE (observed other)

**Promise Lifecycle Patterns**:

- **Discovery**: INQUIRE → OFFERED → OBSERVE
- **Commitment**: OFFER → PROMISED → ACCEPTED
- **Dependency**: DEPEND → ACCEPTED → PROMISED
- **Fulfillment**: PROMISE → FULFILL
- **Breach**: PROMISE → BREACH
- **Retraction**: PROMISE → RETRACT → ACCEPTED
- **Complete Arc**: OFFER → PROMISE → ACCEPT → FULFILL → VALIDATE

**Use Cases**:

- Autonomous system coordination without central control
- Voluntary cooperation between independent services
- Trust and reliability measurement through promise fulfillment
- Decentralized resource allocation
- Capability discovery in dynamic environments
- Dependency tracking between autonomous components
- Contract-based coordination patterns

**Performance**: Coordination timescales (seconds to minutes), high semantic density

**Relationship to Other APIs**:

- **Actors API**: Complementary - Actors handle dialogue (ASK, EXPLAIN), Agents handle commitments (
  PROMISE, FULFILL)
- **Services API**: Agents PROMISE to provide service capabilities
- **Resources API**: Agents OFFER and PROMISE resource availability
- **Monitors API**: Agents OBSERVE conditions and VALIDATE fulfillment
- Meta-percepts track promise fulfillment rates, breach patterns, and autonomy effectiveness

**Promise Theory vs Speech Act Theory**:

- **Agents (Promise Theory)**: Autonomous entities making voluntary commitments - focus on *
  *voluntary cooperation**
- **Actors (Speech Acts)**: Conversational entities performing communicative acts - focus on *
  *dialogue coordination**

These APIs complement each other: Actors establish shared understanding through dialogue, Agents
establish commitments through promises. Both operate at conversational timescales but model
different aspects of coordination.

---

### Counters API

**Purpose**: Semantic signaling for monotonically increasing counters

**Signs**:

- **INCREMENT**: Normal accumulation (expected increase)
- **OVERFLOW**: Boundary violation (exceeded maximum value)
- **RESET**: Intentional zeroing (operator intervention)

**Use Cases**:

- Request counting (total requests processed)
- Event accumulation (errors encountered)
- Throughput tracking

**Performance**: 10M-50M Hz (zero-allocation sign emission)

---

### Gauges API

**Purpose**: Semantic signaling for bidirectional gauges with capacity awareness

**Signs**:

- **INCREMENT**: Resource acquisition, entry, allocation
- **DECREMENT**: Resource release, exit, deallocation
- **OVERFLOW**: Capacity exceeded (saturation or wrapping)
- **UNDERFLOW**: Below minimum (starvation, empty state)
- **RESET**: Return to baseline

**Use Cases**:

- Active connection tracking
- Queue depth monitoring
- Thread pool utilization
- Memory pressure tracking
- Resource capacity planning

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Counters**: Gauges are the bidirectional superset; Counters are the monotonic
subset

---

### Caches API

**Purpose**: Semantic signaling for cache interaction observation

**Signs**:

- **LOOKUP**: An attempt to retrieve an entry from the cache
- **HIT**: A lookup succeeded - entry was found in cache
- **MISS**: A lookup failed - entry was not found in cache
- **STORE**: An entry was added or updated in the cache
- **EVICT**: An entry was automatically removed due to capacity/policy
- **EXPIRE**: An entry was removed due to TTL/expiration
- **REMOVE**: An entry was explicitly invalidated/removed

**Use Cases**:

- Tracking cache effectiveness through hit/miss patterns
- Monitoring cache capacity pressure via eviction frequency
- Detecting staleness issues through expiration patterns
- Understanding cache churn through removal and store frequency

**Performance**: 10M-50M Hz (zero-allocation sign emission)

---

### Probes API

**Purpose**: Structured observation of communication outcomes across distributed boundaries

**Key Concepts**:

- **Outcome**: SUCCESS or FAILURE
- **Origin**: CLIENT (requester) or SERVER (responder)
- **Operation**: CONNECT, SEND, RECEIVE, PROCESS

**Use Cases**:

- Network communication monitoring
- RPC call instrumentation
- Protocol-level diagnostics
- Distributed tracing foundation

---

### Routers API

**Purpose**: Semantic signaling for packet routing operations within network systems

**Signs**:

- **SEND**: A packet was transmitted (originated by this node)
- **RECEIVE**: A packet was received from the network
- **FORWARD**: A packet was forwarded to next hop
- **ROUTE**: A routing decision was made for a packet
- **DROP**: A packet was discarded (congestion, policy, TTL, etc.)
- **FRAGMENT**: A packet was fragmented due to MTU
- **REASSEMBLE**: Packet fragments were reassembled
- **CORRUPT**: Packet corruption was detected (checksum, malformed)
- **REORDER**: Out-of-order packet arrival was detected

**Key Distinctions**:

- **SEND vs FORWARD**: SEND originates packets (source node), FORWARD routes through (intermediary)
- **ROUTE**: Routing decision/table lookup, may precede FORWARD
- **DROP reasons**: Correlate with congestion, policy, TTL, or routing failures

**Use Cases**:

- Monitoring traffic flows through network infrastructure
- Detecting packet loss and corruption patterns
- Observing routing decisions and path changes
- Tracking fragmentation requirements and MTU issues
- Building congestion and performance models

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Other APIs**:

- **Queues API**: Router buffers modeled as queues (packet enqueue/dequeue)
- **Gauges API**: In-flight packets, buffer occupancy tracked via gauges
- **Monitors API**: High drop rates may trigger DEGRADED or DEFECTIVE conditions

---

### Monitors API

**Purpose**: Objective assessment of operational conditions based on signal pattern analysis

**Operational Conditions** (7 states):

| Condition  | Stability | Meaning                                    |
|------------|-----------|--------------------------------------------|
| CONVERGING | Improving | Stabilizing toward reliable operation      |
| STABLE     | Nominal   | Operating within expected parameters       |
| DIVERGING  | Degrading | Destabilizing with increasing variations   |
| ERRATIC    | Chaotic   | Unpredictable behavior, irregular patterns |
| DEGRADED   | Impaired  | Reduced performance, elevated errors       |
| DEFECTIVE  | Failing   | Predominantly failed operations            |
| DOWN       | Failed    | Entirely non-operational                   |

**Confidence Levels**: TENTATIVE → MEASURED → CONFIRMED

**Use Cases**:

- Service health dashboards
- Anomaly detection
- Adaptive circuit breakers
- Capacity planning alerts

---

### Services API

**Purpose**: Rich semantic signaling for service lifecycle, coordination, and work execution

**Dual-Orientation Model**:

- **RELEASE**: Self-perspective ("I am doing this now") - present tense
- **RECEIPT**: Other-perspective ("I observed that happened") - past tense

**Use Cases**:

- Distributed tracing
- Request lifecycle tracking
- Coordination pattern observation
- Failure mode detection

---

### Resources API

**Purpose**: Resource availability and exhaustion tracking

**Resource States**:

- **ACQUIRED**: Resource obtained successfully
- **RELEASED**: Resource returned to pool
- **DEPLETED**: Resource pool exhausted
- **RESTORED**: Resource availability recovered

**Use Cases**:

- Connection pool monitoring
- Memory pressure detection
- Thread pool saturation tracking
- Quota enforcement

---

### Queues API

**Purpose**: Queue occupancy and flow state monitoring

**Queue States**:

- **FULL**: Queue capacity reached (backpressure)
- **EMPTY**: Queue drained (starvation)
- **FILLING**: Queue occupancy increasing
- **DRAINING**: Queue occupancy decreasing

**Use Cases**:

- Backpressure detection
- Consumer starvation alerts
- Flow control optimization
- Capacity planning

---

### Reporters API

**Purpose**: Situational priority reporting with urgency classification

**Situations** (4 priority levels):

| Situation | Priority | Meaning                               |
|-----------|----------|---------------------------------------|
| NORMAL    | Low      | Routine operation, no action required |
| ELEVATED  | Medium   | Attention warranted, monitor closely  |
| HIGH      | Urgent   | Intervention needed soon              |
| CRITICAL  | Severe   | Immediate action required             |

**Use Cases**:

- Alert routing
- Incident management
- On-call escalation
- SLA violation detection

## Observability Flow

### Layered Metrics Architecture

Serventis provides a **complete metrics stack** from raw accumulation to situational assessment:

```
┌─────────────────────────────────────────────────────────┐
│  Actions (automated responses)                          │
└─────────────────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────────────────┐
│  Reporters (situational priority)                       │
│  - NORMAL, ELEVATED, HIGH, CRITICAL                     │
└─────────────────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────────────────┐
│  Monitors (condition classification)                    │
│  - STABLE, DEGRADED, ERRATIC, DOWN                      │
└─────────────────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────────────────┐
│  Observer Agents (signal aggregation)                   │
│  - Rate calculations, threshold detection               │
└─────────────────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────────────────┐
│  Raw Signs (instruments)                                │
│  - Counters (monotonic), Gauges (bidirectional)         │
│  - Caches (interactions), Probes (outcomes)             │
│  - Routers (packets), Services (lifecycle)              │
│  - Resources, Queues, Actors (dialogue)                 │
│  - Agents (promises)                                    │
└─────────────────────────────────────────────────────────┘
```

### Complete Example: HTTP Service Monitoring

## Integration with Substrates

Serventis is built on Substrates primitives with type-safe Percept constraints:

- **Composers**: `Composer<E, P extends Percept>` - Create domain-specific percepts (Probe, Monitor,
  Service, etc.)
- **Conduits**: `Conduit<P extends Percept, E>` - Pool percepts by name with type safety
- **Circuits**: `Circuit.conduit<P extends Percept, E>()` - Create conduits with percept constraints
- **Channels**: Named signal sources organized hierarchically (factories for pipes)
- **Pipes**: `Pipe<E> extends Percept` - Primitive emission mechanism
- **Subscribers**: React to signals with aggregation logic

All signal emission leverages Substrates' **recursive emission ordering** to ensure cascading
effects complete atomically before external observations.

**Type Safety**: The framework enforces that composers return percepts, ensuring intentionality
in instrument design. Channels create pipes (which are percepts), but channels themselves are not
percepts - they're just plumbing. This architectural distinction maintains clear separation between
emission mechanisms (pipes) and semantic facades (instruments).

## Performance Characteristics

- **High-frequency counters/gauges**: 10M-50M Hz (zero-allocation sign emission)
- **High-frequency caches**: 10M-50M Hz (zero-allocation sign emission)
- **High-frequency services**: 10M-50M Hz (zero-allocation sign emission)
- **High-frequency probes**: 1M-10M Hz (per-request instrumentation)
- **Medium-frequency monitors**: 100K-1M Hz (condition assessment)
- **Low-frequency reporters**: 1K-100K Hz (situational updates)

Sign emissions leverage **zero-allocation enum emission** with ~10-20ns cost for non-transit emits.
Transit emits (cascading within circuit thread) achieve **sub-3ns latency** (currently ~2.98ns,
achieving 336M ops/sec on substrate benchmarks).

## Building

Serventis is built as part of the Substrates project:

```bash
# Build serventis extension (without TCK tests)
./mvnw clean install -pl substrates/ext/serventis

# Run specific test suite
./mvnw test -Dtest=ActorsTest -pl substrates/ext/serventis
./mvnw test -Dtest=AgentsTest -pl substrates/ext/serventis
./mvnw test -Dtest=CountersTest -pl substrates/ext/serventis
./mvnw test -Dtest=GaugesTest -pl substrates/ext/serventis
./mvnw test -Dtest=CachesTest -pl substrates/ext/serventis
./mvnw test -Dtest=MonitorsTest -pl substrates/ext/serventis
./mvnw test -Dtest=RoutersTest -pl substrates/ext/serventis
```

### Running TCK Tests

Serventis includes TCK tests that validate integration with SPI implementations. The TCK is optional
and activated via the `tck` profile:

```bash
# Run Serventis with TCK tests using default SPI (alpha implementation)
./mvnw clean install -pl substrates/ext/serventis -Dtck

# Test with a custom SPI implementation
./mvnw clean install -pl substrates/ext/serventis -Dtck \
  -Dsubstrates.spi.groupId=com.example \
  -Dsubstrates.spi.artifactId=my-substrates-spi \
  -Dsubstrates.spi.version=1.0.0
```

**Note**: Tests are skipped by default (`maven.test.skip=true` in `pom.xml`). The `tck` profile
overrides this to enable testing. See the [Substrates README](../../README.md) for more details on
TCK configuration.

## License

Copyright © 2025 William David Louth. All rights reserved.

## Links

- [Humainary.io](https://humainary.io)
