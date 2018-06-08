#!/usr/bin/env bash

authbind --deep bin/activator run -Dhttp.port=80 -Dkurator.jar=$KURATOR_JAR

