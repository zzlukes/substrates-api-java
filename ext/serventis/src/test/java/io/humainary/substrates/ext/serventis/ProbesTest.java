// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Probes;
import io.humainary.substrates.ext.serventis.ext.Probes.Dimension;
import io.humainary.substrates.ext.serventis.ext.Probes.Probe;
import io.humainary.substrates.ext.serventis.ext.Probes.Sign;
import io.humainary.substrates.ext.serventis.ext.Probes.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Probes.Dimension.RECEIPT;
import static io.humainary.substrates.ext.serventis.ext.Probes.Dimension.RELEASE;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// The test class for the [Probe] interface.
///
/// @author William David Louth
/// @since 1.0
final class ProbesTest {

  private static final Cortex               cortex = cortex ();
  private static final Name                 NAME   = cortex.name ( "service.1" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Probe                probe;

  private void assertSignal (
    final Signal signal
  ) {

    circuit
      .await ();

    assertEquals (
      1L, reservoir
        .drain ()
        .filter ( capture -> capture.subject ().name () == NAME )
        .map ( Capture::emission )
        .filter ( s -> s.equals ( signal ) )
        .count ()

    );

  }

  private void emit (
    final Signal signal,
    final Runnable emitter
  ) {

    emitter.run ();

    assertSignal (
      signal
    );

  }

  @BeforeEach
  void setup () {

    circuit =
      cortex ().circuit ();

    final var conduit =
      circuit.conduit (
        Probes::composer
      );

    probe =
      conduit.percept (
        NAME
      );

    reservoir =
      cortex ().reservoir (
        conduit
      );

  }

  @Test
  void testConnect () {

    emit ( new Signal ( Sign.CONNECT, RELEASE ), probe::connect );

  }

  @Test
  void testConnected () {

    emit ( new Signal ( Sign.CONNECT, RECEIPT ), probe::connected );

  }

  @Test
  void testDisconnect () {

    emit ( new Signal ( Sign.DISCONNECT, RELEASE ), probe::disconnect );

  }

  @Test
  void testDisconnected () {

    emit ( new Signal ( Sign.DISCONNECT, RECEIPT ), probe::disconnected );

  }

  @Test
  void testFail () {

    emit ( new Signal ( Sign.FAIL, RELEASE ), probe::fail );

  }

  @Test
  void testFailed () {

    emit ( new Signal ( Sign.FAIL, RECEIPT ), probe::failed );

  }

  @Test
  void testMultipleEmissions () {

    probe.connect ();
    probe.transmit ();
    probe.succeed ();
    probe.disconnect ();

    circuit
      .await ();

    assertEquals (
      4L,
      reservoir
        .drain ()
        .count ()
    );

  }

  @Test
  void testOrientationEnumOrdinals () {

    assertEquals ( 0, RELEASE.ordinal () );
    assertEquals ( 1, RECEIPT.ordinal () );

  }

  @Test
  void testOrientationEnumValues () {

    final var values = Dimension.values ();

    assertEquals ( 2, values.length );

  }

  @Test
  void testProcess () {

    emit ( new Signal ( Sign.PROCESS, RELEASE ), probe::process );

  }

  @Test
  void testProcessed () {

    emit ( new Signal ( Sign.PROCESS, RECEIPT ), probe::processed );

  }

  @Test
  void testReceive () {

    emit ( new Signal ( Sign.RECEIVE, RELEASE ), probe::receive );

  }

  @Test
  void testReceived () {

    emit ( new Signal ( Sign.RECEIVE, RECEIPT ), probe::received );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, Sign.CONNECT.ordinal () );
    assertEquals ( 1, Sign.DISCONNECT.ordinal () );
    assertEquals ( 2, Sign.TRANSMIT.ordinal () );
    assertEquals ( 3, Sign.RECEIVE.ordinal () );
    assertEquals ( 4, Sign.PROCESS.ordinal () );
    assertEquals ( 5, Sign.SUCCEED.ordinal () );
    assertEquals ( 6, Sign.FAIL.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 7, values.length );

  }

