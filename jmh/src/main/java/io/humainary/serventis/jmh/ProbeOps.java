// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.ext.Probes;
import io.humainary.substrates.ext.serventis.ext.Probes.Probe;
import io.humainary.substrates.ext.serventis.ext.Probes.Signal;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

///
/// Benchmark for Probes.Probe operations.
///
/// Measures performance of probe creation and signal emissions for communication
/// operations from both RELEASE (self-perspective) and RECEIPT (observed-perspective)
/// dimensions: CONNECT, DISCONNECT, TRANSMIT, RECEIVE, PROCESS, SUCCEED, and FAIL.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( Mode.AverageTime )
@OutputTimeUnit ( TimeUnit.NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ProbeOps implements Substrates {

  private static final String PROBE_NAME = "rpc.client";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                    cortex;
  private Circuit                   circuit;
  private Conduit < Probe, Signal > conduit;
  private Probe                     probe;
  private Name                      name;

  ///
  /// Benchmark emitting a CONNECT signal (RELEASE).
  ///

  @Benchmark
  public void emit_connect () {

    probe.connect ();

  }


  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_connect_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.connect ();
  }

  ///
  /// Benchmark emitting a CONNECTED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_connected () {

    probe.connected ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_connected_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.connected ();
  }

  ///
  /// Benchmark emitting a DISCONNECT signal (RELEASE).
  ///

  @Benchmark
  public void emit_disconnect () {

    probe.disconnect ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_disconnect_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.disconnect ();
  }

  ///
  /// Benchmark emitting a DISCONNECTED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_disconnected () {

    probe.disconnected ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_disconnected_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.disconnected ();
  }

  ///
  /// Benchmark emitting a FAIL signal (RELEASE).
  ///

  @Benchmark
  public void emit_fail () {

    probe.fail ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.fail ();
  }

  ///
  /// Benchmark emitting a FAILED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_failed () {

    probe.failed ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_failed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.failed ();
  }

  ///
  /// Benchmark emitting a PROCESS signal (RELEASE).
  ///

  @Benchmark
  public void emit_process () {

    probe.process ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_process_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.process ();
  }

  ///
  /// Benchmark emitting a PROCESSED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_processed () {

    probe.processed ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_processed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.processed ();
  }

  ///
  /// Benchmark emitting a RECEIVE signal (RELEASE).
  ///

  @Benchmark
  public void emit_receive () {

    probe.receive ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_receive_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.receive ();
  }

  ///
  /// Benchmark emitting a RECEIVED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_received () {

    probe.received ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_received_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.received ();
  }

  ///
  /// Benchmark emitting a SUCCEED signal (RELEASE).
  ///

  @Benchmark
  public void emit_succeed () {

    probe.succeed ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_succeed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.succeed ();
  }

  ///
  /// Benchmark emitting a SUCCEEDED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_succeeded () {

    probe.succeeded ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_succeeded_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.succeeded ();
  }

  ///
  /// Benchmark emitting a TRANSMIT signal (RELEASE).
  ///

  @Benchmark
  public void emit_transmit () {

    probe.transmit ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_transmit_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.transmit ();
  }

  ///
  /// Benchmark emitting a TRANSMITTED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_transmitted () {

    probe.transmitted ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_transmitted_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.transmitted ();
  }

  ///
  /// Benchmark probe retrieval from conduit.
  ///

  @Benchmark
  public Probe probe_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Probe probe_from_conduit_batch () {
    Probe result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    probe.signal (
      Probes.Sign.CONNECT,
      Probes.Dimension.RELEASE
    );

  }

  ///
  /// Benchmark batched generic signal emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_signal_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      probe.signal (
        Probes.Sign.CONNECT,
        Probes.Dimension.RELEASE
      );
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Probes::composer
      );

    probe =
      conduit.percept (
        name
      );

  }

  @Setup ( Level.Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        PROBE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
