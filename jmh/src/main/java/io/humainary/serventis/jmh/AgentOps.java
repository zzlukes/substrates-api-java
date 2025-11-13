// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.ext.Agents;
import io.humainary.substrates.ext.serventis.ext.Agents.Agent;
import io.humainary.substrates.ext.serventis.ext.Agents.Signal;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Agents.Agent operations.
///
/// Measures performance of agent creation and signal emissions for Promise Theory
/// coordination from both PROMISER (self-perspective) and PROMISEE (observed-perspective)
/// dimensions, including OFFER, PROMISE, ACCEPT, FULFILL, BREACH, RETRACT, INQUIRE,
/// OBSERVE, DEPEND, and VALIDATE.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class AgentOps
  implements Substrates {

  private static final String AGENT_NAME = "capacity.monitor";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                    cortex;
  private Circuit                   circuit;
  private Conduit < Agent, Signal > conduit;
  private Agent                     agent;
  private Name                      name;

  ///
  /// Benchmark agent retrieval from conduit.
  ///

  @Benchmark
  public Agent agent_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Agent agent_from_conduit_batch () {
    Agent result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  ///
  /// Benchmark emitting an ACCEPT signal (PROMISER).
  ///

  @Benchmark
  public void emit_accept () {

    agent.accept ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_accept_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.accept ();
  }

  ///
  /// Benchmark emitting an ACCEPTED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_accepted () {

    agent.accepted ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_accepted_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.accepted ();
  }

  ///
  /// Benchmark emitting a BREACH signal (PROMISER).
  ///

  @Benchmark
  public void emit_breach () {

    agent.breach ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_breach_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.breach ();
  }

  ///
  /// Benchmark emitting a BREACHED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_breached () {

    agent.breached ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_breached_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.breached ();
  }

  ///
  /// Benchmark emitting a DEPEND signal (PROMISER).
  ///

  @Benchmark
  public void emit_depend () {

    agent.depend ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_depend_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.depend ();
  }

  ///
  /// Benchmark emitting a DEPENDED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_depended () {

    agent.depended ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_depended_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.depended ();
  }

  ///
  /// Benchmark emitting a FULFILL signal (PROMISER).
  ///

  @Benchmark
  public void emit_fulfill () {

    agent.fulfill ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fulfill_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.fulfill ();
  }

  ///
  /// Benchmark emitting a FULFILLED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_fulfilled () {

    agent.fulfilled ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fulfilled_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.fulfilled ();
  }

  ///
  /// Benchmark emitting an INQUIRE signal (PROMISER).
  ///

  @Benchmark
  public void emit_inquire () {

    agent.inquire ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_inquire_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.inquire ();
  }

  ///
  /// Benchmark emitting an INQUIRED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_inquired () {

    agent.inquired ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_inquired_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.inquired ();
  }

  ///
  /// Benchmark emitting an OBSERVE signal (PROMISER).
  ///

  @Benchmark
  public void emit_observe () {

    agent.observe ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_observe_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.observe ();
  }

  ///
  /// Benchmark emitting an OBSERVED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_observed () {

    agent.observed ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_observed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.observed ();
  }

  ///
  /// Benchmark emitting an OFFER signal (PROMISER).
  ///

  @Benchmark
  public void emit_offer () {

    agent.offer ();

  }


  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_offer_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.offer ();
  }

  ///
  /// Benchmark emitting an OFFERED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_offered () {

    agent.offered ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_offered_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.offered ();
  }

  ///
  /// Benchmark emitting a PROMISE signal (PROMISER).
  ///

  @Benchmark
  public void emit_promise () {

    agent.promise ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_promise_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.promise ();
  }

  ///
  /// Benchmark emitting a PROMISED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_promised () {

    agent.promised ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_promised_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.promised ();
  }

  ///
  /// Benchmark emitting a RETRACT signal (PROMISER).
  ///

  @Benchmark
  public void emit_retract () {

    agent.retract ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_retract_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.retract ();
  }

  ///
  /// Benchmark emitting a RETRACTED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_retracted () {

    agent.retracted ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_retracted_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.retracted ();
  }

  ///
  /// Benchmark emitting a VALIDATE signal (PROMISER).
  ///

  @Benchmark
  public void emit_validate () {

    agent.validate ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_validate_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.validate ();
  }

  ///
  /// Benchmark emitting a VALIDATED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_validated () {

    agent.validated ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_validated_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.validated ();
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    agent.signal (
      Agents.Sign.PROMISE,
      Agents.Dimension.PROMISER
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
      agent.signal (
        Agents.Sign.PROMISE,
        Agents.Dimension.PROMISER
      );
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Agents::composer
      );

    agent =
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
        AGENT_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
