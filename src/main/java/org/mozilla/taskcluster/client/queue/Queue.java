// The following code is AUTO-GENERATED. Please DO NOT edit.
//
// This package was generated from the schema defined at
// http://references.taskcluster.net/queue/v1/api.json
package org.mozilla.taskcluster.client.queue;

import org.mozilla.taskcluster.client.APICallFailure;
import org.mozilla.taskcluster.client.CallSummary;
import org.mozilla.taskcluster.client.EmptyPayload;
import org.mozilla.taskcluster.client.TaskClusterRequestHandler;

/**
 * The queue, typically available at `queue.taskcluster.net`, is responsible
 * for accepting tasks and track their state as they are executed by
 * workers. In order ensure they are eventually resolved.
 * 
 * This document describes the API end-points offered by the queue. These 
 * end-points targets the following audience:
 *  * Schedulers, who create tasks to be executed,
 *  * Workers, who execute tasks, and
 *  * Tools, that wants to inspect the state of a task.
 *
 * See: http://docs.taskcluster.net/queue/api-docs
 */
public class Queue extends TaskClusterRequestHandler {

    protected static final String defaultBaseURL = "https://queue.taskcluster.net/v1";

    public Queue(String clientId, String accessToken) {
        super(clientId, accessToken, defaultBaseURL);
    }

    public Queue(String clientId, String accessToken, String certificate) {
        super(clientId, accessToken, certificate, defaultBaseURL);
    }

    public Queue(String baseURL) {
        super(baseURL);
    }

    public Queue() {
        super(defaultBaseURL);
    }

    /**
     * This end-point will return the task-definition. Notice that the task
     * definition may have been modified by queue, if an optional property isn't
     * specified the queue may provide a default value.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#task
     */
    public CallSummary<EmptyPayload, TaskDefinitionResponse> task(String taskId) throws APICallFailure {
        return apiCall(null, "GET", "/task/" + uriEncode(taskId), TaskDefinitionResponse.class);
    }

    /**
     * Get task status structure from `taskId`
     *
     * See http://docs.taskcluster.net/queue/api-docs/#status
     */
    public CallSummary<EmptyPayload, TaskStatusResponse> status(String taskId) throws APICallFailure {
        return apiCall(null, "GET", "/task/" + uriEncode(taskId) + "/status", TaskStatusResponse.class);
    }

    /**
     * List taskIds of all tasks sharing the same `taskGroupId`.
     * 
     * As a task-group may contain an unbounded number of tasks, this end-point
     * may return a `continuationToken`. To continue listing tasks you must
     * `listTaskGroup` again with the `continuationToken` as the query-string
     * option `continuationToken`.
     * 
     * By default this end-point will try to return up to 1000 members in one
     * request. But it **may return less**, even if more tasks are available.
     * It may also return a `continuationToken` even though there are no more
     * results. However, you can only be sure to have seen all results if you
     * keep calling `listTaskGroup` with the last `continationToken` until you
     * get a result without a `continuationToken`.
     * 
     * If you're not interested in listing all the members at once, you may
     * use the query-string option `limit` to return fewer.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#listTaskGroup
     */
    public CallSummary<EmptyPayload, ListTaskGroupResponse> listTaskGroup(String taskGroupId) throws APICallFailure {
        return apiCall(null, "GET", "/task-group/" + uriEncode(taskGroupId) + "/list", ListTaskGroupResponse.class);
    }

    /**
     * Create a new task, this is an **idempotent** operation, so repeat it if
     * you get an internal server error or network connection is dropped.
     * 
     * **Task `deadline´**, the deadline property can be no more than 5 days
     * into the future. This is to limit the amount of pending tasks not being
     * taken care of. Ideally, you should use a much shorter deadline.
     * 
     * **Task expiration**, the `expires` property must be greater than the
     * task `deadline`. If not provided it will default to `deadline` + one
     * year. Notice, that artifacts created by task must expire before the task.
     * 
     * **Task specific routing-keys**, using the `task.routes` property you may
     * define task specific routing-keys. If a task has a task specific 
     * routing-key: `<route>`, then when the AMQP message about the task is
     * published, the message will be CC'ed with the routing-key: 
     * `route.<route>`. This is useful if you want another component to listen
     * for completed tasks you have posted.  The caller must have scope
     * `queue:route:<route>` for each route.
     * 
     * **Important** Any scopes the task requires are also required for creating
     * the task. Please see the Request Payload (Task Definition) for details.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#createTask
     */
    public CallSummary<TaskDefinitionRequest, TaskStatusResponse> createTask(String taskId, TaskDefinitionRequest payload) throws APICallFailure {
        return apiCall(payload, "PUT", "/task/" + uriEncode(taskId), TaskStatusResponse.class);
    }

