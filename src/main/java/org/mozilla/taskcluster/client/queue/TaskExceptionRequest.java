package org.mozilla.taskcluster.client.queue;

/**
* Request for a run of a task to be resolved with an exception
*
* See http://schemas.taskcluster.net/queue/v1/task-exception-request.json#
*/
public class TaskExceptionRequest {

    /**
     * Reason that the task is resolved with an exception. This is a subset
     * of the values for `resolvedReason` given in the task status structure.
     * **Report `worker-shutdown`** if the run failed because the worker
     * had to shutdown (spot node disappearing). In case of `worker-shutdown`
     * the queue will immediately **retry** the task, by making a new run.
     * This is much faster than ignoreing the issue and letting the task _retry_
     * by claim expiration. For any other _reason_ reported the queue will not
     * retry the task.
     * **Report `malformed-payload`** if the `task.payload` doesn't match the
     * schema for the worker payload, or referenced resource doesn't exists.
     * In either case, you should still log the error to a log file for the
     * specific run.
     * **Report `resource-unavailable`** if a resource/service needed or
     * referenced in `task.payload` is _temporarily_ unavailable. Do not use this
     * unless you know the resource exists, if the resource doesn't exist you
     * should report `malformed-payload`. Example use-case if you contact the
     * index (a service) on behalf of the task, because of a declaration in
     * `task.payload`, and the service (index) is temporarily down. Don't use
     * this if a URL returns 404, but if it returns 503 or hits a timeout when
     * you retry the request, then this _may_ be a valid exception. The queue
     * assumes that workers have applied retries as needed, and will not retry
     *  the task.
     * **Report `internal-error` if the worker experienced an unhandled internal
     * error from which it couldn't recover. The queue will not retry runs
     * resolved with this reason, but you are clearly signaling that this is a
     * bug in the worker code.
     */
    public String reason;
}