# Substrates JMH Benchmarks

This document contains JMH benchmark results and documentation for the Substrates framework.

## Overview

The Substrates framework provides comprehensive JMH benchmarks measuring performance across all core
components. Benchmarks are organized into three categories:

1. **Core Substrates Benchmarks** - Framework primitives (Circuit, Conduit, Pipe, Flow, Name, State,
   Scope, Reservoir)
2. **Serventis Extension Benchmarks** - Observability instruments (Counter, Monitor, Gauge, Queue,
   Cache, Resource, Actor, Agent, Service, Router, Probe, Reporter, Transaction)
3. **Specialized Patterns** - Hot-path benchmarks and batched operations

## Running Benchmarks

```bash
# Run all benchmarks
substrates/jmh.sh

# List available benchmarks
substrates/jmh.sh -l

# Run specific benchmark
substrates/jmh.sh PipeOps.emit_to_empty_pipe

# Run benchmarks matching pattern
substrates/jmh.sh ".*batch"

# Custom JMH parameters
substrates/jmh.sh -wi 5 -i 10 -f 2
```

## Benchmark Categories

### Hot-Path Benchmarks

Hot-path benchmarks isolate operation costs from lifecycle overhead by using
`@Setup(Level.Iteration)` to reuse resources across invocations. These measure the performance of
already-running circuits without amortizing creation/teardown costs.

**Example**: `CircuitOps.hot_conduit_create()` measures conduit creation on an already-running
circuit.

### Batched Benchmarks

Batched benchmarks use `@OperationsPerInvocation(BATCH_SIZE)` to measure amortized per-operation
cost by executing operations in tight loops. This reduces measurement noise for fast operations (<
10ns) by spreading fixed benchmark overhead across many operations.

**Standard batch size**: 1000 operations per invocation

**Example**: `CounterOps.emit_increment_batch()` executes 1000 counter increments per benchmark
invocation.

### Single-Operation Benchmarks

Single-operation benchmarks measure the full cost of individual operations including any
per-invocation overhead. These provide baseline measurements for comparing against batched results.

## Benchmark Results

