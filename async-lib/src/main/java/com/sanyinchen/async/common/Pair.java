package com.sanyinchen.async.common;

/**
 * Created by sanyinchen on 19-3-23.
 * <p>
 * Pair instance , support T first & second
 *
 * @author sanyinchen
 * @version v0.1
 * @since 19-3-23
 */

public class Pair<F, S> {
    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

}
