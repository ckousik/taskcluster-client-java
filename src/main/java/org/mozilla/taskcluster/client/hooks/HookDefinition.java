package org.mozilla.taskcluster.client.hooks;

/**
* Definition of a hook that will create tasks when defined events occur.
*
* See http://schemas.taskcluster.net/hooks/v1/hook-definition.json#
*/
public class HookDefinition {

    /**
     * Deadline of the task, `pending` and `running` runs are resolved as **failed** if not resolved by other means before the deadline. Note, deadline cannot be more than5 days into the future.
     * 
     * Must be specified as `A years B months C days D hours E minutes F seconds`, though you may leave out zeros. For more details see: `taskcluster.fromNow` in [taskcluster-client](https://github.com/taskcluster/taskcluster-client)
     */
    public String deadline;

    /**
     * Task expiration, time at which task definition and status is deleted. Notice that all artifacts for the must have an expiration that is no later than this.
     * 
     * Must be specified as `A years B months C days D hours E minutes F seconds`, though you may leave out zeros. For more details see: `taskcluster.fromNow` in [taskcluster-client](https://github.com/taskcluster/taskcluster-client)
     */
    public String expires;
    public String hookGroupId;
    public String hookId;
    public class Metadata {

        /**
         * Long-form of the hook's purpose and behavior
         */
        public String description;

        /**
         * Whether to email the owner on an error creating the task.
         */
        public boolean emailOnError;

        /**
         * Human readable name of the hook
         */
        public String name;

        /**
         * Email of the person or group responsible for this hook.
         */
        public String owner;
    }

    public Metadata metadata;
    public String[] schedule;
    public TaskDefinitionRequest task;
}