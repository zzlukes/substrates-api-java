// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.ext;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

import static io.humainary.substrates.ext.serventis.ext.Probes.Dimension.RECEIPT;
import static io.humainary.substrates.ext.serventis.ext.Probes.Dimension.RELEASE;

/// The `Probes` API provides a structured framework for monitoring and reporting
/// communication operations and outcomes in distributed systems. It enables observation
/// of operations from both self-perspective and observed-perspective.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting communication operation outcomes**, not implementing network protocols.
/// If you have actual networking code (HTTP clients, RPC frameworks, message brokers, etc.),
/// use this API to emit observability signals about operations performed. Observer agents can
/// then reason about communication patterns, failure modes, and distributed system reliability
/// without coupling to your protocol or transport implementation details.
///
/// **Example**: Your HTTP client connects to a remote server. Call `probe.connect()` to emit
/// a RELEASE signal ("I am connecting"). When you observe the server has connected, call
/// `probe.connected()` to emit a RECEIPT signal ("It connected"). The signals enable
/// meta-observability: observing the communication operations themselves to understand
/// network behavior and identify failure patterns.
///
/// ## Key Concepts
///
/// This API is built around two core dimensions:
/// - **Sign**: What happened (operation or outcome: CONNECT, TRANSMIT, SUCCEED, FAIL, etc.)
/// - **Dimension**: From whose perspective (RELEASE = self, RECEIPT = observed)
///
/// By combining sign and dimension into signals, the API enables detailed diagnostics and
/// monitoring of distributed communication patterns from dual perspectives.
///
/// ## Usage Example
///
/// ```java
/// final var cortex = Substrates.cortex();
/// // Create a probe for an RPC call
/// var probe = circuit.conduit(Probes::composer).percept(cortex.name("rpc"));
///
/// // Self-perspective (RELEASE) - "I am doing this"
/// probe.connect();      // I am connecting
/// probe.transmit();     // I am transmitting data
/// probe.receive();      // I am receiving data
/// probe.process();      // I am processing
/// probe.succeed();      // I succeeded
/// probe.disconnect();   // I am disconnecting
///
/// // Observed-perspective (RECEIPT) - "It did that"
/// probe.connected();    // It connected
/// probe.transmitted();  // It transmitted
/// probe.received();     // It received
/// probe.processed();    // It processed
/// probe.succeeded();    // It succeeded
/// probe.disconnected(); // It disconnected
///
/// // Failure reporting
/// probe.fail();         // I failed (RELEASE)
/// probe.failed();       // It failed (RECEIPT)
/// ```
///
/// ## Relationship to Other APIs
///
/// `Probes` provides foundational observation data for higher-level APIs:
///
/// - **Monitors API**: Aggregates probe observations to assess operational conditions
///   - Many FAIL signals may indicate DEGRADED or DEFECTIVE conditions
///   - Connection failures suggest DIVERGING or DOWN states
/// - **Services API**: Probe observations inform service lifecycle
///   - DISCONNECT maps to service DISCONNECT
///   - FAIL maps to service FAIL
/// - **Reporters API**: Signal patterns inform situational assessments
///   - Sustained failures elevate from NORMAL to WARNING or CRITICAL situations
///
/// ## Dual-Dimension Model
///
/// The dual-dimension model enables observing communication from two perspectives:
///
/// | Dimension | Perspective | Tense   | Example           |
/// |-----------|-------------|---------|-------------------|
/// | RELEASE   | Self        | Present | "I am connecting" |
/// | RECEIPT   | Observed    | Past    | "It connected"    |
///
/// **Example**: `probe.connect()` emits CONNECT (Sign.CONNECT, RELEASE) indicating
/// self-initiated action, while `probe.connected()` emits CONNECTED (Sign.CONNECT, RECEIPT)
/// indicating observed completion.
///
/// ## Performance Considerations
///
/// Probe emissions are designed for high-frequency operation at request granularity.
/// Typical rates: 100-100K signals/sec per probe. Signals flow asynchronously
/// through the circuit's event queue, adding minimal overhead (<50ns) to instrumented
/// operations. For extremely high-frequency operations (>1M ops/sec), consider sampling
/// or aggregating signals before emission.
///
/// @author William David Louth
/// @since 1.0

