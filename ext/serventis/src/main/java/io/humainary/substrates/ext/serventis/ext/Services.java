// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.ext;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

import static io.humainary.substrates.ext.serventis.ext.Services.Dimension.RECEIPT;
import static io.humainary.substrates.ext.serventis.ext.Services.Dimension.RELEASE;
import static java.util.Objects.requireNonNull;


/// # Services API
///
/// The `Services` API provides a comprehensive framework for observing service-to-service and
/// intra-service interactions through **semantic signal emission**. It enables fine-grained
/// instrumentation of work execution, coordination, and outcome reporting based on signaling
/// theory and social systems regulated by local and remote status assessment.
///
/// ## Purpose
///
/// This API enables systems to emit **rich semantic signals** about service interactions,
/// capturing not just success/failure but the full lifecycle of work: scheduling, delays,
/// retries, redirections, suspensions, and more. The dual-dimension model (RELEASE/RECEIPT)
/// enables both self-reporting and observation of remote signals.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting service interaction semantics**, not implementing services.
/// If you have actual service implementations (REST endpoints, RPC handlers, workflow engines, etc.),
/// use this API to emit observability signals about operations performed by or on them.
/// Observer agents can then reason about service health, interaction patterns, and distributed
/// system behavior without coupling to your implementation details.
///
/// **Example**: When your service receives a request, call `service.start(RELEASE)` before
/// processing begins. When complete, call `service.success(RELEASE)` or `service.fail(RELEASE)`.
/// The signals enable meta-observability: observing the observability instrumentation itself
/// to understand service lifecycle, coordination patterns, and system-wide interaction dynamics.
///
/// ## Key Concepts
///
/// - **Service**: An observable entity that emits signals about work execution and coordination
/// - **Signal**: A semantic event combining a **Sign** (what happened) and **Dimension** (perspective)
/// - **Sign**: The type of interaction (START, CALL, SUCCESS, FAIL, RETRY, etc.)
/// - **Dimension**: The perspective (RELEASE = self, RECEIPT = observed from other)
/// - **Work**: Either local execution or remote calling of operations or functions
///
/// ## Dual-Dimension Model
///
/// Every sign has two dimensions representing different perspectives:
///
/// | Dimension | Perspective        | Timing  | Example Signals               |
/// |-----------|--------------------|---------|------------------------------ |
/// | RELEASE   | Self-perspective   | Present | CALL, START, SUCCESS, FAIL    |
/// | RECEIPT   | Other-perspective  | Past    | CALLED, STARTED, SUCCEEDED    |
///
/// **RELEASE** signals indicate "I am doing this now" while **RECEIPT** signals indicate
/// "I observed that happened". This enables distributed systems to coordinate based on
/// both local actions and observed remote state.
///
/// ## Signal Categories
///
/// The API defines signals across several semantic categories:
///
/// ### Execution Lifecycle
/// - **START/STARTED**: Work execution begins
/// - **STOP/STOPPED**: Work execution completes (regardless of outcome)
/// - **CALL/CALLED**: Remote work request issued
///
/// ### Outcomes
/// - **SUCCESS/SUCCEEDED**: Work completed successfully
/// - **FAIL/FAILED**: Work failed to complete
///
/// ### Flow Control
/// - **DELAY/DELAYED**: Work postponed
/// - **SCHEDULE/SCHEDULED**: Work queued for future execution
/// - **SUSPEND/SUSPENDED**: Work paused, may resume
/// - **RESUME/RESUMED**: Suspended work restarted
///
/// ### Error Handling
/// - **RETRY/RETRIED**: Failed work being reattempted
/// - **RECOURSE/RECOURSED**: Degraded mode activated
/// - **REDIRECT/REDIRECTED**: Work forwarded to alternative service
///
/// ### Rejection
/// - **REJECT/REJECTED**: Work declined (e.g., overload, policy)
/// - **DISCARD/DISCARDED**: Work dropped (e.g., expired, invalid)
/// - **DISCONNECT/DISCONNECTED**: Unable to reach service
///
/// ### Temporal
/// - **EXPIRE/EXPIRED**: Work exceeded time budget
///
/// ## Relationship to Other APIs
///
/// `Services` integrates with other Serventis APIs to form a complete observability picture:
///
/// - **Probes API**: Service failures (FAIL) may correspond to probe.fail() / probe.failed() signals
/// - **Resources API**: Service delays (DELAY) may be caused by resource denials (DENY)
/// - **Monitors API**: Service signal patterns inform condition assessment (many FAILs → DEGRADED)
/// - **Reporters API**: Service conditions influence situational priority (sustained FAILs → CRITICAL)
///
/// ## Orientation Usage Patterns
///
/// ### Self-Reporting (RELEASE)
/// ```java
/// service.call();      // I am calling remote service
/// service.success();   // I completed successfully
/// service.retry();     // I am retrying after failure
/// ```
///
/// ### Observing Remote State (RECEIPT)
/// ```java
/// // Response indicates remote service status
/// if (response.hasHeader("X-Service-Delayed")) {
///   service.delayed();   // Remote service signaled delay
/// }
/// if (response.status == 503) {
///   service.rejected();  // Remote service rejected request
/// }
/// ```
///
/// ## Performance Considerations
///
/// Service signal emissions are designed for per-request operation at moderate to high frequency
/// (100-100K signals/sec typical). Signals flow asynchronously through the circuit's event queue,
/// adding minimal overhead to service operations. The convenience methods (`execute`, `dispatch`)
/// add negligible overhead (~10-20ns) compared to manual signal emission.
///
/// For extremely high-frequency services (>1M requests/sec), consider:
/// - Using manual signal emission to avoid lambda allocation overhead
/// - Sampling signals rather than emitting for every request
/// - Aggregating signals before emission
///
/// @author William David Louth
/// @since 1.0

