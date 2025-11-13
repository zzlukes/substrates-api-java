// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Services;
import io.humainary.substrates.ext.serventis.ext.Services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Services.Dimension.RECEIPT;
import static io.humainary.substrates.ext.serventis.ext.Services.Dimension.RELEASE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/// The test class for the [Service] interface.
///
/// @author William David Louth
/// @since 1.0
final class ServicesTest {

  private static final Cortex               cortex = cortex ();
  private static final Name                 NAME   = cortex.name ( "service.1" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Service              service;

  private void assertSignal (
    final Signal signal
  ) {

    circuit
      .await ();

    assertEquals (
      1L, reservoir
        .drain ()
        .filter ( capture -> capture.emission ().equals ( signal ) )
        .filter ( capture -> capture.subject ().name () == NAME )
        .count ()
    );

  }


  @BeforeEach
  void setup () {

    circuit =
      cortex ().circuit ();

    final var conduit =
      circuit.conduit (
        Services::composer
      );

    service =
      conduit.percept (
        NAME
      );

    reservoir =
      cortex ().reservoir (
        conduit
      );

  }

  // Individual tests for each convenience method

  @Test
  void testCall () {

    service.call ();

    assertSignal (
      new Signal ( Sign.CALL, RELEASE )
    );

  }

  @Test
  void testCalled () {

    service.called ();

    assertSignal (
      new Signal ( Sign.CALL, RECEIPT )
    );

  }

  @Test
  void testDelay () {

    service.delay ();

    assertSignal (
      new Signal ( Sign.DELAY, RELEASE )
    );

  }

  @Test
  void testDelayed () {

    service.delayed ();

    assertSignal (
      new Signal ( Sign.DELAY, RECEIPT )
    );

  }

  /// Tests that [Dimension] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Dimension] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testDimensionEnumOrdinals () {

    assertEquals ( 0, Dimension.RELEASE.ordinal () );
    assertEquals ( 1, Dimension.RECEIPT.ordinal () );

  }

  @Test
  void testDiscard () {

    service.discard ();

    assertSignal (
      new Signal ( Sign.DISCARD, RELEASE )
    );

  }

  @Test
  void testDiscarded () {

    service.discarded ();

    assertSignal (
      new Signal ( Sign.DISCARD, RECEIPT )
    );

  }

  @Test
  void testDisconnect () {

    service.disconnect ();

    assertSignal (
      new Signal ( Sign.DISCONNECT, RELEASE )
    );

  }