    /**
     * Define a task without scheduling it. This API end-point allows you to
     * upload a task definition without having scheduled. The task won't be
     * reported as pending until it is scheduled, see the scheduleTask API 
     * end-point.
     * 
     * The purpose of this API end-point is allow schedulers to upload task
     * definitions without the tasks becoming _pending_ immediately. This useful
     * if you have a set of dependent tasks. Then you can upload all the tasks
     * and when the dependencies of a tasks have been resolved, you can schedule
     * the task by calling `/task/:taskId/schedule`. This eliminates the need to
     * store tasks somewhere else while waiting for dependencies to resolve.
     * 
     * **Important** Any scopes the task requires are also required for defining
     * the task. Please see the Request Payload (Task Definition) for details.
     * 
     * **Note** this operation is **idempotent**, as long as you upload the same
     * task definition as previously defined this operation is safe to retry.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#defineTask
     */
    public CallSummary<TaskDefinitionRequest, TaskStatusResponse> defineTask(String taskId, TaskDefinitionRequest payload) throws APICallFailure {
        return apiCall(payload, "POST", "/task/" + uriEncode(taskId) + "/define", TaskStatusResponse.class);
    }

    /**
     * If you have define a task using `defineTask` API end-point, then you
     * can schedule the task to be scheduled using this method.
     * This will announce the task as pending and workers will be allowed, to
     * claim it and resolved the task.
     * 
     * **Note** this operation is **idempotent** and will not fail or complain
     * if called with `taskId` that is already scheduled, or even resolved.
     * To reschedule a task previously resolved, use `rerunTask`.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#scheduleTask
     */
    public CallSummary<EmptyPayload, TaskStatusResponse> scheduleTask(String taskId) throws APICallFailure {
        return apiCall(null, "POST", "/task/" + uriEncode(taskId) + "/schedule", TaskStatusResponse.class);
    }

    /**
     * This method _reruns_ a previously resolved task, even if it was
     * _completed_. This is useful if your task completes unsuccessfully, and
     * you just want to run it from scratch again. This will also reset the
     * number of `retries` allowed.
     * 
     * Remember that `retries` in the task status counts the number of runs that
     * the queue have started because the worker stopped responding, for example
     * because a spot node died.
     * 
     * **Remark** this operation is idempotent, if you try to rerun a task that
     * isn't either `failed` or `completed`, this operation will just return the
     * current task status.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#rerunTask
     */
    public CallSummary<EmptyPayload, TaskStatusResponse> rerunTask(String taskId) throws APICallFailure {
        return apiCall(null, "POST", "/task/" + uriEncode(taskId) + "/rerun", TaskStatusResponse.class);
    }

    /**
     * This method will cancel a task that is either `unscheduled`, `pending` or
     * `running`. It will resolve the current run as `exception` with
     * `reasonResolved` set to `canceled`. If the task isn't scheduled yet, ie.
     * it doesn't have any runs, an initial run will be added and resolved as
     * described above. Hence, after canceling a task, it cannot be scheduled
     * with `queue.scheduleTask`, but a new run can be created with
     * `queue.rerun`. These semantics is equivalent to calling
     * `queue.scheduleTask` immediately followed by `queue.cancelTask`.
     * 
     * **Remark** this operation is idempotent, if you try to cancel a task that
     * isn't `unscheduled`, `pending` or `running`, this operation will just
     * return the current task status.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#cancelTask
     */
    public CallSummary<EmptyPayload, TaskStatusResponse> cancelTask(String taskId) throws APICallFailure {
        return apiCall(null, "POST", "/task/" + uriEncode(taskId) + "/cancel", TaskStatusResponse.class);
    }

    /**
     * Get a signed URLs to get and delete messages from azure queue.
     * Once messages are polled from here, you can claim the referenced task
     * with `claimTask`, and afterwards you should always delete the message.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#pollTaskUrls
     */
    public CallSummary<EmptyPayload, PollTaskUrlsResponse> pollTaskUrls(String provisionerId, String workerType) throws APICallFailure {
        return apiCall(null, "GET", "/poll-task-url/" + uriEncode(provisionerId) + "/" + uriEncode(workerType), PollTaskUrlsResponse.class);
    }

    /**
     * claim a task, more to be added later...
     *
     * See http://docs.taskcluster.net/queue/api-docs/#claimTask
     */
    public CallSummary<TaskClaimRequest, TaskClaimResponse> claimTask(String taskId, String runId, TaskClaimRequest payload) throws APICallFailure {
        return apiCall(payload, "POST", "/task/" + uriEncode(taskId) + "/runs/" + uriEncode(runId) + "/claim", TaskClaimResponse.class);
    }

