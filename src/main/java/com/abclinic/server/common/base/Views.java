package com.abclinic.server.common.base;

public class Views {
    public static class Abridged {}
    public static class Public extends Abridged {}
    public static class Private extends Public {}
    public static class Confidential extends Private {}
}