```

Humainary's (Alpha) SPI: io.humainary.substrates.spi.alpha.Provider 

Model Name: Mac mini
Model Identifier: Mac16,10
Model Number: MU9D3LL/A

Chip: Apple M4
Total Number of Cores: 10 (4 performance and 6 efficiency)
Memory: 16 GB

java version "25.0.1" 2025-10-21 LTS
Java(TM) SE Runtime Environment (build 25.0.1+8-LTS-27)
Java HotSpot(TM) 64-Bit Server VM (build 25.0.1+8-LTS-27, mixed mode, sharing)

Benchmark                                                       Mode  Cnt      Score     Error  Units
i.h.serventis.jmh.ActorOps.actor_from_conduit                            avgt    5      1.783 ±    0.046  ns/op
i.h.serventis.jmh.ActorOps.actor_from_conduit_batch                      avgt    5      1.612 ±    0.012  ns/op
i.h.serventis.jmh.ActorOps.emit_acknowledge                              avgt    5      8.607 ±    1.297  ns/op
i.h.serventis.jmh.ActorOps.emit_acknowledge_batch                        avgt    5      9.336 ±    0.385  ns/op
i.h.serventis.jmh.ActorOps.emit_affirm                                   avgt    5      8.821 ±    0.985  ns/op
i.h.serventis.jmh.ActorOps.emit_affirm_batch                             avgt    5      8.453 ±    0.616  ns/op
i.h.serventis.jmh.ActorOps.emit_ask                                      avgt    5      8.093 ±    0.869  ns/op
i.h.serventis.jmh.ActorOps.emit_ask_batch                                avgt    5      9.185 ±    1.201  ns/op
i.h.serventis.jmh.ActorOps.emit_clarify                                  avgt    5      8.098 ±    0.551  ns/op
i.h.serventis.jmh.ActorOps.emit_clarify_batch                            avgt    5      9.291 ±    0.425  ns/op
i.h.serventis.jmh.ActorOps.emit_command                                  avgt    5      8.275 ±    1.046  ns/op
i.h.serventis.jmh.ActorOps.emit_command_batch                            avgt    5      9.365 ±    0.750  ns/op
i.h.serventis.jmh.ActorOps.emit_deliver                                  avgt    5      8.339 ±    0.653  ns/op
i.h.serventis.jmh.ActorOps.emit_deliver_batch                            avgt    5      9.353 ±    0.490  ns/op
i.h.serventis.jmh.ActorOps.emit_deny                                     avgt    5      8.677 ±    1.677  ns/op
i.h.serventis.jmh.ActorOps.emit_deny_batch                               avgt    5      7.574 ±    1.028  ns/op
i.h.serventis.jmh.ActorOps.emit_explain                                  avgt    5      7.830 ±    0.731  ns/op
i.h.serventis.jmh.ActorOps.emit_explain_batch                            avgt    5      8.226 ±    0.205  ns/op
i.h.serventis.jmh.ActorOps.emit_promise                                  avgt    5      8.622 ±    0.875  ns/op
i.h.serventis.jmh.ActorOps.emit_promise_batch                            avgt    5      8.866 ±    0.646  ns/op
i.h.serventis.jmh.ActorOps.emit_report                                   avgt    5      8.084 ±    1.427  ns/op
i.h.serventis.jmh.ActorOps.emit_report_batch                             avgt    5      8.145 ±    0.503  ns/op
i.h.serventis.jmh.ActorOps.emit_request                                  avgt    5      8.337 ±    0.729  ns/op
i.h.serventis.jmh.ActorOps.emit_request_batch                            avgt    5      8.992 ±    0.774  ns/op
i.h.serventis.jmh.ActorOps.emit_sign                                     avgt    5      8.516 ±    1.063  ns/op
i.h.serventis.jmh.ActorOps.emit_sign_batch                               avgt    5      8.225 ±    0.651  ns/op
i.h.serventis.jmh.AgentOps.agent_from_conduit                            avgt    5      1.869 ±    0.113  ns/op
i.h.serventis.jmh.AgentOps.agent_from_conduit_batch                      avgt    5      1.663 ±    0.012  ns/op
i.h.serventis.jmh.AgentOps.emit_accept                                   avgt    5      9.559 ±    1.367  ns/op
i.h.serventis.jmh.AgentOps.emit_accept_batch                             avgt    5      7.524 ±    0.369  ns/op
i.h.serventis.jmh.AgentOps.emit_accepted                                 avgt    5      8.809 ±    0.509  ns/op
i.h.serventis.jmh.AgentOps.emit_accepted_batch                           avgt    5      9.769 ±    0.285  ns/op
i.h.serventis.jmh.AgentOps.emit_breach                                   avgt    5      8.868 ±    1.305  ns/op
i.h.serventis.jmh.AgentOps.emit_breach_batch                             avgt    5      8.872 ±    0.957  ns/op
i.h.serventis.jmh.AgentOps.emit_breached                                 avgt    5      8.692 ±    0.506  ns/op
i.h.serventis.jmh.AgentOps.emit_breached_batch                           avgt    5      8.795 ±    1.037  ns/op
i.h.serventis.jmh.AgentOps.emit_depend                                   avgt    5      8.136 ±    1.132  ns/op
i.h.serventis.jmh.AgentOps.emit_depend_batch                             avgt    5      9.184 ±    1.555  ns/op
i.h.serventis.jmh.AgentOps.emit_depended                                 avgt    5      8.113 ±    0.813  ns/op
i.h.serventis.jmh.AgentOps.emit_depended_batch                           avgt    5      9.628 ±    0.881  ns/op
i.h.serventis.jmh.AgentOps.emit_fulfill                                  avgt    5      8.108 ±    1.729  ns/op
i.h.serventis.jmh.AgentOps.emit_fulfill_batch                            avgt    5      9.338 ±    0.959  ns/op
i.h.serventis.jmh.AgentOps.emit_fulfilled                                avgt    5      8.179 ±    1.207  ns/op
i.h.serventis.jmh.AgentOps.emit_fulfilled_batch                          avgt    5      8.119 ±    1.243  ns/op
i.h.serventis.jmh.AgentOps.emit_inquire                                  avgt    5      9.766 ±    1.325  ns/op
i.h.serventis.jmh.AgentOps.emit_inquire_batch                            avgt    5      8.795 ±    1.034  ns/op
i.h.serventis.jmh.AgentOps.emit_inquired                                 avgt    5      7.844 ±    0.966  ns/op
i.h.serventis.jmh.AgentOps.emit_inquired_batch                           avgt    5      9.351 ±    1.036  ns/op
i.h.serventis.jmh.AgentOps.emit_observe                                  avgt    5      8.005 ±    0.705  ns/op
i.h.serventis.jmh.AgentOps.emit_observe_batch                            avgt    5      7.352 ±    1.067  ns/op
i.h.serventis.jmh.AgentOps.emit_observed                                 avgt    5      8.455 ±    0.620  ns/op
i.h.serventis.jmh.AgentOps.emit_observed_batch                           avgt    5      8.230 ±    0.490  ns/op
i.h.serventis.jmh.AgentOps.emit_offer                                    avgt    5      8.944 ±    0.738  ns/op
i.h.serventis.jmh.AgentOps.emit_offer_batch                              avgt    5      9.038 ±    0.626  ns/op
i.h.serventis.jmh.AgentOps.emit_offered                                  avgt    5      8.071 ±    0.980  ns/op
i.h.serventis.jmh.AgentOps.emit_offered_batch                            avgt    5      9.303 ±    1.235  ns/op
i.h.serventis.jmh.AgentOps.emit_promise                                  avgt    5      7.376 ±    2.297  ns/op
i.h.serventis.jmh.AgentOps.emit_promise_batch                            avgt    5      9.207 ±    0.862  ns/op
i.h.serventis.jmh.AgentOps.emit_promised                                 avgt    5      8.274 ±    0.304  ns/op
i.h.serventis.jmh.AgentOps.emit_promised_batch                           avgt    5      8.623 ±    0.397  ns/op
i.h.serventis.jmh.AgentOps.emit_retract                                  avgt    5      8.224 ±    0.856  ns/op
i.h.serventis.jmh.AgentOps.emit_retract_batch                            avgt    5      9.629 ±    0.746  ns/op
i.h.serventis.jmh.AgentOps.emit_retracted                                avgt    5      7.878 ±    1.137  ns/op
i.h.serventis.jmh.AgentOps.emit_retracted_batch                          avgt    5      8.541 ±    1.297  ns/op
i.h.serventis.jmh.AgentOps.emit_signal                                   avgt    5      8.684 ±    0.780  ns/op
i.h.serventis.jmh.AgentOps.emit_signal_batch                             avgt    5      9.776 ±    0.745  ns/op
i.h.serventis.jmh.AgentOps.emit_validate                                 avgt    5      7.713 ±    0.727  ns/op
i.h.serventis.jmh.AgentOps.emit_validate_batch                           avgt    5      8.369 ±    0.451  ns/op
i.h.serventis.jmh.AgentOps.emit_validated                                avgt    5      8.857 ±    1.072  ns/op
i.h.serventis.jmh.AgentOps.emit_validated_batch                          avgt    5      8.819 ±    1.066  ns/op
i.h.serventis.jmh.CacheOps.cache_from_conduit                            avgt    5      1.851 ±    0.025  ns/op
i.h.serventis.jmh.CacheOps.cache_from_conduit_batch                      avgt    5      1.661 ±    0.005  ns/op
i.h.serventis.jmh.CacheOps.emit_evict                                    avgt    5      8.244 ±    1.201  ns/op
i.h.serventis.jmh.CacheOps.emit_evict_batch                              avgt    5      8.004 ±    0.712  ns/op
i.h.serventis.jmh.CacheOps.emit_expire                                   avgt    5      8.200 ±    1.654  ns/op
i.h.serventis.jmh.CacheOps.emit_expire_batch                             avgt    5      8.800 ±    0.505  ns/op
i.h.serventis.jmh.CacheOps.emit_hit                                      avgt    5      8.330 ±    0.952  ns/op
i.h.serventis.jmh.CacheOps.emit_hit_batch                                avgt    5      8.810 ±    0.657  ns/op
i.h.serventis.jmh.CacheOps.emit_lookup                                   avgt    5      8.447 ±    1.193  ns/op
i.h.serventis.jmh.CacheOps.emit_lookup_batch                             avgt    5      8.996 ±    0.530  ns/op
i.h.serventis.jmh.CacheOps.emit_miss                                     avgt    5      8.156 ±    0.539  ns/op
i.h.serventis.jmh.CacheOps.emit_miss_batch                               avgt    5      7.693 ±    0.369  ns/op
i.h.serventis.jmh.CacheOps.emit_remove                                   avgt    5      8.129 ±    1.097  ns/op
i.h.serventis.jmh.CacheOps.emit_remove_batch                             avgt    5      9.208 ±    0.914  ns/op
i.h.serventis.jmh.CacheOps.emit_sign                                     avgt    5      8.307 ±    0.910  ns/op
i.h.serventis.jmh.CacheOps.emit_sign_batch                               avgt    5      8.870 ±    0.802  ns/op
i.h.serventis.jmh.CacheOps.emit_store                                    avgt    5      8.052 ±    1.359  ns/op
i.h.serventis.jmh.CacheOps.emit_store_batch                              avgt    5      8.687 ±    1.134  ns/op
i.h.serventis.jmh.CounterOps.counter_from_conduit                        avgt    5      1.865 ±    0.039  ns/op
i.h.serventis.jmh.CounterOps.counter_from_conduit_batch                  avgt    5      1.663 ±    0.013  ns/op
i.h.serventis.jmh.CounterOps.emit_increment                              avgt    5      8.101 ±    1.109  ns/op
i.h.serventis.jmh.CounterOps.emit_increment_batch                        avgt    5      8.610 ±    0.435  ns/op
i.h.serventis.jmh.CounterOps.emit_overflow                               avgt    5      8.074 ±    0.870  ns/op
i.h.serventis.jmh.CounterOps.emit_overflow_batch                         avgt    5      8.970 ±    0.803  ns/op
i.h.serventis.jmh.CounterOps.emit_reset                                  avgt    5      8.239 ±    1.146  ns/op
i.h.serventis.jmh.CounterOps.emit_reset_batch                            avgt    5      7.951 ±    0.431  ns/op
i.h.serventis.jmh.CounterOps.emit_sign                                   avgt    5      8.210 ±    0.677  ns/op
i.h.serventis.jmh.CounterOps.emit_sign_batch                             avgt    5      8.158 ±    1.087  ns/op
i.h.serventis.jmh.GaugeOps.emit_decrement                                avgt    5      7.955 ±    1.322  ns/op
i.h.serventis.jmh.GaugeOps.emit_decrement_batch                          avgt    5      7.801 ±    1.048  ns/op
i.h.serventis.jmh.GaugeOps.emit_increment                                avgt    5      8.104 ±    0.287  ns/op
i.h.serventis.jmh.GaugeOps.emit_increment_batch                          avgt    5      9.063 ±    0.488  ns/op
i.h.serventis.jmh.GaugeOps.emit_overflow                                 avgt    5      8.008 ±    0.984  ns/op
i.h.serventis.jmh.GaugeOps.emit_overflow_batch                           avgt    5      8.645 ±    0.917  ns/op
i.h.serventis.jmh.GaugeOps.emit_reset                                    avgt    5      8.304 ±    1.346  ns/op
i.h.serventis.jmh.GaugeOps.emit_reset_batch                              avgt    5      9.111 ±    0.683  ns/op
i.h.serventis.jmh.GaugeOps.emit_sign                                     avgt    5      8.034 ±    0.361  ns/op
i.h.serventis.jmh.GaugeOps.emit_sign_batch                               avgt    5      8.789 ±    0.741  ns/op
i.h.serventis.jmh.GaugeOps.emit_underflow                                avgt    5      7.962 ±    0.812  ns/op
i.h.serventis.jmh.GaugeOps.emit_underflow_batch                          avgt    5      9.196 ±    0.359  ns/op
i.h.serventis.jmh.GaugeOps.gauge_from_conduit                            avgt    5      1.851 ±    0.019  ns/op
i.h.serventis.jmh.GaugeOps.gauge_from_conduit_batch                      avgt    5      1.663 ±    0.012  ns/op
i.h.serventis.jmh.MonitorOps.emit_converging_confirmed                   avgt    5      8.471 ±    0.571  ns/op
i.h.serventis.jmh.MonitorOps.emit_converging_confirmed_batch             avgt    5      8.751 ±    0.741  ns/op
i.h.serventis.jmh.MonitorOps.emit_defective_tentative                    avgt    5      7.420 ±    2.216  ns/op
i.h.serventis.jmh.MonitorOps.emit_defective_tentative_batch              avgt    5      9.490 ±    0.741  ns/op
i.h.serventis.jmh.MonitorOps.emit_degraded_measured                      avgt    5      8.040 ±    1.099  ns/op
i.h.serventis.jmh.MonitorOps.emit_degraded_measured_batch                avgt    5      9.554 ±    1.372  ns/op
i.h.serventis.jmh.MonitorOps.emit_down_confirmed                         avgt    5      8.055 ±    1.218  ns/op
i.h.serventis.jmh.MonitorOps.emit_down_confirmed_batch                   avgt    5      9.538 ±    0.563  ns/op
i.h.serventis.jmh.MonitorOps.emit_signal                                 avgt    5      8.347 ±    0.762  ns/op
i.h.serventis.jmh.MonitorOps.emit_signal_batch                           avgt    5      9.654 ±    0.874  ns/op
i.h.serventis.jmh.MonitorOps.emit_stable_confirmed                       avgt    5      8.123 ±    1.583  ns/op
i.h.serventis.jmh.MonitorOps.emit_stable_confirmed_batch                 avgt    5      8.741 ±    1.377  ns/op
i.h.serventis.jmh.MonitorOps.monitor_from_conduit                        avgt    5      1.855 ±    0.018  ns/op
i.h.serventis.jmh.MonitorOps.monitor_from_conduit_batch                  avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.ProbeOps.emit_connect                                  avgt    5      8.339 ±    0.452  ns/op
i.h.serventis.jmh.ProbeOps.emit_connect_batch                            avgt    5      8.979 ±    0.878  ns/op
i.h.serventis.jmh.ProbeOps.emit_connected                                avgt    5      8.347 ±    1.006  ns/op
i.h.serventis.jmh.ProbeOps.emit_connected_batch                          avgt    5      9.821 ±    0.668  ns/op
i.h.serventis.jmh.ProbeOps.emit_disconnect                               avgt    5      8.195 ±    1.395  ns/op
i.h.serventis.jmh.ProbeOps.emit_disconnect_batch                         avgt    5      7.908 ±    0.366  ns/op
i.h.serventis.jmh.ProbeOps.emit_disconnected                             avgt    5      7.838 ±    0.932  ns/op
i.h.serventis.jmh.ProbeOps.emit_disconnected_batch                       avgt    5      9.633 ±    1.216  ns/op
i.h.serventis.jmh.ProbeOps.emit_fail                                     avgt    5      7.831 ±    0.649  ns/op
i.h.serventis.jmh.ProbeOps.emit_fail_batch                               avgt    5      9.462 ±    1.277  ns/op
i.h.serventis.jmh.ProbeOps.emit_failed                                   avgt    5      8.804 ±    1.146  ns/op
i.h.serventis.jmh.ProbeOps.emit_failed_batch                             avgt    5      8.855 ±    1.098  ns/op
i.h.serventis.jmh.ProbeOps.emit_process                                  avgt    5      8.757 ±    0.840  ns/op
i.h.serventis.jmh.ProbeOps.emit_process_batch                            avgt    5      8.772 ±    0.876  ns/op
i.h.serventis.jmh.ProbeOps.emit_processed                                avgt    5      8.159 ±    1.173  ns/op
i.h.serventis.jmh.ProbeOps.emit_processed_batch                          avgt    5      9.874 ±    0.943  ns/op
i.h.serventis.jmh.ProbeOps.emit_receive                                  avgt    5      7.894 ±    0.670  ns/op
i.h.serventis.jmh.ProbeOps.emit_receive_batch                            avgt    5      9.519 ±    0.690  ns/op
i.h.serventis.jmh.ProbeOps.emit_received                                 avgt    5      8.418 ±    0.629  ns/op
i.h.serventis.jmh.ProbeOps.emit_received_batch                           avgt    5      9.317 ±    1.118  ns/op
i.h.serventis.jmh.ProbeOps.emit_signal                                   avgt    5      8.308 ±    0.677  ns/op
i.h.serventis.jmh.ProbeOps.emit_signal_batch                             avgt    5      8.626 ±    1.080  ns/op
i.h.serventis.jmh.ProbeOps.emit_succeed                                  avgt    5      8.658 ±    0.358  ns/op
i.h.serventis.jmh.ProbeOps.emit_succeed_batch                            avgt    5      8.676 ±    1.337  ns/op
i.h.serventis.jmh.ProbeOps.emit_succeeded                                avgt    5      8.757 ±    1.382  ns/op
i.h.serventis.jmh.ProbeOps.emit_succeeded_batch                          avgt    5      9.147 ±    1.200  ns/op
i.h.serventis.jmh.ProbeOps.emit_transmit                                 avgt    5      8.792 ±    0.861  ns/op
i.h.serventis.jmh.ProbeOps.emit_transmit_batch                           avgt    5      9.606 ±    1.186  ns/op
i.h.serventis.jmh.ProbeOps.emit_transmitted                              avgt    5      7.938 ±    0.700  ns/op
i.h.serventis.jmh.ProbeOps.emit_transmitted_batch                        avgt    5      8.514 ±    0.603  ns/op
i.h.serventis.jmh.ProbeOps.probe_from_conduit                            avgt    5      1.850 ±    0.005  ns/op
i.h.serventis.jmh.ProbeOps.probe_from_conduit_batch                      avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.QueueOps.emit_dequeue                                  avgt    5      7.984 ±    1.100  ns/op
i.h.serventis.jmh.QueueOps.emit_dequeue_batch                            avgt    5      7.782 ±    0.668  ns/op
i.h.serventis.jmh.QueueOps.emit_enqueue                                  avgt    5      8.394 ±    0.674  ns/op
i.h.serventis.jmh.QueueOps.emit_enqueue_batch                            avgt    5      8.711 ±    0.763  ns/op
i.h.serventis.jmh.QueueOps.emit_overflow                                 avgt    5      7.995 ±    0.833  ns/op
i.h.serventis.jmh.QueueOps.emit_overflow_batch                           avgt    5      8.889 ±    0.573  ns/op
i.h.serventis.jmh.QueueOps.emit_sign                                     avgt    5      8.483 ±    0.658  ns/op
i.h.serventis.jmh.QueueOps.emit_sign_batch                               avgt    5      8.112 ±    0.404  ns/op
i.h.serventis.jmh.QueueOps.emit_underflow                                avgt    5      7.783 ±    1.756  ns/op
i.h.serventis.jmh.QueueOps.emit_underflow_batch                          avgt    5      8.724 ±    0.639  ns/op
i.h.serventis.jmh.QueueOps.queue_from_conduit                            avgt    5      1.857 ±    0.040  ns/op
i.h.serventis.jmh.QueueOps.queue_from_conduit_batch                      avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.ReporterOps.emit_critical                              avgt    5      8.169 ±    0.783  ns/op
i.h.serventis.jmh.ReporterOps.emit_critical_batch                        avgt    5      9.124 ±    1.240  ns/op
i.h.serventis.jmh.ReporterOps.emit_normal                                avgt    5      8.074 ±    0.361  ns/op
i.h.serventis.jmh.ReporterOps.emit_normal_batch                          avgt    5      7.403 ±    1.017  ns/op
i.h.serventis.jmh.ReporterOps.emit_sign                                  avgt    5      8.406 ±    0.855  ns/op
i.h.serventis.jmh.ReporterOps.emit_sign_batch                            avgt    5      9.299 ±    0.716  ns/op
i.h.serventis.jmh.ReporterOps.emit_warning                               avgt    5      7.900 ±    0.816  ns/op
i.h.serventis.jmh.ReporterOps.emit_warning_batch                         avgt    5      8.859 ±    0.426  ns/op
i.h.serventis.jmh.ReporterOps.reporter_from_conduit                      avgt    5      1.861 ±    0.074  ns/op
i.h.serventis.jmh.ReporterOps.reporter_from_conduit_batch                avgt    5      1.661 ±    0.004  ns/op
i.h.serventis.jmh.ResourceOps.emit_acquire                               avgt    5      7.668 ±    2.128  ns/op
i.h.serventis.jmh.ResourceOps.emit_acquire_batch                         avgt    5      8.157 ±    1.170  ns/op
i.h.serventis.jmh.ResourceOps.emit_attempt                               avgt    5      8.294 ±    1.871  ns/op
i.h.serventis.jmh.ResourceOps.emit_attempt_batch                         avgt    5      7.220 ±    1.915  ns/op
i.h.serventis.jmh.ResourceOps.emit_deny                                  avgt    5      7.899 ±    1.617  ns/op
i.h.serventis.jmh.ResourceOps.emit_deny_batch                            avgt    5      8.103 ±    0.738  ns/op
i.h.serventis.jmh.ResourceOps.emit_grant                                 avgt    5      8.136 ±    0.686  ns/op
i.h.serventis.jmh.ResourceOps.emit_grant_batch                           avgt    5      9.110 ±    0.798  ns/op
i.h.serventis.jmh.ResourceOps.emit_release                               avgt    5      8.400 ±    0.647  ns/op
i.h.serventis.jmh.ResourceOps.emit_release_batch                         avgt    5      7.273 ±    1.329  ns/op
i.h.serventis.jmh.ResourceOps.emit_sign                                  avgt    5      7.963 ±    1.085  ns/op
i.h.serventis.jmh.ResourceOps.emit_sign_batch                            avgt    5      7.686 ±    0.686  ns/op
i.h.serventis.jmh.ResourceOps.emit_timeout                               avgt    5      8.048 ±    0.772  ns/op
i.h.serventis.jmh.ResourceOps.emit_timeout_batch                         avgt    5      8.033 ±    0.571  ns/op
i.h.serventis.jmh.ResourceOps.resource_from_conduit                      avgt    5      1.849 ±    0.016  ns/op
i.h.serventis.jmh.ResourceOps.resource_from_conduit_batch                avgt    5      1.661 ±    0.004  ns/op
i.h.serventis.jmh.RouterOps.emit_corrupt                                 avgt    5      8.232 ±    0.939  ns/op
i.h.serventis.jmh.RouterOps.emit_corrupt_batch                           avgt    5      7.832 ±    0.518  ns/op
i.h.serventis.jmh.RouterOps.emit_drop                                    avgt    5      8.478 ±    0.965  ns/op
i.h.serventis.jmh.RouterOps.emit_drop_batch                              avgt    5      8.216 ±    0.828  ns/op
i.h.serventis.jmh.RouterOps.emit_forward                                 avgt    5      8.519 ±    0.469  ns/op
i.h.serventis.jmh.RouterOps.emit_forward_batch                           avgt    5      8.846 ±    0.571  ns/op
i.h.serventis.jmh.RouterOps.emit_fragment                                avgt    5      8.043 ±    1.304  ns/op
i.h.serventis.jmh.RouterOps.emit_fragment_batch                          avgt    5      9.123 ±    0.452  ns/op
i.h.serventis.jmh.RouterOps.emit_reassemble                              avgt    5      8.147 ±    0.721  ns/op
i.h.serventis.jmh.RouterOps.emit_reassemble_batch                        avgt    5      9.085 ±    1.273  ns/op
i.h.serventis.jmh.RouterOps.emit_receive                                 avgt    5      8.092 ±    0.443  ns/op
i.h.serventis.jmh.RouterOps.emit_receive_batch                           avgt    5      8.600 ±    1.432  ns/op
i.h.serventis.jmh.RouterOps.emit_reorder                                 avgt    5      8.216 ±    1.095  ns/op
i.h.serventis.jmh.RouterOps.emit_reorder_batch                           avgt    5      8.358 ±    1.202  ns/op
i.h.serventis.jmh.RouterOps.emit_route                                   avgt    5      7.946 ±    1.460  ns/op
i.h.serventis.jmh.RouterOps.emit_route_batch                             avgt    5      8.109 ±    0.390  ns/op
i.h.serventis.jmh.RouterOps.emit_send                                    avgt    5      8.002 ±    0.969  ns/op
i.h.serventis.jmh.RouterOps.emit_send_batch                              avgt    5      7.540 ±    0.427  ns/op
i.h.serventis.jmh.RouterOps.emit_sign                                    avgt    5      8.071 ±    0.848  ns/op
i.h.serventis.jmh.RouterOps.emit_sign_batch                              avgt    5      9.163 ±    0.942  ns/op
i.h.serventis.jmh.RouterOps.router_from_conduit                          avgt    5      1.856 ±    0.052  ns/op
i.h.serventis.jmh.RouterOps.router_from_conduit_batch                    avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.ServiceOps.emit_call                                   avgt    5      8.066 ±    1.245  ns/op
i.h.serventis.jmh.ServiceOps.emit_call_batch                             avgt    5      9.473 ±    1.076  ns/op
i.h.serventis.jmh.ServiceOps.emit_called                                 avgt    5      8.355 ±    0.433  ns/op
i.h.serventis.jmh.ServiceOps.emit_called_batch                           avgt    5      9.551 ±    0.437  ns/op
i.h.serventis.jmh.ServiceOps.emit_delay                                  avgt    5      8.187 ±    0.588  ns/op
i.h.serventis.jmh.ServiceOps.emit_delay_batch                            avgt    5      9.622 ±    0.873  ns/op
i.h.serventis.jmh.ServiceOps.emit_delayed                                avgt    5      8.828 ±    0.692  ns/op
i.h.serventis.jmh.ServiceOps.emit_delayed_batch                          avgt    5      9.198 ±    0.555  ns/op
i.h.serventis.jmh.ServiceOps.emit_discard                                avgt    5      8.649 ±    1.761  ns/op
i.h.serventis.jmh.ServiceOps.emit_discard_batch                          avgt    5      8.768 ±    1.130  ns/op
i.h.serventis.jmh.ServiceOps.emit_discarded                              avgt    5      8.576 ±    1.529  ns/op
i.h.serventis.jmh.ServiceOps.emit_discarded_batch                        avgt    5      8.833 ±    1.135  ns/op
i.h.serventis.jmh.ServiceOps.emit_disconnect                             avgt    5      9.519 ±    0.929  ns/op
i.h.serventis.jmh.ServiceOps.emit_disconnect_batch                       avgt    5      8.622 ±    0.904  ns/op
i.h.serventis.jmh.ServiceOps.emit_disconnected                           avgt    5      8.465 ±    1.483  ns/op
i.h.serventis.jmh.ServiceOps.emit_disconnected_batch                     avgt    5      8.862 ±    0.514  ns/op
i.h.serventis.jmh.ServiceOps.emit_expire                                 avgt    5      8.228 ±    1.644  ns/op
i.h.serventis.jmh.ServiceOps.emit_expire_batch                           avgt    5      9.528 ±    0.830  ns/op
i.h.serventis.jmh.ServiceOps.emit_expired                                avgt    5      8.380 ±    1.007  ns/op
i.h.serventis.jmh.ServiceOps.emit_expired_batch                          avgt    5      9.364 ±    1.776  ns/op
i.h.serventis.jmh.ServiceOps.emit_fail                                   avgt    5      8.746 ±    1.226  ns/op
i.h.serventis.jmh.ServiceOps.emit_fail_batch                             avgt    5      9.331 ±    0.833  ns/op
i.h.serventis.jmh.ServiceOps.emit_failed                                 avgt    5      8.606 ±    0.582  ns/op
i.h.serventis.jmh.ServiceOps.emit_failed_batch                           avgt    5      9.037 ±    0.419  ns/op
i.h.serventis.jmh.ServiceOps.emit_recourse                               avgt    5      8.017 ±    0.999  ns/op
i.h.serventis.jmh.ServiceOps.emit_recourse_batch                         avgt    5      8.423 ±    0.981  ns/op
i.h.serventis.jmh.ServiceOps.emit_recoursed                              avgt    5      7.847 ±    1.065  ns/op
i.h.serventis.jmh.ServiceOps.emit_recoursed_batch                        avgt    5      8.975 ±    0.859  ns/op
i.h.serventis.jmh.ServiceOps.emit_redirect                               avgt    5      8.711 ±    0.463  ns/op
i.h.serventis.jmh.ServiceOps.emit_redirect_batch                         avgt    5      9.575 ±    0.603  ns/op
i.h.serventis.jmh.ServiceOps.emit_redirected                             avgt    5      8.471 ±    0.883  ns/op
i.h.serventis.jmh.ServiceOps.emit_redirected_batch                       avgt    5      9.775 ±    1.168  ns/op
i.h.serventis.jmh.ServiceOps.emit_reject                                 avgt    5      8.198 ±    0.843  ns/op
i.h.serventis.jmh.ServiceOps.emit_reject_batch                           avgt    5      9.701 ±    1.277  ns/op
i.h.serventis.jmh.ServiceOps.emit_rejected                               avgt    5      8.385 ±    1.197  ns/op
i.h.serventis.jmh.ServiceOps.emit_rejected_batch                         avgt    5      9.400 ±    1.386  ns/op
i.h.serventis.jmh.ServiceOps.emit_resume                                 avgt    5      8.407 ±    0.943  ns/op
i.h.serventis.jmh.ServiceOps.emit_resume_batch                           avgt    5      9.178 ±    1.401  ns/op
i.h.serventis.jmh.ServiceOps.emit_resumed                                avgt    5      8.359 ±    0.947  ns/op
i.h.serventis.jmh.ServiceOps.emit_resumed_batch                          avgt    5      9.262 ±    1.201  ns/op
i.h.serventis.jmh.ServiceOps.emit_retried                                avgt    5      8.717 ±    1.502  ns/op
i.h.serventis.jmh.ServiceOps.emit_retried_batch                          avgt    5      9.005 ±    0.609  ns/op
i.h.serventis.jmh.ServiceOps.emit_retry                                  avgt    5      8.655 ±    1.095  ns/op
i.h.serventis.jmh.ServiceOps.emit_retry_batch                            avgt    5      8.860 ±    1.139  ns/op
i.h.serventis.jmh.ServiceOps.emit_schedule                               avgt    5      8.181 ±    0.859  ns/op
i.h.serventis.jmh.ServiceOps.emit_schedule_batch                         avgt    5      8.010 ±    0.774  ns/op
i.h.serventis.jmh.ServiceOps.emit_scheduled                              avgt    5      7.906 ±    0.988  ns/op
i.h.serventis.jmh.ServiceOps.emit_scheduled_batch                        avgt    5      9.379 ±    1.548  ns/op
i.h.serventis.jmh.ServiceOps.emit_signal                                 avgt    5      8.340 ±    1.565  ns/op
i.h.serventis.jmh.ServiceOps.emit_signal_batch                           avgt    5      9.259 ±    1.583  ns/op
i.h.serventis.jmh.ServiceOps.emit_start                                  avgt    5      9.027 ±    0.389  ns/op
i.h.serventis.jmh.ServiceOps.emit_start_batch                            avgt    5      7.858 ±    0.914  ns/op
i.h.serventis.jmh.ServiceOps.emit_started                                avgt    5      7.958 ±    0.941  ns/op
i.h.serventis.jmh.ServiceOps.emit_started_batch                          avgt    5      8.279 ±    0.386  ns/op
i.h.serventis.jmh.ServiceOps.emit_stop                                   avgt    5      8.723 ±    0.929  ns/op
i.h.serventis.jmh.ServiceOps.emit_stop_batch                             avgt    5      8.804 ±    1.886  ns/op
i.h.serventis.jmh.ServiceOps.emit_stopped                                avgt    5      8.845 ±    1.085  ns/op
i.h.serventis.jmh.ServiceOps.emit_stopped_batch                          avgt    5      8.867 ±    0.923  ns/op
i.h.serventis.jmh.ServiceOps.emit_succeeded                              avgt    5      8.163 ±    0.966  ns/op
i.h.serventis.jmh.ServiceOps.emit_succeeded_batch                        avgt    5      9.688 ±    1.146  ns/op
i.h.serventis.jmh.ServiceOps.emit_success                                avgt    5      7.903 ±    0.841  ns/op
i.h.serventis.jmh.ServiceOps.emit_success_batch                          avgt    5      7.712 ±    1.899  ns/op
i.h.serventis.jmh.ServiceOps.emit_suspend                                avgt    5      8.849 ±    0.547  ns/op
i.h.serventis.jmh.ServiceOps.emit_suspend_batch                          avgt    5      9.421 ±    1.255  ns/op
i.h.serventis.jmh.ServiceOps.emit_suspended                              avgt    5      8.111 ±    0.852  ns/op
i.h.serventis.jmh.ServiceOps.emit_suspended_batch                        avgt    5      8.535 ±    1.368  ns/op
i.h.serventis.jmh.ServiceOps.service_from_conduit                        avgt    5      1.846 ±    0.009  ns/op
i.h.serventis.jmh.ServiceOps.service_from_conduit_batch                  avgt    5      1.662 ±    0.013  ns/op
i.h.serventis.jmh.TransactionOps.emit_abort_coordinator                  avgt    5      8.291 ±    1.323  ns/op
i.h.serventis.jmh.TransactionOps.emit_abort_coordinator_batch            avgt    5      8.563 ±    0.658  ns/op
i.h.serventis.jmh.TransactionOps.emit_abort_participant                  avgt    5      8.163 ±    0.527  ns/op
i.h.serventis.jmh.TransactionOps.emit_abort_participant_batch            avgt    5      9.472 ±    0.839  ns/op
i.h.serventis.jmh.TransactionOps.emit_commit_coordinator                 avgt    5      8.339 ±    1.242  ns/op
i.h.serventis.jmh.TransactionOps.emit_commit_coordinator_batch           avgt    5      8.802 ±    0.769  ns/op
i.h.serventis.jmh.TransactionOps.emit_commit_participant                 avgt    5      8.720 ±    0.771  ns/op
i.h.serventis.jmh.TransactionOps.emit_commit_participant_batch           avgt    5      7.966 ±    0.719  ns/op
i.h.serventis.jmh.TransactionOps.emit_compensate_coordinator             avgt    5      8.153 ±    0.673  ns/op
i.h.serventis.jmh.TransactionOps.emit_compensate_coordinator_batch       avgt    5      9.517 ±    1.375  ns/op
i.h.serventis.jmh.TransactionOps.emit_compensate_participant             avgt    5      8.058 ±    0.624  ns/op
i.h.serventis.jmh.TransactionOps.emit_compensate_participant_batch       avgt    5      9.448 ±    1.529  ns/op
i.h.serventis.jmh.TransactionOps.emit_conflict_coordinator               avgt    5      7.935 ±    0.907  ns/op
i.h.serventis.jmh.TransactionOps.emit_conflict_coordinator_batch         avgt    5      9.584 ±    0.848  ns/op
i.h.serventis.jmh.TransactionOps.emit_conflict_participant               avgt    5      8.496 ±    0.502  ns/op
i.h.serventis.jmh.TransactionOps.emit_conflict_participant_batch         avgt    5      9.774 ±    0.524  ns/op
i.h.serventis.jmh.TransactionOps.emit_expire_coordinator                 avgt    5      8.775 ±    0.745  ns/op
i.h.serventis.jmh.TransactionOps.emit_expire_coordinator_batch           avgt    5      8.995 ±    0.526  ns/op
i.h.serventis.jmh.TransactionOps.emit_expire_participant                 avgt    5      8.330 ±    1.603  ns/op
i.h.serventis.jmh.TransactionOps.emit_expire_participant_batch           avgt    5      8.755 ±    1.719  ns/op
i.h.serventis.jmh.TransactionOps.emit_prepare_coordinator                avgt    5      8.112 ±    0.772  ns/op
i.h.serventis.jmh.TransactionOps.emit_prepare_coordinator_batch          avgt    5      9.700 ±    1.015  ns/op
i.h.serventis.jmh.TransactionOps.emit_prepare_participant                avgt    5      8.806 ±    0.677  ns/op
i.h.serventis.jmh.TransactionOps.emit_prepare_participant_batch          avgt    5      9.249 ±    2.002  ns/op
i.h.serventis.jmh.TransactionOps.emit_rollback_coordinator               avgt    5      7.995 ±    0.902  ns/op
i.h.serventis.jmh.TransactionOps.emit_rollback_coordinator_batch         avgt    5      8.788 ±    1.370  ns/op
i.h.serventis.jmh.TransactionOps.emit_rollback_participant               avgt    5      8.057 ±    0.805  ns/op
i.h.serventis.jmh.TransactionOps.emit_rollback_participant_batch         avgt    5      7.645 ±    0.591  ns/op
i.h.serventis.jmh.TransactionOps.emit_signal                             avgt    5      8.338 ±    0.962  ns/op
i.h.serventis.jmh.TransactionOps.emit_signal_batch                       avgt    5      8.658 ±    0.860  ns/op
i.h.serventis.jmh.TransactionOps.emit_start_coordinator                  avgt    5      8.486 ±    1.277  ns/op
i.h.serventis.jmh.TransactionOps.emit_start_coordinator_batch            avgt    5      9.363 ±    1.428  ns/op
i.h.serventis.jmh.TransactionOps.emit_start_participant                  avgt    5      8.089 ±    1.506  ns/op
i.h.serventis.jmh.TransactionOps.emit_start_participant_batch            avgt    5      8.703 ±    1.073  ns/op
i.h.serventis.jmh.TransactionOps.transaction_from_conduit                avgt    5      1.853 ±    0.047  ns/op
i.h.serventis.jmh.TransactionOps.transaction_from_conduit_batch          avgt    5      1.661 ±    0.008  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                       avgt    5    289.855 ±  144.176  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                       avgt    5    278.647 ±  151.890  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                   avgt    5    278.530 ±   64.599  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                           avgt    5    677.767 ± 2334.147  ns/op
i.h.substrates.jmh.CircuitOps.create_await_close                         avgt    5  10166.322 ±  497.789  ns/op
i.h.substrates.jmh.CircuitOps.hot_await_queue_drain                      avgt    5   5740.222 ±   91.921  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                         avgt    5     19.975 ±    0.104  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                   avgt    5     19.951 ±    0.043  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow               avgt    5     22.855 ±    0.043  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                             avgt    5      1.706 ±    0.654  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                   avgt    5      3.874 ±    1.095  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                 avgt    5    370.778 ±  890.689  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                       avgt    5    261.256 ±  194.467  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                avgt    5      1.883 ±    0.102  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                          avgt    5      1.656 ±    0.007  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                           avgt    5      2.005 ±    0.047  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                     avgt    5      1.815 ±    0.004  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                 avgt    5      3.484 ±    0.221  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                           avgt    5      3.314 ±    0.038  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                  avgt    5    461.615 ±  447.226  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                            avgt    5    398.275 ±  141.063  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await              avgt    5   8243.370 ±  273.733  ns/op
i.h.substrates.jmh.CortexOps.circuit                                     avgt    5    274.180 ±   42.929  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                               avgt    5    300.283 ±   34.765  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                               avgt    5    261.848 ±  105.425  ns/op
i.h.substrates.jmh.CortexOps.current                                     avgt    5      1.067 ±    0.001  ns/op
i.h.substrates.jmh.CortexOps.name_class                                  avgt    5      1.496 ±    0.017  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                   avgt    5      2.835 ±    0.017  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                               avgt    5     11.242 ±    0.028  ns/op
i.h.substrates.jmh.CortexOps.name_path                                   avgt    5      1.889 ±    0.010  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                             avgt    5      1.682 ±    0.009  ns/op
i.h.substrates.jmh.CortexOps.name_string                                 avgt    5      2.798 ±    0.436  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                           avgt    5      2.551 ±    0.289  ns/op
i.h.substrates.jmh.CortexOps.pipe_empty                                  avgt    5      0.454 ±    0.091  ns/op
i.h.substrates.jmh.CortexOps.pipe_empty_batch                            avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.CortexOps.pipe_observer                               avgt    5      5.759 ±    0.980  ns/op
i.h.substrates.jmh.CortexOps.pipe_observer_batch                         avgt    5      1.458 ±    0.052  ns/op
i.h.substrates.jmh.CortexOps.pipe_transform                              avgt    5      4.532 ±    0.212  ns/op
i.h.substrates.jmh.CortexOps.scope                                       avgt    5      9.734 ±    2.616  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                 avgt    5      7.486 ±    0.056  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                 avgt    5      7.977 ±    0.057  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                avgt    5      2.443 ±    0.155  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                 avgt    5      2.384 ±    0.494  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                    avgt    5      2.414 ±    0.064  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                   avgt    5      2.446 ±    0.051  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                 avgt    5      2.438 ±    0.091  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                 avgt    5      0.442 ±    0.002  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                           avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                        avgt    5     18.245 ±    1.399  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                avgt    5     20.462 ±    0.353  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await               avgt    5     19.976 ±    1.055  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await               avgt    5     18.058 ±    0.903  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                               avgt    5     19.582 ±    2.462  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                              avgt    5     16.976 ±    0.284  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                              avgt    5     17.596 ±    1.555  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                             avgt    5     18.987 ±    0.972  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                               avgt    5     18.538 ±    0.804  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                             avgt    5     16.949 ±    0.064  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                 avgt    5      9.041 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                           avgt    5      9.047 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_compare                                  avgt    5     32.518 ±    0.471  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                            avgt    5     30.918 ±    0.666  ns/op
i.h.substrates.jmh.NameOps.name_depth                                    avgt    5      1.645 ±    0.023  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                              avgt    5      1.367 ±    0.347  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                avgt    5      0.588 ±    0.052  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                avgt    5      2.824 ±    0.021  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                            avgt    5     11.450 ±    0.008  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                            avgt    5     13.018 ±    0.028  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                     avgt    5     11.720 ±    0.133  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                avgt    5      4.217 ±    0.003  ns/op
i.h.substrates.jmh.NameOps.name_from_string                              avgt    5      2.995 ±    0.458  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                        avgt    5      2.725 ±    0.358  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                        avgt    5     12.413 ±    0.137  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                      avgt    5      3.562 ±    0.020  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                       avgt    5      9.108 ±    0.620  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                        avgt    5      1.662 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                  avgt    5      1.892 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                            avgt    5      1.683 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                          avgt    5     40.010 ±    1.282  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                    avgt    5     28.100 ±    8.534  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_1                            avgt    5      0.051 ±    0.007  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_10                           avgt    5      9.260 ±    0.264  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_20                           avgt    5     24.734 ±    0.523  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_5                            avgt    5      4.538 ±    0.274  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_1                           avgt    5      0.051 ±    0.007  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_10                          avgt    5      0.269 ±    0.014  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_20                          avgt    5      5.992 ±    0.013  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_5                           avgt    5      0.200 ±    0.028  ns/op
i.h.substrates.jmh.PipeOps.emit_no_await                                 avgt    5      0.044 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.emit_to_async_pipe                            avgt    5      8.276 ±    0.793  ns/op
i.h.substrates.jmh.PipeOps.emit_to_chained_pipes                         avgt    5      1.636 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.emit_to_double_transform                      avgt    5      0.500 ±    0.003  ns/op
i.h.substrates.jmh.PipeOps.emit_to_empty_pipe                            avgt    5      0.448 ±    0.086  ns/op
i.h.substrates.jmh.PipeOps.emit_to_fanout                                avgt    5      0.903 ±    0.036  ns/op
i.h.substrates.jmh.PipeOps.emit_to_receptor_pipe                         avgt    5      0.656 ±    0.124  ns/op
i.h.substrates.jmh.PipeOps.emit_to_transform_pipe                        avgt    5      0.718 ±    0.039  ns/op
i.h.substrates.jmh.PipeOps.emit_with_await                               avgt    5   5598.948 ±  374.559  ns/op
i.h.substrates.jmh.PipeOps.emit_with_counter_await                       avgt    5   7804.642 ±  553.909  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await         avgt    5     94.645 ±   15.879  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch   avgt    5     17.166 ±    0.231  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await         avgt    5     96.770 ±    2.453  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch   avgt    5     28.018 ±    1.024  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                    avgt    5     93.555 ±    2.253  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch              avgt    5     27.689 ±    0.723  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await        avgt    5    304.665 ±    6.166  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await        avgt    5     81.359 ±    1.406  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch  avgt    5     23.245 ±    0.303  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await        avgt    5     91.334 ±    1.748  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch  avgt    5     25.565 ±    0.186  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await         avgt    5     97.852 ±   12.053  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                        avgt    5     18.171 ±    2.633  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                  avgt    5     16.737 ±    0.133  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                            avgt    5     20.496 ±    6.417  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                      avgt    5     16.995 ±    0.065  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                       avgt    5      2.436 ±    0.032  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                 avgt    5      0.034 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                avgt    5    293.677 ±   48.597  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                          avgt    5    299.490 ±   46.942  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                avgt    5    911.180 ±  383.761  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                       avgt    5      2.468 ±    0.020  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                 avgt    5      0.034 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                           avgt    5      2.465 ±    0.021  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                     avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                              avgt    5     27.063 ±    0.109  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                        avgt    5     26.652 ±    0.083  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                 avgt    5     42.991 ±    0.146  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch           avgt    5     41.740 ±    2.838  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                      avgt    5   1397.599 ±  344.949  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                avgt    5   1482.118 ±  184.110  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                        avgt    5    307.907 ±   65.797  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                  avgt    5    285.421 ±   48.337  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                         avgt    5    609.558 ±   79.890  ns/op
i.h.substrates.jmh.StateOps.slot_name                                    avgt    5      0.530 ±    0.055  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                              avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                    avgt    5      0.443 ±    0.002  ns/op
i.h.substrates.jmh.StateOps.slot_value                                   avgt    5      0.639 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                             avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                avgt    5     10.294 ±    0.065  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                          avgt    5     10.756 ±    0.182  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                          avgt    5      2.160 ±    0.027  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                           avgt    5      4.797 ±    0.690  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                     avgt    5      4.536 ±    1.556  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                          avgt    5      4.699 ±    0.717  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                        avgt    5      2.684 ±    0.518  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                  avgt    5      2.483 ±    0.312  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                        avgt    5      4.554 ±    1.885  ns/op
i.h.substrates.jmh.StateOps.state_value_read                             avgt    5      1.486 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                       avgt    5      1.267 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                          avgt    5      4.907 ±    0.321  ns/op
```