    /**
     * reclaim a task more to be added later...
     *
     * See http://docs.taskcluster.net/queue/api-docs/#reclaimTask
     */
    public CallSummary<EmptyPayload, TaskReclaimResponse> reclaimTask(String taskId, String runId) throws APICallFailure {
        return apiCall(null, "POST", "/task/" + uriEncode(taskId) + "/runs/" + uriEncode(runId) + "/reclaim", TaskReclaimResponse.class);
    }

    /**
     * Report a task completed, resolving the run as `completed`.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#reportCompleted
     */
    public CallSummary<EmptyPayload, TaskStatusResponse> reportCompleted(String taskId, String runId) throws APICallFailure {
        return apiCall(null, "POST", "/task/" + uriEncode(taskId) + "/runs/" + uriEncode(runId) + "/completed", TaskStatusResponse.class);
    }

    /**
     * Report a run failed, resolving the run as `failed`. Use this to resolve
     * a run that failed because the task specific code behaved unexpectedly.
     * For example the task exited non-zero, or didn't produce expected output.
     * 
     * Don't use this if the task couldn't be run because if malformed payload,
     * or other unexpected condition. In these cases we have a task exception,
     * which should be reported with `reportException`.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#reportFailed
     */
    public CallSummary<EmptyPayload, TaskStatusResponse> reportFailed(String taskId, String runId) throws APICallFailure {
        return apiCall(null, "POST", "/task/" + uriEncode(taskId) + "/runs/" + uriEncode(runId) + "/failed", TaskStatusResponse.class);
    }

    /**
     * Resolve a run as _exception_. Generally, you will want to report tasks as
     * failed instead of exception. You should `reportException` if,
     * 
     *   * The `task.payload` is invalid,
     *   * Non-existent resources are referenced,
     *   * Declared actions cannot be executed due to unavailable resources,
     *   * The worker had to shutdown prematurely, or,
     *   * The worker experienced an unknown error.
     * 
     * Do not use this to signal that some user-specified code crashed for any
     * reason specific to this code. If user-specific code hits a resource that
     * is temporarily unavailable worker should report task _failed_.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#reportException
     */
    public CallSummary<TaskExceptionRequest, TaskStatusResponse> reportException(String taskId, String runId, TaskExceptionRequest payload) throws APICallFailure {
        return apiCall(payload, "POST", "/task/" + uriEncode(taskId) + "/runs/" + uriEncode(runId) + "/exception", TaskStatusResponse.class);
    }

    /**
     * This API end-point creates an artifact for a specific run of a task. This
     * should **only** be used by a worker currently operating on this task, or
     * from a process running within the task (ie. on the worker).
     * 
     * All artifacts must specify when they `expires`, the queue will
     * automatically take care of deleting artifacts past their
     * expiration point. This features makes it feasible to upload large
     * intermediate artifacts from data processing applications, as the
     * artifacts can be set to expire a few days later.
     * 
     * We currently support 4 different `storageType`s, each storage type have
     * slightly different features and in some cases difference semantics.
     * 
     * **S3 artifacts**, is useful for static files which will be stored on S3.
     * When creating an S3 artifact the queue will return a pre-signed URL
     * to which you can do a `PUT` request to upload your artifact. Note
     * that `PUT` request **must** specify the `content-length` header and
     * **must** give the `content-type` header the same value as in the request
     * to `createArtifact`.
     * 
     * **Azure artifacts**, are stored in _Azure Blob Storage_ service, which
     * given the consistency guarantees and API interface offered by Azure is
     * more suitable for artifacts that will be modified during the execution
     * of the task. For example docker-worker has a feature that persists the
     * task log to Azure Blob Storage every few seconds creating a somewhat
     * live log. A request to create an Azure artifact will return a URL
     * featuring a [Shared-Access-Signature](http://msdn.microsoft.com/en-us/library/azure/dn140256.aspx),
     * refer to MSDN for further information on how to use these.
     * **Warning: azure artifact is currently an experimental feature subject
     * to changes and data-drops.**
     * 
     * **Reference artifacts**, only consists of meta-data which the queue will
     * store for you. These artifacts really only have a `url` property and
     * when the artifact is requested the client will be redirect the URL
     * provided with a `303` (See Other) redirect. Please note that we cannot
     * delete artifacts you upload to other service, we can only delete the
     * reference to the artifact, when it expires.
     * 
     * **Error artifacts**, only consists of meta-data which the queue will
     * store for you. These artifacts are only meant to indicate that you the
     * worker or the task failed to generate a specific artifact, that you
     * would otherwise have uploaded. For example docker-worker will upload an
     * error artifact, if the file it was supposed to upload doesn't exists or
     * turns out to be a directory. Clients requesting an error artifact will
     * get a `403` (Forbidden) response. This is mainly designed to ensure that
     * dependent tasks can distinguish between artifacts that were suppose to
     * be generated and artifacts for which the name is misspelled.
     * 
     * **Artifact immutability**, generally speaking you cannot overwrite an
     * artifact when created. But if you repeat the request with the same
     * properties the request will succeed as the operation is idempotent.
     * This is useful if you need to refresh a signed URL while uploading.
     * Do not abuse this to overwrite artifacts created by another entity!
     * Such as worker-host overwriting artifact created by worker-code.
     * 
     * As a special case the `url` property on _reference artifacts_ can be
     * updated. You should only use this to update the `url` property for
     * reference artifacts your process has created.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#createArtifact
     */
    public CallSummary<Object, Object> createArtifact(String taskId, String runId, String name, Object payload) throws APICallFailure {
        return apiCall(payload, "POST", "/task/" + uriEncode(taskId) + "/runs/" + uriEncode(runId) + "/artifacts/" + uriEncode(name), Object.class);
    }