  @Test
  void testDisconnected () {

    service.disconnected ();

    assertSignal (
      new Signal ( Sign.DISCONNECT, RECEIPT )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testDispatchFunctionNull () {

    assertThrows (
      NullPointerException.class,
      () -> service.dispatch ( (Fn < String, java.lang.Exception >) null )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testDispatchOperationNull () {

    assertThrows (
      NullPointerException.class,
      () -> service.dispatch ( (Op < java.lang.Exception >) null )
    );

  }

  @Test
  void testDispatchWithFunctionFailure () {

    assertThrows (
      RuntimeException.class,
      () -> service.dispatch (
        () -> {
          throw new RuntimeException ( "test" );
        }
      )
    );

    circuit
      .await ();

    final var emitted = reservoir.drain ().toList ();

    assertEquals ( 2, emitted.size () );
    assertEquals ( new Signal ( Sign.CALL, RELEASE ), emitted.get ( 0 ).emission () );
    assertEquals ( new Signal ( Sign.FAIL, RELEASE ), emitted.get ( 1 ).emission () );

  }

  @Test
  void testDispatchWithFunctionSuccess () {

    final var result = service.dispatch ( () -> "dispatched" );

    assertEquals ( "dispatched", result );

    circuit
      .await ();

    final var emitted = reservoir.drain ().toList ();

    assertEquals ( 2, emitted.size () );
    assertEquals ( new Signal ( Sign.CALL, RELEASE ), emitted.get ( 0 ).emission () );
    assertEquals ( new Signal ( Sign.SUCCESS, RELEASE ), emitted.get ( 1 ).emission () );

  }

  @Test
  void testDispatchWithOperationFailure () {

    assertThrows (
      RuntimeException.class,
      () -> service.dispatch (
        () -> {
          throw new RuntimeException ( "test" );
        }
      )
    );

    circuit
      .await ();

    final var emitted = reservoir.drain ().toList ();

    assertEquals ( 2, emitted.size () );
    assertEquals ( new Signal ( Sign.CALL, RELEASE ), emitted.get ( 0 ).emission () );
    assertEquals ( new Signal ( Sign.FAIL, RELEASE ), emitted.get ( 1 ).emission () );

  }

  @Test
  void testDispatchWithOperationSuccess () {

    service.dispatch ( () -> {
    } );

    circuit
      .await ();

    final var emitted = reservoir.drain ().toList ();

    assertEquals ( 2, emitted.size () );
    assertEquals ( new Signal ( Sign.CALL, RELEASE ), emitted.get ( 0 ).emission () );
    assertEquals ( new Signal ( Sign.SUCCESS, RELEASE ), emitted.get ( 1 ).emission () );

  }

  @Test
  void testExecuteFunctionNull () {

    assertThrows (
      NullPointerException.class,
      () -> service.execute ( (Fn < String, java.lang.Exception >) null )
    );

  }

  @Test
  void testExecuteOperationNull () {

    assertThrows (
      NullPointerException.class,
      () -> service.execute ( (Op < java.lang.Exception >) null )
    );

  }

  @Test
  void testExecuteWithFunctionFailure () {

    assertThrows (
      RuntimeException.class,
      () -> service.execute (
        () -> {
          throw new RuntimeException ( "test" );
        }
      )
    );

    circuit
      .await ();

    final var emitted = reservoir.drain ().toList ();

    assertEquals ( 3, emitted.size () );
    assertEquals ( new Signal ( Sign.START, RELEASE ), emitted.get ( 0 ).emission () );
    assertEquals ( new Signal ( Sign.FAIL, RELEASE ), emitted.get ( 1 ).emission () );
    assertEquals ( new Signal ( Sign.STOP, RELEASE ), emitted.get ( 2 ).emission () );

  }

  @Test
  void testExecuteWithFunctionSuccess () {

    final var result = service.execute ( () -> "success" );

    assertEquals ( "success", result );

    circuit
      .await ();

    final var emitted = reservoir.drain ().toList ();

    assertEquals ( 3, emitted.size () );
    assertEquals ( new Signal ( Sign.START, RELEASE ), emitted.get ( 0 ).emission () );
    assertEquals ( new Signal ( Sign.SUCCESS, RELEASE ), emitted.get ( 1 ).emission () );
    assertEquals ( new Signal ( Sign.STOP, RELEASE ), emitted.get ( 2 ).emission () );

  }

  @Test
  void testExecuteWithOperationFailure () {

    assertThrows (
      RuntimeException.class,
      () -> service.execute (
        () -> {
          throw new RuntimeException ( "test" );
        }
      )
    );

    circuit
      .await ();

    final var emitted = reservoir.drain ().toList ();

    assertEquals ( 3, emitted.size () );
    assertEquals ( new Signal ( Sign.START, RELEASE ), emitted.get ( 0 ).emission () );
    assertEquals ( new Signal ( Sign.FAIL, RELEASE ), emitted.get ( 1 ).emission () );
    assertEquals ( new Signal ( Sign.STOP, RELEASE ), emitted.get ( 2 ).emission () );

  }

  @Test
  void testExecuteWithOperationSuccess () {

    service.execute ( () -> {
    } );

    circuit
      .await ();

    final var emitted = reservoir.drain ().toList ();

    assertEquals ( 3, emitted.size () );
    assertEquals ( new Signal ( Sign.START, RELEASE ), emitted.get ( 0 ).emission () );
    assertEquals ( new Signal ( Sign.SUCCESS, RELEASE ), emitted.get ( 1 ).emission () );
    assertEquals ( new Signal ( Sign.STOP, RELEASE ), emitted.get ( 2 ).emission () );

  }

  @Test
  void testExpire () {

    service.expire ();

    assertSignal (
      new Signal ( Sign.EXPIRE, RELEASE )
    );

  }

  @Test
  void testExpired () {

    service.expired ();

    assertSignal (
      new Signal ( Sign.EXPIRE, RECEIPT )
    );

  }

  @Test
  void testFail () {

    service.fail ();

    assertSignal (
      new Signal ( Sign.FAIL, RELEASE )
    );

  }

  @Test
  void testFailed () {

    service.failed ();

    assertSignal (
      new Signal ( Sign.FAIL, RECEIPT )
    );

  }

  @Test
  void testFnOfCasting () {

    final Fn < String, RuntimeException > fn = () -> "test";

    final var casted = Fn.of ( fn );

    assertEquals ( fn, casted );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testFnOfNull () {

    assertThrows (
      NullPointerException.class,
      () -> Fn.of ( null )
    );

  }

  @Test
  void testMultipleEmissions () {

    service.start ();
    service.call ();
    service.success ();
    service.stop ();

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
  void testOpFromFn () throws java.lang.Exception {

    final Fn < String, java.lang.Exception > fn = () -> "result";

    final Op < java.lang.Exception > op = Op.of ( fn );

    op.exec ();

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testOpFromFnNull () {

    assertThrows (
      NullPointerException.class,
      () -> Op.of ( (Fn < String, java.lang.Exception >) null )
    );

  }

  @Test
  void testOpOfCasting () {

    final Op < RuntimeException > operation = () -> {
    };

    final var casted = Op.of ( operation );

    assertEquals ( operation, casted );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testOpOfNull () {

    assertThrows (
      NullPointerException.class,
      () -> Op.of ( (Op < java.lang.Exception >) null )
    );

  }

  @Test
  void testRecourse () {

    service.recourse ();

    assertSignal (
      new Signal ( Sign.RECOURSE, RELEASE )
    );

  }

  @Test
  void testRecoursed () {

    service.recoursed ();

    assertSignal (
      new Signal ( Sign.RECOURSE, RECEIPT )
    );

  }

  @Test
  void testRedirect () {

    service.redirect ();

    assertSignal (
      new Signal ( Sign.REDIRECT, RELEASE )
    );

  }

  @Test
  void testRedirected () {

    service.redirected ();

    assertSignal (
      new Signal ( Sign.REDIRECT, RECEIPT )
    );

  }

  // Test all signals via emit

  @Test
  void testReject () {

    service.reject ();

    assertSignal (
      new Signal ( Sign.REJECT, RELEASE )
    );

  }

  // Test execute with function

  @Test
  void testRejected () {

    service.rejected ();

    assertSignal (
      new Signal ( Sign.REJECT, RECEIPT )
    );

  }

  @Test
  void testResume () {

    service.resume ();

    assertSignal (
      new Signal ( Sign.RESUME, RELEASE )
    );

  }

  @Test
  void testResumed () {

    service.resumed ();

    assertSignal (
      new Signal ( Sign.RESUME, RECEIPT )
    );

  }

  @Test
  void testRetried () {

    service.retried ();

    assertSignal (
      new Signal ( Sign.RETRY, RECEIPT )
    );

  }

  // Test dispatch with function

  @Test
  void testRetry () {

    service.retry ();

    assertSignal (
      new Signal ( Sign.RETRY, RELEASE )
    );

  }

  @Test
  void testSchedule () {

    service.schedule ();

    assertSignal (
      new Signal ( Sign.SCHEDULE, RELEASE )
    );

  }

  @Test
  void testScheduled () {

    service.scheduled ();

    assertSignal (
      new Signal ( Sign.SCHEDULE, RECEIPT )
    );

  }

  /// Tests that [Sign] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Sign] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, Sign.START.ordinal () );
    assertEquals ( 1, Sign.STOP.ordinal () );
    assertEquals ( 2, Sign.CALL.ordinal () );
    assertEquals ( 3, Sign.SUCCESS.ordinal () );
    assertEquals ( 4, Sign.FAIL.ordinal () );
    assertEquals ( 5, Sign.RECOURSE.ordinal () );
    assertEquals ( 6, Sign.REDIRECT.ordinal () );
    assertEquals ( 7, Sign.EXPIRE.ordinal () );
    assertEquals ( 8, Sign.RETRY.ordinal () );
    assertEquals ( 9, Sign.REJECT.ordinal () );
    assertEquals ( 10, Sign.DISCARD.ordinal () );
    assertEquals ( 11, Sign.DELAY.ordinal () );
    assertEquals ( 12, Sign.SCHEDULE.ordinal () );
    assertEquals ( 13, Sign.SUSPEND.ordinal () );
    assertEquals ( 14, Sign.RESUME.ordinal () );
    assertEquals ( 15, Sign.DISCONNECT.ordinal () );

  }

  @Test
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    service.signal ( Sign.START, RELEASE );
    service.signal ( Sign.START, RECEIPT );
    service.signal ( Sign.STOP, RELEASE );
    service.signal ( Sign.STOP, RECEIPT );
    service.signal ( Sign.CALL, RELEASE );
    service.signal ( Sign.CALL, RECEIPT );
    service.signal ( Sign.SUCCESS, RELEASE );
    service.signal ( Sign.SUCCESS, RECEIPT );
    service.signal ( Sign.FAIL, RELEASE );
    service.signal ( Sign.FAIL, RECEIPT );
    service.signal ( Sign.RECOURSE, RELEASE );
    service.signal ( Sign.RECOURSE, RECEIPT );
    service.signal ( Sign.REDIRECT, RELEASE );
    service.signal ( Sign.REDIRECT, RECEIPT );
    service.signal ( Sign.EXPIRE, RELEASE );
    service.signal ( Sign.EXPIRE, RECEIPT );
    service.signal ( Sign.RETRY, RELEASE );
    service.signal ( Sign.RETRY, RECEIPT );
    service.signal ( Sign.REJECT, RELEASE );
    service.signal ( Sign.REJECT, RECEIPT );
    service.signal ( Sign.DISCARD, RELEASE );
    service.signal ( Sign.DISCARD, RECEIPT );
    service.signal ( Sign.DELAY, RELEASE );
    service.signal ( Sign.DELAY, RECEIPT );
    service.signal ( Sign.SCHEDULE, RELEASE );
    service.signal ( Sign.SCHEDULE, RECEIPT );
    service.signal ( Sign.SUSPEND, RELEASE );
    service.signal ( Sign.SUSPEND, RECEIPT );
    service.signal ( Sign.RESUME, RELEASE );
    service.signal ( Sign.RESUME, RECEIPT );
    service.signal ( Sign.DISCONNECT, RELEASE );
    service.signal ( Sign.DISCONNECT, RECEIPT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 32, signals.size () );
    assertEquals ( new Signal ( Sign.START, RELEASE ), signals.get ( 0 ) );
    assertEquals ( new Signal ( Sign.START, RECEIPT ), signals.get ( 1 ) );
    assertEquals ( new Signal ( Sign.STOP, RELEASE ), signals.get ( 2 ) );
    assertEquals ( new Signal ( Sign.STOP, RECEIPT ), signals.get ( 3 ) );
    assertEquals ( new Signal ( Sign.CALL, RELEASE ), signals.get ( 4 ) );
    assertEquals ( new Signal ( Sign.CALL, RECEIPT ), signals.get ( 5 ) );
    assertEquals ( new Signal ( Sign.SUCCESS, RELEASE ), signals.get ( 6 ) );
    assertEquals ( new Signal ( Sign.SUCCESS, RECEIPT ), signals.get ( 7 ) );
    assertEquals ( new Signal ( Sign.FAIL, RELEASE ), signals.get ( 8 ) );
    assertEquals ( new Signal ( Sign.FAIL, RECEIPT ), signals.get ( 9 ) );
    assertEquals ( new Signal ( Sign.RECOURSE, RELEASE ), signals.get ( 10 ) );
    assertEquals ( new Signal ( Sign.RECOURSE, RECEIPT ), signals.get ( 11 ) );
    assertEquals ( new Signal ( Sign.REDIRECT, RELEASE ), signals.get ( 12 ) );
    assertEquals ( new Signal ( Sign.REDIRECT, RECEIPT ), signals.get ( 13 ) );
    assertEquals ( new Signal ( Sign.EXPIRE, RELEASE ), signals.get ( 14 ) );
    assertEquals ( new Signal ( Sign.EXPIRE, RECEIPT ), signals.get ( 15 ) );
    assertEquals ( new Signal ( Sign.RETRY, RELEASE ), signals.get ( 16 ) );
    assertEquals ( new Signal ( Sign.RETRY, RECEIPT ), signals.get ( 17 ) );
    assertEquals ( new Signal ( Sign.REJECT, RELEASE ), signals.get ( 18 ) );
    assertEquals ( new Signal ( Sign.REJECT, RECEIPT ), signals.get ( 19 ) );
    assertEquals ( new Signal ( Sign.DISCARD, RELEASE ), signals.get ( 20 ) );
    assertEquals ( new Signal ( Sign.DISCARD, RECEIPT ), signals.get ( 21 ) );
    assertEquals ( new Signal ( Sign.DELAY, RELEASE ), signals.get ( 22 ) );
    assertEquals ( new Signal ( Sign.DELAY, RECEIPT ), signals.get ( 23 ) );
    assertEquals ( new Signal ( Sign.SCHEDULE, RELEASE ), signals.get ( 24 ) );
    assertEquals ( new Signal ( Sign.SCHEDULE, RECEIPT ), signals.get ( 25 ) );
    assertEquals ( new Signal ( Sign.SUSPEND, RELEASE ), signals.get ( 26 ) );
    assertEquals ( new Signal ( Sign.SUSPEND, RECEIPT ), signals.get ( 27 ) );
    assertEquals ( new Signal ( Sign.RESUME, RELEASE ), signals.get ( 28 ) );
    assertEquals ( new Signal ( Sign.RESUME, RECEIPT ), signals.get ( 29 ) );
    assertEquals ( new Signal ( Sign.DISCONNECT, RELEASE ), signals.get ( 30 ) );
    assertEquals ( new Signal ( Sign.DISCONNECT, RECEIPT ), signals.get ( 31 ) );

  }

  // Multiple emissions test

  @Test
  void testSignalAccessors () {

    final var START = new Signal ( Sign.START, RELEASE );
    final var STARTED = new Signal ( Sign.START, RECEIPT );
    final var SUCCESS = new Signal ( Sign.SUCCESS, RELEASE );
    final var SUCCEEDED = new Signal ( Sign.SUCCESS, RECEIPT );

    assertEquals ( Sign.START, START.sign () );
    assertEquals ( Dimension.RELEASE, START.dimension () );

    assertEquals ( Sign.START, STARTED.sign () );
    assertEquals ( Dimension.RECEIPT, STARTED.dimension () );

    assertEquals ( Sign.SUCCESS, SUCCESS.sign () );
    assertEquals ( Dimension.RELEASE, SUCCESS.dimension () );

    assertEquals ( Sign.SUCCESS, SUCCEEDED.sign () );
    assertEquals ( Dimension.RECEIPT, SUCCEEDED.dimension () );

  }

  // Subject association test

  /// Tests that [Sign] and [Dimension] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Sign] and [Dimension] enum constants
  /// do not change, which is critical for serialization and external integrations.
  /// Signal is now a record composed of Sign and Dimension.

  @Test
  void testSignalComponentOrdinals () {

    // Verify Sign ordinals remain stable
    assertEquals ( 0, Sign.START.ordinal () );
    assertEquals ( 1, Sign.STOP.ordinal () );
    assertEquals ( 2, Sign.CALL.ordinal () );
    assertEquals ( 3, Sign.SUCCESS.ordinal () );
    assertEquals ( 4, Sign.FAIL.ordinal () );
    assertEquals ( 5, Sign.RECOURSE.ordinal () );
    assertEquals ( 6, Sign.REDIRECT.ordinal () );
    assertEquals ( 7, Sign.EXPIRE.ordinal () );
    assertEquals ( 8, Sign.RETRY.ordinal () );
    assertEquals ( 9, Sign.REJECT.ordinal () );
    assertEquals ( 10, Sign.DISCARD.ordinal () );
    assertEquals ( 11, Sign.DELAY.ordinal () );
    assertEquals ( 12, Sign.SCHEDULE.ordinal () );
    assertEquals ( 13, Sign.SUSPEND.ordinal () );
    assertEquals ( 14, Sign.RESUME.ordinal () );
    assertEquals ( 15, Sign.DISCONNECT.ordinal () );

    // Verify Dimension ordinals remain stable
    assertEquals ( 0, Dimension.RELEASE.ordinal () );
    assertEquals ( 1, Dimension.RECEIPT.ordinal () );

    // Verify Signal composition works correctly
    final var signal = new Signal ( Sign.START, Dimension.RELEASE );
    assertEquals ( Sign.START, signal.sign () );
    assertEquals ( Dimension.RELEASE, signal.dimension () );

  }

  @Test
  void testSignalDimensionMappings () {

    // Verify all Sign×Dimension combinations can be constructed
    for ( final var sign : Sign.values () ) {

      for ( final var dimension : Dimension.values () ) {

        final var signal = new Signal ( sign, dimension );

        assertEquals (
          sign,
          signal.sign ()
        );

        assertEquals (
          dimension,
          signal.dimension ()
        );

      }

    }

    // Verify both dimensions exist
    assertEquals ( 2, Dimension.values ().length );
    assertEquals ( Dimension.RELEASE, Dimension.values ()[0] );
    assertEquals ( Dimension.RECEIPT, Dimension.values ()[1] );

  }

  @Test
  void testStart () {

    service.start ();

    assertSignal (
      new Signal ( Sign.START, RELEASE )
    );

  }

  @Test
  void testStarted () {

    service.started ();

    assertSignal (
      new Signal ( Sign.START, RECEIPT )
    );

  }

  @Test
  void testStop () {

    service.stop ();

    assertSignal (
      new Signal ( Sign.STOP, RELEASE )
    );

  }

  @Test
  void testStopped () {

    service.stopped ();

    assertSignal (
      new Signal ( Sign.STOP, RECEIPT )
    );

  }

  @Test
  void testSubjectAssociation () {

    service.start ();

    circuit
      .await ();

    assertEquals (
      NAME,
      reservoir
        .drain ()
        .map ( Capture::subject )
        .map ( Subject::name )
        .findFirst ()
        .orElseThrow ()
    );

  }

  @Test
  void testSucceeded () {

    service.succeeded ();

    assertSignal (
      new Signal ( Sign.SUCCESS, RECEIPT )
    );

  }

  @Test
  void testSuccess () {

    service.success ();

    assertSignal (
      new Signal ( Sign.SUCCESS, RELEASE )
    );

  }

  @Test
  void testSuspend () {

    service.suspend ();

    assertSignal (
      new Signal ( Sign.SUSPEND, RELEASE )
    );

  }

  @Test
  void testSuspended () {

    service.suspended ();

    assertSignal (
      new Signal ( Sign.SUSPEND, RECEIPT )
    );

  }

}