## Benchmark Implementation Details

### CircuitOps - Hot-Path Benchmarks

CircuitOps includes specialized hot-path benchmarks that isolate operation costs from circuit
lifecycle overhead:

- `hot_conduit_create()` - Create conduit on already-running circuit
- `hot_conduit_create_named()` - Create named conduit on running circuit
- `hot_conduit_with_flow()` - Create conduit with flow operators on running circuit
- `hot_pipe_async()` - Create async pipe on running circuit
- `hot_pipe_async_with_flow()` - Create async pipe with flows on running circuit

These use `@Setup(Level.Iteration)` to create a circuit once per iteration, then measure operations
without including circuit creation/teardown costs.

**Usage**: Compare against regular benchmarks to understand lifecycle overhead vs. operation cost.

### Batched Benchmarks Standard

All batched benchmarks follow a consistent pattern with `BATCH_SIZE = 1000`:

```java

@Benchmark
@OperationsPerInvocation ( BATCH_SIZE )
public ReturnType operation_batch () {
    ReturnType result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
        result = operation ();
    }
    return result;  // Prevent dead-code elimination
}
```

**Files with batched benchmarks**:

**Substrates Core**:

- `CircuitOps` - Circuit creation (6 batched benchmarks)
- `ConduitOps` - Channel pooling (4 batched benchmarks)
- `CortexOps` - Factory methods (7 batched benchmarks)
- `NameOps` - Name operations (6 batched benchmarks)
- `StateOps` - State/slot operations (6 batched benchmarks)
- `ReservoirOps` - Emission capture (5 batched benchmarks)
- `ScopeOps` - Lifecycle management (10 batched benchmarks)

