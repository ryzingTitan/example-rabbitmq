package com.example.rabbitmq.cucumber

import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasspathResource("features")
class CucumberTestRunner
