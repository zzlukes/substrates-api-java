// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.ext.Services;
import io.humainary.substrates.ext.serventis.ext.Services.Service;
import io.humainary.substrates.ext.serventis.ext.Services.Signal;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Services.Service operations.
/// <p>
/// Measures performance of service creation and signal emissions for service
/// lifecycle operations from both RELEASE (self-perspective) and RECEIPT
/// (observed-perspective) dimensions, including START, STOP, CALL, SUCCESS,
/// FAIL, RETRY, DELAY, SCHEDULE, SUSPEND, RESUME, REJECT, DISCARD, and more.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ServiceOps implements Substrates {

  private static final String SERVICE_NAME = "order.processor";
  private static final int    BATCH_SIZE   = 1000;

  private Cortex                      cortex;
  private Circuit                     circuit;
  private Conduit < Service, Signal > conduit;
  private Service                     service;
  private Name                        name;

  ///
  /// Benchmark emitting a CALL signal (RELEASE).
  ///

  @Benchmark
  public void emit_call () {

    service.call ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_call_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.call ();
  }

  ///
  /// Benchmark emitting a CALLED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_called () {

    service.called ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_called_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.called ();
  }

  ///
  /// Benchmark emitting a DELAY signal (RELEASE).
  ///

  @Benchmark
  public void emit_delay () {

    service.delay ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_delay_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.delay ();
  }

  ///
  /// Benchmark emitting a DELAYED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_delayed () {

    service.delayed ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_delayed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.delayed ();
  }

  ///
  /// Benchmark emitting a DISCARD signal (RELEASE).
  ///

  @Benchmark
  public void emit_discard () {

    service.discard ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_discard_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.discard ();
  }

  ///
  /// Benchmark emitting a DISCARDED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_discarded () {

    service.discarded ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_discarded_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.discarded ();
  }

  ///
  /// Benchmark emitting a DISCONNECT signal (RELEASE).
  ///

  @Benchmark
  public void emit_disconnect () {

    service.disconnect ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_disconnect_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.disconnect ();
  }

  ///
  /// Benchmark emitting a DISCONNECTED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_disconnected () {

    service.disconnected ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_disconnected_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.disconnected ();
  }

  ///
  /// Benchmark emitting an EXPIRE signal (RELEASE).
  ///

  @Benchmark
  public void emit_expire () {

    service.expire ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expire_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.expire ();
  }

  ///
  /// Benchmark emitting an EXPIRED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_expired () {

    service.expired ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expired_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.expired ();
  }

  ///
  /// Benchmark emitting a FAIL signal (RELEASE).
  ///

  @Benchmark
  public void emit_fail () {

    service.fail ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.fail ();
  }

  ///
  /// Benchmark emitting a FAILED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_failed () {

    service.failed ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_failed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.failed ();
  }

  ///
  /// Benchmark emitting a RECOURSE signal (RELEASE).
  ///

  @Benchmark
  public void emit_recourse () {

    service.recourse ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_recourse_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.recourse ();
  }

  ///
  /// Benchmark emitting a RECOURSED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_recoursed () {

    service.recoursed ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_recoursed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.recoursed ();
  }

  ///
  /// Benchmark emitting a REDIRECT signal (RELEASE).
  ///

  @Benchmark
  public void emit_redirect () {

    service.redirect ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_redirect_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.redirect ();
  }

  ///
  /// Benchmark emitting a REDIRECTED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_redirected () {

    service.redirected ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_redirected_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.redirected ();
  }

  ///
  /// Benchmark emitting a REJECT signal (RELEASE).
  ///

  @Benchmark
  public void emit_reject () {

    service.reject ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_reject_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.reject ();
  }

  ///
  /// Benchmark emitting a REJECTED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_rejected () {

    service.rejected ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_rejected_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.rejected ();
  }

  ///
  /// Benchmark emitting a RESUME signal (RELEASE).
  ///

  @Benchmark
  public void emit_resume () {

    service.resume ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_resume_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.resume ();
  }

  ///
  /// Benchmark emitting a RESUMED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_resumed () {

    service.resumed ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_resumed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.resumed ();
  }

  ///
  /// Benchmark emitting a RETRIED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_retried () {

    service.retried ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_retried_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.retried ();
  }

  ///
  /// Benchmark emitting a RETRY signal (RELEASE).
  ///

  @Benchmark
  public void emit_retry () {

    service.retry ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_retry_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.retry ();
  }

  ///
  /// Benchmark emitting a SCHEDULE signal (RELEASE).
  ///

  @Benchmark
  public void emit_schedule () {

    service.schedule ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_schedule_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.schedule ();
  }

  ///
  /// Benchmark emitting a SCHEDULED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_scheduled () {

    service.scheduled ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_scheduled_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.scheduled ();
  }

  ///
  /// Benchmark emitting a START signal (RELEASE).
  ///

  @Benchmark
  public void emit_start () {

    service.start ();

  }


  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_start_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.start ();
  }

  ///
  /// Benchmark emitting a STARTED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_started () {

    service.started ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_started_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.started ();
  }

  ///
  /// Benchmark emitting a STOP signal (RELEASE).
  ///

  @Benchmark
  public void emit_stop () {

    service.stop ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_stop_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.stop ();
  }

  ///
  /// Benchmark emitting a STOPPED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_stopped () {

    service.stopped ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_stopped_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.stopped ();
  }

  ///
  /// Benchmark emitting a SUCCEEDED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_succeeded () {

    service.succeeded ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_succeeded_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.succeeded ();
  }

  ///
  /// Benchmark emitting a SUCCESS signal (RELEASE).
  ///

  @Benchmark
  public void emit_success () {

    service.success ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_success_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.success ();
  }

  ///
  /// Benchmark emitting a SUSPEND signal (RELEASE).
  ///

  @Benchmark
  public void emit_suspend () {

    service.suspend ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_suspend_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.suspend ();
  }

  ///
  /// Benchmark emitting a SUSPENDED signal (RECEIPT).
  ///

  @Benchmark
  public void emit_suspended () {

    service.suspended ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_suspended_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.suspended ();
  }

  ///
  /// Benchmark service retrieval from conduit.
  ///

  @Benchmark
  public Service service_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Service service_from_conduit_batch () {
    Service result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    service.signal (
      Services.Sign.START,
      Services.Dimension.RELEASE
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
      service.signal (
        Services.Sign.START,
        Services.Dimension.RELEASE
      );
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Services::composer
      );

    service =
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
        SERVICE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