**Serventis Extensions**:

- `CounterOps` - Counter signals (4 batched benchmarks)
- `MonitorOps` - Monitor signals (6 batched benchmarks)
- `GaugeOps` - Gauge signals (6 batched benchmarks)
- `QueueOps` - Queue signals (5 batched benchmarks)
- `CacheOps` - Cache signals (8 batched benchmarks)
- `ResourceOps` - Resource signals (7 batched benchmarks)
- `ActorOps` - Speech act signals (12 batched benchmarks)
- `AgentOps` - Promise theory signals (21 batched benchmarks)
- `ServiceOps` - Service lifecycle signals (27 batched benchmarks)
- `RouterOps` - Packet routing signals (10 batched benchmarks)
- `ProbeOps` - Communication signals (15 batched benchmarks)
- `ReporterOps` - Reporter signals (4 batched benchmarks)
- `TransactionOps` - Transaction coordination signals (18 batched benchmarks)

### Interpreting Results

#### Hot-Path vs. Cold-Path

Compare hot-path and cold-path benchmarks to understand lifecycle costs:

```
CircuitOps.create_and_close:        526 ns/op  (includes creation + close)
CircuitOps.hot_conduit_create:      21 ns/op   (operation only, no lifecycle)
```

This shows that circuit creation/teardown dominates total cost, while the actual conduit creation
operation is extremely fast on a running circuit.

