package com.sanyinchen.async.basic;

/**
 * Created by sanyinchen on 19-8-31.
 *
 * @author sanyinchen
 * @version v0.1
 * @since 19-8-31
 */

public interface IThreadContainer {

    int getMaxJobLimit();

    int getMaxThreadLimit();

    int getBlockingJobs();

    int getLivingThread();

}