public final class Probes
  implements Serventis {

  private Probes () { }

  /// A static composer function for creating Probe instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var probe = circuit.conduit(Probes::composer).percept(cortex.name("rpc.client"));
  /// ```
  ///
  /// @param channel the channel from which to create the probe
  /// @return a new Probe instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Probe composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Probe (
        channel.pipe ()
      );

  }

  /// The [Sign] enum represents the type of communication event.
  ///
  /// Signs include both operations (CONNECT, TRANSMIT, RECEIVE, PROCESS) and outcomes (SUCCEED, FAIL),
  /// treating them as peer events rather than dimensional cross products.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates connection establishment
    CONNECT,

    /// Indicates connection closure
    DISCONNECT,

    /// Indicates data transmission
    TRANSMIT,

    /// Indicates data reception
    RECEIVE,

    /// Indicates data processing
    PROCESS,

    /// Indicates successful completion
    SUCCEED,

    /// Indicates failed completion
    FAIL

  }

  /// The [Dimension] enum represents the perspective from which a signal is emitted.
  ///
  /// Every sign has two dimensions representing self-perspective and other-perspective.

  public enum Dimension
    implements Serventis.Dimension {

    /// Self-perspective emission (present tense: "I am doing this")
    RELEASE,

    /// Other-perspective observation (past tense: "It did that")
    RECEIPT

  }

  /// A [Probe] is an instrument that emits signals about communication operations.
  /// It serves as the primary reporting mechanism within the Probes API.
  ///
  /// Probes can be attached to various components within a distributed system to monitor
  /// and report on communication operations and outcomes.
  ///
  /// ## Usage
  ///
  /// Use semantic methods for all communication events:
  /// ```java
  /// probe.connect();      // RELEASE: "I am connecting" (self-perspective)
  /// probe.connected();    // RECEIPT: "It connected" (observed)
  /// probe.transmit();     // RELEASE: "I am transmitting" (self-perspective)
  /// probe.transmitted();  // RECEIPT: "It transmitted" (observed)
  /// probe.succeed();      // RELEASE: "I succeeded" (self-perspective)
  /// probe.failed();       // RECEIPT: "It failed" (observed)
  /// ```
  ///
  /// Probes provide simple, direct methods that make communication monitoring expressive.

  @Provided
  public static final class Probe
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Probe (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// A RELEASE signal indicating connection establishment
    public void connect () {

      pipe.emit (
        SIGNALS.get (
          Sign.CONNECT,
          RELEASE
        )
      );

    }

    /// A RECEIPT signal indicating connection establishment
    public void connected () {

      pipe.emit (
        SIGNALS.get (
          Sign.CONNECT,
          RECEIPT
        )
      );

    }

    /// A RELEASE signal indicating connection closure
    public void disconnect () {

      pipe.emit (
        SIGNALS.get (
          Sign.DISCONNECT,
          RELEASE
        )
      );

    }

    /// A RECEIPT signal indicating connection closure
    public void disconnected () {

      pipe.emit (
        SIGNALS.get (
          Sign.DISCONNECT,
          RECEIPT
        )
      );

    }

    /// A RELEASE signal indicating failed completion
    public void fail () {

      pipe.emit (
        SIGNALS.get (
          Sign.FAIL,
          RELEASE
        )
      );

    }

    /// A RECEIPT signal indicating failed completion
    public void failed () {

      pipe.emit (
        SIGNALS.get (
          Sign.FAIL,
          RECEIPT
        )
      );

    }

    /// A RELEASE signal indicating data processing
    public void process () {

      pipe.emit (
        SIGNALS.get (
          Sign.PROCESS,
          RELEASE
        )
      );

    }

    /// A RECEIPT signal indicating data processing
    public void processed () {

      pipe.emit (
        SIGNALS.get (
          Sign.PROCESS,
          RECEIPT
        )
      );

    }

    /// A RELEASE signal indicating data reception
    public void receive () {

      pipe.emit (
        SIGNALS.get (
          Sign.RECEIVE,
          RELEASE
        )
      );

    }

    /// A RECEIPT signal indicating data reception
    public void received () {

      pipe.emit (
        SIGNALS.get (
          Sign.RECEIVE,
          RECEIPT
        )
      );

    }

    /// Signals a communication event by composing sign and dimension.
    ///
    /// @param sign      the sign component
    /// @param dimension the dimension component

    @Override
    public void signal (
      final Sign sign,
      final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          sign,
          dimension
        )
      );

    }

    /// A RELEASE signal indicating successful completion
    public void succeed () {

      pipe.emit (
        SIGNALS.get (
          Sign.SUCCEED,
          RELEASE
        )
      );

    }

    /// A RECEIPT signal indicating successful completion
    public void succeeded () {

      pipe.emit (
        SIGNALS.get (
          Sign.SUCCEED,
          RECEIPT
        )
      );

    }

    /// A RELEASE signal indicating data transmission
    public void transmit () {

      pipe.emit (
        SIGNALS.get (
          Sign.TRANSMIT,
          RELEASE
        )
      );

    }

    /// A RECEIPT signal indicating data transmission
    public void transmitted () {

      pipe.emit (
        SIGNALS.get (
          Sign.TRANSMIT,
          RECEIPT
        )
      );

    }

  }

  /// The [Signal] record represents a communication signal composed of a sign and dimension.
  ///
  /// Signals are the composition of Sign (what happened) and Dimension (from whose perspective),
  /// enabling observation of communication operations from both self and observed perspectives.
  ///
  /// @param sign      the communication event classification
  /// @param dimension the perspective from which the signal is emitted

  @Provided
  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

}