#### Batched vs. Single-Operation

Batched benchmarks provide more stable measurements for fast operations:

```
NameOps.name_from_string:           3 ns/op    (single operation)
NameOps.name_from_string_batch:     3 ns/op    (amortized over 1000 ops)
```

The batched version provides more stable measurements by amortizing JMH framework overhead across
many operations, though the actual operation cost remains consistent at ~3 ns/op.

#### Emission Latency vs. Round-Trip Time

**CRITICAL DISTINCTION**: In production, emissions are asynchronous - the caller doesn't wait for
processing. The `await()` call is primarily for testing/benchmarking synchronization and is rarely
used in production.

**Emission Cost** (Production Hot Path):

- **Empty pipe**: ~0.4 ns/op (PipeOps.emit_to_empty_pipe) - Fastest, discards value
- **Receptor pipe**: ~0.6 ns/op (PipeOps.emit_to_receptor_pipe) - Synchronous callback
- **Transform pipe**: ~0.7 ns/op (PipeOps.emit_to_transform_pipe) - Inline transformation
- **Async pipe**: ~8 ns/op (PipeOps.emit_to_async_pipe) - Enqueue to circuit
- **Counter signal**: ~8 ns/op (CounterOps.emit_increment) - Serventis signal emission

These measure **what the caller pays** - the cost of calling `pipe.emit()` and returning
immediately.

