package com.example.systemcommon.fw.job;

public interface Job {

    String name();

    JobResult execute();
}