public final class Services
  implements Serventis {

  private Services () { }

  /// A functional interface representing a function that returns a result and may throw a checked exception.
  ///
  /// @param <R> the return type of the function
  /// @param <T> the throwable class type that may be thrown

  @FunctionalInterface
  public interface Fn < R, T extends Throwable > {

    /// Creates a Fn instance, useful for resolving ambiguity with overloaded methods
    /// or for explicitly typing lambda expressions at compile-time.
    ///
    /// @param fn  the function to wrap
    /// @param <R> the return type of the [#eval()]
    /// @param <T> the throwable class type thrown by the operation
    /// @return The specified function as a Fn instance
    /// @throws NullPointerException if `fn` param is `null`

    static < R, T extends Throwable > Fn < R, T > of (
      @NotNull final Fn < R, T > fn
    ) {

      requireNonNull ( fn );

      return fn;

    }


    /// Invokes the underlying function.
    ///
    /// @return The result from calling of the function call
    /// @throws T The derived throwable type thrown

    R eval () throws T;

  }

  /// A functional interface representing an operation that returns no result and may throw a checked exception.
  ///
  /// @param <T> the throwable class type that may be thrown

  @FunctionalInterface
  public interface Op < T extends Throwable > {

    /// Creates an Op instance, useful for resolving ambiguity with overloaded methods
    /// or for explicitly typing lambda expressions at compile-time.
    ///
    /// @param op  the operation to wrap
    /// @param <T> the throwable class type thrown by the operation
    /// @return The specified operation as an Op instance
    /// @throws NullPointerException if `op` param is `null`

    static < T extends Throwable > Op < T > of (
      @NotNull final Op < T > op
    ) {

      requireNonNull ( op );

      return op;

    }


    /// Converts a [Fn] into an Op.
    ///
    /// @param fn  the [Fn] to be transformed
    /// @param <R> the return type of the function being converted
    /// @param <T> the throwable class type thrown by the operation
    /// @return An Op that wraps the function
    /// @throws NullPointerException if `fn` param is `null`

    @New
    @NotNull
    static < R, T extends Throwable > Op < T > of (
      @NotNull final Fn < R, ? extends T > fn
    ) {

      requireNonNull ( fn );

      return fn::eval;

    }


    /// Invokes the underlying operation.
    ///
    /// @throws T The derived throwable type thrown

    void exec () throws T;

  }

  /// A static composer function for creating Service instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var service = circuit.conduit(Services::composer).percept(cortex.name("order.processor"));
  /// ```
  ///
  /// @param channel the channel from which to create the service
  /// @return a new Service instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Service composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Service (
        channel.pipe ()
      );

  }


  /// A [Sign] classifies operations, transitions, and outcomes that occur during service request
  /// execution and inter-service calling. These classifications enable analysis of service behavior,
  /// coordination patterns, and resilience strategies.
  ///
  /// Note: We use the term `work` here to mean either (remote) call or (local) execution.
  ///
  /// ## Sign Categories
  ///
  /// Signs are organized into functional categories representing different aspects of service behavior:
  ///
  /// - **Lifecycle**: START, STOP, CALL
  /// - **Outcomes**: SUCCESS, FAIL
  /// - **Flow Control**: DELAY, SCHEDULE, SUSPEND, RESUME
  /// - **Error Handling**: RETRY, RECOURSE, REDIRECT
  /// - **Rejection**: REJECT, DISCARD, DISCONNECT
  /// - **Temporal**: EXPIRE

  public enum Sign
    implements Serventis.Sign {

    /// Indicates the start of work execution.
    ///
    /// START marks the beginning of actual work processing, after scheduling and before outcomes.
    /// Used for tracking execution timing, concurrency levels, and work-in-progress metrics.
    ///
    /// **Typical usage**: Local execution start, thread/coroutine activation, batch processing start

    START,

    /// Indicates the completion of work execution (regardless of outcome).
    ///
    /// STOP marks the end of work processing, whether successful or failed. The time between
    /// START and STOP represents actual execution duration. Always emitted in finally blocks
    /// to ensure accurate execution timing.
    ///
    /// **Typical usage**: Execution completion, resource cleanup, duration measurement

    STOP,

    /// Indicates a request (call) for work to be done by another service.
    ///
    /// CALL represents the invocation of remote or external work, distinguishing it from local
    /// execution (START). Used to track inter-service dependencies, call graphs, and distributed
    /// trace initiation.
    ///
    /// **Typical usage**: RPC calls, HTTP requests, message sends, async job submission

    CALL,

    /// Indicates successful completion of work.
    ///
    /// SUCCESS represents work that completed as intended, meeting its contract and producing
    /// valid results. Forms the baseline for success rate calculations and SLO compliance.
    ///
    /// **Typical usage**: Successful execution, valid results returned, contract fulfilled

    SUCCESS,

    /// Indicates failure to complete work.
    ///
    /// FAIL represents work that could not complete successfully due to errors, exceptions,
    /// invalid inputs, or violated constraints. Forms the basis for error rate calculations
    /// and degradation detection.
    ///
    /// **Typical usage**: Exceptions thrown, validation failures, constraint violations, errors

    FAIL,

    /// Indicates activation of a degraded operational mode after failure.
    ///
    /// RECOURSE represents fallback strategies activated when primary paths fail. Unlike RETRY
    /// (trying again) or REDIRECT (going elsewhere), RECOURSE means degraded functionality.
    ///
    /// **Typical usage**: Circuit breaker fallbacks, cached responses, degraded mode, default values

    RECOURSE,

    /// Indicates forwarding of work to an alternative service or endpoint.
    ///
    /// REDIRECT represents work being routed to a different destination, often for load balancing,
    /// failover, or service mesh routing. Preserves work identity while changing destination.
    ///
    /// **Typical usage**: Load balancer redirects, failover routing, service mesh splits, A/B tests

    REDIRECT,

    /// Indicates work exceeded its time budget.
    ///
    /// EXPIRE represents work that ran too long or missed its deadline. Different from TIMEOUT
    /// (waiting for response) - EXPIRE means the work itself took too long.
    ///
    /// **Typical usage**: Deadline exceeded, SLA violation, budget exhaustion, TTL expiration

    EXPIRE,

    /// Indicates automatic retry of work after failure.
    ///
    /// RETRY represents reattempting work that previously failed, typically for transient errors.
    /// Forms the basis for retry rate analysis, backoff effectiveness, and eventual success tracking.
    ///
    /// **Typical usage**: Transient error recovery, network retry, idempotent operation repeat

    RETRY,

    /// Indicates refusal to accept work.
    ///
    /// REJECT represents work being turned away at the boundary, typically due to overload,
    /// policy violations, or capacity limits. Work is never started. Enables admission control.
    ///
    /// **Typical usage**: Rate limiting, circuit breaker open, overload protection, policy denial

    REJECT,

    /// Indicates deliberate dropping of work.
    ///
    /// DISCARD represents work being intentionally abandoned, typically due to invalidity,
    /// irrelevance, or changed priorities. Different from REJECT (not accepted) - DISCARD means
    /// accepted but then dropped.
    ///
    /// **Typical usage**: Invalid requests, expired messages, priority shedding, queue overflow

    DISCARD,

    /// Indicates postponement of work.
    ///
    /// DELAY represents work being intentionally slowed or deferred, often for backpressure,
    /// rate limiting, or coordination. Work will eventually proceed.
    ///
    /// **Typical usage**: Backpressure delays, rate limiting pauses, exponential backoff, throttling

    DELAY,

    /// Indicates work queued for future execution.
    ///
    /// SCHEDULE represents work being placed in a queue or scheduled for later processing.
    /// Forms the basis for queue depth, scheduling lag, and backlog analysis.
    ///
    /// **Typical usage**: Task queuing, delayed execution, batch scheduling, async job queuing

    SCHEDULE,

    /// Indicates work paused awaiting resumption.
    ///
    /// SUSPEND represents work being temporarily halted, preserving state for later resumption.
    /// Different from DELAY (brief pause) - SUSPEND means potentially long-term suspension.
    ///
    /// **Typical usage**: Long-running workflows, saga compensation, manual intervention, resource wait

    SUSPEND,

    /// Indicates previously suspended work restarting.
    ///
    /// RESUME represents suspended work being reactivated. Forms pairs with SUSPEND signals
    /// to track suspension duration and resumption patterns.
    ///
    /// **Typical usage**: Workflow continuation, saga resume, manual restart, resource availability

    RESUME,

    /// Indicates inability to reach or communicate with a service.
    ///
    /// DISCONNECT represents communication failures preventing work submission. Different from
    /// FAIL (work attempted and failed) - DISCONNECT means work couldn't be attempted.
    ///
    /// **Typical usage**: Connection failures, network partitions, service unreachable, DNS failures

    DISCONNECT

  }

  /// The [Dimension] enum classifies the perspective and timing of signal recording.
  ///
  /// Every sign in the Services API has two dimensions, representing fundamentally different
  /// perspectives on service interactions. This dual-dimension model enables both **self-reporting**
  /// and **observation of remote state**.
  ///
  /// ## The Two Dimensions
  ///
  /// | Dimension | Perspective       | Timing  | Voice     | Example                          |
  /// |-----------|-------------------|---------|-----------|----------------------------------|
  /// | RELEASE   | Self (1st person) | Present | "I am"    | "I am calling the service"       |
  /// | RECEIPT   | Other (3rd person)| Past    | "It did"  | "It was called by the service"   |
  ///
  /// ## RELEASE vs RECEIPT
  ///
  /// **RELEASE** signals represent **actions being taken now** by the local service:
  /// - Generated by the service performing the action
  /// - Present-tense semantics ("I am starting", "I am calling")
  /// - Used for self-reporting and local state tracking
  /// - Forms the basis for distributed tracing spans
  ///
  /// **RECEIPT** signals represent **observations of remote actions** that occurred in the past:
  /// - Generated when observing signals from other services
  /// - Past-tense semantics ("It was started", "It was called")
  /// - Extracted from responses, headers, or event notifications
  /// - Used for distributed coordination and status propagation
  ///
  /// ## Practical Examples
  ///
  /// ### Client Making a Call
  /// ```java
  /// client.call();      // RELEASE: "I am calling the remote service"
  /// var response = remoteService.process(request);
  /// if (response.status == 200) {
  ///   client.success(); // RELEASE: "I completed successfully"
  /// }
  /// ```
  ///
  /// ### Server Receiving Signals in Response
  /// ```java
  /// var response = callRemoteService(request);
  ///
  /// // Response headers indicate remote service state
  /// if (response.hasHeader("X-Service-Delayed")) {
  ///   service.delayed();   // RECEIPT: "It was delayed" (past)
  /// }
  /// if (response.hasHeader("X-Service-Rejected")) {
  ///   service.rejected();  // RECEIPT: "It was rejected" (past)
  /// }
  /// ```
  ///
  /// ### Service Observing Cascading State
  /// ```java
  /// // Local service receives request indicating upstream delays
  /// if (request.hasHeader("X-Upstream-Degraded")) {
  ///   localService.delayed();   // RECEIPT: Propagating observed state
  /// }
  ///
  /// // Local service reports its own state
  /// localService.start();       // RELEASE: Local action
  /// ```
  ///
  /// ## Temporal Semantics
  ///
  /// - **RELEASE**: Present tense, happening **now**, real-time reporting
  /// - **RECEIPT**: Past tense, happened **earlier**, delayed observation
  ///
  /// The temporal distinction is crucial for understanding causality in distributed systems.
  /// RECEIPT signals represent state that was true at some point in the past and may no longer
  /// be current, while RELEASE signals represent the current state.
  ///
  /// ## Use in Distributed Coordination
  ///
  /// The dual-dimension model enables:
  /// - **Circuit breakers**: Observing REJECTED/FAILED receipts to open circuit
  /// - **Load shedding**: Observing DELAYED receipts to apply backpressure
  /// - **Status propagation**: Forwarding observed state through service chains
  /// - **Distributed tracing**: Correlating releases with receipts across services
  /// - **Social systems**: Services coordinating based on observed peer behavior

  public enum Dimension implements Serventis.Dimension {

    /// The emission of a sign from a self-perspective (1st person, present tense).
    ///
    /// RELEASE represents **actions this service is taking right now**. Use RELEASE when
    /// the local service is performing an operation and reporting its own state.
    ///
    /// **Mental model**: "I am doing this now"
    /// **Examples**: CALL, START, SUCCESS, FAIL, RETRY
    /// **Usage**: Self-reporting, local state tracking, span creation

    RELEASE,

    /// The reception of a sign observed from an other-perspective (3rd person, past tense).
    ///
    /// RECEIPT represents **observations of what other services did in the past**. Use RECEIPT
    /// when observing signals from remote services, typically extracted from responses, headers,
    /// or event notifications.
    ///
    /// **Mental model**: "It did that earlier"
    /// **Examples**: CALLED, STARTED, SUCCEEDED, FAILED, RETRIED
    /// **Usage**: Remote state observation, coordination, status propagation

    RECEIPT

  }

  /// The `Service` class represents a composition of one or more functions or operations.
  ///
  /// A service is a subject precept (instrument) that emits signals.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods for all service lifecycle events:
  /// ```java
  /// service.start();     // Lifecycle signals
  /// service.success();
  /// service.fail();
  /// ```
  ///
  /// Services provide semantic methods that reflect the service lifecycle,
  /// making code more expressive and self-documenting.

  @Provided
  public static final class Service
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Service (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// A signal released indicating the request (call) for work to be done (executed)

    public void call () {

      pipe.emit (
        SIGNALS.get (
          Sign.CALL,
          RELEASE
        )
      );

    }

    /// A signal received indicating the request (call) for work to be done (executed)

    public void called () {

      pipe.emit (
        SIGNALS.get (
          Sign.CALL,
          RECEIPT
        )
      );

    }

    /// A signal released indicating the delay of work

    public void delay () {

      pipe.emit (
        SIGNALS.get (
          Sign.DELAY,
          RELEASE
        )
      );

    }

    /// A signal received indicating the delay of work

    public void delayed () {

      pipe.emit (
        SIGNALS.get (
          Sign.DELAY,
          RECEIPT
        )
      );

    }

    /// A signal released indicating the dropping of work

    public void discard () {

      pipe.emit (
        SIGNALS.get (
          Sign.DISCARD,
          RELEASE
        )
      );

    }

    /// A signal received indicating the dropping of work

    public void discarded () {

      pipe.emit (
        SIGNALS.get (
          Sign.DISCARD,
          RECEIPT
        )
      );

    }

    /// A signal released indicating the disconnection of work

    public void disconnect () {

      pipe.emit (
        SIGNALS.get (
          Sign.DISCONNECT,
          RELEASE
        )
      );

    }

    /// A signal received indicating the disconnection of work

    public void disconnected () {

      pipe.emit (
        SIGNALS.get (
          Sign.DISCONNECT,
          RECEIPT
        )
      );

    }

    /// A method that emits the appropriate signals for this service in the calling of a function.
    ///
    /// @param fn  the function to be called
    /// @param <R> the return type of the function
    /// @param <T> the throwable class type
    /// @return The return value of the function
    /// @throws T                    the checked exception type of the function
    /// @throws NullPointerException if the function param is `null`

    public < R, T extends Throwable > R dispatch (
      @NotNull final Fn < R, T > fn
    ) throws T {

      requireNonNull ( fn );

      call ();

      try {

        final var result =
          fn.eval ();

        success ();

        return
          result;

      } catch (
        final Throwable t
      ) {

        fail ();

        throw t;

      }

    }

    /// A method that emits the appropriate signals for this service in the calling of an operation.
    ///
    /// @param op  the operation to be called
    /// @param <T> the throwable class type
    /// @throws T                    the checked exception type of the operation
    /// @throws NullPointerException if the operation param is `null`

    public < T extends Throwable > void dispatch (
      @NotNull final Op < T > op
    ) throws T {

      requireNonNull ( op );

      call ();

      try {

        op.exec ();

        success ();

      } catch (
        final Throwable t
      ) {

        fail ();

        throw t;

      }


    }

    /// A method that emits the appropriate signals for this service in the execution of a function.
    ///
    /// @param fn  the function to be executed
    /// @param <R> the return type of the function
    /// @param <T> the throwable class type
    /// @return The return value of the function
    /// @throws T                    the checked exception type of the function
    /// @throws NullPointerException if the function param is `null`

    public < R, T extends Throwable > R execute (
      final Fn < R, T > fn
    ) throws T {

      requireNonNull ( fn );

      start ();

      try {

        final var result =
          fn.eval ();

        success ();

        return
          result;

      } catch (
        final Throwable t
      ) {

        fail ();

        throw t;

      } finally {

        stop ();

      }

    }

    /// A method that emits the appropriate signals for this service in the execution of an operation.
    ///
    /// @param op  the operation to be executed
    /// @param <T> the throwable class type
    /// @throws T                    the checked exception type of the operation
    /// @throws NullPointerException if the operation param is `null`

    public < T extends Throwable > void execute (
      final Op < T > op
    ) throws T {

      requireNonNull ( op );

      start ();

      try {

        op.exec ();

        success ();

      } catch (
        final Throwable t
      ) {

        fail ();

        throw t;

      } finally {

        stop ();

      }

    }

    /// A signal released indicating the expiration of work

    public void expire () {

      pipe.emit (
        SIGNALS.get (
          Sign.EXPIRE,
          RELEASE
        )
      );

    }

    /// A signal received indicating the expiration of work

    public void expired () {

      pipe.emit (
        SIGNALS.get (
          Sign.EXPIRE,
          RECEIPT
        )
      );

    }

    /// A signal released indicating failure to complete a unit of work

    public void fail () {

      pipe.emit (
        SIGNALS.get (
          Sign.FAIL,
          RELEASE
        )
      );

    }

    /// A signal received indicating failure to complete a unit of work

    public void failed () {

      pipe.emit (
        SIGNALS.get (
          Sign.FAIL,
          RECEIPT
        )
      );

    }

    /// A signal released indicating activation of some recourse strategy for work

    public void recourse () {

      pipe.emit (
        SIGNALS.get (
          Sign.RECOURSE,
          RELEASE
        )
      );

    }

    /// A signal received indicating activation of some recourse strategy for work

    public void recoursed () {

      pipe.emit (
        SIGNALS.get (
          Sign.RECOURSE,
          RECEIPT
        )
      );

    }

    /// A signal released indicating the redirection of work to another service

    public void redirect () {

      pipe.emit (
        SIGNALS.get (
          Sign.REDIRECT,
          RELEASE
        )
      );

    }

    /// A signal received indicating the redirection of work to another service

    public void redirected () {

      pipe.emit (
        SIGNALS.get (
          Sign.REDIRECT,
          RECEIPT
        )
      );

    }

    /// A signal released indicating the rejection of work

    public void reject () {

      pipe.emit (
        SIGNALS.get (
          Sign.REJECT,
          RELEASE
        )
      );

    }

    /// A signal received indicating the rejection of work

    public void rejected () {

      pipe.emit (
        SIGNALS.get (
          Sign.REJECT,
          RECEIPT
        )
      );

    }

    /// A signal released indicating the resumption of work

    public void resume () {

      pipe.emit (
        SIGNALS.get (
          Sign.RESUME,
          RELEASE
        )
      );

    }

    /// A signal received indicating the resumption of work

    public void resumed () {

      pipe.emit (
        SIGNALS.get (
          Sign.RESUME,
          RECEIPT
        )
      );

    }

    /// A signal received indicating the retry of work

    public void retried () {

      pipe.emit (
        SIGNALS.get (
          Sign.RETRY,
          RECEIPT
        )
      );

    }

    /// A signal released indicating the retry of work

    public void retry () {

      pipe.emit (
        SIGNALS.get (
          Sign.RETRY,
          RELEASE
        )
      );

    }

    /// A signal released indicating the scheduling of work

    public void schedule () {

      pipe.emit (
        SIGNALS.get (
          Sign.SCHEDULE,
          RELEASE
        )
      );

    }

    /// A signal received indicating the scheduling of work

    public void scheduled () {

      pipe.emit (
        SIGNALS.get (
          Sign.SCHEDULE,
          RECEIPT
        )
      );

    }

    /// Signals a service event by composing sign and dimension.
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

    /// A signal released indicating the start of work to be done

    public void start () {

      pipe.emit (
        SIGNALS.get (
          Sign.START,
          RELEASE
        )
      );

    }


    /// A signal received indicating the start of work to be done

    public void started () {

      pipe.emit (
        SIGNALS.get (
          Sign.START,
          RECEIPT
        )
      );

    }


    /// A signal released indicating the completion of work

    public void stop () {

      pipe.emit (
        SIGNALS.get (
          Sign.STOP,
          RELEASE
        )
      );

    }


    /// A signal received indicating the completion of work

    public void stopped () {

      pipe.emit (
        SIGNALS.get (
          Sign.STOP,
          RECEIPT
        )
      );

    }

    /// A signal received indicating successful completion of work

    public void succeeded () {

      pipe.emit (
        SIGNALS.get (
          Sign.SUCCESS,
          RECEIPT
        )
      );

    }

    /// A signal released indicating successful completion of work

    public void success () {

      pipe.emit (
        SIGNALS.get (
          Sign.SUCCESS,
          RELEASE
        )
      );

    }

    /// A signal released indicating the suspension of work

    public void suspend () {

      pipe.emit (
        SIGNALS.get (
          Sign.SUSPEND,
          RELEASE
        )
      );

    }

    /// A signal received indicating the suspension of work

    public void suspended () {

      pipe.emit (
        SIGNALS.get (
          Sign.SUSPEND,
          RECEIPT
        )
      );

    }

  }

  /// The [Signal] record represents a service signal composed of a sign and dimension.
  ///
  /// Signals are the composition of Sign (what happened) and Dimension (from whose perspective),
  /// enabling observation of service interactions from both self and observed perspectives.
  ///
  /// Note: We use the term `work` here to mean either (remote) call or (local) execution.
  ///
  /// @param sign      the service interaction classification
  /// @param dimension the perspective from which the signal is emitted

  @Provided
  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

}