**Round-Trip Cost** (Testing/Synchronization):

- **Emission + await**: ~6470 ns/op (PipeOps.emit_with_await) - Full synchronization
- **With counter**: ~6361 ns/op (PipeOps.emit_with_counter_await) - Including observer work

These measure **emission + circuit processing + await()** - used for testing but not representative
of production performance where emissions are fire-and-forget.

**Key Insight**: The async pipe emission (~8ns) is the production hot-path cost for crossing the
circuit boundary. The round-trip benchmarks (~6470ns) include queue draining and thread
synchronization overhead that doesn't occur in normal production flow.

#### Parameterized Topology Benchmarks

PipeOps includes parameterized benchmarks to measure how topology affects performance:

**Chain Depth** (1, 5, 10, 20 pipes):

- Measures how emission cost scales with chain length
- Each chain consists of identity transformation pipes: `v -> v`
- Tests synchronous pipe chaining overhead

```bash
# Compare scaling across different chain depths
substrates/jmh.sh "PipeOps.emit_chain_depth.*"
```

**Fan-out Width** (1, 5, 10, 20 targets):

- Measures how emission cost scales with broadcast width
- Each fan-out broadcasts a single emission to N empty pipes
- Tests one-to-many emission overhead

```bash
# Compare scaling across different fan-out widths
substrates/jmh.sh "PipeOps.emit_fanout_width.*"
```

