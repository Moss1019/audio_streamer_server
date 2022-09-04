package com.mossonthetree.audioserver.tcpsocket;

@FunctionalInterface
public interface OnReceiveSig<T1, T2, R> {
    public R apply(T1 t1, T2 t2);
}