  @Test
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    probe.signal ( Sign.CONNECT, RELEASE );
    probe.signal ( Sign.CONNECT, RECEIPT );
    probe.signal ( Sign.DISCONNECT, RELEASE );
    probe.signal ( Sign.DISCONNECT, RECEIPT );
    probe.signal ( Sign.TRANSMIT, RELEASE );
    probe.signal ( Sign.TRANSMIT, RECEIPT );
    probe.signal ( Sign.RECEIVE, RELEASE );
    probe.signal ( Sign.RECEIVE, RECEIPT );
    probe.signal ( Sign.PROCESS, RELEASE );
    probe.signal ( Sign.PROCESS, RECEIPT );
    probe.signal ( Sign.SUCCEED, RELEASE );
    probe.signal ( Sign.SUCCEED, RECEIPT );
    probe.signal ( Sign.FAIL, RELEASE );
    probe.signal ( Sign.FAIL, RECEIPT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 14, signals.size () );
    assertEquals ( new Signal ( Sign.CONNECT, RELEASE ), signals.get ( 0 ) );
    assertEquals ( new Signal ( Sign.CONNECT, RECEIPT ), signals.get ( 1 ) );
    assertEquals ( new Signal ( Sign.DISCONNECT, RELEASE ), signals.get ( 2 ) );
    assertEquals ( new Signal ( Sign.DISCONNECT, RECEIPT ), signals.get ( 3 ) );
    assertEquals ( new Signal ( Sign.TRANSMIT, RELEASE ), signals.get ( 4 ) );
    assertEquals ( new Signal ( Sign.TRANSMIT, RECEIPT ), signals.get ( 5 ) );
    assertEquals ( new Signal ( Sign.RECEIVE, RELEASE ), signals.get ( 6 ) );
    assertEquals ( new Signal ( Sign.RECEIVE, RECEIPT ), signals.get ( 7 ) );
    assertEquals ( new Signal ( Sign.PROCESS, RELEASE ), signals.get ( 8 ) );
    assertEquals ( new Signal ( Sign.PROCESS, RECEIPT ), signals.get ( 9 ) );
    assertEquals ( new Signal ( Sign.SUCCEED, RELEASE ), signals.get ( 10 ) );
    assertEquals ( new Signal ( Sign.SUCCEED, RECEIPT ), signals.get ( 11 ) );
    assertEquals ( new Signal ( Sign.FAIL, RELEASE ), signals.get ( 12 ) );
    assertEquals ( new Signal ( Sign.FAIL, RECEIPT ), signals.get ( 13 ) );

  }

  @Test
  void testSignalAccessors () {

    final var connect = new Signal ( Sign.CONNECT, RELEASE );
    assertEquals ( Sign.CONNECT, connect.sign () );
    assertEquals ( RELEASE, connect.dimension () );

    final var connected = new Signal ( Sign.CONNECT, RECEIPT );
    assertEquals ( Sign.CONNECT, connected.sign () );
    assertEquals ( RECEIPT, connected.dimension () );

    final var succeed = new Signal ( Sign.SUCCEED, RELEASE );
    assertEquals ( Sign.SUCCEED, succeed.sign () );
    assertEquals ( RELEASE, succeed.dimension () );

    final var failed = new Signal ( Sign.FAIL, RECEIPT );
    assertEquals ( Sign.FAIL, failed.sign () );
    assertEquals ( RECEIPT, failed.dimension () );

  }

  @Test
  void testSignalCoverage () {

    // 7 signs × 2 dimensions = 14 signal combinations
    assertEquals ( 7, Sign.values ().length );
    assertEquals ( 2, Dimension.values ().length );

  }

  @Test
  void testSubjectAttachment () {

    probe.connect ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( new Signal ( Sign.CONNECT, RELEASE ), capture.emission () );

  }

  @Test
  void testSucceed () {

    emit ( new Signal ( Sign.SUCCEED, RELEASE ), probe::succeed );

  }

  @Test
  void testSucceeded () {

    emit ( new Signal ( Sign.SUCCEED, RECEIPT ), probe::succeeded );

  }

  @Test
  void testTransmit () {

    emit ( new Signal ( Sign.TRANSMIT, RELEASE ), probe::transmit );

  }

  @Test
  void testTransmitted () {

    emit ( new Signal ( Sign.TRANSMIT, RECEIPT ), probe::transmitted );

  }

}