**Expected Scaling**:

- **Chain depth**: Linear scaling O(n) - each pipe adds transformation overhead
- **Fan-out width**: Linear scaling O(n) - each target requires separate emit() call

These benchmarks help determine the performance impact of different pipe topologies in neural-like
network architectures.

#### Serventis Signals

All Serventis signal emissions have similar performance (~7-9 ns/op) because they follow the same
pattern:

1. Cache lookup for pre-computed Signal instance (~2 ns)
2. Pipe emission to circuit queue (~5-7 ns)

This consistency validates the zero-allocation signal caching strategy.

**Transaction Coordination Signals**: The TransactionOps benchmarks measure distributed transaction
coordination operations from both COORDINATOR (transaction manager) and PARTICIPANT (client)
perspectives. All transaction signals (START, PREPARE, COMMIT, ROLLBACK, ABORT, EXPIRE, CONFLICT,
COMPENSATE) have consistent ~8-9 ns/op emission cost across both dimensions, confirming that the
dual-perspective model adds no performance overhead. Transaction creation (~1.85 ns/op) matches
other Serventis instruments, enabling observation of distributed protocols (2PC, 3PC, Saga) with
minimal impact.

### Dead-Code Elimination Prevention

All benchmarks return values to prevent JVM dead-code elimination:

```java

@Benchmark
public Name name_from_string () {
    return cortex.name ( FIRST );  // Return value used by JMH
}
```

Without returning the value, the JVM might optimize away the entire operation, producing unrealistic
results.

## Performance Guidelines

### When to Use Hot-Path Benchmarks

Use hot-path benchmarks when:

- Measuring operation performance in long-lived circuits
- Optimizing steady-state behavior
- Comparing algorithmic approaches without lifecycle noise
- Understanding the actual cost of an operation in production

### When to Use Batched Benchmarks

Use batched benchmarks when:

- Operations are extremely fast (< 10 ns)
- Single measurements have high variance
- Comparing relative performance of similar operations
- Measuring throughput-oriented scenarios

### When to Use Single-Operation Benchmarks

Use single-operation benchmarks when:

- Operations are slow enough for stable measurements (> 100 ns)
- Total cost including overhead is relevant
- Comparing different operation types
- Establishing baseline measurements

## Example: Comparing Benchmark Types

To understand the performance characteristics of a circuit conduit:

```bash
# 1. Measure full lifecycle cost
substrates/jmh.sh CircuitOps.conduit_create_close

# 2. Measure hot-path operation cost
substrates/jmh.sh CircuitOps.hot_conduit_create

# 3. Measure amortized cost
substrates/jmh.sh CortexOps.circuit_batch
```

This provides three perspectives:

- Full lifecycle cost: Creation + operation + cleanup
- Hot-path cost: Operation only (steady state)
- Amortized cost: Average over many operations

## Benchmark Configuration

All benchmarks use consistent JMH configuration:

```java
@BenchmarkMode ( Mode.AverageTime )
@OutputTimeUnit ( TimeUnit.NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )
```

This configuration balances measurement accuracy with reasonable execution time.

## Contributing Benchmarks

When adding new benchmarks, follow these guidelines:

1. **Add batched variants** for operations < 10 ns
2. **Use BATCH_SIZE = 1000** consistently
3. **Return results** to prevent DCE
4. **Include hot-path variants** for lifecycle operations
5. **Document expected performance** in code comments
6. **Update BENCHMARKS.md** with new results

## See Also

- `CLAUDE.md` - Development conventions and performance best practices
- `USER_GUIDE.md` - Comprehensive API usage guide
- `substrates/jmh/` - Benchmark source code