    /**
     * Get artifact by `<name>` from a specific run.
     * 
     * **Public Artifacts**, in-order to get an artifact you need the scope
     * `queue:get-artifact:<name>`, where `<name>` is the name of the artifact.
     * But if the artifact `name` starts with `public/`, authentication and
     * authorization is not necessary to fetch the artifact.
     * 
     * **API Clients**, this method will redirect you to the artifact, if it is
     * stored externally. Either way, the response may not be JSON. So API
     * client users might want to generate a signed URL for this end-point and
     * use that URL with a normal HTTP client.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#getArtifact
     */
    public CallSummary<EmptyPayload, EmptyPayload> getArtifact(String taskId, String runId, String name) throws APICallFailure {
        return apiCall(null, "GET", "/task/" + uriEncode(taskId) + "/runs/" + uriEncode(runId) + "/artifacts/" + uriEncode(name), EmptyPayload.class);
    }

    /**
     * Get artifact by `<name>` from the last run of a task.
     * 
     * **Public Artifacts**, in-order to get an artifact you need the scope
     * `queue:get-artifact:<name>`, where `<name>` is the name of the artifact.
     * But if the artifact `name` starts with `public/`, authentication and
     * authorization is not necessary to fetch the artifact.
     * 
     * **API Clients**, this method will redirect you to the artifact, if it is
     * stored externally. Either way, the response may not be JSON. So API
     * client users might want to generate a signed URL for this end-point and
     * use that URL with a normal HTTP client.
     * 
     * **Remark**, this end-point is slightly slower than
     * `queue.getArtifact`, so consider that if you already know the `runId` of
     * the latest run. Otherwise, just us the most convenient API end-point.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#getLatestArtifact
     */
    public CallSummary<EmptyPayload, EmptyPayload> getLatestArtifact(String taskId, String name) throws APICallFailure {
        return apiCall(null, "GET", "/task/" + uriEncode(taskId) + "/artifacts/" + uriEncode(name), EmptyPayload.class);
    }

    /**
     * Returns a list of artifacts and associated meta-data for a given run.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#listArtifacts
     */
    public CallSummary<EmptyPayload, ListArtifactsResponse> listArtifacts(String taskId, String runId) throws APICallFailure {
        return apiCall(null, "GET", "/task/" + uriEncode(taskId) + "/runs/" + uriEncode(runId) + "/artifacts", ListArtifactsResponse.class);
    }

    /**
     * Returns a list of artifacts and associated meta-data for the latest run
     * from the given task.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#listLatestArtifacts
     */
    public CallSummary<EmptyPayload, ListArtifactsResponse> listLatestArtifacts(String taskId) throws APICallFailure {
        return apiCall(null, "GET", "/task/" + uriEncode(taskId) + "/artifacts", ListArtifactsResponse.class);
    }

    /**
     * Get an approximate number of pending tasks for the given `provisionerId`
     * and `workerType`.
     * 
     * The underlying Azure Storage Queues only promises to give us an estimate.
     * Furthermore, we cache the result in memory for 20 seconds. So consumers
     * should be no means expect this to be an accurate number.
     * It is, however, a solid estimate of the number of pending tasks.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#pendingTasks
     */
    public CallSummary<EmptyPayload, CountPendingTasksResponse> pendingTasks(String provisionerId, String workerType) throws APICallFailure {
        return apiCall(null, "GET", "/pending/" + uriEncode(provisionerId) + "/" + uriEncode(workerType), CountPendingTasksResponse.class);
    }

    /**
     * Documented later...
     * 
     * **Warning** this api end-point is **not stable**.
     *
     * See http://docs.taskcluster.net/queue/api-docs/#ping
     */
    public CallSummary<EmptyPayload, EmptyPayload> ping() throws APICallFailure {
        return apiCall(null, "GET", "/ping", EmptyPayload.class);
    }
}