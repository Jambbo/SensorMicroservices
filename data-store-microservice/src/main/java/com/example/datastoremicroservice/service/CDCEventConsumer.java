package com.example.datastoremicroservice.service;
//CDC - Changes Data Capture. We are reading from kafka from queue that changes(events)
// that were happened with db. We are listening the db with sensor's data. By reading
// these changes we are able to process them here. This approach called CDC.
public interface CDCEventConsumer {

    void handle(String message);//message=json

}
