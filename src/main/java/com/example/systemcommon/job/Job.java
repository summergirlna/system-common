package com.example.systemcommon.job;

public interface Job {

    String name();

    JobResult execute();
}
